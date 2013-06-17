package com.hex.android.net;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.hex.core.Move;
import com.hex.network.Action;
import com.hex.network.Client;
import com.hex.network.NetCommunication;
import com.hex.network.ServerResponse;

public class AndroidClient implements Client {

    private String name;
    protected NetCommunication talk;
    protected int id = 1;
    public Gson gson = new Gson();
    private String otherNane = "Not connected!";
    private String game = null;
    private final LinkedBlockingQueue<Move> moves = new LinkedBlockingQueue<Move>();

    public AndroidClient() {

        name = n;
        this.talk = gameManager;

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
            setGame(sr.data);
            break;
        case OUT_OF_SYNC_ERROR:
            break;
        case UNDO:
            break;

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hex.pc.network.Client#getPlayerName()
     */
    @Override
    public String getPlayerName() {
        return this.otherNane;
    }

    @Override
    public void kill() {
        talk.kill();

    }

    @Override
    public boolean giveUp() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Move getPlayerTurn() {
        try {
            return this.moves.take();

        }
        catch(InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to get move");
        }

    }

    @Override
    public void sendMove(Move move) {
        ServerResponse sr = new ServerResponse(name, this.id, move, Action.MOVE, null);
        String json = gson.toJson(sr);
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

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

}
