package ru.iisuslik.multiplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ru.iisuslik.controller.Controller;
import ru.tilacyn.saboteur.GameActivity;
import ru.tilacyn.saboteur.R;
import ru.tilacyn.saboteur.SaboteurApplication;

public class MultiPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AAAAAAAA";
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


    Controller controller;
    MultiPlayer multiPlayer;


    public void startGame(int playerCount) {
        Intent intent = new Intent(MultiPlayerActivity.this, GameActivity.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        controller.serialize(byteArrayOutputStream);

        byte [] controllerByteArray = byteArrayOutputStream.toByteArray();

        intent.putExtra("playerCount", playerCount);
        //intent.putExtra("controller", controllerByteArray);
        startActivity(intent);
    }

    public void startSignInIntent() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
            startActivityForResult(multiPlayer.signInClient.getSignInIntent(), RC_SIGN_IN);
        else {
            //showToast("there is last signed in account");
            onConnected(account);
        }
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked() {
        if (multiPlayer.multiplayerClient == null) {
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
        if (multiPlayer.multiplayerClient == null) {
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

    // Create a one-on-one automatch game.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);
        findViewById(R.id.sign_out).setOnClickListener(this);
        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.check).setOnClickListener(this);
        controller = SaboteurApplication.getInstance().getController();
        controller.initializeMultiplayer();
        multiPlayer = controller.multiPlayer;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        multiPlayer.signInClient = GoogleSignIn.getClient(this, gso);
        startSignInIntent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                startSignInIntent();
                break;
            case R.id.sign_out:
                signOut();
                break;
            case R.id.start:
                onStartMatchClicked();
                break;
            case R.id.check:
                onCheckGamesClicked();
                break;


        }
    }

    private void showToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                showToast("bad sign in");
            }
            if (resultCode == RESULT_OK)
                showToast("Nice sign in");

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    //message = getString(R.string.signin_other_error);
                } else {
                    showToast(message);
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
                showToast("Look at matches bad result");
                return;
            }

            TurnBasedMatch match = intent
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                //multiPlayer.updateMatch(match);
                multiPlayer.curMatch = match;
                int playerCount = match.getParticipantIds().size();
                startGame(playerCount);

            }
            //Log.d(TAG, "Match = " + match);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog

            if (resultCode != Activity.RESULT_OK) {
                // user canceled
                //logBadActivityResult(requestCode, resultCode,
                //        "User cancelled returning from 'Select players to Invite' dialog");
                showToast("Invite bad result");
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
            showToast("so close");

            // Start the match
            multiPlayer.multiplayerClient.createMatch(tbmc)
                    .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                        @Override
                        public void onSuccess(TurnBasedMatch turnBasedMatch) {
                            showToast("success createMatch");
                            //multiPlayer.onInitiateMatch(turnBasedMatch);
                            multiPlayer.curMatch = turnBasedMatch;
                            int playerCount = turnBasedMatch.getParticipantIds().size();
                            Log.d(TAG, "onActivityResult() - success createMatch");
                            startGame(playerCount);
                        }
                    });

        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        if (!GoogleSignIn.hasPermissions(googleSignInAccount, Games.SCOPE_GAMES_LITE)) {
            showToast("Doesn't have permissions");
            return;
        }
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
                ).addOnFailureListener(createFailureListener("There was a problem getting the player!"));

        // Retrieve the TurnBasedMatch from the connectionHint

        GamesClient gamesClient = Games.getGamesClient(this, googleSignInAccount);
        gamesClient.getActivationHint()
                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccess(Bundle hint) {
                        if (hint != null) {
                            showToast("getHint");
                            TurnBasedMatch match = hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

                            if (match != null) {
                                multiPlayer.updateMatch(match);
                            }
                        }
                    }
                }).addOnFailureListener(createFailureListener(
                "There was a problem getting the activation hint!"));
        //setViewVisibility();

    }

    private void onDisconnected() {

        Log.d(TAG, "onDisconnected()");

        multiPlayer.multiplayerClient = null;
        multiPlayer.invitationsClient = null;

        //setViewVisibility();
    }

    public void signOut() {

        multiPlayer.signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                            showToast("sign out complete");
                        } else {
                            Log.d(TAG, "signOut(): error");
                        }

                        onDisconnected();
                    }
                });
    }


    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("fail");
                handleException(e, string);
            }
        };
    }

    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof TurnBasedMultiplayerClient.MatchOutOfDateApiException) {
            TurnBasedMultiplayerClient.MatchOutOfDateApiException matchOutOfDateApiException =
                    (TurnBasedMultiplayerClient.MatchOutOfDateApiException) exception;

            new AlertDialog.Builder(this)
                    .setMessage("Match was out of date, updating with latest match data...")
                    .setNeutralButton(android.R.string.ok, null)
                    .show();

            TurnBasedMatch match = matchOutOfDateApiException.getMatch();
            multiPlayer.updateMatch(match);
            status = matchOutOfDateApiException.getStatusCode();
            showToast("match out of date api exception: " + status + ": " + details);

            return;
        }

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
            showToast("apiException: " + status + ": " + details);
        }
        /*
        if (!checkStatusCode(status)) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
        */
    }
}
