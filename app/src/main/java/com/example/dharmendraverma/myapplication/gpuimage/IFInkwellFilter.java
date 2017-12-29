package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;

import com.example.dharmendraverma.myapplication.R;

/**
 * Created by sam on 14-8-9.
 */
public class IFInkwellFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform highp float touchPos;\n" +
            " \n" +
            " uniform highp float enabledDrawSide;\n"+
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +

            "if(textureColor.a == 0.0)\n" +
            "              {\n" +
            "                  gl_FragColor = vec4(0.0,0.0,0.0,1.0);\n" +
            "                  return;\n" +
            "              }"+
            "  if (enabledDrawSide != -1.0 && touchPos > 0.0 && touchPos < 1.0) {\n" +
            "                           if (enabledDrawSide == 0.0) {\n" +
            "                               if (textureCoordinate.x > touchPos) {\n" +
            "                                   gl_FragColor = textureColor;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           } else {\n" +
            "                               if (textureCoordinate.x < touchPos) {\n" +
            "                                   gl_FragColor = textureColor;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           }\n" +
            "                       }"+
            "     texel = vec3(dot(vec3(0.3, 0.6, 0.1), texel));\n" +
            "     texel = vec3(texture2D(inputImageTexture2, vec2(texel.r, .16666)).r);\n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    public IFInkwellFilter(Context paramContext) {
        super(paramContext, SHADER);
        setRes();
    }

    private void setRes() {
        addInputTexture(R.drawable.inkwell_map);
    }
}
