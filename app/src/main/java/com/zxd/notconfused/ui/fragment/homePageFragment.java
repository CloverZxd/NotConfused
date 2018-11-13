package com.zxd.notconfused.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.zxd.notconfused.ui.views.bannerImageView;

import java.util.ArrayList;
import java.util.List;

public class homePageFragment extends Fragment implements View.OnClickListener {


    //主View
    private  View view;

    //====================获取地理位置信息=======================

    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage,container,false);
        toolbar=view.findViewById(R.id.toolbar);
        //fragment声明
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //隐藏自带的项目名称
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkpermission();//获取定位相关权限
        initResources();//获取资源文件，动态添加列表数据
        initBanner();
        initView();
        return view;
    }

    private void checkpermission() {
        //判断定位相关权限打开了没有
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.
                permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.
                permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }else{
            getCurrentLocationLatLng();//获取地理位置信息
        }
    }

    private void getCurrentLocationLatLng() {
        //展示定位信息
        ll_location = view.findViewById(R.id.ll_location);
        tv_location = view.findViewById(R.id.tv_location);
        //默认显示定位中...
        tv_location.setText("正在定位...");
        ll_location.setOnClickListener(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

 /* //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景） 设置了场景就不用配置定位模式等
    option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
    if(null != locationClient){
        locationClient.setLocationOption(option);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        locationClient.stopLocation();
        locationClient.startLocation();
    }*/
        // 同时使用网络定位和GPS定位,优先返回最高精度的定位结果,以及对应的地址描述信息
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //只会使用网络定位
        /* mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);*/
        //只使用GPS进行定位
        /*mLocationOption.setLocationMode(AMapLocationMode.Device_Sensors);*/
        // 设置为单次定位 默认为false
        /*mLocationOption.setOnceLocation(true);*/
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。默认连续定位 切最低时间间隔为1000ms
        mLocationOption.setInterval(10000);
        //设置是否返回地址信息（默认返回地址信息）
        /*mLocationOption.setNeedAddress(true);*/
        //关闭缓存机制 默认开启 ，在高精度模式和低功耗模式下进行的网络定位结果均会生成本地缓存,不区分单次定位还是连续定位。GPS定位结果不会被缓存。
        /*mLocationOption.setLocationCacheEnable(false);*/
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
        /*    if (!IsGpsWork.isGpsEnabled(getActivity())) {
                Toast toast = Toast.makeText(getActivity(), "打开gps", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {*/
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        double currentLat = amapLocation.getLatitude();//获取纬度
                        double currentLon = amapLocation.getLongitude();//获取经度
//                        latLonPoint = new LatLonPoint(currentLat, currentLon);  // latlng形式的
                        /*currentLatLng = new LatLng(currentLat, currentLon);*/   //latlng形式的
                        Log.i("currentLocation", "currentLat : " + currentLat + " currentLon : " + currentLon);
                        amapLocation.getAccuracy();//获取精度信息
                        tv_location.setText(amapLocation.getPoiName());
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                        tv_location.setText("定位失败");
                    }
                }
            //}
        }
    };

    //设置打开定位权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result:grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getActivity(),"必须同意所有权限才可以使用本应用",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    getCurrentLocationLatLng();//获取地理位置信息
                }else{
                    Toast.makeText(getActivity(),"发生未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (getChildFragmentManager().getBackStackEntryCount() == 0) {
            inflater.inflate(R.menu.menu_home_page_fragment, menu);
        }
    }*/
}
