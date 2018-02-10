package ru.iisuslik.cards;

import org.json.JSONArray;
import org.json.JSONException;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Heal extends Card {

    private boolean healLamp, healTrolley, healPick;

    public Heal(JSONArray action, Field field) throws JSONException {
        super(action.getInt(4), field, -1);
        healLamp = action.getInt(1) == 1;
        healPick = action.getInt(2) == 1;
        healTrolley = action.getInt(3) == 1;
    }

    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].needHeal(this)
                && field.controller.isMyTurn();
    }

    public void play(final int playerNumber) {
        field.currentTD = new TurnData(CARD_TYPE.HEAL, ownerPlayerNumber,
                field.players[ownerPlayerNumber].getCardNumber(this),
                -1, -1, playerNumber,
                isHealLamp(), isHealTrolley(), isHealPick());
        field.players[playerNumber].heal(this);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();

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
