package ru.tilacyn.saboteur;


//import ru.iisuslik.*;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat.*;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import ru.iisuslik.controller.Controller;

public class MainActivity extends AppCompatActivity {

    private class Style {
        Button selected;
        Button selectedPlayerCount;
        int selectedButtonStyle = R.drawable.yellow_button;
        int selectedTextColor = Color.BLACK;

        int buttonStyle = R.drawable.brown_button;
        int buttonTextColor = Color.WHITE;

        Typeface buttonFont = Typeface.createFromAsset(getAssets(), "almendra.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "almendra.ttf");

        Style() {
            selected = null;

        }

        void decorateButton(Button b) {
            b.setTextSize(13);
            b.setBackgroundResource(buttonStyle);
            b.setTextColor(buttonTextColor);
            b.setTypeface(buttonFont);
        }

        void updateSelected(Button b) {
            if (selected != null) {
                selected.setBackgroundResource(style.buttonStyle);
                selected.setTextColor(style.buttonTextColor);
            }

            selected = b;
            selected.setBackgroundResource(selectedButtonStyle);
            selected.setTextColor(selectedTextColor);
        }

        void updateSelectedPlayerCount(Button b) {
            if (selectedPlayerCount != null) {
                selectedPlayerCount.setBackgroundResource(buttonStyle);
                selectedPlayerCount.setTextColor(buttonTextColor);
            }

            selectedPlayerCount = b;
            selectedPlayerCount.setBackgroundResource(selectedButtonStyle);
            selectedPlayerCount.setTextColor(selectedTextColor);
        }


        void removeSelected() {
            if (selected != null) {
                selected.setBackgroundResource(buttonStyle);
                selected.setTextColor(buttonTextColor);
            }
            selected = null;
        }
    }

    private class Layouts {
        //Button help;
        Button newGame;
        Button continueGame;
        Button settings;
        TextView lyrics;
        TableLayout playerCountTable;
        TableRow playerCountRow;
        ConstraintLayout screen;
        TextView name;

        Layouts() {
            screen = (ConstraintLayout) findViewById(R.id.activity_main);

            screen.setBackgroundResource(R.drawable.dwarf_lords);

            playerCountRow = new TableRow(MainActivity.this);
            playerCountTable = (TableLayout) findViewById(R.id.playerCountTable);

            name = (TextView) findViewById(R.id.Saboteur);

            continueGame = (Button) findViewById(R.id.continueGame);
            newGame = (Button) findViewById(R.id.newGame);
            //help  = (Button) findViewById(R.id.help);
            settings = (Button) findViewById(R.id.settings);
            lyrics = (TextView) findViewById(R.id.lyrics);

        }
    }

    private Style style;

    private Layouts layouts;


    private int playerCount;
    private int savedPlayerCount;

    private boolean settingsOpen;

    private Controller controller;

    // for managing controller

    private byte[] controllerByteArray;
    private int reqCode = 43;

    private boolean canContinue;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCode) {
            if (resultCode == RESULT_OK) {
                if(data != null) {
                    controllerByteArray = data.getByteArrayExtra("controller");//get player number
                    controller = Controller.deserialize(new ByteArrayInputStream(controllerByteArray));
                    canContinue = true;
                } else {
                    Toast.makeText(getApplicationContext(), "data = null",
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong. For probable solutions see https://saboteur.com/pepec",
                        Toast.LENGTH_SHORT).show();
            }
        }
        style.removeSelected();
    }

    void initializeAll() {
        settingsOpen = false;

        playerCount = 2;

        controller = new Controller();
        layouts = new Layouts();
        style = new Style();

        canContinue = false;
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeAll();

        layouts.name.setText("Saboteur");
        layouts.name.setTextSize(60);
        layouts.name.setTextColor(Color.YELLOW);
        layouts.name.setTypeface(Typeface.createFromAsset(getAssets(), "AL Fantasy Type.ttf"));


        layouts.continueGame.setText("Continue");
        style.decorateButton(layouts.continueGame);


        layouts.newGame.setText("New Game");
        style.decorateButton(layouts.newGame);


        layouts.continueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canContinue) {
                    return;
                }
                style.updateSelected(layouts.continueGame);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                controller.serialize(byteArrayOutputStream);

                controllerByteArray = byteArrayOutputStream.toByteArray();

                intent.putExtra("playerCount", savedPlayerCount);
                intent.putExtra("controller", controllerByteArray);

                startActivityForResult(intent, reqCode);
            }
        });

        layouts.newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canContinue = true;
                controller = new Controller();
                controller.initializeField(playerCount);

                savedPlayerCount = playerCount;

                style.updateSelected(layouts.newGame);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                controller.serialize(byteArrayOutputStream);

                controllerByteArray = byteArrayOutputStream.toByteArray();

                intent.putExtra("playerCount", playerCount);
                intent.putExtra("controller", controllerByteArray);

                startActivityForResult(intent, reqCode);
            }
        });


        //layouts.help.setText("Help");
        //style.decorateButton(layouts.help);

        layouts.settings.setText("Settings");
        style.decorateButton(layouts.settings);


        layouts.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settingsOpen) {
                    style.updateSelected(layouts.settings);
                    for (int i = 2; i < 8; i++) {
                        final Button b = new Button(MainActivity.this);
                        b.setWidth(30);
                        b.setBackgroundResource(style.buttonStyle);
                        b.setTypeface(Typeface.createFromAsset(getAssets(), "almendra.ttf"));
                        b.setText(((Integer) i).toString());
                        b.setTextSize(15);
                        b.setTextColor(Color.WHITE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                style.updateSelectedPlayerCount(b);
                                playerCount = Character.getNumericValue(b.getText().charAt(0));
                            }
                        });
                        layouts.playerCountRow.addView(b);
                    }
                    layouts.playerCountTable.addView(layouts.playerCountRow);
                    settingsOpen = true;
                } else {
                    style.removeSelected();
                    layouts.playerCountRow.removeAllViews();
                    layouts.playerCountTable.removeAllViews();
                    settingsOpen = false;
                }
            }
        });

        layouts.lyrics.setText("Seven for the Dwarf-lords\nIn their halls of stone");
        layouts.lyrics.setTextColor(Color.YELLOW);
        layouts.lyrics.setTypeface(Typeface.createFromAsset(getAssets(), "odessa.ttf"));
        layouts.lyrics.setTextSize(30);

    }
}
