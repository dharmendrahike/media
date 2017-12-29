package com.example.dharmendraverma.myapplication.gpuimage;

import android.opengl.GLES20;

/**
 * Created by dharmendraverma on 05/05/17.
 */

public class GPUImageBeautifyFilter extends GPUImageFilter {

    public static final String GPUImageBeautifyVertexShaderString = "" +
            "   attribute vec4 position;\n" +
            "   attribute vec4 inputTextureCoordinate;\n" +
            "   const int GAUSSIAN_SAMPLES = 9;\n" +
            "   uniform vec2 singleStepOffsetLocation;\n" +
            "   \n" +
            "   varying vec2 leftTextureCoordinate;\n" +
            "   varying vec2 rightTextureCoordinate;\n" +
            "   \n" +
            "   varying vec2 topTextureCoordinate;\n" +
            "   varying vec2 topRightTextureCoordinate;\n" +
            "   \n" +
            "   varying vec2 bottomTextureCoordinate;\n" +
            "   varying vec2 bottomLeftTextureCoordinate;\n" +
            "   \n" +
            "   varying vec2 textureCoordinate;\n" +
            "void main()\n" +
            "   {\n" +
            "	gl_Position = position;\n" +
            "	textureCoordinate = inputTextureCoordinate.xy;\n" +
            "\n" +
            "    vec2 widthStep = vec2(singleStepOffsetLocation.x, 0.0);\n" +
            "    vec2 heightStep = vec2(0.0, singleStepOffsetLocation.y);\n" +
            "    vec2 widthNegativeHeightStep = vec2(singleStepOffsetLocation.x, -singleStepOffsetLocation.y);\n" +
            "\n" +
            "    leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" +
            "    rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" +
            "\n" +
            "    topTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" +
            "    topRightTextureCoordinate = inputTextureCoordinate.xy + widthNegativeHeightStep;\n" +
            "\n" +
            "    bottomTextureCoordinate = inputTextureCoordinate.xy + heightStep;\n" +
            "    bottomLeftTextureCoordinate = inputTextureCoordinate.xy - widthNegativeHeightStep;\n" +
            "}";

        public static final String GPUImageBeautifyFragmentShaderString= "" +
                "    precision mediump float;\n" +
                "    \n" +
                "    varying lowp vec2 textureCoordinate;\n"+
                "    \n"+
                "    uniform sampler2D inputImageTexture;\n"+
                "    \n"+
                "    const int GAUSSIAN_SAMPLES = 9;\n" +
                "    vec4 bilateral;\n"+
                "    vec4 canny;\n"+
                "    \n"+
                "    varying vec2 leftTextureCoordinate;\n" +
                "    varying vec2 rightTextureCoordinate;\n" +
                "    \n" +
                "    varying vec2 topTextureCoordinate;\n" +
                "    varying vec2 topRightTextureCoordinate;\n" +
                "    \n" +
                "    varying vec2 bottomTextureCoordinate;\n" +
                "    varying vec2 bottomLeftTextureCoordinate;\n" +
                "    \n"+
                "    uniform float distanceNormalizationFactor;\n" +
                "    uniform mediump float smoothDegree;\n"+
                "    \n"+
                "    uniform lowp vec2 singleStepOffset;\n"+
                "    uniform lowp vec4 params;\n"+
                "    uniform lowp float brightness;\n" +
                "    uniform lowp float saturation;\n" +
                "    uniform vec2 blurStep[GAUSSIAN_SAMPLES];\n"+
                "    const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "    \n"+
                "    const lowp vec3 W = vec3(0.299, 0.587, 0.114);\n"+
                "    const lowp mat3 saturateMatrix = mat3(\n"+
                "       1.1102, -0.0598, -0.061,\n"+
                "       -0.0774, 1.0826, -0.1186,\n"+
                "       -0.0228, -0.0228, 1.1772);\n"+
                "    lowp vec2 blurCoordinatesSkin[24];\n"+
                "    \n"+
                "    lowp float hardLight(lowp float color) {\n"+
                "       if (color <= 0.5)\n"+
                "       color = color * color * 2.0;\n"+
                "       else\n"+
                "       color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n"+
                "       return color;\n"+
                "    }\n"+
                "    \n"+
                "   void main(){\n"+
                "     lowp vec4 centralColor;\n" +
                "     lowp float gaussianWeightTotal;\n" +
                "     lowp vec4 sum;\n" +
                "     lowp vec4 sampleColor;\n" +
                "     lowp float distanceFromCentralColor;\n" +
                "     lowp float gaussianWeight;\n" +
                "     \n" +
                "     centralColor = texture2D(inputImageTexture,textureCoordinate + (blurStep[4]) );\n" +
                "     gaussianWeightTotal = 0.18;\n" +
                "     sum = centralColor * 0.18;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture,textureCoordinate + (blurStep[0]) );\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[1]));\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[2]));\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[3]));\n" +
                "     float topLeftIntensity = sampleColor.r;\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[5]) );\n" +
                "     float bottomRightIntensity = sampleColor.r;\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.15 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[6]) );\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.12 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[7]) );\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     \n" +
                "     sampleColor = texture2D(inputImageTexture, textureCoordinate + (blurStep[8]) );\n" +
                "     distanceFromCentralColor = min(distance(centralColor, sampleColor) * distanceNormalizationFactor, 1.0);\n" +
                "     gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor);\n" +
                "     gaussianWeightTotal += gaussianWeight;\n" +
                "     sum += sampleColor * gaussianWeight;\n" +
                "     bilateral= sum / gaussianWeightTotal;\n" +
                "     \n"+
              //  "    float bottomLeftIntensity = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
              //  "    float topRightIntensity = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
              //  "    float leftIntensity = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
              //  "    float rightIntensity = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
              //  "    float bottomIntensity = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
              //  "    float topIntensity = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
              //  "    float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
              //  "    float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "    \n" +
              //  "    float mag = length(vec2(h, v));\n" +
                "    \n" +
               // "    canny = vec4(vec3(mag), 1.0);\n" +
                "    vec4 origin = texture2D(inputImageTexture,textureCoordinate);\n"+
                "    vec4 smooth;\n"+
                "    float r = origin.r;\n"+
                "    float g = origin.g;\n"+
                "    float b = origin.b;\n"+
                "    \n"+
                "    if (r > 0.3725 && g > 0.1568 && b > 0.0784 && r > b && (max(max(r, g), b) - min(min(r, g), b)) > 0.0588 && abs(r-g) > 0.0588) {\n"+
                "    smooth = (1.0 - smoothDegree) * (origin - bilateral) + bilateral;\n"+
                "    }\n"+
                "    else {\n"+
                "    smooth = origin;\n"+
                "    }\n"+
                "    smooth.r = log(1.0 + 0.2 * smooth.r)/log(1.2);\n"+
                "    smooth.g = log(1.0 + 0.2 * smooth.g)/log(1.2);\n"+
                "    smooth.b = log(1.0 + 0.2 * smooth.b)/log(1.2);\n"+
                "    lowp vec4 centralColorSkin = smooth;\n"+
                "    blurCoordinatesSkin[0] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, -10.0);\n"+
                "    blurCoordinatesSkin[1] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, 10.0);\n"+
                "    blurCoordinatesSkin[2] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-10.0, 0.0);\n"+
                "    blurCoordinatesSkin[3] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(10.0, 0.0);\n"+
                "    blurCoordinatesSkin[4] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, -8.0);\n"+
                "    blurCoordinatesSkin[5] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, 8.0);\n"+
                "    blurCoordinatesSkin[6] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, 8.0);\n"+
                "    blurCoordinatesSkin[7] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, -8.0);\n"+
                "    blurCoordinatesSkin[8] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(8.0, -5.0);\n"+
                "    blurCoordinatesSkin[9] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(8.0, 5.0);\n"+
                "    blurCoordinatesSkin[10] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-8.0, 5.0);\n"+
                "    blurCoordinatesSkin[11] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-8.0, -5.0);\n"+
               // "    blurCoordinatesSkin[12] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, -6.0);\n"+
               // "    blurCoordinatesSkin[13] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, 6.0);\n"+
               // "    blurCoordinatesSkin[14] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(6.0, 0.0);\n"+
               // "    blurCoordinatesSkin[15] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-6.0, 0.0);\n"+
                "    blurCoordinatesSkin[16] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, -4.0);\n"+
                "    blurCoordinatesSkin[17] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, 4.0);\n"+
                "    blurCoordinatesSkin[18] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, -4.0);\n"+
                "    blurCoordinatesSkin[19] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, 4.0);\n"+
               // "    blurCoordinatesSkin[20] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-2.0, -2.0);\n"+
               // "    blurCoordinatesSkin[21] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-2.0, 2.0);\n"+
              //  "    blurCoordinatesSkin[22] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(2.0, -2.0);\n"+
              //  "    blurCoordinatesSkin[23] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(2.0, 2.0);\n"+
                "    \n"+
                "    lowp float sampleColorSkin = centralColorSkin.g * 15.0;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[0]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[1]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[2]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[3]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[4]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[5]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[6]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[7]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[8]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[9]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[10]).g;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[11]).g;\n"+
               // "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[12]).g * 2.0;\n"+
               // "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[13]).g * 2.0;\n"+
               // "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[14]).g * 2.0;\n"+
               // "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[15]).g * 2.0;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[16]).g * 2.0;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[17]).g * 2.0;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[18]).g * 2.0;\n"+
                "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[19]).g * 2.0;\n"+
              //  "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[20]).g * 3.0;\n"+
              //  "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[21]).g * 3.0;\n"+
               // "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[22]).g * 3.0;\n"+
              //  "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[23]).g * 3.0;\n"+
                "    sampleColorSkin = sampleColorSkin / 35.0;\n"+
                "    float highPass = centralColorSkin.g - sampleColorSkin + 0.5;\n"+
                "    for (int i = 0; i < 5; i++) {\n"+
                "       highPass = hardLight(highPass);\n"+
                "    }\n"+
                "    float lumance = dot(centralColorSkin.rgb, W);\n"+
                "    lowp float luminance = dot(centralColorSkin.rgb, luminanceWeighting);\n" +
                "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
                "    float alpha = pow(lumance, params.r);\n"+
                "    if(params.b == 0.1 && params.a == 0.1) {\n"+
                "    gl_FragColor = centralColorSkin;\n"+
                "    }else{\n"+
                "    vec3 smoothColor = centralColorSkin.rgb + (centralColorSkin.rgb - vec3(highPass)) * alpha * 0.1;\n"+
                "    smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n"+
                "    smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n"+
                "    smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n"+
                "    lowp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0) - centralColorSkin.rgb);\n"+
                "    lowp vec3 bianliang = max(smoothColor, centralColorSkin.rgb);\n"+
                "    lowp vec3 rouguang = 2.0 * centralColorSkin.rgb * smoothColor + centralColorSkin.rgb * centralColorSkin.rgb - 2.0 * centralColorSkin.rgb * centralColorSkin.rgb * smoothColor;\n"+
                "    lowp vec4 skinColor = vec4(mix(centralColorSkin.rgb, lvse, alpha), 1.0);\n"+
                "    skinColor.rgb = mix(skinColor.rgb, bianliang, alpha);\n"+
                "    skinColor.rgb = mix(skinColor.rgb, rouguang, params.b);\n"+
                "    vec3 satcolor = skinColor.rgb * saturateMatrix;\n"+
                "    lowp vec3 brightColor  = mix(skinColor.rgb, satcolor, params.a) + vec3(brightness);\n"+
                "    gl_FragColor = vec4(mix(greyScaleColor, brightColor.rgb, saturation), centralColorSkin.a);\n" +
                "}" +
                "    }";

        private int mUniformParamLocation;
        private int mSingleStepOffSet;
        private int mSingleStepOffSetLocation;
        private int mBrightnessLocation;
        private float mBrightness;
        private float beautyLevel;
        private float beautifyLevel;
        private float toneLevel;
        private int mSaturationLocation;
        private float mSaturation;
        private static final Float BeautifyFilterDefaultNormalizationFactor = 4.0f;
        private static final Float BeautifyFilterDefaultIntensity = 0.18f;
        private float mDistanceNormalizationFactor;
        private int mDisFactorLocation;
        private int mUniformSmoothnessLocation;
        private float mSmoothness;
        private int mUniformBlurCoordinatesLocation;

        private final int GAUSSIAN_SAMPLES = 9;
        private float mBlurStep[] = new float[GAUSSIAN_SAMPLES*2];
        private float mPixelStep[] = new float[2];

    public GPUImageBeautifyFilter() {
            this(BeautifyFilterDefaultNormalizationFactor);
        }

        public GPUImageBeautifyFilter(final float distanceNormalizationFactor){
            super(GPUImageBeautifyVertexShaderString,GPUImageBeautifyFragmentShaderString);
            beautyLevel = BeautifyFilterDefaultIntensity;
            mSmoothness = beautyLevel;
            mBrightness =(float) (0.0 + beautyLevel / 8.0);
            mSaturation = (float)(1.0 + beautyLevel / 8.0);
            toneLevel =(float) (beautyLevel/4.0);
            beautifyLevel = toneLevel;
            mDistanceNormalizationFactor=distanceNormalizationFactor;
        }

        @Override
        public void onInit() {
            super.onInit();
            mUniformParamLocation = GLES20.glGetUniformLocation(getProgram(), "params");
            mSingleStepOffSet = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
            mSingleStepOffSetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffsetLocation");
            mBrightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
            mSaturationLocation = GLES20.glGetUniformLocation(getProgram(), "saturation");
            mDisFactorLocation = GLES20.glGetUniformLocation(getProgram(), "distanceNormalizationFactor");
            mUniformSmoothnessLocation =  GLES20.glGetUniformLocation(getProgram(), "smoothDegree");
            mUniformBlurCoordinatesLocation = GLES20.glGetUniformLocation(getProgram(),"blurStep");

        }

        @Override
        public void onInitialized() {
            super.onInitialized();
            setDistanceNormalizationFactor(mDistanceNormalizationFactor);
            setParams(beautyLevel,toneLevel);
            setBrightness(mBrightness);
            setSaturation(mSaturation);
            setSmoothnessFactor(mSmoothness);
            setBeautifyLevel(beautyLevel);
        }


     @Override
        public void onOutputSizeChanged(final int width, final int height) {
            super.onOutputSizeChanged(width, height);
            setTexelSize(width, height);
            /* Re Calculate Blur Coordinate on StepSize Change */
            InitBlurCoordinates();
         }

        public void setBeautyLevel(Float beautyLevel) {
            this.beautyLevel = beautyLevel;
            toneLevel = (float) Math.max(0.01f, beautyLevel / 4.0f);
            mSmoothness = (float) Math.max(beautyLevel,0.01f);
            mBrightness = 0.0f;
            this.beautifyLevel = toneLevel;

            setSmoothnessFactor(mSmoothness);
            setBrightness(mBrightness);
            setBeautifyLevel(beautifyLevel);
            setToneLevel(toneLevel);
        }

        public void setDistanceNormalizationFactor(final float newValue) {
            mDistanceNormalizationFactor = newValue;
            setFloat(mDisFactorLocation, newValue);
        }
        private void setTexelSize(final float w, final float h) {
            mPixelStep[0] = 1.0f / w;
            mPixelStep[1] = 1.0f / h;
            setFloatVec2(mSingleStepOffSet, new float[] {mPixelStep[0],  mPixelStep[1]});
            setFloatVec2(mSingleStepOffSetLocation, new float[] {mPixelStep[0],  mPixelStep[1]});
        }

        public void setSmoothnessFactor(float smoothnessFactor){
            mSmoothness=smoothnessFactor;
            setFloat(mUniformSmoothnessLocation,mSmoothness);
        }

        public void setBeautifyLevel(Float beautifyLevel) {
            this.beautifyLevel = beautifyLevel;
            setParams(this.beautifyLevel,toneLevel);
        }

        public void setToneLevel(Float toneLevel) {
            this.toneLevel = toneLevel;
            setParams(beautifyLevel,this.toneLevel);
        }

        public void setBrightness(final float brightness) {
            mBrightness = brightness;
            setFloat(mBrightnessLocation, mBrightness);
        }

        public void setSaturation(final float saturation) {
            mSaturation = saturation;
            setFloat(mSaturationLocation, mSaturation);
        }

        private void InitBlurCoordinates()
        {
            float multiplier ;
            for (int i = 0; i < GAUSSIAN_SAMPLES; i++) {
                multiplier = (i - ((GAUSSIAN_SAMPLES - 1.0f) / 2.0f));
                mBlurStep[i] = multiplier *  mPixelStep[0];
                mBlurStep[++i] = multiplier * mPixelStep[1];
            }

            setFloatVec2Array(mUniformBlurCoordinatesLocation ,GAUSSIAN_SAMPLES, mBlurStep);
        }

        public void setParams(Float beautifyLevel,Float tone) {
            float fBeautyParam[] =new float[4];
            fBeautyParam[0] = (float) (1.0 - 0.6 * beautifyLevel);
            fBeautyParam[1] = (float) (1.0 - 0.3 * beautifyLevel);
            fBeautyParam[2] = (float) (0.1 + 0.3 * tone);
            fBeautyParam[3] = (float) (0.1 + 0.3 * tone);
            setFloatVec4(mUniformParamLocation,fBeautyParam);
        }
}
