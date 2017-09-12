package com.fw.basemodules.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.library.R;


public class RobotoTextView extends TextView {
	public RobotoTextView(Context context) {
		super(context);
	}

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttributes(context, attrs);
	}

	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		parseAttributes(context, attrs);
	}

	private void parseAttributes(Context context, AttributeSet attrs) {
		if (!isInEditMode()) {
			TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);

			String fontName = values.getString(R.styleable.RobotoTextView_typeface);

			Typeface tf = CustomTypeface.getInstance(context).getTypeface(fontName);

			setTypeface(tf);
			
			values.recycle();
		}
	}
	
	
	
	public void setTypeface(Context context,int fontID) {
//		Typeface tf = CustomTypeface.getInstance(context).getTypeface(fontName);
//
//		setTypeface(tf);
	}
}
