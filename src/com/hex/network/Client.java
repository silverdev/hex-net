package com.hex.network;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.hex.core.Move;

public class Client extends Thread {
    private String name;
    protected NetCommunication talk;
    protected int id = 1;

    public Gson gson = new Gson();

    private final LinkedBlockingQueue<Move> moves = new LinkedBlockingQueue<Move>();
    public NetworkCallbacks callbacks;

    public Client(NetCommunication talk) {
        System.out.println("Creating New TestClient: ");
        this.talk = talk;
    }

    public void messageDispach(String message) {
        ServerResponse sr = gson.fromJson(message, ServerResponse.class);
        switch(sr.action) {
        case MOVE:
            moves.add(sr.move);
            break;
        case APROVE:
            break;
        case NEW_GAME:
            this.callbacks.newGame(sr.data);
            break;
        case OUT_OF_SYNC_ERROR:
            this.callbacks.error();
            break;
        case UNDO:
            break;
        case REQUEST_NEW_GAME:
            String gameData = this.callbacks.newGameReqest();
            if(gameData != null) {
                this.sendNewGame(gameData);
                this.callbacks.newGame(gameData);
            }
            break;
        case REQUEST_UNDO:
            this.callbacks.undo(sr.number);
            break;
        case SENDCHAT:
            this.callbacks.chat(sr.data);
            break;
        case STARTING:
            this.moves.clear();
            break;
        default:
            break;
        }
    }

    private void sendNewGame(String gameData) {
        ServerResponse sr = new ServerResponse(name, this.id, null, Action.NEW_GAME, gameData);
        String json = gson.toJson(sr);
        this.talk.sendMessage(json);

    }

    public void kill() {
        talk.kill();

    }

    public boolean giveUp() {
        // TODO Auto-generated method stub
        return false;
    }

    public Move getPlayerTurn() {
        try {
            return this.moves.take();

        }
        catch(InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to get move");
        }

    }

    public void sendMove(Move move) {
        ServerResponse sr = new ServerResponse(name, this.id, move, Action.MOVE, null);
        String json = gson.toJson(sr);
        this.talk.sendMessage(json);

    }

    /**
     * @return the game
     */

    public int getTeam() {
        return this.id;
    }

    public void requestNewGame() {
        ServerResponse sr = new ServerResponse(name, this.id, null, Action.REQUEST_NEW_GAME, null);
        String json = gson.toJson(sr);

        this.talk.sendMessage(json);

    }

    public void simulateMove(Move move) {
        this.moves.add(move);

    }

    public void starting() {
        ServerResponse sr = new ServerResponse(name, this.id, null, Action.STARTING, null);
        String json = gson.toJson(sr);
        talk.sendMessage(json);

    }

}
