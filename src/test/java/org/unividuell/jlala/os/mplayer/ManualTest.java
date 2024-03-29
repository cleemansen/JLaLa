package org.unividuell.jlala.os.mplayer;

import static org.fest.assertions.Assertions.assertThat;

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
    public void play() throws Exception {
        // prepare
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();

        // execute
        sut.loadAndPlay(path, false);
        // seek to 1 min
        sut.setTrackPositionAbsolute(60000);
        
        Thread.sleep(2000);
        
        // verify
        assertThat(sut.getTrackPosition()).isEqualTo(60 + 2);
    }
    
    @Test
    public void stop() throws Exception {
     // prepare
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();

        sut.loadAndPlay(path, false);
        // seek to 1 min
        sut.setTrackPositionAbsolute(60000);
        
        Thread.sleep(2000);
        // execute;
        sut.stop();
        Thread.sleep(4000);
    }
    
    @Test
    public void volume() throws Exception {
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();
        // execute
        sut.loadAndPlay(path, false);
        sut.setVolume(10);
        // seek to 1 min
        sut.setTrackPositionAbsolute(60000);
        
        long start = sut.getVolume();
        Thread.sleep(3000);
        for (; start <= 100; start++) {
            sut.setVolume(start);
            Thread.sleep(100);
        }
        long actual = sut.getVolume();
        Thread.sleep(3000);
        
        // verify
        assertThat(actual).isEqualTo(100);
    }
    
    @Test
    public void position() throws Exception {
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();
        // execute
        sut.loadAndPlay(path, false);
        sut.setVolume(40);
        
        long position = sut.getTrackPosition();
        assertThat(position).isEqualTo(0);
        // seek to 1 min
        sut.setTrackPositionAbsolute(60000);
        Thread.sleep(500);
        position = sut.getTrackPosition();
        assertThat(position).isEqualTo(60);
    }
    
    @Test
    public void pause() throws Exception {
        URL sound = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        String path = new File(sound.getPath()).getAbsolutePath();
        // execute
        sut.loadAndPlay(path, false);
        sut.setVolume(40);
        
        sut.setTrackPositionAbsolute(60000);
        Thread.sleep(1000);
        sut.pause();
        Thread.sleep(2000);
        sut.pause();
        Thread.sleep(1000);
        sut.pause();
        Thread.sleep(2000);
        sut.pause();
        Thread.sleep(3000);
    }
    
    @Test
    public void loadScndFile() throws Exception {
        URL sound01 = this.getClass().getClassLoader().getResource("luv_deluxe.ogg");
        URL sound02 = this.getClass().getClassLoader().getResource("TDCC_next_year.ogg");
        String path01 = new File(sound01.getPath()).getAbsolutePath();
        String path02 = new File(sound02.getPath()).getAbsolutePath();
        // execute
        sut.loadAndPlay(path01, false);
        sut.setVolume(40);
        
        sut.setTrackPositionAbsolute(60000);
        Thread.sleep(3000);
        
        sut.loadAndPlay(path02, false);
        Thread.sleep(10000);
    }

}
