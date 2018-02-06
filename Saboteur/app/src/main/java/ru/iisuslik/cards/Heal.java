package ru.iisuslik.cards;

import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.TurnData;

public class Heal extends Card {

    private boolean healLamp, healTrolley, healPick;

    public Heal(int id, String name, String description, Field field, int playerNumber,
                boolean healLamp, boolean healTrolley, boolean healPick) {
        super(id, name, description, field, playerNumber);
        this.healLamp = healLamp;
        this.healPick = healPick;
        this.healTrolley = healTrolley;
    }

    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].needHeal(this)
                && field.controller.isMyTurn();
    }

    public void play(final int playerNumber) {
        field.players[playerNumber].heal(this);
        field.players[ownerPlayerNumber].playCard(this);
        field.iPlayedCard();
            field.currentTD = new TurnData(ownerPlayerNumber,
                    field.players[ownerPlayerNumber].getCardNumber(this),
                    -1, -1, playerNumber, this) {
                @Override
                public void apply(Card card) {
                    ((Heal)card).play(targetPlayerNumber);
                    card.field.startNextTurn(false);
                }
            };

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
