package chandrra.com.pcrssfeed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by smallipeddi on 10/7/16.
 *
 * Custom RecyclerView Adapter
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER_ARTICLE = 0;
    public static final int GROUP_ARTICLES = 1;
    ArrayList<PCRssFeedItems> feedItems = null;
    Context context;
    ProgressBar progressBar;
    public CustomAdapter(Context context, ArrayList<PCRssFeedItems> feedItems) {
        this.feedItems = feedItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case GROUP_ARTICLES:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_article_item, parent, false);
                return new GroupViewHolder(view);
            case HEADER_ARTICLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_article_first, parent, false);
                return new FirstHeaderArticleViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PCRssFeedItems current = feedItems.get(position);
        if (getItemCount() > 0) {
            if (current != null) {
                switch (getItemViewType(position)) {

                    case GROUP_ARTICLES:
                        ((GroupViewHolder)holder).image.setImageBitmap(null);
                        ((GroupViewHolder)holder).title.setText(current.getTitle());
                        //Custom Image Loader
//                        new DownloadImageTask(((GroupViewHolder)holder).image)
//                                .execute(current.getImageUrl());
                        //Image Loading with Glide
                        Glide.with(context).load(current.getImageUrl()).into(((GroupViewHolder) holder).image);

                        ((GroupViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ArticleWebView.class);
                                intent.putExtra("link", current.getLink());
                                intent.putExtra("title", current.getTitle());
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case HEADER_ARTICLE:
                        ((FirstHeaderArticleViewHolder)holder).image.setImageBitmap(null);

                        ((FirstHeaderArticleViewHolder)holder).title.setText(current.getTitle());
                        //Custom Image Loader
//                        new DownloadImageTask(((FirstHeaderArticleViewHolder)holder).image)
//                                .execute(current.getImageUrl());
                        //Image Loading with Picasso
                        //Picasso.with(context).load(current.getImageUrl()).into(((FirstHeaderArticleViewHolder)holder).image);
                        //Image Loading with Glide
                        Glide.with(context).load(current.getImageUrl()).into(((FirstHeaderArticleViewHolder)holder).image);
                        String desciptionPubDate = current.getPubDate().substring(0, 17) + " - " + current.getDescription().replaceAll("\\<.*?>","");
                        ((FirstHeaderArticleViewHolder)holder).descriptionPubDate.setText(desciptionPubDate);
                        ((FirstHeaderArticleViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, ArticleWebView.class);
                                intent.putExtra("link", current.getLink());
                                intent.putExtra("title", current.getTitle());
                                context.startActivity(intent);
                            }
                        });
                        break;

                }
            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_ARTICLE;
        } else {
            return GROUP_ARTICLES;
        }
    }

    @Override
    public int getItemCount() {
        if (feedItems != null) {
            return feedItems.size();
        } else {
            return 0;
        }

    }
    /**
     * Custom View Holder for Second Article
     */


    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        CardView cardView;
        public GroupViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.image_article);
            title = (TextView) itemView.findViewById(R.id.title_article);
            cardView = (CardView) itemView.findViewById(R.id.cardview_articles);
        }
    }
    /**
     * Custom View Holder for First Article
     */
    public class FirstHeaderArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, descriptionPubDate;
        ImageView image;
        CardView cardView;

        public FirstHeaderArticleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_article_first_item);
            descriptionPubDate = (TextView) itemView.findViewById(R.id.description_pubDate_article_first_item);
            image = (ImageView) itemView.findViewById(R.id.image_article_first_item);
            if (new MainActivityArticles().isTablet(context)) {
                image.getLayoutParams().height = dpTopxs(400);
            } else {
                image.getLayoutParams().height = dpTopxs(200);
            }
            cardView = (CardView) itemView.findViewById(R.id.cardview_article_first_item);
        }
    }

    /**
     * Downloading Images in Async Task
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        //ProgressBar imgLoadingIndicator;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //imgLoadingIndicator.getProgressDrawable();

        }

        protected void onPostExecute(Bitmap result) {
            //imgLoadingIndicator.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    public int dpTopxs(int dps) {
        //final float scale = getContext().getResources().getDisplayMetrics().density;
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }
}