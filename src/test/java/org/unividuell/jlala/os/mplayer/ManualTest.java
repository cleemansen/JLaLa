package org.unividuell.jlala.os.mplayer;

import java.io.*;
import java.net.*;

import org.junit.*;

public class ManualTest {
    
    private MPlayer sut;
    
    @Before
    public void setup() throws Throwable {
        // Mac OS X Snow Leoprad Env
        // sudo port install mplayer-devel
        
        // Debian wheezy @Raspberry PI
        // Volume: mplayer -mixer-channel "Speaker Front",1 test.mp3 
        sut = new MPlayer(true, "/opt/local/bin/mplayer", "-slave -idle -quiet");
        
        String userName = System.getProperty("user.name");
        java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        System.out.println("userName: " + userName);
        System.out.println("Hostname of local machine: " + localMachine.getHostName());
    }
    
    @After
    public void after() {
        sut.close();
    }
    
    @Test
    public void testName() throws Exception {
        // prepare
        
        // execute

        // verify 
    }
    
    @Test
    public void play() throws Exception {
        // prepare
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();
        

        // execute
        sut.loadFile(path, false);
        // seek to 1 min
        sut.setTrackPositionAbsolute(60000);
        
        Thread.sleep(2000);
        
        long start = sut.getVolume();
        for (; start > 5; start--) {
            sut.setVolume(start);
        }
        Thread.sleep(3000);
        for (; start < 100; start++) {
            sut.setVolume(start);
            Thread.sleep(200);
        }
        
        Thread.sleep(10000);
        
        // verify 
    }

}
