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

import android.annotation.SuppressLint;
import android.opengl.GLES20;


import com.example.dharmendraverma.myapplication.gpuimage.util.TextureRotationUtil;

import static com.example.dharmendraverma.myapplication.gpuimage.GPUImageRenderer.CUBE;

import static com.example.dharmendraverma.myapplication.gpuimage.util.TextureRotationUtil.TEXTURE_NO_ROTATION;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Resembles a filter that consists of multiple filters applied after each
 * other.
 */
public class GPUImageFilterGroup extends GPUImageFilter {

    protected List<GPUImageFilter> mFilters;
    protected Map<GPUImageFilter, String> mMergedFilters;
    protected int[] mFrameBuffers;
    protected int[] mFrameBufferTextures;

    protected final FloatBuffer mGLCubeBuffer;
    protected final FloatBuffer mGLTextureBuffer;
    protected final FloatBuffer mGLTextureFlipBuffer;
    protected String filterIdx;
    private int destinationFrameBuffer;

    /**
     * Instantiates a new GPUImageFilterGroup with no filters.
     */
    public GPUImageFilterGroup() {
        this(null);
    }

    /**
     * Instantiates a new GPUImageFilterGroup with the given filters.
     *
     * @param filters the filters which represent this filter
     */
    public GPUImageFilterGroup(List<GPUImageFilter> filters) {
        mFilters = filters;
        if (mFilters == null) {
            mFilters = new ArrayList<GPUImageFilter>() {
                @Override
                public GPUImageFilter get(int index) {
                    if (index > size() - 1) {
                        return super.get(index - (size() - 1) - 1);
                    } else if (index < 0) {
                        return super.get(size() - (index + 1));
                    }
                    return super.get(index);
                }
            };
        } else {
            updateMergedFilters();
        }

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

        float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        mGLTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureFlipBuffer.put(flipTexture).position(0);
    }

    public void addFilter(GPUImageFilter aFilter) {
        if (aFilter == null) {
            return;
        }
        mFilters.add(aFilter);
        filterIdx = aFilter.getTag();
        updateMergedFilters();
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        for (GPUImageFilter filter : mFilters) {
            filter.init();
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onDestroy()
     */
    @Override
    public void onDestroy() {
        destroyFramebuffers();
        for (GPUImageFilter filter : mFilters) {
            filter.destroy();
        }
        super.onDestroy();
    }

    private void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.cyberagent.android.gpuimage.GPUImageFilter#onOutputSizeChanged(int,
     * int)
     */
    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        if (mFrameBuffers != null) {
            destroyFramebuffers();
        }

        int size = mFilters.size();
        for (int i = 0; i < size; i++) {
            mFilters.get(i).onOutputSizeChanged(width, height);
        }

        if (mMergedFilters != null && mMergedFilters.size() > 0) {
            size = mMergedFilters.size();
            mFrameBuffers = new int[size - 1];
            mFrameBufferTextures = new int[size - 1];

            for (int i = 0; i < size - 1; i++) {
                GLES20.glGenFramebuffers(1, mFrameBuffers, i);
                GLES20.glGenTextures(1, mFrameBufferTextures, i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[i], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.cyberagent.android.gpuimage.GPUImageFilter#onDraw(int,
     * java.nio.FloatBuffer, java.nio.FloatBuffer)
     */
    @SuppressLint("WrongCall")
    @Override
    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        runPendingOnDrawTasks();
        if (!isInitialized() || mFrameBuffers == null || mFrameBufferTextures == null) {
            return;
        }
        if (mMergedFilters != null) {
            int size = mMergedFilters.size();
            int previousTexture = textureId;

            Set<Map.Entry<GPUImageFilter, String>> mergedFilters = mMergedFilters.entrySet();
            int mergedFiltersIdx = 0;
            for (Map.Entry<GPUImageFilter, String> filterEntry : mergedFilters) {
                long initial = System.currentTimeMillis();
                GPUImageFilter filter = filterEntry.getKey();
                boolean isNotLast = mergedFiltersIdx < size - 1;
                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[mergedFiltersIdx]);
                    GLES20.glClearColor(0, 0, 0, 0);
                }

                if (mergedFiltersIdx == 0) {
                    filter.onDraw(previousTexture, cubeBuffer, textureBuffer);
                } else if (mergedFiltersIdx == size - 1) {
                    filter.onDraw(previousTexture, mGLCubeBuffer, (size % 2 == 0) ? mGLTextureFlipBuffer : mGLTextureBuffer);
                } else {
                    filter.onDraw(previousTexture, mGLCubeBuffer, mGLTextureBuffer);
                }
                if(filter. getTag()!= null){
                    //Logger.d("atul",filter.getTag()+" = "+(System.currentTimeMillis() - initial));
                }else{
                   // Logger.d("atul","Unknown = "+(System.currentTimeMillis() - initial));
                }
                if (isNotLast) {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, destinationFrameBuffer);
                    previousTexture = mFrameBufferTextures[mergedFiltersIdx];
                }

                mergedFiltersIdx++;
            }
        }

    }

    public void setDestinationFrameBuffer(int destinationFrameBuffer){
        this.destinationFrameBuffer = destinationFrameBuffer;
    }
    /**
     * Gets the filters.
     *
     * @return the filters
     */
    public List<GPUImageFilter> getFilters() {
        return mFilters;
    }

    public Map<GPUImageFilter, String> getMergedFilters() {
        return mMergedFilters;
    }

    public void updateMergedFilters() {
        if (mFilters == null) {
            return;
        }

        if (mMergedFilters == null) {
            mMergedFilters = new LinkedHashMap<>();
        } else {
            mMergedFilters.clear();
        }

        Map<GPUImageFilter, String> filters;
        for (GPUImageFilter filter : mFilters) {
            if (filter instanceof GPUImageFilterGroup) {
                ((GPUImageFilterGroup) filter).updateMergedFilters();
                filters = ((GPUImageFilterGroup) filter).getMergedFilters();
                if (filters == null || filters.isEmpty())
                    continue;

                for (Map.Entry<GPUImageFilter, String> entry : filters.entrySet()) {
                    mMergedFilters.put(entry.getKey(), filter.getTag());
                }
                continue;
            }
            mMergedFilters.put(filter, filterIdx);
        }
    }

    public List<GPUImageFilter> getMergedFilters(GPUImageFilter filter) {
        if (filter == null) {
            return null;
        }

        ArrayList<GPUImageFilter> mergedFiltersList = new ArrayList<>();

        if (filter instanceof GPUImageFilterGroup) {
            Map<GPUImageFilter, String> mergedSubFilters = ((GPUImageFilterGroup) filter).getMergedFilters();
            mergedFiltersList.addAll(mergedSubFilters.keySet());
        } else {
            mergedFiltersList.add(filter);
        }

        return mergedFiltersList;
    }
}
