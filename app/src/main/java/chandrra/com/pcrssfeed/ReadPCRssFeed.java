package chandrra.com.pcrssfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by smallipeddi on 10/7/16.
 * Fetching details asynchronously.
 */

public class ReadPCRssFeed extends AsyncTask<Void, Void, Void> {
    Context context;
    String address = "https://blog.personalcapital.com/feed/?cat=3,891,890,68,284";
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    URL url;
    ArrayList<PCRssFeedItems> pcRssFeedItemses;

    public ReadPCRssFeed(Context context, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading RSS feed...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(false);
        progressDialog.show();


    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

        CustomAdapter adapter = new CustomAdapter(context, pcRssFeedItemses);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 50, true));
        GridLayoutManager gridLayoutManager;
        //Tablet
        MainActivityArticles mainActivityArticles = new MainActivityArticles();

        /**
         * Checking if device is a tablet and assigning span type for grids so that for
         * tablet 3 cells will be displayed
         */
        if (mainActivityArticles.isTablet(context)) {

            gridLayoutManager = new GridLayoutManager(context,3);
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
            gridLayoutManager = new GridLayoutManager(context,2);
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
    }

    @Override
    protected Void doInBackground(Void... params) {
        parseXml(getData());
        return null;
    }

    /**
     * Parsing xml feed data
     * @param data
     */
    private void parseXml(Document data) {
        if (data != null) {
            pcRssFeedItemses = new ArrayList<>();
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
                    pcRssFeedItemses.add(item);
                }
            }



        }

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

}
