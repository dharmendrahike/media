package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;


//import jp.co.cyberagent.android.gpuimage.sample.R;

/**
 * Created by sam on 14-8-9.
 */
public class IFFadeOut extends GPUImageFilter {
    private Context mContext;
    private static final String SHADER =   "\n" +
            " precision highp float;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float touchPos;\n" +
            " \n" +
            " uniform highp float enabledDrawSide;\n"+

            " \n" +
            " void main()\n" +
            " {\n" +
            " vec4 color = texture2D(inputImageTexture, textureCoordinate);\n" +
            " if(color.a == 0.0)\n" +
            "              {\n" +
            "                  gl_FragColor = vec4(0.0,0.0,0.0,1.0);\n" +
            "                  return;\n" +
            "              }"+
            "  if (enabledDrawSide != -1.0 && touchPos > 0.0 && touchPos < 1.0) {\n" +
            "                           if (enabledDrawSide == 0.0) {\n" +
            "                               if (textureCoordinate.x > touchPos) {\n" +
            "                                   gl_FragColor = color;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           } else {\n" +
            "                               if (textureCoordinate.x < touchPos) {\n" +
            "                                   gl_FragColor = color;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           }\n" +
            "                       }"+

            " float r = color.r;\n"+
            " float g = color.g;\n"+
            " float b = color.b;\n"+

            " float s32Step = 64.0;\n"+
            " float temp = 0.00351*s32Step;\n" +
            " temp = 1.0 - temp;\n" +
            " r = r * temp;\n" +
            " temp = 0.8125*s32Step;\n" +
            " temp = temp/256.0;\n" +
            " r = r + temp;\n" +
            " temp = 0.002075*s32Step;\n" +
            " temp = 1.0 - temp;\n" +
            " g = g * temp;\n" +
            " temp = 0.40625*s32Step;\n" +
            " temp = temp/256.0;\n" +
            " g = g + temp;\n" +
            " temp = 0.004547*s32Step;\n" +
            " temp = 1.0 - temp;\n" +
            " b = b * temp;\n" +
            " temp = 0.5625*s32Step;\n" +
            " temp = temp/256.0;\n" +
            " b = b + temp;"+

            " vec3 new_color = vec3(r, g, b);\n" +
            " gl_FragColor = vec4(new_color, 1.0);\n" +
            " }\n";

    public IFFadeOut(Context context) {
        super(NO_FILTER_VERTEX_SHADER, SHADER);
        mContext = context;

    }

    public void onInit() {
        super.onInit();
        //filterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        //initInputTexture();
    }

    /*protected void onDrawArraysPre() {
        super.onDrawArraysPre();
        if (filterSourceTexture2 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, filterSourceTexture2);
            GLES20.glUniform1i(filterInputTextureUniform2, 3);
        }
    }*/


}
