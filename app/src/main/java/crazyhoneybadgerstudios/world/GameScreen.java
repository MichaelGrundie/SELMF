package crazyhoneybadgerstudios.world;

import android.graphics.Bitmap;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.animation.Animator;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.engine.audio.Music;
import crazyhoneybadgerstudios.util.GraphicsHelper;

/**
 * Game screen class acting as a container for a coherent section of the game (a
 * level, configuration screen, etc.).
 *
 * @version 1.0
 */
public abstract class GameScreen {

	// /////////////////////////////////////////////////////////////////////////
	// Properties
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Name that is given to this game screen
	 */
	protected final String mName;

	protected Animator mAnimator;

	/**
	 * Return the name of this game screen
	 *
	 * @return Name of this game screen
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Game to which game screen belongs
	 */
	protected final Game mGame;

	/**
	 * Return the game to which this game screen is attached
	 *
	 * @return Game to which screen is attached
	 */
	public Game getGame() {
		return mGame;
	}

	/*
	 * Background music used on the screen
	 */
	protected Music backgroundMusic;

	/**
	 * @author Courtney Shek
	 *
	 * Return the background music attached to the screen
	 *
	 * @return Background music attached to the screen
	 */
	public Music getBackgroundMusic()
	{
		return backgroundMusic;
	}

	/**
	 * Asset Manager used on all gameScreens
	 */
	protected AssetStore assetManager;

	/**
	 * ScreenViewPort used on all gameScreens
	 */
	protected ScreenViewport screenViewport;
	public ScreenViewport getScreenViewPort() {
		return screenViewport;
	}
	// /////////////////////////////////////////////////////////////////////////
	// Constructors
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create a new game screen associated with the specified game instance
	 *
	 * @param game
	 *            Game instance to which the game screen belongs
	 */
	public GameScreen(String name, Game game) {
		mName = name;
		mGame = game;
		assetManager = mGame.getAssetManager();
		mAnimator = new Animator();
	}

	// /////////////////////////////////////////////////////////////////////////
	// Update and Draw
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Update the game screen. Invoked automatically from the game.
	 *
	 * NOTE: If the update is multi-threaded control should not be returned from
	 * the update call until all update processes have completed.
	 *
	 * @param elapsedTime
	 *            Elapsed time information for the frame
	 */
	public abstract void update(ElapsedTime elapsedTime);

	public void updateScreen(ElapsedTime elapsedTime){
		update(elapsedTime);
		//mAnimator.update(elapsedTime);
	}

	/**
	 * Draw the game screen. Invoked automatically from the game.
	 *
	 * @param elapsedTime
	 *            Elapsed time information for the frame
	 * @param graphics2D
	 *            Graphics instance used to draw the screen
	 */
	public abstract void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D);

	// /////////////////////////////////////////////////////////////////////////
	// Android Life Cycle
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @author Courtney Shek
	 *
	 * Invoked automatically by the game whenever the app is paused.
	 */
	public void pause()
	{
		if(backgroundMusic != null)
		{
			getBackgroundMusic().pause();
		}
	}

	/**
	 * @author Courtney Shek
	 *
	 * Invoked automatically by the game whenever the app is resumed.
	 */
	public void resume()
	{
		if(mGame.getPlayerProfile().getSoundEnabled() && backgroundMusic != null)
		{
			getBackgroundMusic().play();
		}
	}

	/**
	 * @author Courtney Shek
	 *
	 * Invoked automatically by the game whenever the app is disposed.
	 */
	public void dispose()
	{
		if(backgroundMusic != null)
		{
			getBackgroundMusic().stop();
		}
	}


	/**
	 * @author Courtney Shek
	 *
	 * Used to dispose of Bitmaps and Background music before moving on to the next screen.
	 */
	public void cleanScreen()
	{
		if(backgroundMusic != null)
		{
			backgroundMusic.dispose();
		}
	}

	/**
	 * Sets up music for the current gameScreen
	 *
	 * @param musicName - Name of the music being used
	 * @param musicLocation - Where the music is stored
	 * @return The background music
	 */
	public Music setUpMusic(String musicName, String musicLocation)
	{
		assetManager.loadAndAddMusic(musicName, musicLocation);
		backgroundMusic = assetManager.getMusic(musicName);

		if(mGame.getPlayerProfile().getSoundEnabled() && backgroundMusic != null)
		{
			backgroundMusic.setLopping(true);
			backgroundMusic.play();
		}

		return backgroundMusic;
	}

	/**
	 * @author Courtney Shek
	 *
	 * Sets up bitmaps used on the current gameScreen
	 *
	 * @param bitmapName - The name of the bitmap used
	 * @param bitmapLocation - Where the bitmap is stored
	 * @return The new bitmap
	 */
	public Bitmap setUpBitmap(String bitmapName, String bitmapLocation)
	{
		assetManager.loadAndAddBitmap(bitmapName, bitmapLocation);
		Bitmap bitmap = assetManager.getBitmap(bitmapName);

		return bitmap;
	}

	/**
	 * @author Courtney Shek
	 *
	 * Sets up rectangles that are used on gameScreens
	 */
	public void setUpRects()
	{

	}

	/**
	 * @author Courtney Shek
	 *
	 * Sets up the ScreenViewPort for the current GameScreen
	 */
	public void setUpScreenViewPort()
	{
		screenViewport = new ScreenViewport();
		GraphicsHelper.create3To2AspectRatioScreenViewport(mGame, screenViewport);
	}


}