package ru.tilacyn.saboteur;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import ru.iisuslik.controller.Controller;
import ru.iisuslik.multiplayer.MultiPlayerActivity;

public class MainActivity extends AppCompatActivity {

    private Style style;

    private Layouts layouts;


    private int playerCount;
    private int savedPlayerCount;


    private Controller controller;

    // for managing controller

    private byte[] controllerByteArray;
    private int reqCode = 43;

    private boolean canContinue;

    // for database
    SQLiteDatabase logDb;
    LogDatabaseHelper logDatabaseHelper;

    byte[] stringToByteArray(String s) {
        byte[] res = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            res[i] = (byte) s.charAt(i);
        }
        return res;
    }

    String byteArrayToString(byte[] b) {
        StringBuilder res = new StringBuilder();
        for (byte aB : b) {
            res.append((char) aB);
        }
        return res.toString();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCode) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    controllerByteArray = data.getByteArrayExtra("controller");
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
        playerCount = 2;

        controller = new Controller();
        style = new Style();
        layouts = new Layouts();

        canContinue = false;

        logDatabaseHelper = new LogDatabaseHelper(getApplicationContext());
        logDatabaseHelper.create_db();
        logDb = logDatabaseHelper.open();
        SaboteurApplication.getInstance().tunnels = loadJSONFromAsset("tunnels.json");
        SaboteurApplication.getInstance().actions = loadJSONFromAsset("actions.json");
    }

    private int[][] loadJSONFromAsset(String file) {
        int [][] res;
        try {
            InputStream is = this.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONArray json = new JSONArray(new String(buffer, "UTF-8"));
            res = new int[json.length()][json.getJSONArray(0).length()];
            for (int i = 0; i < json.length(); i++) {
                for(int j = 0; j < json.getJSONArray(i).length(); j++) {
                    res[i][j] = json.getJSONArray(i).getInt(j);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    /**
     * inner class for managing style features
     */
    private class Style {
        Button selected;
        Button selectedPlayerCount;
        int selectedButtonStyle = R.drawable.yellow_button;
        int selectedTextColor = Color.BLACK;

        int textBackgroundColor = Color.parseColor("#FF4B2510");

        int buttonStyle = R.drawable.red_button;
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

    /**
     * inner class for managing layouts
     */
    private class Layouts {
        ConstraintLayout screen;
        TextView name;
        TextView lyrics;

        Main main;
        SinglePlayer singlePlayer;
        MultiPlayer multiPlayer;


        /**
         * inner class for managing layouts necessary for single player and multi player both
         */
        private class Main {
            Button multiPlayerButton;
            Button singlePlayerButton;
            Button help;

            Help helpClass;

            private class Help {
                TextView rules;

                TextView helpText;

                ScrollView helpScroll;

                Button back;

                Help() {
                    back = (Button) findViewById(R.id.helpBack);
                    helpText = (TextView) findViewById(R.id.helpText);
                    rules = (TextView) findViewById(R.id.rules);
                    helpScroll = (ScrollView) findViewById(R.id.helpScrollView);
                }

                void show() {
                    screen.removeAllViews();
                    screen.addView(helpScroll);
                    screen.addView(rules);
                    screen.addView(back);
                }

                void makeRules() {
                    rules.setTextSize(25);
                    rules.setTextColor(Color.YELLOW);
                    rules.setTypeface(style.textFont);

                    rules.setText(R.string.rules);
                }

                void makeHelpText() {
                    helpText.setTextSize(13);
                    helpText.setBackgroundColor(style.textBackgroundColor);
                    helpText.setTextColor(Color.WHITE);
                    helpText.setTypeface(style.textFont);
                    helpText.setText(getString(R.string.rules_text));
                }

                void makeBack() {
                    style.decorateButton(back);
                    back.setText(R.string.back_name);

                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            main.show();
                        }
                    });
                }

                void makeAllViews() {
                    makeBack();
                    makeHelpText();
                    makeRules();
                }

            }

            Main() {
                multiPlayerButton = (Button) findViewById(R.id.multiPlayerButton);
                singlePlayerButton = (Button) findViewById(R.id.singlePlayerButton);
                help = (Button) findViewById(R.id.help);
                helpClass = new Help();
            }

            //ok
            void makeSinglePlayerButton() {
                style.decorateButton(singlePlayerButton);
                singlePlayerButton.setText(R.string.single_player_name);

                singlePlayerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        singlePlayer.show();
                    }
                });
            }

            //ok
            void makeMultiPlayerButton() {
                style.decorateButton(multiPlayerButton);
                multiPlayerButton.setText(R.string.multi_player_name);

                multiPlayerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        multiPlayer.show();
                    }
                });
            }

            void makeHelp() {
                style.decorateButton(help);
                help.setText(R.string.help_name);

                help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helpClass.show();
                    }
                });
            }

            void makeAllViews() {
                makeMultiPlayerButton();
                makeSinglePlayerButton();
                makeHelp();
                helpClass.makeAllViews();
            }

            void show() {
                screen.removeAllViews();
                addCoolViews();

                screen.addView(multiPlayerButton);
                screen.addView(singlePlayerButton);
                screen.addView(help);
            }

        }

        /**
         * inner class for managing single player layouts (buttons and etc)
         */
        private class SinglePlayer {
            Button continueGame;
            Button loadGame;
            Button settings;
            Button newGame;
            Button save;
            Button back;

            Cursor cursor;

            String textInput;

            GameLoader gameLoader;

            ScrollView scrollPCT;
            TableLayout playerCountTable;

            SinglePlayer() {
                playerCountTable = (TableLayout) findViewById(R.id.playerCountTable);
                scrollPCT = (ScrollView) findViewById(R.id.playerCountScrollView);

                continueGame = (Button) findViewById(R.id.continueGame);
                newGame = (Button) findViewById(R.id.newGame);

                loadGame = (Button) findViewById(R.id.loadGame);
                settings = (Button) findViewById(R.id.settings);


                save = (Button) findViewById(R.id.save);
                back = (Button) findViewById(R.id.singlePlayerBack);

                gameLoader = new GameLoader();
            }

            void makeContinue() {
                continueGame.setText(R.string.continue_name);
                style.decorateButton(continueGame);
                continueGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!canContinue) {
                            return;
                        }
                        style.updateSelected(continueGame);
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        controller.serialize(byteArrayOutputStream);

                        controllerByteArray = byteArrayOutputStream.toByteArray();

                        intent.putExtra("playerCount", savedPlayerCount);
                        intent.putExtra("controller", controllerByteArray);

                        startActivityForResult(intent, reqCode);
                    }
                });

            }

            void makeSave() {
                save.setText(R.string.save_name);
                style.decorateButton(save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (controller == null || !controller.isFieldInitialized()) {
                            Toast.makeText(getApplicationContext(), "No active game",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        cursor = logDb.rawQuery("select * from " + LogDatabaseHelper.TABLE, null);
                        cursor.moveToFirst();

                        LayoutInflater inflater = getLayoutInflater();
                        View dialogLayout = inflater.inflate(R.layout.dialog_layout, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setView(dialogLayout);

                        final EditText editGameName = (EditText) dialogLayout.findViewById(R.id.editGameName);

                        builder.setTitle("Save")
                                .setMessage("Enter game name")
                                .setIcon(R.drawable.arkenstone)
                                .setCancelable(false)
                                .setNegativeButton("Default", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        textInput = "game" + ((Integer) cursor.getCount()).toString();
                                        dialog.cancel();
                                        Cursor checkCursor = logDb.rawQuery("select * from " + LogDatabaseHelper.TABLE + " where name = '" + textInput + "'", null);

                                        if (checkCursor.getCount() != 0) {
                                            Toast.makeText(getApplicationContext(), "This name already exists!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }


                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        controller.serialize(byteArrayOutputStream);
                                        ContentValues values = new ContentValues();
                                        values.put(LogDatabaseHelper.COLUMN_NAME, textInput);
                                        values.put(LogDatabaseHelper.COLUMN_LOG, byteArrayToString(byteArrayOutputStream.toByteArray()));


                                        long newRowId = logDb.insert(LogDatabaseHelper.TABLE, null, values);
                                        gameLoader.updateList("*");
                                    }
                                })
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        textInput = editGameName.getText().toString();
                                        dialog.cancel();
                                        if (textInput.length() == 0) {
                                            Toast.makeText(getApplicationContext(), "No input, try again!",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        Cursor checkCursor = logDb.rawQuery("select * from " + LogDatabaseHelper.TABLE + " where name = '" + textInput + "'", null);

                                        if (checkCursor.getCount() != 0) {
                                            Toast.makeText(getApplicationContext(), "This name already exists!",
                                                    Toast.LENGTH_SHORT).show();
                                            checkCursor.close();
                                            return;
                                        }
                                        checkCursor.close();


                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        controller.serialize(byteArrayOutputStream);
                                        ContentValues values = new ContentValues();
                                        values.put(LogDatabaseHelper.COLUMN_NAME, textInput);
                                        values.put(LogDatabaseHelper.COLUMN_LOG, byteArrayToString(byteArrayOutputStream.toByteArray()));

                                        long newRowId = logDb.insert(LogDatabaseHelper.TABLE, null, values);

                                        gameLoader.updateList("*");
                                    }
                                }).setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }

            void makeBack() {
                back.setText(R.string.back_name);
                style.decorateButton(back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.show();
                    }
                });
            }

            void makeNewGame() {
                newGame.setText(R.string.new_game_name);
                style.decorateButton(newGame);

                newGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canContinue = true;
                        controller = new Controller();
                        controller.initializeField(playerCount);

                        savedPlayerCount = playerCount;

                        style.updateSelected(newGame);
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        controller.serialize(byteArrayOutputStream);

                        controllerByteArray = byteArrayOutputStream.toByteArray();

                        intent.putExtra("playerCount", playerCount);
                        intent.putExtra("controller", controllerByteArray);

                        startActivityForResult(intent, reqCode);
                    }
                });
            }

            void makeLoadGame() {
                loadGame.setText(R.string.load_game_name);
                style.decorateButton(loadGame);

                loadGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        style.updateSelected(loadGame);
                        gameLoader.updateList("*");
                        gameLoader.show();
                    }
                });
            }

            void makeSettings() {
                settings.setText(R.string.settings_name);
                style.decorateButton(settings);

                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSettings();
                        for (int i = 2; i < 8; i++) {
                            final Button b = new Button(MainActivity.this);
                            b.setWidth(30);
                            b.setBackgroundResource(style.buttonStyle);
                            b.setTypeface(Typeface.createFromAsset(getAssets(), "almendra.ttf"));
                            b.setText(((Integer) i).toString());
                            b.setTextSize(15);
                            b.setTextColor(Color.WHITE);
                            b.setScaleX((float) 0.8);
                            b.setScaleY((float) 0.8);
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    style.updateSelectedPlayerCount(b);
                                    playerCount = Character.getNumericValue(b.getText().charAt(0));
                                    singlePlayer.show();
                                    playerCountTable.removeAllViews();
                                }
                            });
                            TableRow playerCountRow = new TableRow(MainActivity.this);
                            playerCountRow.addView(b);
                            playerCountTable.addView(playerCountRow);
                        }
                    }
                });
            }

            void makeAllViews() {
                makeContinue();
                makeNewGame();
                makeBack();
                makeLoadGame();
                makeSave();
                makeSettings();
                gameLoader.makeAllViews();
            }

            void show() {
                screen.removeAllViews();
                addCoolViews();
                screen.addView(continueGame);
                screen.addView(newGame);
                screen.addView(loadGame);
                screen.addView(save);
                screen.addView(settings);
                screen.addView(back);
            }

            void showSettings() {
                screen.removeAllViews();
                addCoolViews();
                screen.addView(scrollPCT);
            }

            private class GameLoader {
                ListView list;
                Button back;
                Button load;
                Button show;
                EditText search;
                ImageButton magnifier;


                String chosenGame;

                GameLoader() {
                    list = (ListView) findViewById(R.id.gameLoaderList);
                    load = (Button) findViewById(R.id.gameLoaderLoad);
                    show = (Button) findViewById(R.id.show);
                    search = (EditText) findViewById(R.id.search);
                    magnifier = (ImageButton) findViewById(R.id.magnifier);
                    this.back = (Button) findViewById(R.id.gameLoaderBack);
                    chosenGame = null;
                }

                void makeLoadValid() {
                    load.setTextColor(Color.WHITE);
                }

                void makeLoadInvalid() {
                    load.setTextColor(Color.GRAY);
                }

                void makeList() {
                    list.setBackgroundColor(style.textBackgroundColor);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                            String gameName = ((TextView) itemClicked).getText().toString();
                            if (!(chosenGame.equals(gameName))) {
                                chosenGame = gameName;
                                makeLoadValid();
                            } else {
                                chosenGame = null;
                                makeLoadInvalid();
                            }
                        }
                    });
                }

                void updateList(String textInput) {
                    if (textInput == "*") {
                        cursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE, null);
                    } else {
                        cursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE + " where name like '%" + textInput + "%'", null);
                    }


                    cursor.moveToFirst();
                    final String[] names = new String[cursor.getCount()];
                    for (int i = 0; i < names.length; i++) {
                        names[i] = cursor.getString(0);
                        cursor.moveToNext();
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.log_list_item1
                            , names);

                    list.setAdapter(adapter);
                }

                void makeLoad() {
                    load.setText(R.string.load_name);
                    style.decorateButton(load);
                    makeLoadInvalid();

                    load.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (chosenGame == null) {
                                return;
                            }
                            style.updateSelected(load);
                            cursor.moveToFirst();
                            cursor = logDb.rawQuery("select * from " + LogDatabaseHelper.TABLE + " where name = '" + chosenGame + "'", null);
                            cursor.moveToFirst();
                            controllerByteArray = stringToByteArray(cursor.getString(1));

                            controller = Controller.deserialize(new ByteArrayInputStream(controllerByteArray));
                            savedPlayerCount = controller.getPlayersNumber();
                            Intent intent = new Intent(MainActivity.this, GameActivity.class);

                            intent.putExtra("playerCount", savedPlayerCount);
                            intent.putExtra("controller", controllerByteArray);

                            startActivityForResult(intent, reqCode);

                        }
                    });
                }

                void makeShow() {
                    show.setText("show");
                    style.decorateButton(show);
                    show.setTextColor(Color.GRAY);
                }

                void makeBack() {
                    this.back.setText("back");
                    style.decorateButton(this.back);
                    this.back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            style.updateSelected(back);
                            singlePlayer.show();
                        }
                    });
                }

                void makeSearch() {
                    //search.setTextColor(Color.WHITE);
                    search.setBackgroundColor(Color.WHITE);
                    search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            String textInput = search.getText().toString();
                            if(textInput == null) {
                                textInput = "*";
                            } else if (textInput.length() == 0) {
                                textInput = "*";
                            }
                            updateList(textInput);
                            return false;
                        }
                    });
                }

                void makeMagnifier() {
                    magnifier.setImageResource(R.drawable.search);
                    magnifier.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String textInput = search.getText().toString();
                            if(textInput == null) {
                                textInput = "*";
                            } else if (textInput.length() == 0) {
                                textInput = "*";
                            }
                            updateList(textInput);
                        }
                    });
                }

                void makeAllViews() {
                    this.makeBack();
                    makeShow();
                    makeLoad();
                    makeList();
                    makeMagnifier();
                    makeSearch();
                }

                void show() {
                    screen.removeAllViews();
                    screen.addView(list);
                    screen.addView(load);
                    screen.addView(show);
                    screen.addView(back);
                    screen.addView(magnifier);
                    screen.addView(search);
                }
            }
        }


        /**
         * inner class for managing multi player layouts (buttons and etc)
         */
        private class MultiPlayer {
            Button newGame;
            Button back;

            MultiPlayer() {
                back = (Button) findViewById(R.id.multiPlayerBack);
                newGame = (Button) findViewById(R.id.multiPlayerNewGame);
            }


            void makeNewGame() {
                style.decorateButton(newGame);
                newGame.setText("Multiplayer");

                newGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, MultiPlayerActivity.class);
                        startActivity(intent);
                    }
                });

            }

            void makeBack() {
                style.decorateButton(back);
                back.setText("back");

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main.show();
                    }
                });
            }

            void makeAllViews() {
                makeNewGame();
                makeBack();
            }

            void show() {
                screen.removeAllViews();
                addCoolViews();

                screen.addView(back);
                screen.addView(newGame);
            }
        }

        Layouts() {
            screen = (ConstraintLayout) findViewById(R.id.activity_main);

            screen.setBackgroundResource(R.drawable.dwarf_lords);

            name = (TextView) findViewById(R.id.Saboteur);

            lyrics = (TextView) findViewById(R.id.lyrics);

            singlePlayer = new SinglePlayer();
            multiPlayer = new MultiPlayer();
            main = new Main();
        }

        void makeName() {
            name.setText("Saboteur");
            name.setTextSize(60);
            name.setTextColor(Color.YELLOW);
            name.setTypeface(Typeface.createFromAsset(getAssets(), "AL Fantasy Type.ttf"));
        }

        void makeLyrics() {
            lyrics.setText("Seven for the Dwarf-lords\nIn their halls of stone");
            lyrics.setTextColor(Color.YELLOW);
            lyrics.setTypeface(Typeface.createFromAsset(getAssets(), "odessa.ttf"));
            lyrics.setTextSize(30);
        }

        void makeAllViews() {
            makeName();
            makeLyrics();

            multiPlayer.makeAllViews();
            singlePlayer.makeAllViews();
            main.makeAllViews();
        }

        void addCoolViews() {
            screen.addView(name);
            screen.addView(lyrics);
        }

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAll();

        layouts.makeAllViews();

        layouts.main.show();

    }
}
