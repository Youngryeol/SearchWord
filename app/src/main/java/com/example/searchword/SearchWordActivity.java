package com.example.searchword;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchWordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    //private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchword_list);

        Log.d("test", "============================begin");

        this.recyclerView = findViewById(R.id.my_recycler_view);
        this.recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);

        //this.requestQueue = Volley.newRequestQueue(this);

        new BackgroundTask().execute();

        refreshView = findViewById(R.id.swipe);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new BackgroundTask().execute();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String>{

        String target;
        String target_NewsSearch;
        List<NewsData> news = new ArrayList<>();

        @Override
        protected void onPreExecute(){
            target = "https://datalab.naver.com/keyword/realtimeList.naver?where=main";
            //target_NewsSearch = "https://search.naver.com/search.naver?where=news&sm=tab_jum&query=";
            target_NewsSearch = "https://search.naver.com/search.naver?where=image&sm=tab_jum&query=";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try
            {
               news = new ArrayList<>();

                Document doc = Jsoup.connect(target).get();
                // 전체연령대
                // ele.select("strong").text()
                // 검색어 20개
                //ele.select("ul[class=rank_list v2]").select("li")
                // 뉴스 검색 링크
                // https://search.naver.com/search.naver?where=news&sm=tab_jum&query=페이북 첫 결제
                // 뉴스 제목 클래스
                //  a[class=_sp_each_title]


                //Elements ele1 = doc.select("div[data-age=all]");
                Element ele = doc.selectFirst("div[data-age=all]");
                Elements eles = ele.select("ul[class=rank_list v2]").select("li");
                for (Element item : eles)
                {
                    String num = item.select("em[class=num]").text();
                    String title = item.select("span[class=title]").text();
                    Log.d("=================", num + ". "+title);

                    NewsData newsData = new NewsData();
                    newsData.setTitle(num + ". "+title);
                    //newsData.setUrlToImage(obj.getString("urlToImage"));
                    //newsData.setDescription(obj.getString("description"));

                    Document doc_NewsSearch = Jsoup.connect(target_NewsSearch + title).get();


                    if (doc_NewsSearch != null) {
                        //Element ele_NewsSearch_Title = doc_NewsSearch.selectFirst("a[class=_sp_each_title]");
                        Element _ell = doc_NewsSearch.selectFirst("div[class=img_area _item]");
                        if (_ell != null) {
                            Element _ell_a = _ell.selectFirst("a[class=thumb _thumb]");
                            if (_ell_a != null) {
                                newsData.setDescription(_ell_a.attr("title"));

                                Element _ell_img = _ell_a.selectFirst("img[class=_img]");
                                if (_ell_img !=null) {
                                    newsData.setUrlToImage(_ell_img.attr("data-source"));
                                }
                            }

                            Element _ell_link = _ell.selectFirst("a[class=org_view spimg]");
                            if (_ell_link != null) {
                                newsData.setHttpUrl(_ell_link.attr("href"));
                            }
                        }
                    }

                    news.add(newsData);
                }

                Log.d("=================", Integer.toString(news.size()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public  void onProgressUpdate(Void...values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(String result){

            // specify an adapter (see also next example)
            adapter = new MyAdapter(news, SearchWordActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (view.getTag() != null)
                    {
                        Object obj = view.getTag();
                        int position = (int) obj;

                        Intent intent = new Intent(SearchWordActivity.this, WebsiteActivity.class);
                        intent.putExtra("news", ((MyAdapter)adapter).getNews(position));
                        startActivity(intent);
                    }
                }
            });

            recyclerView.setAdapter(adapter);

            //Log.d("onPostExecute", result);
            //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //intent.putExtra("userList", result);
            //MainActivity.this.startActivity(intent);

            refreshView.setRefreshing(false);
        }
    }

}
