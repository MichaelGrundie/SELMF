package crazyhoneybadgerstudios.util;

import android.util.Log;

import java.util.ArrayList;

/**
 * @author Dariusz Jerzewski
 * a cyclic queue implementation for easy animations
 * @param <T>
 */

public class CyclicQueue<T>{
    private int size; //current size of queue
    private int index; //current index, used for cycling through itemes
    private int max = 30; //default max items in queue
    private ArrayList<T> items; //arraylist of all items in the queue

    /**
     * constructor allowing to choose max size required by the user
     * @param targetSize
     */
    public CyclicQueue(int targetSize){
        this.size = 0;
        this.max = targetSize;
        items = new ArrayList<>(max);
    }

    /**
     * default constructor initialising the queue with default values
     */
    public CyclicQueue(){
        this.size = 0;
        items = new ArrayList<>(max);
    }

    /**
     * add an item to the list
     * @param item
     */
    public void enqueue(T item){
        if(size < max){
            items.add(item);
            size++;
        }
    }

    /**
     * add an array of items to the list
     * @param item
     */
    public void enqueue(T[] item){
        if(item.length + size < max){
            for(int i = 0; i < item.length; i++){
                items.add(item[i]);
                size++;
            }
        }
    }

    /**
     * get a first item in the queuee and remove it from the list
     * @return
     */
    public T dequeue(){
        T item = null;
        if(items.size() > 0){
            item = items.get(0);
            items.remove(0);
            size--;
        }
        return item;
    }

    /**
     * return current item, dont delete it
     * @return
     */
    public T peek(){
        T item = null;
        if(items.size() > 0 && index < size){
            item = items.get(index);
        }
        return item;
    }

    /**
     * return current item at an index location, dont delete it
     * @return
     */
    public T peek(int index){
        T item = null;
        if(items.size() > 0 && size > index){
            item = items.get(index);
        }
        return item;
    }

    /**
     * reset the current index
     */
    public void resetCounter(){
        index = 0;
    }

    /**
     * cycyle - similar to dequeue but dont remove item
     * @return
     */
    public T cycle(){
        T item = null;
        if(items.size() > 0 ){
            if(index < size){
                item = items.get(index);
                index++;
                if(index >= size)
                    index = 0;
            }
        }
        return item;
    }

    /**
     * get the current size of the list
     * @return
     */
    public int getSize(){
        return size;
    }

    /**
     * get maximum allowed size of the list
     * @return
     */
    public int getMax(){
        return max;
    }

    /**
     * get the current index
     * @return
     */
    public int getIndex(){
        return index;
    }

    /**
     * get last added value
     * @return
     */
    public T top(){
        T item = null;
        if(items.size() > 0 && size < max - 1){
            item = items.get(size);
        }
        return item;
    }

    /**
     * get the underlying array list
     * @return
     */
    public ArrayList<T> getList(){
        return items;
    }

    /**
     * get the underlying array list as an array
     * @return
     */
    public T[] getArray(){
        return (T[]) items.toArray();
    }
}
