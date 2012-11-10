package de.knewcleus.fgfs.multiplayer;

public interface IChatListener {

    public void newChatMessageReceived(String callSign, String frequency, String message);
    
}
