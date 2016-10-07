package chandrra.com.pcrssfeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by smallipeddi on 10/7/16.
 *
 * WebView implementation
 * To Load articles when they are clicked.
 */

public class ArticleWebView extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_view);
        webView = (WebView) findViewById(R.id.webview);
        Bundle bundle = getIntent().getExtras();
        String link = bundle.getString("link") + "?displayMobileNavigation=0";
        Log.d("link from web","" +link);
        webView.loadUrl(link);


        Log.d("link from web","" +link);

        this.webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Log.d("link from url","" +url);
                view.loadUrl(url);
                return true;
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(bundle.getString("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Enabling back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    public boolean onOptionsItemSelected(MenuItem item){
        this.finish(); //Finish activity when back button is clicked
        return true;
    }
}
