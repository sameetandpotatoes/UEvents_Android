package com.sapra.uevents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Tag extends Fragment{
	private View rootView;
	public Tag(){
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		if (isAdded()){
			rootView = inflater.inflate(R.layout.tags, container, false);
			addImageButtonListeners();
			return rootView;
		} else{
			return null;
		}
	}
	@Override
	public void onCreate(Bundle savedState) {
	    super.onCreate(savedState);
	    setRetainInstance(true); // handle rotations gracefully
	}
	public void onDestroy(){
		super.onDestroy();
	}
	private void addImageButtonListeners(){
		ImageButton button1 = null, button2 = null, button3 = null, button4 = null, button5 = null, button6 = null;
        ImageButton imagebuttons[]={ button1, button2, button3, button4, button5, button6};
        int ids[] = {R.id.all,R.id.food,R.id.nightlife,R.id.music, R.id.offcampus, R.id.sports};
	    for(int i = 0; i < imagebuttons.length; i++)
	    {
	        imagebuttons[i]=(ImageButton) rootView.findViewById(ids[i]);
	        imagebuttons[i].setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	ImageButton ib = ((ImageButton) v);
	            	String tag = (String) ib.getTag();
	            	
	            	int position = 4;
	            	if (tag.equals("Food"))
	            		position = Constants.FOOD;
	            	else if (tag.equals("Music"))
	            		position = Constants.MUSIC;
	            	else if (tag.equals("Off Campus"))
	            		position = Constants.OFF_CAMPUS;
	            	else if (tag.equals("Sports"))
	            		position = Constants.SPORTS;
	            	else if (tag.equals("Nightlife"))
	            		position = Constants.NIGHTLIFE;
	            	else
	            		position = 0;
	            	((LoggedIn) getActivity()).getPCListener().onPageSelected(position);
	            }
	        });
		}
	}
}
