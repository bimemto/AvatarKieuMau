package com.duyota.standardavatar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by admin on 5/12/16.
 */
public class RatioImageView extends ImageView {

    private int squareDimen = 0;
    private float ratio = 1;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
//        ratio = a.getFloat(a.getIndex(0), 1);
//        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int square = getMeasuredWidth();
        if (square > squareDimen) {
            squareDimen = (int) (square / ratio);
        }
        setMeasuredDimension(square, squareDimen);
    }
}
