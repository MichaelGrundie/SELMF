package crazyhoneybadgerstudios.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dariusz Jerzewski on 06/05/2017.
 */

public class PerlinNoise {
    private RNG rng;//random number generator, initialised with a seed by constructor
    private int DIM = 2; //dimesnsions in our game we use 2d rendering, but can be easily changed to support 3d if needed.

    //arrays to store the vectors used to calculate the noise
    private ArrayList<Vector2> corners = new ArrayList<>();
    private HashMap<Vector2,Vector2> gradients = new HashMap<>();

    //seed  used for RNG
    private long seed;

    /**
     * constructor with long seed, does not need any conversions
     * @param seed
     */
    public PerlinNoise(long seed){
        rng = new RNG(seed);
        this.seed = seed;

        calculateCorners();
    }

    /**
     * constructor accepting string as a seed, generates a long from it and passes it to RNG
     * @param seed
     */
    public PerlinNoise(String seed){
        this.seed = RNG.generateSeed(seed);
        rng = new RNG(this.seed);

        calculateCorners();
    }

    /**
     * @author Dariusz Jerzewki
     * initialising vectors to calculate noise
     */
    private void calculateCorners(){
        for(int i = 0; i < 1 << DIM; i++){
            float[] temp = new float[DIM];
            for(int j = 0; j < DIM; j++){
                if((i & (1 << j)) != 0){
                    temp[j] = 1;
                }else{
                    temp[j] = 0;
                }
            }
            Vector2 nv = new Vector2(temp[0], temp[1]);
            corners.add(nv);
        }
    }

    /**
     * @author Dariusz Jerzewski
     * @param pos
     * @return gradient in specified position
     */
    public Vector2 grad(Vector2 pos){
        Vector2 temp = gradients.get(pos);
        if(temp == null){
            float[] vec = new float[DIM];
//            for(int i = 0; i < DIM; i++){
//                vec[i] = MathHelper.linearConvertion(rng.nextLong()) - 0.5f;
                vec[0] = 2 * pos.x * pos.y;
                vec[1] = 2 * pos.x * pos.y;
//            }
            temp = new Vector2(vec[0], vec[1]);
            temp.normalise();
            gradients.put(pos, temp);
        }
        return temp;
    }

    /**
     * @author Dariusz Jerzewski
     * get sample at specified position
     * @param pos
     * @return noise value between -1 and 1
     */
    public float sample(Vector2 pos){
        float total = 0; //value to return

        //calculate weight for the gradient calculation
        Vector2 temp =  Vector2.floor(new Vector2(pos));

        Vector2 a = new Vector2();


        //calculate the height basing on gradient vectors and corner vectors
        //using perlin algorhitm
        for(Vector2 v : corners){
            Vector2 q = new Vector2(temp.x, temp.y);
            q.add(v);
            Vector2 tmpG = new Vector2(grad(q));
            Vector2 g = new Vector2(tmpG);
            Vector2 tmp = new Vector2(pos.x, pos.y);
            tmp.subtract(q);
            double m = g.dot(tmp);
            Vector2 t = new Vector2(pos.x, pos.y);
            t.subtract(q);
            if(t.x < 0.f)
                t.x = Math.abs(t.x);
            if(t.y < 0.f)
                t.y = Math.abs(t.y);
            t.multiply(-1f);
            t.add(1f,1f);
            Vector2 w = new Vector2(t.x, t.y);
            w.x = (float)Math.pow(w.x, 2f);
            w.y = (float)Math.pow(w.y, 2f);
            w.multiply(3);
            tmp = new Vector2(t.x, t.y);
            tmp.x = (float) Math.pow(tmp.x, 3);
            tmp.y = (float) Math.pow(tmp.y, 3);
            tmp.multiply(2);
            w.subtract(tmp);
            total += w.x * w.y * m;
        }

        return total;
    }

    /**
     * get sample with inverted negative numbers
     * @param pos
     * @return
     */
    public float sampleClampNegative(Vector2 pos){
        return Math.abs(sample(pos));
    }

    /**
     * reset the RNG
     */
    public void resetRNG(){
        rng = new RNG(seed);
    }
}
