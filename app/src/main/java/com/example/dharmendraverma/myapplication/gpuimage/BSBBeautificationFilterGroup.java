package com.example.dharmendraverma.myapplication.gpuimage;

/**
 * Created by dharmendraverma on 10/04/17.
 */

public class BSBBeautificationFilterGroup extends GPUImageFilterGroup {

    BSBBeautificationFilter beautify;
    GPUImageSaturationFilter saturationFilter;
    GPUImageBrightnessFilter brightnessFilter;
    GPUImageSkinToneBeautifyFilter toneFilter;
    Float beautyLevel; // 0 - 1, 0 is no change
    Float brightness; // -1 - 1, 0 is no change
    Float saturation; // 0 - 2, 1 is no change
    Float normalization; // 0 - 10
    Float toneLevel; // 0 - 1
    Float smoothness; //
    private static final Float BSBBeautifyFilterDefaultIntensity = 0.18f;

    public BSBBeautificationFilterGroup() {
        super();
        beautify=new BSBBeautificationFilter();
        beautify.setTag("BSBBeautificationFilter");
        /*hueFilter=new GPUImageHueFilter();
        hueFilter.setTag("GPUImageHueFilter");*/
        saturationFilter=new GPUImageSaturationFilter();
        saturationFilter.setTag("GPUImageSaturationFilter");
        brightnessFilter=new GPUImageBrightnessFilter();
        brightnessFilter.setTag("GPUImageBrightnessFilter");
//        contrastFilter=new GPUImageContrastFilter();
//        contrastFilter.setTag("GPUImageContrastFilter");
        toneFilter=new GPUImageSkinToneBeautifyFilter();
        toneFilter.setTag("GPUImageSkinToneBeautifyFilter");

        beautyLevel = BSBBeautifyFilterDefaultIntensity;
        smoothness = beautyLevel;
        brightness =(float) (0.0 + beautyLevel / 8.0);
        saturation = (float)(1.0 + beautyLevel / 8.0);
        toneLevel =(float) (beautyLevel/4.0);
        setSaturation(saturation);
        setBeautyLevel(BSBBeautifyFilterDefaultIntensity);
        setBrightness(brightness);
      //  addFilter(new GPUImageBoxBlurFilter(0.09f));
          addFilter(beautify);
          addFilter(toneFilter);
          addFilter(saturationFilter);
          addFilter(brightnessFilter);
    }

    public void setBeautyLevel(Float beautyLevel) {
        this.beautyLevel = beautyLevel;
        toneLevel = (float) Math.max(0.01f, beautyLevel / 4.0f);
        smoothness = (float) Math.max(beautyLevel,0.01f);
        brightness = (float) Math.min(0.5f,(0.0f + beautyLevel / 8.0f));

        toneFilter.toneLevel = toneLevel;
        toneFilter.beautyLevel = toneLevel;

        setSmoothness(smoothness);
        setBrightness(brightness);
        setToneLevel(toneLevel);
    }

    public void setBrightness(Float brightness) {
        this.brightness = brightness;
        brightnessFilter.setBrightness(brightness);
    }

    public void setSaturation(Float saturation) {
        this.saturation = saturation;
        saturationFilter.setSaturation(saturation);
    }


    public void setToneLevel(Float toneLevel) {
        this.toneLevel = toneLevel;
        toneFilter.setToneLevel(toneLevel);
        toneFilter.setBeautyLevel(toneLevel);
    }


    public void setSmoothness(float smoothness){
        beautify.setSmoothnessFactor(smoothness);
    }

}
