package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Watch extends Action {
    public Watch(int id, String name, String description, Field field, int playerNumber) {
        super(id, name, description, field, playerNumber, WATCH);
    }

    public boolean canPlay(int i, int j) {
        Tunnel toCheck = field.field[i][j];
        return !field.didCurrentPlayerPlayCard() && toCheck != null && toCheck.isClosedTunnel();
    }

    public boolean play(int i, int j, boolean needToSend) {
        ClosedTunnel ct = (ClosedTunnel) field.field[i][j];
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        if (needToSend) {
            field.currentTD = new TurnData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    i, j, -1, this) {
                @Override
                public void apply(Card card) {
                    ((Watch)card).play(i, j, false);
                }
            };
        }
        return ct.isGold();
    }

    public boolean play(int i, int j) {
        return play(i, j, true);
    }
}
