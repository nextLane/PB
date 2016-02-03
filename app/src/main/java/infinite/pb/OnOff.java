package infinite.pb;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.net.VpnService;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class OnOff extends AppCompatActivity {

    private Switch onoff;
    private MyProxyServer serverService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= new Intent(OnOff.this, MyProxyServer.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_on_off);


        onoff = (Switch) findViewById(R.id.switch1);



        //attach a listener to check for changes in state
        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                 //start service
                    Log.d("oal:::::","listener workin!");
                    startService(new Intent(getBaseContext(), MyProxyServer.class));
                    Log.d("oal:::::", "service started workin!");


                } else {
                    //stop service
                    stopService(new Intent(getBaseContext(), MyProxyServer.class));
                    Log.d("oal:::::", "vpn service not ready!");


                }

            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            MyProxyServer.MyBinder b = (MyProxyServer.MyBinder) binder;
            serverService = b.getService();
            Toast.makeText(OnOff.this, "Server set", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            serverService = null;
        }
    };

    public void viewData(View view)
    {
        Intent intent = new Intent(OnOff.this, ViewRecords.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on_off, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
