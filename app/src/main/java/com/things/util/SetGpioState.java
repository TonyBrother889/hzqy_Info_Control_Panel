package com.things.util;

import com.google.android.things.pio.Gpio;

import java.io.IOException;

/**
 * Created by zhangxiang on 2018/3/20.
 */

public class SetGpioState {

    /**设置GPIO为低电平*/
    public static void setOutputLow(Gpio gpio) throws IOException {
        // Initialize the pin as a high output
        gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        // Low voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_LOW);

        gpio.setValue(false);
    }

    /**设置GPIO为高电平*/

    public static void setOutputHight(Gpio gpio) throws IOException {
        // Initialize the pin as a high output
        gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        // Low voltage is considered active
        gpio.setActiveType(Gpio.ACTIVE_HIGH);

        gpio.setValue(true);
    }

    /**设置GPIO状态*/

    public static void setOutputState(Gpio gpio, boolean state) throws IOException {
        if(!state){
            gpio.setActiveType(Gpio.ACTIVE_LOW);
            gpio.setValue(!state);
        }else {
            gpio.setActiveType(Gpio.ACTIVE_HIGH);
            gpio.setValue(state);
        }
    }
}
