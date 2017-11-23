package ru.tilacyn.saboteur;


import ru.iisuslik.controller.*;
import ru.iisuslik.cards.*;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private TableLayout table;

    // table row params for table(game field)
    private TableRow.LayoutParams params;

    // YOU

    private TextView yourNumber;
    private ImageView dwarf;

    // CardsTable, cardsRow and button cards

    private Button cards;
    private TableRow cardsRow;

    // toolsTable

    // discard
    private Button discard;

    // game field
    private Button gameField;

    // tools
    private Button tools;

    // switch
    private Button switchPlayer;

    //spin
    private Button spin;

    // buttons table
    private TableLayout buttonsTable;
    private TableRow buttonsRow;

    // tools table
    private TableLayout toolsTable;
    private ScrollView toolsScroll;

    // log table
    private Button log;
    private ArrayList<TableRow> logRows;

    // chosen cell
    private int chosenX;
    private int chosenY;

    //chosen tunnel
    private TextView textChosenTunnel;
    private ImageView chosenTunnel;


    // choosing player
    private int playerChoose = -1;


    // return controller
    byte[] controllerByteArray;


    boolean contains(ViewGroup parent, View child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).equals(child)) {
                return true;
            }
        }
        return false;
    }

    void makeNormalView() {
        screen.setBackgroundResource(R.drawable.background3);
        screen.removeView(toolsScroll);
        toolsTable.removeAllViews();
        if (!contains(screen, yourNumber)) {
            screen.addView(yourNumber);
        }
        if (!contains(screen, dwarf)) {
            screen.addView(dwarf);
        }
    }

    void removeSpin() {
        screen.removeView(chosenTunnel);
        screen.removeView(textChosenTunnel);
        buttonsRow.removeView(spin);
    }

    void setDiscard() {
        isDiscard = false;
    }

    //Initializing

    void initializeAll() {

        //controller
        //saboteurApplication = SaboteurApplication.getInstance();
        //controller = saboteurApplication.getController();


        byte[] controllerCypher = getIntent().getByteArrayExtra("controller");

        controller = Controller.deserialize(new ByteArrayInputStream(controllerCypher));

        controllerByteArray = controllerCypher;

        //constraint layout
        screen = (ConstraintLayout) findViewById(R.id.activity_main);

        // game field
        table = (TableLayout) findViewById(R.id.table);

        // table row params for table(game field)
        params = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        // YOU

        yourNumber = (TextView) findViewById(R.id.yourName);
        dwarf = (ImageView) findViewById(R.id.dwarf);

        // CardsTable, cardsRow and button cards

        cards = new Button(this);
        //cardsTable = (TableLayout)findViewById(R.id.buttonsTable);
        cardsRow = new TableRow(this);

        // game field
        gameField = new Button(this);

        // tools
        tools = new Button(this);

        // toolsTable

        // discard
        discard = new Button(this);

        // spin
        spin = new Button(this);

        // switch
        switchPlayer = new Button(this);

        //buttons table
        buttonsTable = (TableLayout) findViewById(R.id.buttonsTable);
        buttonsRow = new TableRow(this);

        // tools table
        toolsTable = (TableLayout) findViewById(R.id.toolsTable);
        toolsScroll = (ScrollView) findViewById(R.id.stableTools);


        // style
        style = new Style();

        // log
        log = new Button(this);
        logRows = new ArrayList<>();

        // chosenTunnel
        chosenTunnel = (ImageView) findViewById(R.id.chosenTunnel);
        textChosenTunnel = (TextView) findViewById(R.id.textChosenTunnel);

    }


    // Making Buttons

    void makeTools() {
        tools.setText("Tools");
        style.decorateButton(tools);

        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.updateSelected(tools);
                removeSpin();
                makeToolsTable(false);
            }
        });
    }

    void makeLog() {
        log.setText("Log");
        style.decorateButton(log);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.updateSelected(log);
                removeSpin();
                makeLogTable();
            }
        });
    }

    void makeGameField() {
        gameField.setText("Field");
        style.decorateButton(gameField);
        gameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.updateSelected(gameField);
                removeSpin();
                drawTable();
            }
        });
    }

    void makeCards() {
        cards.setText("Cards");
        style.decorateButton(cards);
        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style.updateSelected(cards);
                removeSpin();
                drawCards();
            }
        });

    }

    void makeSpin() {
        spin.setText("Spin");
        style.decorateButton(spin);
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

    void makeSwitch() {
        switchPlayer.setText("Switch");
        style.decorateButton(switchPlayer);

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

                makeLogRow();
                style.youOpen = false;
                controller.startNextTurn();
                setDiscard();
                makeYou();
                drawTable();
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
        style.decorateButton(discard);

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.canStartNextTurn()) {
                    return;
                }
                if (isDiscard) {
                    style.updateSelected(cards);
                } else {
                    style.updateSelected(discard);
                }
                removeSpin();
                isDiscard = !isDiscard;
                drawCards();
            }
        });
    }


    // Making tables on "table" Layout

    void drawTable() {
        makeNormalView();

        style.updateSelected(gameField);

        table.removeAllViews();

        TableRow arkenstoneTableRow0 = new TableRow(this);
        arkenstoneTableRow0.setLayoutParams(params);
        arkenstoneTableRow0.setGravity(Gravity.CENTER_HORIZONTAL);


        for (int i = 0; i < fieldWidth + 2; i++) {
            ImageView empty_tunnel = new ImageView(this);
            empty_tunnel.setImageResource(R.drawable.arkenstone);
            arkenstoneTableRow0.addView(empty_tunnel);
        }

        table.addView(arkenstoneTableRow0);


        TableRow[] field = new TableRow[fieldHeight];

        for (int i = 0; i < fieldHeight; i++) {
            field[i] = new TableRow(this);
            field[i].setLayoutParams(params);
            field[i].setGravity(Gravity.CENTER_HORIZONTAL);
        }

        for (int i = 0; i < fieldHeight; i++) {
            ImageView arkenstone1 = new ImageView(this);
            arkenstone1.setImageResource(R.drawable.arkenstone);
            field[i].addView(arkenstone1);

            for (int j = 0; j < fieldWidth; j++) {
                final ImageView tunnel = new ImageView(this);
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
                                    Toast.makeText(getApplicationContext(), "This is a hidden treasure!",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Oh no! Just a simple tunnel(",
                                            Toast.LENGTH_SHORT).show();
                                }

                                chosenX = x;
                                chosenY = y;

                                drawTable();
                            }
                        }

                    }
                });


                map.put((ImageView) field[i].getVirtualChildAt(j + 1), new Pair<Integer, Integer>(i, j));
            }

            ImageView arkenstone2 = new ImageView(this);
            arkenstone2.setImageResource(R.drawable.arkenstone);
            field[i].addView(arkenstone2);


        }

        for (int i = 0; i < fieldHeight; i++) {
            table.addView(field[i]);
        }


        TableRow arkenstoneTableRow1 = new TableRow(this);
        arkenstoneTableRow1.setLayoutParams(params);
        arkenstoneTableRow1.setGravity(Gravity.CENTER_HORIZONTAL);

        for (int i = 0; i < fieldWidth + 2; i++) {
            ImageView arkenstone = new ImageView(this);
            arkenstone.setImageResource(R.drawable.arkenstone);
            arkenstoneTableRow1.addView(arkenstone);
        }

        table.addView(arkenstoneTableRow1);

    }

    void drawCards() {
        makeNormalView();
        table.removeAllViews();
        cardsRow.removeAllViews();
        for (int i = 0; i < controller.getCurrentPlayerHand().size(); i++) {
            ImageButton empty = new ImageButton(GameActivity.this);
            empty.setImageResource(R.drawable.dark_side_of_the_moon);
            cardsRow.addView(empty);
        }
        table.addView(cardsRow);


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
        style.updateSelected(tools);

        table.removeAllViews();

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

            playerRows[i] = new TableRow(this);
            playerRows[i].setGravity(Gravity.CENTER_HORIZONTAL);
            playerRows[i].setBackgroundColor(style.textBackgroundColor);


            TextView playerNumber = new TextView(this);
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
            ImageView lamp = new ImageView(this);
            ImageView pick = new ImageView(this);
            ImageView trolley = new ImageView(this);

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


            table.addView(playerRows[i]);

        }
    }

    void makeLogTable() {
        makeNormalView();
        table.removeAllViews();
        for (int i = 0; i < logRows.size(); i++) {
            table.addView(logRows.get(i));
        }
    }

    // additional function to update logTable
    void makeLogRow() {
        TableRow logRow = new TableRow(this);
        logRow.setBackgroundColor(style.textBackgroundColor);

        TextView message = new TextView(this);

        message.setText(((Integer) (logRows.size() + 1)).toString() + ". Player " +
                ((Integer) (controller.currentPlayerNumber() + 1)).toString());

        if (isDiscard) {
            message.setText(message.getText() + " : discard");
        } else if (chosenCard instanceof Tunnel) {
            message.setText(message.getText() + " put tunnel on (" +
                    ((Integer) chosenX).toString() + ", " + ((Integer) chosenY).toString() + ") cell");

        } else if (chosenCard instanceof Heal) {
            message.setText(message.getText() + " repaired Player " +
                    ((Integer) (playerChoose + 1)).toString() + "'s ");
            if (((Heal) chosenCard).isHealLamp()) {
                if (((Heal) chosenCard).isHealTrolley()) {
                    message.setText(message.getText() + "lamp and trolley");
                } else if (((Heal) chosenCard).isHealPick()) {
                    message.setText(message.getText() + "lamp and pick");
                } else {
                    message.setText(message.getText() + "lamp");
                }
            } else {
                if (((Heal) chosenCard).isHealTrolley()) {
                    if (((Heal) chosenCard).isHealPick()) {
                        message.setText(message.getText() + "pick and trolley");
                    } else {
                        message.setText(message.getText() + "trolley");
                    }
                } else {
                    message.setText(message.getText() + "pick");
                }
            }
        } else if (chosenCard instanceof Debuff) {
            message.setText(message.getText() + " broke Player " +
                    ((Integer) (playerChoose + 1)).toString() + "'s ");
            if (((Debuff) chosenCard).isBreakingLamp()) {
                message.setText(message.getText() + "lamp");
            } else if (((Debuff) chosenCard).isBreakingPick()) {
                message.setText(message.getText() + "pick");
            } else {
                message.setText(message.getText() + "trolley");
            }
        } else if (chosenCard instanceof Destroy) {
            message.setText(message.getText() + " destroyed tunnel on (" +
                    ((Integer) chosenX).toString() + ", " + ((Integer) chosenY).toString() + ") cell");
        } else if (chosenCard instanceof Watch) {
            message.setText(message.getText() + " watched at (" +
                    ((Integer) chosenX).toString() + ", " + ((Integer) chosenY).toString() + ") cell");
        }


        message.setTextSize(12);
        message.setTextColor(Color.WHITE);
        message.setTypeface(style.textFont);


        logRow.addView(message);

        logRows.add(logRow);
    }

    void makeButtonsTable() {
        buttonsRow.addView(gameField);
        buttonsRow.addView(cards);
        buttonsRow.addView(switchPlayer);
        buttonsRow.addView(discard);
        buttonsRow.addView(tools);
        buttonsRow.addView(log);
        buttonsTable.addView(buttonsRow);
    }


    // Layout params and dependencies


    void setLayoutParams() {
        screen.setBackgroundResource(R.drawable.background2);
        //screen.setBackgroundColor(Color.parseColor("#FF4B2510"));
    }

    void choosePlayer() {
        //table.removeAllViews();
        makeToolsTable(true);
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

        drawTable();

    }

}
