package com.example.dharmendraverma.myapplication.gpuimage;

import android.opengl.GLES20;

/**
 * Created by dharmendraverma on 04/04/17.
 */

public class GPUImageSkinToneBeautifyFilter extends GPUImageFilter {

    Float beautyLevel;
    Float brightLevel;
    Float toneLevel;

    public static final String kLFGPUImageBeautyFragmentShaderString = "" +
                    "varying vec2 textureCoordinate;\n"+
                    "\n"+
                    "uniform sampler2D inputImageTexture;\n"+
                    "\n"+
                    "uniform lowp vec2 singleStepOffset;\n"+
                    "uniform lowp vec4 params;\n"+
                    "uniform lowp float brightness;\n"+
                    "\n"+
                    "const lowp vec3 W = vec3(0.299, 0.587, 0.114);\n"+
                    "const lowp mat3 saturateMatrix = mat3(\n"+
                    "1.1102, -0.0598, -0.061,\n"+
                    "-0.0774, 1.0826, -0.1186,\n"+
                    "-0.0228, -0.0228, 1.1772);\n"+
                    "lowp vec2 blurCoordinates[24];\n"+
                    "\n"+
                    "lowp float hardLight(lowp float color) {\n"+
                    "if (color <= 0.5)\n"+
                    "color = color * color * 2.0;\n"+
                    "else\n"+
                    "color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n"+
                    "return color;\n"+
                    "}\n"+
                    "\n"+
                    "void main(){\n"+
                    "lowp vec3 centralColor = texture2D(inputImageTexture, textureCoordinate).rgb;\n"+
                    "blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);\n"+
                    "blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);\n"+
                    "blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);\n"+
                    "blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);\n"+
                    "blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);\n"+
                    "blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);\n"+
                    "blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);\n"+
                    "blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);\n"+
                    "blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);\n"+
                    "blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);\n"+
                    "blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);\n"+
                    "blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);\n"+
                    "blurCoordinates[12] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);\n"+
                    "blurCoordinates[13] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);\n"+
                    "blurCoordinates[14] = textureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);\n"+
                    "blurCoordinates[15] = textureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);\n"+
                    "blurCoordinates[16] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);\n"+
                    "blurCoordinates[17] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);\n"+
                    "blurCoordinates[18] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);\n"+
                    "blurCoordinates[19] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);\n"+
                    "blurCoordinates[20] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, -2.0);\n"+
                    "blurCoordinates[21] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, 2.0);\n"+
                    "blurCoordinates[22] = textureCoordinate.xy + singleStepOffset * vec2(2.0, -2.0);\n"+
                    "blurCoordinates[23] = textureCoordinate.xy + singleStepOffset * vec2(2.0, 2.0);\n"+
                    "\n"+
                    "lowp float sampleColor = centralColor.g * 22.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[0]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[1]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[2]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[3]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[4]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[5]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[6]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[7]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[8]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[9]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[10]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[11]).g;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[12]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[13]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[14]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[15]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[16]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[17]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[18]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[19]).g * 2.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[20]).g * 3.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[21]).g * 3.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[22]).g * 3.0;\n"+
                    "sampleColor += texture2D(inputImageTexture, blurCoordinates[23]).g * 3.0;\n"+
                    "sampleColor = sampleColor / 62.0;\n"+
                    "float highPass = centralColor.g - sampleColor + 0.5;\n"+
                    "for (int i = 0; i < 5; i++) {\n"+
                    "highPass = hardLight(highPass);\n"+
                    "}\n"+
                    "float lumance = dot(centralColor, W);\n"+
                    "float alpha = pow(lumance, params.r);\n"+
                    "if(params.b == 0.1 && params.a == 0.1) {\n"+
                    "gl_FragColor.rgb = centralColor;\n"+
                    "return;\n"+
                    "}\n"+
                    "vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;\n"+
                    "smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n"+
                    "smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n"+
                    "smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n"+
                    "lowp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n"+
                    "lowp vec3 bianliang = max(smoothColor, centralColor);\n"+
                    "lowp vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n"+
                    "lowp vec4 skinColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n"+
                    "skinColor.rgb = mix(skinColor.rgb, bianliang, alpha);\n"+
                    "skinColor.rgb = mix(skinColor.rgb, rouguang, params.b);\n"+
                    "vec3 satcolor = skinColor.rgb * saturateMatrix;\n"+
                    "gl_FragColor.rgb = mix(skinColor.rgb, satcolor, params.a);\n"+
                    " }";

    public GPUImageSkinToneBeautifyFilter() {
        super(NO_FILTER_VERTEX_SHADER,kLFGPUImageBeautyFragmentShaderString);
    }

    private int mUniformParamLocation;
    private int mUniformBrightnessLocation;
    private int mSingleStepOffSet;

    @Override
    public void onInit() {
        super.onInit();
        mUniformParamLocation = GLES20.glGetUniformLocation(getProgram(), "params");
        mUniformBrightnessLocation = GLES20.glGetUniformLocation(getProgram(), "brightness");
        mSingleStepOffSet = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        toneLevel = 0.5f;
        beautyLevel = 0.5f;
        brightLevel = 0.5f;
        setParams(beautyLevel,toneLevel);
        setBrightLevel(brightLevel);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffSet, new float[] {2.0f / w, 2.0f / h});
    }

    @Override
    public void onOutputSizeChanged(final int width, final int height) {
        super.onOutputSizeChanged(width, height);
        setTexelSize(width, height);
    }

    public void setBeautyLevel(Float beautyLevel) {
        this.beautyLevel = beautyLevel;
        setParams(this.beautyLevel,toneLevel);
    }

    public void setBrightLevel(Float brightLevel) {
        this.brightLevel = brightLevel;
        setFloat(mUniformBrightnessLocation,brightLevel);
    }

    public void setToneLevel(Float toneLevel) {
        this.toneLevel = toneLevel;
        setParams(beautyLevel,this.toneLevel);
    }

    public void setParams(Float beauty,Float tone) {
        float fBeautyParam[] =new float[4];
        fBeautyParam[0] = (float) (1.0 - 0.6 * beauty);
        fBeautyParam[1] = (float) (1.0 - 0.3 * beauty);
        fBeautyParam[2] = (float) (0.1 + 0.3 * tone);
        fBeautyParam[3] = (float) (0.1 + 0.3 * tone);
        setFloatVec4(mUniformParamLocation,fBeautyParam);
    }
}
