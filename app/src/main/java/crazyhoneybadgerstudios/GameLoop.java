package crazyhoneybadgerstudios;

import android.util.Log;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.selmf.R;

// /////////////////////////////////////////////////////////////////////////
// Game Loop
// /////////////////////////////////////////////////////////////////////////

/**
 * Core game loop thread
 *
 * @version 1.0
 */

 public class GameLoop implements Runnable {

    // ////////////////////////////////////////////////////////////////////
    // Properties
    // ////////////////////////////////////////////////////////////////////

    /**
     * Concurrent boolean lock that can be used to control update and draw
     * inter-thread sequencing.
     */
    class BooleanLock {
        boolean isLocked;

        public BooleanLock(boolean isLocked) {
            this.isLocked = isLocked;
        }
    }

    private Game game;
    /**
     * Sequence locks for the update and draw steps
     */
    volatile BooleanLock update;
    volatile BooleanLock draw;

    /**
     * Thread on which the game loop will run
     */
    Thread renderThread = null;

    /**
     * Flag determining if the update/draw thread is running
     */
    volatile boolean running = false;

    ElapsedTime elapsedTime;

    /**
     * Variable holding the duration (in ns) of the target game step period.
         * Changes to the Game's mTargetUpdatesPerSecond will change this value.
     */
    long targetStepPeriod;

    /**
     * Because an update/draw might load a lot of graphics, etc. a maximum
     * step period is introduced to provide a ceiling on the maximum
     * step size that will be reported to game objects (guarding them against
     * the need to check for abnormally long frames). By default, a value of
     * three times the target step period is assumed.
     */
    double maximumStepPeriodScale = 3.0f;

    // ////////////////////////////////////////////////////////////////////
    // Constructor
    // ////////////////////////////////////////////////////////////////////

    /**
     * Create a new game loop (the update/draw process will not commence
     * until the run method is executed).
     */
    public GameLoop(Game game) {
        this.game = game;
        // Setup the target step period
        targetStepPeriod = 1000000000 / game.getTargetFramesPerSecond();
        // Create a new time structure
        elapsedTime = new ElapsedTime();
        // Create update and draw locks
        update = new BooleanLock(false);
        draw = new BooleanLock(false);
    }

    // ////////////////////////////////////////////////////////////////////
    // Methods: Update/Draw Loop
    // ////////////////////////////////////////////////////////////////////

    /**
     * Start the update/draw process within a new thread.
     *
     * A relatively simple approach is employed that can support basic
     * multi-threading. A more sophisticated threaded approache might adopt
     * a three-phase prep-update-draw approach where the prep of frame n+1
     * occurs concurrently (across one or more threads) whilst the draw of
     * frame n executes. A more sophisticated timing approach might decouple
     * the draw and render phases, skipping the render of a frame if needed
     * to maintain a target update rate.
     */
    @Override
    public void run() {

        // Ensure that we have a game screen available to update and render
        if (game.getScreenManager().getCurrentScreen() == null) {
            String errorTag = game.getActivity().getResources().getString(
                    R.string.ERROR_TAG);
            String errorMessage = "You need to add a game screen to the screen mananger.";
            Log.e(errorTag, errorMessage);
            throw new RuntimeException(errorTag + errorMessage);
        }

        /**
         * Define variables which will be used to provide timing information
         * to enable precise control of the update/render cycle.
         *
         * startRun records the time at which the first iteration commenced
         * and is used to track total run time.
         *
         * The startStep and endStep variables record the time before and
         * time immediately after the update/render step.
         *
         * sleepTime records how long the thread should sleep before it is
         * necessary to start on the next update/render cycle (this may be a
         * negative period - i.e. the update/render process took longer than
         * desired). overSleepTime records how much longer the thread sleep
         * than was originally requested (i.e. accounting for the
         * unpredictable delay in waking up the thread).
         */
        long startRun;
        long startStep, endStep;
        long sleepTime, overSleepTime;

        /**
         * Define default starting values. The startTime and postRender
         * times are set to one frame 'in the past' to avoid near zero
         * timings for the first iteration. overSleepTime is set to zero.
         */
        startRun = System.nanoTime() - targetStepPeriod;
        startStep = startRun;
        overSleepTime = 0L;

        try {
            while (running) {

                // Update the timing information
                long currentTime = System.nanoTime();
                elapsedTime.totalTime = (currentTime - startRun) / 1000000000.0;
                elapsedTime.stepTime = (currentTime - startStep) / 1000000000.0;
                startStep = currentTime;

                // Weighted average update of the average number of frames
                // per second
                float newAverageFramesPerSecond = 0.85f * game.getAverageFramesPerSecond()
                        + 0.15f * (1.0f / (float) elapsedTime.stepTime);

                game.setAverageFramesPerSecond(newAverageFramesPerSecond);

                // If needed ensure the reported step time is not abnormally large
                if (elapsedTime.stepTime > (targetStepPeriod / 1000000000.0) * maximumStepPeriodScale)
                    elapsedTime.stepTime  =
                            (targetStepPeriod / 1000000000.0) * maximumStepPeriodScale;

                // Trigger an update
                synchronized (update) {
                    update.isLocked = true;
                }
                game.doUpdate(elapsedTime);
                // Wait for the update to complete before progressing
                synchronized (update) {
                    if (update.isLocked) {
                        update.wait();
                    }
                }

                // Trigger a draw request
                synchronized (draw) {
                    draw.isLocked = true;
                }
                game.doDraw(elapsedTime);
                // Wait for the draw to complete before progressing
                // If a plan-update-draw approach was employed the
                // wait for the draw would be tested post plan completion.
                synchronized (draw) {
                    if (draw.isLocked) {
                        draw.wait();
                    }
                }

                // Measure how long the update/draw took to complete and
                // how long to sleep until the next cycle is due. This may
                // be a negative number (we've exceeded the 'available'
                // time).
                endStep = System.nanoTime();
                sleepTime = (targetStepPeriod - (endStep - startStep))
                        - overSleepTime;

                // If needed put the thread to sleep
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1000000L); // Covert ns into ms

                    // Determine how much longer we slept than was
                    // originally requested, we'll correct for this error
                    // next frame
                    overSleepTime = (System.nanoTime() - endStep)
                            - sleepTime;
                } else {
                    overSleepTime = 0L;
                }
            }

        } catch (InterruptedException e) {
        }
    }

    /**
     * Notify the game loop that the draw has completed. This method will be
     * called by the game when it is notified that the draw has completed.
     */
    public void notifyDrawCompleted() {
        synchronized (draw) {
            draw.isLocked = false;
            draw.notifyAll();
        }
    }

    /**
     * Notify the game loop that the update has completed. This method will
     * be called by the game when it is notified that the update has
     * completed.
     */
    public void notifyUpdateCompleted() {
        synchronized (update) {
            update.isLocked = false;
            update.notifyAll();
        }
    }

    // ////////////////////////////////////////////////////////////////////
    // Methods: Pause/Resume
    // ////////////////////////////////////////////////////////////////////

    /**
     * Pause the game loop. This method will be called by the game whenever
     * it is paused.
     */
    public void pause() {
        running = false;
        while (true) {
            try {
                renderThread.join();
                return;
            } catch (InterruptedException e) {
                // Log something here
                // retry
            }
        }
    }

    /**
     * Resume the game loop. This method will be called by the game whenever
     * it is resumed.
     */
    public void resume() {
        running = true;

        draw.isLocked = false;
        update.isLocked = false;

        renderThread = new Thread(this);
        renderThread.start();
    }
}