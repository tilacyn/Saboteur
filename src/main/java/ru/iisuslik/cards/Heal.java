package ru.iisuslik.cards;

import ru.iisuslik.field.Field;

public class Heal extends Action {

    private boolean healLamp, healTrolley, healPick;

    public Heal(int id, String name, String description, Field field, int playerNumber,
                boolean healLamp, boolean healTrolley, boolean healPick) {
        super(id, name, description, field, playerNumber, HEAL);
        this.healLamp = healLamp;
        this.healPick = healPick;
        this.healTrolley = healTrolley;
        this.id = 0;
    }

    public boolean canPlay(int playerNumber) {
        return !field.didCurrentPlayerPlayCard() && field.players[playerNumber].needHeal(this);
    }

    public void play(int playerNumber) {
        field.players[playerNumber].heal(this);
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
