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
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
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

import java.util.List;

import in.hoptec.smartconnect.utils.ApManager;

public class WifiConnectActivity extends BaseActivity {

    /******WIFI AP CONSTANTS******/
    private String MONG_HOST_IP = "http://192.168.4.1";
    private String AP_NAME="MONG_TEST";
    private String AP_PASS="password";
    private String API_KEY="AEZAKMI";


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

    /******WIFI CONNECTION VARS******/
    private BroadcastReceiver mWifiScanReceiver =null,mWifiStateChangedReceiver=null,mConnectedReciever=null;
    private WifiManager mWifiManager;
    private final int I_SCAN_REC=12,I_WIFI_STATE_REC=232,I_I_WIFI_CONN_REC=3432;
    private boolean isAppInDisconnectionMode =false;
    private boolean isScanning=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ctx=this;
        act=this;
        super.onCreate(savedInstanceState);
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



    }


    @Override
    protected void onPostResume() {

        super.onPostResume();
        startConnection();

    }

    @Override
    protected void onPause() {
        disconnect();
        super.onPause();
    }

    //For Making Use of VectorDrawables in FAB
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
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
        isAppInDisconnectionMode =false;
        utl.snack(coordinatorLayout,"Refreshing !");
        startConnection();

    }

    private void disconnect()
    {
        isAppInDisconnectionMode =true;
        utl.snack(coordinatorLayout,"Disconnecting !");
        unrgisterRecievers();
        wifiActionIndicator.setImageResource(R.drawable.avd_conn);
        mWifiManager.disconnect();
        addLog("Disconnected");
    }

    private void startConnection()
    {

        if(isAppInDisconnectionMode) {
            return;
        }
        initRecievers();
        wifiActionIndicator.setImageResource(R.drawable.avd_conn);
        utl.animate_avd(wifiActionIndicator);

        if(ApManager.isApOn(ctx))
        {
            utl.diag(ctx, "Hotspot On !", "Please turn OFF the WiFi hotspot and click OK to continue !", "TURNED OFF", new utl.ClickCallBack() {
                @Override
                public void done(DialogInterface dialogInterface) {
                    startConnection();
                }
            });

            return;

        }

        if(utl.isWifiConnected(act))
        {

            isWifiConnectedAlready();

        }
        else {

            registerRecievers(0);

            if(!isWifiOn()){

                mWifiManager.setWifiEnabled(true);

            }



        }

    }

    private boolean isWifiOn()
    {
        boolean isWifiOn=false;
        if(mWifiManager.getWifiState()== WifiManager.WIFI_STATE_ENABLED)
        {
            isWifiOn=true;
        }
        else {
            isWifiOn=false;
        }
        return isWifiOn;

    }

    private String  getCurrentNetwork()
    {
        String ssid="";
        WifiInfo wifiInfo;
        wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid =""+ wifiInfo.getSSID();
        }

        return ssid;

    }

    private void isWifiConnectedAlready()
    {
        String currentWifiSSID = getCurrentNetwork();
        addLog("WIFI_","Connected to WIFI - "+ currentWifiSSID);
        if(currentWifiSSID.contains(AP_NAME))
        {
            addLog("WIFI_","Connected to device ALREADY.");
            getWaterFlowData();
        }
        else {
            addLog("WIFI_","Connected another AP ALREADY DIsconnecting....And connectingg to "+AP_NAME);
            mWifiManager.disconnect();
            registerRecievers(0);

        }

    }

    private void initRecievers()
    {


        mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                if(isAppInDisconnectionMode)
                    return;

                isScanning=false;

                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> mScanResults = mWifiManager.getScanResults();
                    addLog("WIFI_", "WIFI STATE SCANNING  IS COMPLETED"  );

                    String ssids = "";
                    for (ScanResult scanResults : mScanResults) {
                       ssids += "\n" + scanResults.SSID;
                    }
                    addLog("WIFI_", "Listing deviceds done .");
                    addLog("WIFI_", ssids);

                    if(ssids.contains(AP_NAME))
                        connect(AP_NAME,AP_PASS);
                    else
                        addLog("Device Not in Range ! \nPlease Make Sure Device is turned on and " +
                                "is in Range and Pull Down to refresh .");


                }
            }


        };

        mConnectedReciever=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(isAppInDisconnectionMode)
                    return;

                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();

                bindNetwork();
                if(netInfo==null)
                {
                    utl.e("WIFI_","NetInfo Is Null");
                }
                else {
                    utl.e("WIFI_","NetType Is : "+netInfo.getTypeName()+" \n"+netInfo.getType()+"\nState : "+netInfo.getState());
                }
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
                {
                    utl.l("WIFI_","Have Wifi Connection ,  BInding Now ");

                    isWifiConnectedAlready();
                    bindNetwork();



                }
                else{
                    Log.d("WIFI_", "Don't have Wifi Connection");
                    if(mWifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED&&!isScanning){
                        mWifiManager.startScan();
                        isScanning=true;
                    }


                }

            }
        };



        mWifiStateChangedReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



                int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE ,
                        WifiManager.WIFI_STATE_UNKNOWN);

                switch(extraWifiState){
                    case WifiManager.WIFI_STATE_DISABLED:
                        utl.l("WIFI_","WIFI STATE DISABLED");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        utl.l("WIFI_","WIFI STATE DISABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:

                        utl.l("WIFI_","WIFI STATE ENABLED");

                        if(utl.isWifiConnected(act)){

                            utl.l("WIFI_","WIFI Connected ,  BInding Now ");

                            isWifiConnectedAlready();
                            bindNetwork();

                        }


                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        utl.l("WIFI_","WIFI STATE ENABLING");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        utl.l("WIFI_","WIFI STATE UNKNOWN");
                        break;
                }



            }
        };

    }

    private void registerRecievers(int n)
    {

        if (n==0) {

            act.registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            act.registerReceiver(mConnectedReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            act.registerReceiver(mWifiStateChangedReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        } else if(n==I_WIFI_STATE_REC) {
            act.registerReceiver(mWifiStateChangedReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        }else if(n==I_SCAN_REC) {
            act.registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        }
        else if(n==I_I_WIFI_CONN_REC) {
            act.registerReceiver(mConnectedReciever,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }

    }

    private void unrgisterRecievers()
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

    private void connect(String ssid,String key)
    {

        utl.e("Connecting to Wifi : "+ssid+" : "+key) ;
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        int netId = mWifiManager.addNetwork(wifiConfig);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();

    }

    private void getWaterFlowData()
    {
        unrgisterRecievers();

        addLog("Connected to Device");
        wifiActionIndicator.setImageResource(R.drawable.avd_coc);

        String url=MONG_HOST_IP+"/rpc/read";
        addLog("WIFI_",url);
        JSONObject jo=new JSONObject();
        try {

            jo.put("api_key",API_KEY);
            jo.put("read_id",10);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(url).addJSONObjectBody(jo).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {


                if(response.toString().toLowerCase().contains("success"))
                {
                    try {
                        String re="_____\n"+
                                "\n"+"Free RAM : "+response.getString("free_ram")+
                                "\n"+"Uptime : "+response.getString("uptime")+
                                "\n"+"Status : "+response.getString("status")+
                                "\nSerial Data : "+response.getString("serial");

                        addLog("Device Connected\nSensor API Response\n" + re);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {

                    addLog("Device Connected\nSensor API Response\n" + response.toString());
                }

                addLog("WIFI_",response.toString());
            }

            @Override
            public void onError(ANError ANError) {
                addLog(ANError.getErrorDetail());
                addLog(ANError.getErrorBody());
            }
        });

    }

    private void addLog(String log)
    {

        String tx="";
        if(LOG_UP)
            tx=( "\n-----\n"+log+ logTextView.getText().toString());
        else
            tx=(logTextView.getText().toString()+ "\n-----\n"+log);

        Log.d("Logging",""+log);
        logTextView.setText(tx);

    }

    private void addLog(String tag,String log)
    {

        String tx="";
        if(LOG_UP)
            tx=( "\n-----\n"+log+ logTextView.getText().toString());
        else
            tx=(logTextView.getText().toString()+ "\n-----\n"+log);

        Log.d(tag,log);
        logTextView.setText(tx);

    }


    private void bindNetwork()
    {

            final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest request = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                request = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();

            final ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback(){

                    @Override
                    public void onAvailable (Network network){


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            utl.e(("WIFI_"),"Binding Network : Network Available : "+network.toString());
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            cm.bindProcessToNetwork(network);      // M and above

                        }
                        // or:
                       else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            cm.setProcessDefaultNetwork(network);  // L and above

                        }


                    }

            };

            cm.registerNetworkCallback(request, callback);
        }
    }
}
