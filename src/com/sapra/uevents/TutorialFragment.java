package com.sapra.uevents;

import com.facebook.Session;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class TutorialFragment extends Fragment{
	private int drawable;
	private ImageView background;
	public TutorialFragment(){
		
	}
	public static TutorialFragment newInstance(int drawableId){
		TutorialFragment tf = new TutorialFragment();
		tf.drawable = drawableId;
		return tf;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	final View view = (View) inflater.inflate(R.layout.home_slide_tutorial, container, false);
    	if(background != null) {
    		((BitmapDrawable)background.getDrawable()).getBitmap().recycle();
        }
    	background = (ImageView) view.findViewById(R.id.background);
        background.setImageResource(drawable);
    	if (drawable == R.drawable.tutorial6){
    		Button loginButton = (Button) view.findViewById(R.id.ok_got_it);
    		loginButton.setVisibility(View.VISIBLE);
    		loginButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					Intent events = new Intent(getActivity().getApplicationContext(), LoggedIn.class);
					getActivity().onTrimMemory(getActivity().TRIM_MEMORY_RUNNING_CRITICAL);
			    	events.putExtra("Session", Session.getActiveSession());
			    	startActivity(events);
			    	getActivity().finish();
				}
    		});
    	}
    	return view;
	}
}
