package ru.iisuslik.multiplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import ru.iisuslik.controller.Controller;
import ru.tilacyn.saboteur.GameActivity;
import ru.tilacyn.saboteur.MainActivity;
import ru.tilacyn.saboteur.R;
import ru.tilacyn.saboteur.SaboteurApplication;

public class MultiPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


    Controller controller;
    MultiPlayer multiPlayer;


    public void startGame(int playerCount){
        Intent intent = new Intent(MultiPlayerActivity.this, GameActivity.class);
        intent.putExtra("playerCount", playerCount);
        startActivity(intent);
    }

    public void startSignInIntent() {
        startActivityForResult(multiPlayer.signInClient.getSignInIntent(), RC_SIGN_IN);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked() {
        if(multiPlayer.multiplayerClient == null){
            return;
        }
        multiPlayer.multiplayerClient.getSelectOpponentsIntent(1, 7, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                });
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked() {
        if(multiPlayer.multiplayerClient == null){
            return;
        }
        multiPlayer.multiplayerClient.getInboxIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);
        findViewById(R.id.sign_out).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.check).setOnClickListener(this);
        controller = new Controller();
        controller.initializeMultiplayer();
        multiPlayer = controller.multiPlayer;
        multiPlayer.signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                startSignInIntent();
                break;
            case R.id.start:
                onStartMatchClicked();
                break;
            case R.id.check:
                onCheckGamesClicked();
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    //message = getString(R.string.signin_other_error);
                }

                onDisconnected();
                /*
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
                */
            }
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (resultCode != Activity.RESULT_OK) {
                //logBadActivityResult(requestCode, resultCode,
                //       "User cancelled returning from the 'Select Match' dialog.");
                return;
            }

            TurnBasedMatch match = intent
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                multiPlayer.updateMatch(match);
            }

            int playerCount = match.getParticipantIds().size();
            startGame(playerCount);

            //Log.d(TAG, "Match = " + match);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                //logBadActivityResult(requestCode, resultCode,
                //        "User cancelled returning from 'Select players to Invite' dialog");
                return;
            }

            // get the invitee list
            ArrayList<String> invitees = intent
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria;

            int minAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers,
                        maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            multiPlayer.multiplayerClient.createMatch(tbmc)
                    .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                        @Override
                        public void onSuccess(TurnBasedMatch turnBasedMatch) {
                            multiPlayer.onInitiateMatch(turnBasedMatch);
                            int playerCount = turnBasedMatch.getParticipantIds().size();
                            startGame(playerCount);
                        }
                    });

        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {

        multiPlayer.multiplayerClient = Games.getTurnBasedMultiplayerClient(this, googleSignInAccount);
        multiPlayer.invitationsClient = Games.getInvitationsClient(this, googleSignInAccount);

        Games.getPlayersClient(this, googleSignInAccount)
                .getCurrentPlayer()
                .addOnSuccessListener(
                        new OnSuccessListener<Player>() {
                            @Override
                            public void onSuccess(Player player) {
                                //mDisplayName = player.getDisplayName();
                                multiPlayer.playerId = player.getPlayerId();

                                //setViewVisibility();
                            }
                        }
                );

        // Retrieve the TurnBasedMatch from the connectionHint
        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (match != null) {
                                multiPlayer.updateMatch(match);
                            }
                        }
                    }
                });
        //setViewVisibility();
    }

    private void onDisconnected() {

        //Log.d(TAG, "onDisconnected()");

        multiPlayer.multiplayerClient = null;
        multiPlayer.invitationsClient = null;

        //setViewVisibility();
    }
}
