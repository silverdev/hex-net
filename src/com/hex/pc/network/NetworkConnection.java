package com.hex.pc.network;

import com.hex.core.Game;
import com.hex.core.PlayerObject;
import com.hex.network.Client;
import com.hex.network.NetworkPlayer;

public class NetworkConnection {

    public static Game netGame(Game game,boolean host,String IP) {
      
        if(host) {
            return hostGame(game);
        }
        else return connectToGame(IP);
    }

    private static Game connectToGame(String IP) {
       
        Client com = new PcClient("guest", IP);
        NetworkPlayer netPlayer = new NetworkPlayer(com, new NetworkCallbacks());
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
        Host host = new Host("host", game);
        NetworkPlayer netPlayer = new NetworkPlayer(host, new NetworkCallbacks());
        String gameData = host.getGame();

        Game netGame = Game.load(gameData, new PlayerObject(1), netPlayer);
        return netGame;
    }

}
