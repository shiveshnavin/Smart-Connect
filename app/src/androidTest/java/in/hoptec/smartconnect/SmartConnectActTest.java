package in.hoptec.smartconnect;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)

public class SmartConnectActTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("in.hoptec.smartconnect", appContext.getPackageName());
    }


    @Rule
    public ActivityTestRule<SmartConnectActivity> rule  = new ActivityTestRule<>(SmartConnectActivity.class);


    public JSONObject getNormalResponse() throws Exception
    {
        JSONObject job=new JSONObject();
        job.put("free_ram",300);
        job.put("uptime",183);
        job.put("status","2000");
        job.put("serial","$0300,0180,0000,0001,0001,1000,0050,0040,0020~");


        return job ;

    }


    @Test
    public void checkIfParsingDoneRight() throws  Exception{


        SmartConnectActivity smartConnectActivity=rule.getActivity();
        smartConnectActivity.isAppInDisconnectionMode=true;

        //Parsing Normal Data

        TextView  tdsIn, tdsOut, totalFlowValue;
        tdsIn =(TextView) smartConnectActivity.findViewById(R.id.tds_in);
        tdsOut =(TextView) smartConnectActivity.findViewById(R.id.tds_out);
        totalFlowValue =(TextView) smartConnectActivity.findViewById(R.id.tf_ic);

        smartConnectActivity.parse(getNormalResponse());


        assertEquals("TDS Test Passed . Actual : "+tdsIn.getText().toString(),tdsIn.getText().toString(),"0000");









    }










}
