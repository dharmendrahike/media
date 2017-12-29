package com.example.dharmendraverma.myapplication.gpuimage;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicBoolean;

/*import com.bsb.hike.camera.event.CameraPreviewTapConfirmedEvent;
import com.bsb.hike.camera.event.PreviewSingleTapConfirmed;
import com.bsb.hike.ui.HomeActivity;
import com.bsb.hike.utils.Utils;*/

/**
 * Created by atul on 25/08/16.
 */
public abstract class GPUImageCarouselOnTouchListener implements View.OnTouchListener {

    private final String TAG = GPUImageCarouselOnTouchListener.class.getSimpleName();
    private final DisplayMetrics metrics;
    private Context mContext;
    private GestureDetector mDetector;
    private int currentX;
    private boolean isDragging;

    public static final int STATUS_SHIFT_RIGHT = -111;
    public static final int STATUS_SHIFT_LEFT = -222;
    public static final int STATUS_MOVE_CANCEL = -333;
    public static final int STATUS_MOVE_LEFT = -444;
    public static final int STATUS_MOVE_RIGHT = -555;
    public static final int STATUS_MOVE_FLING = -666;
    private int startX;
    private int status = STATUS_MOVE_CANCEL;
    private AtomicBoolean isAnimating = new AtomicBoolean(false);
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private float lastDistanceX;

    private class CarouselGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isAnimating.get()) {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (lastDistanceX > 0) {
                        status = STATUS_SHIFT_RIGHT;
                        if (startX == 0) {
                            startX = metrics.widthPixels;
                        }
                        endDrag(0);
                    } else {
                        status = STATUS_MOVE_CANCEL;
                        endDrag(metrics.widthPixels);
                    }
                }
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (lastDistanceX < 0) {
                        status = STATUS_SHIFT_LEFT;
                        endDrag(metrics.widthPixels);
                    } else {
                        status = STATUS_MOVE_CANCEL;
                        endDrag(0);
                    }
                } else if (Math.abs(e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE) {
                    endDrag();
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
             if (!isAnimating.get()) {
                isDragging = true;
                Log.d("Gesture", "x: " + distanceX + " y: " + distanceY);
                if(e2 == null)
                    e2 = e1;
                currentX = (int) e2.getX();
                lastDistanceX = distanceX;
                onTouchMoved(getGLAdjustedXPosn(e2.getX()), (e1.getX() - e2.getX() > 0) ? STATUS_MOVE_LEFT : STATUS_MOVE_RIGHT);
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("Gesture", "onSingleTapConfirmed");
         //   EventBus.getDefault().post(new CameraPreviewTapConfirmedEvent(new PreviewSingleTapConfirmed(e)));
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!isAnimating.get()) {
                Log.d("Gesture", "ondown");
                startX = (int) e.getX();
            }
            return true;
        }

    }

    
    public GPUImageCarouselOnTouchListener(Context context) {
        mContext = context;
        mDetector = new GestureDetector(mContext, new CarouselGestureDetector());
        metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                 //   Utils.requestParentDisallowInterceptTouchEvent(view, true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
               //     Utils.requestParentDisallowInterceptTouchEvent(view, false);
                    break;
            }
        boolean result = mDetector.onTouchEvent(motionEvent);
        if (!result && !isAnimating.get()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Log.d("Gesture", "action_up");
                if (isDragging) {
                    endDrag();
                    return true;
                } else {
                    return false;
                }

            } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                if (isDragging) {
                    endDrag();
                    return true;
                } else {
                    return false;
                }

            }
        }

        return result;
    }

    private void reset() {
        startX = 0;
        currentX = 0;
        status = STATUS_MOVE_CANCEL;
        isDragging = false;
        isAnimating.set(false);
        Log.d(TAG, "animation done");
    }

    private void endDrag() {
        int screenEnd = metrics.widthPixels;
        int screenMid = screenEnd / 2;
        int delta = currentX - startX;

        int to;
        if (delta < 0) {
            if (currentX > screenMid) {
                to = screenEnd;
                status = STATUS_MOVE_CANCEL;

            } else {
                to = 0;
                status = STATUS_SHIFT_RIGHT;
            }
        } else {
            if (currentX > screenMid) {
                to = screenEnd;
                status = STATUS_SHIFT_LEFT;
            } else {
                to = 0;
                status = STATUS_MOVE_CANCEL;
            }
        }

        endDrag(to);
    }

    private void endDrag(int to) {
        isAnimating.set(true);
        final ValueAnimator dragAnimator = ValueAnimator.ofInt(currentX, to);
        dragAnimator.setInterpolator(new FastOutLinearInInterpolator());
        dragAnimator.setDuration(150);
        dragAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer newVal = (Integer) dragAnimator.getAnimatedValue();
                currentX = newVal;
                onTouchMoved(getGLAdjustedXPosn(currentX), STATUS_MOVE_FLING);
            }
        });

        dragAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                //Do nothing
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dragAnimator.cancel();
                onTouchMoved(getGLAdjustedXPosn(currentX), status);
                reset();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //Do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                //Do nothing
            }
        });
        dragAnimator.start();

    }

    private float getGLAdjustedXPosn(float pos) {
        if (pos == 0f) {
            return pos;
        }
        return pos / metrics.widthPixels;
    }

    public abstract void onTouchMoved(float newPos, int status);

}
