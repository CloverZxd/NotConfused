package com.zxd.notconfused.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zxd.notconfused.R;
import com.zxd.notconfused.ui.fragment.HomePageFragment;
import com.zxd.notconfused.ui.fragment.TestFragment;
import com.zxd.notconfused.utils.BottomNavigationViewHelper;
import com.zxd.notconfused.utils.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.vp)
    NoScrollViewPager viewPager;

    MenuItem menuItem;

    List<Fragment> list = new ArrayList<>();

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     /*   getWindow().requestFeature(Window.FEATURE_NO_TITLE);
       //将背景图和状态栏融合到一起
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        navigationView.setNavigationItemSelectedListener(mOnNavigationItemClick);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItem);
        viewPager.setNoScroll(true);//控制viewpager左右滑动，true为不可滑动，false为可滑动
        list.add(new HomePageFragment());
        list.add(new TestFragment());
        list.add(new TestFragment());
        list.add(new TestFragment());
        viewPager.setAdapter(mAdapter);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItem
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItem = item;

            switch (item.getItemId()){
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_person:
                    viewPager.setCurrentItem(3);
                    return true;

            }
            return false;
        }
    };


    private NavigationView.OnNavigationItemSelectedListener mOnNavigationItemClick
            = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_camera:
                    Toast.makeText(MainActivity.this, "第一个", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };
    //viewpager滑动切换时，保留原来页面的数据状态
    FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            fragment = list.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id",""+position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment)super.instantiateItem(container,position);
            getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //                super.destroyItem(container, position, object);
            Fragment fragment = list.get(position);
            getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
        }
    };
}
