package in.hoptec.smartconnect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.smartconnect.adapters.BoxesAdapter;
import in.hoptec.smartconnect.utils.ApManager;

public class Main extends BaseActivity {

/*
*
*
*
    String  MONG_HOST_IP = "http://192.168.4.1";
    String AP_NAME="MONG_TEST";
    String AP_PASS="password";
    String API_KEY="AEZAKMI";
*/

    String  MONG_HOST_IP = "http://192.168.4.1";
    String AP_NAME="MONG_TEST";
    String AP_PASS="password";
    String API_KEY="AEZAKMI";






    boolean LOG_UP=false;

    public void addLog(String log)
    {

        String tx="";
        if(LOG_UP)
        tx=( "\n-----\n"+log+text.getText().toString());
        else {
            tx=(text.getText().toString()+ "\n-----\n"+log);

        }
       Log.d("Logging",""+log);


        text.setText(tx);


    }

    boolean DISCON_MODE=false;


    public void addLog(String tag,String log)
    {



        String tx="";
        if(LOG_UP)
            tx=( "\n-----\n"+log+text.getText().toString());
        else {
            tx=(text.getText().toString()+ "\n-----\n"+log);

        }Log.d(tag,log);

        text.setText(tx);



    }





    AppBarLayout appBarLayout;
    LinearLayout header;

    Toolbar toolbar;
    FloatingActionButton fab_help, fab_donate;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }



    BroadcastReceiver mWifiScanReceiver =null,mWifiStateChangedReceiver=null,mConnectedReciever=null;


     @BindView(R.id.logs)TextView text;
    @BindView(R.id.cont)CoordinatorLayout cont;
    @BindView(R.id.wifi2) ImageView wifi2;
    @BindView(R.id.c_info) LinearLayout c_info;
    @BindView(R.id.c_help) LinearLayout c_help;
    @BindView(R.id.c_donate) LinearLayout c_donate;

    public Context ctx;
    public Activity act;
    int oHie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx=this;
        act=this;

        ButterKnife.bind(this);


        setTitle("");
        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        header=(LinearLayout)findViewById(R.id.header);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);

        fab_help = (FloatingActionButton) findViewById(R.id.fab);
        fab_donate = (FloatingActionButton) findViewById(R.id.fab2);

        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                connect();

            }
        });


        fab_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();


            }
        });



        oHie=200;//appBarLayout.getTop()-appBarLayout.getBottom();

        ;;///Float.valueOf(""+appBarLayout.getMeasuredHeight());//utl.pxFromDp(ctx,getResources().getDimension(R.dimen.app_bar_height));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {



                Integer voff=-1*verticalOffset;

                Float alpha=1.0f-voff.floatValue()/oHie;
                Float alpha2=voff.floatValue()/oHie;


                fab_help.animate().setDuration(FAB_ANIM_DUR).alpha(alpha2);
                fab_donate.animate().setDuration(FAB_ANIM_DUR).alpha(alpha2);




                header.setAlpha(alpha);

               // addLog();("MAIN","Voff : "+verticalOffset+" \nAlpha "+alpha+"\n Max H "+oHie);


                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                   // collapseToolbar();


                }
                else
                {
                   // expandToolbar();
                    //Expanded


                }


            }
        });

        fab_help.animate().alpha(0f);
        fab_donate.animate().alpha(0f);


        initNavigationDrawer();;
        expandToolbar();
        initOnCLickListeners();



        mWifiManager = (WifiManager) act.getSystemService(WIFI_SERVICE);


        m_start();


    }

    public void initOnCLickListeners()
    {

        c_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
            }
        });
        setAnimPressRel(c_help);



        c_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        setAnimPressRel(c_info);



        c_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        setAnimPressRel(c_donate);


        setAnimPressRel(fab_help);
        setAnimPressRel(fab_donate);




    }


    public void setAnimPressRel(final View base)
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

    long FAB_ANIM_DUR=400;
    public void expandToolbar()
    {


        fab_help.animate().setDuration(FAB_ANIM_DUR).alpha(0f);
        fab_donate.animate().setDuration(FAB_ANIM_DUR).alpha(0f);



    }

    public void collapseToolbar()
    {

        fab_help.animate().setDuration(FAB_ANIM_DUR).alpha(1f);
        fab_donate.animate().setDuration(FAB_ANIM_DUR).alpha(1f);


    }

    ImageView logo;
    DrawerLayout drawerLayout;
    public void initNavigationDrawer() {

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
                    case R.id.cloud:
                        Toast.makeText(getApplicationContext(),"Sensors",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
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
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("v"+BuildConfig.VERSION_NAME);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case  R.id.about:



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void reset()
    {

        text.setText("[RESET]");
        disconnect();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                connect();
            }
        },1000);


    }


    public void connect()
    {
        DISCON_MODE=false;


        utl.snack(cont,"Connecting !");

        m_start();

    }



    public void refresh()
    {
        DISCON_MODE=false;


        utl.snack(cont,"Refreshing !");

        m_start();


    }

    public void disconnect()
    {


    DISCON_MODE=true;

        utl.snack(cont,"Disconnecting !");
        unrgisterRecievers();
        wifi2.setImageResource(R.drawable.avd_conn);


        mWifiManager.disconnect();

        addLog("Disconnected");



    }
    @Nullable @BindView(R.id.include)NestedScrollView nest;
     RecyclerView.LayoutManager mLayoutManager;
    ArrayList<BoxesAdapter.Dummy> box_list;
    BoxesAdapter mAdapter;


    public static WifiManager mWifiManager;

     public void m_start()
    {

        if(DISCON_MODE)
        {
            return;
        }

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
            // addLog();();("WIFI_","AP Mode Toggle to : "+ApManager.isApOn(ctx));
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
        if(mWifiManager.getWifiState()== WifiManager.WIFI_STATE_ENABLED)
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

        addLog("WIFI_","Connected to WIFI - "+ssid);

        if(ssid.contains(AP_NAME))
        {
            addLog("WIFI_","Connected to device ALREADY.");
            getWaterFlowData();
        }
        else {
            addLog("WIFI_","Connected another AP ALREADY DIsconnecting....And connectingg to "+AP_NAME);


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

                if(DISCON_MODE)
                {
                    return;
                }

                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> mScanResults = mWifiManager.getScanResults();

                    addLog("WIFI_", "WIFI STATE SCANNING STATE IS " + CUR_SCAN_STATE);

                    String ssids = "";
                    for (ScanResult scanResults : mScanResults
                            ) {

                        // addLog();();("WIFI_ RR : "+scanResults.SSID);

                        ssids += "\n" + scanResults.SSID;

                    }

                    addLog("WIFI_", "Listing deviceds done .");
                     addLog("WIFI_", ssids);

                    if(ssids.contains(AP_NAME))
                    {
                        connect(AP_NAME,AP_PASS);
                    }
                    else {
                        addLog("Device Not in Range ! \nPlease Make Sure Device is turned on and is in Range and Pull Down to refresh .");
                    }


                }
            }


        };





        mConnectedReciever=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(DISCON_MODE)
                {
                    return;
                }
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
    public void onDestroy() {
        unrgisterRecievers();

        super.onDestroy();



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


        addLog("Connected to Device");
        wifi2.setImageResource(R.drawable.avd_coc);

        String url=MONG_HOST_IP+"/rpc/read_water_flow";
        addLog("WIFI_",url);
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


                if(response.toString().toLowerCase().contains("success"))
                {
                    try {
                        String re="_____\n"+
                                "\n"+"Free RAM : "+response.getString("free_ram")+
                                "\n"+"Uptime : "+response.getString("uptime")+
                                "\n"+"Status : "+response.getString("status")+
                                "\nSensor Data : "+response.getString("sensor_10")

                                ;


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

        ;




    }















}
