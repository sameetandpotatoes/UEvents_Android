package com.sapra.uevents;

import java.util.Arrays;
import java.util.List;

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
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public final class TestFragment extends Fragment {
    private LoginButton lb;
	private UiLifecycleHelper uiHelper;
	private boolean loggedInAlready = false;
	private Typeface regular, bold;
	private String mContent = "";
    private int drawableId;
    private Session mSession = null;
    public TestFragment(int imageId, String mContent){
    	this.mContent = mContent;
    	this.drawableId = imageId;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	uiHelper = new UiLifecycleHelper(getActivity(), callback);
    	regular = Typeface.createFromAsset(getActivity().getAssets(), Constants.REGULAR);
    	bold = Typeface.createFromAsset(getActivity().getAssets(), Constants.BOLD);
    	uiHelper.onCreate(savedInstanceState);
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, callback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
                session = new Session.Builder(getActivity().getApplicationContext()).setApplicationId(getActivity().getApplicationContext().getString(R.string.app_id)).build();
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
        
        bold = null;
        regular = null;
        
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
				if (((LoginPage) getActivity()).getUserInfo()){
					if (((LoginPage) getActivity()).postToAPI()){
						Log.i("API", "Logging in");
						((LoginPage) getActivity()).loggedIn();
					} else{
						Log.i("API", "Going to university");
						((LoginPage) getActivity()).university();
					}
				} else{
					Log.i("API", "Already had user info");
				}
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
