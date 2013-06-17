package com.hex.android.net;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

public class HexRealTimeMessageReceivedListener implements RealTimeMessageReceivedListener {

    private GameManager gameManager;

	public HexRealTimeMessageReceivedListener(GameManager gameManager) {
    	this.gameManager =gameManager; 
	}

	@Override
    public void onRealTimeMessageReceived(RealTimeMessage msg) { 
		System.out.println("message Receved " +msg.isReliable());
		if (msg.isReliable()){
        this.gameManager.receiveMessage(new String(msg.getMessageData()));
		}

    }

}
