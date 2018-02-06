package ru.tilacyn.saboteur;


import ru.iisuslik.controller.*;
import ru.iisuslik.cards.*;
import ru.iisuslik.gameData.TurnData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class GameActivity extends AppCompatActivity {

    private class Style {
        Button selected;
        int selectedButtonStyle = R.drawable.yellow_button;
        int selectedTextColor = Color.BLACK;

        int textBackgroundColor = Color.parseColor("#FF4B2510");

        int buttonStyle = R.drawable.red_button;
        int buttonTextColor = Color.WHITE;

        Typeface buttonFont = Typeface.createFromAsset(getAssets(), "almendra.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "almendra.ttf");

        int selectedPlayerColor = Color.YELLOW;

        boolean youOpen;

        Style() {
            selected = gameField;
            youOpen = false;
        }

        void decorateButton(Button b) {
            b.setTextSize(13);
            b.setBackgroundResource(buttonStyle);
            b.setTextColor(buttonTextColor);
            b.setTypeface(buttonFont);
        }

        void updateSelected(Button b) {
            selected.setBackgroundResource(buttonStyle);
            selected.setTextColor(buttonTextColor);
            selected = b;
            selected.setBackgroundResource(selectedButtonStyle);
            selected.setTextColor(selectedTextColor);
        }
    }

    private class ActionButton extends android.support.v7.widget.AppCompatButton {
        public ActionButton(Context context) {
            super(context);
        }

        public void decorate() {
            style.decorateButton(this);
        }

        public void remove() {
            buttonsRow.removeView(this);
        }

        public void add() {
            if(!contains(screen, this)) {
                buttonsRow.addView(this);
            }
        }

        public void makeItSelected() {
            style.updateSelected(this);
        }

    }

    public class MainTableLayout extends TableLayout {

        public MainTableLayout(Context context) {
            super(context);
        }

        void drawTable() {
            gameField.makeItSelected();

            removeAllViews();

            TableRow arkenstoneTableRow0 = new TableRow(GameActivity.this);
            arkenstoneTableRow0.setLayoutParams(params);
            arkenstoneTableRow0.setGravity(Gravity.CENTER_HORIZONTAL);


            for (int i = 0; i < fieldWidth + 2; i++) {
                ImageView empty_tunnel = new ImageView(GameActivity.this);
                empty_tunnel.setImageResource(R.drawable.arkenstone);
                arkenstoneTableRow0.addView(empty_tunnel);
            }

            addView(arkenstoneTableRow0);


            TableRow[] field = new TableRow[fieldHeight];

            for (int i = 0; i < fieldHeight; i++) {
                field[i] = new TableRow(GameActivity.this);
                field[i].setLayoutParams(params);
                field[i].setGravity(Gravity.CENTER_HORIZONTAL);
            }

            for (int i = 0; i < fieldHeight; i++) {
                ImageView arkenstone1 = new ImageView(GameActivity.this);
                arkenstone1.setImageResource(R.drawable.arkenstone);
                field[i].addView(arkenstone1);

                for (int j = 0; j < fieldWidth; j++) {
                    final ImageView tunnel = new ImageView(GameActivity.this);
                    Tunnel real = controller.getField()[i][j];
                    if (real == null) {
                        tunnel.setImageResource(R.drawable.empty_tunnel);
                        //tunnel.setBackgroundColor(Color.parseColor("554B2510"));
                    } else {
                        if (real.isClosedTunnel() && ((ClosedTunnel) real).isClosed()) {
                            tunnel.setImageResource(R.drawable.hidden_tunnel);
                        } else {
                            if (real.isClosedTunnel() && ((ClosedTunnel) real).isGold()) {
                                tunnel.setImageResource(R.drawable.gold);
                            } else {
                                tunnel.setImageResource(getCardImageById[real.getId()]);
                            }
                        }
                    }
                    //tunnel.setImageResource(R.drawable.small_tunnel_pattern);
                    //tunnel.setScaleX(0.3F);
                    //tunnel.setScaleY(0.3F);

                    field[i].addView(tunnel);

                    tunnel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int x = (int) map.get(tunnel).first;
                            int y = (int) map.get(tunnel).second;
                            //System.out.println(x);
                            //System.out.println(y);
                            if (chosenCard instanceof Tunnel) {
                                //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                                if (((Tunnel) chosenCard).canPlay(x, y)) {
                                    ((Tunnel) chosenCard).play(x, y);

                                    removeSpin();

                                    chosenX = x;
                                    chosenY = y;

                                    drawTable();
                                } else {
                                    Toast.makeText(getApplicationContext(), "This field " + x + ' ' + y + " is unavailable", Toast.LENGTH_SHORT).show();

                                }
                            }
                            if (chosenCard instanceof Destroy) {
                                //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                                if (((Destroy) chosenCard).canPlay(x, y)) {
                                    ((Destroy) chosenCard).play(x, y);

                                    chosenX = x;
                                    chosenY = y;

                                    drawTable();
                                }
                            }

                            if (chosenCard instanceof Watch) {
                                //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                                if (((Watch) chosenCard).canPlay(x, y)) {
                                    if (((Watch) chosenCard).play(x, y)) {
                                        tunnel.setImageResource(R.drawable.gold);
                                    } else {
                                        tunnel.setImageResource(R.drawable.not_gold);
                                    }

                                    chosenX = x;
                                    chosenY = y;

                                    //drawTable();
                                }
                            }

                        }
                    });


                    map.put((ImageView) field[i].getVirtualChildAt(j + 1), new Pair<Integer, Integer>(i, j));
                }

                ImageView arkenstone2 = new ImageView(GameActivity.this);
                arkenstone2.setImageResource(R.drawable.arkenstone);
                field[i].addView(arkenstone2);


            }

            for (int i = 0; i < fieldHeight; i++) {
                addView(field[i]);
            }


            TableRow arkenstoneTableRow1 = new TableRow(GameActivity.this);
            arkenstoneTableRow1.setLayoutParams(params);
            arkenstoneTableRow1.setGravity(Gravity.CENTER_HORIZONTAL);

            for (int i = 0; i < fieldWidth + 2; i++) {
                ImageView arkenstone = new ImageView(GameActivity.this);
                arkenstone.setImageResource(R.drawable.arkenstone);
                arkenstoneTableRow1.addView(arkenstone);
            }

            addView(arkenstoneTableRow1);

        }

        void drawCards() {
            removeAllViews();
            final TableRow cardsRow = new TableRow(GameActivity.this);
            for (int i = 0; i < controller.getCurrentPlayerHand().size(); i++) {
                ImageButton empty = new ImageButton(GameActivity.this);
                empty.setImageResource(R.drawable.dark_side_of_the_moon);
                cardsRow.addView(empty);
            }
            addView(cardsRow);


            final ArrayList<Card> playerCards = controller.getCurrentPlayerHand();

            for (int i = 0; i < playerCards.size(); i++) {
                //final ImageView cardImage = (ImageView) cardsRow.getVirtualChildAt(i);
                final ImageButton cardImage = (ImageButton) cardsRow.getVirtualChildAt(i);
                if (playerCards.get(i) instanceof Tunnel) {
                    cardImage.setImageResource(getCardImageById[playerCards.get(i).getId() + 40]);
                } else {
                    cardImage.setImageResource(getCardImageById[playerCards.get(i).getId()]);
                }
                cardImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeSpin();
                        for (int i = 0; i < cardsRow.getVirtualChildCount(); i++) {
                            if (cardsRow.getVirtualChildAt(i).equals(cardImage)) {
                                chosenCard = playerCards.get(i);
                                break;
                            }
                        }

                        if (isDiscard) {
                            if (chosenCard.canDiscard()) {
                                chosenCard.discard();
                            } else {
                                Toast.makeText(getApplicationContext(), "You cannot discard this card", Toast.LENGTH_SHORT).show();
                            }
                            if (controller.canStartNextTurn()) {
                                cardsRow.removeView(cardImage);
                            }
                            return;
                        }

                        if (chosenCard instanceof Debuff) {
                            choosePlayer();
                        }

                        if (chosenCard instanceof Heal) {
                            choosePlayer();
                        }

                        if (chosenCard instanceof Tunnel) {
                            makeChosenTunnel();
                            makeSpin();
                            buttonsRow.addView(spin, 0);
                            drawTable();
                        }

                        if (chosenCard instanceof Destroy) {
                            drawTable();
                        }
                        if (chosenCard instanceof Watch) {
                            drawTable();
                        }
                        if (controller.canStartNextTurn()) {
                            cardsRow.removeView(cardImage);
                        }
                    }
                });
            }
        }

        void makeToolsTable(final boolean clickable) {
            tools.makeItSelected();

            removeAllViews();

        /*

        screen.removeView(yourNumber);
        screen.removeView(dwarf);

        screen.setBackgroundResource(R.drawable.green_menu_tools);
        if(!contains(screen, toolsScroll)){
            screen.addView(toolsScroll);
        }

        */

            TableRow[] playerRows = new TableRow[playerCount];
            for (int i = 0; i < playerCount; i++) {

                playerRows[i] = new TableRow(GameActivity.this);
                playerRows[i].setGravity(Gravity.CENTER_HORIZONTAL);
                playerRows[i].setBackgroundColor(style.textBackgroundColor);


                TextView playerNumber = new TextView(GameActivity.this);
                playerNumber.setText("Player " + ((Integer) (i + 1)).toString());
                playerNumber.setTextSize(20);
                playerNumber.setTypeface(style.textFont);

                //playerNumber.setGravity(Gravity.);
                if (i == controller.currentPlayerNumber()) {
                    playerNumber.setTextColor(style.selectedPlayerColor);
                } else {
                    playerNumber.setTextColor(Color.WHITE);
                }

                boolean[] currentDebuffs = controller.getPlayerDebuffs(i);
                ImageView lamp = new ImageView(GameActivity.this);
                ImageView pick = new ImageView(GameActivity.this);
                ImageView trolley = new ImageView(GameActivity.this);

                if (!currentDebuffs[0]) {
                    lamp.setImageResource(R.drawable.lamp);
                } else {
                    lamp.setImageResource(R.drawable.broken_lamp);
                }

                if (!currentDebuffs[1]) {
                    pick.setImageResource(R.drawable.pick);
                } else {
                    pick.setImageResource(R.drawable.broken_pick);
                }

                if (!currentDebuffs[2]) {
                    trolley.setImageResource(R.drawable.trolley);
                } else {
                    trolley.setImageResource(R.drawable.broken_trolley);
                }

                playerRows[i].addView(playerNumber);
                playerRows[i].addView(lamp);
                playerRows[i].addView(pick);
                playerRows[i].addView(trolley);
                final TableRow currentRow = playerRows[i];
                playerRows[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickable) {
                            playerChoose = Character.getNumericValue(((TextView) currentRow.getVirtualChildAt(0)).getText().charAt(7)) - 1;
                            Toast.makeText(getApplicationContext(), "Player " + Integer.toString(playerChoose + 1),
                                    Toast.LENGTH_SHORT).show();
                            if (chosenCard instanceof Debuff) {
                                if (((Debuff) chosenCard).canPlay(playerChoose)) {
                                    ((Debuff) chosenCard).play(playerChoose);
                                    makeToolsTable(false);
                                } else {
                                    Toast.makeText(getApplicationContext(), "This gnome cannot be played this way!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (chosenCard instanceof Heal) {
                                if (((Heal) chosenCard).canPlay(playerChoose)) {
                                    ((Heal) chosenCard).play(playerChoose);
                                    makeToolsTable(false);
                                } else {
                                    Toast.makeText(getApplicationContext(), "This gnome cannot be played this way!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            return;
                        }
                    }
                });


                addView(playerRows[i]);

            }
        }

        void makeLogTable() {
            removeAllViews();
            for (int i = 0; i < logRows.size(); i++) {
                addView(logRows.get(i));
            }
        }

    }

    private Style style;

    private int[] getCardImageById = new int[100];

    private void fillCardIdArray() {


        getCardImageById[2] = R.drawable.tunnel_pattern_2;
        getCardImageById[3] = R.drawable.tunnel_pattern_3;
        getCardImageById[4] = R.drawable.tunnel_pattern_4;
        getCardImageById[5] = R.drawable.tunnel_pattern_5;
        getCardImageById[6] = R.drawable.tunnel_pattern_6;
        getCardImageById[7] = R.drawable.tunnel_pattern_7;
        getCardImageById[8] = R.drawable.tunnel_pattern_8;
        getCardImageById[9] = R.drawable.tunnel_pattern_9;
        getCardImageById[10] = R.drawable.tunnel_pattern_10;
        getCardImageById[11] = R.drawable.tunnel_pattern_11;
        getCardImageById[12] = R.drawable.tunnel_pattern_12;
        getCardImageById[13] = R.drawable.tunnel_pattern_13;
        getCardImageById[14] = R.drawable.tunnel_pattern_14;
        getCardImageById[15] = R.drawable.tunnel_pattern_15;
        getCardImageById[16] = R.drawable.tunnel_pattern_16;

        //getCardImageById[17] = R.drawable.tunnel_pattern_17;
        //getCardImageById[18] = R.drawable.tunnel_pattern_18;
        getCardImageById[19] = R.drawable.tunnel_pattern_19;
        //getCardImageById[20] = R.drawable.tunnel_pattern_20;
        getCardImageById[21] = R.drawable.tunnel_pattern_21;
        getCardImageById[22] = R.drawable.tunnel_pattern_22;
        getCardImageById[23] = R.drawable.tunnel_pattern_23;
        //getCardImageById[24] = R.drawable.tunnel_pattern_24;
        getCardImageById[25] = R.drawable.tunnel_pattern_25;
        getCardImageById[26] = R.drawable.tunnel_pattern_26;
        getCardImageById[27] = R.drawable.tunnel_pattern_27;
        getCardImageById[28] = R.drawable.tunnel_pattern_28;
        getCardImageById[29] = R.drawable.tunnel_pattern_29;
        getCardImageById[30] = R.drawable.tunnel_pattern_30;
        getCardImageById[31] = R.drawable.tunnel_pattern_31;


        getCardImageById[42] = R.drawable.big_tunnel_pattern_2;
        getCardImageById[43] = R.drawable.big_tunnel_pattern_3;
        getCardImageById[44] = R.drawable.big_tunnel_pattern_4;
        getCardImageById[45] = R.drawable.big_tunnel_pattern_5;
        getCardImageById[46] = R.drawable.big_tunnel_pattern_6;
        getCardImageById[47] = R.drawable.big_tunnel_pattern_7;
        getCardImageById[48] = R.drawable.big_tunnel_pattern_8;
        getCardImageById[49] = R.drawable.big_tunnel_pattern_9;
        getCardImageById[50] = R.drawable.big_tunnel_pattern_10;
        getCardImageById[51] = R.drawable.big_tunnel_pattern_11;
        getCardImageById[52] = R.drawable.big_tunnel_pattern_12;
        getCardImageById[53] = R.drawable.big_tunnel_pattern_13;
        getCardImageById[54] = R.drawable.big_tunnel_pattern_14;
        getCardImageById[55] = R.drawable.big_tunnel_pattern_15;
        getCardImageById[56] = R.drawable.big_tunnel_pattern_16;
        //getCardImageById[57] = R.drawable.big_tunnel_pattern_17;
        //getCardImageById[58] = R.drawable.big_tunnel_pattern_18;
        getCardImageById[59] = R.drawable.big_tunnel_pattern_19;
        //getCardImageById[60] = R.drawable.big_tunnel_pattern_20;
        getCardImageById[61] = R.drawable.big_tunnel_pattern_21;
        getCardImageById[62] = R.drawable.big_tunnel_pattern_22;
        getCardImageById[63] = R.drawable.big_tunnel_pattern_23;
        //getCardImageById[64] = R.drawable.big_tunnel_pattern_24;
        getCardImageById[65] = R.drawable.big_tunnel_pattern_25;
        getCardImageById[66] = R.drawable.big_tunnel_pattern_26;
        getCardImageById[67] = R.drawable.big_tunnel_pattern_27;
        getCardImageById[68] = R.drawable.big_tunnel_pattern_28;
        getCardImageById[69] = R.drawable.big_tunnel_pattern_29;
        getCardImageById[70] = R.drawable.big_tunnel_pattern_30;
        getCardImageById[71] = R.drawable.big_tunnel_pattern_31;


        getCardImageById[32] = R.drawable.card_32;
        getCardImageById[33] = R.drawable.card_33;
        getCardImageById[34] = R.drawable.card_34;
        getCardImageById[35] = R.drawable.card_35;
        getCardImageById[36] = R.drawable.card_36;
        getCardImageById[37] = R.drawable.card_37;
        getCardImageById[38] = R.drawable.card_38;
        getCardImageById[39] = R.drawable.card_39;
        getCardImageById[40] = R.drawable.card_40;
        getCardImageById[41] = R.drawable.card_41;
        getCardImageById[42] = R.drawable.card_42;
    }

    private int playerCount;
    private Controller controller;
    private HashMap<ImageView, Pair<Integer, Integer>> map = new HashMap();
    private int fieldHeight;
    private int fieldWidth;

    private Card chosenCard;

    private boolean isDiscard = false;

    // constraint layout
    private ConstraintLayout screen;

    // game field
    private MainTableLayout table;

    // table row params for table(game field)
    private TableRow.LayoutParams params;

    // YOU

    private TextView yourNumber;
    private ImageView dwarf;

    // CardsTable, cardsRow and button cards

    private ActionButton cards;

    // discard
    private ActionButton discard;

    // game field
    private ActionButton gameField;

    // tools
    private ActionButton tools;

    // switch
    private ActionButton switchPlayer;

    //spin
    private ActionButton spin;

    // buttons table
    private TableLayout buttonsTable;
    private TableRow buttonsRow;

    // log table
    private ActionButton log;
    private ArrayList<TableRow> logRows;

    // chosen cell
    private int chosenX;
    private int chosenY;

    //chosen tunnel
    private TextView textChosenTunnel;
    private ImageView chosenTunnel;

    // discard indicator
    private TextView textDiscardIndicator;


    // choosing player
    private int playerChoose = -1;


    // return controller
    byte[] controllerByteArray;

    //hscroll
    HorizontalScrollView hscroll;


    boolean contains(ViewGroup parent, View child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).equals(child)) {
                return true;
            }
        }
        return false;
    }

    void removeSpin() {
        screen.removeView(chosenTunnel);
        screen.removeView(textChosenTunnel);
        spin.remove();
    }

    void setDiscardFalse() {
        isDiscard = false;
        screen.removeView(textDiscardIndicator);
    }

    //Initializing

    void initializeAll() {

        //controller
        //saboteurApplication = SaboteurApplication.getInstance();
        //controller = saboteurApplication.getController();

        hscroll = (HorizontalScrollView) findViewById(R.id.hscroll);


        if(getIntent().getByteArrayExtra("controller") != null) {
            byte[] controllerCypher = getIntent().getByteArrayExtra("controller");
            controller = Controller.deserialize(new ByteArrayInputStream(controllerCypher));
            controllerByteArray = controllerCypher;
        }
        else {
            controller = SaboteurApplication.getInstance().getController();
            controllerByteArray = null;
        }
        //constraint layout
        screen = (ConstraintLayout) findViewById(R.id.activity_main);

        // game field
        //TableLayout lol = (TableLayout) findViewById(R.id.table);
        table = new MainTableLayout(this);
        hscroll.addView(table);

        // table row params for table(game field)
        params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        // YOU

        yourNumber = (TextView) findViewById(R.id.yourName);
        dwarf = (ImageView) findViewById(R.id.dwarf);

        // CardsTable, cardsRow and button cards

        cards = new ActionButton(this);

        // game field
        gameField = new ActionButton(this);

        // tools
        tools = new ActionButton(this);

        // discard
        discard = new ActionButton(this);

        // spin
        spin = new ActionButton(this);

        // switch
        switchPlayer = new ActionButton(this);

        //buttons table
        buttonsTable = (TableLayout) findViewById(R.id.buttonsTable);
        buttonsRow = new TableRow(this);

        // tools table
        //toolsTable = (TableLayout) findViewById(R.id.toolsTable);
        //toolsScroll = (ScrollView) findViewById(R.id.stableTools);


        // style
        style = new Style();

        // log
        log = new ActionButton(this);
        logRows = new ArrayList<>();

        // chosenTunnel
        chosenTunnel = (ImageView) findViewById(R.id.chosenTunnel);
        textChosenTunnel = (TextView) findViewById(R.id.textChosenTunnel);

        // discard indicator
        textDiscardIndicator = (TextView) findViewById(R.id.textDiscardIndicator);

    }


    // Making Buttons

    void makeTools() {
        tools.setText("Tools");
        tools.decorate();

        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tools.makeItSelected();
                setDiscardFalse();
                removeSpin();
                table.makeToolsTable(false);
            }
        });
    }

    void makeLog() {
        log.setText("Log");
        log.decorate();

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.makeItSelected();
                setDiscardFalse();
                removeSpin();
                table.makeLogTable();
            }
        });
    }

    void makeGameField() {
        gameField.setText("Field");
        gameField.decorate();

        gameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameField.makeItSelected();
                setDiscardFalse();
                removeSpin();
                table.drawTable();
            }
        });
    }

    void makeCards() {
        cards.setText("Cards");
        cards.decorate();
        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cards.makeItSelected();
                setDiscardFalse();
                removeSpin();
                table.drawCards();
            }
        });

    }

    void makeSpin() {
        spin.setText("Spin");
        spin.decorate();
        spin.setBackgroundResource(R.drawable.yellow_button);
        spin.setTextColor(Color.BLUE);

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Tunnel) chosenCard).spin();
                makeChosenTunnel();
            }
        });
    }

    void makeYou() {


        yourNumber.setText("Player " + ((Integer) (controller.currentPlayerNumber() + 1)).toString());
        yourNumber.setTextSize(30);
        yourNumber.setGravity(Gravity.CENTER_HORIZONTAL);
        yourNumber.setTextColor(Color.WHITE);
        yourNumber.setTypeface(style.textFont);


        dwarf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSaboteur = controller.isCurrentPlayerSaboteur();

                if (!isSaboteur) {
                    if (style.youOpen) {
                        dwarf.setImageResource(0);
                        dwarf.setBackgroundResource(0);
                        dwarf.setBackgroundResource(R.drawable.gnome_animation_out);
                        ((AnimationDrawable) dwarf.getBackground()).start();
                    } else {
                        dwarf.setImageResource(0);
                        dwarf.setBackgroundResource(0);
                        dwarf.setBackgroundResource(R.drawable.gnome_animation_in);
                        ((AnimationDrawable) dwarf.getBackground()).start();
                    }
                } else {
                    if (style.youOpen) {
                        dwarf.setImageResource(0);
                        dwarf.setBackgroundResource(0);
                        dwarf.setBackgroundResource(R.drawable.saboteur_animation_out);
                        ((AnimationDrawable) dwarf.getBackground()).start();
                    } else {
                        dwarf.setImageResource(0);
                        dwarf.setBackgroundResource(0);
                        dwarf.setBackgroundResource(R.drawable.saboteur_animation_in);
                        ((AnimationDrawable) dwarf.getBackground()).start();
                    }
                }
                style.youOpen = !style.youOpen;
            }
        });

        dwarf.setImageResource(R.drawable.dark_side_of_the_moon_1);
    }

    void makeChosenTunnel() {
        if (!contains(screen, chosenTunnel)) {
            screen.addView(chosenTunnel);
        }
        if (!contains(screen, textChosenTunnel)) {
            screen.addView(textChosenTunnel);
        }

        chosenTunnel.setImageResource(getCardImageById[chosenCard.getId()]);

        textChosenTunnel.setText("Tunnel");
        textChosenTunnel.setTextSize(13);
        textChosenTunnel.setTextColor(Color.WHITE);
        textChosenTunnel.setTypeface(style.textFont);

    }

    void makeDiscardIndicator() {
        if (!contains(screen, textDiscardIndicator)) {
            screen.addView(textDiscardIndicator);
        }

        textDiscardIndicator.setText("Discard");
        textDiscardIndicator.setTextSize(13);
        textDiscardIndicator.setTextColor(Color.WHITE);
        textDiscardIndicator.setTypeface(style.textFont);
    }

    void makeSwitch() {
        switchPlayer.setText("sWitch");
        switchPlayer.decorate();

        switchPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpin();
                if (controller.isThisTheEnd()) {
                    Toast.makeText(getApplicationContext(), "This is the end, congratulations to the winners!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (!controller.canStartNextTurn()) {
                    Toast.makeText(getApplicationContext(), "Your turn still! Don't fraud!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateLogRows();
                style.youOpen = false;
                controller.startNextTurn();
                setDiscardFalse();
                makeYou();
                table.drawTable();
                playerChoose = -1;


                Intent res = new Intent();
                byte[] controllerByteArray;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                controller.serialize(byteArrayOutputStream);

                controllerByteArray = byteArrayOutputStream.toByteArray();

                res.putExtra("controller", controllerByteArray);

                setResult(RESULT_OK, res);

            }
        });
    }

    void makeDiscard() {
        discard.setText("Discard");
        discard.decorate();


        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.canStartNextTurn()) {
                    return;
                }
                if (isDiscard) {
                    cards.makeItSelected();
                } else {
                    makeDiscardIndicator();
                    discard.makeItSelected();
                }
                removeSpin();
                if(isDiscard) {
                    setDiscardFalse();
                } else {
                    isDiscard = true;
                }
                table.drawCards();
            }
        });
    }


    // Making tables on "table" Layout


    // additional function to update logTable
    void updateLogRows() {

        logRows.clear();

        //Toast.makeText(getApplicationContext(), ((Integer) controller.gameData.turns.size()).toString(), Toast.LENGTH_SHORT).show();

        for (TurnData turn : controller.gameData.turns) {
            TableRow logRow = new TableRow(this);
            logRow.setBackgroundColor(style.textBackgroundColor);

            TextView message = new TextView(this);

            message.setText("Player " + turn.ownerPlayerNumber);

            if (turn.card instanceof Heal) {
                message.setText(message.getText() + " repaired Player " +
                        ((Integer) (turn.targetPlayerNumber + 1)).toString() + "'s ");
                if (((Heal) turn.card).isHealLamp()) {
                    if (((Heal) turn.card).isHealTrolley()) {
                        message.setText(message.getText() + "lamp and trolley");
                    } else if (((Heal) turn.card).isHealPick()) {
                        message.setText(message.getText() + "lamp and pick");
                    } else {
                        message.setText(message.getText() + "lamp");
                    }
                } else {
                    if (((Heal) turn.card).isHealTrolley()) {
                        if (((Heal) turn.card).isHealPick()) {
                            message.setText(message.getText() + "pick and trolley");
                        } else {
                            message.setText(message.getText() + "trolley");
                        }
                    } else {
                        message.setText(message.getText() + "pick");
                    }
                }
            }

            if (turn.card instanceof Debuff) {
                message.setText(message.getText() + " broke Player " +
                        ((Integer) (turn.targetPlayerNumber + 1)).toString() + "'s ");
                if (((Debuff) turn.card).isBreakingLamp()) {
                    message.setText(message.getText() + "lamp");
                } else if (((Debuff) turn.card).isBreakingPick()) {
                    message.setText(message.getText() + "pick");
                } else {
                    message.setText(message.getText() + "trolley");
                }
            }

            if (turn.card instanceof Tunnel) {
                message.setText(message.getText() + " put tunnel on (" +
                        ((Integer) turn.i).toString() + ", " + ((Integer) turn.j).toString() + ") cell");
            }

            if (turn.card instanceof Destroy) {
                message.setText(message.getText() + " destroyed tunnel on (" +
                        ((Integer) turn.i).toString() + ", " + ((Integer) turn.j).toString() + ") cell");

            }

            if (turn.card instanceof Watch) {
                message.setText(message.getText() + " watched at (" +
                        ((Integer) turn.i).toString() + ", " + ((Integer) turn.j).toString() + ") cell");
            }
            message.setTextSize(12);
            message.setTextColor(Color.WHITE);
            message.setTypeface(style.textFont);


            logRow.addView(message);

            logRows.add(logRow);
            //TODO discard
        }


    }

    void makeButtonsTable() {
        gameField.add();
        cards.add();
        switchPlayer.add();
        tools.add();
        discard.add();
        log.add();

        buttonsTable.addView(buttonsRow);
    }


    // Layout params and dependencies


    void setLayoutParams() {
        screen.setBackgroundResource(R.drawable.background3);
        //screen.setBackgroundColor(Color.parseColor("#FF4B2510"));
    }

    void choosePlayer() {
        //table.removeAllViews();
        table.makeToolsTable(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //

        Intent intent = new Intent();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        controller.serialize(byteArrayOutputStream);

        controllerByteArray = byteArrayOutputStream.toByteArray();
        //
        intent.putExtra("controller", controllerByteArray);

        setResult(RESULT_OK, intent);

        Toast.makeText(getApplicationContext(), "onStop applied", Toast.LENGTH_SHORT).show();
        //finish();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        initializeAll();
        //Log.d("EEEE", "EEEEEEExperimenty");
        //controller.multiPlayer.sendData(new byte[2]);//strange
        //controller.update();
        if(!controller.isSinglePlayer())
            controller.multiPlayer.onInitiateMatch(controller.multiPlayer.curMatch);
        playerCount = getIntent().getIntExtra("playerCount", 2);

        fillCardIdArray();

        fieldHeight = controller.getHeight();
        fieldWidth = controller.getWidth();

        setLayoutParams();

        makeCards();

        makeYou();

        makeDiscard();

        makeSwitch();

        makeTools();

        makeLog();

        makeGameField();

        makeButtonsTable();

        table.drawTable();

    }

}
