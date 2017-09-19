package com.teckmiles.guiweatherdresser.anim_utils;

import android.animation.ValueAnimator;
import android.widget.TextView;

/**
 * Created by muizz on 27/08/2017.
 */

public class Animation_Utils {

    public static void animateTemp(int tempValue, final TextView textView){

        ValueAnimator va = ValueAnimator.ofInt(tempValue);
        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(animation.getAnimatedValue().toString()+"\u00b0C");
            }
        });
        va.start();
    }

    public static void animateWeather(){



    }
}
