package crazyhoneybadgerstudios;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Window;
import android.view.WindowManager;

import crazyhoneybadgerstudios.selmf.R;

//This is the main and only activity.
public class MainActivity extends Activity {

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Adding flags to set the game window up.
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Sets content view to a frame layout.
        setContentView(R.layout.activity_fragment);

        //Adds in the game fragment.
        FragmentManager fragManager = getFragmentManager();
        Game game = (Game)fragManager.findFragmentById(R.id.activity_fragment_id);


        if(game==null){
            game = new Game();
            fragManager.beginTransaction().add(
                    R.id.activity_fragment_id, game).commit();

        }

    }



}
