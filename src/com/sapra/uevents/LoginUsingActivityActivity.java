package com.sapra.uevents;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
public class LoginUsingActivityActivity extends FragmentActivity implements ActionBar.TabListener{
    
//	private static String userId, schoolName, userName;
	private static Session session;
	private static Context context;
	private static boolean firstTime = true;
	private static String tag = "";
	private static String eventsURL, myEventsURL;
	public static Typeface bold, regular;
	
	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	ViewPager mViewPager;
	private ViewPager.SimpleOnPageChangeListener PCListener;
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        checkConnectivity();
        final ActionBar actionbar = getActionBar();
        AppRater.app_launched(this);
        
        // Create global configuration and initialize ImageLoader with this configuration
        Constants.config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .build();
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Constants.width = size.x;
        Constants.height = size.y;
        
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());
        session = (Session) getIntent().getExtras().get("Session");
        
        bold = Typeface.createFromAsset(getAssets(), Constants.BOLD);
        regular = Typeface.createFromAsset(getAssets(), Constants.REGULAR);
        
        context = this;      
        eventsURL = ENVRouter.eventsURL();
        myEventsURL = ENVRouter.myEventsURL();
        
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getResources().getString(R.color.uchicago))));
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.nothing);
        
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(Constants.PAGES_COUNT);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        PCListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	switch(position){
	            	case 0: tag = "All Events"; break;
	            	case 1: tag = "Tags"; break;
	            	case 2: tag = "My Events"; break;
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
        for (int i = 0; i < mDemoCollectionPagerAdapter.getCount(); i++) {
        	if (i < 4){
        		actionbar.addTab(
        			actionbar.newTab()
                        .setIcon(mDemoCollectionPagerAdapter.getIconTitle(i))
                        .setTabListener(this));
        	}
        }
        if (firstTime){
        	PCListener.onPageSelected(0);
        	firstTime = !firstTime;
        }
    }
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
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
    }     
    public static class DemoCollectionPagerAdapter extends FragmentPagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
        	switch(i){
	        	case 0: //All Events
	        		return EventsFragment.newInstance(eventsURL, session, context, "All");
	        	case 1:
	        		return new Tag();
	        	case 2: //My Events
	        		return EventsFragment.newInstance(myEventsURL, session, context, "My");
	        	case 3:
	        		return SettingsFragment.newInstance();
	        	case Constants.FOOD:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.FOODTAG, session, context, Constants.FOODTAG);
	        	case Constants.NIGHTLIFE:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.NIGHTLIFETAG, session, context, Constants.NIGHTLIFETAG);
	        	case Constants.MUSIC:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.MUSICTAG, session, context, Constants.MUSICTAG);
	        	case Constants.OFF_CAMPUS:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.OFF_CAMPUSTAG, session, context, Constants.OFF_CAMPUSTAG);
	        	case Constants.SPORTS:
	        		return EventsFragment.newInstance(eventsURL+"&filter="+Constants.SPORTSTAG, session, context, Constants.SPORTSTAG);
	        	default:
	        		return EventsFragment.newInstance(eventsURL, session, context, "All");
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
            		return R.drawable.tags;
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
