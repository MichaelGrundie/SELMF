package crazyhoneybadgerstudios.game.game_screens;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.AssetLoading;
import crazyhoneybadgerstudios.game.Game_Elements.DeckBuilder;
import crazyhoneybadgerstudios.world.GameScreen;

/**
 * @author Jodie Burnside - 4/28/2017.
 */

public class BattleLoadingScreen extends GameScreen
{
    private Bitmap loadingBackground;
    private Rect backgroundRect;

    private DeckBuilder deckBuilder;
    private int battleLvlNum;

    public BattleLoadingScreen(Game game, int battleLevelNum)
    {
        super("Loading Screen", game);

        // Loading of Assets
        AssetStore assetManager = game.getAssetManager();
        assetManager.loadAndAddBitmap("loadingBackground", "img/loadingBackground.png");
        loadingBackground = assetManager.getBitmap("loadingBackground");

        setUpScreenViewPort();
        battleLvlNum = battleLevelNum;
        backgroundRect = new Rect(screenViewport.left, screenViewport.top, screenViewport.right, screenViewport.bottom);

        //Pre-load assets
        try
        {
            AssetLoading.loadPauseMenu(game.getAssetManager());
            AssetLoading.loadHelperBook(game.getAssetManager());
            AssetLoading.loadBattleScreen(game.getAssetManager());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("Battle Loading Screen: ", "One or more assets failed to load");
        }

    }

    public void update(ElapsedTime elapsedTime)
    {
            mGame.getScreenManager().removeScreen(this.getName());
            BattleScreen battleScreen = new BattleScreen(mGame, battleLvlNum);
            mGame.getScreenManager().addScreen(battleScreen);
    }

    public void draw(ElapsedTime elapsedTime, IGraphics2D iGraphics2D)
    {
        iGraphics2D.drawBitmap(loadingBackground, null, backgroundRect, null);
    }
}
