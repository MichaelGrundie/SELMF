package crazyhoneybadgerstudios;

import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.graphics.CanvasRenderSurface;
import crazyhoneybadgerstudios.engine.graphics.IRenderSurface;
import crazyhoneybadgerstudios.engine.graphics.TextParser;
import crazyhoneybadgerstudios.engine.input.Input;
import crazyhoneybadgerstudios.engine.io.FileIO;
import crazyhoneybadgerstudios.game.Game_Elements.User;
import crazyhoneybadgerstudios.game.game_screens.MenuScreen;
import crazyhoneybadgerstudios.game.game_screens.SplashScreen;
import crazyhoneybadgerstudios.world.GameScreen;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Central game class, providing access to core game services and management of
 * the update/render cycle.
 * 
 * @version 1.0
 */
public class Game extends Fragment
{

	//TEXT PARSER
	TextParser mTextParser;


	// /////////////////////////////////////////////////////////////////////////
	// Properties: Frames per Second
	// /////////////////////////////////////////////////////////////////////////
	private User playerProfile;
	public User getPlayerProfile() { return playerProfile; }
	public void setPlayerProfile(User playerProfile) { this.playerProfile = playerProfile; }


	/**
	 * Variable used to record the target number of update/draw iterations in a
	 * one second interval. The game thread will sleep between iterations if
	 * possible.
	 */
	private int mTargetFramesPerSecond = 20;

	/**
	 * Get the target number of frames per second
	 *
	 * @return Target number of frames per second
	 */
	public int getTargetFramesPerSecond() {
		return mTargetFramesPerSecond;
	}

	/**
	 * Set the target number of frames per second
	 * 
	 * @param targetFramesPerSecond
	 *            Target number of frames per second
	 */
	public void setTargetFramesPerSecond(int targetFramesPerSecond) {
		mTargetFramesPerSecond = targetFramesPerSecond;

		// Update the target update/draw period in the game thread (which is
		// stored in ns)
		if (mLoop != null)
			mLoop.targetStepPeriod = 1000000000 / targetFramesPerSecond;
	}

	/**
	 * Average number of frames per second that is being achieved
	 */
	private float mAverageFramesPerSecond;

	/**
	 * Get the average number of frames per second that is being achieved
	 * 
	 * @return Average number of frames per second that is being achieved
	 */
	public float getAverageFramesPerSecond() {
		return mAverageFramesPerSecond;
	}

	public void setAverageFramesPerSecond(float averageFramesPerSecond){
		mAverageFramesPerSecond = averageFramesPerSecond;
	}
	// /////////////////////////////////////////////////////////////////////////
	// Properties: Managers and Services
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Asset Manager
	 */
	protected AssetStore mAssetManager;

	/**
	 * Get the game's asset manager
	 * 
	 * @return Asset manager
	 */
	public AssetStore getAssetManager() {
		return mAssetManager;
	}
	
	/**
	 * Screen Manager
	 */
	protected ScreenManager mScreenManager;

	/**
	 * Get the game's screen manager
	 * 
	 * @return Asset manager
	 */
	public ScreenManager getScreenManager() {
		return mScreenManager;
	}
		
	/**
	 * Input Service
	 */
	protected Input mInput;

	/**
	 * Get the game's input service
	 * 
	 * @return Input service
	 */
	public Input getInput() {
		return mInput;
	}
	
	/**
	 * File IO Service
	 */
	protected FileIO mFileIO;

	/**
	 * Get the game's file IO service

	 * @return File IO service
	 */
	public FileIO getFileIO() {
		return mFileIO;
	}
		
	/**
	 * Render Surface
	 */
	protected IRenderSurface mRenderSurface;
	
	
	// /////////////////////////////////////////////////////////////////////////
	// Properties: Game Loop
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Game loop thread
	 */
	private GameLoop mLoop;


	// /////////////////////////////////////////////////////////////////////////
	// Properties: Screen Size
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Width of the game window in pixels
	 */
	private int mScreenWidth = -1;

	/**
	 * Get the width of the game window
	 * 
	 * @return Width of the game window
	 */
	public int getScreenWidth() {
		return mScreenWidth;
	}
	
	/**
	 * Height of the game window in pixels
	 */
	private int mScreenHeight = -1;

	/**
	 * Get the height of the game window
	 * 
	 * @return Height of the game window
	 */
	public int getScreenHeight() {
		return mScreenHeight;
	}

	
	// /////////////////////////////////////////////////////////////////////////
	// Methods: State Management
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a new game loop
		mLoop = new GameLoop(this);

		// ////////////////////////////////////////////////////////
		// Manager/Service Creation : None-view/context dependent
		// ////////////////////////////////////////////////////////

		// Create the file IO service
		mFileIO = new FileIO(getActivity().getApplicationContext());
				
		// Create the asset manager
		mAssetManager = new AssetStore(mFileIO);
		
		// Create the screen manager
		mScreenManager = new ScreenManager();
		
		// Request control of the volume
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		playerProfile = new User(this);
		playerProfile.setCurrentLevel(0); //when game is first opened, their current level is defaulted to 0 (aka tutorial level)

	}


	/**
	 * @author Michael Grundie
	 *
	 * Test Constructor.
	 *
	 * @param savedInstanceState
	 * @param assetStore
	 */
	public void onCreate(Bundle savedInstanceState, AssetStore assetStore) {
		super.onCreate(savedInstanceState);

		// Create the asset manager
		mAssetManager = assetStore;
		getActivity();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// ////////////////////////////////////////////////////////
		// Manager/Service Creation : View/context dependent
		// ////////////////////////////////////////////////////////

		// Create the output view and associated renderer
		mRenderSurface = new CanvasRenderSurface(this, getActivity());
		View view = mRenderSurface.getAsView();

		//create default paint size 100, easy to scale
		Paint defPaint = new Paint();
		defPaint.setAntiAlias(true);
		defPaint.setTextSize(100f);
		defPaint.setColor(Color.BLACK);
		//Create text parser
		//mTextParser = new TextParser(defPaint, TextParser.WordSpacing.NORMAL,((CanvasRenderSurface)mRenderSurface).getIGraphics2d());


		// Get our input from the created view
		mInput = new Input(getActivity(), view);

		// Store the size of the window we're using		
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;

		// This code is commented out to disable the splash screen during testing
		//NOTE: If disabling splash screen to test, ensure that AssetLoading.Load...() methods in constructors of menu, character creation, map screen and tutorial are UNCOMMENTED
		SplashScreen ss = new SplashScreen(this);
		mScreenManager.addScreen(ss);

		return view;
	}

	/**
	 * function to get text parser
	 */

	public TextParser getTextParser(){
		return mTextParser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// If needed, resume the current game screen
		if (mScreenManager.getCurrentScreen() != null)
			mScreenManager.getCurrentScreen().resume();

		// Resume the game loop
		mLoop.resume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// Pause the game loop
		mLoop.pause();

		// If needed, pause the current game screen
		if (mScreenManager.getCurrentScreen() != null)
			mScreenManager.getCurrentScreen().pause();

		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// Dispose of any game screens
		mScreenManager.dispose();

		super.onDestroy();
	}

	/**
	 * Called from the activity whenever the back key has been pressed. 
	 * 
	 * @return True if the back event has been consumed by the game, false otherwise.
	 */
	public boolean onBackPressed() {
		return false;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Methods: Update and Draw
	// /////////////////////////////////////////////////////////////////////////

	// To best exploit the multi-threaded nature of current CPUs,
	// a planning phase could be added here - invoked currently
	// with the draw phase within the game loop. Given this structure
	// the update phase is reduced to swapping the world from state n
	// to state n+1.
	// public void doPrep(ElapsedTime elapsedTime) { }

	/**
	 * Perform the update step
	 * 
	 * @param elapsedTime
	 *            Elapsed time information for the current frame
	 */
	public void doUpdate(ElapsedTime elapsedTime) {
		// Reset accumulators for keys/touch events for the current frame
		((Input) mInput).resetAccumulators();		
		
		// Get and update the current game screen
		GameScreen gameScreen = mScreenManager.getCurrentScreen();
		if (gameScreen != null)
			gameScreen.updateScreen(elapsedTime);

		// It is assumed that if the update is multi-threaded then the
		// method call will not return until all update processes have
		// completed. Once this happens, notify the game loop.
		notifyUpdateCompleted();		
	}

	/**
	 * Notify the game loop that the update has completed. This method is in
	 * invoked automatically once control has returned from the Game update()
	 * method.
	 */
	public void notifyUpdateCompleted() {
		mLoop.notifyUpdateCompleted();
	}

	/**
	 * Perform the draw step
	 * 
	 * @param elapsedTime
	 *            Elapsed time information for the current frame
	 */
	public void doDraw(ElapsedTime elapsedTime) {
		// Get and draw the current screen. The render surface will
		// invoked Game.notifyDrawCompleted when the draw is done.
		GameScreen gameScreen = mScreenManager.getCurrentScreen();
		if (gameScreen != null)
			mRenderSurface.render(elapsedTime, gameScreen);
	}

	/**
	 * Notify the game loop that the draw has completed. This method is in
	 * invoked automatically by the render surface when the draw has completed.
	 */
	public void notifyDrawCompleted() {
		mLoop.notifyDrawCompleted();
	}



	//SAVE/LOAD GAME DATA

	//Method saves appropriate user data, to be loaded on next resuming the game.
	//Uses shared preferences
	//@author Jodie Burnside (40150039)
	public void saveData()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("level", playerProfile.getCurrentLevel());
		editor.putBoolean("soundEnabled", getPlayerProfile().getSoundEnabled());
		editor.putInt("headNumber", getPlayerProfile().getPlayerSelectedHead());
		editor.putInt("robesNumber", getPlayerProfile().getPlayerSelectedRobes());
		editor.putInt("goldCoins", getPlayerProfile().getGoldCoins());

		editor.commit();
	}

	//Method loads user data from shared preferences, into the playerProfile (User object), to resume a previous game
	//Uses shared preferences
	//@author Jodie Burnside (40150039)
	public void loadData()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		playerProfile.setCurrentLevel(prefs.getInt("level", 0));
		getPlayerProfile().setSoundEnabled(prefs.getBoolean("soundEnabled", true));
		getPlayerProfile().setPlayerSelectedHead(prefs.getInt("headNumber", 0));
		getPlayerProfile().setPlayerSelectedRobes(prefs.getInt("robesNumber", 0));
		getPlayerProfile().setGoldCoins(prefs.getInt("goldCoins", 0));

		editor.commit();
	}

}