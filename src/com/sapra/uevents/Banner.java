package com.sapra.uevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class Banner extends ImageView {
    public Banner(Context context) {
        super(context);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * Dynamically changes the height to maintain scale aspect ratio
     *
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 400;
        if (getDrawable() != null && getDrawable().getIntrinsicWidth() != 0){
        	height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        	if (height < 400)
        		height = 400;
        	else if (height > 550)
        		height = 550;
        }
        setMeasuredDimension(width, height);
        setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    /**
     * Recycles bitmap to save memory
     *
     */
    public void onDestroy(){
    	Drawable drawable = this.getDrawable();
    	if (drawable instanceof BitmapDrawable) {
    	    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
    	    Bitmap bitmap = bitmapDrawable.getBitmap();
    	    bitmap.recycle();
    	}
    	this.setImageBitmap(null);
    }
}
