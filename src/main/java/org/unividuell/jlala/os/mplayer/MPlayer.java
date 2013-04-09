package org.unividuell.jlala.os.mplayer;

import java.io.*;

import org.slf4j.*;
import org.unividuell.jlala.*;
import org.unividuell.jlala.os.*;

public class MPlayer implements Player {
    
    private static final String SET_PROP = "pausing_keep set_property ";

    private static final String GET_PROP = "pausing_keep get_property ";

    final Logger logger = LoggerFactory.getLogger(MPlayer.class);
    
    /** The path to the MPlayer executable. */
    private String mplayerPath = "/usr/bin/mplayer";
    /** Options passed to MPlayer. */
    private String mplayerOptions = "-slave -idle";

    /** The process corresponding to MPlayer. */
    private Process mplayerProcess;
    /** The standard input for MPlayer where you can send commands. */
    private PrintStream mplayerIn;
    /**
     * A combined reader for the the standard output and error of MPlayer. Used
     * to read MPlayer responses.
     */
    private BufferedReader mplayerOutErr;
    
    /** signals weather a file is loaded in the mplayer process. */
    private boolean isFileLoaded = false;
    
    /**
     * Does nothing. You have to initialize everything by your own!
     */
    public MPlayer() {
        logger.warn("YOU ARE DOING BAD THINGS!!");
    }

    public MPlayer(boolean startProcess, String mPlayerPath) {
        if (mPlayerPath != null) {
            setMPlayerPath(mPlayerPath);
        }
        if (startProcess) {
            try {
                initProcess();
            } catch (IOException e) {
                String msg = "couldn't start mplayer process.";
                logger.error(msg, e);
                throw new IllegalStateException(msg, e);
            }
        }
    }
    
    public MPlayer(boolean startProcess, String mPlayerPath, String mPlayerOptions) {
        if (mPlayerPath != null) {
            setMPlayerPath(mPlayerPath);
        }
        if (mPlayerOptions != null) {
            setMplayerOptions(mPlayerOptions);
        }
        if (startProcess) {
            try {
                initProcess();
            } catch (IOException e) {
                String msg = "couldn't start mplayer process.";
                logger.error(msg, e);
                throw new IllegalStateException(msg, e);
            }
        }
    }

    protected void initProcess() throws IOException {
        if (mplayerProcess == null) {
            // start MPlayer as an external process
            String command = mplayerPath + " " + mplayerOptions;
            logger.info("Starting MPlayer process: " + command);
            mplayerProcess = Runtime.getRuntime().exec(command);
            
            // create the piped streams where to redirect the standard output
            // and error of MPlayer
            // specify a bigger pipesize
            PipedInputStream readFrom = new PipedInputStream(1024 * 1024);
            PipedOutputStream writeTo = new PipedOutputStream(readFrom);
            mplayerOutErr = new BufferedReader(new InputStreamReader(readFrom));

            // create the threads to redirect the standard output and error of
            // MPlayer
            new OSPlayerCommunication(mplayerProcess.getInputStream(), writeTo, "MPlayer says: ").start();
            new OSPlayerCommunication(mplayerProcess.getErrorStream(), writeTo, "MPlayer encountered an error: ").start();

            // the standard input of MPlayer
            mplayerIn = new PrintStream(mplayerProcess.getOutputStream());
        }
    }

    /** @return the path to the MPlayer executable. */
    protected String getMPlayerPath() {
        return mplayerPath;
    }
    
    protected void setMplayerIn(PrintStream mplayerIn) {
        this.mplayerIn = mplayerIn;
    }
    
    protected void setMplayerOutErr(BufferedReader mplayerOutErr) {
        this.mplayerOutErr = mplayerOutErr;
    }

    /**
     * Sets the path to the MPlayer executable.
     * 
     * @param mplayerPath
     *            the new MPlayer path; this will be actually efective after
     *            {@link #close() closing} the currently running player.
     */
    protected void setMPlayerPath(String mplayerPath) {
        this.mplayerPath = mplayerPath;
    }
    
    protected void setMplayerOptions(String mplayerOptions) {
        this.mplayerOptions = mplayerOptions;
    }

    protected void open(String uri) throws IOException {
//        String path = file.getAbsolutePath().replace('\\', '/');
        
        execute("loadfile " + uri + " 0");

        // wait to start playing
        waitForAnswer("Starting playback...");
        logger.info("Started playing file " + uri);
        isFileLoaded = true;
    }

    @Override
    public void close() {
        if (mplayerProcess != null) {
            execute("quit");
            try {
                mplayerProcess.waitFor();
            } catch (InterruptedException e) {
            }
            mplayerProcess = null;
            isFileLoaded = false;
        }
    }
    
    /**
     * Sends a command to MPlayer..
     * 
     * @param command
     *            the command to be sent
     */
    private void execute(String command) {
        execute(command, null);
    }

    /**
     * Sends a command to MPlayer and waits for an answer.
     * 
     * @param command
     *            the command to be sent
     * @param expected
     *            the string with which has to start the line; if null don't
     *            wait for an answer
     * @return the MPlayer answer
     */
    private String execute(String command, String expected) {
        if (mplayerProcess != null) {
            logger.info("Send to MPlayer the command '" + command + "' and expecting "
                    + (expected != null ? "'" + expected + "'" : "no answer"));
            mplayerIn.print(command);
            mplayerIn.print("\n");
            mplayerIn.flush();
            logger.info("Command sent");
            if (expected != null) {
                String response = waitForAnswer(expected);
                logger.info("MPlayer command response: " + response);
                return response;
            }
        }
        return null;
    }

    /**
     * Read from the MPlayer standard output and error a line that starts with
     * the given parameter and return it.
     * 
     * @param expected
     *            the expected starting string for the line
     * @return the entire line from the standard output or error of MPlayer
     */
    private String waitForAnswer(String expected) {
        // todo add the possibility to specify more options to be specified
        // todo use regexp matching instead of the beginning of a string
        String line = null;
        if (expected != null) {
            try {
                while ((line = mplayerOutErr.readLine()) != null) {
                    logger.info("Reading line: " + line);
                    if (line.startsWith(expected)) {
                        return line;
                    }
                }
            } catch (IOException e) {
            }
        }
        return line;
    }
    
    protected String getProperty(String name) {
        if (name == null || mplayerProcess == null) {
            return null;
        }
        String s = "ANS_" + name + "=";
        String x = execute(GET_PROP + name, s);
        if (x == null)
            return null;
        if (!x.startsWith(s))
            return null;
        return x.substring(s.length());
    }
    
    protected long getPropertyAsLong(String name) {
        try {
            return Long.parseLong(getProperty(name));
        } catch (NumberFormatException exc) {
        } catch (NullPointerException exc) {
        }
        return 0;
    }

    protected float getPropertyAsFloat(String name) {
        try {
            return Float.parseFloat(getProperty(name));
        } catch (NumberFormatException exc) {
        } catch (NullPointerException exc) {
        }
        return 0f;
    }

    protected void setProperty(String name, String value) {
        execute(SET_PROP + name + " " + value);
    }

    protected void setProperty(String name, long value) {
        execute(SET_PROP + name + " " + value);
    }

    protected void setProperty(String name, float value) {
        execute(SET_PROP + name + " " + value);
    }
    
    public void setMplayerProcess(Process mplayerProcess) {
        this.mplayerProcess = mplayerProcess;
    }
    
    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void play() {
        // TODO Auto-generated method stub

    }

    @Override
    public void togglePause() {
        execute("pause");
    }

    @Override
    public void pause() {
        execute("pause");

    }
    
    @Override
    public long getVolume() {
        long vol = 0;
        if (isFileLoaded) {
            vol = (long) getPropertyAsFloat("volume");
        }
        return vol;
    }
    
    @Override
    public void setVolume(float vol) {
        if (isFileLoaded) {
            setProperty("volume", vol);
        }
    }

    @Override
    public void changeVolumeRelative(float change) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTrackPositionPercentage(float percentage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTrackPositionAbsolute(long milliseconds) {
        if (isFileLoaded) {
            long seconds = milliseconds / 1000;
            setProperty("time_pos", seconds);
        }
    }

    @Override
    public long getTrackPosition() {
        long pos = 0;
        if (isFileLoaded) {
            pos = (long) getPropertyAsFloat("time_pos");
        }
        return pos;
    }

    @Override
    public float getTrackPositionPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void loadFile(String uri, boolean appendToPlaylist) throws IOException {
        open(uri);
    }

    @Override
    public void muteToggle() {
        // TODO Auto-generated method stub

    }

    @Override
    public void muteOn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void muteOff() {
        // TODO Auto-generated method stub

    }

}
