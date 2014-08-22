package com.sapra.uevents;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public final class TestFragment extends Fragment {
    private LoginButton lb;
	private UiLifecycleHelper uiHelper;
	private String mContent = "";
    private int drawableId;
    private Session mSession = null;
    public TestFragment(){
    }
    public static TestFragment newInstance(int imageId, String mContent){
    	TestFragment tf = new TestFragment();
    	tf.mContent = mContent;
    	tf.drawableId = imageId;
    	return tf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	uiHelper = new UiLifecycleHelper(getActivity(), callback);
    	uiHelper.onCreate(savedInstanceState);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, callback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
                Context c = getActivity().getApplicationContext();
                session = new Session.Builder(c).setApplicationId(c.getString(ENVRouter.getFacebookAppId())).build();
                c = null;
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(callback));
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this.getActivity(), requestCode, resultCode, data);
    }
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	final View view = (View) inflater.inflate(R.layout.home_slide, container, false);
    	ImageView background = (ImageView) view.findViewById(R.id.background);
    	background.setImageResource(drawableId);
    	
    	List<String> permissions = Arrays.asList("email, public_profile, user_friends");
    	lb = (LoginButton) view.findViewById(R.id.facebook);
    	lb.setFragment(this);
    	lb.setReadPermissions(permissions);
        lb.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				onClickLogin();
			}
		});
    	return view;
    }
    private boolean isSessionChanged(Session session) {

        // Check if session state changed
        if (mSession.getState() != session.getState())
            return true;

        // Check if accessToken changed
        if (mSession.getAccessToken() != null) {
            if (!mSession.getAccessToken().equals(session.getAccessToken()))
                return true;
        }
        else if (session.getAccessToken() != null) {
            return true;
        }

        // Nothing changed
        return false;
    }
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened() && getActivity() != null) {
			if (mSession == null || isSessionChanged(session)){
				mSession = session;
				Log.i("API", "Getting user info");
				((LoginPage) getActivity()).getUserInfo();
//				if (((LoginPage) getActivity()).getUserInfo()){
					if (((LoginPage) getActivity()).postToAPI()){
						Log.i("API", "Logging in");
						((LoginPage) getActivity()).loggedIn();
					} else{
						Log.i("API", "Going to university");
						((LoginPage) getActivity()).university();
					}
//				} else{
//					Log.i("API", "Already had user info");
//					((LoginPage) getActivity()).loggedIn();
//				}
			}
		} else if (state.isClosed()) {
			lb.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					onClickLogin();
				}
			});
		}
		else{
			if (!Session.getActiveSession().isOpened())
				Session.getActiveSession().openForRead(new Session.OpenRequest(this.getActivity()).setCallback(callback));
		}
	}
	private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
        	session.openForRead(new Session.OpenRequest(this.getActivity()).setCallback(callback));
        } else {
            Session.openActiveSession(this.getActivity(), true, callback);
        }
    }
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    	uiHelper.onSaveInstanceState(outState);
    }
}
