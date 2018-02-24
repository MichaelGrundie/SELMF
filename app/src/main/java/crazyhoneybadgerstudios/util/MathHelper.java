package crazyhoneybadgerstudios.util;

import android.util.Log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dariusz Jerzewski on 06/05/2017.
 */

public final class MathHelper {

    /**
     * @author Dariusz Jerzewski
     * @param svalue
     * @return scale number down to range 0.0 - 1.0
     */
    public static float linearConvertion(float svalue){
        return svalue / Float.MAX_VALUE;
    }

    /**
     * @author Dariusz Jerzewski
     * @param svalue
     * @return scale number down to range 0.0 - 1.0
     */
    public static float linearConvertion(long svalue){
        return (float)svalue / Long.MAX_VALUE;
    }

    /**
     * @author Dariusz Jerzewski
     * @param x
     * @param y
     * @param weight - should be in range 0.0-1.0 - use linearConvertion to convert a number to this range
     * @return
     */
    public static float lerp(float x, float y, float weight){
        return (1.0f - weight) * x + weight * y;
    }

    /**
     * @author Dariusz Jerzewski
     * @param weight - should be in range 0.0-1.0 - use linearConvertion to convert a number to this range
     * @return
     */
    public static float lerp(Vector2 v, float weight){
        return (1.0f - weight) * v.x + weight * v.y;
    }

}
