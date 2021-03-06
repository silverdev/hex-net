package com.hex.network;

import java.io.Serializable;

import com.hex.core.Game;
import com.hex.core.Move;
import com.hex.core.MoveList;
import com.hex.core.Player;
import com.hex.core.PlayingEntity;
import com.hex.core.TurnMismatchException;

public class NetworkPlayer implements PlayingEntity {
    private static final long serialVersionUID = 1L;

    private static final Move EMPTY_MOVE = new Move(-1, -1, (byte) -1, -1, -1);

    private int color;
    private long timeLeft;
    private final int team;
    private boolean skipMove = false;
    private String name;
    private boolean forfeit;

    private Client tc;

    public NetworkPlayer(int team, NetCommunication nc) {
        this.tc = new Client(nc);
        // this.tc.start();
        this.team = team;
        System.out.println("the team is ____" + team);
    }

    public void setCallbacks(NetworkCallbacks nc) {
        this.tc.callbacks = nc;
    }

    public void receivedMessage(String msg) {
        this.tc.messageDispach(msg);
    }

    @Override
    public void newgameCalled() {
        endMove();
    }

    @Override
    public boolean supportsUndo(Game game) {
        int turnNumber = game.getMoveNumber() - 1;

        if(game.getCurrentPlayer().getType().equals(Player.Human)) {
            if(game.getWaitingPlayer().getType().equals(Player.Net)) {
                if(game.getMoveNumber() > 1) {
                    turnNumber--;
                }
            }
        }

        tc.requestUndo(turnNumber);
        return false;
    }

    @Override
    public boolean supportsNewgame() {
        tc.requestNewGame();
        return false;
    }

    @Override
    public void quit() {
        endMove();
        // tc.kill();
    }

    @Override
    public boolean supportsSave() {
        return false;
    }

    @Override
    public void endMove() {
        this.skipMove = true;
        this.tc.simulateMove(EMPTY_MOVE);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setTime(long time) {
        this.timeLeft = time;
    }

    @Override
    public long getTime() {
        return timeLeft;
    }

    @Override
    public boolean giveUp() {
        return forfeit;
    }

    @Override
    // remote player is always player two
    public byte getTeam() {
        return (byte) this.team;
    }

    @Override
    public Player getType() {
        return Player.Net;
    }

    @Override
    public void getPlayerTurn(Game game) {
        // if this is not the first move send play the last move
        MoveList moveList = game.getMoveList();
        if(moveList.size() > 0) {
            tc.sendMove(moveList.getMove());
        }
        // get the other players Move
        // if the move is empty skip it;
        Move move = null;
        do {
            move = tc.getPlayerTurn();
            if(this.skipMove) {
                this.skipMove = false;
                return;
            }
            // Ignore repeat move
            if(move.getMoveNumber() < game.getMoveNumber()) {
                move = EMPTY_MOVE;
            }
        }
        while(move == EMPTY_MOVE);

        // make sure that this is the move I am listening for
        if(move.getMoveNumber() != game.getMoveNumber()) {
            throw new TurnMismatchException("NetGame error: \n the move nuber is: " + move.getMoveNumber() + " but the game move nuber: "
                    + game.getMoveNumber());
        }
        game.getMoveList().makeMove(move);
        game.gamePieces[move.getX()][move.getY()].setTeam((byte) this.team, game);
    }

    @Override
    public Serializable getSaveState() {
        return null;
    }

    @Override
    public void setSaveState(Serializable state) {

    }

    @Override
    public void lose(Game game) {
        System.out.println("i win");
        tc.sendMove(game.getMoveList().getMove());
        System.out.println("I let the looser know");

    }

    @Override
    public void win() {
        // TODO Auto-generated method stub

    }

    @Override
    public void undoCalled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void startGame() {
        this.skipMove = false;
        tc.starting();

    }

    public void exit() {
        this.tc.kill();
    }

    public void forfeit() {
        this.forfeit = true;
    }
}
