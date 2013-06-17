package com.hex.network;

public interface NetCommunication {

    public abstract void sendMessage(String msg);

    public abstract void kill();

}
