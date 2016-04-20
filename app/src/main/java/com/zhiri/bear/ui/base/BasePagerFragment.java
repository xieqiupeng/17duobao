package com.zhiri.bear.ui.base;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zhiri.bear.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MagicBean on 2016/01/06 17:17:21
 */
public class BasePagerFragment extends BaseFragment {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.mPager)
    protected ViewPager mPager;
    protected ArrayList<PagerModel> mEntities = new ArrayList<>();
    @Bind(R.id.list_container)
    protected FrameLayout listContainer;
    private ModuleAdapter mAdapter;

    @Override
    protected int getRootViewLayoutId() {
        return R.layout.fragment_base_pager;
    }

    @Override
    protected void initializeViews() {
        mPager.setOffscreenPageLimit(3);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mAdapter = new ModuleAdapter(getChildFragmentManager());
    }

    @Override
    protected void initializeData() {
        mPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class ModuleAdapter extends FragmentStatePagerAdapter {
        public ModuleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            PagerModel model = mEntities.get(i);
            return model.fragment;
        }

        @Override
        public int getCount() {
            return mEntities.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mEntities.get(position).title;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
