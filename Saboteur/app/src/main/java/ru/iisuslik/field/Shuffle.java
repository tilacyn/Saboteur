package ru.iisuslik.field;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Random;

import ru.iisuslik.player.Player;

public class Shuffle implements Serializable {
    public int [] closedTunnelsShuffle;
    public int [] deckShuffle;
    public int [] whoAreSaboteur;

    public Shuffle(int playersCount, int deckSize, int saboteurCount) {
        closedTunnelsShuffle = new int[]{0, 1, 2};
        whoAreSaboteur = new int[playersCount];
        deckShuffle = new int [deckSize];

        randomShuffle(closedTunnelsShuffle);
        for(int i = 0; i < deckSize; i++) {
            deckShuffle[i] = i;
        }
        randomShuffle(deckShuffle);
        for (int i = 0; i < playersCount; i++) {
            if (i < saboteurCount) {
                whoAreSaboteur[i] = Player.SABOTEUR;
            } else {
                whoAreSaboteur[i] = Player.GNOME;
            }
        }
        randomShuffle(whoAreSaboteur);
    }

    private void randomShuffle(int arr[]) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    public void serialize(OutputStream out) {
        try {
            ObjectOutputStream myOut = new ObjectOutputStream(out);
            myOut.writeObject(this);
            myOut.flush();
            myOut.close();
        } catch (Exception ignored) {
        }
    }

    public static Shuffle deserialize(InputStream in) {
        Shuffle next = null;
        try {
            ObjectInputStream myIn = new ObjectInputStream(in);
            next = (Shuffle) myIn.readObject();
            myIn.close();

        } catch (Exception ignored) {
        }
        return next;

    }

}
