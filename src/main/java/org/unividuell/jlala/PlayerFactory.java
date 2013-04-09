package org.unividuell.jlala;

import org.unividuell.jlala.os.mplayer.*;



public class PlayerFactory {
    
    private static PlayerFactory instance;
    
    private final Player mPlayer = new MPlayer(true, null, "-slave -idle -quiet");
    
    private PlayerFactory() {
        // do nothing
    }
    
    public static PlayerFactory getInstance() {
        if (instance == null) {
            instance = new PlayerFactory();
        }
        return instance;
    }
    
    public Player getMPlayer() {
        return mPlayer;
    }

}
