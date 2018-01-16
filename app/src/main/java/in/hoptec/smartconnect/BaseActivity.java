package in.hoptec.smartconnect;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by shivesh on 7/1/18.
 */

public class BaseActivity  extends AppCompatActivity {




    public String getstring(@StringRes int res)
    {
         return getResources().getString(res);

    }








}
