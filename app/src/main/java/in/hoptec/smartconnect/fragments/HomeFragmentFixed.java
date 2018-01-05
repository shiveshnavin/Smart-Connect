package in.hoptec.smartconnect.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.hoptec.smartconnect.R;
import in.hoptec.smartconnect.adapters.BoxesAdapter;
import in.hoptec.smartconnect.utils.ApManager;
import in.hoptec.smartconnect.utils.Transact;
import in.hoptec.smartconnect.utils.WifiReciever;
import in.hoptec.smartconnect.utl;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static android.content.Context.WIFI_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragmentFixed extends Fragment implements Transact{

    Transact cb;


    String  MONG_HOST_IP = "http://192.168.4.1";
    String AP_NAME="MONG_TEST";
    String AP_PASS="password";
    String API_KEY="AEZAKMI";

    public HomeFragmentFixed()
    {





    }



    public void transactToState(int state)
    {




    }

    BroadcastReceiver mWifiScanReceiver =null,mWifiStateChangedReceiver=null,mConnectedReciever=null;
    public Context ctx;
    public Activity act;

    ImageView wifi2;
    SwipeRefreshLayout swipe;

   public static WifiManager mWifiManager;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_home, container, false);

        ctx=getContext();
        act=getActivity();

        swipe=(SwipeRefreshLayout)view.findViewById(R.id.swipe);
        text=(TextView) view.findViewById(R.id.text);
        wifi2=(ImageView) view.findViewById(R.id.wifi);

        mWifiManager = (WifiManager) act.getSystemService(WIFI_SERVICE);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                m_start();

            }
        });

        m_start();

        return view;
    }

    public void m_start()
    {


        initRecievers();
        wifi2.setImageResource(R.drawable.avd_conn);
        utl.animate_avd(wifi2);


        if(ApManager.isApOn(ctx))
        {
            utl.diag(ctx, "Hotspot On !", "Please turn OFF the WiFi hotspot and click OK to continue !", "TURNED OFF", new utl.ClickCallBack() {
                @Override
                public void done(DialogInterface dialogInterface) {
                    m_start();
                }
            });
            //ApManager.configApState(act);
           // utl.l("WIFI_","AP Mode Toggle to : "+ApManager.isApOn(ctx));
            return;

        }

        if(utl.isWifiConnected(act))
        {

            isWifiConnectedAlready();

        }
        else {
            registerRecievers(0);



            if(isWifiOn()){




            }
            else {

                mWifiManager.setWifiEnabled(true);


            }



        }



    }

    public boolean isWifiOn()
    {

        boolean isWifiOn=false;
        if(mWifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED)
        {
            isWifiOn=true;
        }
        else {
            isWifiOn=false;
        }
        return isWifiOn;



    }


    public String  getCurrentNetwork()
    {


        String ssid="";

        WifiInfo wifiInfo;



        wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid =""+ wifiInfo.getSSID();
        }


        return ssid;

    }

    public void isWifiConnectedAlready()
    {


        ssid=getCurrentNetwork();

        utl.l("WIFI_","Connected to WIFI - "+ssid);

        if(ssid.contains(AP_NAME))
            {
                 utl.l("WIFI_","Connected to device ALREADY.");
                getWaterFlowData();
            }
            else {
            utl.l("WIFI_","Connected another AP ALREADY DIsconnecting....And connectingg to "+AP_NAME);


                CUR_CONN_STATE=CONNECTING;
                mWifiManager.disconnect();
                registerRecievers(0);



            }




    }


    public final int I_SCAN_REC=12,I_WIFI_STATE_REC=232,I_I_WIFI_CONN_REC=3432;
    String CUR_SCAN_STATE="NOT RUNNING",RUNNING="RUNNING",COMPLETED="COMPLETED";
    String NOT_CONNECTED="NOT CONNECTED",CUR_CONN_STATE=NOT_CONNECTED,CONNECTING="CONNECTING",CONNECTED="CONNECTED";
    public void initRecievers()
    {


        mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> mScanResults = mWifiManager.getScanResults();

                    utl.l("WIFI_", "WIFI STATE SCANNING STATE IS " + CUR_SCAN_STATE);

                    String ssids = "";
                    for (ScanResult scanResults : mScanResults
                            ) {

                        // utl.l("WIFI_ RR : "+scanResults.SSID);

                        ssids += "\n" + scanResults.SSID;

                    }

                    utl.l("WIFI_", "Listing deviceds done .");
                    swipe.setRefreshing(false);
                    utl.e("WIFI_", ssids);

                    if(ssids.contains(AP_NAME))
                    {
                        connect(AP_NAME,AP_PASS);
                    }
                    else {
                        text.setText("Device Not in Range ! \nPlease Make Sure Device is turned on and is in Range and Pull Down to refresh .");
                    }


                }
            }


        };





        mConnectedReciever=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
                {


                   isWifiConnectedAlready();

                    Log.d("WIFI_", "Have Wifi Connection");
                }
                else{
                    Log.d("WIFI_", "Don't have Wifi Connection");

                    if(mWifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED)
                    {
                        mWifiManager.startScan();
                        //connect(AP_NAME,AP_PASS);
                    }


                }


            }
        };





    }

    public void registerRecievers(int n)
    {

        if (n==0) {
            //act.registerReceiver(mWifiStateChangedReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

            act.registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            act.registerReceiver(mConnectedReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } else if(n==I_WIFI_STATE_REC) {
           act.registerReceiver(mWifiStateChangedReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));


        }else if(n==I_SCAN_REC) {
            act.registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        }
        else if(n==I_I_WIFI_CONN_REC) {
            act.registerReceiver(mConnectedReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        }


    }
    boolean connectedOnce=false;

    @Override
    public void onDestroyView() {
        unrgisterRecievers();

        super.onDestroyView();



    }

    public void unrgisterRecievers()
    {
        try {
            act.unregisterReceiver(mWifiStateChangedReceiver);
        } catch (Exception e) {
         }

        try {
            act.unregisterReceiver(mWifiScanReceiver);
        } catch (Exception e) {
         }

        try {
            act.unregisterReceiver(mConnectedReciever);
        } catch (Exception e) {

        }


    }

    TextView text;;
    String ssid="";


    public void connect(String ssid,String key)
    {


        IntentFilter it=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
       // m_start();

    }

    public void getWaterFlowData()
    {
        unrgisterRecievers();


        CUR_CONN_STATE=CONNECTED;


        text.setText("Connected to Device");
        wifi2.setImageResource(R.drawable.avd_coc);
        text.setTextColor(ctx.getResources().getColor(R.color.connected));

        String url=MONG_HOST_IP+"/rpc/read_water_flow";
        utl.l("WIFI_",url);
        JSONObject jo=new JSONObject();
        try {
            jo.put("api_key",API_KEY);
            jo.put("sensor_id",10);




        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(url).addJSONObjectBody(jo).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                swipe.setRefreshing(false);

                if(response.toString().toLowerCase().contains("success"))
                {
                    try {
                        String re="_____\n"+
                                "\n"+"Free RAM : "+response.getString("free_ram")+
                                "\n"+"Uptime : "+response.getString("uptime")+
                                "\n"+"Status : "+response.getString("status")+
                        "\nSensor Data : "+response.getString("sensor_10")

                        ;


                        text.setText("Device Connected\nSensor API Response\n" + re);




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {


                    text.setText("Device Connected\nSensor API Response\n" + response.toString());
                }
                utl.l("WIFI_",response);



            }

            @Override
            public void onError(ANError ANError) {

                utl.l(ANError.getErrorDetail());
                utl.l(ANError.getErrorBody());

            }
        });

        ;




    }




}
