package ru.iisuslik.multiplayer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

import ru.iisuslik.controller.Controller;
import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.GameData;
import ru.iisuslik.gameData.Shuffle;


public class MultiPlayer implements Serializable {
    private static final String TAG = "MP";
    private boolean sendingData = false;
    private boolean receivingData = false;
    String playerId;
    GoogleSignInClient signInClient;
    TurnBasedMultiplayerClient multiplayerClient;
    InvitationsClient invitationsClient;
    public TurnBasedMatch curMatch;
    public Controller controller;

    public MultiPlayer(Controller controller) {
        this.controller = controller;
    }


    /**
     * Get your number in id list
     * @return your number
     */
    public int getMyNumber() {
        String myParticipantId = curMatch.getParticipantId(playerId);
        ArrayList<String> ids = curMatch.getParticipantIds();
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals(myParticipantId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Takes turn in match and updates actual data, next turn has another player
     *
     * @param data byte array to send
     */
    public void sendData(byte[] data) {
        String nextParticipantId = getNextParticipantId();
        sendData(data, nextParticipantId);
    }

    public void sendFirstData(byte[] data) {
        String nextParticipantId = curMatch.getParticipantId(playerId);
        sendData(data, nextParticipantId);
    }

    private void sendData(byte[] data, String nextParticipantId) {
        // Create the next turn
        if (sendingData) {
            Log.d(TAG, "sendData() - data is sending now");
            return;
        }
        sendingData = true;
        Log.d(TAG, "sendData() begins, match status " + curMatch.getStatus());
        multiplayerClient.takeTurn(curMatch.getMatchId(),
                data, nextParticipantId).addOnCompleteListener(new OnCompleteListener<TurnBasedMatch>() {
            @Override
            public void onComplete(@NonNull Task<TurnBasedMatch> task) {
                if (task.isSuccessful()) {
                    curMatch = task.getResult();
                    controller.applyData(curMatch.getData());
                    sendingData = false;
                    Log.d(TAG, "sendData() successful");
                } else {
                    sendingData = false;
                    if (task.getException() != null)
                        Log.d(TAG, "sendData() problems");
                }
            }
        });
    }

    /**
     * Get id of next player, who will take a turn after you
     *
     * @return next player id
     */
    private String getNextParticipantId() {

        String myParticipantId = curMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = curMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (curMatch.getAvailableAutoMatchSlots() <= 0) {
            return participantIds.get(0);
        } else {
            return null;
        }
    }

    /**
     * Updates match without loading newest version
     */
    public void fastUpdate() {
        controller.applyData(curMatch.getData());
    }

    /**
     * Loads newest match version with curMatch id and receives data from it
     */
    public void update() {
        if (receivingData) {
            Log.d(TAG, "update() - we are receiving data already");
            return;
        }
        receivingData = true;
        multiplayerClient.loadMatch(curMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<AnnotatedData<TurnBasedMatch>>() {
                    @Override
                    public void onSuccess(AnnotatedData<TurnBasedMatch> turnBasedMatchAnnotatedData) {
                        receivingData = false;
                        curMatch = turnBasedMatchAnnotatedData.get();
                        updateMatch(curMatch);
                    }
                });
    }


    public boolean isMyTurn() {
        int turnStatus = curMatch.getTurnStatus();
        return turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
    }

    /**
     * Update game data before starting game activity
     */
    public void onInitiateMatch(@NotNull TurnBasedMatch match) {
        Log.d(TAG, "onInitiateMatch()");
        if (!sendingData && match.getData() != null) {
            // Match has not null data, so it isn't 1 turn
            controller.applyData(curMatch.getData());
        } else {
            // This is the first turn in game, initialize game data
            startMatch(match);
        }
    }

    /**
     * Initialize shuffle in game data and send it to everyone
     */
    private void startMatch(TurnBasedMatch match) {
        curMatch = match;
        int playerCount = match.getParticipantIds().size();
        if (controller.gameData == null)
            controller.gameData = new GameData();
        controller.gameData.shuffle = new Shuffle(playerCount, Controller.DECK_SIZE,
                Field.getSaboteurCount(playerCount));
        controller.initializeField(controller.gameData.shuffle);
        controller.sendFirstData(controller.gameData);
        Log.d(TAG, "startMatch()");
    }

    public void updateMatch(TurnBasedMatch match) {
        controller.applyData(match.getData());
    }

    /**
     * Finish match
     */
    public void finish() {
        multiplayerClient.finishMatch(curMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        updateMatch(turnBasedMatch);
                    }
                });
        Log.d(TAG, "finishMatch()");
    }


}
