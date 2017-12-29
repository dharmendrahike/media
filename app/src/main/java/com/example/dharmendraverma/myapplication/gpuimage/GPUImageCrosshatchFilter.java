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
//
//import jp.co.cyberagent.android.gpuimage.util.RawResourceReader;
//
///**
// * crossHatchSpacing: The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
// * lineWidth: A relative width for the crosshatch lines. The default is 0.003.
// */
//public class GPUImageCrosshatchFilter extends GPUImageFilter {
//
//    private float mCrossHatchSpacing;
//    private int mCrossHatchSpacingLocation;
//    private float mLineWidth;
//    private int mLineWidthLocation;
//
//    /**
//     * Using default values of crossHatchSpacing: 0.03f and lineWidth: 0.003f.
//     */
//    public GPUImageCrosshatchFilter() {
//        this(0.03f, 0.003f);
//    }
//
//    public GPUImageCrosshatchFilter(float crossHatchSpacing, float lineWidth) {
//        super(NO_FILTER_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(R.raw.frag_crosshatch));
//        mCrossHatchSpacing = crossHatchSpacing;
//        mLineWidth = lineWidth;
//    }
//
//    @Override
//    public void onInit() {
//        super.onInit();
//        mCrossHatchSpacingLocation = GLES20.glGetUniformLocation(getProgram(), "crossHatchSpacing");
//        mLineWidthLocation = GLES20.glGetUniformLocation(getProgram(), "lineWidth");
//    }
//
//    @Override
//    public void onInitialized() {
//        super.onInitialized();
//        setCrossHatchSpacing(mCrossHatchSpacing);
//        setLineWidth(mLineWidth);
//    }
//
//    /**
//     * The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
//     *
//     * @param crossHatchSpacing default 0.03
//     */
//    public void setCrossHatchSpacing(final float crossHatchSpacing) {
//        float singlePixelSpacing;
//        if (getOutputWidth() != 0) {
//            singlePixelSpacing = 1.0f / (float) getOutputWidth();
//        } else {
//            singlePixelSpacing = 1.0f / 2048.0f;
//        }
//
//        if (crossHatchSpacing < singlePixelSpacing) {
//            mCrossHatchSpacing = singlePixelSpacing;
//        } else {
//            mCrossHatchSpacing = crossHatchSpacing;
//        }
//
//        setFloat(mCrossHatchSpacingLocation, mCrossHatchSpacing);
//    }
//
//    /**
//     * A relative width for the crosshatch lines. The default is 0.003.
//     *
//     * @param lineWidth default 0.003
//     */
//    public void setLineWidth(final float lineWidth) {
//        mLineWidth = lineWidth;
//        setFloat(mLineWidthLocation, mLineWidth);
//    }
//}
