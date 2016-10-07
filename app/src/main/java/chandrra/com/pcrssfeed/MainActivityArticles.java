package chandrra.com.pcrssfeed;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class MainActivityArticles extends AppCompatActivity {
    RecyclerView recyclerView;
    boolean tablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Checking if device is connected
         */
        if (isConnected(getApplicationContext())) {
            tablet = isTablet(getApplicationContext());
            setContentView(R.layout.activity_main_articles);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Articles from RSS Feed");

            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("clicked", "fab");

                    /**
                     * Implement Refresh here
                     */
                }
            });



            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            ReadPCRssFeed readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
            readPCRssFeed.execute();

        }
        else {
            showConnectionDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * To check if device is a Tablet.
     * @param context
     * @return
     */
    public boolean isTablet (Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            //return "Tablet";
            return true;
        }else{
            //return "Mobile";
            return false;
        }

    }

    /**
     * To check if device is connected to Internet
     * @param context
     * @return
     */
    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows dialog if not connected to internet
     */
    private void showConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Do you want to connect to WiFi?")
                .setTitle("No Internet access");

        // Add the buttons
        builder.setPositiveButton("Connect to WiFi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
            }
        });
        // Set other dialog properties
        //Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
