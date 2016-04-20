package com.zhiri.bear.ui.base;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.zhiri.bear.R;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by MagicBean on 2016/01/06 17:17:21
 */
public class BasePagerActivity extends BaseActivity {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.mPager)
    ViewPager mPager;
    protected ArrayList<PagerModel> mEntities = new ArrayList<>();
    private ModuleAdapter mAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_base_pager);
    }

    @Override
    protected void initializeViews() {
        mPager.setOffscreenPageLimit(3);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mAdapter = new ModuleAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    protected void initializeData() {

    }

    @Override
    public void onClick(View v) {

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
