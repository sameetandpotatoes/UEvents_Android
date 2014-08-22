package com.sapra.uevents;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TutorialAdapter extends FragmentPagerAdapter {
    protected final String[] CONTENT = new String[] { 
        	"1",
        	"2"
        };
        protected final int[] IMAGES = new int[]{
        	R.drawable.tutorial1,
        	R.drawable.tutorial2,
        	R.drawable.tutorial3,
        	R.drawable.tutorial4,
        	R.drawable.tutorial5,
        	R.drawable.tutorial6
        };
        private int mCount = IMAGES.length;

        public TutorialAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TutorialFragment.newInstance(IMAGES[position]);
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