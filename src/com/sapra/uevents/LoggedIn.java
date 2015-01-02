package com.sapra.uevents;

import java.lang.reflect.Method;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Display;

import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
public class LoggedIn extends FragmentActivity implements ActionBar.TabListener{
    
	private static Session session;
	private static boolean firstTime = true;
	private static String tag = "";
	private static String eventsURL, myEventsURL;
	public static int tagPos = 0;
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
//        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mPagerAdapter);
        PCListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	if (tagPos == 0){
            		tagPos = position;
            	}
//            	System.out.println("T: " + tagPos);
            	switch(tagPos){
	            	case 0: tag = "All Events"; break;
	            	case 1: tag = "Tags"; break;
	            	case 2: tag = User.firstName + "'s Events"; break;
	            	case 3: tag = "Settings"; break;
	            	default: tag = Constants.getTag(position); break;
            	}
            	if (tagPos < 4){
            		actionbar.setSelectedNavigationItem(tagPos);
            	}else{
            		actionbar.setSelectedNavigationItem(1);
            		mViewPager.setCurrentItem(tagPos, false);
            	}
            	if (!(tagPos > position && position == 3)){
//            		System.out.println(tagPos + " " + position + " " + tag);
            		if (tagPos > 4){
            			tag = Constants.getTag(tagPos);
            		}
            		setActionBarTitle(tag, true);
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
    @SuppressWarnings("deprecation")
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
		tagPos = 0;
	}
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
	/**
     * Handles log out of Facebook and redirects to login
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
    public static class PagerAdapter extends FragmentStatePagerAdapter {
    	private EventsFragment allEvents, userEvents;
    	private Tag tag;
    	private SettingsFragment sf;
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
        	if (tagPos > i && i == 4){
         		i = tagPos;
         	}
        	switch(i){
	        	case 0: //All Events
	        		if (allEvents == null){
	        			System.out.println("Fragment was null");
	        			allEvents = EventsFragment.newInstance(eventsURL, session, "All"); 
	        		}
	        		return allEvents;
	        	case 1: //Tags
	        		if (tag == null){
	        			tag = new Tag();
	        		}
	        		return tag;
	        	case 2: //My Events
	        		if (userEvents == null){
	        			System.out.println("Fragment was null");
	        			userEvents = EventsFragment.newInstance(myEventsURL, session, "My"); 
	        		}
	        		return userEvents;
	        	case 3: //Settings
	        		if (sf == null){
	        			sf = SettingsFragment.newInstance(); 
	        		}
	        		return sf;
	        	default: //All Tags
	        		return EventsFragment.newInstance(eventsURL, session, Constants.getTag(i));
        	}
        }
        @Override
        public int getCount() {
            return 5;
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
