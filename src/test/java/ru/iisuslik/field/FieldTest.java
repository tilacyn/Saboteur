package ru.iisuslik.field;

import org.junit.Test;
import ru.iisuslik.cards.Card;
import ru.iisuslik.cards.Tunnel;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FieldTest {
    @Test
    public void Deck() throws Exception {
        Field f = new Field(3);
        for(Card card: f.deck) {
            card.printFirst();
        }
        System.out.println();
        for(Card card: f.deck) {
            card.printSecond();
        }
        System.out.println();
        for(Card card: f.deck) {
            card.printThird();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    @Test
    public void Field() throws Exception {
        Field f = new Field(3);
        f.print();
    }

    @Test
    public void Hands() throws Exception {
        Field f = new Field(3);
        for(int i = 0; i < 3; i++) {
            for(Card card: f.players[i].getHand()) {
                    card.printFirst();
            }
            System.out.println();
            for(Card card: f.players[i].getHand()) {
                    card.printSecond();
            }
            System.out.println();
            for(Card card: f.players[i].getHand()) {
                card.printThird();
            }
            System.out.println();
        }
    }

}