package org.unividuell.jlala;

import java.io.*;


/**
 * A Player. Supports basic operation of a common player.
 */
public interface Player {
    
    /**
     * Stop playback.
     */
    void stop();
    
    /**
     * Start playback.
     */
    void play();
    
    /**
     * Toggle the pause state.
     */
    void togglePause();
    
    /**
     * pause as soon as possible
     */
    void pause();
    
    /**
     * Set the volume.
     * @param vol volume between [0, 100]
     */
    void setVolume(float vol);

    /**
     * Returns the volume
     * @return current volume [0, 100]
     */
    long getVolume();
    
    /**
     * Increase/decrease volume.
     * @param change
     */
    void changeVolumeRelative(float change);
    
    /**
     * seek to position
     * @param percentage position in track between [0, 100]
     */
    void setTrackPositionPercentage(float percentage);
    
    /**
     * seek to position
     * @param percentage position in track in milliseconds
     */
    void setTrackPositionAbsolute(long milliseconds);
    
    /**
     * return track position
     * @return track position in millis
     */
    long getTrackPosition();
    
    /**
     * return track position
     * @return track position in percent of the track.
     */
    float getTrackPositionPercentage();
    
    /**
     * loads a file.
     * @param file 
     * @param appendToPlaylist append to a playlist or start playing instantly?
     * @throws IOException 
     */
    void loadFile(String uri, boolean appendToPlaylist) throws IOException;
    
    /**
     * toggle mute state.
     */
    void muteToggle();
    
    /**
     * mute on.
     */
    void muteOn();
    
    /**
     * mute off.
     */
    void muteOff();

}
