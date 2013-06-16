package com.hex.android.net;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

public class HexRoomUpdateListener implements RoomUpdateListener {
    private static final int RC_WAITING_ROOM = 1025;
    private Activity mMainActivity;
    private String mRoomId;
    private GameManager gameManager;

    public HexRoomUpdateListener(Activity main, GameManager gameManager) {
        mMainActivity = main;
        this.gameManager = gameManager;
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if(statusCode != GamesClient.STATUS_OK) {
            // display error
            return;
        }
        // If I joined then I am not the host
        this.gameManager.host = false;

        // tell the game gamager the room id
        mRoomId = room.getRoomId();
        this.gameManager.mRoomId = mRoomId;
        // get waiting room intent
        System.out.println("joining");
        Intent i = gameManager.mHelper.getGamesClient().getRealTimeWaitingRoomIntent(room, 1);
        mMainActivity.startActivityForResult(i, RC_WAITING_ROOM);
        System.out.println("room is gone");
    }

    @Override
    public void onLeftRoom(int arg0, String arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRoomConnected(int status, Room room) {
        // set a list of all the players
        gameManager.connectedPlayers = room.getParticipantIds();
        gameManager.makeGame();

    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if(statusCode != GamesClient.STATUS_OK) {
            // display error
            return;
        }
        // If i made the room I am host
        System.out.print("I am hosting");
        this.gameManager.host = true;

        mRoomId = room.getRoomId();
        this.gameManager.mRoomId = mRoomId;
        // get waiting room intent
        Intent i = gameManager.mHelper.getGamesClient().getRealTimeWaitingRoomIntent(room, Integer.MAX_VALUE);
        mMainActivity.startActivityForResult(i, RC_WAITING_ROOM);
        System.out.print("game is started!");

    }

}
