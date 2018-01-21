package in.hoptec.smartconnect;

import android.util.Log;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void dateFormatCorrect() throws Exception {
        assertEquals("2018/01/21", utl.getDate());
    }

    @Test
    public void stringRefiningCorrectly() throws Exception {

        assertEquals("test_case__", utl.refineString("test%case#$","_"));
    }



}