package in.hoptec.smartconnect;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import in.hoptec.smartconnect.adapters.HelpPagerAdapter;
import in.hoptec.smartconnect.fragments.Help_Fr0;
import me.relex.circleindicator.CircleIndicator;

public class Splash extends AppCompatActivity {
    View pager_container,title_cont;

       View bg;
    public Context ctx;
    public Activity act;
    float init_pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ctx=this;
        act=this;


         ImageView mIcDownloadAnimator = (ImageView) findViewById(R.id.logo);
       final Drawable drawable = mIcDownloadAnimator.getDrawable();

        if (drawable instanceof Animatable) {
            //((Animatable) drawable).start();
        }

        final TextView textView = (TextView) findViewById(R.id.title);
        Spannable word = new SpannableString("File d");


        word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(word);
        Spannable wordTwo = new SpannableString("Box");

        wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.material_green_300)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(wordTwo);

        final Animation myAnim2 = AnimationUtils.loadAnimation(ctx, R.anim.slid_up);
        myAnim2.setDuration(getResources().getInteger(R.integer.spl_dur)/2);


        // textView.startAnimation(myAnim2);
        final ImageView mIcDownloadAnimator2 = (ImageView) findViewById(R.id.title_im);




        bg=findViewById(R.id.backg);
        title_cont=findViewById(R.id.title_cont);

        Animation myAnim = AnimationUtils.loadAnimation(ctx, R.anim.slid_down_from_top);

        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //  mIcDownloadAnimator2.startAnimation(myAnim2);


                ImageView mIcDownloadAnimator = (ImageView) findViewById(R.id.logo);
                final Drawable drawable = mIcDownloadAnimator.getDrawable();

                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }

               mIcDownloadAnimator2.animate().alpha(1f);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        title_cont.startAnimation(myAnim);


        pager_container=findViewById(R.id.pager_container);
        pager_container.setVisibility(View.GONE);

        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(utl.getKey("firstinstall",ctx)==null)
                {

                    if (drawable instanceof Animatable) {

                        ((Animatable) drawable).stop();
                    }

                    pager_container.setVisibility(View.VISIBLE);


                    Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slid_up);
                    animation.setDuration(500);
                    animation.setStartOffset(0);

                    pager_container.startAnimation(animation);





                    setUpIntro();

                }
                else  {
                    Intent intent=new Intent(ctx, Home.class);
                    startActivity(intent);
                    finish();


                }


              utl.setKey("firstinstall","1",ctx);




            }
        },getResources().getInteger(R.integer.spl_dur_ttl) );
        utl.animateBackGround(bg,curSt,colorsS[0],false,dur);
        curSt="#0a7e07";


            init_pos=title_cont.getY();
      /*  final View activityRootView = findViewById(R.id.backg);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {

                       float pos= Help_Fr0.curv.getY();
                        if(init_pos>pos)
                            title_cont.setY(pos-200);


                    }
                });*/




        setUpPermissions();

    }

    ArrayList<Integer> colors;

    public static  ViewPager pager ;
    public static int curItem=0;



    Integer dur=1000;
    public static  HelpPagerAdapter pageAdapter;
    String curSt="#ffff5252";
    public static String  colorsS [] ={"#ffc53929","#ff0b8043","#ff3367d6"};
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
                        utl.animateBackGround(bg,curSt,colorsS[0],false,dur);
                        curSt=colorsS[0];

                        break;
                    case 1:
                        utl.animateBackGround(bg,curSt,colorsS[1],false,dur);
                        curSt=colorsS[1];

                        break;
                    case 2:
                        utl.animateBackGround(bg,curSt,colorsS[2],false,dur);
                        curSt=colorsS[2];

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

        Help_Fr0 frag=new Help_Fr0();

        frag.image= R.drawable.ic_help_animated_file;
        frag.message="Connect to RO";
        frag.islast=false;
        frag.pos=0;
        frag.clr=colors.get(0);
         fList.add(frag);

        /***/
        frag=new Help_Fr0();

        frag.image= R.drawable.ic_help_animated_lock;
        frag.message="View Sensors";
        frag.islast=false;
        frag.pos=1;
        frag.clr=colors.get(1);
         fList.add(frag);


        /***/
        frag=new Help_Fr0();

        frag.image= R.drawable.ic_help_animated_cloud;
        frag.message="Notifications";
        frag.islast=true;
        frag.pos=2;
        frag.clr=colors.get(2);
         fList.add(frag);



        return fList;
     }







    boolean permissionOK=false,firebaseOK=false;





    public void setUpPermissions()
    {

        ActivityCompat.requestPermissions(act,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                },
                1);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        &&grantResults[2] == PackageManager.PERMISSION_GRANTED  && grantResults[0] == PackageManager.PERMISSION_GRANTED  && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    permissionOK=true;


                } else {

                    Toast.makeText(ctx, "Permission denied to R/W your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*****************Firebase*****************/




    }

