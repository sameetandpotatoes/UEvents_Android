package com.sapra.uevents;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingsFragment extends Fragment{
	private ImageLoader imageLoader;
	public SettingsFragment(){
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(Constants.config);
	}
	public static final SettingsFragment newInstance(){
		SettingsFragment sf = new SettingsFragment();
		return sf;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if (isAdded()){
			View rootView = inflater.inflate(R.layout.settings, container, false);
			Typeface regular = LoggedIn.regular;
			TextView school = (TextView) rootView.findViewById(R.id.school_selected);
			school.setTextColor(Color.BLACK);
			school.setText(User.name + "\n" + User.schoolName);
			school.setTypeface(regular);
			
			MLRoundedImageView profilePictureView = (MLRoundedImageView) rootView.findViewById(R.id.fbProfilePictureView);
			imageLoader.displayImage(User.pictureURL, profilePictureView);
			LoginButton loginView = (LoginButton) rootView.findViewById(R.id.connectWithFbButton);
			loginView.setBackgroundColor(getResources().getColor(R.color.uchicago));
			loginView.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
//					User.clear();
					((LoggedIn) getActivity()).onClickLogout();
				}
			});
			regular = null;
			return rootView;
		} else{
			return null;
		}
	}
}