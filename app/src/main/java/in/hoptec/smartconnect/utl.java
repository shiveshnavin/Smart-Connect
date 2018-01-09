package in.hoptec.smartconnect;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


import in.hoptec.smartconnect.utils.Constants;
import in.hoptec.smartconnect.utils.FileOperations;
import in.hoptec.smartconnect.utils.GenricCallback;
import in.hoptec.smartconnect.utils.GenricUser;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shivesh on 28/6/17.
 */

public class utl {

    public static boolean ANIM_LV1_ENABLED=true;
    public static boolean DISPLAY_ENABLED=true;
    public static boolean DEBUG_ENABLED=true;

    public static Gson js=new Gson();

    public static Context ctx;

    /*********************************************************/
    public static void init(Context ctxx)
    {
        ctx=ctxx;
    }


    public static void animate_avd(ImageView img)
    {

        try {
            final Drawable drawable = img.getDrawable();

            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        } catch (Exception e) {

            utl.e("ERROR WHILE ANIMATING IMAGEVIEW");
        }


    }

    public static void animate(View app, String property, int initv, int finalv, boolean repeat, int dur)
    {


        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                initv, finalv);
        colorAnim.setEvaluator(new ArgbEvaluator());

        if(repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }



    public static void animateBackGround(View app, String  initcolor, String finalcolor, boolean repeat, int dur)
    {

        String property="backgroundColor";
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                Color.parseColor(initcolor), Color.parseColor(finalcolor));
        colorAnim.setEvaluator(new ArgbEvaluator());

        if(repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }





    public static int getColor(@ColorRes int c)
    {
        int col=0;
        col=ctx.getResources().getColor(c);
        return col;
    }



    public static void fullScreen(Activity act)
    {
        try {
            act.requestWindowFeature(Window.FEATURE_NO_TITLE);
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getH(Context ctx)
    {
        WindowManager windowManager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();

    }


    public static int getW(Context ctx)
    {
        WindowManager windowManager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();

    }


    public  static void SlideUP(View view, Context context)
    {
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.slid_up));
    }

    public static void SlideDown(View view, Context context)
    {
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.slid_down));
    }


    public static String CLAN_PRO_NORMAL="ClanPro-Book.otf",
            RUBIK="Rubik-Regular.ttf"
            ,AVENIR_MED="Avenir-Medium.ttf"
            , CAVIAR="CaviarDreams.ttf",ROBOTO_THIN="Roboto-Thin.ttf";


    public static Typeface getFace(String font,Context ctx)
    {
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/"+font);

        if(face==null)
        {
            face=Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/"+AVENIR_MED);
        }
        return face;

    }



    public static Typeface setFace(String font,TextView textView)
    {
        Context ctx=textView.getContext();
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/"+font);

        if(face==null)
        {
            face= Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/"+AVENIR_MED);
        }
        textView.setTypeface(face);
        return face;

    }





    public static void changeTextColor(final TextView textView, int startColor, int endColor,
                                       final long animDuration, final long animUnit ){
        if (textView == null) return;

        final int startRed = Color.red(startColor);
        final int startBlue = Color.blue(startColor);
        final int startGreen = Color.green(startColor);

        final int endRed = Color.red(endColor);
        final int endBlue = Color.blue(endColor);
        final int endGreen = Color.green(endColor);

        final CountDownTimer ct= new CountDownTimer(animDuration, animUnit) {
            //animDuration is the time in ms over which to run the animation
            //animUnit is the time unit in ms, update color after each animUnit

            @Override
            public void onTick(long l) {
                int red = (int) (endRed + (l * (startRed - endRed) / animDuration));
                int blue = (int) (endBlue + (l * (startBlue - endBlue) / animDuration));
                int green = (int) (endGreen + (l * (startGreen - endGreen) / animDuration));

                textView.setTextColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onFinish() {
                textView.setTextColor(Color.rgb(endRed, endGreen, endBlue));

            }
        };

        ct.start();
    }



    public static String refineString(String red,String rep)
    {
        red = red.replaceAll("[^a-zA-Z0-9]", rep);
        return  red;
    }


    public static  boolean isValidMobile(String phone)
    {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isValidMail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String TAG="TAH UTL";
    public static void log(String t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void log(String t,String tt)
    {

        Log.d(""+t, ""+tt);
    }


    public static void l(String t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void l(String t,String tt)
    {

        Log.d(""+t, ""+tt);
    }




    public static void e(String t)
    {

        Log.e(""+TAG, ""+t);
    }

    public static void e(String t,String tt)
    {

        Log.e(""+t, ""+tt);
    }

    public static void l(Object t)
    {

        Log.d(""+TAG, ""+t);
    }


    public static void l(String t,Object tt)
    {

        Log.d(""+t, ""+tt);
    }



    public static void logout()
    {

        try {
            removeUserData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isWifiConnected(Activity act)
    {
        ConnectivityManager connManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {

            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isConnected()
    {
        boolean isCon=false;
        try {
            String command = "ping -c 1 google.com";
            isCon= (Runtime.getRuntime().exec (command).waitFor() == 0);
        } catch ( Exception e) {

            e.printStackTrace();
            isCon= false;
        }

        utl.l("isConnected : "+isCon);
        return isCon;
    }

    public static int getApkVerison(Context ctx)
    {
        try{
            int x;
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }



    public static void changeColorDrawable(ImageView imageView, @ColorRes int res) {

        try {
            DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(imageView.getContext(), res));
        } catch (Exception e) {
          //  e.printStackTrace();
            utl.l("Drawable TintErr 409");
        }


    }


    public static int getDominantColor1(Bitmap bitmap) {

        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());

        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            rC = colorMap.get(0).get(r);
            if (rC == null)
                rC = 0;
            colorMap.get(0).put(r, ++rC);

            gC = colorMap.get(1).get(g);
            if (gC == null)
                gC = 0;
            colorMap.get(1).put(g, ++gC);

            bC = colorMap.get(2).get(b);
            if (bC == null)
                bC = 0;
            colorMap.get(2).put(b, ++bC);
        }

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;
        }

        int dominantColor = Color.rgb(rgb[0], rgb[1], rgb[2]);

        return dominantColor;
    }



    public static Float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static Float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



    public static void showSoftKeyboard(Activity activity, View linearLayout) {

        InputMethodManager inputMethodManager =
                (InputMethodManager)activity. getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                linearLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }



    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.toLowerCase().contains(context.getPackageName())||
                                context.getPackageName().toLowerCase().contains(activeProcess.toLowerCase())
                                ) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().toLowerCase().contains(context.getPackageName())||
                    context.getPackageName().toLowerCase().contains(componentInfo.getPackageName().toLowerCase())
                    ) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    @SuppressWarnings("ResourceAsColor")
    public static void snack(Activity act, String t)
    {

        View rootView = act.getWindow().getDecorView().getRootView();
        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.red_300));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }




    public static void snack(View rootView,String t)
    {

        Context act=rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.red_300));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }


    public static void snack(Activity act,String t,String a,final GenricCallback cb)
    {

        View rootView = act.getWindow().getDecorView().getRootView();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onCall();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.red_300));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }






    public static void snack(View rootView,String t,String a,final GenricCallback cb)
    {

        Context act=rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView,  ""+t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onCall();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.red_300));

        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if(DISPLAY_ENABLED)
            snackbar.show();

    }






    public static interface ClickCallBack{

        public void done(DialogInterface dialogInterface);

    }
    public static void diag(Context c,String title,String desc,String action,final ClickCallBack click)
    {



        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void diag(Context c,String title,String desc,boolean isCancellable,String action,final ClickCallBack click)
    {



        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setCancelable(isCancellable);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    public static void diag(Context c,String title,String desc)
    {
        try {
            final AlertDialog.Builder
                    alertDialogBuilder = new AlertDialog.Builder
                    (c);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(Html.fromHtml(desc));
            alertDialogBuilder.setNeutralButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface
                                                    dialog, int id) {
                            dialog.cancel();
                        }
                    });


            AlertDialog alertDialog
                    = alertDialogBuilder.create();


            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void toast(Context c,String t) {


        try {
            if(DISPLAY_ENABLED)
                Toast.makeText(c, t, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static interface InputDialogCallback {
        public void onDone(String text);
    }

    public static  Integer TYPE_EMAIL=120,TYPE_PHONE=293,TYPE_DEF=101;
    public static EditText input  ;

    public static AlertDialog inputDialog(Context ctx,String title,String message,final int TYPE,final InputDialogCallback callback)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(ctx,  R.style.MyAlertDialogStyle));
        alert.setTitle(title);
        alert.setMessage(message);


        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d("", "Text Value : " + value);

                callback.onDone(value);
                return;
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                         return;
                    }
                });

        LayoutInflater lfl=new LayoutInflater(ctx) {
            @Override
            public LayoutInflater cloneInContext(Context context) {
                return null;
            }
        };
        View v=lfl.inflate(R.layout.input,null);
        alert.setView(v);


        AlertDialog diag= alert.create();
        diag.setCanceledOnTouchOutside(false);

        input=(EditText) v.findViewById(R.id.input);

        if(TYPE==TYPE_EMAIL)
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        if(TYPE==TYPE_PHONE)
            input.setInputType(InputType.TYPE_CLASS_PHONE);
        else
            input.setInputType(InputType.TYPE_CLASS_TEXT);


        diag.show();;
        return diag;

    }


    public static String getRealPathFromUri(Uri uri) {
        String result = null;
        try {
            if(uri==null)
                return null;

            String documentID;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String[] pathParts = uri.getPath().split("/");
                documentID = pathParts[pathParts.length - 1];
            } else {
                String pathSegments[] = uri.getLastPathSegment().split(":");
                documentID = pathSegments[pathSegments.length - 1];
            }
            String mediaPath = MediaStore.Images.Media.DATA;

            Cursor imageCursor = ctx.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
            if (imageCursor.moveToFirst()) {
                result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result==null)
            result=uri.getPath().replace("file://","").replace("%20"," ");
        utl.l("Found File path "+result);
        return result;
    }




    public static final String MY_PREFS_NAME = "scon";
    public static  SharedPreferences.Editor editor;// = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

    public static void setKey(String k,String v,Context ctx)
    {

        utl.l("KEY SET Key:  -"+k+ "- VAL: "+v);
        editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(k,v);

        editor.commit();

    }

    public static String getKey(String k,Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(k, null);

        utl.l("KEY GET : "+k+" VAL: "+restoredText);

        return restoredText;
    }

    public static void setShared(Context ctx)
    {
        /*
        editor.putString("name", "Elena");
        editor.putInt("idName", 12);
        editor.commit();*/


        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("installed", null);
        if (restoredText != null) {

            String name = prefs.getString("installed", "true");

            Log.d("INSTALL "," ALREADY INSTALL ");
        }
        else {
            Log.d("INSTALL "," FIRST INSTALL ");
            File folder=new File(Constants.getFolder());
            if(folder.exists())
            {
                utl.log("INSTALL :Deleting folder");
                utl.deleteDir(folder);
            }
            editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("installed", "true");

            editor.commit();
        }

    }


    public static void copyFile(File src,File dst)
    {
        try{

            if(!src.exists())
                return;
            InputStream in=new FileInputStream(src);
            OutputStream os=new FileOutputStream(dst);

            byte []buf=new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                os.write(buf,0,len);
            }
            in.close();
            os.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }


    public static  boolean writeData(String text)
    {
        String data= Constants.localDataFile();
        FileOperations fop=new FileOperations();
        Gson g=new Gson();
        fop.write(data,text);
        Log.d("DATA WROTE",""+fop.read(data));
        return  true;
    }




    public static String readData()
    {
        String data= Constants.localDataFile();
        if(!new File(data).exists())
            return null;
        FileOperations fop=new FileOperations();

        Log.d("DATA READ",""+fop.read(data));
        return  fop.read(data);



    }




    public static  boolean writeFile(String file,String text)
    {

        String data= Constants.folder+"/"+file;
        FileOperations fop=new FileOperations();
        Gson g=new Gson();
        fop.write(data,text);
        Log.d("DATA WROTE",""+fop.read(data));
        return  true;
    }

    public static String readFile(String file)
    {
        String data= Constants.folder+"/"+file;
        if(!new File(data).exists())
            return null;
        FileOperations fop=new FileOperations();

        Log.d("DATA READ",""+fop.read(data));
        return  fop.read(data);



    }

    public static void clearCache()
    {

        File cache=new File(Constants.getFolder());
        for(File f: cache.listFiles())
        {

            f.delete();

        }


        File folder=new File(Constants.getFolder());
        for(File f: folder.listFiles())
        {
            if(f.getName().contains(".mp4")||f.getName().contains(".png"))
            {
                f.delete();
            }
        }



    }



    public static  boolean removeData()
    {
        String data= Constants.localDataFile();
        File f=new File(data);
        f.delete();
        return  true;
    }

    public static  boolean removeUserData()
    {
        String data= Constants.userDataFile();
        File f=new File(data);
        f.delete();
        return  true;
    }

    public static  boolean writeUserData(GenricUser guser, Context ctx)
    {
        String data= Constants.userDataFile();
        FileOperations fop=new FileOperations();
        Gson g=new Gson();
        fop.write(data,g.toJson(guser));
        Log.d("DATA WROTE",""+fop.read(data));
        return  true;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }



    public static String uid(int l)
    {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("uuid Full= " + uuid);
        String ret= uuid.substring(0, Math.min(uuid.length(), l));;
        System.out.println("uuid "+l+" = " + ret);
        return ret;
    }



    public static int getRandomIntInRange(int max, int min){
        Random mRandom = new Random();

        return mRandom.nextInt((max-min)+min)+min;
    }


    public static GenricUser readUserData()
    {
        String data= Constants.userDataFile();
        if(!new File(data).exists())
            return null;
        FileOperations fop=new FileOperations();
        Gson g=new Gson();
        try {
            Log.d("DATA READ","");
            //Log.d("DATA READ",""+fop.read(data));
            GenricUser guser=g.fromJson(fop.read(data),GenricUser.class);
            return  guser;


        } catch (JsonSyntaxException e) {

            e.printStackTrace();

            return  null;
        }
    }



    public static Dialog dialog;
    public static void showDig(boolean show,Context ctx)
    {
        try {
            if(dialog!=null)
                if(dialog.isShowing()&&show)
                {
                    return;
                }
            if(show)
            {

                utl.log("DIAG_OPEN : "+ctx.getClass());
                dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.gen_load);
                final Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ctx,R.color.transblack2)));
                //dialog.getWindow().getAttributes().alpha = 0.7f;


                dialog.setContentView(R.layout.gen_load);
                //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                dialog.show();

                ImageView mIcDownloadAnimator = (ImageView) dialog.findViewById(R.id.loading);
                final Drawable drawable = mIcDownloadAnimator.getDrawable();

                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }


                Animation rotation = AnimationUtils.loadAnimation(ctx, R.anim.rot_3d);
                rotation.setFillAfter(true);
                mIcDownloadAnimator.startAnimation(rotation);

/*

                AVLoadingIndicatorView splashView=(AVLoadingIndicatorView)dialog.findViewById(R.id.splash_view2);
                splashView.show();
*/


            }
            else   {



                utl.log("DIAG_CLOSE: "+ctx.getClass());
                if(dialog!=null)
                    if(dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean saveVideoThumb(String filenameTarget,String video)
    {
        Bitmap bmp;

        bmp = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Video.Thumbnails.MINI_KIND);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filenameTarget);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        }


        finally {
            try {
                if (out != null) {
                    out.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return false;
        }


    }







}
