package crazyhoneybadgerstudios.engine.input;

/**
 * Touch event.
 * Any changes done by @author Chloe McMullan
 * @version 1.0
 */
public class TouchEvent {

	/**
	 * Touch event constants
	 */
	public static final int TOUCH_DOWN = 0;
	public static final int TOUCH_UP = 1;
	public static final int TOUCH_DRAGGED = 2;
	public static final int LONG_PRESS = 3;
	public static final int SINGLE_TAP_CONFIRMED = 4;

	/**
	 * Type of touch event that has occurred (TOUCH_DOWN, TOUCH_UP,
	 * TOUCH_DRAGGED)
	 */
	public int type;

	/**
	 * Screen position (pixel) at which the touch event occurred.
	 */
	public float x, y;

	/**
	 * Screen position (pixel) of the previous touch event, if associated with this one
	 */
	public float oldX, oldY;

	/**
	 * Pointer ID associated with this touch event
	 */
	public int pointer;
}