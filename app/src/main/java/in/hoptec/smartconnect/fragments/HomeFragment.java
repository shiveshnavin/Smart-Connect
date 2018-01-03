package in.hoptec.smartconnect.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.hoptec.smartconnect.R;
import in.hoptec.smartconnect.adapters.BoxesAdapter;
import in.hoptec.smartconnect.database.BoxMeta;
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
public class HomeFragment extends Fragment implements Transact{

    Transact cb;


    public HomeFragment()
    {}


    private RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void transactToState(int state)
    {




    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                // add your logic here


                setWifis(mScanResults);
                swipe.setRefreshing(false);

                utl.showDig(false,ctx);


                wifi2.setImageResource(R.drawable.ic_vector);


                text.setText("Connected to jarvis");
                text.setTextColor(ctx.getResources().getColor(R.color.green_100));


                if(utl.js.toJson(mScanResults).contains("jarvis")&&!utl.isConnected())
                {
                    wifi2.setImageResource(R.drawable.ic_vector);


                    text.setText("Connected to jarvis");
                    text.setTextColor(ctx.getResources().getColor(R.color.green_100));

                    connect("jarvis","goforit@delhi");
                   // utl.toast(ctx,"Connecting !!");
                }


            }
        }
    };

    public Context ctx;
    public Activity act;

    ImageView wifi2;
    SwipeRefreshLayout swipe;

    WifiManager mWifiManager;
    View view;
    ArrayList<BoxesAdapter.Dummy> box_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_home, container, false);

        ctx=getContext();
        act=getActivity();

        recyclerView =(RecyclerView)view.findViewById(R.id.boxes);
        swipe=(SwipeRefreshLayout)view.findViewById(R.id.swipe);

        text=(TextView) view.findViewById(R.id.text);


        wifi2=(ImageView) view.findViewById(R.id.wifi);

        mWifiManager = (WifiManager) act.getSystemService(WIFI_SERVICE);
        try {
            act.unregisterReceiver(mWifiScanReceiver); } catch (Exception e) {
            e.printStackTrace();
        }
            act.registerReceiver(mWifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mWifiManager.startScan();



        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                utl.showDig(true,ctx);

                mWifiManager.startScan();



            }
        });

       // utl.showDig(true,ctx);


        try {

            wifi2.setImageResource(R.drawable.avd_conn);
            final Drawable drawable = wifi2.getDrawable();

            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }




        } catch (Exception e) {
            e.printStackTrace();
        }

        if(ApManager.isApOn(ctx))
        {
            ApManager.configApState(ctx);

            utl.l("AP Mode Toggle to : "+ApManager.isApOn(ctx));

        }


        wifi=(WifiManager)act.getSystemService(Context.WIFI_SERVICE);

        if(wifi.getWifiState()==WifiManager.WIFI_STATE_DISABLED||!wifi.isWifiEnabled())
        {

            // wifi.setWifiEnabled(false);//Turn off Wifi

            wifi.setWifiEnabled(true);//Turn on Wifi
            utl.l("STA Mode Toggle to : ON" );


      /*      act.registerReceiver(this.WifiStateChangedReceiver,
                    new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
*/


        }
        else {

            if(utl.isConnected())
            {
                wifi2.setImageResource(R.drawable.ic_vector);

                text.setText("Connected to jarvis");
                text.setTextColor(Color.parseColor("#2baf2b"));
            }
        }


        new Handler().postDelayed(r,2000);

        return view;
    }


    TextView text;;


    private BroadcastReceiver WifiStateChangedReceiver
            = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE ,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch(extraWifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    utl.l("WIFI STATE DISABLED");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    utl.l("WIFI STATE DISABLING");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    mWifiManager.startScan();

                    utl.l("WIFI STATE ENABLED");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    utl.l("WIFI STATE ENABLING");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    utl.l("WIFI STATE UNKNOWN");
                    break;
            }

        }};



     WifiManager wifi;


    final Runnable r=new Runnable() {
        @Override
        public void run() {

            if(wifi.isWifiEnabled())
                mWifiManager.startScan();

            else
            {
                wifi.setWifiEnabled(true);//Turn on Wifi
                utl.l("STA Mode Toggle to : ON" );

                new Handler().postDelayed(r, 2000);

            }




        }
    };


    public void setWifis(List<ScanResult> mScanResults)
    {


        mLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(mLayoutManager);

        box_list =new ArrayList<>();
        int i=0;
      /*  do {
            box_list.add(new BoxesAdapter.Dummy(i));
        } while (i++<10);
*/

        for (ScanResult r:mScanResults
             ) {





            box_list.add(new BoxesAdapter.Dummy(r));










        }


        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new BoxesAdapter(ctx, box_list){


            @Override
            public void click(int pos, BoxesAdapter.Dummy cat) {
                super.click(pos,cat);

                if(pos>= box_list.size())
                    return;
                box_list.remove(cat);
                //notifyItemRemoved(pos);
                notifyItemRemoved(pos);
               recyclerView.postDelayed(new Runnable() {
                   @Override
                   public void run() {

                       notifyDataSetChanged();

                   }
               },500);

            }

            @Override
            public void clickLong(int pos) {
                super.clickLong(pos);

                //  box_list.add(new BoxesAdapter.Dummy());

                //  notifyItemInserted(pos);
                //notifyDataSetChanged();


            }
        };



        LandingAnimator animator = new LandingAnimator(new OvershootInterpolator(1f));
        recyclerView.setItemAnimator(animator);
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(mAdapter);
        alphaAdapter.setDuration(1000);



        recyclerView.setAdapter(alphaAdapter);




    }


    BroadcastReceiver receiver = new WifiReciever() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {



                if(utl.isConnected())
                //  utl.diag(act,"CONNECTED !","Connected to \"jarvis\" .");
                    wifi2.setImageResource(R.drawable.ic_vector);
            } else {


            }
        }
    };



    public void connect(String ssid,String key)
    {

        IntentFilter it=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        try{

            act.unregisterReceiver(mWifiScanReceiver);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        act.registerReceiver(receiver, it);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        WifiManager wifiManager = (WifiManager)act.getSystemService(WIFI_SERVICE);
//remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();


        wifi2.setImageResource(R.drawable.ic_vector);


        text.setText("Connected to jarvis");
        text.setTextColor(ctx.getResources().getColor(R.color.green_100));



    }


}
