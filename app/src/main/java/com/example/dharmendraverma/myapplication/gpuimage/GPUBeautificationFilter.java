package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;

/**
 * Modified by pradeepc on 12/09/17.
 */

public class GPUBeautificationFilter extends IFImageFilter {
    public static final String FragmentShaderString= "" +
            "    precision mediump float;\n" +
            "    uniform sampler2D inputImageTexture;\n"+
            "    varying highp vec2 textureCoordinate;\n" +
            "    \n" +
            "    const lowp float smoothDegree = 0.25;\n"+
            "    const lowp float saturation = 1.0225;\n" +
            "    const lowp float distanceNormalizationFactor = 4.0;\n" +
            "    \n"+
            "    const mediump vec4 params = vec4(0.85, 0.925, 0.175, 0.175);\n"+
            "    const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "    const mediump vec3 W = vec3(0.299, 0.587, 0.114);\n"+
            "    const mediump vec2 singleStepOffset = vec2(0.001,0.001);\n"+

            "    const lowp mat3 saturateMatrix = mat3(\n"+
            "       1.1102, -0.0598, -0.061,\n"+
            "       -0.0774, 1.0826, -0.1186,\n"+
            "       -0.0228, -0.0228, 1.1772);\n"+

            "    lowp vec2 blurCoordinatesSkin[12];\n"+
            "    uniform highp float touchPos;\n" +
            "    uniform highp float enabledDrawSide;\n"+
            "    vec4 bilateral;\n"+

            "    lowp float hardLight(lowp float color) {\n"+
            "       if (color <= 0.5)\n"+
            "       color = color * color * 2.0;\n"+
            "       else\n"+
            "       color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n"+
            "       return color;\n"+
            "    }\n"+

            "   void main(){\n"+
            "     vec4 origin = texture2D(inputImageTexture,textureCoordinate);\n"+
            "     \n" +
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
            "    lowp vec4 centralColorSkin = origin;\n"+
            "    blurCoordinatesSkin[0] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, -10.0);\n"+
            "    blurCoordinatesSkin[1] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, 10.0);\n"+
            "    blurCoordinatesSkin[2] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-10.0, 0.0);\n"+
            "    blurCoordinatesSkin[3] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(10.0, 0.0);\n"+
            "    blurCoordinatesSkin[4] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, -8.0);\n"+
            "    blurCoordinatesSkin[5] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, 8.0);\n"+
            "    blurCoordinatesSkin[6] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, 8.0);\n"+
            "    blurCoordinatesSkin[7] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, -8.0);\n"+
            "    blurCoordinatesSkin[8] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, -4.0);\n"+
            "    blurCoordinatesSkin[9] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, 4.0);\n"+
            "    blurCoordinatesSkin[10] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, -4.0);\n"+
            "    blurCoordinatesSkin[11] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, 4.0);\n"+
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
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[8]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[9]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[10]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[11]).g * 2.0;\n"+
            "    sampleColorSkin = sampleColorSkin / 31.0;\n"+
            "    float highPass = centralColorSkin.g - sampleColorSkin + 0.5;\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "     float lumance = dot(centralColorSkin.rgb, W);\n"+
            "     lowp float luminance = dot(centralColorSkin.rgb, luminanceWeighting);\n" +
            "     lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "     float alpha = pow(lumance, params.r);\n"+
            "     vec3 smoothColor = centralColorSkin.rgb + (centralColorSkin.rgb - vec3(highPass)) * alpha * 0.1;\n"+
            "     smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n"+
            "     smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n"+
            "     smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n"+
            "     lowp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0) - centralColorSkin.rgb);\n"+
            "     lowp vec3 bianliang = max(smoothColor, centralColorSkin.rgb);\n"+
            "     lowp vec3 rouguang = 2.0 * centralColorSkin.rgb * smoothColor + centralColorSkin.rgb * centralColorSkin.rgb - 2.0 * centralColorSkin.rgb * centralColorSkin.rgb * smoothColor;\n"+
            "     lowp vec4 skinColor = vec4(mix(centralColorSkin.rgb, lvse, alpha), 1.0);\n"+
            "     skinColor.rgb = mix(skinColor.rgb, bianliang, alpha);\n"+
            "     skinColor.rgb = mix(skinColor.rgb, rouguang, params.b);\n"+
            "     vec3 satcolor = skinColor.rgb * saturateMatrix;\n"+
            "     lowp vec3 brightColor  = mix(skinColor.rgb, satcolor, params.a);\n"+
            "     gl_FragColor = vec4(mix(greyScaleColor, brightColor.rgb, saturation), centralColorSkin.a);\n" +
            "   }";


    public static final String FragmentShaderString_OES= "" +
            "   #extension GL_OES_EGL_image_external : require\n" +
            "   precision mediump float;\n" +
            "   uniform lowp samplerExternalOES inputImageTexture;\n" +
            "    varying highp vec2 textureCoordinate;\n" +
            "    \n" +
            "    const lowp float smoothDegree = 0.25;\n"+
            "    const lowp float saturation = 1.0225;\n" +
            "    const lowp float distanceNormalizationFactor = 4.0;\n" +
            "    \n"+
            "    const mediump vec4 params = vec4(0.85, 0.925, 0.175, 0.175);\n"+
            "    const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "    const mediump vec3 W = vec3(0.299, 0.587, 0.114);\n"+
            "    const mediump vec2 singleStepOffset = vec2(0.001,0.001);\n"+

            "    const lowp mat3 saturateMatrix = mat3(\n"+
            "       1.1102, -0.0598, -0.061,\n"+
            "       -0.0774, 1.0826, -0.1186,\n"+
            "       -0.0228, -0.0228, 1.1772);\n"+

            "    lowp vec2 blurCoordinatesSkin[12];\n"+
            "    uniform highp float touchPos;\n" +
            "    uniform highp float enabledDrawSide;\n"+
            "    vec4 bilateral;\n"+

            "    lowp float hardLight(lowp float color) {\n"+
            "       if (color <= 0.5)\n"+
            "       color = color * color * 2.0;\n"+
            "       else\n"+
            "       color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n"+
            "       return color;\n"+
            "    }\n"+

            "   void main(){\n"+
            "     vec4 origin = texture2D(inputImageTexture,textureCoordinate);\n"+
            "     \n" +
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
            "    lowp vec4 centralColorSkin = origin;\n"+
            "    blurCoordinatesSkin[0] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, -10.0);\n"+
            "    blurCoordinatesSkin[1] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(0.0, 10.0);\n"+
            "    blurCoordinatesSkin[2] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-10.0, 0.0);\n"+
            "    blurCoordinatesSkin[3] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(10.0, 0.0);\n"+
            "    blurCoordinatesSkin[4] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, -8.0);\n"+
            "    blurCoordinatesSkin[5] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(5.0, 8.0);\n"+
            "    blurCoordinatesSkin[6] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, 8.0);\n"+
            "    blurCoordinatesSkin[7] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-5.0, -8.0);\n"+
            "    blurCoordinatesSkin[8] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, -4.0);\n"+
            "    blurCoordinatesSkin[9] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(-4.0, 4.0);\n"+
            "    blurCoordinatesSkin[10] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, -4.0);\n"+
            "    blurCoordinatesSkin[11] = textureCoordinate.xy + 2.0 * singleStepOffset * vec2(4.0, 4.0);\n"+
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
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[8]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[9]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[10]).g * 2.0;\n"+
            "    sampleColorSkin += texture2D(inputImageTexture, blurCoordinatesSkin[11]).g * 2.0;\n"+
            "    sampleColorSkin = sampleColorSkin / 31.0;\n"+
            "    float highPass = centralColorSkin.g - sampleColorSkin + 0.5;\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "    highPass = hardLight(highPass);\n"+
            "     float lumance = dot(centralColorSkin.rgb, W);\n"+
            "     lowp float luminance = dot(centralColorSkin.rgb, luminanceWeighting);\n" +
            "     lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "     float alpha = pow(lumance, params.r);\n"+
            "     vec3 smoothColor = centralColorSkin.rgb + (centralColorSkin.rgb - vec3(highPass)) * alpha * 0.1;\n"+
            "     smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n"+
            "     smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n"+
            "     smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n"+
            "     lowp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0) - centralColorSkin.rgb);\n"+
            "     lowp vec3 bianliang = max(smoothColor, centralColorSkin.rgb);\n"+
            "     lowp vec3 rouguang = 2.0 * centralColorSkin.rgb * smoothColor + centralColorSkin.rgb * centralColorSkin.rgb - 2.0 * centralColorSkin.rgb * centralColorSkin.rgb * smoothColor;\n"+
            "     lowp vec4 skinColor = vec4(mix(centralColorSkin.rgb, lvse, alpha), 1.0);\n"+
            "     skinColor.rgb = mix(skinColor.rgb, bianliang, alpha);\n"+
            "     skinColor.rgb = mix(skinColor.rgb, rouguang, params.b);\n"+
            "     vec3 satcolor = skinColor.rgb * saturateMatrix;\n"+
            "     lowp vec3 brightColor  = mix(skinColor.rgb, satcolor, params.a);\n"+
            "     gl_FragColor = vec4(mix(greyScaleColor, brightColor.rgb, saturation), centralColorSkin.a);\n" +
            "   }";

    public GPUBeautificationFilter(Context ParamContext){
        super(ParamContext,FragmentShaderString_OES);
    }
}
