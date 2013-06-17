package com.hex.android.net;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
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
	public LinkedBlockingQueue<Game> gameQueue;
	boolean host;
	public String myID =null;
	
	public GameManager(GameHelper mHelper, Activity main) {
		this.mHelper = mHelper;
		this.hexRoomUpdateListener = new HexRoomUpdateListener(main,this);
		this.hexRoomStatusUpdateListener= new HexRoomStatusUpdateListener(this);
		this.hexRealTimeMessageReceivedListener = new HexRealTimeMessageReceivedListener(this);
		this.gameQueue = new LinkedBlockingQueue<Game>();
		this.myID = mHelper.getGamesClient().getCurrentPlayerId();
		System.out.println("my player id is "+myID);
		
	}
	

	public void setPlayer(Room room){
		this.myID = room.getParticipantId(
				mHelper.getGamesClient().getCurrentPlayerId());
		System.out.println(connectedPlayers.toString());
		System.out.println(connectedPlayers.get(0));
		if (connectedPlayers.get(0).equals(this.myID)){
	        System.out.println("I am hosting");
	        this.host = true;
		}
		else{
			System.out.println("I am not");
			this.host = false;
		}
	}

	@Override
	public void sendMessage(String msg) {
		byte[] messageData = msg.getBytes();
		for (String playerId : connectedPlayers){
			System.out.println(playerId+" ");
			
		}
		System.out.print("\n");
		for (String playerId : connectedPlayers){
			if (!myID.equals(playerId)){
				System.out.println(playerId+" ");
		mHelper.getGamesClient().sendReliableRealTimeMessage(
				null, messageData, mRoomId, playerId);
		System.out.println("sending room "+mRoomId);
		System.out.println("sent msg "+msg);
			}
		}
		
	}

	@Override
	public void run() {
		
		
	}

	@Override
	public void kill() {
		
		
	}

	public void makeGame() {
		
		if (this.host) {
			this.client = new Host("host",this);
		} 
		else {
			this.client = new AndroidClient("client",this);
		}
		System.out.println("trying to get game "+host);
		String gameState = syncGame(client);
		Game g = Game.load(gameState);
		System.out.println("the game is made "+g);
		gameQueue.clear();
		gameQueue.add(g);
		
	}
   public Game getGame()
   {
	   System.out.print("feching");
	   try {
		return gameQueue.take();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new RuntimeException("network error");
	}
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
		System.out.println("the message is "+ message);
		client.messageDispach(message);
		System.out.println(message);
		
	}

}

