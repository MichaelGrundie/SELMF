package crazyhoneybadgerstudios.game.Game_Elements;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.engine.AssetStore;

/**
 * @author Jodie Burnside - 4/27/2017.
 */

public class AssetLoading
{

    //Menu Assets
    public static Bitmap background, playNowButton, loadButton, soundON, soundOFF;

    //Tutorial (LanyonLevel0) Assets
    public static Bitmap tutorialBackground, speakerOldMan;
    public static Bitmap[] tutorialTextBanners = new Bitmap[6]; //Holds the text banners for tutorial in order they should be displayed.

    //Character Creation Screen Assets
    public static Bitmap charCreationbackground, arrowRight, arrowLeft, confirmBt;
    public static Bitmap[] heads = new Bitmap[5];
    public static Bitmap[] robes = new Bitmap[4];

    //Map Screen Assets
    public static Bitmap suLevelDot, botanicLevelDot, csbLevelDot, suPopUp, botanicPopUp, csbPopUp, botanicPopUpLOCKED, csbPopUpLOCKED, redDash, backgroundMap;

    //Tile Map Screen Assets
    public static Bitmap mapSheet, player, leftControl, upControl, downControl, rightControl,
            enemy, professor, npcgirl, npcboy, battlePopUp, yesImage, noImage, okImage, message1,
            message2, message3, message4, advice1, advice2, advice3, advice4;

    //Pause Screen Assets
    public static Bitmap pauseBackground, buttonImage, warningPopUpImage, saveConfirmationBackground,
            resumeGameImage, returnToMapImage, saveGameImage, quitGameImage, helpImage;

    //HelperBook Screen Assets
    public static Bitmap bookBackgroundBlank, resume, howToPlayTutorial, startChoice, unimon, battleChoice, battleTutorial, battleTutorialContinued, end, yesButton, noButton;

    //Battle End Assets
    public static Bitmap defeat, victory, continueImg, prizeCardBackground;

    //Accessories Shop Assets
    public static Bitmap wizardsStaff, necklace, hairFlower, stallBackground, hairFlowerOUTOFSTOCK, necklaceOUTOFSTOCK, wizardsStaffOUTOFSTOCK, necklaceSALE, wizardsStaffSALE, hairFlowerSALE, stockPopUp, coinPopUp, buyPopUp;

    //Method load all bitmap assets for the loadMenu screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadMenu(AssetStore as)
    {
                //Background and buttons
                background = AssetLoading.setUpBitmap("background", "img/landscapeQUB.png", as);
                playNowButton = AssetLoading.setUpBitmap("playNow", "img/playnow.png", as);
                loadButton = AssetLoading.setUpBitmap("loadBt", "img/loadGame.png", as);

                //Load sound on/off icons
                soundON = AssetLoading.setUpBitmap("soundON", "img/soundON.png", as);
                soundOFF = AssetLoading.setUpBitmap("soundOFF", "img/soundOFF.png", as);

    }

    //Method load all bitmap assets for the tutorial/LanyonLevel0 screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadTutorial(AssetStore as)
    {
            tutorialBackground = AssetLoading.setUpBitmap("Lanyon Background", "img/lanyonEntrance.png", as);
            speakerOldMan = AssetLoading.setUpBitmap("Old Man", "img/tutorial/grumpyOldMan.png", as);
            tutorialTextBanners[0] = AssetLoading.setUpBitmap("tutorialText1", "img/tutorial/tutorialText1.png",  as);
            tutorialTextBanners[1] = AssetLoading.setUpBitmap("tutorialText2", "img/tutorial/tutorialText2.png",  as);
            tutorialTextBanners[2] = AssetLoading.setUpBitmap("tutorialText3", "img/tutorial/tutorialText3.png",  as);
            tutorialTextBanners[3] = AssetLoading.setUpBitmap("tutorialText4", "img/tutorial/tutorialText4.png",  as);
            tutorialTextBanners[4] = AssetLoading.setUpBitmap("tutorialText5", "img/tutorial/tutorialText5.png",  as);
            tutorialTextBanners[5] = AssetLoading.setUpBitmap("tutorialText6", "img/tutorial/tutorialText6.png",  as);
    }

    //Method load all bitmap assets for the character creation screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadCharacterCreation(AssetStore as)
    {
            // Screen Layout
            charCreationbackground =  AssetLoading.setUpBitmap("creationBackground", "img/characterCreation/background.png", as);
            arrowLeft = AssetLoading.setUpBitmap("arrowLeft", "img/characterCreation/leftArrow.png",as);
            arrowRight =   AssetLoading.setUpBitmap("arrowRight", "img/characterCreation/rightArrow.png", as);
            confirmBt = AssetLoading.setUpBitmap("confirmBt", "img/characterCreation/confirm.png", as);

            //Heads
            heads[0] = AssetLoading.setUpBitmap("femaleHead1", "img/characterCreation/femaleHead1.png", as);
            heads[1] = AssetLoading.setUpBitmap("femaleHead2", "img/characterCreation/femaleHead2.png", as);
            heads[2] = AssetLoading.setUpBitmap("unisexHead", "img/characterCreation/unisexHead.png", as);
            heads[3] = AssetLoading.setUpBitmap("maleHead1", "img/characterCreation/maleHead1.png", as);
            heads[4] = AssetLoading.setUpBitmap("maleHead2", "img/characterCreation/maleHead2.png", as);

            //Robes
            robes[0] = AssetLoading.setUpBitmap("robes1", "img/characterCreation/robes1.png", as);
            robes[1] = AssetLoading.setUpBitmap("robes2", "img/characterCreation/robes2.png", as);
            robes[2] = AssetLoading.setUpBitmap("robes3", "img/characterCreation/robes3.png", as);
            robes[3] = AssetLoading.setUpBitmap("robes4", "img/characterCreation/robes4.png", as);

    }

    //Method load all bitmap assets for the map screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadMapScreen(AssetStore as)
    {
            redDash = setUpBitmap("redDash", "img/map/red-dash.png", as);
            suLevelDot = setUpBitmap("mapDot", "img/map/mapDot.png",as);
            botanicLevelDot = setUpBitmap("mapDot", "img/map/mapDot.png", as);
            csbLevelDot = setUpBitmap("mapDot", "img/map/mapDot.png", as);
            backgroundMap = setUpBitmap("blankMap", "img/map/blankMap.png", as); //HERE
            suPopUp = setUpBitmap("suPopUp", "img/map/SUpopup.png", as);
            botanicPopUp = setUpBitmap("botanicPopUp", "img/map/Botanicpopup.png", as);
            csbPopUp = setUpBitmap("csbPopUp", "img/map/CSBpopup.png", as);
            botanicPopUpLOCKED = setUpBitmap("botanicPopUpLOCKED", "img/map/BotanicpopupLOCKED.png", as);
            csbPopUpLOCKED = setUpBitmap("csbPopUpLOCKED", "img/map/CSBpopupLOCKED.png", as);
    }

    //Method load all bitmap assets for the battle screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadBattleScreen(AssetStore as)
    {
        as.loadAndAddBitmap("BOARD", "img/Board.png");
        as.loadAndAddBitmap("UNIMONCARD", "img/CardImages/UnimonCard.png");
        as.loadAndAddBitmap("HUMANITARIANCARD", "img/CardImages/HumanitarianCard.png");
        as.loadAndAddBitmap("MEDICALCARD", "img/CardImages/MedicalCard.png");
        as.loadAndAddBitmap("STEMCARD", "img/CardImages/StemCard.png");
        as.loadAndAddBitmap("CARDHOLDER", "img/card_slot.png");
        as.loadAndAddBitmap("ATTACK", "img/CardImages/attack.png");
        as.loadAndAddBitmap("DEFENSE", "img/CardImages/defense.png");
        as.loadAndAddBitmap("HEALTH", "img/CardImages/health.png");
        as.loadAndAddBitmap("BASIC" , "img/CardImages/Basic.png");
        as.loadAndAddBitmap("HUMANITARIAN", "img/CardImages/Humanitarian.png");
        as.loadAndAddBitmap("MEDICAL", "img/CardImages/Medical.png");
        as.loadAndAddBitmap("STEM", "img/CardImages/Stem.png");
        as.loadAndAddBitmap("BUTTON", "img/button.png");
        as.loadAndAddBitmap("POPUP", "img/popUpScroll.png");
        as.loadAndAddBitmap("PLUS", "img/plus.png");
        as.loadAndAddBitmap("MINUS", "img/minus.png");
        as.loadAndAddBitmap("CHARGE","img/CardImages/ChargeBorder.png");
        as.loadAndAddBitmap("GREENBUTTON","img/CardImages/GreenButtonBox.png");
        as.loadAndAddBitmap("PAUSE", "img/Pause.png");
        as.loadAndAddBitmap("SKIPTURN", "img/red-button.png");
        as.loadAndAddBitmap("KO", "img/ko.png");
        as.loadAndAddSound("POP", "audio/pop.flac" );
        as.loadAndAddSound("ATTACKSOUND", "audio/attack.wav" );
        as.loadAndAddSound("CARDCLICK", "audio/cardClick.wav" );
        as.loadAndAddSound("CHARGESOUND", "audio/charged.wav" );
        as.loadAndAddSound("DEFENSEUP", "audio/defenseUp.wav" );
        as.loadAndAddSound("USEDEFENSE", "audio/defenseUsed.wav" );
        as.loadAndAddSound("DROPPOP", "audio/dropPop.wav" );
        as.loadAndAddSound("EVOLVE", "audio/evolve.mp3" );
        as.loadAndAddSound("ERROR", "audio/error.wav" );
        as.loadAndAddSound("HEALTHDOWN", "audio/healthDown.wav" );
        as.loadAndAddSound("HEALTHUP", "audio/healthUp.wav" );
            //Load battle end assets
            defeat = setUpBitmap("Defeat", "img/defeat.png", as);
            victory = setUpBitmap("Victory", "img/victory.png", as);
            continueImg = setUpBitmap("continueBt", "img/continueBt.png", as);
            prizeCardBackground = setUpBitmap("prizeBackground", "img/prizeCardBackground.png", as);
    }

    /**
     * Loads assets for the tile map screen
     *
     *
     * @author Courtney Shek (40147807)
     */
    public static void loadTileMapScreen(AssetStore as)
    {
            mapSheet = setUpBitmap("MapSheet", "img/TileMap/tileSet.png", as);
            player = setUpBitmap("Player", "img/TileMap/player.png",
                    as);
            upControl = setUpBitmap("UpControl", "img/TileMap/ArrowUp.png", as);
            downControl = setUpBitmap("DownControl", "img/TileMap/ArrowDown.png",
                    as);
            leftControl = setUpBitmap("LeftControl", "img/TileMap/ArrowLeft.png",
                    as);
            rightControl = setUpBitmap("RightControl", "img/TileMap/ArrowRight.png",
                    as);
            enemy = setUpBitmap("PlayerEnemy", "img/TileMap/enemy1.png",
                    as);
            professor = setUpBitmap("Professor", "img/TileMap/professor.png",as);
            npcboy = setUpBitmap("npcboy", "img/TileMap/npcboy.png", as);
            npcgirl = setUpBitmap("npcgirl", "img/TileMap/npcgirl1.png", as);
            battlePopUp = setUpBitmap("BattlePopUp", "img/TileMap/battlePopUp.png",
                    as);
            yesImage = setUpBitmap("YesButton", "img/PauseScreen/Yes.png", as);
            noImage = setUpBitmap("NoButton", "img/PauseScreen/No.png", as);
            okImage = setUpBitmap("OKButton", "img/PauseScreen/OK.png", as);
            message1 = setUpBitmap("Message1", "img/TileMap/npcMessage1.png", as);
            message2 = setUpBitmap("Message2", "img/TileMap/npcMessage2.png", as);
            message3 = setUpBitmap("Message3", "img/TileMap/npcMessage3.png", as);
            message4 = setUpBitmap("Message4", "img/TileMap/npcMessage4.png", as);
            advice1 = setUpBitmap("Advice1", "img/TileMap/advice1.png", as);
            advice2 = setUpBitmap("Advice2", "img/TileMap/advice2.png", as);
            advice3 = setUpBitmap("Advice3", "img/TileMap/advice3.png", as);
            advice4 = setUpBitmap("Advice4", "img/TileMap/advice4.png", as);
    }

    //Method load all bitmap assets for the pause screen
    //@params Game game
    //@author Jodie Burnside (40150039) and Courtney Shek (4014807)
    public static void loadPauseMenu(AssetStore as)
    {
            pauseBackground = setUpBitmap("pauseBackground", "img/Board.png",
                    as);
            buttonImage = setUpBitmap("Button", "img/button.png", as);
            warningPopUpImage = setUpBitmap("WarningPopup", "img/PauseScreen/WarningPopUp.png",
                    as);
            saveConfirmationBackground = setUpBitmap("SaveConfirmation",
                    "img/PauseScreen/SaveGameConfirmation.png", as);
            helpImage = setUpBitmap("Help", "img/PauseScreen/Help.png",as);
            quitGameImage = setUpBitmap("QuitGame", "img/PauseScreen/QuitGame.png",
                    as);
            resumeGameImage = setUpBitmap("ResumeGame", "img/PauseScreen/ResumeGame.png",
                    as);
            returnToMapImage = setUpBitmap("ReturnToMap", "img/PauseScreen/ReturnToMap.png",
                    as);
            saveGameImage = setUpBitmap("SaveGame", "img/PauseScreen/SaveGame.png",
                    as);

    }

    //Method load all bitmap assets for the helper book screen
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static void loadHelperBook(AssetStore as)
    {
            bookBackgroundBlank = AssetLoading.setUpBitmap("bookBackgroundBlank", "img/HelperBook/bookBackground.png", as);
            resume = AssetLoading.setUpBitmap("resumeBt", "img/HelperBook/resumeBt.png",as);
            howToPlayTutorial = AssetLoading.setUpBitmap("tutorialBt", "img/HelperBook/tutorialBt.png", as);
            startChoice = AssetLoading.setUpBitmap("startChoice", "img/HelperBook/help1.png", as);
            unimon = AssetLoading.setUpBitmap("BookUnimon", "img/HelperBook/help1-2.png", as);
            battleChoice = AssetLoading.setUpBitmap("battleChoice", "img/HelperBook/help2.png", as);
            battleTutorial = AssetLoading.setUpBitmap("battleTutorial", "img/HelperBook/help2-2.png", as);
            battleTutorialContinued = AssetLoading.setUpBitmap("battleTutorialContinued", "img/HelperBook/help2-2-2.png", as);
            end = AssetLoading.setUpBitmap("end", "img/HelperBook/helpEnd.png", as);
            yesButton = AssetLoading.setUpBitmap("yesTick", "img/HelperBook/tick.png", as);
            noButton = AssetLoading.setUpBitmap("noX", "img/HelperBook/x.png", as);
    }

    public static void loadAccessoriesShop(AssetStore as)
    {
            //basic item e.g. wizardsStaff is the bitmap image for drawing on top of character. SALE is the sale in store image. OUTOFSTOCK is the out of stock img.
            wizardsStaff = setUpBitmap("wizardsStaff", "img/store/wizardsStaff.png", as);
            necklace = setUpBitmap("necklace", "img/store/necklace.png", as);
            hairFlower = setUpBitmap("hairFlower", "img/store/hairFlower.png", as);
            stallBackground = setUpBitmap("stallBackground", "img/store/accStore.png", as);
            hairFlowerSALE = setUpBitmap("hairFlowerSALE", "img/store/hairFlowerSale.png", as);
            necklaceSALE = setUpBitmap("necklaceSALE", "img/store/necklaceSALE.png", as);
            wizardsStaffSALE = setUpBitmap("wizardsStaffSALE", "img/store/wizardsStaffSALE.png", as);
            hairFlowerOUTOFSTOCK = setUpBitmap("hairFlowerOUTOFSTOCK", "img/store/hairFlowerOUTOFSTOCK.png", as);
            necklaceOUTOFSTOCK = setUpBitmap("necklaceOUTOFSTOCK", "img/store/necklaceOUTOFSTOCK.png", as);
            wizardsStaffOUTOFSTOCK = setUpBitmap("wizardsStaffOUTOFSTOCK", "img/store/wizardsStaffOUTOFSTOCK.png", as);
            coinPopUp = setUpBitmap("coinPopUp", "img/store/noCoinPopUp.png", as);
            buyPopUp = setUpBitmap("buyPopUp", "img/store/buySuccess.png", as);
            stockPopUp = setUpBitmap("stockPopUp", "img/store/outOfStockPopUp.png", as);
    }


    //Method set up a bitmal, by loading it into the asset manager, and returns the already set up bitmap to assign into a variable.
    //@params Game game
    //@author Jodie Burnside (40150039)
    public static Bitmap setUpBitmap(String bitmapName, String bitmapLocation, AssetStore assetManager) //Load and assign a bitmap to a bitmap variable
    {
        try
        {
            assetManager.loadAndAddBitmap(bitmapName, bitmapLocation);
            Bitmap bitmap = assetManager.getBitmap(bitmapName);

            return bitmap;
        }
        catch(NullPointerException e)
        {
            // log the error
            e.printStackTrace();
            Log.d("AssetLoading", "update: One or more assets failed to load");
            return null;
        }
    }
}
