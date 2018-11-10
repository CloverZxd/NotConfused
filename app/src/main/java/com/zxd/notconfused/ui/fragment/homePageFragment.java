package com.zxd.notconfused.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxd.notconfused.R;
import com.zxd.notconfused.adapter.homeGridViewAdpter;
import com.zxd.notconfused.adapter.homeViewPagerAdapter;
import com.zxd.notconfused.bean.a0;

import java.util.ArrayList;
import java.util.List;

public class homePageFragment extends Fragment{

    //主View
    private  View view;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage,container,false);
        initResources();//获取资源文件，动态添加列表数据
        initView();
        return view;
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
}
