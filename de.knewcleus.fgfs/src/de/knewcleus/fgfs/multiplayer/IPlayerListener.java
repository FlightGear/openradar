package de.knewcleus.fgfs.multiplayer;

public interface IPlayerListener<T extends Player> {

    public void playerAdded(T player);
    
    public void playerRemoved(T player);

    public void playerListEmptied(T player);
    
}
