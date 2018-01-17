package in.hoptec.smartconnect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import in.hoptec.smartconnect.utils.ApManager;

public class Test extends AppCompatActivity {


    /******
     * WIFI AP CONSTANTS
     ******/
    private String MONG_HOST_IP = "http://192.168.4.1";
    private String AP_NAME = "MONG_TEST";
    private String AP_PASS = "password";
    private String API_KEY = "AEZAKMI";

    private boolean scanDone = false;
    private boolean scanInitiatedByApp = false;

    private boolean isAppInDisconnectionMode = false;


    private Context ctx;

    private boolean connected = false;


    BroadcastReceiver suppliantChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            SupplicantState newState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);

            utl.e("WIFI_", "Splliant State : " + newState);
            switch (newState) {
                case COMPLETED:

                    if (!getCurrentNetwork().contains(AP_NAME) && getCurrentNetwork().length() > 1) {
                        Log.d("WIFI_", "CONNECTED to another " + getCurrentNetwork() + " . diconnecting");

                        mWifiManager.disconnect();
                    } else {

                        connected = true;
                        //bindNetwork();
                        Log.d("WIFI_", "CONNECTED and Binding");
                        tx.setText("Connected !");

                        //getWaterFlowData();

                        bindNetwork();

                    }

                    break;
                case DISCONNECTED:

                    if (!connected) {

                        tx.setText("Connecting... !");
                        Log.d("WIFI_", "DISCONNECTED");
                        scanDone = false;
                        scanInitiatedByApp = true;
                        mWifiManager.startScan();

                    }


            }


        }
    };


    BroadcastReceiver mWifiStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    mWifiManager.setWifiEnabled(true);
                    utl.l("WIFI_", "WIFI STATE DISABLED..Enabling");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    utl.l("WIFI_", "WIFI STATE DISABLING");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    if (!scanDone) {

                        scanInitiatedByApp = true;
                        mWifiManager.startScan();

                    }
                    utl.l("WIFI_", "WIFI STATE ENABLED");

                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    utl.l("WIFI_", "WIFI STATE ENABLING");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    utl.l("WIFI_", "WIFI STATE UNKNOWN");
                    break;
            }


        }
    };


    BroadcastReceiver mConnectedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();

            //bindNetwork();
            if (netInfo == null) {
                utl.e("WIFI_", "NetInfo Is Null");
            } else {
                utl.e("WIFI_", "NetType Is : " + netInfo.getTypeName() + " \n" + netInfo.getType() + "\nState : " + netInfo.getState());
            }
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                utl.l("WIFI_", "Have Wifi Connection ,  BInding Now ");


                if (!getCurrentNetwork().contains(AP_NAME) && getCurrentNetwork().length() > 1) {
                    Log.d("WIFI_", "CONNECTED to another " + getCurrentNetwork() + " . diconnecting");

                    mWifiManager.disconnect();
                } else {
                    tx.setText("Connected !");

                }


            } else {
                utl.l("WIFI_", "Dont Have Wifi Connection  ");

                tx.setText("Disconnedted !");
                Log.d("WIFI_", "DISCONNECTED");
                scanDone = false;
                scanInitiatedByApp = true;
                mWifiManager.startScan();


            }

        }
    };


    BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {


            if (!scanInitiatedByApp) {
                utl.e("Scraping Stray Scan Result");
                return;
            }

            scanInitiatedByApp = false;
            scanDone = true;


            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                addLog("WIFI_", "WIFI STATE SCANNING  IS COMPLETED");

                String ssids = "";
                for (ScanResult scanResults : mScanResults) {
                    ssids += "\n" + scanResults.SSID;
                }
                addLog("WIFI_", "Listing deviceds done .");
                addLog("WIFI_", ssids);

                if (ssids.contains(AP_NAME))
                    connect(AP_NAME, AP_PASS);
                else
                    addLog("Device Not in Range ! \nPlease Make Sure Device is turned on and " +
                            "is in Range and Pull Down to refresh .");


            }
        }


    };


    private WifiManager mWifiManager;
    private Activity act;

    private TextView tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        act = this;

        setContentView(R.layout.activity_test);

        tx = (TextView) findViewById(R.id.st);
        mWifiManager = (WifiManager) act.getSystemService(WIFI_SERVICE);

        startConnection();


    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }


    @Override
    protected void onDestroy() {

        try {

            unRegister();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void connect(String ssid, String key) {

        utl.e("Connecting to Wifi : " + ssid + " : " + key);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        int netId = mWifiManager.addNetwork(wifiConfig);
        //mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();

    }


    private void startConnection() {

        if (isAppInDisconnectionMode) {
            return;
        }


        if (ApManager.isApOn(ctx)) {
            utl.diag(ctx, "Hotspot On !", "Please turn OFF the WiFi hotspot and click OK to continue !", "TURNED OFF", new utl.ClickCallBack() {
                @Override
                public void done(DialogInterface dialogInterface) {
                    startConnection();
                }
            });

            return;

        }
        register();

    }


    private void register() {


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            act.registerReceiver(suppliantChangeReciever, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
        } else {

            act.registerReceiver(mConnectedReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }
        act.registerReceiver(mWifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        act.registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


    }

    private void unRegister() {


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            act.unregisterReceiver(suppliantChangeReciever);
        } else {

            act.unregisterReceiver(mConnectedReciever);

        }

        act.unregisterReceiver(mWifiScanReceiver);
        act.unregisterReceiver(mWifiStateChangedReceiver);

    }

    private void addLog(String log) {


        Log.d("Logging", "" + log);


    }

    private void addLog(String tag, String log) {

        Log.d(tag, log);

    }


    private String getCurrentNetwork() {
        String ssid = "";
        WifiInfo wifiInfo;
        wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = "" + wifiInfo.getSSID();
        }

        return ssid;

    }


    private void bindNetwork() {

        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();

            final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        utl.e(("WIFI_"), "Binding Network : Network Available : " + network.toString());
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cm.bindProcessToNetwork(network);      // M and above

                    }
                    // or:
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cm.setProcessDefaultNetwork(network);  // L and above

                    }

                    utl.e("WIFI_","Wifi IP Add is "+GetDeviceipWiFiData());
                    getWaterFlowData();
                }

            };

            cm.registerNetworkCallback(request, callback);
        }
    }



    public String GetDeviceipWiFiData()
    {

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        @SuppressWarnings("deprecation")

        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }

    private void getWaterFlowData() {



        addLog("Connected to Device");

        String url = MONG_HOST_IP + "/rpc/read";
        addLog("WIFI_", url);
        JSONObject jo = new JSONObject();
        try {

            jo.put("api_key", API_KEY);
            jo.put("read_id", 10);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(url).addJSONObjectBody(jo).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                addLog("WIFI_", "Respose : " + response.toString());


                if (response.toString().toLowerCase().contains("success")) {
                    try {
                        String re = "_____\n" +
                                "\n" + "Free RAM : " + response.getString("free_ram") +
                                "\n" + "Uptime : " + response.getString("uptime") +
                                "\n" + "Status : " + response.getString("status") +
                                "\nSerial Data : " + response.getString("serial");

                        tx.setText("Device Connected\nSensor API Response\n" + re);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    addLog("Device Connected\nSensor API Response\n" + response.toString());
                }

            }

            @Override
            public void onError(ANError ANError) {
                addLog("Err err:" + ANError.getErrorDetail());
                addLog("Err Body:" + ANError.getErrorBody());
            }
        });

    }



}
