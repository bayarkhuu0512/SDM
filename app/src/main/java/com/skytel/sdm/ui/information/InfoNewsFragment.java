package com.skytel.sdm.ui.information;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skytel.sdm.InfoNewsDetailActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.HandsetChangeTypeAdapter;
import com.skytel.sdm.adapter.InfoNewsTypeAdapter;
import com.skytel.sdm.adapter.NewsListAdapter;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.entities.HandsetChangeType;
import com.skytel.sdm.entities.InfoNewsType;
import com.skytel.sdm.entities.NewsListItem;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Altanchimeg on 7/18/2016.
 */

public class InfoNewsFragment extends Fragment implements Constants {
    String TAG = InfoNewsFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private CustomProgressDialog mProgressDialog;

    private ListView mInfoNewsTypeListview;
    private ListView mNewsListview;
    private InfoNewsTypeAdapter mInfoNewsAdapter;
    private NewsListAdapter mNewsListAdapter;
    private ArrayList<InfoNewsType> mInfoNewsTypeArrayList = new ArrayList<InfoNewsType>();
    private ArrayList<NewsListItem> mNewsListArrayList = new ArrayList<NewsListItem>();

    public InfoNewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_news, container, false);
        mContext = getActivity();
        mClient = HttpClient.getInstance();

        mProgressDialog = new CustomProgressDialog(getActivity());

        mNewsListview = (ListView) rootView.findViewById(R.id.news_list_view);
        mNewsListAdapter = new NewsListAdapter(getActivity(), mNewsListArrayList);
        mNewsListview.setAdapter(mNewsListAdapter);


        mInfoNewsTypeListview = (ListView) rootView.findViewById(R.id.info_type_list_view);
        mInfoNewsAdapter = new InfoNewsTypeAdapter(getActivity(), mInfoNewsTypeArrayList);
        mInfoNewsTypeListview.setAdapter(mInfoNewsAdapter);
        mInfoNewsTypeListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mInfoNewsAdapter.setCurrentCategoryPos(position);
                Log.d(TAG, "type position: " + position);
                Log.d(TAG, "category id: " + mInfoNewsTypeArrayList.get(position).getCategoryId());
                try {
                    runGetInfoNewsList(mInfoNewsTypeArrayList.get(position).getCategoryId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            runGetInfoNewsList("0");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void runGetInfoNewsList(final String categoryTypeId) throws Exception {
        mProgressDialog.show();

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_SKYTEL_MN_URL);
        url.append(Constants.FUNCTION_GET_INFO_NEWS_TYPE);
        url.append("?category=" + categoryTypeId);

        Log.d(TAG, "URL: " + url.toString());

        Request request = new Request.Builder()
                .url(url.toString())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
                                             @Override
                                             public void onFailure(Call call, IOException e) {
                                                 mProgressDialog.dismiss();
                                                 System.out.println("onFailure");
                                                 e.printStackTrace();
                                                 getActivity().runOnUiThread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                                                         // Used for debug
                                                     }
                                                 });
                                             }

                                             @Override
                                             public void onResponse(Call call, Response response) throws IOException {
                                                 mProgressDialog.dismiss();

                                                 System.out.println("onResponse");

                                                 if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

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

                                                     getActivity().runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
                                                         }
                                                     });

                                                     Log.d(TAG, "error_code: " + error_code);
                                                     Log.d(TAG, "message: " + message);
                                                     JSONObject jsonObjCategory = jsonObj.getJSONObject("result");
                                                     if (categoryTypeId == "0") {

                                                         JSONArray jArray = jsonObjCategory.getJSONArray("categoryList");
                                                         Log.d(TAG, "*****JARRAY CategoryList*****" + jArray.length());
                                                         mInfoNewsTypeArrayList.clear();
                                                         for (int i = 0; i < jArray.length(); i++) {
                                                             JSONObject jsonData = jArray.getJSONObject(i);

                                                             String categoryId = jsonData.getString("id");
                                                             String name = jsonData.getString("name");

                                                             Log.d(TAG, "INDEX:       " + i);
                                                             Log.d(TAG, "categoryId: " + categoryId);
                                                             Log.d(TAG, "name: " + name);

                                                             InfoNewsType infoNewsType = new InfoNewsType();
                                                             // DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                                             infoNewsType.setId(i);
                                                             infoNewsType.setCategoryId(categoryId);
                                                             infoNewsType.setName(name);

                                                             mInfoNewsTypeArrayList.add(i, infoNewsType);
                                                         }


                                                         getActivity().runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 Log.d(TAG, "mInfoNewsTypeArrayList size: " + mInfoNewsTypeArrayList.size());
                                                                 mInfoNewsAdapter.setCurrentCategoryPos(0);
                                                                 mInfoNewsAdapter.notifyDataSetChanged();
                                                                 try {
                                                                     runGetInfoNewsList(mInfoNewsTypeArrayList.get(0).getCategoryId());
                                                                 } catch (Exception e) {
                                                                     e.printStackTrace();
                                                                 }
                                                             }
                                                         });

                                                     } else {

                                                         JSONArray jArrayNewsList = jsonObjCategory.getJSONArray("newsList");
                                                         Log.d(TAG, "*****JARRAY NewsList*****" + jArrayNewsList.length());
                                                         mNewsListArrayList.clear();
                                                         for (int i = 0; i < jArrayNewsList.length(); i++) {
                                                             JSONObject jsonData = jArrayNewsList.getJSONObject(i);

                                                             int newsListItemId = jsonData.getInt("id");
                                                             String title = jsonData.getString("title");
                                                             String intro = jsonData.getString("intro");
                                                             String image = jsonData.getString("image");
                                                             String creatAt = jsonData.getString("creatAt");

                                                             Log.d(TAG, "INDEX:       " + i);

                                                             Log.d(TAG, "newsListItemId: " + newsListItemId);
                                                             Log.d(TAG, "title: " + title);
                                                             Log.d(TAG, "intro: " + intro);
                                                             Log.d(TAG, "image: " + image);
                                                             Log.d(TAG, "creatAt: " + creatAt);

                                                             NewsListItem newsListItem = new NewsListItem();

                                                             newsListItem.setId(i);
                                                             newsListItem.setNewsListItemId(newsListItemId);
                                                             newsListItem.setTitle(title);
                                                             newsListItem.setIntro(intro);
                                                             newsListItem.setImage(image);
                                                             newsListItem.setCreatedDate(creatAt);

                                                             mNewsListArrayList.add(i, newsListItem);
                                                         }

                                                         getActivity().runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 mInfoNewsAdapter.notifyDataSetChanged();

                                                                 mNewsListAdapter.refresh(mNewsListArrayList);
                                                                 mNewsListAdapter.notifyDataSetChanged();
                                                                 mNewsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                     @Override
                                                                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                         Log.d(TAG, "News ID: "+mNewsListArrayList.get(position).getNewsListItemId());
                                                                         Intent intent = new Intent(mContext, InfoNewsDetailActivity.class);
                                                                         intent.putExtra("news_id", mNewsListArrayList.get(position).getNewsListItemId());
                                                                         startActivity(intent);
                                                                     }
                                                                 });
                                                             }
                                                         });
                                                     }


                                                 } catch (JSONException e) {
                                                     getActivity().runOnUiThread(new Runnable() {
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


}