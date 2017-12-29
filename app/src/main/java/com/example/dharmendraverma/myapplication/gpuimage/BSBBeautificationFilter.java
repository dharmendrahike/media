package com.example.dharmendraverma.myapplication.gpuimage;

import android.opengl.GLES20;

/**
 * Created by dharmendraverma on 07/04/17.
 */

public class BSBBeautificationFilter extends GPUImageFilter {
        public static final String BILATERAL_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                "const int GAUSSIAN_SAMPLES = 9;\n" +
                "uniform vec2 singleStepOffset;\n" +
                "\n" +
                "uniform float texelWidth; \n" +
                "uniform float texelHeight; \n" +
                "\n" +
                "varying vec2 leftTextureCoordinate;\n" +
                "varying vec2 rightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 topTextureCoordinate;\n" +
                "varying vec2 topLeftTextureCoordinate;\n" +
                "varying vec2 topRightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 bottomTextureCoordinate;\n" +
                "varying vec2 bottomLeftTextureCoordinate;\n" +
                "varying vec2 bottomRightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +
                "void main()\n" +
                "{\n" +
                "	gl_Position = position;\n" +
                "	textureCoordinate = inputTextureCoordinate.xy;\n" +
                "	int multiplier = 0;\n" +
                "	vec2 blurStep;\n" +
                "	for (int i = 0; i < GAUSSIAN_SAMPLES; i++)\n" +
                "	{\n" +
                "		multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));\n" +
                "		blurStep = float(multiplier) * singleStepOffset;\n" +
                "		blurCoordinates[i] = inputTextureCoordinate.xy + blurStep;\n" +
                "	}\n" +
                "\n" +
                "    vec2 widthStep = vec2(texelWidth, 0.0);\n" +
                "    vec2 heightStep = vec2(0.0, texelHeight);\n" +
                "    vec2 widthHeightStep = vec2(texelWidth, texelHeight);\n" +
                "    vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);\n" +
                "\n" +
                "    leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" +
                "    rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" +
                "\n" +
                "    topTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" +
                "    topLeftTextureCoordinate = inputTextureCoordinate.xy - widthHeightStep;\n" +
                "    topRightTextureCoordinate = inputTextureCoordinate.xy + widthNegativeHeightStep;\n" +
                "\n" +
                "    bottomTextureCoordinate = inputTextureCoordinate.xy + heightStep;\n" +
                "    bottomLeftTextureCoordinate = inputTextureCoordinate.xy - widthNegativeHeightStep;\n" +
                "    bottomRightTextureCoordinate = inputTextureCoordinate.xy + widthHeightStep;\n" +
                "}";

        public static final String BILATERAL_FRAGMENT_SHADER = "" +
                "    precision mediump float;\n" +
                "    \n" +
                "    const int GAUSSIAN_SAMPLES = 9;\n" +
                "    varying vec2 textureCoordinate;\n" +
                "    vec4 bilateral;\n"+
                "    vec4 canny;\n"+
                "    \n"+
                "    varying vec2 leftTextureCoordinate;\n" +
                "    varying vec2 rightTextureCoordinate;\n" +
                "    \n" +
                "    varying vec2 topTextureCoordinate;\n" +
                "    varying vec2 topLeftTextureCoordinate;\n" +
                "    varying vec2 topRightTextureCoordinate;\n" +
                "    \n" +
                "    varying vec2 bottomTextureCoordinate;\n" +
                "    varying vec2 bottomLeftTextureCoordinate;\n" +
                "    varying vec2 bottomRightTextureCoordinate;\n" +
                "    \n"+
                "    varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];\n" +
                "    uniform sampler2D inputImageTexture;\n" +
                "    uniform float distanceNormalizationFactor;\n" +
                "    uniform mediump float smoothDegree;\n"+
                "    \n"+
                "    void main()\n" +
                "     {\n" +
                "     lowp vec4 centralColor;\n" +
                "     lowp float gaussianWeightTotal;\n" +
                "     lowp vec4 sum;\n" +
                "     lowp vec4 sampleColor;\n" +
                "     lowp float distanceFromCentralColor;\n" +
                "     lowp float gaussianWeight;\n" +
                "     \n" +
                "     centralColor = texture2D(inputImageTexture, blurCoordinates[4]);\n" +
                "     gaussianWeightTotal = 0.18;\n" +
                "     sum = centralColor * 0.18;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[0]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[1]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[2]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[3]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[5]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[6]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[7]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, blurCoordinates[8]);\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     bilateral= sum / gaussianWeightTotal;\n" +
                "     \n"+
                "    float bottomLeftIntensity = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
                "    float topRightIntensity = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
                "    float topLeftIntensity = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n" +
                "    float bottomRightIntensity = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\n" +
                "    float leftIntensity = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
                "    float rightIntensity = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
                "    float bottomIntensity = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
                "    float topIntensity = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
                "    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "    \n" +
                "    float mag = length(vec2(h, v));\n" +
                "    \n" +
                "    canny = vec4(vec3(mag), 1.0);\n" +
                "    vec4 origin = texture2D(inputImageTexture,textureCoordinate);\n"+
                "    vec4 smooth;\n"+
                "    float r = origin.r;\n"+
                "    float g = origin.g;\n"+
                "    float b = origin.b;\n"+
                "    \n"+
                "    if (canny.r < 0.2 && r > 0.3725 && g > 0.1568 && b > 0.0784 && r > b && (max(max(r, g), b) - min(min(r, g), b)) > 0.0588 && abs(r-g) > 0.0588) {\n"+
                "    smooth = (1.0 - smoothDegree) * (origin - bilateral) + bilateral;\n"+
                "    }\n"+
                "    else {\n"+
                "    smooth = origin;\n"+
                "    }\n"+
                "    smooth.r = log(1.0 + 0.2 * smooth.r)/log(1.2);\n"+
                "    smooth.g = log(1.0 + 0.2 * smooth.g)/log(1.2);\n"+
                "    smooth.b = log(1.0 + 0.2 * smooth.b)/log(1.2);\n"+
                "    gl_FragColor = smooth;\n"+
                "    }";


        private float mDistanceNormalizationFactor;
        private int mDisFactorLocation;
        private int mSingleStepOffsetLocation;
        private int mUniformTexelWidthLocation;
        private int mUniformTexelHeightLocation;
        private int mUniformSmoothnessLocation;
        private boolean mHasOverriddenImageSizeFactor = false;
        private float mTexelWidth;
        private float mTexelHeight;
        private float mLineSize = 1.0f;
        private float mSmoothness;
        private static final Float BSBBeautifyFilterDefaultNormalizationFactor = 4.0f;


    public BSBBeautificationFilter (){
            this(BSBBeautifyFilterDefaultNormalizationFactor);
        }

        public BSBBeautificationFilter(final float distanceNormalizationFactor) {
            super(BILATERAL_VERTEX_SHADER,BILATERAL_FRAGMENT_SHADER);
            mDistanceNormalizationFactor = distanceNormalizationFactor;
        }

        @Override
        public void onInit() {
            super.onInit();
            mDisFactorLocation = GLES20.glGetUniformLocation(getProgram(), "distanceNormalizationFactor");
            mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
            mUniformTexelWidthLocation = GLES20.glGetUniformLocation(getProgram(), "texelWidth");
            mUniformTexelHeightLocation = GLES20.glGetUniformLocation(getProgram(), "texelHeight");
            mUniformSmoothnessLocation =  GLES20.glGetUniformLocation(getProgram(), "smoothDegree");
            if (mTexelWidth != 0) {
                updateTexelValues();
            }
        }

        @Override
        public void onInitialized() {
            super.onInitialized();
            setDistanceNormalizationFactor(mDistanceNormalizationFactor);
        }

        public void setDistanceNormalizationFactor(final float newValue) {
            mDistanceNormalizationFactor = newValue;
            setFloat(mDisFactorLocation, newValue);
        }

        public void setSmoothnessFactor(float smoothnessFactor){
            mSmoothness=smoothnessFactor;
            setFloat(mUniformSmoothnessLocation,mSmoothness);
        }

        private void setTexelSize(final float w, final float h) {
            setFloatVec2(mSingleStepOffsetLocation, new float[] {1.0f / w, 1.0f / h});
        }

        @Override
        public void onOutputSizeChanged(final int width, final int height) {
            super.onOutputSizeChanged(width, height);
            if (!mHasOverriddenImageSizeFactor) {
                setLineSize(mLineSize);
            }
            setTexelSize(width, height);
        }

        public void setLineSize(final float size) {
        mLineSize = size;
        mTexelWidth = size / getOutputWidth();
        mTexelHeight = size / getOutputHeight();
        updateTexelValues();
        }

        private void updateTexelValues() {
        setFloat(mUniformTexelWidthLocation, mTexelWidth);
        setFloat(mUniformTexelHeightLocation, mTexelHeight);
        }
    }
