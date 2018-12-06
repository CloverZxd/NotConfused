package com.zxd.notconfused.ui.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.bumptech.glide.Glide;
import com.zxd.notconfused.R;
import com.zxd.notconfused.api.mIp;
import com.zxd.notconfused.utils.mUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements WeatherSearch.OnWeatherSearchListener {

    /**
     * 利用高德地图查询天气
     */
    WeatherSearchQuery mquery;

    WeatherSearch mweathersearch;

    LocalWeatherLive weatherlive;


    //返回
    @BindView(R.id.iv_back)
    ImageView iv_back;
    //title
    @BindView(R.id.title_city)
    TextView title_city;
    //实时天气发布时间
    @BindView(R.id.title_time)
    TextView title_time;
    //实时天气
    @BindView(R.id.tv_tq)
    TextView tv_tq;
    //实时温度
    @BindView(R.id.tv_wd)
    TextView tv_wd;
    //实时风力风力
    @BindView(R.id.tv_fl)
    TextView tv_fl;
    //实时风力风向
    @BindView(R.id.tv_fx)
    TextView tv_fx;
    //实时湿度
    @BindView(R.id.tv_sd)
    TextView tv_sd;

    //背景图
    @BindView(R.id.bing_pic_img)
    ImageView bingPicImg;
    private SharedPreferences prefs;

    //下拉刷新
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    String county = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将背景图和状态栏融合到一起
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        county =getIntent().getStringExtra("county");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ButterKnife.bind(this);
        //背景图先从缓存中直接读取上次缓存的，如果没有的话再在服务器中获取
        if(prefs.getString("bing_pic",null) != null){
            Glide.with(this).load(prefs.getString("bing_pic",null)).into(bingPicImg);
        }else{
            loadBingPic();
        }


        //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        mquery = new WeatherSearchQuery(county, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch=new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mweathersearch.searchWeatherAsyn(); //异步搜索
                //刷新天气的同时也刷新背景图
                loadBingPic();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //请求网络背景图
    private void loadBingPic() {

        mUtils.sendOkHttpRequest(mIp.BingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                prefs.edit().putString("bing_pic",bingPic).apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult ,int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null&&weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                title_city.setText(weatherlive.getCity());
                title_time.setText(weatherlive.getReportTime().substring(weatherlive.getReportTime().length()-9,weatherlive.getReportTime().length()-3)+"发布");
                tv_fl.setText(weatherlive.getWindPower());
                tv_sd.setText(weatherlive.getHumidity()+"%");
                tv_wd.setText(weatherlive.getTemperature()+"℃");
                tv_tq.setText(weatherlive.getWeather());
                tv_fx.setText(weatherlive.getWindDirection()+"风");
                swipeRefreshLayout.setRefreshing(false);
            }else {
                Toast.makeText(this,"请求失败",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }else {
            Toast.makeText(this,"请求失败"+rCode,Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rCode) {
        if (rCode == 1000) {
            String s = localWeatherForecastResult.getForecastResult().getCity();
            Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
            /* if (localWeatherForecastResult != null&&localWeatherForecastResult.getForecastResult() != null) {
             *//* weatherlive = weatherLiveResult.getLiveResult();
                Toast.makeText(WeatherActivity.this,weatherlive.getReportTime()+"发布"+
                        "............."+weatherlive.getWeather()+"......."+weatherlive.getTemperature()+"°"+
                        "............"+weatherlive.getWindDirection()+"风     "+weatherlive.getWindPower()+"级"+
                        ".............."+"湿度         "+weatherlive.getHumidity()+"%",Toast.LENGTH_LONG).show();*//*
             *//*reporttime1.setText(weatherlive.getReportTime()+"发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature()+"°");
                wind.setText(weatherlive.getWindDirection()+"风     "+weatherlive.getWindPower()+"级");
                humidity.setText("湿度         "+weatherlive.getHumidity()+"%");*//*
            }else {
                Toast.makeText(this,"请求失败",Toast.LENGTH_SHORT).show();
            }*/
        }else {
            Toast.makeText(this,"请求失败"+rCode,Toast.LENGTH_SHORT).show();
        }
    }
}
