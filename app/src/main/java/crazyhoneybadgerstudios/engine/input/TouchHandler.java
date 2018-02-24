package crazyhoneybadgerstudios.engine.input;

import android.view.*;

import java.util.ArrayList;
import java.util.List;

import crazyhoneybadgerstudios.util.Pool;
import crazyhoneybadgerstudios.util.Vector2;

/**
 * Creates all the touch events for the input class
 * @author Michael Grundie - implentation of 'finger still on screen' boolean
 * @author Chloe McMullan - All but finger still on screen implementation.
 */

public class TouchHandler extends GestureDetector.SimpleOnGestureListener{

	// /////////////////////////////////////////////////////////////////////////
	// Properties
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Define the maximum number of touch events that can be supported
	 */
	public static final int MAX_TOUCHPOINTS = 10;

	/**
	 * Occurred and position information for the supported number of touch
	 * events.
	 */
	private boolean[] mExistsTouch = new boolean[MAX_TOUCHPOINTS];
	private float mTouchX[] = new float[MAX_TOUCHPOINTS];
	private float mTouchY[] = new float[MAX_TOUCHPOINTS];

	/**
	 * Touch event pool and lists of current (for this frame) and unconsumed
	 * (occurring since the frame started) touch events.
	 */
	private Pool<TouchEvent> mPool;
	private List<TouchEvent> mTouchEvents = new ArrayList<TouchEvent>();
	private List<TouchEvent> mUnconsumedTouchEvents = new ArrayList<TouchEvent>();

	/**
	 * Axis scale values - can be used to scale the input range from native
	 * pixels to some predefined range.
	 */
	private float mScaleX;
	private float mScaleY;

	/**
	 * Define the maximum number of touch events that can be retained in the
	 * touch store.
	 */
	private final int TOUCH_POOL_SIZE = 100;

    private Vector2 lastScrollEvent; // contains co-ords of the last drag event, if it occurred

	public boolean fingerOnScreen = false; // check for if the finger is still on the screen

	// /////////////////////////////////////////////////////////////////////////


	/**
	 * The constructor to create the pool and scale accordingly
	 * @param view the view to detect touches on
	 */
	public TouchHandler(View view) {

		mPool = new Pool<TouchEvent>(new Pool.ObjectFactory<TouchEvent>() {
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		}, TOUCH_POOL_SIZE);

		mScaleX = 1.0f;
		mScaleY = 1.0f;
        lastScrollEvent = new Vector2(-0.01f, -0.01f); //-0.01f is used to test for no scroll event yet recorded

	}

	// /////////////////////////////////////////////////////////////////////////
	// Constructors                                                          ///
	////////////////////////////////////////////////////////////////////////////


	/**
	 * Triggered if a single tap up event occurs
	 * @param event details of the event
	 * @return true if event consumed, otherwise false
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		fingerOnScreen = false; //finger will no longer be on the screen

        //updates positions
		updateEventPositions(event);
		int pointerID = event.getPointerId(event.getActionIndex());

		// Retrieve and populate a touch event
		TouchEvent touchEvent = instantiateTouchEvent(pointerID);
		touchEvent.type = TouchEvent.TOUCH_UP;
		mExistsTouch[pointerID] = false;

		synchronized (this) {
			mUnconsumedTouchEvents.add(touchEvent);
		}
		return true;
	}



	/**
	 * Triggered if a single tap up is confirmed to only be a single tap
	 * @param event details of this event
	 * @return true if the event is consumed, otherwise false
	 */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		fingerOnScreen = false; //finger will no longer be on the screen

        updateEventPositions(event);
        int pointerID = event.getPointerId(event.getActionIndex());

        // Retrieve and populate a touch event
        TouchEvent touchEvent = instantiateTouchEvent(pointerID);
        touchEvent.type = TouchEvent.SINGLE_TAP_CONFIRMED;
        mExistsTouch[pointerID] = false;

        synchronized (this) {
            mUnconsumedTouchEvents.add(touchEvent);
        }
        return true;
	}



	/**
	 * Trigged if the user presses down. Triggered at the beginning of any event
	 * @param event details of this event
	 * @return true if the event is consumed, otherwise false
	 */
	@Override
	public boolean onDown(MotionEvent event) {
	    fingerOnScreen = true; // finger will be on the screen

        lastScrollEvent.set(event.getX(), event.getY()); // flag to show no drag has yet occured

		updateEventPositions(event);
		int pointerID = event.getPointerId(event.getActionIndex());

		// Retrieve and populate a touch event
		TouchEvent touchEvent = instantiateTouchEvent(pointerID);
		touchEvent.type = TouchEvent.TOUCH_DOWN;
		mExistsTouch[pointerID] = true;

		synchronized (this) {
			mUnconsumedTouchEvents.add(touchEvent);
		}
		return true;
	}



	/**
	 * Triggered if a long press touch happens
	 * @param event details of this event
	 */
	@Override
	public void onLongPress(MotionEvent event) {
	    fingerOnScreen = true; //long presses end with finger still on screen

		updateEventPositions(event);
		int pointerId = event.getPointerId(event.getActionIndex());

        //instantiates the details about the touch event
		TouchEvent touchEvent = instantiateTouchEvent(pointerId); //creates a pool
		touchEvent.type = TouchEvent.LONG_PRESS;
		mExistsTouch[pointerId] = true;

		synchronized (this) {
			mUnconsumedTouchEvents.add(touchEvent);
		}
	}



	/**
	 * Triggered if a drag occurs
	 * @param event1 the beginning event (i.e, where the first touch down of this drag was)
	 * @param event2 The most recent event which has continued the drag (i.e. furthest event of the drag yet)
	 * @param distanceX X distance between the first and second event
	 * @param distanceY Y distance between the first and second event
	 * @return true if the event is consumed, otherwise false
	 */
	@Override
	public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
							float distanceY) {
        fingerOnScreen = true;

		updateEventPositions(event2);
		int pointerID = event2.getPointerId(event2.getActionIndex());
		TouchEvent dragEvent = instantiateTouchEvent(pointerID);

        //last scroll event will always be accurate as touch down records last scroll for first event
        dragEvent.oldX = lastScrollEvent.x;
        dragEvent.oldY = lastScrollEvent.y;

        lastScrollEvent.set(dragEvent.x, dragEvent.y);

        dragEvent.type = TouchEvent.TOUCH_DRAGGED;
        mExistsTouch[pointerID] = true;

        synchronized (this) {
            mUnconsumedTouchEvents.add(dragEvent);
        }
		return true;
	}


    ////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// UTIL METHODS ////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Updates the positions of events to match the scale if required
     * @param event event containing the pointers that need updated
     */
	private void updateEventPositions(MotionEvent event) {
		// Update the locations of all occurring touch points
		for (int ptrIdx = 0; ptrIdx < event.getPointerCount(); ptrIdx++) {
			// Update the relevant touch point location
			int pointerId = event.getPointerId(ptrIdx);
			mTouchX[pointerId] = event.getX(ptrIdx) * mScaleX;
			mTouchY[pointerId] = event.getY(ptrIdx) * mScaleY;
		}
	}



    /**
     * Instantiates a touch event of the id given
     * @param pointerID pointer id to instatiate the touch event of
     * @return returns the touch event object created
     */
	private TouchEvent instantiateTouchEvent(int pointerID) {
		TouchEvent touchEvent = mPool.get();
		touchEvent.pointer = pointerID;
		touchEvent.x = mTouchX[pointerID];
		touchEvent.y = mTouchY[pointerID];
		return touchEvent;
	}


    /**
     * Checks if a touch exists for a particular pointer ID
     * @param pointerId pointer ID to check
     * @return true = touch exists, false = touch doesn't exist
     */
	public boolean existsTouch(int pointerId) {
		synchronized (this) {
			return mExistsTouch[pointerId];
		}
	}

	/**
	 * Get the (scaled) x-location of the specified pointer ID
	 *
	 * Note: The method assumes that the specified pointer ID has a valid touch
	 * location (i.e. that existsTouch(pointerID) is true).
	 *
	 * @param pointerId
	 *            ID of the pointer to test for
	 * @return x-touch location of the specified pointer ID, or Float.NAN if the
	 *         pointer ID does not currently exist
	 */
	public float getTouchX(int pointerId) {
		synchronized (this) {
			// Assumes the user will ensure correct range checking - for speed
			if (mExistsTouch[pointerId])
				return mTouchX[pointerId];
			else
				return Float.NaN;
		}
	}

	/**
	 * Get the (scaled) y-location of the specified pointer ID
	 *
	 * Note: The method assumes that the specified pointer ID has a valid touch
	 * location (i.e. that existsTouch(pointerID) is true).
	 *
	 * @param pointerId
	 *            ID of the pointer to test for
	 * @return y-touch location of the specified pointer ID, or Float.NAN if the
	 *         pointer ID does not currently exist
	 */
	public float getTouchY(int pointerId) {
		synchronized (this) {
			// Assumes the user will ensure correct range checking - for speed
			if (mExistsTouch[pointerId])
				return mTouchY[pointerId];
			else
				return Float.NaN;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// Methods: Event Accumulation
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Return the list of touch events accumulated for the current frame.
	 *
	 * IMPORTANT: A shared list of touch events is returned. The list should be
	 * considered read only.
	 *
	 * @return List of touch events accumulated for the current frame
	 */
	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
			return mTouchEvents;
		}
	}

	/**
	 * Reset the accumulator - update the current set of frame touch events to
	 * those accumulated since the last time the accumulator was reset.
	 *
	 * Note: It is assumed that this method will be called once per frame.
	 */
	public void resetAccumulator() {
		synchronized (this) {
			// Release all existing touch events
			int len = mTouchEvents.size();
			for (int i = 0; i < len; i++)
				mPool.add(mTouchEvents.get(i));
			mTouchEvents.clear();
			// Copy across accumulated events
			mTouchEvents.addAll(mUnconsumedTouchEvents);
			mUnconsumedTouchEvents.clear();
		}
	}

}
