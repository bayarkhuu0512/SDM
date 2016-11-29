package com.skytel.sdp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdp.network.HttpClient;
import com.skytel.sdp.utils.Constants;
import com.skytel.sdp.utils.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoNewsDetailActivity extends AppCompatActivity implements Constants {
    String TAG = InfoNewsDetailActivity.class.getName();

    private int mNewsId;

    private OkHttpClient mClient;
    private Context mContext;
    private CustomProgressDialog mProgressDialog;

    private TextView mContentTitle;
    private ImageView mContentImage;
    private TextView mContentBodyText;

    private WebView content_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mClient = HttpClient.getInstance();
        mProgressDialog = new CustomProgressDialog(this);
        setContentView(R.layout.activity_info_news_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mNewsId = extras.getInt("news_id");
            Log.d(TAG, "News ID: "+mNewsId);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mContentTitle = (TextView) findViewById(R.id.content_title);
        mContentImage = (ImageView) findViewById(R.id.content_image);
       // mContentBodyText = (TextView) findViewById(R.id.content_body);

        content_body = (WebView) findViewById(R.id.content_body_web);

        try {
            mProgressDialog.show();
            runGetInfoNewsDetail(mNewsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void runGetInfoNewsDetail(int news_id) throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_SKYTEL_MN_URL);
        url.append(Constants.FUNCTION_GETINFO_NEWS_DETAIL_PART1);
        url.append(news_id);
        url.append(Constants.FUNCTION_GETINFO_NEWS_DETAIL_PART2);

        Log.d(TAG, "send URL: "+url.toString());
        Request request = new Request.Builder()
                .url(url.toString())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
                                             @Override
                                             public void onFailure(Call call, IOException e) {
                                                 mProgressDialog.dismiss();
                                                 System.out.println("onFailure");
                                                 e.printStackTrace();
                                                 runOnUiThread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                                                     }
                                                 });
                                             }

                                             @Override
                                             public void onResponse(Call call, Response response) throws IOException {
                                                 mProgressDialog.dismiss();

                                                 System.out.println("onResponse");

                                                 if (!response.isSuccessful())
                                                     throw new IOException("Unexpected code " + response);

                                                 Headers responseHeaders = response.headers();
                                                 for (int i = 0; i < responseHeaders.size(); i++) {
                                                     System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                                                 }

                                                 String resp = response.body().string();
                                                 System.out.println("resp " + resp);

                                                 try {

                                                     JSONObject jsonObj = new JSONObject(resp);
                                                     int error_code = jsonObj.getInt("error_code");
                                                     final String message = jsonObj.getString("message");


                                                     Log.d(TAG, "error_code: " + error_code);
                                                     Log.d(TAG, "message: " + message);
                                                     runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {

                                                             Toast.makeText(mContext, ""+ message, Toast.LENGTH_LONG).show();
                                                             // Used for debug
                                                         }
                                                     });

                                                     JSONObject jsonObjCategory = jsonObj.getJSONObject("result");

                                                     JSONObject jObjNews = jsonObjCategory.getJSONObject("content");

                                                     int newsId = jObjNews.getInt("id");
                                                     final String title = jObjNews.getString("title");
                                                     String intro = jObjNews.getString("intro");
                                                     final String image = jObjNews.getString("image");
                                                     String createdAt = jObjNews.getString("createdAt");
                                                     final String text = jObjNews.getString("text");
                                                     String description = jObjNews.getString("description");


                                                     Log.d(TAG, "newsId: " + newsId);
                                                     Log.d(TAG, "title: " + title);
                                                     Log.d(TAG, "intro: " + intro);
                                                     Log.d(TAG, "image: " + image);
                                                     Log.d(TAG, "createdAt: " + createdAt);
                                                     Log.d(TAG, "text: " + text);
                                                     Log.d(TAG, "description: " + description);

                                                     runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             mContentTitle.setText(title);
                                                             Picasso.with(mContext).load(image).into(mContentImage);

                                                             String htmlBody = text.toString().replaceAll("(<(/)img>)|(<img.+?>)", "");


                                                             Spanned htmlSpanned = Html.fromHtml(text, new ImageGetter(mContext), null);

                                                             //mContentBodyText.setText(htmlBody);


                                                             content_body.loadDataWithBaseURL(null, htmlSpanned.toString(), "text/html", "utf-8", null);
                                                             //mContentBodyText.setText(content_body.);*/

                                                         }
                                                     });
                                                 } catch (JSONException e) {
                                                     runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             Toast.makeText(mContext, "Алдаатай хариу ирлээ", Toast.LENGTH_LONG).show();
                                                         }
                                                     });
                                                     e.printStackTrace();

                                                 }
                                             }
                                         }

        );
    }
    public static CharSequence trim(CharSequence s, int start, int end) {
        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }

        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }

        return s.subSequence(start, end);
    }
}


class ImageGetter implements Html.ImageGetter {
    Context context;

    public ImageGetter(Context context) {
        this.context = context;
    }

    public Drawable getDrawable(String source) {
        return context.getResources().getDrawable(R.drawable.skytel_logo);

/*
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL("http://www.skytel.mn/" + source).getContent());
            return new BitmapDrawable(bitmap);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
*/
/*        Bitmap myBitmap = null;
        try {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        URL url = new URL("http://www.skytel.mn/uploads/images/6.jpg");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            //  return myBitmap;
            return new BitmapDrawable(myBitmap);*/

/*
        } catch (Exception e) {
            e.printStackTrace();
*/
        //    return null;
/*
            return context.getResources().getDrawable(R.drawable.skytel_logo);
        }
*/

        //        Log.d("ImageGetter", "Source " + source);
        //   ImageView imageView = new ImageView(context);
        //Picasso.with(context).load("https://www.skytel.mn/uploads/images/6.jpg").into(imageView);
        //    Picasso.with(context).load("https://www.skytel.mn/uploads/images/6.jpg").into(target);

        //   Drawable d = imageView.getDrawable();
        //Drawable d = context.getResources().getDrawable(R.drawable.skytel_logo);
        //  d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        //   return d;
       /*
        int id;


        id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());

        if (id == 0) {
            // the drawable resource wasn't found in our package, maybe it is a stock android drawable?
            id = context.getResources().getIdentifier(source, "drawable", "android");
        }

        if (id == 0) {
            // prevent a crash if the resource still can't be found
            return null;
        } else {
            Drawable d = context.getResources().getDrawable(R.drawable.skytel_logo);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }*/
   /* }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            imageView.setImageBitmap(bitmap);
            //          Drawable image = imageView.getDrawable();

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
*/
    }
}

