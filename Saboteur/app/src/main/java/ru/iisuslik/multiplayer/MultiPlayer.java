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

import java.io.Serializable;
import java.util.ArrayList;

import ru.iisuslik.controller.Controller;
import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.GameData;
import ru.iisuslik.gameData.Shuffle;


public class MultiPlayer implements Serializable {
    private static final String TAG = "MMMMMMMMM";
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

    public void sendData(byte[] data) {

        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        if (sendingData) {
            Log.d(TAG, "sendData() - data is sending now");
            return;
        }
        sendingData = true;
        Log.d(TAG, "sendData() in my turn? " + isMyTurn() + "shuffle is null? " + (controller.gameData.shuffle == null));
        Log.d(TAG, "sendData() match status " + curMatch.getStatus());
        multiplayerClient.takeTurn(curMatch.getMatchId(),
                data, nextParticipantId).addOnCompleteListener(new OnCompleteListener<TurnBasedMatch>() {
            @Override
            public void onComplete(@NonNull Task<TurnBasedMatch> task) {
                if (task.isSuccessful()) {
                    curMatch = task.getResult();
                    updateMatch(curMatch);
                    sendingData = false;
                    Log.d(TAG, "sendData() izi, shuffle is null? " + (controller.gameData.shuffle == null));
                } else {
                    sendingData = false;
                    if (task.getException() != null)
                        Log.d(TAG, "sendData() problem " + task.getException().getMessage());
                }
            }
        });
    }

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
    public void fastUpdate() {
        updateMatch(curMatch);
    }
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


    void updateMatch(TurnBasedMatch match) {
        curMatch = match;
        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    break;
                }
        }

        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                if (!sendingData)
                    controller.applyData(curMatch.getData());
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                if (!sendingData)
                    controller.applyData(curMatch.getData());
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
        }
    }

    public boolean isMyTurn() {
        int turnStatus = curMatch.getTurnStatus();
        return turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN;
    }

    public void onInitiateMatch(TurnBasedMatch match) {
        Log.d(TAG, "onInitiateMatch() match == null? " + (match == null));
        if (!sendingData && match.getData() != null) {
            updateMatch(match);
        } else
            startMatch(match);
    }

    private void startMatch(TurnBasedMatch match) {
        curMatch = match;
        int playerCount = match.getParticipantIds().size();
        if (controller.gameData == null)
            controller.gameData = new GameData();
        controller.gameData.shuffle = new Shuffle(playerCount, Controller.DECK_SIZE,
                Field.getSaboteurCount(playerCount));
        controller.initializeField(controller.gameData.shuffle);
        controller.sendData(controller.gameData);
        Log.d(TAG, "startMatch()");
    }

    public void finish() {
        multiplayerClient.finishMatch(curMatch.getMatchId())
                .addOnSuccessListener(new OnSuccessListener<TurnBasedMatch>() {
                    @Override
                    public void onSuccess(TurnBasedMatch turnBasedMatch) {
                        updateMatch(turnBasedMatch);
                    }
                });
    }


}
