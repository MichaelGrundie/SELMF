package crazyhoneybadgerstudios.game.Game_Elements.CardClasses;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;
import crazyhoneybadgerstudios.engine.AssetStore;
import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.CanvasGraphics2D;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.ui.LayerViewportReleaseButton;
import crazyhoneybadgerstudios.util.GraphicsHelper;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.GameScreen;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author  Dairusz Jerzewski
 * @author  Michael Grundie
 */

public class UnimonCard extends Card{

    //unimon assigned to this card
    private Unimon unimon;

    //booleans to trigger updates and enable certain features
    private boolean increase = false;
    private boolean bitmapNeedsRefreshed = true;
    private boolean charged;
    private boolean isActive;

    //images to be displayed on the card
    private Bitmap card;
    private Bitmap unimonImage;

    //asset manager required to load assets, e.g. bitmaps
    private AssetStore assetStore;

    //border visible once card is charged
    private GameObject chargeBorder;

    //default paint used to print the text and draw bitmaps
    private Paint borderflash;

    //buttons to trigger attacks
    private LayerViewportReleaseButton basicArea, specialArea;

    //vars to be displayed on card
    private int defense;
    private int displayedHealth, displayedDefense;
    private int coolDown = 0;


    /**
     * constructor to create a new UnimonCard
     * @param width
     * @param height
     * @param unimon
     * @param gameScreen
     */
    public UnimonCard(float width, float height, Unimon unimon, GameScreen gameScreen)
    {
        super(gameScreen);
        this.unimon = unimon;
        name = unimon.getName();
        type = TypeOfCard.UNIMON;
        school = School.BASIC;
        mBound.halfWidth = width/2;
        mBound.halfHeight = height/2;
        assetStore = gameScreen.getGame().getAssetManager();
        mBitmap = assetStore.getBitmap("UNIMONCARD");
        card = mBitmap;
        unimonImage = assetStore.getBitmap(name);
        charged = false;
        chargeBorder = new GameObject(mBound.x,mBound.y,mBound.getWidth()+4, mBound.getHeight()+4,assetStore.getBitmap("CHARGE"),gameScreen);
        borderflash = new Paint();
        borderflash.setAlpha(255);
        displayedHealth = unimon.getHealth();
        displayedDefense = defense = 0;

    }

    /**
     * @author Courtney Shek
     *
     * Constructor for testing purposes
     *
     * @param unimon - Unimon attached to the card
     * @param gameScreen - GameScreen the card is used on
     * @param school - The School the card belongs to
     */
    public UnimonCard(Unimon unimon, GameScreen gameScreen, School school)
    {
        super(gameScreen, school);
        this.unimon = unimon;
        this.school = School.BASIC;
    }

    /**
     * @author Michael Grundie.
     */
    public void turnUpdates()
    {
        if(defense > 0)
        {
            defense = 0;
            bitmapNeedsRefreshed = true;
        }

    }

    /**
     * evolve card using schoolCard, set the card evolution school
     * @param school
     */
    public void evolveCard(School school)
    {
        this.school = school;
        unimon.evolve(school);
        bitmapNeedsRefreshed = true;
    }

    /**
     * get cooldown of special ability
     * @return
     */
    public int getCoolDown()
    {
        return coolDown;
    }

    /**
     * @author Michael Grundie.
     * @param toSetTo
     */
    public void setActive(boolean toSetTo)
    {
        isActive = toSetTo;
    }

    /**
     * @author Michael Grundie.
     */
    public void resetHealth()
    {
        unimon.resetHealth();
    }

    /**
     * @author Michael Grundie.
     * @return
     */
    public boolean isActive()
    {
        return isActive;
    }


    /**
     * @author Michael Grundie.
     *
     * Checks to see which, if any, of the final move buttons have been pressed.
     *
     * @return -
     *          Returns the school associated with the pressed button.
     */
    public School finalMoveSelected()
    {
        if (basicArea.pushTriggered())
        {
            if(coolDown > 0)
            {
                coolDown --;
                bitmapNeedsRefreshed = true;
            }
            return School.BASIC;

        }

        if(charged && coolDown == 0)
        {
            if (specialArea.pushTriggered()) {
                charged = false;
                coolDown = unimon.getSpecial().getCoolDown();
                return school;
            }
        }

        return null;
    }

    /**
     * @author Michael Grundie.
     * @return
     */
    public boolean isCharged()
    {
        return charged;
    }

    /**
     * @author Michael Grundie.
     */
    public void chargeEnergy(boolean chargeValue)
    {
        charged = chargeValue;
        bitmapNeedsRefreshed = true;
    }

    /**
     * @author Michael Grundie.
     * @return
     */
    public boolean isEvolved()
    {
        return school!= School.BASIC;
    }


    public int getHealth()
    {
        return unimon.getHealth();
    }

    public void setHealth(int health)
    {
        unimon.setHealth(health);
    }

    /**
     * @author Courtney Shek
     *
     * Returns the Unimon's temporary health
     *
     * @return The Unimon's temporary health
     */
    public int getTempHealth()
    {
        return unimon.getTempHealth();
    }

    /**
     * @author Courtney Shek
     *
     * Sets the Unimon's temporary health
     *
     * @param tempHealth - The Unimon's temporary Health
     */

    public void setTempHealth(int tempHealth)
    {
        unimon.setTempHealth(tempHealth);
    }

    public int getAttack() {
        return unimon.getAttack();
    }

    public int getDefense()
    {
        return defense;
    }

    public void setDefense(int defense)
    {
        this.defense = defense;
    }

    public void setCoolDown(int coolDown)
    {

        this.coolDown = coolDown;
        bitmapNeedsRefreshed = true;
    }

    public Bitmap getUnimonImage() { return unimonImage; }


    /**
     * @author Courtney Shek
     *
     * Returns the unimon attached to the card
     *
     * @return The Unimon attached to the card
     */
    public Unimon getUnimon()
    {
        return unimon;
    }

    public School getSchool() { return super.getSchool();}

    /**
     * @author v.1 Dairusz Jerzewski - Idea and initial testing.
     *
     * @author v.2 Michael Grundie - Positioned and sized all the elements for different variations
     *                               of the card. (Current card layout).
     *
     * This method positions all the elements of the card and has them merged into 1
     * bitmap that can be assigned to the card.
     */
    private void makeNewBitmap()
    {

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ArrayList<Rect> rects = new ArrayList<>();

        int rowHeight  = card.getHeight()/10;
        int firstRowY = (rowHeight * 5) - (rowHeight/2);
        int widthAndHeight = card.getWidth()/5;
        int colGap = widthAndHeight/5;
        int rowGap = 40;

        Paint statValuesPaint = new Paint();
        statValuesPaint.setTextSize(60);
        statValuesPaint.setColor(Color.RED);


        Paint text = new Paint();
        int txt = card.getWidth()/ Math.max(10,name.length());
        text.setTextSize(40);
        text.setColor(Color.BLACK);

        IGraphics2D graphics2D = new CanvasGraphics2D(mGameScreen.getGame().getActivity().getAssets());
        //TEXT
        Bitmap health = graphics2D.drawText(String.valueOf(displayedHealth), statValuesPaint);
        Bitmap defenseBit = graphics2D.drawText(String.valueOf(displayedDefense), statValuesPaint);

        Bitmap uname = graphics2D.drawText(name, text);


        Rect cardRect = new Rect(0, 0, card.getWidth(),card.getHeight());

        Rect unimonRect = new Rect(43, 72, card.getWidth() - 43, 250);


        Rect healthRect = new Rect(cardRect.right-colGap-widthAndHeight,rowHeight*9,cardRect.right-colGap,cardRect.bottom);
        Rect healthIconRect = new Rect(healthRect.left-widthAndHeight,rowHeight*9,healthRect.left,cardRect.bottom);
        Rect nameRect = new Rect(2, 2,2+ txt*name.length(),2+txt);

        Rect defenseRect = new Rect(cardRect.left+colGap,rowHeight*9,cardRect.left+widthAndHeight+colGap,cardRect.bottom);
        Rect defenseIconRect = new Rect(defenseRect.right,rowHeight*9,defenseRect.right+ widthAndHeight,cardRect.bottom);


        bitmaps.add(card);
        bitmaps.add(unimonImage);
        bitmaps.add(uname);
        bitmaps.add(health);
        bitmaps.add(assetStore.getBitmap("HEALTH"));
        bitmaps.add(defenseBit);
        bitmaps.add((assetStore.getBitmap("DEFENSE")));



        rects.add(cardRect);
        rects.add(unimonRect);
        rects.add(nameRect);
        rects.add(healthRect);
        rects.add(healthIconRect);
        rects.add(defenseRect);
        rects.add(defenseIconRect);

        widthAndHeight= card.getWidth()/6;
        colGap = (2*widthAndHeight)/5;


        Rect basicRect = new Rect(colGap, firstRowY, colGap+widthAndHeight, firstRowY+rowHeight);
        Rect attackRect = new Rect(basicRect.right + colGap, basicRect.top, basicRect.right + colGap + widthAndHeight, basicRect.bottom);


        Rect attRect = new Rect(attackRect.right + (2*colGap) +widthAndHeight, attackRect.top, attackRect.right + (2*colGap) + (2*widthAndHeight), attackRect.bottom);

        Bitmap attVal = graphics2D.drawText(String.valueOf(unimon.getAttack()), statValuesPaint);

        rects.add(basicRect);
        rects.add(attackRect);
        rects.add(attRect);

        bitmaps.add(assetStore.getBitmap("BASIC"));
        bitmaps.add(assetStore.getBitmap("ATTACK"));
        bitmaps.add(attVal);


        if(school != School.BASIC)
        {
            /**
             * Merges the stats relevant to the currently upgraded to school into 1 bitmap.
             */
            bitmaps.add(assetStore.getBitmap(school.name()));
            Specials.TypeOfSpecial special = unimon.getSpecial().getType();
            bitmaps.add(assetStore.getBitmap(special.name()));
            int coolDownVal = coolDown;
            if(coolDownVal>20)
            {
                bitmaps.add(graphics2D.drawText("!",statValuesPaint));
            }
            else bitmaps.add(graphics2D.drawText(Integer.toString(coolDownVal),statValuesPaint));

            Bitmap atVal = graphics2D.drawText(String.valueOf(unimon.getSpecial().getValue()), statValuesPaint);
            bitmaps.add(atVal);


            Rect sRect = new Rect(colGap, basicRect.bottom + rowGap, widthAndHeight + colGap, basicRect.bottom+rowGap+rowHeight);
            Rect aRect = new Rect(sRect.right + colGap, sRect.top, sRect.right + widthAndHeight+ colGap, sRect.bottom);
            Rect cooldownRect = new Rect(aRect.right+colGap,aRect.top,aRect.right+colGap+widthAndHeight,aRect.bottom);
            Rect atRect = new Rect(cooldownRect.right + colGap, cooldownRect.top, cooldownRect.right + widthAndHeight + colGap, aRect.bottom);

            rects.add(sRect);
            rects.add(aRect);
            rects.add(cooldownRect);
            rects.add(atRect);
        }
        else
        {
            /**
             * Merges all the school stats for an un-evolved unimon into 1 Bitmap.
             * This allows the player to see the possible upgrade stats each school can have
             * on the card.
             */
            //ORDER FROM TOP TO BOTTOM = MED, STEM, HUM!
            Specials[] specials = unimon.getAllSpecials();
            for (int i = 1; i<4; i++)
            {

                bitmaps.add(assetStore.getBitmap(i == 1 ? "MEDICAL" : i == 2 ? "STEM" : "HUMANITARIAN"));
                bitmaps.add(assetStore.getBitmap(specials[i-1].getType().name()));

                int coolDownVal = specials[i-1].getCoolDown();
                if(coolDownVal>20)
                {
                    bitmaps.add(graphics2D.drawText("!",statValuesPaint));
                }
                else bitmaps.add(graphics2D.drawText(Integer.toString(coolDownVal),statValuesPaint));

                bitmaps.add(graphics2D.drawText(Integer.toString(specials[i-1].getValue()), statValuesPaint));


                for(int j = 0; j<4; j++)
                {
                    int left  = (j+1 * colGap) + (j*widthAndHeight) + (j*colGap);
                    int top = basicRect.top + (i* (widthAndHeight));
                    int right = left + widthAndHeight;
                    int bottom = top + widthAndHeight;

                    rects.add(new Rect(left,top,right,bottom));
                }
            }
        }


        mBitmap = GraphicsHelper.mergeBitmaps(bitmaps.toArray(new Bitmap[bitmaps.size()]),
                rects.toArray(new Rect[rects.size()]), new Paint());
    }


    /**
     * @author Michael Grundie.
     *
     * @param elapsedTime
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        super.update(elapsedTime);

        if(selected && basicArea == null && isActive)
        {
            createButtons();
        }


        if(!selected)
        {
            destroyButtons();
        }

        if (selected  && isActive && ((BattleScreen)mGameScreen).getTurnCount() >1)
        {
            updateButtons(elapsedTime);
        }

        updateChangableStatValues();


        if(bitmapNeedsRefreshed)
        {

            makeNewBitmap();
            bitmapNeedsRefreshed = false;
        }

        /**
         * Moves the charge border with the card and
         */
        if(charged)
        {
            updateChargeBorder();
        }
    }

    /**
     * @author Michael Grundie.
     *
     * Creates the basic button when the card is active, selected, and the turnCount is above 1.
     * (Players can not attack on their set-up turn)
     */
    private void createButtons()
    {

        if(((BattleScreen)mGameScreen).getTurnCount() >1 ) {

            float y = mBound.getTop() - mBound.getHeight() / 10 * 5;
            basicArea = new LayerViewportReleaseButton(mBound.x, y, mBound.getWidth(), mBound.getHeight() / 10, "GREENBUTTON", null, mGameScreen);
            if (charged && specialArea == null && coolDown == 0) {
                /**
                 * Creates the special button when the card is active, selected, and charged.
                 */
                specialArea = new LayerViewportReleaseButton(mBound.x, y - 10 - mBound.getHeight() / 10, mBound.getWidth(), mBound.getHeight() / 10, "GREENBUTTON", null, mGameScreen);
            }
        }
    }

    /**
     * @author Michael Grundie
     */
    private void destroyButtons()
    {
        if (basicArea != null) basicArea = null;
        if (specialArea!= null) specialArea = null;
    }

    /**
     * @author Michael Grundie
     */
    private void updateButtons(ElapsedTime elapsedTime)
    {
        basicArea.update(elapsedTime);
        if(charged && coolDown==0)
        {
            specialArea.update(elapsedTime);
        }
    }

    /**
     * @author Michael Grundie
     */
    private void updateChangableStatValues()
    {
        if(displayedDefense > defense)
        {
            displayedDefense --;
            bitmapNeedsRefreshed =true;
        }
        if(displayedDefense< defense)
        {
            displayedDefense++;
            bitmapNeedsRefreshed =true;
        }
        if(displayedHealth > unimon.getHealth() && displayedDefense == defense)
        {
            displayedHealth--;
            bitmapNeedsRefreshed =true;
        }
        if(displayedHealth<unimon.getHealth())
        {
            displayedHealth++;
            bitmapNeedsRefreshed =true;
        }
    }

    /**
     * @author Michael Grundie
     */
    private void updateChargeBorder()
    {
        chargeBorder.setPosition(mBound.x,mBound.y);
        chargeBorder.getBound().halfWidth = mBound.halfWidth+4;
        chargeBorder.getBound().halfHeight = mBound.halfHeight+4;
        if (increase && borderflash.getAlpha() >=255) increase = false;
        if(!increase && borderflash.getAlpha()<=50) increase = true;
        if(increase) borderflash.setAlpha(borderflash.getAlpha()+10);
        else borderflash.setAlpha(borderflash.getAlpha()-10);
    }


    /**
     * @author Michael Grundie.
     *
     *
     * @param elapsedTime
     *            Elapsed time information
     * @param graphics2D
     *            Graphics instance
     * @param layerViewport
     *            Game layer viewport
     * @param screenViewport
     *            Screen viewport
     * @param paint
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D, LayerViewport layerViewport,
                     ScreenViewport screenViewport, Paint paint) {

        super.draw(elapsedTime, graphics2D, layerViewport, screenViewport, paint);

        if(isActive && selected && ((BattleScreen)mGameScreen).getTurnCount() >1)
        {
            basicArea.draw(elapsedTime, graphics2D, layerViewport, screenViewport);

            if (charged && coolDown==0)
            {
                specialArea.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
            }
        }

        if (charged)
        {
            chargeBorder.draw(elapsedTime, graphics2D, layerViewport, screenViewport, borderflash);
        }
    }



}
