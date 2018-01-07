package in.hoptec.smartconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.hoptec.smartconnect.adapters.BoxesAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class Main extends BaseActivity {

    AppBarLayout appBarLayout;
    LinearLayout header;

    Toolbar toolbar;
    FloatingActionButton fab_help, fab_donate;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @BindView(R.id.cont)CoordinatorLayout cont;

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


                getHelp();

            }
        });


        fab_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                donate();


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

                utl.l("MAIN","Voff : "+verticalOffset+" \nAlpha "+alpha+"\n Max H "+oHie);


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



        box_list =new ArrayList<>();
        int i=0;
        do {
            box_list.add(new BoxesAdapter.Dummy(i));
        } while (i++<70);


    }

    public void initOnCLickListeners()
    {

        c_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHelp();
            }
        });
        setAnimPressRel(c_help);



        c_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info();
            }
        });
        setAnimPressRel(c_info);



        c_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donate();
            }
        });
        setAnimPressRel(c_donate);


        setAnimPressRel(fab_help);
        setAnimPressRel(fab_donate);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipe.setRefreshing(false);


                        box_list =new ArrayList<>();
                        int i=0;
                        do {
                            box_list.add(new BoxesAdapter.Dummy(i));
                        } while (i++<10);


                    }
                },2000);
            }
        });



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




    public void getHelp()
    {


        utl.snack(cont,"Get Help !");


    }



    public void donate()
    {


        utl.snack(cont,"Donate !");



    }

    public void info()
    {



        utl.snack(cont,"Info !");





    }
    @BindView(R.id.swipe)SwipeRefreshLayout swipe;
    @BindView(R.id.include)NestedScrollView nest;
     RecyclerView.LayoutManager mLayoutManager;
    ArrayList<BoxesAdapter.Dummy> box_list;
    BoxesAdapter mAdapter;
















}
