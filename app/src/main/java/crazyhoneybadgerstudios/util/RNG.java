package crazyhoneybadgerstudios.util;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Dariusz Jerzewski on 06/05/2017.
 * a randon number generator for Long and Int, better than Random provided by Java
 */

public class RNG extends Random{
    //lock for thread safety
    private Lock lock = new ReentrantLock();
    //default values used for calculations
    private long x;
    private long y = 4101842887655102017L;
    private long z = 1;

    /**
     * default constructor without any seed
     */
    public RNG(){
        this(System.nanoTime());
    }

    /**
     * use long as a seed to number generator
     * @param seed
     */
    public RNG(long seed){
        lock.lock();
        x = seed ^ y;
        nextLong();
        y = x;
        nextLong();
        z = y;
        lock.unlock();
    }

    /**
     * @author Dariusz Jerzewski
     * xors
     * @return randomly generated long
     */
    public long nextLong(){
        lock.lock();
        try
        {
            //calculating starting value
            x = x * 2862933555777941757L + 7046029254386353087L;

            //xorshift the value
            y ^= y >>> 17;
            y ^= y << 31;
            y ^= y >>> 8;

            //calculate z
            z = 4294957665L * (z & 0xFFFFFFFF) + (z >>> 32);

            //xorshift newly obtained value
            long d = x ^ (x << 21);
            d ^= d >>> 35;
            d ^= d << 4;

            //calculate final value from precalculated variables
            long s = (d + y) ^ z;

            return s;
        }
        finally{
            lock.unlock();
        }
    }

    /**
     * @author Dariusz Jerzewski
     * generate seed from string
     */
    public static long generateSeed(String value){
        long seed = 0L;

        try{
            for(char c : value.toCharArray()){
                seed *= ((long) c);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return seed;
    }

    /**
     * @author Dariusz Jerzewski
     * get next int
     * @param bits - what is the range, in bits
     * @return
     */
    public int next(int bits){
        return (int) (nextLong() >>> (64-bits));
    }
}
