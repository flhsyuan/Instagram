package com.example.asus.instagram.Interface;

/**
 * @author : Yujie Lyu
 * @date : 29-09-2018
 * @time : 18:03
 */
public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onContrastChanged(float contrast);
    void onEditStarted();
    void onEditCompleted();
}
