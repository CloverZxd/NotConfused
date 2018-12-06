package com.zxd.notconfused.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.bumptech.glide.Glide;
import com.xuezj.cardbanner.CardBanner;
import com.xuezj.cardbanner.ImageData;
import com.xuezj.cardbanner.adapter.BannerAdapter;
import com.xuezj.cardbanner.adapter.BannerViewHolder;
import com.zxd.notconfused.R;
import com.zxd.notconfused.adapter.homeGridViewAdpter;
import com.zxd.notconfused.adapter.homeViewPagerAdapter;
import com.zxd.notconfused.bean.a0;
import com.zxd.notconfused.ui.activity.MapActivity;
import com.zxd.notconfused.ui.activity.WeatherActivity;
import com.zxd.notconfused.ui.views.bannerImageView;
import com.zxd.notconfused.utils.mUtils;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomePageFragment extends Fragment implements View.OnClickListener ,EasyPermissions.PermissionCallbacks {

    /**
     * 所需权限
     */
    public static final int ACCESS_FINE_LOCATION=100;

    //主View
    private  View view;


    //侧滑菜单
    DrawerLayout drawer;

    NavigationView navigationView;
    //====================获取地理位置信息=======================

    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    /**
     * 利用高德地图查询天气
     */
    WeatherSearchQuery mquery;

    WeatherSearch mweathersearch;
    //获取实时天气
    LocalWeatherLive weatherlive;
    //获取未来天气
    List<LocalDayWeatherForecast> mDayWeatherList;

    String county = "";

    //====================轮播图部分=============================
    private CardBanner banner;
    //轮播图展示的数据集合
    private List<ImageData> imageData;
    private ArrayList<String> image;



    //====================菜单栏部分=============================
    //可滑动的viewpager
    private ViewPager vp;

    //展示菜单列表的集合
    private List<a0> menuList = new ArrayList<>();

    //gridView中展示控件的集合
    private List<View> viewGridViewList = new ArrayList<>();

    //将gridview作为子控件view的集合
    private List<View> viewPagerList = new ArrayList<>();


    //几屏幕能展示完全部控件  总页数
    int pageNo = 0;

    //展示跟随viewPager滑动时的圆点的布局
    private LinearLayout points;

    //小圆点图片的集合
    private ImageView[] ivPoints;

    Toolbar toolbar;
    //展示定位信息的布局
    LinearLayout ll_location;
    TextView tv_location;

    //天气显示部分
    RelativeLayout rl_tq;
    //温度
    TextView tv_wd;
    //天气描述
    TextView tv_tq;
    //天气图标
    ImageView iv_tq;

    //获取天气信息

    String wd;
    String tq;



    private Handler mHandler;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage,container,false);
        toolbar=view.findViewById(R.id.toolbar);

        Toast.makeText(getActivity(),"你好123",Toast.LENGTH_SHORT).show();
        //fragment声明
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //隐藏自带的项目名称
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);


      /*  drawer = view.findViewById(R.id.drawer_layout);

        navigationView = view.findViewById(R.id.nav_view);*/
        //判断权限是否加入
        checkPerm();
        initResources();//获取资源文件，动态添加列表数据
        initBanner();
        initView();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){

                    case 0:
                        //更新天气信息
                        tv_wd.setText(wd+"℃");
                        tv_tq.setText(tq);
                        iv_tq.setImageResource(mUtils.getWeatherImage(tq));
                        break;
                }
            }
        };
        return view;
    }


    private void getCurrentLocationLatLng() {
        //展示定位信息
        ll_location = view.findViewById(R.id.ll_location);
        tv_location = view.findViewById(R.id.tv_location);
        //默认显示定位中...
        tv_location.setText("正在定位...");
        ll_location.setOnClickListener(this);

        //展示天气部分数据
        tv_wd = view.findViewById(R.id.tv_wd);
        tv_tq = view.findViewById(R.id.tv_tq);
        iv_tq = view.findViewById(R.id.iv_tq);

        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        mLocationOption.setInterval(10000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private void initBanner() {
        banner = view.findViewById(R.id.banner);

        image = new ArrayList<>();
        image.add("http://ww1.sinaimg.cn/large/610dc034ly1fhyeyv5qwkj20u00u0q56.jpg");
        image.add("https://ws1.sinaimg.cn/large/610dc034gy1fhvf13o2eoj20u011hjx6.jpg");
        image.add("http://ww1.sinaimg.cn/large/610dc034ly1fhxe0hfzr0j20u011in1q.jpg");
        banner.setBannerAdapter(new BannerAdapter() {
            @Override
            public int getCount() {
                return image.size();
            }

            @Override
            public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder holder = new ViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.banner_item_homepage,parent,false));
                return holder;
            }

            @Override
            public void onBindViewHolder(BannerViewHolder holder, int position) {
                ViewHolder viewHolder= (ViewHolder) holder;
                Glide.with(getActivity())
                        .load(image.get(position))
                        .into(viewHolder.bannerImageView);
            }
        });

        banner.start();
        banner.setOnItemClickListener(new CardBanner.OnItemClickListener() {
            @Override
            public void onItem(int position) {
                Toast.makeText(getActivity(), "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initView() {
        //初始化控件
        vp=view.findViewById(R.id.viewpager);
        points=view.findViewById(R.id.points);
        rl_tq = view.findViewById(R.id.rl_tq);
        rl_tq.setOnClickListener(this);
        for(a0 a0 : menuList){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_homegirdview,
                    null);
            ((ImageView)view.findViewById(R.id.iamgeview)).setImageResource(a0.getImagUrl());
            ((TextView)view.findViewById(R.id.textview)).setText(a0.getImagName());
            viewGridViewList.add(view);
        }

        if(viewGridViewList.size() == 0){
            return;
        }

        //用屏幕的总宽度除以控件的宽度 计算出一行能放几个控件
        int oneWN =displayWidth() / viewWidth(viewGridViewList.get(0));
        //一页最多能放多少控件
        int maxpageNo = oneWN * 2;

        //用总控件个数除以一屏可放控件的总个数求余数
        if(viewGridViewList.size() % maxpageNo ==0){ //求余  余数为0表示正好几屏幕刚好放完
            pageNo = viewGridViewList.size() / maxpageNo;
        }else{
            pageNo = (viewGridViewList.size()/ maxpageNo) +1;//如果不能整除的话，那就再多加一页用于展示多余的
        }

        for( int i=0;i<pageNo;i++){
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(getActivity(),
                    R.layout.gridview_viewpager, null);
            gridView.setNumColumns(oneWN);
            gridView.setAdapter(new homeGridViewAdpter(getActivity(),menuList,i,maxpageNo));
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Object obj = gridView.getItemAtPosition(i);
                    if(obj != null && obj instanceof a0){
                        System.out.println(obj);
                        Toast.makeText(getActivity(), ((a0)obj).getImagName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewPagerList.add(gridView);
        }

        vp.setAdapter(new homeViewPagerAdapter(viewPagerList));



        //动态添加下面的导航图标(小圆点)  几屏就有几个导航图标
        ivPoints = new ImageView[pageNo];
        for (int i=0;i<pageNo;i++){
            //循环加入圆点图片组
            ivPoints[i] = new ImageView(getActivity());
            if(i==0){//第一次初始化时默认第一个页面圆点为选中状态
                ivPoints[i].setImageResource(R.drawable.check);
            }else{
                ivPoints[i].setImageResource(R.drawable.nocheck);
            }
            ivPoints[i].setPadding(3,6,3,5);
            points.addView(ivPoints[i]);
        }
        //设置ViewPager的滑动监听，主要是设置点点的背景颜色的改变
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                for (int i=0;i<pageNo;i++){
                    if(i == position){//滑动过程中让当前页圆点为选中状态
                        ivPoints[i].setImageResource(R.drawable.check);
                    }else{
                        ivPoints[i].setImageResource(R.drawable.nocheck);
                    }
                }
            }
        });

    }



    //获取手机屏幕宽度
    public int displayWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;     // 屏幕宽度（像素）
    }

    //获取控件的宽度
    public int viewWidth(View v) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(width, height);
        return v.getMeasuredWidth();
    }



    private void initResources() {

        for(int i=0;i<14;i++){
            a0 a0=new a0();
            a0.setImagName("测试"+i+">>");
            if(i==0){
                a0.setImagUrl(R.drawable.cc1);
            }
            if(i==1){
                a0.setImagUrl(R.drawable.cc2);
            }
            if(i==2){
                a0.setImagUrl(R.drawable.cc3);
            }
            if(i==3){
                a0.setImagUrl(R.drawable.cc4);
            }
            if(i==4){
                a0.setImagUrl(R.drawable.cc5);
            }
            if(i==5){
                a0.setImagUrl(R.drawable.cc6);
            }
            if(i==6){
                a0.setImagUrl(R.drawable.cc7);
            }
            if(i==7){
                a0.setImagUrl(R.drawable.cc8);
            }
            if(i==8){
                a0.setImagUrl(R.drawable.cc9);
            }
            if(i==9){
                a0.setImagUrl(R.drawable.cc10);
            }
            if(i==10){
                a0.setImagUrl(R.drawable.cc11);
            }
            if(i==11){
                a0.setImagUrl(R.drawable.cc12);
            }
            if(i==12){
                a0.setImagUrl(R.drawable.cc13);
            }
            if(i==13){
                a0.setImagUrl(R.drawable.cc14);
            }
            menuList.add(a0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
        if(mLocationClient!=null) {
            mLocationClient.startLocation(); // 启动定位
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationClient!=null) {
            mLocationClient.stopLocation();//停止定位
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLocationClient!=null) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.ll_location:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
            case R.id.rl_tq:
                Intent intent = new Intent(getActivity(),WeatherActivity.class);
                intent.putExtra("county",county);
                startActivity(intent);

                break;
        }
    }

    class ViewHolder extends BannerViewHolder {
        public bannerImageView bannerImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            bannerImageView = itemView.findViewById(R.id.item_img);
        }
    }

    /**
     * 定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {

                        //只定位一次
                        if (isFirstLoc) {
                            //定位成功回调信息，设置相关消息
                            amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                            double currentLat = amapLocation.getLatitude();//获取纬度
                            double currentLon = amapLocation.getLongitude();//获取经度
                            Log.i("currentLocation", "currentLat : " + currentLat + " currentLon : " + currentLon);
                            amapLocation.getAccuracy();//获取精度信息
                            tv_location.setText(amapLocation.getPoiName());
                            county = amapLocation.getCity();
                            //获取天气信息
                            //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
                            mquery = new WeatherSearchQuery(amapLocation.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
                            mweathersearch=new WeatherSearch(getActivity());
                            mweathersearch.setOnWeatherSearchListener(onWeatherSearchListener);
                            mweathersearch.setQuery(mquery);
                            mweathersearch.searchWeatherAsyn(); //异步搜索

                            isFirstLoc = false;
                        }
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                        tv_location.setText("定位失败");
                    }
                }
        }
    };


   public WeatherSearch.OnWeatherSearchListener onWeatherSearchListener = new WeatherSearch.OnWeatherSearchListener() {
        //实况天气
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult ,int rCode) {
            if (rCode == 1000) {
                if (weatherLiveResult != null&&weatherLiveResult.getLiveResult() != null) {
                    weatherlive = weatherLiveResult.getLiveResult();
                    wd = weatherlive.getTemperature();
                    tq = weatherlive.getWeather();
                    mHandler.sendEmptyMessage(0);
                }else {
                    Toast.makeText(getActivity(),"天气获取失败",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getActivity(),"天气获取失败"+rCode,Toast.LENGTH_SHORT).show();
            }
        }

        //预报天气
        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rCode) {
            if (rCode == 1000) {
                if (localWeatherForecastResult != null&&localWeatherForecastResult.getForecastResult() != null) {
                    mDayWeatherList = localWeatherForecastResult.getForecastResult().getWeatherForecast();
//                    weatherlive = weatherLiveResult.getLiveResult();
                    Toast.makeText(getActivity(),localWeatherForecastResult.getForecastResult().toString(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"天气获取失败",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getActivity(),"天气获取失败"+rCode,Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 检查权限
     */
    @AfterPermissionGranted(ACCESS_FINE_LOCATION)
    private void checkPerm() {
        //权限参数
        String[] params={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(EasyPermissions.hasPermissions(getActivity(),params)){
            getCurrentLocationLatLng();
        }else{
            EasyPermissions.requestPermissions(this,"需要开通定位权限",ACCESS_FINE_LOCATION,params);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //这里表示拒绝权限后再点击出现的去设置对话框
            //这里需要重新设置Rationale和title，否则默认是英文格式
            //Rationale：对话框的提示内容，否则默认是英文格式
            // title：对话框的提示标题，否则默认是英文格式


            new AppSettingsDialog.Builder(this)
                    .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置屏幕以修改应用权限")
                    .setTitle("必需权限")
                    .build()
                    .show();
        }

    }

}
