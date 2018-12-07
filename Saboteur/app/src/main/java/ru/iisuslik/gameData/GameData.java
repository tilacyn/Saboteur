package ru.iisuslik.gameData;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;



public class GameData implements Serializable {

    public ArrayList<TurnData> turns = new ArrayList<>();
    public Shuffle shuffle;

    public void serialize(OutputStream out) throws IOException {
        Log.d("GGGGGGG", "turns are null? " + (turns == null));
        ObjectOutputStream myOut = new ObjectOutputStream(out);
        myOut.writeObject(this);
        myOut.flush();
        myOut.close();
    }

    public static GameData deserialize(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream myIn = new ObjectInputStream(in);
        GameData next = (GameData) myIn.readObject();
        myIn.close();
        return next;
    }
}
