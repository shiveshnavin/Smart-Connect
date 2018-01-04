package in.hoptec.smartconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ConnectToDevice extends AppCompatActivity {

    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_device);


        view=findViewById(R.id.content_home);







    }
}
