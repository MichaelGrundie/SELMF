package crazyhoneybadgerstudios.game.Game_Elements.CardContainingStructures;

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashMap;

import crazyhoneybadgerstudios.engine.ElapsedTime;
import crazyhoneybadgerstudios.engine.graphics.IGraphics2D;
import crazyhoneybadgerstudios.game.Game_Elements.CardClasses.Card;
import crazyhoneybadgerstudios.game.Game_Elements.PlayArea;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;
import crazyhoneybadgerstudios.world.GameObject;
import crazyhoneybadgerstudios.world.LayerViewport;
import crazyhoneybadgerstudios.world.ScreenViewport;

/**
 * @author Dariusz Jerzewski
 * store mutiple card holders to represent playable areas
 */
public class CardHolderContainer{
    private int currentNumberOfContainers = 0; // number of containers stored in the object
    private int maxNumberOfContainers; //max allowed number of containers
    private String[] keys; //name of each container
    private HashMap<String, ArrayList<GameObject>> containers; //stored containers in a hashmap

    /**
     * constructor initialising new object
     * @param size max allowed containers
     */
    public CardHolderContainer(int size)
    {
        keys = new String[size];
        containers = new HashMap<>(size);
        maxNumberOfContainers = size;
    }

    /**
     * add a card to a selected container
     * @param container
     * @param card
     */
    public void addCardToCardHolder(String container, Card card)
    {
        for ( GameObject holder: containers.get(container))
        {
            CardHolder Holder = (CardHolder) holder;
            if(Holder.getCard() == null) {
                Holder.setCard(card);
                return;
            }
        }
    }

    /**
     * add a card to holder at specific location
     * @param container
     * @param x
     * @param y
     * @param card
     */
    public void addCardToCardHolder(String container, float x, float y, Card card)
    {
        for ( GameObject holder: containers.get(container))
        {
            CardHolder Holder = (CardHolder) holder;
            if(Holder.getBound().contains( x,  y)) {
                if(Holder.getCard() == null){
                    Holder.setCard(card);
                    return;
                }
                else{
                    addCardToCardHolder(container, card);
                }
            }
        }
    }

    /**
     * add new container with new list of holders
     * @param containerName
     * @param container
     */
    public void addRow(String containerName, ArrayList<GameObject> container)
    {
        keys[currentNumberOfContainers] = containerName;
        currentNumberOfContainers++;
        if(containers.size() < maxNumberOfContainers)
            containers.put(containerName, container);
    }

    //get hashmap of all containers
    public HashMap<String, ArrayList<GameObject>> getContainers()
    {
        return containers;
    }

    //return all containers as list
    public ArrayList<ArrayList<GameObject>> getArrayListContainers()
    {
        ArrayList<ArrayList<GameObject>> temp = new ArrayList<>(maxNumberOfContainers ==2? 2:3);

        if (maxNumberOfContainers==3) {
            temp.add(containers.get("HAND"));
            temp.add(containers.get("BENCH"));
            temp.add(containers.get("ACTIVE"));
        }else
        {
            temp.add(containers.get("ACTIVE"));
            temp.add(containers.get("BENCH"));
        }
        return temp;
    }


    /**
     * @author Chloe McMullan
     * @param area area to get the containers from
     * @return array containing an arraylist of the containers
     */
    public ArrayList<GameObject> getSpecificContainer(String area) {
        ArrayList<GameObject> arr = new ArrayList<>();

        if (containers.containsKey(area)) arr = containers.get(area);

        return arr;
    }

    /**
     * initialise new empty containers
     * @param lengths
     * @param keys
     * @param battleScreen
     */
    public void setupEmptyContainers(int[] lengths, String[] keys, BattleScreen battleScreen){
        if(lengths.length == maxNumberOfContainers)
        {
            ArrayList<GameObject> temp;
            for(int i = 0 ; i < lengths.length; i++){
                temp = new ArrayList<>(lengths[i]);
                for(int j = 0 ; j < lengths[i]; j++){
                    PlayArea slotType;
                    switch(keys[i])
                    {
                        case "HAND": slotType = PlayArea.HAND;
                            break;
                        case"BENCH": slotType = PlayArea.BENCH;
                            break;
                        default: slotType = PlayArea.ACTIVE;
                            break;
                    }

                    temp.add(new CardHolder(0,0, battleScreen, slotType));
                }
                containers.put(keys[i], temp);
            }
        }
    }

    /**
     * draw holders
     * @param elapsedTime
     * @param iGraphics2D
     * @param layerViewport
     * @param screenViewport
     * @param paint
     */
    public void drawCardHolders(ElapsedTime elapsedTime, IGraphics2D iGraphics2D, LayerViewport layerViewport, ScreenViewport screenViewport, Paint paint)
    {
        for(ArrayList<GameObject> gameObjectArrayList : containers.values())
        {
            for(GameObject gameObject : gameObjectArrayList)
            {
                gameObject.draw(elapsedTime,iGraphics2D,layerViewport,screenViewport,paint);
            }
        }
    }

    /**
     * move a card between two containers
     * @param origin
     * @param target
     * @param card
     * @param x
     * @param y
     * @param index
     */
    public void moveCardsBetweenContainers(String origin, String target, Card card, float x, float y, int index){
        containers.get(origin).remove(index);
        addCardToCardHolder(target, x, y, card);
    }
}
