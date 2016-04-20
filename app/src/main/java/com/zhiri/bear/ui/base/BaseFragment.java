package com.zhiri.bear.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.zhiri.bear.App;
import com.zhiri.bear.R;
import com.zhiri.bear.models.User;
import com.zhiri.bear.ui.user.LoginActivity;
import com.zhiri.bear.utils.NetworkUtils;
import com.zhiri.bear.utils.T;
import com.zhiri.bear.views.LoadingDialog;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by Sky on 2015/9/4 22:21
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private boolean isVisibleToUser;
    private boolean isViewInitiated;
    private boolean isDataInitiated;
    protected LayoutInflater mIflater;
    protected View mRootView;
    protected Activity mActivity;
    private LoadingDialog mLoadingDialog;
    public BaseFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mIflater = inflater;
//        mRootView = initializeInflaterView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(getRootViewLayoutId(), container, false);
        if (mRootView != null) {
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    protected abstract int getRootViewLayoutId();

//    protected abstract View initializeInflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void initializeViews();

    protected abstract void initializeData();

    public abstract void onClick(View v);

    protected int getResColor(int color) {
        return this.getResources().getColor(color);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        initializeData();
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            prepareFetchData();
        }
    }

    protected void prepareFetchData() {
        prepareFetchData(false);
        prepareFinish();
    }

    protected void prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            isDataInitiated = true;
            fetchData();
        }
    }

    protected void prepareFinish() {
        if (isVisibleToUser && isViewInitiated) {
            lazyLoadData();
        }
    }

    public void lazyLoadData() {

    }

    protected void fetchData() {
    }

    public View findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(String action) {
        startActivity(action, null);
    }

    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(getActivity());
        }
        mLoadingDialog.show();
    }

    public void closeLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public void showLoading(int status) {
        if (status == 0) {
            showLoadingDialog();
        } else {
            closeLoadingDialog();
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
        closeLoadingDialog();
    }

    @Override
    public void onDetach() {
        Logger.d("TAG", "onDetach() : ");
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkNetWork() {
        boolean available = NetworkUtils.isNetworkAvailable(getContext().getApplicationContext());
        if (!available) {
            T.showShort(getContext().getApplicationContext(), "请检测网络链接");
            return false;
        }
        return true;
    }

    public boolean checkLogin() {
        User user = App.getInst().getUser();
        if (user == null) {
            startActivity(LoginActivity.class);
            return false;
        }
        return true;
    }


}
