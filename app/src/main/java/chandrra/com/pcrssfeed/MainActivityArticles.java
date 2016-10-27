package chandrra.com.pcrssfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivityArticles extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context = this;
    ArrayList<PCRssFeedItems> pcRssFeedItems;
    CustomAdapter adapter;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_articles);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Articles from RSS Feed");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Implement Refresh here
                 */
                refreshRSSFeed();
                Log.d("clicked", "fab");
            }
        });

        /**
         * Checking if device is connected
         */
        if (isConnected(getApplicationContext())) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            ReadPCRssFeed readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
            readPCRssFeed.execute();

            try {
                pcRssFeedItems = readPCRssFeed.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            //CustomAdapter
            adapter = new CustomAdapter(context, pcRssFeedItems);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 50, true));
            GridLayoutManager gridLayoutManager;
            /**
             * Checking if device is a tablet and assigning span type for grids so that for
             * tablet 3 cells will be displayed
             */
            if (isTablet(getApplicationContext())) {
                gridLayoutManager = new GridLayoutManager(context, 3);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0) {
                            return 3;
                        } else {
                            return 1;
                        }
                    }
                });
            } else {
                gridLayoutManager = new GridLayoutManager(context, 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
            }

            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.addItemDecoration(new Spacing(20));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        } else {
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

        //Sometimes not stable
//        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
//            //return "Tablet";
//            return true;
//        }else{
//            //return "Mobile";
//            return false;
//        }

        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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

    /**
     *
     * Fetching details asynchronously.
     */
    public class ReadPCRssFeed extends AsyncTask<Void, Void, ArrayList<PCRssFeedItems>> {
        Context context;
        String address = "https://blog.personalcapital.com/feed/?cat=3,891,890,68,284";
        RecyclerView recyclerView;
        URL url;

        @Override
        protected void onCancelled(ArrayList<PCRssFeedItems> pcRssFeedItemses) {
            super.onCancelled(pcRssFeedItemses);
        }

        public ReadPCRssFeed(Context context, RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            this.context = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading RSS feed ...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected ArrayList doInBackground(Void... params) {
            pcRssFeedItems = parseXml(getData());
            return pcRssFeedItems;
        }

        /**
         * Parsing xml feed data
         * @param data
         */
        private ArrayList parseXml(Document data) {
            if (data != null) {
                pcRssFeedItems = new ArrayList<>();
                Element rootElement = data.getDocumentElement();
                Node channel = rootElement.getChildNodes().item(1);
                NodeList items = channel.getChildNodes();
                for (int i = 0; i < items.getLength(); i++) {
                    Node currentchild = items.item(i);
                    if (currentchild.getNodeName().equalsIgnoreCase("item")) {
                        PCRssFeedItems item = new PCRssFeedItems();
                        NodeList itemchilds = currentchild.getChildNodes();
                        for (int j = 0; j < itemchilds.getLength(); j++) {
                            Node current = itemchilds.item(j);
                            if (current.getNodeName().equalsIgnoreCase("title")) {
                                item.setTitle(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("description")) {
                                item.setDescription(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("link")) {
                                item.setLink(current.getTextContent());
                            } else if (current.getNodeName().equalsIgnoreCase("media:content")) {
                                String imageUrl = current.getAttributes().item(0).getTextContent();
                                item.setImageUrl(imageUrl);

                            }

                        }
                        pcRssFeedItems.add(item);
                    }
                }



            }
            return pcRssFeedItems;

        }

        /**
         * Getting document from URL
         * @return
         */
        public Document getData() {
            try {
                url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(inputStream);
                return document;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
        /**
         * RecyclerView item decoration - give equal margin around grid item
         */


        private ArrayList result(ArrayList myValue) {
            //handle value
            return myValue;
        }

    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
    }

    /**
     * Update/Refresh RssFeedContent
     */
    public void refreshRSSFeed() {
        ReadPCRssFeed readPCRssFeed; //= new ReadPCRssFeed(this, recyclerView);
        /**
         * Content can also be updated using notifyDataSetChanged
         */
        //        readPCRssFeed.execute();
//        ArrayList<PCRssFeedItems> pcRssFeedItemsRefresh = null;

//        try {
//            pcRssFeedItemsRefresh = readPCRssFeed.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//       recyclerView.invalidate();
//        recyclerView.removeAllViews();
        //pcRssFeedItems.remove(1);
//        pcRssFeedItems.clear();
//        pcRssFeedItems.addAll(pcRssFeedItemsRefresh);
//        adapter.notifyDataSetChanged();
//        progressDialog.dismiss();

        //Working solution by setting a new adapter
        /**
         * Updating Content
         */
        readPCRssFeed = new ReadPCRssFeed(this, recyclerView);
        readPCRssFeed.execute();
        try {
            pcRssFeedItems = readPCRssFeed.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //pcRssFeedItems.remove(1);

        adapter = new CustomAdapter(context, pcRssFeedItems);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();
    }

}
