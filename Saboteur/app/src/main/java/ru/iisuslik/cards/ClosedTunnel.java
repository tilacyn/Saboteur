package ru.iisuslik.cards;

import org.json.JSONArray;
import org.json.JSONException;

import ru.iisuslik.field.Field;

public class ClosedTunnel extends Tunnel {

    private boolean closed = true;
    private boolean gold;

    public ClosedTunnel(int[] tunnel, Field field) {
        super(tunnel, field);
        this.gold = tunnel[0] == 1;
        this.closed = true;
        closedTunnel = true;
    }

    public void open(){
        closed = false;
    }

    public boolean isGold() {
        return gold;
    }

    public boolean isClosed() {
        return closed;
    }
}