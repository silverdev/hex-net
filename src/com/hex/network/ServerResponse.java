package com.hex.network;

import java.util.List;

import com.hex.core.Move;

public class ServerResponse {

    public int responseCode;
    public String playerToken;
    public List<String> error;
    public String playerName;
    public int playerID;
    public int resources;
    public Move move;
    public Action action;
    public String data;
    public int number;

    public ServerResponse(String playerName, int playerID, Move move, Action action, String data, int number) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.move = move;
        this.action = action;
        this.data = data;
        this.number = number;
    }

    public ServerResponse(String playerName, int PlayerID, Move move, Action action, String data) {
        this(playerName, PlayerID, move, action, data, 0);
    }
}
