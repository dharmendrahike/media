package com.example.dharmendraverma.myapplication.gpuimage;

import android.content.Context;

/**
 * Created by sam on 14-8-9.
 */
public class IFHudsonFilter extends IFImageFilter {
    private static final String SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2; //blowout;\n" +
            " uniform sampler2D inputImageTexture3; //overlay;\n" +
            " uniform sampler2D inputImageTexture4; //map\n" +
            " \n" +
            " \n" +
            " uniform highp float touchPos;\n" +
            " \n" +
            " uniform highp float enabledDrawSide;\n"+
            " \n" +
            " void main()\n" +
            " {\n" +
            "     \n" +
            "     vec4 texel = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "if(texel.a == 0.0)\n" +
            "              {\n" +
            "                  gl_FragColor = vec4(0.0,0.0,0.0,1.0);\n" +
            "                  return;\n" +
            "              }"+
            "  if (enabledDrawSide != -1.0 && touchPos > 0.0 && touchPos < 1.0) {\n" +
            "                           if (enabledDrawSide == 0.0) {\n" +
            "                               if (textureCoordinate.x > touchPos) {\n" +
            "                                   gl_FragColor = texel;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           } else {\n" +
            "                               if (textureCoordinate.x < touchPos) {\n" +
            "                                   gl_FragColor = texel;\n" +
            "                                   return;\n" +
            "                               }\n" +
            "                           }\n" +
            "                       }"+
            "     \n" +
            "     vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;\n" +
            "     texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;\n" +
            "     texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;\n" +
            "     \n" +
            "     vec4 mapped;\n" +
            "     mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;\n" +
            "     mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;\n" +
            "     mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;\n" +
            "     mapped.a = 1.0;\n" +
            "     gl_FragColor = mapped;\n" +
            " }\n";

    public IFHudsonFilter(Context paramContext) {
        super(paramContext, SHADER);
        setRes();
    }

    private void setRes() {
        /*addInputTexture(R.drawable.hudson_background);
        addInputTexture(R.drawable.overlay_map);
        addInputTexture(R.drawable.hudson_map);*/
    }
}
