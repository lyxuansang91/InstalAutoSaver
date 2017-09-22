package com.adu.instaautosaver.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Thomc on 21/02/2016.
 */
public class SquareRelativeLayout extends ViewGroup {

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int u, int r, int d) {
        getChildAt(0).layout(0, 0, r - l, d - u); // Layout with max size
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        View child = getChildAt(0);
        child.measure(widthMeasureSpec, widthMeasureSpec);
        int width = resolveSize(child.getMeasuredWidth(), widthMeasureSpec);
        child.measure(width, width); // 2nd pass with the correct size
        setMeasuredDimension(width, width);
    }
}
