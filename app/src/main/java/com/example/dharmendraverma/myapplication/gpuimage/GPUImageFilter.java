/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

public class GPUImageFilter{
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform lowp samplerExternalOES inputImageTexture;\n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            //"    gl_FragColor = vec4(1.0,0.0,0.0,1.0);\n"+
            "}";
    //
    /*
    * public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float enabledDrawSide;\n"+
            "uniform highp float touchPos;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     vec4 origin = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     if (enabledDrawSide != -1.0 && touchPos > 0.0 && touchPos < 1.0) {\n" +
            "         if (enabledDrawSide == 0.0) {\n" +
            "             if (textureCoordinate.x > touchPos) {\n" +
            "                 gl_FragColor = origin;\n" +
            "                 return;\n" +
            "             }\n" +
            "         } else {\n" +
            "         if (textureCoordinate.x < touchPos) {\n" +
            "             gl_FragColor = origin;\n" +
            "             return;\n" +
            "          }\n" +
            "          }\n" +
            "       }"+
            "     \n" +

            "}";
    *
    * */

    //FILTER PORT.
    private FloatBuffer mVertexBuffer = null;
    protected float[] mVerticesForSampler2D = {
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f };
    /*protected float[] mVerticesForSampler2D = {
            -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 0.0f, 1.0f };*/

    protected int mVertexBufferObjectId;

    protected final int SHORT_SIZE_BYTES = 2;
    protected final int FLOAT_SIZE_BYTES = 4;

    private ShortBuffer mIndexBuffer = null;
    protected short[] mIndices = {0, 1, 2, 1, 2, 3};
    protected int mElementBufferObjectId;

    //FILTER PORT
    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;
    protected int mOutputWidth;
    protected int mOutputHeight;
    private boolean mIsInitialized;
    private int mTouchPosHandle;
    private boolean mIsEndProcessing = false;
    protected static float touchPosition = -1f;
    private int mEnabledDrawSideHandle;
    protected volatile float drawSide = 1f;
    private String tag;

    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<Runnable>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public final void init() {
        onInit();
        mIsInitialized = true;
        onInitialized();
    }

    public void onInit() {
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);

        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        //mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId,
                "inputTextureCoordinate");
        mTouchPosHandle = GLES20.glGetUniformLocation(getProgram(), "touchPos");
        mEnabledDrawSideHandle = GLES20.glGetUniformLocation(getProgram(), "enabledDrawSide");
        mIsInitialized = true;


        int[] vboIds = new int[2];
        GLES20.glGenBuffers(2, vboIds, 0);
        mVertexBufferObjectId = vboIds[0];
        mElementBufferObjectId = vboIds[1];

        //Create texture buffer for filters.
        mVertexBuffer = ByteBuffer.allocateDirect(mVerticesForSampler2D.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVerticesForSampler2D).position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferObjectId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * FLOAT_SIZE_BYTES, mVertexBuffer, GLES20.GL_STATIC_DRAW);
        mVertexBuffer = null;


        mIndexBuffer = ByteBuffer.allocateDirect(mIndices.length * SHORT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndexBuffer.put(mIndices).position(0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBufferObjectId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity() * SHORT_SIZE_BYTES, mIndexBuffer, GLES20.GL_STATIC_DRAW);

        mIndexBuffer = null;



    }

    public void onInitialized() {
    }

    public final void destroy() {
        mIsInitialized = false;
        GLES20.glDeleteProgram(mGLProgId);
        onDestroy();
    }

    public void onDestroy() {
    }

    public void setTouchScreenPosn(float argPosn) {
        touchPosition = argPosn;
    }

    public void setDrawSide(float drawSide) {
        this.drawSide = drawSide;
    }

    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        /*GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        GLES20.glUniform1f(mTouchPosHandle, touchPosition);
        GLES20.glUniform1f(mEnabledDrawSideHandle, drawSide);
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);*/


        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return;
        }
        GLES20.glUniform1f(mTouchPosHandle, touchPosition);
        GLES20.glUniform1f(mEnabledDrawSideHandle, drawSide);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
       /* GLES20.glUniform1i(mGLUniformTexture, 0);*/


        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferObjectId);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 3, GLES20.GL_FLOAT, false, 5 * FLOAT_SIZE_BYTES, 0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, true, 5 * FLOAT_SIZE_BYTES, 3 * FLOAT_SIZE_BYTES);

        onDrawArraysPre();
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mElementBufferObjectId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3 * SHORT_SIZE_BYTES, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);


        GLES20.glFinish();
        GLES20.glUseProgram(0);


    }

    protected void onDrawArraysPre() {
    }

    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
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

    protected void setFloatVec2Array(final int location,final int count ,final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, count, FloatBuffer.wrap(arrayValue));
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

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public static String loadShader(String file, Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream ims = assetManager.open(file);

            String re = convertStreamToString(ims);
            ims.close();
            return re;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
