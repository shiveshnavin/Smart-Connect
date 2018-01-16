package in.hoptec.smartconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import in.hoptec.smartconnect.adapters.HelpPagerAdapter;
import in.hoptec.smartconnect.fragments.HelpTabFragment;
import me.relex.circleindicator.CircleIndicator;

public class Splash extends BaseActivity {

    private boolean showIntroTabs =false;
    private View viewPagerContainer;
    private View bg;
    private Context ctx;
    private Activity act;

    private ArrayList<Integer> colors;

    public static ViewPager pager ;
    public static int curItem=0;

    private Integer dur=1000;
    public static  HelpPagerAdapter pageAdapter;
    private String curBGColor ="#ffff5252";
    private String  colorsS [] ={"#ffc53929","#ff0b8043","#ff3367d6"};

    private boolean permissionOK=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ctx=this;
        act=this;

       ImageView logoImageView = (ImageView) findViewById(R.id.logo);
       final Drawable drawable = logoImageView.getDrawable();

        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        bg=findViewById(R.id.backg);

        viewPagerContainer =findViewById(R.id.pager_container);
        viewPagerContainer.setVisibility(View.GONE);


        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(utl.getKey("firstinstall",ctx)==null&&false)
                {

                    if (drawable instanceof Animatable) {

                        ((Animatable) drawable).stop();
                    }

                    viewPagerContainer.setVisibility(View.VISIBLE);


                    Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slid_up);
                    animation.setDuration(500);
                    animation.setStartOffset(0);

                    viewPagerContainer.startAnimation(animation);





                    setUpIntro();

                }
                else if(permissionOK) {


                    launch();


                }


              utl.setKey("firstinstall","1",ctx);




            }
        },getResources().getInteger(R.integer.spl_dur_ttl) );

        utl.animateBackGround(bg, curBGColor,colorsS[0],false,dur);
        curBGColor ="#0a7e07";


        setUpPermissions();

    }


    public void setUpIntro()
    {

        colors=new ArrayList<>();

        colors.add(R.color.material_deep_orange_700);
        colors.add(R.color.material_green_700);
        colors.add(R.color.material_blue_700);


        List<Fragment> fragments = getFragments();
        pageAdapter = new HelpPagerAdapter(getSupportFragmentManager(), fragments);
        pager=       (ViewPager)findViewById(R.id.help_pager);
        pager.setAdapter(pageAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

               // utl.l("Pos : "+position+"\n Offset : "+positionOffset+"\n Pix "+positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {

                utl.l("Page Sel : "+position);

                curItem=position;

                switch (position)
                {
                    case 0:
                        utl.animateBackGround(bg, curBGColor,colorsS[0],false,dur);
                        curBGColor =colorsS[0];

                        break;
                    case 1:
                        utl.animateBackGround(bg, curBGColor,colorsS[1],false,dur);
                        curBGColor =colorsS[1];

                        break;
                    case 2:
                        utl.animateBackGround(bg, curBGColor,colorsS[2],false,dur);
                        curBGColor =colorsS[2];

                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

         CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
         indicator.setViewPager(pager);


    }

    private List<Fragment> getFragments() {

        List<Fragment> fList = new ArrayList<Fragment>();

        HelpTabFragment frag=new HelpTabFragment();

        frag.image= R.drawable.ic_help_animated_file;
        frag.message="Connect to RO";
        frag.islast=false;
        frag.pos=0;
        frag.clr=colors.get(0);
         fList.add(frag);

        /***/
        frag=new HelpTabFragment();

        frag.image= R.drawable.ic_help_animated_lock;
        frag.message="View Sensors";
        frag.islast=false;
        frag.pos=1;
        frag.clr=colors.get(1);
         fList.add(frag);


        /***/
        frag=new HelpTabFragment();

        frag.image= R.drawable.ic_help_animated_cloud;
        frag.message="Notifications";
        frag.islast=true;
        frag.pos=2;
        frag.clr=colors.get(2);
         fList.add(frag);



        return fList;
     }

    public void setUpPermissions()
    {


        ActivityCompat.requestPermissions(act,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.WRITE_SETTINGS
                },
                1);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        &&grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                   permissionOK=true;

                } else {

                    Toast.makeText(ctx, "Permission denied to R/W your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    boolean alreadyLaunched=false;

    public void launch()
    {

        if (permissionOK&&!alreadyLaunched) {
            alreadyLaunched=true;
            Intent intent=new Intent(ctx, WifiConnectActivity.class);
            startActivity(intent);
            finish();
        }
        else if(!alreadyLaunched) {
            utl.diag(ctx, "Insufficient permissions !", "Please Restart App", "RESTART", new utl.ClickCallBack() {
                @Override
                public void done(DialogInterface dialogInterface) {
                    Intent intent=new Intent(ctx, Splash.class);
                    startActivity(intent);
                    finish();
                }
            });
        }


    }

    }

