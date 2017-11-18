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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class GameActivity extends AppCompatActivity {


    private int[] getCardImageById = new int[100];

    private void fillCardIdArray() {
        getCardImageById[0] = R.drawable.tunnel_pattern_0;
        getCardImageById[1] = R.drawable.tunnel_pattern_0;
        getCardImageById[2] = R.drawable.tunnel_pattern_2;
        getCardImageById[3] = R.drawable.tunnel_pattern_3;
        getCardImageById[4] = R.drawable.tunnel_pattern_4;
        getCardImageById[5] = R.drawable.tunnel_pattern_5;
        getCardImageById[7] = R.drawable.tunnel_pattern_7;
        getCardImageById[9] = R.drawable.tunnel_pattern_9;
        getCardImageById[11] = R.drawable.tunnel_pattern_11;
        getCardImageById[13] = R.drawable.tunnel_pattern_13;
        getCardImageById[6] = R.drawable.tunnel_pattern_6;
        getCardImageById[14] = R.drawable.tunnel_pattern_14;
        getCardImageById[16] = R.drawable.small_tunnel_pattern;
        getCardImageById[15] = R.drawable.tunnel_pattern_15;
        getCardImageById[10] = R.drawable.tunnel_pattern_10;
        getCardImageById[12] = R.drawable.tunnel_pattern_12;
        getCardImageById[8] = R.drawable.tunnel_pattern_8;
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
    private Controller controller = new Controller();
    private HashMap<ImageView, Pair<Integer, Integer>> map = new HashMap();
    private int[] xPos = {0};
    private int[] yPos = {0};
    private int fieldHeight;
    private int fieldWidth;

    int buttonStyle = R.drawable.red_button;

    private boolean isCardChosen = false;
    private Card chosenCard;

    private boolean isDiscard = false;

    // constraint layout
    ConstraintLayout screen;

    // game field
    TableLayout table;

    // table row params for table(game field)
    TableRow.LayoutParams params;

    // YOU

    TextView yourNumber;
    ImageView dwarf;

    // CardsTable, cardsRow and button cards

    Button cards;
    TableRow cardsRow;

    // toolsTable

    // discard
    Button discard;

    // game field
    Button gameField;

    // tools
    Button tools;

    // switch
    Button switchPlayer;

    //spin
    Button spin;

    // buttons table
    TableLayout buttonsTable;
    TableRow buttonsRow;

    // communicating with PlayerChooseActivity
    private static final int PLAYER_CHOOSE = 43;
    private int playerChoose = -1;



    public void initializeAll() {

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

        //spin
        spin = new Button(this);

        // switch
        switchPlayer = new Button(this);

        //buttons table
        buttonsTable = (TableLayout) findViewById(R.id.buttonsTable);
        buttonsRow = new TableRow(this);


    }

    public void makeTools() {
        tools.setText("TOOLS");
        tools.setTextSize(13);
        tools.setBackgroundResource(buttonStyle);
        tools.setTextColor(Color.WHITE);
        tools.setTypeface(Typeface.createFromAsset(getAssets(),"comic.ttf"));

        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpin();
                makeToolsTable();
            }
        });
    }

    public void removeSpin() {
        buttonsRow.removeView(spin);
    }

    public void makeGameField() {
        gameField.setText("FIELD");
        gameField.setTextSize(13);
        gameField.setBackgroundResource(buttonStyle);
        gameField.setTextColor(Color.WHITE);
        gameField.setTypeface(Typeface.createFromAsset(getAssets(),"comic.ttf"));

        gameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpin();
                drawTable();
            }
        });
    }

    public void setDiscard() {
        isDiscard = false;
    }

    public void drawTable() {
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
                } else {
                    if (real.isClosedTunnel() && ((ClosedTunnel) real).isClosed()) {
                        tunnel.setImageResource(R.drawable.hidden_tunnel);
                    } else {
                        if(real.isClosedTunnel() && ((ClosedTunnel) real).isGold()) {
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
                        removeSpin();
                        int x = (int) map.get(tunnel).first;
                        int y = (int) map.get(tunnel).second;
                        //System.out.println(x);
                        //System.out.println(y);
                        if (chosenCard instanceof Tunnel) {
                            //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                            if (((Tunnel) chosenCard).canPlay(x, y)) {
                                ((Tunnel) chosenCard).play(x, y);
                                drawTable();
                            } else {
                                Toast.makeText(getApplicationContext(), "This field " + x + ' ' + y + " is unavailable", Toast.LENGTH_SHORT).show();

                            }
                        }
                        if (chosenCard instanceof Destroy) {
                            //tunnel.setImageResource(getCardImageById[chosenCard.getId()]);
                            if (((Destroy) chosenCard).canPlay(x, y)) {
                                ((Destroy) chosenCard).play(x, y);
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

    public void drawCards() {
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
            cardImage.setImageResource(getCardImageById[playerCards.get(i).getId()]);
            cardImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeSpin();
                    isCardChosen = true;
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
                        if(controller.canStartNextTurn()) {
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
                    if(controller.canStartNextTurn()) {
                        cardsRow.removeView(cardImage);
                    }
                }
            });
        }
    }

    void makeCards() {
        cards.setText("CARDS");
        cards.setTextSize(13);
        cards.setBackgroundResource(buttonStyle);
        cards.setTextColor(Color.WHITE);
        cards.setTypeface(Typeface.createFromAsset(getAssets(),"comic.ttf"));

        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpin();
                drawCards();
            }
        });

    }

    void makeSpin() {
        spin.setText("SPIN");
        spin.setTextSize(13);
        spin.setBackgroundResource(R.drawable.red_button);
        spin.setTextColor(Color.YELLOW);
        spin.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));
        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Tunnel) chosenCard).spin();
                buttonsRow.removeView(spin);
                drawCards();
            }
        });
    }

    void makeYou() {


        yourNumber.setText("Player " + ((Integer) (controller.currentPlayerNumber() + 1)).toString());
        yourNumber.setTextSize(30);
        yourNumber.setGravity(Gravity.CENTER_HORIZONTAL);
        yourNumber.setTextColor(Color.WHITE);
        yourNumber.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));




        dwarf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSaboteur = controller.isCurrentPlayerSaboteur();

                if (!isSaboteur) {
                    dwarf.setImageResource(R.drawable.bread_winner);
                } else {
                    dwarf.setImageResource(0);
                    dwarf.setBackgroundResource(R.drawable.saboteur_animation);
                    ((AnimationDrawable) dwarf.getBackground()).start();
                }
            }
        });

        dwarf.setImageResource(R.drawable.dark_side_of_the_moon);
    }

    void makeToolsTable() {
        table.removeAllViews();
        TableRow[] playerRows = new TableRow[playerCount];
        for (int i = 0; i < playerCount; i++) {

            playerRows[i] = new TableRow(this);
            playerRows[i].setGravity(Gravity.CENTER_HORIZONTAL);


            TextView playerNumber = new TextView(this);
            playerNumber.setText("Player " + ((Integer) (i + 1)).toString());
            playerNumber.setTextSize(30);
            playerNumber.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));


            //playerNumber.setGravity(Gravity.);
            playerNumber.setTextColor(Color.WHITE);

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


            table.addView(playerRows[i]);

        }
    }

    void makeSwitch() {
        switchPlayer.setText("SWITCH");
        switchPlayer.setTextSize(13);
        switchPlayer.setBackgroundResource(buttonStyle);
        switchPlayer.setTextColor(Color.WHITE);
        switchPlayer.setTypeface(Typeface.createFromAsset(getAssets(),"comic.ttf"));


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
                controller.startNextTurn();
                setDiscard();
                makeYou();
                drawTable();
                playerChoose = -1;
            }
        });
    }

    void makeDiscard() {
        discard.setTextSize(13);
        discard.setText("DISCARD");
        discard.setBackgroundResource(buttonStyle);
        discard.setTextColor(Color.WHITE);
        discard.setTypeface(Typeface.createFromAsset(getAssets(),"comic.ttf"));

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSpin();
                isDiscard = !isDiscard;
                drawCards();
            }
        });
    }

    void makeButtonsTable() {
        buttonsRow.addView(gameField);
        buttonsRow.addView(cards);
        buttonsRow.addView(switchPlayer);
        buttonsRow.addView(discard);
        buttonsRow.addView(tools);
        buttonsTable.addView(buttonsRow);
    }

    void setLayoutParams() {
        screen.setBackgroundResource(R.drawable.background2);
        //screen.setBackgroundColor(Color.parseColor("#FF4B2510"));
    }

    void setDependencies() { // to be called once

    }

    void choosePlayer() {
        table.removeAllViews();
        for(int i = 0; i < playerCount; i++) {
            final TableRow playerRow = new TableRow(this);
            TextView playerNumber = new TextView(this);
            playerNumber.setText("Player " + ((Integer) (i + 1)).toString());
            playerNumber.setTextSize(30);
            playerNumber.setTextColor(Color.WHITE);
            playerNumber.setTypeface(Typeface.createFromAsset(getAssets(),"comicbd.ttf"));
            playerRow.addView(playerNumber);
            playerRow.setBackgroundColor(Color.parseColor("#FF4B2510"));
            playerRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playerChoose = Character.getNumericValue(((TextView) playerRow.getVirtualChildAt(0)).getText().charAt(7)) - 1;
                    Toast.makeText(getApplicationContext(), "Player " + Integer.toString(playerChoose + 1),
                            Toast.LENGTH_SHORT).show();
                    if (chosenCard instanceof Debuff) {
                        if (((Debuff) chosenCard).canPlay(playerChoose)) {
                            ((Debuff) chosenCard).play(playerChoose);
                        } else {
                            Toast.makeText(getApplicationContext(), "This gnome cannot be played this way!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (chosenCard instanceof Heal) {
                        if (((Heal) chosenCard).canPlay(playerChoose)) {
                            ((Heal) chosenCard).play(playerChoose);
                        } else {
                            Toast.makeText(getApplicationContext(), "This gnome cannot be played this way!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            table.addView(playerRow);
        }
    }

    void choosePlayerActivity() {
        Intent intent = new Intent(this, PlayerChooseActivity.class);
        intent.putExtra("playerCount", playerCount);
        intent.putExtra("currentPlayerNumber", controller.currentPlayerNumber());
        startActivityForResult(intent, PLAYER_CHOOSE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAYER_CHOOSE) {
            if (resultCode == RESULT_OK) {
                int playerChoose = data.getIntExtra("player", 0);//get player number
                Toast.makeText(getApplicationContext(), "Player " + Integer.toString(playerChoose + 1),
                        Toast.LENGTH_SHORT).show();
                if (chosenCard instanceof Debuff) {
                    if (((Debuff) chosenCard).canPlay(playerChoose)) {
                        ((Debuff) chosenCard).play(playerChoose);
                    }
                }

                if (chosenCard instanceof Heal) {
                    if (((Heal) chosenCard).canPlay(playerChoose)) {
                        ((Heal) chosenCard).play(playerChoose);
                    }
                }
            } else {
                playerChoose = -1;
                Toast.makeText(getApplicationContext(), "Choose please ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        initializeAll();

        playerCount = getIntent().getIntExtra("playerCount", 2);

        controller.initializeField(playerCount);

        fillCardIdArray();

        fieldHeight = controller.getHeight();
        fieldWidth = controller.getWidth();

        setLayoutParams();

        setDependencies();

        drawTable();

        makeCards();

        makeYou();

        makeDiscard();

        makeSwitch();

        makeTools();

        makeGameField();

        makeButtonsTable();

    }

}
