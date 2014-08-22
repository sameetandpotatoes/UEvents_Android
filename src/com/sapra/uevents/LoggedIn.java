package com.sapra.uevents;

import java.lang.reflect.Method;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Display;

import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
public class LoggedIn extends FragmentActivity implements ActionBar.TabListener{
    
	private static Session session;
	private static boolean firstTime = true;
	private static String tag = "";
	private static String eventsURL, myEventsURL;
	
	PagerAdapter mPagerAdapter;
	ViewPager mViewPager;
	private ViewPager.SimpleOnPageChangeListener PCListener;
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        checkConnectivity();
        final ActionBar actionbar = getActionBar();
        AppRater.app_launched(this);
        
        setBounds();
        forceTabs();
        
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        session = (Session) getIntent().getExtras().get("Session");
           
        eventsURL = ENVRouter.eventsURL();
        myEventsURL = ENVRouter.myEventsURL();
        
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.uchicago))));
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.nothing);
        
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(Constants.PAGES_COUNT);
        mViewPager.setAdapter(mPagerAdapter);
        PCListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	switch(position){
	            	case 0: tag = "All Events"; break;
	            	case 1: tag = "Tags"; break;
	            	case 2: tag = User.firstName + "'s Events"; break;
	            	case 3: tag = "Settings"; break;
	            	case Constants.FOOD: tag = Constants.FOODTAG; break;
	            	case Constants.NIGHTLIFE: tag = Constants.NIGHTLIFETAG; break;
	            	case Constants.MUSIC: tag = Constants.MUSICTAG; break;
	            	case Constants.OFF_CAMPUS: tag = Constants.OFF_CAMPUSTAG; break;
	            	case Constants.SPORTS: tag = Constants.SPORTSTAG; break;
            	}
            	setActionBarTitle(tag, true);
            	if (position < 4)
            		actionbar.setSelectedNavigationItem(position);
            	else{
            		actionbar.setSelectedNavigationItem(1);
            		mViewPager.setCurrentItem(position);
            	}
            	checkConnectivity();
            }
        };
        mViewPager.setOnPageChangeListener(PCListener);
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
        	if (i < 4){
        		actionbar.addTab(
        			actionbar.newTab()
                        .setIcon(mPagerAdapter.getIconTitle(i))
                        .setTabListener(this));
        	}
        }
        if (firstTime){
        	PCListener.onPageSelected(0);
        	firstTime = !firstTime;
        }
    }
    public void setBounds() {
    	Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Constants.width = size.x;
        Constants.height = size.y;
	}
	/**
     * Checks connectivity and displays dialog
     *
     */
    private void checkConnectivity(){
    	if (!isConnected()){
    		AlertDialog ad = new AlertDialog.Builder(this).create();
	 	    ad.setCancelable(false);
	 	    ad.setTitle("Error Retrieving Events");
	 	    ad.setMessage("Check your wireless connections and try again.");
	 	    ad.setButton("Try Again", new DialogInterface.OnClickListener() {
	 	        public void onClick(DialogInterface dialog, int which) {
	 	        	finish();
	 	        	startActivity(getIntent());
	 	        }
	 	    });
	     	ad.show();
    	}
    }
    /**
     * Determine if phone is wirelessly connected
     * @return Returns true if connected
     */
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    @Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        setBounds();
        forceTabs(); // Handle orientation changes.
    }
    @Override
    public void onStart(){
    	super.onStart();
    	EasyTracker.getInstance(this).activityStart(this);
    }
    @Override
    public void onStop(){
    	super.onStop();
    	EasyTracker.getInstance(this).activityStop(this);
    }
    /**
     * Forces the tabs to remain below the action bar
     */
    public void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, false);
        }
        catch(final Exception e) {
            // Handle issues as needed: log, warn user, fallback etc
            // This error is safe to ignore, standard tabs will appear.
        }
    }
    
    public Handler mUiHandler = new Handler(Looper.getMainLooper());

    public void setActionBarTitle(final CharSequence title, final boolean showHomeAsUp)
    {
        mUiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ActionBar ab = getActionBar();
                ab.setDisplayHomeAsUpEnabled(showHomeAsUp);
                ab.setDisplayShowTitleEnabled(false);
                SpannableString s = new SpannableString(title);
                s.setSpan(new TypefaceSpan("HN-Light.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                setTitle(s);
                ab.setDisplayShowTitleEnabled(true);
            }
        });
    }
    public ViewPager.SimpleOnPageChangeListener getPCListener(){
    	return PCListener;
    }
    public ViewPager getViewPager(){
    	return mViewPager;
    }
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(arg0.getPosition());
	}
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
	/**
     * Handles log out of Facebook and redirects to login
     *
     */
    public void onClickLogout() {
	  Session session = Session.getActiveSession();
	  if (!session.isClosed()) {
	      session.closeAndClearTokenInformation();
	      session = null;
	  }
	  Intent home = new Intent(getApplicationContext(), LoginPage.class);
	  home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	  startActivity(home);
	  finish();
	}
    public void onResume() {
        super.onResume();
        setBounds();
    }     
    public static class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
        	switch(i){
	        	case 0: //All Events
	        		return EventsFragment.newInstance(eventsURL, session, "All");
	        	case 1:
	        		return new Tag();
	        	case 2: //My Events
	        		return EventsFragment.newInstance(myEventsURL, session, "My");
	        	case 3:
	        		return SettingsFragment.newInstance();
	        	case Constants.FOOD:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.FOODTAG, session, Constants.FOODTAG);
	        	case Constants.NIGHTLIFE:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.NIGHTLIFETAG, session, Constants.NIGHTLIFETAG);
	        	case Constants.MUSIC:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.MUSICTAG, session, Constants.MUSICTAG);
	        	case Constants.OFF_CAMPUS:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.OFF_CAMPUSTAG, session, Constants.OFF_CAMPUSTAG);
	        	case Constants.SPORTS:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.SPORTSTAG, session, Constants.SPORTSTAG);
	        	default:
	        		return EventsFragment.newInstance(eventsURL, session, "All");
        	}
        }

        @Override
        public int getCount() {
            return Constants.PAGES_COUNT;
        }
        public int getIconTitle(int position) {
            switch(position){
            	case 0: //All Events
            		return R.drawable.allevents;
            	case 1:
            		return R.drawable.tag25;
            	case 2:
            		return R.drawable.myevents;
            	case 3:
            		return R.drawable.settings;
            	default:
            		return R.drawable.allevents;		
            }
        }
    }
}
