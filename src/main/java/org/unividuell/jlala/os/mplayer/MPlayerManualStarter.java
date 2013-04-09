package org.unividuell.jlala.os.mplayer;

import java.io.*;

import org.unividuell.jlala.*;

public class MPlayerManualStarter {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            args = new String[] { "test.ogg" };
        }
        new MPlayerManualStarter(args[0]);
    }

    public MPlayerManualStarter(String file) throws IOException {

        Player mPlayer = new MPlayer(true, null);

        mPlayer.loadAndPlay(file, false);
    }

}
