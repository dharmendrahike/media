package com.example.dharmendraverma.myapplication.gpuimage;///*
// * Copyright (C) 2012 CyberAgent
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package jp.co.cyberagent.android.gpuimage;
//
//import android.opengl.GLES20;
//
//import java.nio.FloatBuffer;
//
//import jp.co.cyberagent.android.gpuimage.util.RawResourceReader;
//
///**
// * Applies a ColorMatrix to the image.
// */
//public class GPUImageColorMatrixFilter extends GPUImageFilter {
//
//    private float mIntensity;
//    private float[] mColorMatrix;
//    private int mColorMatrixLocation;
//    private int mIntensityLocation;
//
//    public GPUImageColorMatrixFilter() {
//        this(1.0f, new float[]{
//                1.0f, 0.0f, 0.0f, 0.0f,
//                0.0f, 1.0f, 0.0f, 0.0f,
//                0.0f, 0.0f, 1.0f, 0.0f,
//                0.0f, 0.0f, 0.0f, 1.0f
//        });
//    }
//
//    public GPUImageColorMatrixFilter(final float intensity, final float[] colorMatrix) {
//        super(NO_FILTER_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(R.raw.frag_colormatrix));
//        mIntensity = intensity;
//        mColorMatrix = colorMatrix;
//    }
//
//    @Override
//    public void onInit() {
//        super.onInit();
//        mColorMatrixLocation = GLES20.glGetUniformLocation(getProgram(), "colorMatrix");
//        mIntensityLocation = GLES20.glGetUniformLocation(getProgram(), "intensity");
//    }
//
//    @Override
//    public void onInitialized() {
//        super.onInitialized();
//        setIntensity(mIntensity);
//        setColorMatrix(mColorMatrix);
//    }
//
//    public void setIntensity(final float intensity) {
//        mIntensity = intensity;
//        setFloat(mIntensityLocation, intensity);
//    }
//
//    public void setColorMatrix(final float[] colorMatrix) {
//        mColorMatrix = colorMatrix;
//        setUniformMatrix4f(mColorMatrixLocation, colorMatrix);
//    }
//
//    @Override
//    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
//        super.onDraw(textureId, cubeBuffer, textureBuffer);
//    }
//}
