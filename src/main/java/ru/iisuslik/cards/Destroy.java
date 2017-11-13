package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Destroy extends Action {
    public Destroy(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber, DESTROY);
        this.id = 0;
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !field.didCurrentPlayerPlayCard() && toCheck != null && !toCheck.name.equals("Entry") && !toCheck.isClosedTunnel();
    }

    public void play(int i, int j) {
        field.field[i][j] = null;
        field.iPlayedCard();
    }
}
