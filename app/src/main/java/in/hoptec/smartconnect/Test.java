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
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.hoptec.smartconnect.utils.ApManager;

public class Test extends AppCompatActivity {

    /******WIFI CONNECTION VARS******/

    private String MONG_HOST_IP = "http://192.168.4.1";
    private String AP_NAME = "MONG_TEST";
    private String AP_PASS = "password";
    private String API_KEY = "AEZAKMI";

    private boolean scanDone = false;
    private boolean scanInitiatedByApp = false;

    private boolean isAppInDisconnectionMode = false;



    private boolean connected = false;
    private WifiManager mWifiManager;


    /******UI VARS******/
    private TextView logTextView;
    private CoordinatorLayout coordinatorLayout;
    private ImageView wifiActionIndicator;
    private LinearLayout disconnectButton;
    private LinearLayout connectButton;
    private LinearLayout refreshButton;

    private ImageView logo;
    private DrawerLayout drawerLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout header;

    private Toolbar toolbar;
    private FloatingActionButton fabConnect, fabDisconnect;

    private Context ctx;
    private Activity act;

    private int appBarHeight=200;
    private long FAB_ANIM_DUR=400;
    private boolean LOG_UP=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        act = this;

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("");

        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        header=(LinearLayout)findViewById(R.id.header);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        fabConnect = (FloatingActionButton) findViewById(R.id.fab);
        fabDisconnect = (FloatingActionButton) findViewById(R.id.fab2);
        logTextView=(TextView)findViewById(R.id.logs);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.cont);
        wifiActionIndicator=(ImageView)findViewById(R.id.wifiAction);
        disconnectButton=(LinearLayout)findViewById(R.id.disconnect);
        connectButton=(LinearLayout)findViewById(R.id.connect);
        refreshButton=(LinearLayout)findViewById(R.id.refresh);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Integer verticalOffsetNormalized=-1*verticalOffset;

                Float alphaHeader=1.0f-verticalOffsetNormalized.floatValue()/ appBarHeight;
                Float alphaFAB=verticalOffsetNormalized.floatValue()/ appBarHeight;

                fabConnect.animate().setDuration(FAB_ANIM_DUR).alpha(alphaFAB);
                fabDisconnect.animate().setDuration(FAB_ANIM_DUR).alpha(alphaFAB);

                header.setAlpha(alphaHeader);


            }
        });

        fabConnect.animate().alpha(0f);
        fabDisconnect.animate().alpha(0f);


        initNavigationDrawer();;
        expandToolbar();
        initOnCLickListeners();

        mWifiManager = (WifiManager) act.getSystemService(WIFI_SERVICE);


        startConnection();


    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void addPressReleaseAnimation(final View base)
    {

        final Animation press= AnimationUtils.loadAnimation(ctx,R.anim.rec_zoom_in);
        final Animation release= AnimationUtils.loadAnimation(ctx,R.anim.rec_zoom_nomal);

        base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        base.startAnimation(press);
                        break;
                    case MotionEvent.ACTION_UP:

                        base.startAnimation(release);
                        break;
                    case MotionEvent.ACTION_CANCEL:

                        base.startAnimation(release);
                        break;
                    default:
                        break;
                }



                return false;
            }
        });


    }


    private void initOnCLickListeners()
    {

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        addPressReleaseAnimation(connectButton);



        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        addPressReleaseAnimation(disconnectButton);



        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        addPressReleaseAnimation(refreshButton);


        addPressReleaseAnimation(fabConnect);
        addPressReleaseAnimation(fabDisconnect);


        fabConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                connect();

            }
        });


        fabDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();


            }
        });



    }


    private void expandToolbar()
    {


        fabConnect.animate().setDuration(FAB_ANIM_DUR).alpha(0f);
        fabDisconnect.animate().setDuration(FAB_ANIM_DUR).alpha(0f);



    }

    private void initNavigationDrawer()
    {

        final NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {

                logo=(ImageView)navigationView.getHeaderView(0).findViewById(R.id.logo);
                logo.setVisibility(View.INVISIBLE);
            }
        },400);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.sensors:
                        Toast.makeText(getApplicationContext(),"Sensors",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.reset:
                        Toast.makeText(getApplicationContext(),"Reset", Toast.LENGTH_SHORT).show();
                        reset();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.about:
                        Toast.makeText(getApplicationContext(),"About", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView versionTextView = (TextView)header.findViewById(R.id.tv_email);
        versionTextView.setText("v"+BuildConfig.VERSION_NAME);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                final ImageView logo=(ImageView)v.findViewById(R.id.logo);

                logo.setVisibility(View.INVISIBLE);
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);

                final ImageView logo=(ImageView)v.findViewById(R.id.logo);

                utl.animate_avd(logo);
                try {
                    logo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logo.setVisibility(View.VISIBLE);

                        }
                    },50);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    BroadcastReceiver suppliantChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(isAppInDisconnectionMode)
                return;

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
                        logTextView.setText("Connected !");

                        //getWaterFlowData();

                        bindNetwork();

                    }

                    break;
                case DISCONNECTED:

                    if (!connected) {

                        logTextView.setText("Connecting... !");
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


            if(isAppInDisconnectionMode)
                return;


            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    connected=false;
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


            if(isAppInDisconnectionMode)
                return;


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
                    logTextView.setText("Connected !");

                }


            } else {
                utl.l("WIFI_", "Dont Have Wifi Connection  ");

                logTextView.setText("Disconnedted !");
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


            if(isAppInDisconnectionMode)
                return;


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
                    logTextView.setText("Device Not in Range ! \nPlease Make Sure Device is turned on and " +
                            "is in Range and Pull Down to refresh .");


            }
        }


    };




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



    private void reset()
    {


        logTextView.setText("[RESET]");
        disconnect();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                connect();
            }
        },1000);


    }

    private void connect()
    {
        isAppInDisconnectionMode =false;
        utl.snack(coordinatorLayout,"Connecting !");
        startConnection();

    }

    private void refresh()
    {
        connected=false;
        isAppInDisconnectionMode =false;
        utl.snack(coordinatorLayout,"Refreshing !");
        startConnection();

    }

    private void disconnect()
    {
        connected=false;

        isAppInDisconnectionMode =true;
        utl.snack(coordinatorLayout,"Disconnecting !");
        unRegister();
        wifiActionIndicator.setImageResource(R.drawable.avd_conn);
        mWifiManager.disconnect();
        addLog("Disconnected");
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

        utl.animate_avd(wifiActionIndicator);


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

                wifiActionIndicator.setImageResource(R.drawable.avd_coc);

                addLog("WIFI_", "Respose : " + response.toString());


                if (response.toString().toLowerCase().contains("success")) {
                    try {

                        parse(response);
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



    private void parse(JSONObject response) throws Exception
    {

        String re = "_____\n" +
                "\n" + "Free RAM : " + response.getString("free_ram") +
                "\n" + "Uptime : " + response.getString("uptime") +
                "\n" + "Status : " + response.getString("status") +
                "\nSensor Data : ";
        String rx= response.getString("serial");

        if (rx.length()>1) {

            String usefulData = rx;

            usefulData = usefulData.substring(usefulData.indexOf("$") + 1);
            usefulData = usefulData.substring(0, usefulData.indexOf("~"));

            String [] data= (usefulData.split(","));


            for (int i=0;i<data.length;i++)
            {
                re=re+"\nSensor "+i+" Val : "+data[i];
            }
        } else {

        }
        logTextView.setText("Device Connected\nSensor API Response\n" + re);


    }


}
