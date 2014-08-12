package com.sapra.uevents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter {
    protected final String[] CONTENT = new String[] { 
    	"1",
    	"2"
    };
    protected final int[] IMAGES = new int[]{
    	R.drawable.home1,
    	R.drawable.home2
    };
    private int mCount = IMAGES.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new TestFragment(IMAGES[position], CONTENT[position]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return CONTENT[position];
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}