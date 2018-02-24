package crazyhoneybadgerstudios.util;

import crazyhoneybadgerstudios.world.GameObject;

/**
 * @author Chloe McMullan - 13/04/2017.
 */

public class GameObjectMovement {

    private static final String TAG = "GameObjectMovement";

    /**
     * Allows you to animate a game object to a specified area
     * @param go game object to move
     * @param numOfUpdates number of updates to do the movement in
     * @param xSource x co-ord of the source position
     * @param ySource y co-ord of the source position
     * @param xDestination x co-ord of the destination position
     * @param yDestination y co-ord of the destination position
     * @return true if the movement has been completed, false otherwise.
     */
    public static boolean moveObject(GameObject go, int numOfUpdates, float xSource, float ySource,
                                     float xDestination, float yDestination) {
        float xDistance = xDestination - xSource; float yDistance = yDestination - ySource;

        float xDistort = xDistance / numOfUpdates;
        float yDistort = yDistance / numOfUpdates;

        go.setPosition(go.getBound().x + xDistort, go.getBound().y + yDistort);

        return go.getBound().x - xDestination < 1 && xDestination - go.getBound().x < 1; //allows error of 1 pixel
    }

    /**
     * Allows you to animate a game object to a specified area
     * @param go game object to move
     * @param numOfUpdates number of updates to do the movement in
     * @param source vector of the source position
     * @param dest vector of the destination position
     * @return true if the movement has been completed, false otherwise.
     */
    public static boolean moveObject(GameObject go, int numOfUpdates,
                                     Vector2 source, Vector2 dest) {
        float xDistance = dest.x - source.x; float yDistance = dest.y - source.y;

        float xDistort = xDistance / numOfUpdates;
        float yDistort = yDistance / numOfUpdates;

        go.setPosition(go.getBound().x + xDistort, go.getBound().y + yDistort);

        return go.getBound().x - dest.x < 1 && dest.x - go.getBound().x < 1; //allows error of 1 pixel
    }


}
