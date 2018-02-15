package ru.iisuslik.multiplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;

import ru.iisuslik.controller.Controller;
import ru.tilacyn.saboteur.GameActivity;
import ru.tilacyn.saboteur.R;
import ru.tilacyn.saboteur.SaboteurApplication;

public class MultiPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MPA";
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


    Controller controller;
    MultiPlayer multiPlayer;


    public void startGame(int playerCount) {
        Intent intent = new Intent(MultiPlayerActivity.this, GameActivity.class);
        controller.gameData = null;
        controller.field = null;
        intent.putExtra("playerCount", playerCount);
        startActivity(intent);
    }

    public void startSignInIntent() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null)
            startActivityForResult(multiPlayer.signInClient.getSignInIntent(), RC_SIGN_IN);
        else {
            onConnected(account);
        }
    }

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_player);
        //get controller
        controller = SaboteurApplication.getInstance().getController();
        controller.initializeMultiplayer();
        multiPlayer = controller.multiPlayer;
        //sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        multiPlayer.signInClient = GoogleSignIn.getClient(this, gso);
        startSignInIntent();
        //set backgtound
        ConstraintLayout screen = (ConstraintLayout) findViewById(R.id.activity_multiplayer);
        screen.setBackgroundResource(R.drawable.background3);
        makeButtons();
    }

    private void makeButtons() {
        //sign in
        Button signIn = (Button) findViewById(R.id.sign_in);
        decorateButton(signIn);
        signIn.setOnClickListener(this);
        //sign out
        Button signOut = (Button) findViewById(R.id.sign_out);
        decorateButton(signOut);
        signOut.setOnClickListener(this);
        //start
        Button start = (Button) findViewById(R.id.start);
        decorateButton(start);
        start.setOnClickListener(this);
        //select
        Button select = (Button) findViewById(R.id.check);
        decorateButton(select);
        select.setOnClickListener(this);
    }

    void decorateButton(Button b) {
        b.setTextSize(13);
        b.setBackgroundResource(R.drawable.red_button);
        b.setTextColor(Color.WHITE);
        b.setTypeface(Typeface.createFromAsset(getAssets(), "almendra.ttf"));
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

    private void handleSignIn(int resultCode, Intent intent) {
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
            if (message != null && !message.isEmpty()) {
                showToast(message);
            }

            onDisconnected();
        }
    }

    private void handleLookAtMatches(int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            showToast("Look at matches bad result");
            return;
        }

        TurnBasedMatch match = intent
                .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

        if (match != null) {
            multiPlayer.curMatch = match;
            int playerCount = match.getParticipantIds().size();
            startGame(playerCount);

        }
    }

    private void handleSelectPlayers(int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            showToast("Invite bad result");
            return;
        }

        ArrayList<String> invitees = intent
                .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
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
                        multiPlayer.curMatch = turnBasedMatch;
                        int playerCount = turnBasedMatch.getParticipantIds().size();
                        Log.d(TAG, "onActivityResult() - success createMatch");
                        startGame(playerCount);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            handleSignIn(resultCode, intent);
        } else if (requestCode == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog
            handleLookAtMatches(resultCode, intent);
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // Returning from 'Select players to Invite' dialog
            handleSelectPlayers(resultCode, intent);
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
                                multiPlayer.playerId = player.getPlayerId();
                            }
                        }
                ).addOnFailureListener(createFailureListener("There was a problem getting the player!"));
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
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");
        multiPlayer.multiplayerClient = null;
        multiPlayer.invitationsClient = null;
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
        int status;

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
    }
}
