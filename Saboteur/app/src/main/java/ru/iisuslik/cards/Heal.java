package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Heal extends Action {

    private boolean healLamp, healTrolley, healPick;

    public Heal(int id, String name, String description, Field field, int playerNumber,
                boolean healLamp, boolean healTrolley, boolean healPick) {
        super(id, name, description, field, playerNumber, HEAL);
        this.healLamp = healLamp;
        this.healPick = healPick;
        this.healTrolley = healTrolley;
    }

    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].needHeal(this);
    }

    public void play(final int playerNumber, final boolean needToSend) {
        field.players[playerNumber].heal(this);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
        if (needToSend) {
            field.currentTD = new TurnData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    -1, -1, playerNumber, this) {
                @Override
                public void apply(Card card) {
                    ((Heal)card).play(targetPlayerNumber, false);
                }
            };
        }
    }
    public void play(int playerNumber){
        play(playerNumber, true);
    }

    public boolean isHealLamp() {
        return healLamp;
    }

    public boolean isHealPick() {
        return healPick;
    }

    public boolean isHealTrolley() {
        return healTrolley;
    }
}
