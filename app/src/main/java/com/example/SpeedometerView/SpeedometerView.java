package com.example.SpeedometerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class SpeedometerView extends View {
    private static int SELECTION_COUNT = 181;
    private float mWidth;
    private float mHeight;
    private Paint mTextPaint;
    private Paint mDialPaint;
    private Paint mLinePaint;
    private float mRadius;
    private float arrowRadius;
    private float mActualSpeed;

    private float mX;
    private float mY;

    private int mHighSpeedOnColor;
    private int mHighSpeedOffColor;

    AnimatorSet mAccelerationAnimatorSet = new AnimatorSet();
    AnimatorSet mDecelerationAnimatorSet = new AnimatorSet();

    private final StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];


    public SpeedometerView(Context context) {
        super(context);
        init(null);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        mHighSpeedOnColor = Color.RED;
        mHighSpeedOffColor = Color.BLACK;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);
        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(Color.GRAY);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mHighSpeedOffColor);
        mLinePaint.setStrokeWidth(10);
        mActualSpeed = 0;

        if(attrs!= null) {
            TypedArray typedArray =getContext().obtainStyledAttributes(attrs,
                    R.styleable.SpeedometerView, 0, 0);
            mHighSpeedOffColor = typedArray.getColor(R.styleable.SpeedometerView_highSpeedOffColor, mHighSpeedOffColor);
            mHighSpeedOnColor = typedArray.getColor(R.styleable.SpeedometerView_highSpeedOnColor, mHighSpeedOnColor);
            typedArray.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mHeight, mWidth) / 2 * 0.8);
        arrowRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(0, arrowRadius);
        mX = xyData[0];
        mY = xyData[1];


    }

    public void setCoordinates(float speed) {
        mActualSpeed = speed;
        if(mActualSpeed >= 150f) {
            mLinePaint.setColor(mHighSpeedOnColor);
        }
        else {
            mLinePaint.setColor(mHighSpeedOffColor);
        }
        float[] xyData = computeXYForPosition(speed, arrowRadius);
        mX = xyData[0];
        mY = xyData[1];
        invalidate();
    }

    public void accelerate() {
        mDecelerationAnimatorSet.cancel();
        float start = mActualSpeed;
        ObjectAnimator accelerateAnimator = ObjectAnimator.ofFloat(this, "Coordinates", start, SELECTION_COUNT);
        accelerateAnimator.setDuration(10000 * (SELECTION_COUNT - (int)mActualSpeed) / SELECTION_COUNT);
        accelerateAnimator.setInterpolator(new DecelerateInterpolator());

        mAccelerationAnimatorSet.play(accelerateAnimator);
        mAccelerationAnimatorSet.start();
    }

    public void decelerate() {
        mAccelerationAnimatorSet.cancel();
        float start = mActualSpeed;
        ObjectAnimator decelerateAnimator = ObjectAnimator.ofFloat(this, "Coordinates", start, 0);
        decelerateAnimator.setDuration(10000 * (int)start / SELECTION_COUNT);
        decelerateAnimator.setInterpolator(new DecelerateInterpolator());

        mDecelerationAnimatorSet.play(decelerateAnimator);
        mDecelerationAnimatorSet.start();
    }


    private float[] computeXYForPosition(final float speed, final float radius) {
        float[] result = mTempResult;
        double startAngle = Math.PI * 0.75f;
        double angle = startAngle + (speed * (Math.PI / SELECTION_COUNT * 1.5f));
        result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
        result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Main dial
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mDialPaint);

        //Digit label
        final float labelRadius = mRadius - 40;
        StringBuffer label = mTempLabel;
        for (int i = 0; i < SELECTION_COUNT; i+=20) {
            float[] xyData = computeXYForPosition(i, labelRadius);
            float x = xyData[0] - 10f;
            float y = xyData[1] + 10f;
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }

        canvas.drawLine(mWidth / 2, mHeight / 2, mX, mY, mLinePaint);
    }

}
