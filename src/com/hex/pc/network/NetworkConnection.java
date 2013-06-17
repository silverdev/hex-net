package com.hex.pc.network;

import com.hex.android.net.Host;
import com.hex.core.Game;
import com.hex.core.PlayerObject;
import com.hex.network.NetworkCallbacks;
import com.hex.network.NetworkPlayer;

public class NetworkConnection {

    public static Game netGame(Game game, boolean host, String IP) {

        if(host) {
            return hostGame(game);
        }
        else return connectToGame(IP);
    }

    private static Game connectToGame(String IP) {

        Talker com = new Talker(IP);
        NetworkPlayer netPlayer = new NetworkPlayer(1, com);

        Game netGame;
        String gameData;
        synchronized(com) {
            while(com.getGame() == null) {
                try {
                    com.wait();
                }
                catch(InterruptedException e) {

                    e.printStackTrace();
                }
            }
            gameData = com.getGame();
        }
        netGame = Game.load(gameData, netPlayer, new PlayerObject(2));

        return netGame;
    }

    private static Game hostGame(Game game) {
        Server s =
        Host host = new Host(,game);
        NetworkPlayer netPlayer = new NetworkPlayer(host, new NetworkCallbacks());
        String gameData = host.getGame();

        Game netGame = Game.load(gameData, new PlayerObject(1), netPlayer);
        return netGame;
    }
}
