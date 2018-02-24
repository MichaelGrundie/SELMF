package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.ScreenManager;
import crazyhoneybadgerstudios.engine.audio.Music;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Dariusz Jerzewski
 */

public class SplashScreen extends GameScreen {
    private boolean mMusicPlayed = false;
    private Rect mLogoRect;
    private Bitmap mLogoBitmap;
    private Music mSplashMusic;

    private double mSplashLength = 9.5;

    private double mSplashFade = 0.;

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game
     */
    public SplashScreen(Game game) {
        super("Splash Screen", game);

        mLogoBitmap = setUpBitmap("honeybadger", "img/honeybadger.png");

        mSplashMusic = setUpMusic("SplashMusic", "audio/SplashMusic.wav");
        mSplashMusic.setLopping(false);

        assetManager.loadAndAddBitmap("card_test", "img/card_test.png");

        int left = (int)((game.getScreenWidth() - 900)/2);
        int top = (int)((game.getScreenHeight() - 900)/2);
        int right =  game.getScreenWidth() - left;
        int bottom =  game.getScreenHeight() - top;


        mLogoRect = new Rect(left,top,right,bottom);

        try
        {
            //Pre-load assets during splash screen time
            AssetLoading.loadMenu(game.getAssetManager());
            AssetLoading.loadTutorial(game.getAssetManager());
            AssetLoading.loadCharacterCreation(game.getAssetManager());
            AssetLoading.loadMapScreen(game.getAssetManager());
            AssetLoading.loadTileMapScreen(game.getAssetManager());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("Splash Screen Loading: ", "One or more assets failed to load");
        }
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        mSplashLength -= elapsedTime.stepTime;
        if(!mMusicPlayed){
            mSplashMusic.play();
            mMusicPlayed = true;
        }else if(mSplashLength <= 0){
            mSplashMusic.stop();

            mGame.getScreenManager().removeScreen(this.getName());
            ScreenManager sm = mGame.getScreenManager();
            MenuScreen menuScreen = new MenuScreen(mGame);
            sm.addScreen(menuScreen);
            sm.setAsCurrentScreen("Menu Screen");


        }
    }
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        graphics2D.clear(Color.WHITE);
        if (mLogoBitmap != null){
            Paint alphaPaint = new Paint();
            if(mSplashLength >= 7.5 && mSplashFade < 1) {
                mSplashFade += elapsedTime.stepTime;
            }
            else if(mSplashLength > 0 && mSplashLength<= 1 && mSplashFade>0){
                mSplashFade -= elapsedTime.stepTime;
            }

            if(mSplashFade > 1 || (mSplashLength < 7.5 && mSplashLength > 1)) mSplashFade=1;
            else if(mSplashFade < 0) mSplashFade=0;

            alphaPaint.setAlpha((int) (mSplashFade * 255));
            graphics2D.drawBitmap(mLogoBitmap, null, mLogoRect, alphaPaint);
        }
    }

    /**
     * @author Courtney Shek
     *
     * Removes assets no longer used on this screen before moving onto the next one
     */
    public void cleanScreen()
    {
        mSplashMusic.dispose();
        mLogoBitmap.recycle();
    }

}
