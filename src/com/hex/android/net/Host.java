package com.hex.android.net;

import com.hex.core.Game;
import com.hex.network.Action;
import com.hex.network.ServerResponse;

public class Host extends AndroidClient {

    public Host(String name,GameManager gm) {
        super(name,gm);
        super.id = 2;
        String gameData = "{\"gameOptions\":{\"timer\":{\"startTime\":1371174295237,\"elapsedTime\":0,\"type\":0,\"totalTime\":0,\"additionalTime\":0},\"gridSize\":5,\"swap\":false},\"moveList\":{\"moveList\":[]},\"moveNumber\":1,\"currentPlayer\":1,\"gameStart\":1371174295497,\"gameEnd\":0,\"player1\":{\"type\":\"Human\",\"color\":-16776961,\"name\":\"Player1\"},\"player2\":{\"type\":\"Human\",\"color\":-256,\"name\":\"Player2\"}}";
        super.setGame(gameData);
        sendGame(gameData, name);

    }

    private void sendGame(String savedGame, String name) {

        ServerResponse sr = new ServerResponse(name, id, null, Action.NEW_GAME, savedGame);
        System.out.println("the game is " + savedGame);
        String json = super.gson.toJson(sr);
        super.talk.sendMessage(json);

    }

    

}
