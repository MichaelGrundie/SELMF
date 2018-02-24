package crazyhoneybadgerstudios.engine.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import crazyhoneybadgerstudios.util.GraphicsHelper;

/**
 * @author Dariusz Jerzewski - 17/04/2017.
 * A class to parse a string to single bitmap representing it
 */

public class TextParser {

    /**
     * type of word spacing
     */
    public enum WordSpacing{
        WORD_BREAK,
        NORMAL
    }

    /**
     * char lists for comparisons, containing bitmaps of each allowed letter
     */
    private HashMap<String, Bitmap> numbers = new HashMap<>();
    private HashMap<String, Bitmap> lowerCase = new HashMap<>();
    private HashMap<String, Bitmap> upperCase = new HashMap<>();
    private HashMap<String, Bitmap> specialSigns = new HashMap<>();
    private HashMap<String, Bitmap> delimiters = new HashMap<>();

    //params necessary to print text to bitmap
    private WordSpacing wordSpacing = WordSpacing.NORMAL;
    private Paint paint;

    //for drawText to bitmap
    private IGraphics2D iGraphics2D;

    /**
     * Text parser constructor
     * @param fontPaint default paint used
     * @param defaultWordSpacing default word soacing
     * @param iGraphics2D rendering interface
     */
    public TextParser(Paint fontPaint, WordSpacing defaultWordSpacing, IGraphics2D iGraphics2D){
        wordSpacing = defaultWordSpacing;
        paint = fontPaint;
        this.iGraphics2D = iGraphics2D;

        loadCharsAsBitmapsToMaps(numbers, '0', '9');
        loadCharsAsBitmapsToMaps(lowerCase, 'a', 'z');
        loadCharsAsBitmapsToMaps(upperCase, 'A', 'Z');
        loadSpecialSigns();

    }

    /**
     * load all special signs into appropriate arrays
     */
    private void loadSpecialSigns(){
        Bitmap temp = iGraphics2D.drawText(String.valueOf(' '), paint);
        delimiters.put(String.valueOf(' '), temp);
        temp = iGraphics2D.drawText(String.valueOf(','), paint);
        delimiters.put(String.valueOf(','), temp);
        temp = iGraphics2D.drawText(String.valueOf('.'), paint);
        delimiters.put(String.valueOf('.'), temp);
        temp = iGraphics2D.drawText(String.valueOf('?'), paint);
        delimiters.put(String.valueOf('?'), temp);
        temp = iGraphics2D.drawText(String.valueOf('!'), paint);
        delimiters.put(String.valueOf('!'), temp);

        temp = iGraphics2D.drawText(String.valueOf(':'), paint);
        specialSigns.put(String.valueOf(':'), temp);
        temp = iGraphics2D.drawText(String.valueOf('_'), paint);
        specialSigns.put(String.valueOf('_'), temp);
        temp = iGraphics2D.drawText(String.valueOf('-'), paint);
        specialSigns.put(String.valueOf('-'), temp);
    }

    /**
     * load chars to hashmap by iterating through alphabet
     * @param collection
     * @param firstChar
     * @param lastChar
     */
    private void loadCharsAsBitmapsToMaps(HashMap<String, Bitmap> collection, char firstChar, char lastChar){
        if(iGraphics2D == null) return;

        for(;firstChar <= lastChar ;firstChar++){
            Bitmap charBitmap = iGraphics2D.drawText(String.valueOf(firstChar), paint);
            collection.put(String.valueOf(firstChar), charBitmap);
        }
    }

    /**
     * parse text from string
     * @param text text to be parsed
     * @param size final size of the text to be scaled to
     * @param width max width of one line of text
     * @return string as bitmap
     */
    public Bitmap parseText(String text, float size, float width){
        if(iGraphics2D == null) return null; // if null, return as it will crash

        switch(wordSpacing){
            case NORMAL:
                return mergeText(breakStringNormal(getBitmaps(text), size, width), size);
            case WORD_BREAK:
                return mergeText(breakStringWordBreak(getBitmaps(text), size , width), size);
            default: return null;
        }
    }


    /**
     * parse text withour scaling bitmaps
     * @param text to be converted
     * @param width max width of a single line
     * @return bitmap representing the string
     */
    public Bitmap parseText(String text, float width){
        if(iGraphics2D == null) return null;

        switch(wordSpacing){
            case NORMAL:
                return mergeText(breakStringNormal(getBitmaps(text), paint.getTextSize(), width), paint.getTextSize());
            case WORD_BREAK:
                return mergeText(breakStringWordBreak(getBitmaps(text),paint.getTextSize(), width),paint.getTextSize());
            default: return null;
        }
    }

    /**
     *
     * match string and add to result list
     * @param text to get bitmaps from
     * @return list of bitmaps representing string
     */
    private ArrayList<Bitmap> getBitmaps(String text){
        ArrayList<Bitmap> result = new ArrayList<>();
        for(char c : text.toCharArray()){
            if(Character.isUpperCase(c)){
                Bitmap temp = upperCase.get(String.valueOf(c));
                if (temp != null){
                    result.add(temp);
                }
            }
            else if(Character.isLowerCase(c)){
                Bitmap temp = lowerCase.get(String.valueOf(c));
                if (temp != null)
                    result.add(temp);
            }
            else if(delimiters.containsKey(String.valueOf(c))){
                Bitmap temp = delimiters.get(String.valueOf(c));
                if (temp != null)
                    result.add(temp);
            }
            else{
                Bitmap temp = specialSigns.get(String.valueOf(c));
                if (temp != null)
                    result.add(temp);
            }
        }return result;
    }

    /**
     * break string trying to not break the word in the middle
     * @param array list of bitmaps
     * @param size size of bitmap
     * @param width max width for line
     * @return each line from string
     */
    private ArrayList<Bitmap[]> breakStringNormal(ArrayList<Bitmap> array, float size, float width){
        int lineCount = 1; //initial line count increase if text needs to be broken
        int currentOffset = 0; // current posiyion

        //lists to store all bitmaps
        ArrayList<Bitmap[]> temp = new ArrayList<>();
        ArrayList<Bitmap> tempWorkingArr;

        //loop through string and add bitmaps to array
        for(int i = 0; i < lineCount; i++){
            float currentWidth = 0f;
            boolean breakWord = false;

            tempWorkingArr = new ArrayList<>();

            int index = 0;
            for(int j = currentOffset; currentWidth < width && j < array.size(); j++){
                if(j == currentOffset && delimiters.get(String.valueOf(" ")) == array.get(j)){
                    index++;
                    continue;
                }

                currentWidth += size;

                if(currentWidth >= width - size) {
                    breakWord = true;
                }

                tempWorkingArr.add(array.get(j));

                if(!breakWord) {
                    if (currentWidth > width *2/5) {
                        if(delimiters.containsValue(array.get(j))) {
                            lineCount++;
                            break;
                        }
                    }
                }else{
                        lineCount++;
                        break;
                }
            }
            currentOffset += tempWorkingArr.size() + index;

            //line complete, add it to line list
            temp.add(tempWorkingArr.toArray(new Bitmap[tempWorkingArr.size()]));
        }

        return temp;
    }

    /**
     * parse string without looking whether the words at the end of line will be broken or not
     * @param array list of bitmaps to process,
     * @param size size of bitmap char
     * @param width max width of line
     * @return lines of bitmaps
     */
    private ArrayList<Bitmap[]> breakStringWordBreak(ArrayList<Bitmap> array,float size, float width){
        int lineLength = (int)(width/size); // length of a line
        int lineCount = (int) Math.ceil((double) array.size()/lineLength); //amount of lines to print

        int offset = 0; //current position in array

        //arrays to hold the bitmaps
        ArrayList<Bitmap[]> temp = new ArrayList<>();
        ArrayList<Bitmap> tempWorkingArr;

        //iterate through all elements and build new array
        for(int i = 0; i < lineCount; i++){
            tempWorkingArr = new ArrayList<>();

            for(int j = offset; offset < j +lineLength && offset <array.size(); offset++){
                tempWorkingArr.add(array.get(offset));
            }

            temp.add(tempWorkingArr.toArray(new Bitmap[tempWorkingArr.size()]));
        }

        return temp;
    }

    /**
     * merge text into a single bitmap
     * @param arr array of bitmaps to merge
     * @param size size of bitmap to be converted to
     * @return bitmap representing the string
     */
    private Bitmap mergeText(ArrayList<Bitmap[]> arr, float size){
        float height = size*arr.size(); //height of the bitmap
        float width = 0; //width

        //calculate width
        for(Bitmap[] barr : arr){
            int tempWidth = 0;
            tempWidth = barr.length * (int)size;
            if(tempWidth > width)
                width = tempWidth;
        }

        //new canvas bitmap to print the chars to
        Bitmap image = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        int y = 0;

        //iterate through bitmap array and merge with image bitmap. adjust positions
        for(Bitmap[] barr : arr){
            int x = 0;
            for(Bitmap bmap : barr){
                double ratio = (bmap.getWidth()/paint.getTextSize());
                canvas.drawBitmap(bmap, null,
                        new Rect(x, y, x + (int)(bmap.getWidth() * ratio), y +  (int)(bmap.getHeight() * ratio)), paint);
                x+=size;
            }
            y+=size;
        }

        return  image;
    }
}
