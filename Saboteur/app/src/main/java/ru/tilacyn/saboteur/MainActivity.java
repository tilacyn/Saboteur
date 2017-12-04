package ru.tilacyn.saboteur;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import ru.iisuslik.controller.Controller;

public class MainActivity extends AppCompatActivity {


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
        String res = "";
        for(int i = 0; i < b.length; i++) {
            res = res + (char) b[i];
        }
        return res;
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
        settingsOpen = false;

        playerCount = 2;

        controller = new Controller();
        style = new Style();
        layouts = new Layouts();

        canContinue = false;

        logDatabaseHelper = new LogDatabaseHelper(getApplicationContext());
        logDatabaseHelper.create_db();
        logDb = logDatabaseHelper.open();

    }

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
        Button loadGame;
        Button loadOrSave;
        Button settings;
        TextView lyrics;
        Button save;
        Button back;

        HorizontalScrollView hscrollPCT;
        TableLayout playerCountTable;
        Cursor cursor;
        TableRow playerCountRow;
        ConstraintLayout screen;
        TextView name;
        Log log;
        String textInput;



        private class Log {
            ListView list;
            Button back;
            Button load;
            Button show;

            String chosenGame;

            Log() {
                list = (ListView) findViewById(R.id.logList);
                load = (Button) findViewById(R.id.load);
                show = (Button) findViewById(R.id.show);
                this.back = (Button) findViewById(R.id.logBack);
                chosenGame = null;
            }

            void makeLoadValid() {
                load.setTextColor(Color.WHITE);
            }

            void makeLoadInvalid() {
                load.setTextColor(Color.GRAY);
            }

            void makeList() {
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        String gameName = ((TextView) itemClicked).getText().toString();
                        if (chosenGame != gameName) {
                            chosenGame = gameName;
                            makeLoadValid();
                        } else {
                            chosenGame = null;
                            makeLoadInvalid();
                        }
                    }
                });
            }

            void updateList() {
                cursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE, null);

                cursor.moveToFirst();
                final String[] names = new String[cursor.getCount()];
                for (int i = 0; i < names.length; i++) {
                    names[i] = cursor.getString(0);
                    cursor.moveToNext();
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.log_list_item1
                        , names);

                log.list.setAdapter(adapter);
            }

            void makeLoad() {
                load.setText("load");
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
                        cursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE + " where name = '" + chosenGame + "'", null);
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
                        style.updateSelected(Log.this.back);
                        showMainMenu();
                    }
                });
            }

            void makeAllViews() {
                this.makeBack();
                makeShow();
                makeLoad();
                makeList();
            }

            void showList() {
                screen.removeAllViews();
                screen.addView(list);
                screen.addView(load);
                screen.addView(show);
                screen.addView(back);
            }
        }


        Layouts() {
            screen = (ConstraintLayout) findViewById(R.id.activity_main);

            screen.setBackgroundResource(R.drawable.dwarf_lords);

            playerCountRow = new TableRow(MainActivity.this);
            playerCountTable = (TableLayout) findViewById(R.id.playerCountTable);
            hscrollPCT = (HorizontalScrollView) findViewById(R.id.playerCountScrollView);

            name = (TextView) findViewById(R.id.Saboteur);

            continueGame = (Button) findViewById(R.id.continueGame);
            newGame = (Button) findViewById(R.id.newGame);
            loadGame = (Button) findViewById(R.id.loadGame);
            loadOrSave = (Button) findViewById(R.id.loadOrSave);
            settings = (Button) findViewById(R.id.settings);
            lyrics = (TextView) findViewById(R.id.lyrics);
            save = (Button) findViewById(R.id.save);
            back = (Button) findViewById(R.id.back);
            log = new Log();
        }

        void makeName() {
            name.setText("Saboteur");
            name.setTextSize(60);
            name.setTextColor(Color.YELLOW);
            name.setTypeface(Typeface.createFromAsset(getAssets(), "AL Fantasy Type.ttf"));
        }

        void makeContinue() {
            continueGame.setText("Continue");
            style.decorateButton(continueGame);
            continueGame.setOnClickListener(new View.OnClickListener() {
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

        }

        void makeSave() {
            save.setText("Save");
            style.decorateButton(save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (controller == null || !controller.isFieldInitialized()) {
                        Toast.makeText(getApplicationContext(), "No active game",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE, null);
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
                                    Cursor checkCursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE + " where name = '" + textInput + "'", null);

                                    if(checkCursor.getCount() != 0) {
                                        Toast.makeText(getApplicationContext(), "This name already exists!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    controller.serialize(byteArrayOutputStream);
                                    ContentValues values = new ContentValues();
                                    values.put(logDatabaseHelper.COLUMN_NAME, textInput);
                                    values.put(logDatabaseHelper.COLUMN_LOG, byteArrayToString(byteArrayOutputStream.toByteArray()));


                                    long newRowId = logDb.insert(logDatabaseHelper.TABLE, null, values);
                                    log.updateList();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    textInput = editGameName.getText().toString();
                                    dialog.cancel();
                                    if(textInput.length() == 0) {
                                        Toast.makeText(getApplicationContext(), "No input, try again!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Cursor checkCursor = logDb.rawQuery("select * from " + logDatabaseHelper.TABLE + " where name = '" + textInput + "'", null);

                                    if(checkCursor.getCount() != 0) {
                                        Toast.makeText(getApplicationContext(), "This name already exists!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }


                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    controller.serialize(byteArrayOutputStream);
                                    ContentValues values = new ContentValues();
                                    values.put(logDatabaseHelper.COLUMN_NAME, textInput);
                                    values.put(logDatabaseHelper.COLUMN_LOG, byteArrayToString(byteArrayOutputStream.toByteArray()));

                                    long newRowId = logDb.insert(logDatabaseHelper.TABLE, null, values);

                                    log.updateList();
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
            back.setText("back");
            style.decorateButton(back);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMainMenu();
                }
            });
        }

        void makeNewGame() {
            newGame.setText("New Game");
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
            loadGame.setText("Load Game");
            style.decorateButton(loadGame);

            loadGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    style.updateSelected(loadGame);
                    log.updateList();
                    log.showList();
                }
            });
        }

        void makeLoadOrSave() {
            loadOrSave.setText("Load/Save");
            style.decorateButton(loadOrSave);
            loadOrSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSaveLoad();
                }
            });
        }

        void makeSettings() {
            layouts.settings.setText("Settings");
            style.decorateButton(layouts.settings);


            layouts.settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!settingsOpen) {
                        style.updateSelected(settings);
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
                            playerCountRow.addView(b);
                        }
                        playerCountTable.addView(playerCountRow);
                        settingsOpen = true;
                    } else {
                        style.removeSelected();
                        playerCountRow.removeAllViews();
                        playerCountTable.removeAllViews();
                        settingsOpen = false;
                    }
                }
            });
        }

        void makeLyrics() {
            lyrics.setText("Seven for the Dwarf-lords\nIn their halls of stone");
            lyrics.setTextColor(Color.YELLOW);
            lyrics.setTypeface(Typeface.createFromAsset(getAssets(), "odessa.ttf"));
            lyrics.setTextSize(30);
        }

        void makeAllViews() {
            makeName();
            makeContinue();
            makeNewGame();
            makeLoadGame();
            makeLoadOrSave();
            makeSettings();
            makeLyrics();
            makeBack();
            makeSave();
            log.makeAllViews();
        }

        void showMainMenu() {
            screen.removeAllViews();
            screen.addView(name);
            screen.addView(continueGame);
            screen.addView(newGame);
            screen.addView(settings);
            screen.addView(lyrics);
            screen.addView(loadOrSave);
            screen.addView(hscrollPCT);
            style.removeSelected();
        }

        void showSaveLoad() {
            screen.removeAllViews();
            screen.addView(name);
            screen.addView(loadGame);
            screen.addView(save);
            screen.addView(back);
            screen.addView(lyrics);
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAll();

        layouts.makeAllViews();

        layouts.showMainMenu();


    }
}
