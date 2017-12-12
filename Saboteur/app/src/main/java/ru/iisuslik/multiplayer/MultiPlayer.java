package ru.iisuslik.multiplayer;

import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.TurnBasedMultiplayerClient;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchUpdateCallback;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import ru.iisuslik.controller.Controller;
import ru.iisuslik.field.Field;
import ru.iisuslik.gameData.Shuffle;
import ru.tilacyn.saboteur.SaboteurApplication;

/**
 * Created by iisus on 03.12.2017.
 */

public class MultiPlayer {
    public String playerId;
    public GoogleSignInClient signInClient;
    public TurnBasedMultiplayerClient multiplayerClient;
    public InvitationsClient invitationsClient;
    public TurnBasedMatch curMatch;
    public  boolean isDoingTurn;
    public Controller controller;

    public MultiPlayer() {
        controller = SaboteurApplication.getInstance().getController();
    }


    public int getMyNumber() {
        ArrayList<String> ids = curMatch.getParticipantIds();
        Collections.sort(ids);
        for(int i = 0; i < ids.size(); i++) {
            if(ids.get(i).equals(playerId)) {
                return i;
            }
        }
        return -1;
    }

    public void sendData(byte [] data) {

        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        multiplayerClient.takeTurn(curMatch.getMatchId(),
                data, nextParticipantId);
    }

    public String getNextParticipantId() {

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
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }


    public void updateMatch(TurnBasedMatch match) {
        curMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                //showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
               // showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                //showWarning("Waiting for auto-match...",
                        //"We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                   /* showWarning("Complete!",
                            "This game is over; someone finished it, and so did you!  " +
                                    "There is nothing to be done.");
                    */break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                /*showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");*/
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:

                controller.applyData(curMatch.getData());
                //setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                controller.applyData(curMatch.getData());
                //showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                //showWarning("Good inititative!",
                       // "Still waiting for invitations.\n\nBe patient!");
        }

        //mTurnData = null;

        //setViewVisibility();
    }

    public void onInitiateMatch(TurnBasedMatch match) {

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {

        curMatch = match;
        int playerCount = match.getParticipantIds().size();
        controller.gameData.shuffle = new Shuffle(playerCount, Controller.DECK_SIZE,
                Field.getSaboteurCount(playerCount));
        controller.sendData(controller.gameData);
    }


}
