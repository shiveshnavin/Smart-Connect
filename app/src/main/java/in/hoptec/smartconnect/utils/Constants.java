package in.hoptec.smartconnect.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import in.hoptec.smartconnect.R;
import in.hoptec.smartconnect.utl;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {



    public static String HOST="http://192.168.23.3";


    public static String folder;
    public static String datafile;

    public static Context ctx;

    public static void init(Context context)
    {
        utl.init(ctx);
        ctx=context;
        File file=new File(getFolder());
        if(!file.exists())
        {
            file.mkdir();
        }
    }

    private static String FIRE_BASE="https://test-a0930.firebaseio.com/";

    public static String fireURL()
    {
        return Constants.FIRE_BASE+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }


    public static String getFolder()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");
        return folder;
    }


    public static String userDataFile()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/firebaseUser.json";
        return datafile;
    }



    public static String localDataFile()
    {
        folder = Environment.getExternalStorageDirectory().getPath().toString()+"/."+ utl.refineString(ctx.getResources().getString(R.string.app_name),"");

        File file=new File(folder);
        if(!file.exists())
        {
            file.mkdir();
        }
        datafile=folder+"/data.json";
        return datafile;
    }



    public static String getApp()
    {
        return utl.refineString(ctx.getResources().getString(R.string.app_name),"");
    }











}
