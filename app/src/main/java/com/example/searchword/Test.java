package com.example.searchword;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        new BackgroudTask().execute();
    }

    class BackgroudTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute(){
            target = "https://datalab.naver.com/keyword/realtimeList.naver?where=main";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try
            {
                Document doc = Jsoup.connect(target).get();
                // 전체연령대
                // ele.select("strong").text()
                // 검색어 20개
                //ele.select("ul[class=rank_list v2]").select("li")

                //Elements ele1 = doc.select("div[data-age=all]");
                Element ele = doc.selectFirst("div[data-age=all]");
                Elements eles = ele.select("ul[class=rank_list v2]").select("li");
                for (Element item : eles)
                {
                    String num = item.select("em[class=num]").text();
                    String title = item.select("span[class=title]").text();
                    Log.d("=================", num + ". "+title);
                }

                //Log.d("=================", ele.text());

                /*
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

                 */
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

            //Log.d("onPostExecute", result);
            //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //intent.putExtra("userList", result);
            //MainActivity.this.startActivity(intent);
        }
    }
}
