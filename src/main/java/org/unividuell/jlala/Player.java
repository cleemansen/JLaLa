package org.unividuell.jlala;


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
    void setTrackPositionAbsolute(float milliseconds);
    
    /**
     * return track position
     * @return track position in millis
     */
    float getTrackPosition();
    
    /**
     * return track position
     * @return track position in percent of the track.
     */
    float getTrackPositionPercentage();
    
    /**
     * loads a file.
     * @param appendToPlaylist append to a playlist or start playing instantly?
     */
    void loadFile(boolean appendToPlaylist);
    
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
