package crazyhoneybadgerstudios.game.Game_Elements;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import crazyhoneybadgerstudios.Game;
import crazyhoneybadgerstudios.game.game_screens.BattleScreen;

/**
 * Created by Dariusz Jerzewski on 04/05/2017.
 * providing a way to serialise files as base 64 encoded strings
 */

public class GameState {

    public static void SaveObject(String objectName, Serializable object, Game mGame){
        String temp = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            temp = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(),0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mGame.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        if(temp != null)
            editor.putString(objectName, temp);
        else
            Log.d("state", "null");
        editor.commit();

        Log.d("state", "commited");
    }

    public static Object LoadObject(String objectName, Game mGame){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mGame.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        String string = prefs.getString(objectName, null);

        byte[] bytes = null;
        if(string != null)
            bytes = Base64.decode(string,0);
        else Log.d("string", "empty");

        Serializable object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream( new ByteArrayInputStream(bytes) );
            object = (Serializable)objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        return object;
    }
}
