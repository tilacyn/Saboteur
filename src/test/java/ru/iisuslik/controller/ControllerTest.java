package ru.iisuslik.controller;

import org.junit.Test;
import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Tunnel;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static ru.iisuslik.controller.Controller.ENTRY_POS_I;
import static ru.iisuslik.controller.Controller.ENTRY_POS_J;

public class ControllerTest {

    private void printCurrentPlayerHand(Controller c){
        for(Card card: c.getCurrentPlayerHand()) {
            card.printFirst();
        }
        System.out.println();
        for(Card card: c.getCurrentPlayerHand()) {
            card.printSecond();
        }
        System.out.println();
        for(Card card: c.getCurrentPlayerHand()) {
            card.printThird();
        }
        System.out.println();
    }

    @Test
    public void all() throws Exception {
        Controller c = new Controller();
        c.initializeField(3);


        printCurrentPlayerHand(c);


        assertFalse(c.canStartNextTurn());
        assertArrayEquals(c.getCurrentPlayerDebuffs(), new boolean[]{false, false, false});
        ArrayList<Card> cards = c.getCurrentPlayerHand();
        boolean played = false;
        for(int i = 0; i < cards.size(); i++) {
            if(cards.get(i).getType() == Card.TUNNEL) {
                Tunnel tunnel = (Tunnel)cards.get(i);
                assertFalse(tunnel.canPlay(ENTRY_POS_I, ENTRY_POS_J));
                if(tunnel.canPlay(ENTRY_POS_I - 1,ENTRY_POS_J )){
                    tunnel.play(ENTRY_POS_I - 1, ENTRY_POS_J);
                    played = true;
                }
            }
        }
        if(played) {
            for(Card card: c.getCurrentPlayerHand()) {
                assertFalse(card.canDiscard());
            }
        }

        c.printField();

        System.out.println(c.isCurrentPlayerSaboteur());
        printCurrentPlayerHand(c);
        assertTrue(c.canStartNextTurn());
        c.startNextTurn();

        System.out.println(c.isCurrentPlayerSaboteur());
        printCurrentPlayerHand(c);
    }

}