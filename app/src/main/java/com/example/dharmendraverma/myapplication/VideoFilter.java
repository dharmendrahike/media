package com.example.dharmendraverma.myapplication;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.DisplayMetrics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


import com.example.dharmendraverma.myapplication.gpuimage.GPUBeautificationFilter;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageCarouselGroup;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageFilter;
import com.example.dharmendraverma.myapplication.gpuimage.GPUImageRenderer;
import com.example.dharmendraverma.myapplication.gpuimage.IFBrannanFilterOptimized;
import com.example.dharmendraverma.myapplication.gpuimage.IFFadeOut;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;


import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

/**
 * Created by Neo on 28/11/17.
 */

public class VideoFilter extends FilterOES {

    private GPUImageCarouselGroup mFilter;
    int mSurfaceWidth;
    int mSurfaceHeight;
    Context mContext;

    public VideoFilter(int surfaceWidth,int surfaceHeight)
    {
        mRenderType = FilterOES.RENDER_TYPE_EDITOR;
        mFilterType = FILTER_TYPE_EDITOR;
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;
    }

    public void init() {
        mFilter = getFilter(mContext);
        mFilter.init();

        mFilter.onOutputSizeChanged(mSurfaceWidth,mSurfaceHeight);
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void setOutputSize(int width,int height)
    {

    }

    public void onOutputSizeChanged(int width, int height)
    {
    }

    public static GPUImageCarouselGroup getFilter(Context context) {

        List<GPUImageFilter> filterList = new ArrayList<>();
        filterList.add(new GPUImageFilter());
        //filterList.add(new GPUImageFilter());
        //filterList.add(new GPUImageFilter());

        //Old filters.
        /*int[] toneFilterResArray = new int[]{
                R.raw.polaroid,
                R.raw.chuski,
                R.raw.lofi,
                R.raw.azure,
                R.raw.jalebi,
                R.raw.clarendon_pop
        };
        //Add any new filters here.
        for (int i = 0; i < toneFilterResArray.length; i++) {
            GPUImageToneCurveFilter toneFilter = new GPUImageToneCurveFilter();
            toneFilter.setFromCurveFileInputStream(
                    HikeMessengerApp.getInstance().getApplicationContext().getResources().openRawResource(toneFilterResArray[i])); //polaroid
            toneFilter.setTag("FilterId: " + toneFilterResArray[i]);
            filterList.add(toneFilter);
        }
        filterList.add(2, new GPUImageGrayscaleFilter());*/
        filterList.add(new GPUBeautificationFilter(context));
        filterList.add(new IFBrannanFilterOptimized(context));
        filterList.add(new IFFadeOut(context));
        //filterList.add(new IFInkwellFilter(HikeMessengerApp.getInstance().getApplicationContext()));
        //filterList.add(new IFLomoFilter(HikeMessengerApp.getInstance().getApplicationContext()));
        //filterList.add(new IFLordKelvinFilter(HikeMessengerApp.getInstance().getApplicationContext()));
        //filterList.add(new IFValenciaFilter(HikeMessengerApp.getInstance().getApplicationContext()));
        //filterList.add(new IFXprollFilter(HikeMessengerApp.getInstance().getApplicationContext()));

        //filterList.add(new IF1977Filter(HikeMessengerApp.getInstance().getApplicationContext())); .
        //filterList.add(new IFBrannanFilter(HikeMessengerApp.getInstance().getApplicationContext())); //Done .yes.
        //filterList.add(new IFEarlybirdFilter(HikeMessengerApp.getInstance().getApplicationContext())); //Done ..yes
        //These filter doesn't exist.But if we plan to include them in future this is the size associated with them.
        //filterList.add(new IFAmaroFilter(HikeMessengerApp.getInstance().getApplicationContext())); //1.8 mbs
        //filterList.add(new IFHefeFilter(HikeMessengerApp.getInstance().getApplicationContext())); //1.6 mbs
        //filterList.add(new IFHudsonFilter(HikeMessengerApp.getInstance().getApplicationContext())); //1.7 mbs
        //filterList.add(new IFRiseFilter(HikeMessengerApp.getInstance().getApplicationContext())); //1.8 mbs
        //filterList.add(new IFSierraFilter(HikeMessengerApp.getInstance().getApplicationContext())); //1.6 mbs
        //filterList.add(new IFSutroFilter(HikeMessengerApp.getInstance().getApplicationContext())); //2.4 mbs

        return new GPUImageCarouselGroup(filterList);
    }

    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer)
    {
        runPendingOnDrawTasks();
        mFilter.onDraw(textureId,cubeBuffer,textureBuffer);
        runPendingOnDrawTasksEnd();
    }


    public void onTouchMoved(float newPos, int status)
    {
        mFilter.onTouchMoved(newPos,status);
    }

    public void setTouchScreenPosn(float argPosn)
    {
        mFilter.setTouchScreenPosn(argPosn);
    }

    public void destroy() {
        if (mFilter != null) {
            mFilter.onDestroy();
        }
    }

    public void onDestroy()
    {

    }

}



