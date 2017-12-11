package ru.iisuslik.gameData;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class GameData implements Serializable {

    public ArrayList<TurnData> turns = new ArrayList<>();
    public Shuffle shuffle;


    public void serialize(OutputStream out) {
        try {
            ObjectOutputStream myOut = new ObjectOutputStream(out);
            myOut.writeObject(this);
            myOut.flush();
            myOut.close();
        } catch (Exception ignored) {
        }
    }

    public static GameData deserialize(InputStream in) {
        GameData next = null;
        try {
            ObjectInputStream myIn = new ObjectInputStream(in);
            next = (GameData) myIn.readObject();
            myIn.close();

        } catch (Exception ignored) {
        }
        return next;

    }
}
