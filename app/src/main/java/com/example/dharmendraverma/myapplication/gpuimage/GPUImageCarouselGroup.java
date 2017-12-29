package com.example.dharmendraverma.myapplication.gpuimage;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*import com.bsb.hike.camera.event.ChangeGLRenderMode;*/
/*import com.bsb.hike.camera.event.OnFilterChanged;*/

/*import de.greenrobot.event.EventBus;*/

/**
 * Created by atul on 25/08/16.
 */
public class GPUImageCarouselGroup extends GPUImageFilterGroup {

    private final ChangeGLRenderMode renderModeEvent;

    private volatile AtomicInteger carouselPosIdx = new AtomicInteger(0);

    private SparseArray<ArrayList<GPUImageFilter>> mFiltersArray;

    private volatile AtomicInteger baselinePosn = new AtomicInteger(-1);

    private float currPosn = -1.0f;

    private volatile float targetPosn;

    private boolean initAnimationComplete;

    private volatile AtomicInteger mStatus = new AtomicInteger(GPUImageCarouselOnTouchListener.STATUS_MOVE_CANCEL);

    public GPUImageCarouselGroup(List<GPUImageFilter> filterList) {
        super(filterList);
        mFiltersArray = new SparseArray<>();

        for (int i = 0; i < filterList.size(); i++) {
            ArrayList<GPUImageFilter> mergedFiltersSparse = new ArrayList<>();
            List<GPUImageFilter> mergedFilters = getMergedFilters(filterList.get(i));
            for (int j = 0; j < mergedFilters.size(); j++) {
                mergedFiltersSparse.add(j, mergedFilters.get(j));
            }
            mFiltersArray.put(i, mergedFiltersSparse);
        }

        renderModeEvent = new ChangeGLRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        runPendingOnDrawTasks();
        if (!isInitialized() ||  mFrameBuffers == null || mFrameBufferTextures == null) {
            return;
        }

        processNewState();
        List<GPUImageFilter> mActiveMergedFilters = new ArrayList();

        if (baselinePosn.get() == 1) { // moving left, right element required
            ArrayList<GPUImageFilter> leftFilters = mFiltersArray.get(carouselPosIdx.get());
            setSide(leftFilters, 0);
            ArrayList<GPUImageFilter> rightFilters = mFiltersArray.get(getRightPosn());
            setSide(rightFilters, 1);
            mActiveMergedFilters.addAll(leftFilters);
            mActiveMergedFilters.addAll(rightFilters);
        } else if (baselinePosn.get() == 0) { // moving right, left element required
            ArrayList<GPUImageFilter> leftFilters = mFiltersArray.get(getLeftPosn());
            setSide(leftFilters, 0);
            ArrayList<GPUImageFilter> rightFilters = mFiltersArray.get(carouselPosIdx.get());
            setSide(rightFilters, 1);
            mActiveMergedFilters.addAll(leftFilters);
            mActiveMergedFilters.addAll(rightFilters);
        } else {
            mActiveMergedFilters = mFiltersArray.get(carouselPosIdx.get());
            setSide(mActiveMergedFilters, -1f);
        }

        if (mActiveMergedFilters != null) {
            float threshVal = 0.07f;
            if (currPosn != -1 && !initAnimationComplete) {
                if (currPosn < targetPosn) {
                    float diff = Math.abs(targetPosn - currPosn);
                    if (diff > threshVal) {
                        currPosn = currPosn + threshVal;
                    } else {
                        currPosn = targetPosn;
                    }
                } else if (currPosn > targetPosn) {
                    float diff = Math.abs(targetPosn - currPosn);
                    if (diff > threshVal) {
                        currPosn = currPosn - threshVal;
                    } else {
                        currPosn = targetPosn;
                    }
                }
                super.setTouchScreenPosn(currPosn);
            } else {
                GPUImageCarouselGroup.super.setTouchScreenPosn(targetPosn);
            }

            int size = mActiveMergedFilters.size();
            int previousTexture = textureId;

            for (int i = 0; i < mActiveMergedFilters.size(); i++) {
                GPUImageFilter filter = mActiveMergedFilters.get(i);
                boolean isNotLast = i < size - 1;
                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                    GLES20.glClearColor(0, 0, 0, 0);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                }

                if (i == 0) {
                    GLES20.glClearColor(0, 0, 0, 0);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    filter.onDraw(previousTexture, cubeBuffer, textureBuffer);
                } else if (i == size - 1) {
                    if (filter instanceof GPUImageTwoInputFilter) {
                        if ((size % 2 == 0)) {
                            ((GPUImageTwoInputFilter) filter).setFlipped(true);
                        } else {
                            ((GPUImageTwoInputFilter) filter).setFlipped(false);
                        }
                    }
                    GLES20.glClearColor(0, 0, 0, 0);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    filter.onDraw(previousTexture, mGLCubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer);
                }
                else {
                    GLES20.glClearColor(0, 0, 0, 0);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                    filter.onDraw(previousTexture, mGLCubeBuffer, mGLTextureBuffer);
                }

                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    previousTexture = mFrameBufferTextures[i];
                }
            }
        }
    }

    private void setSide(List<GPUImageFilter> argFiltersList, float side) {
        for (GPUImageFilter filterSide : argFiltersList) {
            filterSide.setDrawSide(side);
        }
    }

    public void onTouchMoved(float newPos, int status) {
        targetPosn = newPos;
        mStatus.set(status);
        if (renderModeEvent.getRenderMode() == GLSurfaceView.RENDERMODE_CONTINUOUSLY) {
            processNewState();
        }
    }

    public void processNewState() {
        switch (mStatus.get()) {
            case GPUImageCarouselOnTouchListener.STATUS_SHIFT_LEFT:
                if (baselinePosn.get() == 0) {
                    moveToLeft();
                    reset();
                }
                break;

            case GPUImageCarouselOnTouchListener.STATUS_SHIFT_RIGHT:
                if (baselinePosn.get() == 1) {
                    moveToRight();
                    reset();
                }
                break;

            case GPUImageCarouselOnTouchListener.STATUS_MOVE_CANCEL:
                reset();
                break;

            case GPUImageCarouselOnTouchListener.STATUS_MOVE_LEFT:
                if (baselinePosn.get() == -1) {
                    currPosn = 1f;
                    baselinePosn.set(1);
                    renderModeEvent.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    EventBus.getDefault().post(renderModeEvent);
                    Log.d("Carousel", "BaseLinePos " + 1);

                }
                break;
            case GPUImageCarouselOnTouchListener.STATUS_MOVE_RIGHT:
                if (baselinePosn.get() == -1) { // Started moving
                    currPosn = 0f;
                    baselinePosn.set(0);
                    renderModeEvent.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    EventBus.getDefault().post(renderModeEvent);
                    Log.d("Carousel", "BaseLinePos " + 0);
                }
                break;

            case GPUImageCarouselOnTouchListener.STATUS_MOVE_FLING:
//                initAnimationComplete = true;
                break;
        }
    }

    private void reset() {
        currPosn = -1;
        baselinePosn.set(-1);
        touchPosition = -1;
        targetPosn = 0;
        initAnimationComplete = false;
        renderModeEvent.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        EventBus.getDefault().post(renderModeEvent);
    }


    private void moveToLeft() {
        carouselPosIdx.set(getLeftPosn());
        resetFiltersList(mFiltersArray.get(getRightPosn()));
        EventBus.getDefault().post(new OnFilterChanged(Integer.toString(carouselPosIdx.get()),false));
    }

    private int getLeftPosn() {
        if (carouselPosIdx.get() == 0) {
            return mFilters.size() - 1;
        } else {
            return carouselPosIdx.get() - 1;
        }
    }

    private void moveToRight() {
        carouselPosIdx.set(getRightPosn());
        resetFiltersList(mFiltersArray.get(getLeftPosn()));
        EventBus.getDefault().post(new OnFilterChanged(Integer.toString(carouselPosIdx.get()),true));
    }

    private int getRightPosn() {
        if (carouselPosIdx.get() == (mFilters.size() - 1)) {
            return 0;
        } else {
            return carouselPosIdx.get() + 1;
        }
    }

    private void resetFiltersList(ArrayList<GPUImageFilter> gpuImageFilters) {
        for (GPUImageFilter filter : gpuImageFilters) {
            filter.setDrawSide(1f);
        }
    }


    LinkedList<Long> times = new LinkedList<Long>() {{
        add(System.nanoTime());
    }};

    private final int MAX_SIZE = 100;
    private final double NANOS = 1000000000.0;

    /**
     * Calculates and returns frames per second
     */
    private double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        return difference > 0 ? times.size() / difference : 0.0;
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
    }
}
