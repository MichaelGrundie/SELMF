package crazyhoneybadgerstudios.util;


import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import crazyhoneybadgerstudios.world.GameObject;

public class LayoutHelper {

    /**
     * @author Dariusz Jerzewski
     * Create array list of game objects based of max width of container
     * @param maxWidth
     * @param gameObjects
     * @return
     */
    private static ArrayList<ArrayList<GameObject>> getRows(float maxWidth, ArrayList<GameObject> gameObjects){
        if (gameObjects.size() == 0){
            return null;
        }

        ArrayList<ArrayList<GameObject>> rows = new ArrayList<>(); //an empty array list to get all the rows
        ArrayList<GameObject> temp = new ArrayList<>(gameObjects); //copy of original array
        ArrayList<GameObject> row = new ArrayList<>(); //current row
        ArrayList<Integer> arr = new ArrayList<>(); // array of indicies to remove from copy of array
        int count = 0;
        int rowWidth = 0;

        //get rows basing on max width
        if(gameObjects.size() == 1){
            row.add(gameObjects.get(0));
            rows.add(row);
            return rows;
        }
        else{
            for(GameObject go : gameObjects){
                if(row.size() < 1 || rowWidth < maxWidth){
                    row.add(go);
                    rowWidth += go.getBound().getWidth();
                    arr.add(gameObjects.indexOf(go));
                }
            }

            for(Integer in : arr){
                temp.remove(gameObjects.get(in));
            }

            if(temp.size() > 0){
                rows.addAll(getRows(maxWidth, temp));
            }
        }

        rows.add(row);
        return rows;
    }

    public static void CreateGridLayout(float containerX, float containerY, float containerWidth, float marginHorizontal, float marginVertical, ArrayList<GameObject> gameObjects){
        ArrayList<ArrayList<GameObject>> rows = getRows(containerWidth, gameObjects);
        CreateGridLayoutFromRows(containerX, containerY, containerWidth, marginHorizontal, marginVertical, rows);
    }

    /**
     * @author v.1 Dariusz Jerzewski
     * @author v.2 Michael Grundie - Spent a number of hours debugging/ adjusting values of this
     *                               methoud in order to display the holders correctly.
     *
     *
     * @param containerX - top left corner of rect
     * @param containerY - top left corner of rect
     * @param containerWidth - container width
     * @param marginHorizontal - margin to add between objects horizontally
     * @param marginVertical - margin to add between object vertically
     * @param gameObjectRows - game object array list of array list
     */
    public static void CreateGridLayoutFromRows(float containerX, float containerY, float containerWidth, float marginHorizontal, float marginVertical, ArrayList<ArrayList<GameObject>> gameObjectRows){
        float prevRowHeight =  containerY + marginHorizontal;
        for(ArrayList<GameObject> row : gameObjectRows){
            if(row.size() == 0){
                continue;
            }
            else if(row.size() == 1){
                for (GameObject go : row) {
                    go.setPosition((containerWidth / 2) +  containerX, prevRowHeight + go.getBound().getHeight()/2);
                    prevRowHeight = prevRowHeight + (go.getBound().getHeight() + marginVertical);
                }
            }
            else {
                float totalRowWidth = 0;
                float maxRowHeight = 0;
                for (GameObject go : row) {
                    totalRowWidth += (go.getBound().getWidth());
                    if (go.getBound().getHeight() > maxRowHeight) {
                        maxRowHeight = go.getBound().getHeight();
                    }
                }

                int marginNo = row.size() >= 2 ? row.size() - 1 : 0;
                float rowStartPosX = (containerWidth - ((marginHorizontal * marginNo) + totalRowWidth)) / 2;
                for (GameObject go : row) {
                    go.setPosition(rowStartPosX + go.getBound().halfWidth + containerX, prevRowHeight + go.getBound().getHeight()/2);
                    rowStartPosX += (go.getBound().getWidth() + marginHorizontal);
                }
                prevRowHeight = prevRowHeight + (maxRowHeight + marginVertical);
            }
        }
    }


    /**
     * Splits a rectangle into numOfCards number of rectangles, each occupying an even space
     * @param cardNum the 'space' of the matrix you wish this to occupy (starts at 0)
     * @param numOfCards Number of rects the original space should be split into
     * @param space the space to split up
     * @param numOfColumns The number of columns the space should have
     * @return a rect containing the padded bounds
     */
    public static Rect workOutRectPos(int cardNum, int numOfCards, Rect space, int numOfColumns) {

        int numOfRows = numOfCards / numOfColumns;
        int sectionWidth = space.width()/ numOfColumns;
        int sectionHeight = space.height() / numOfRows;

        //rect of the correct size, just needs place in the correct area
        Rect rect = new Rect(space.left, space.top,
                space.left + sectionWidth, space.top + sectionHeight);

        //places in the correct area
        int positionInRow = cardNum % numOfColumns;
        int positionInColumn = cardNum / numOfColumns;
        rect.offset(((space.width() / numOfColumns) * positionInRow),
                (space.height() / numOfRows)* positionInColumn);
        return rect;
    }
}
