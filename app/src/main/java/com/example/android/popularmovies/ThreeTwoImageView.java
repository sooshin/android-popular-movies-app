package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * The ThreeTwoImageView class is responsible for making ImageView 3:2 aspect ratio.
 * The ThreeTwoImageView is used for movie backdrop image in the activity_detail.xml.
 */
public class ThreeTwoImageView extends AppCompatImageView {

    /** Constant value */
    private static final int TWO = 2;
    private static final int THREE = 3;

    /**
     * Creates ThreeTwoImageView
     *
     * @param context Used to talk to the UI and app resources
     */
    public ThreeTwoImageView(Context context) {
        super(context);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * This method measures the view and its content to determine the measured width and the measured
     * height, which will make 3:2 aspect ratio.
     *
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by th parent
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * TWO / THREE;
        int threeTwoHeightSpec =
                MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
    }
}
