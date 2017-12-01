package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.GameData;

public class Destroy extends Action {
    public Destroy(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber, DESTROY);
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !(i == Field.ENTRY_POS_I && j == Field.ENTRY_POS_J) &&
                !field.didCurrentPlayerPlayCard() &&
                toCheck != null && !toCheck.isClosedTunnel();
    }

    public void play(int i, int j, boolean needToSend) {
        field.field[i][j] = null;
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        if (needToSend) {
            field.currentGD = new GameData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    i, j, -1, this) {
                @Override
                public void apply(Card card) {
                    ((Destroy) card).play(i, j, false);
                }
            };
        }
    }

    public void play(int i, int j) {
        play(i, j, true);
    }
}
