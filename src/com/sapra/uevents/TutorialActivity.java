package com.sapra.uevents;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class TutorialActivity extends FragmentActivity{
	private TutorialAdapter mAdapter;
	private ViewPager mPager;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.tutorial);
		mAdapter = new TutorialAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
    }
}
