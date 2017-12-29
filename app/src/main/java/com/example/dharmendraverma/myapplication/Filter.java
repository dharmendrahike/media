package com.example.dharmendraverma.myapplication;

/**
 * Created by dharmendraverma on 29/12/17.
 */

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.dharmendraverma.myapplication.gpuimage.OpenGlUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;


import javax.microedition.khronos.opengles.GL10;


public class Filter {


    public interface TakePictureListener {
        public void onPictureTaken(Bitmap bitmap) throws FileNotFoundException;
    }

    public static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    public static final int RENDER_TYPE_SURFACE_TEXTURE = 0;
    public static final int RENDER_TYPE_PREVIEW_BUFFER = 1;
    public static final int RENDER_TYPE_SAMPLER2D = 2;
    public static final int RENDER_TYPE_FACE_FILTER = 3;
    public static final int RENDER_TYPE_EDITOR = 4;
    public static final int RENDER_TYPE_BEAUTY = 5;



    protected int mRenderType = GL_TEXTURE_EXTERNAL_OES;

    public static final int FILTER_TYPE_OES = 1;
    public static final int FILTER_TYPE_PBUFFER = 2;
    public static final int FILTER_TYPE_BEAUTY_OES = 3;
    public static final int FILTER_TYPE_FACE = 4;
    public static final int FILTER_TYPE_EDITOR = 5;
    public static final int FILTER_TYPE_NIGHT_MODE = 6;
    public static final int FILTER_TYPE_QR_CODE = 7;

    protected int mFilterType = FILTER_TYPE_OES;


    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform lowp sampler2D texSampler;\n" +
            "varying highp vec2 v_texcoord;\n" +
            "void main() {\n" +
            "    vec4 color = texture2D(texSampler, v_texcoord);\n" +
            "    gl_FragColor = color;\n" +
            //"    gl_FragColor = vec4(1.0,0.0,0.0,1.0);\n"+
            "}";

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            //"uniform mat4 u_MVPMatrix;\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texcoord;\n" +
            "varying vec2 v_texcoord;\n" +
            "void main() {\n" +
            //    "gl_Position = u_MVPMatrix * a_position;\n"+
            "gl_Position =  a_position;\n" +
            "v_texcoord = a_texcoord;\n" +
            "}";

    public LinkedList<Runnable> mRunOnDraw = null;
    public LinkedList<Runnable> mRunOnDrawEnd = null;

    public LinkedList<Runnable> mRunOnDrawStartAlways = null;
    public LinkedList<Runnable> mRunOnDrawEndAlways = null;

    protected int mGLProgId;

    protected final int SHORT_SIZE_BYTES = 2;
    protected final int FLOAT_SIZE_BYTES = 4;

    protected final static String A_POSITION = "a_position";
    protected final static String A_TEXCOORD = "a_texcoord";
    //protected final static String U_MVPMATRIX = "u_MVPMatrix";
    protected final static String U_SAMPLER0 = "texSampler";
    protected final static String U_LUMINANCE_SAMPLER = "luminanceTexture";
    protected final static String U_CHROMINANCE_SAMPLER = "chrominanceTexture";


    protected int maPositionHandle;
    protected int maTextureHandle;
    protected int muMVPMatrixHandle;
    protected int muSampler0Handle;
    protected int mLumninanceSampler;
    protected int mChrominanceSampler;

    private float[] mProjMatrix = new float[16];

    private String mVertexShader;
    private String mFragmentShader;


    protected int[] mTextureConstant = {GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1};
    protected int[] mSamplers = null;

    protected boolean mIsInitialized = false;

    protected FilterObserver mObserver = null;

    public Filter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
        mIsInitialized = false;
    }

    public Filter(String vertexShader, String fragmentShader) {
        mRunOnDraw = new LinkedList<>();
        mRunOnDrawEnd = new LinkedList<>();

        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    void lazyInit(String vertexShader, String fragmentShader)
    {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public int getRenderType() {
        return mRenderType;
    }

    public void init() {
        onInit();

        onInitialized();
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public void onInit() {
        //super.onInit();
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);

        Matrix.orthoM(mProjMatrix, 0, -1, 1, 1, -1, 1, -1);
        maPositionHandle = GLES20.glGetAttribLocation(mGLProgId, A_POSITION);
        maTextureHandle = GLES20.glGetAttribLocation(mGLProgId, A_TEXCOORD);
        //muMVPMatrixHandle = GLES20.glGetUniformLocation(mGLProgId, U_MVPMATRIX);
        //This call will go inside respective filter type. TO FIX
        switch (getRenderType()) {
            case RENDER_TYPE_PREVIEW_BUFFER:
                mLumninanceSampler = GLES20.glGetUniformLocation(mGLProgId, U_LUMINANCE_SAMPLER);
                mChrominanceSampler = GLES20.glGetUniformLocation(mGLProgId, U_CHROMINANCE_SAMPLER);
                mSamplers = new int[2];
                mSamplers[0] = mLumninanceSampler;
                mSamplers[1] = mChrominanceSampler;
                break;
            case RENDER_TYPE_SURFACE_TEXTURE:
            case RENDER_TYPE_SAMPLER2D:
            case RENDER_TYPE_FACE_FILTER:
                muSampler0Handle = GLES20.glGetUniformLocation(mGLProgId, U_SAMPLER0);
                mSamplers = new int[1];
                mSamplers[0] = muSampler0Handle;
                break;
        }
    }

    public void onInitialized() {
        mIsInitialized = true;
    }



    public void setOutputSize(final int width, final int height) {

    }

    public void onDraw(final int[] textureArr, int elementBufferObjectId) {

    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {

    }

    public void onDraw(int elementBufferObjectId) {

    }

    public void onDraw(int vertexBufferObjectId, int elementBufferObjectId) {

    }

    public void onDraw(GL10 gl10)
    {

    }

    public void onDraw(final int[] textureArr, int vertexBufferObjectId, int elementBufferObjectId)  {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }

        for (int i = 0; i < textureArr.length; i++) {
            GLES20.glActiveTexture(mTextureConstant[i]);
            int textureType = GLES20.GL_TEXTURE_2D;
            switch (getRenderType()) {
                case RENDER_TYPE_SURFACE_TEXTURE:
                    textureType = GL_TEXTURE_EXTERNAL_OES;
                    break;
                case RENDER_TYPE_SAMPLER2D:
                case RENDER_TYPE_PREVIEW_BUFFER:
                    textureType = GLES20.GL_TEXTURE_2D;
                    break;
            }
            GLES20.glBindTexture(textureType, textureArr[i]);
            GLES20.glUniform1i(mSamplers[i], i);
        }

        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObjectId);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 5 * FLOAT_SIZE_BYTES, 0);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, true, 5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES);

        onDrawArraysPre();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, elementBufferObjectId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3 * SHORT_SIZE_BYTES, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(maTextureHandle);
        GLES20.glDisableVertexAttribArray(maPositionHandle);

        runPendingOnDrawTasksEnd();

        GLES20.glFinish();
        GLES20.glUseProgram(0);
    }

    public void onPreviewFrame(final byte[] bytes, final Camera camera, int orientation) {
    }

    public void setObserver(FilterObserver observer) {
        mObserver = observer;
    }

    protected void onDrawArraysPre() {
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    protected void runPendingOnDrawTasksEnd() {
        while (!mRunOnDrawEnd.isEmpty()) {
            mRunOnDrawEnd.removeFirst().run();
        }
    }

    protected void runAllAlways(ArrayList<Runnable> arr) {
        synchronized (arr) {
            for (Runnable item : arr) {
                item.run();
            }
        }
    }

    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.addLast(runnable);
        }
    }

    protected void runOnDrawStartAlways(final Runnable runnable) {
        synchronized (mRunOnDrawStartAlways) {
            mRunOnDrawStartAlways.addLast(runnable);
        }
    }

    protected void runOnDrawEndAlways(final Runnable runnable) {
        synchronized (mRunOnDrawEndAlways) {
            mRunOnDrawEndAlways.addLast(runnable);
        }
    }

    public int getProgram() {
        return mGLProgId;
    }


    public void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
        onDestroy();
    }

    public void onDestroy() {
    }

    public interface FilterObserver {
        public void onProcessingDone();
    }

    public void setParameters(Object... objects) {

    }

    protected void setFilterType(int type)
    {
        mFilterType = type;
    }

    public int getFilterType()
    {
        return  mFilterType;
    }

    public void setImageBitmap(Bitmap bitmap)
    {

    }

    public void onTouchMoved(float newPos, int status)
    {

    }
}

