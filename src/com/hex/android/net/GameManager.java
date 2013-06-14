package com.hex.android.net;

import java.util.ArrayList;

import android.app.Activity;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.gson.Gson;
import com.hex.core.Game;
import com.hex.network.NetCommunication;



public class GameManager implements NetCommunication {
	GameHelper mHelper;
	private HexRealTimeMessageReceivedListener hexRealTimeMessageReceivedListener;
    private HexRoomStatusUpdateListener hexRoomStatusUpdateListener;
    private HexRoomUpdateListener hexRoomUpdateListener;
    private AndroidClient client = null;
    private Gson gson = new Gson();
	public String mRoomId;
	public ArrayList<String> connectedPlayers;
	public boolean host = false;
	
	public GameManager(GameHelper mHelper, Activity main) {
		this.mHelper = mHelper;
		this.hexRoomUpdateListener = new HexRoomUpdateListener(main,this);
		this.hexRoomStatusUpdateListener= new HexRoomStatusUpdateListener(this);
		this.hexRealTimeMessageReceivedListener = new HexRealTimeMessageReceivedListener(this);

		
	}
	


	@Override
	public void sendMessage(String msg) {
		byte[] messageData = msg.getBytes();
		for (String playerId : connectedPlayers){
		mHelper.getGamesClient().sendReliableRealTimeMessage(
				null, messageData, mRoomId, playerId);
		System.out.print("sent msg "+msg);
		}
		
	}

	@Override
	public void run() {
		
		
	}

	@Override
	public void kill() {
		
		
	}

	public Game getGame() {
		System.out.println("the game is called");
		if (this.host) {
			this.client = new Host("host",this);
		} 
		else {
			this.client = new AndroidClient("client",this);
		}
		String gameState = syncGame(client);
		System.out.print(gameState);
		Game g = gson.fromJson(gameState, Game.class);
		System.out.println("the game is made "+g);
		return g;
	}

	private String syncGame(AndroidClient com) { //might ifloop
		String g;
		synchronized(com) {
		    while(com.getGame() == null) {
		    	System.out.println("waiting " +com.getGame());
		        try {
		            com.wait();
		        }
		        catch(InterruptedException e) {

		            e.printStackTrace();
		        }
		    }
		    g = com.getGame();
		}
		return g;
		
	}



	public RoomUpdateListener getHexRoomUpdateListener() {
		
		return this.hexRoomUpdateListener;
	}

	public RoomStatusUpdateListener getHexRoomStatusUpdateListener() {
		
		return this.hexRoomStatusUpdateListener;
	}

	public RealTimeMessageReceivedListener getHexRealTimeMessageReceivedListener() {
		
		return this.hexRealTimeMessageReceivedListener;
	}

	public void receiveMessage(String message) {
		client.messageDispach(message);
		System.out.println(message);
		
	}

}

