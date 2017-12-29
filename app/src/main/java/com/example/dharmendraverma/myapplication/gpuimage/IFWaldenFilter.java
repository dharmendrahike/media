package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;

/**
 * Created by sam on 14-8-9.
 */
public class IFWaldenFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //map\n" +
            " uniform sampler2D inputImageTexture3; //vigMap\n" +
            " \n" +
            " uniform highp float touchPos;\n" +
            " \n" +
            " uniform highp float enabledDrawSide;\n"+

            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     vec4 texColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "  if(texColor.a == 0.0)\n" +
            "              {\n" +
            "                  gl_FragColor = vec4(0.0,0.0,0.0,1.0);\n" +
            "                  return;\n" +
            "              }"+
            "  if (enabledDrawSide != -1.0 && touchPos > 0.0 && touchPos < 1.0) {\n" +
            "                           if (enabledDrawSide == 0.0) {\n" +
            "                               if (textureCoordinate.x > touchPos) {\n" +
            "                                   gl_FragColor = texColor;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           } else {\n" +
            "                               if (textureCoordinate.x < touchPos) {\n" +
            "                                   gl_FragColor = texColor;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           }\n" +
            "                       }"+

            "     \n" +
            "     texel = vec3(\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.r, .16666)).r,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.g, .5)).g,\n" +
            "                  texture2D(inputImageTexture2, vec2(texel.b, .83333)).b);\n" +
            "     \n" +
            "     vec2 tc = (2.0 * textureCoordinate) - 1.0;\n" +
            "     float d = dot(tc, tc);\n" +
            "     vec2 lookup = vec2(d, texel.r);\n" +
            "     texel.r = texture2D(inputImageTexture3, lookup).r;\n" +
            "     lookup.y = texel.g;\n" +
            "     texel.g = texture2D(inputImageTexture3, lookup).g;\n" +
            "     lookup.y = texel.b;\n" +
            "     texel.b\t= texture2D(inputImageTexture3, lookup).b;\n" +
            "     \n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    public IFWaldenFilter(Context paramContext) {
        super(paramContext, SHADER);
        setRes();
    }

    private void setRes() {
        //addInputTexture(R.drawable.walden_map);
        //addInputTexture(R.drawable.vignette_map);
    }
}
