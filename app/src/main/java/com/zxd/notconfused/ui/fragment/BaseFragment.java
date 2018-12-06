package com.zxd.notconfused.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基类Fragment viewpager懒加载并且每个界面只初始化一次
 */
public abstract class BaseFragment extends Fragment {

	protected View mRootView;
	public Context mContext;
	protected boolean isVisible;
	private boolean isPrepared;
	private boolean isFirst = true;

	public BaseFragment() {
		// Required empty public constructor
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		//        Log.d("TAG", "fragment->setUserVisibleHint");
		if (getUserVisibleHint()) {
			isVisible = true;
			lazyLoad();
		} else {
			isVisible = false;
			onInvisible();
		}
	}


	@Override
	public void onCreate( Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		setHasOptionsMenu(true);
		//        Log.d("TAG", "fragment->onCreate");
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = initView(inflater);
		}
		//        Log.d("TAG", "fragment->onCreateView");
		return mRootView;
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//        Log.d("TAG", "fragment->onActivityCreated");
		isPrepared = true;
		lazyLoad();
	}

	protected void lazyLoad() {
		if (!isPrepared || !isVisible || !isFirst) {
			return;
		}
		Log.d("TAG", getClass().getName() + "->initData()");
		initData();
		isFirst = false;
	}

	//do something
	protected void onInvisible() {


	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		onResult(requestCode, resultCode, data);
	}
	public abstract View initView(LayoutInflater inflater);

	public abstract void initData();

	public abstract void onResult(int requestCode, int resultCode, Intent data);



}