package com.hex.network;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.hex.core.Move;

public class Client extends Thread {

    private String name;
    protected NetCommunication talk;
    protected int id = 1;
    private GameLogger logger;
    public Gson gson = new Gson();
    private String otherNane = "Not connected!";
    private String game = null;
    private final LinkedBlockingQueue<Move> moves = new LinkedBlockingQueue<Move>();

    public Client(NetCommunication talk) {
        System.out.println("Creating New TestClient: ");
        this.talk = talk;
        logger = new GameLogger(name);
        this.name = "net Player";

    }

    public void messageDispach(String message) {
        logger.log(message);
        ServerResponse sr = gson.fromJson(message, ServerResponse.class);
        switch(sr.action) {
        case MOVE:
            moves.add(sr.move);
            break;
        case APROVE:
            break;
        case NEW_GAME:
            setGame(sr.data);
            break;
        case OUT_OF_SYNC_ERROR:
            break;
        case UNDO:
            break;

        }

    }

    public String getPlayerName() {
        return this.otherNane;
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
        logger.log(json);
        this.talk.sendMessage(json);

    }

    /**
     * @return the game
     */
    public String getGame() {

        return game;

    }

    public int getTeam() {
        return this.id;
    }

    /**
     * @param game
     *            the game to set
     */
    public void setGame(String gameData) {
        synchronized(this) {
            this.game = gameData;
            this.notifyAll();
        }
    }

}
