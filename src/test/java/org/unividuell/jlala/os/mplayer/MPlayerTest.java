package org.unividuell.jlala.os.mplayer;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

@RunWith(MockitoJUnitRunner.class)
public class MPlayerTest {
    
    
    private static final String GET = "get_property ";

    private static final String ANS = "ANS_";

    private static final String SET = "set_property ";

    @Mock
    PrintStream mockOSmPlayerIn;
    
    @Mock
    BufferedReader mockOSmPlayerOutErr;
    
    @Mock
    Process mockProcess;
    
    private MPlayer sut;
    
    @Before
    public void setup() throws IOException {
        sut = new MPlayer();
        sut.setMplayerIn(mockOSmPlayerIn);
        sut.setMplayerOutErr(mockOSmPlayerOutErr);
        sut.setMplayerProcess(mockProcess);
    }
    
    @Test
    public void open() throws Exception {
        // prepare
        when(mockOSmPlayerOutErr.readLine()).thenReturn("Starting playback...");
        String file = "/path/to/file.ogg";

        // execute
        sut.open(file);
        
        // verify
        verify(mockOSmPlayerIn).print("loadfile " + file + " 0");
    }

    @Test
    public void getTrackPosition() throws Exception {
        // given
        Long expected = 2000L;
        given(mockOSmPlayerOutErr.readLine()).willReturn(ANS + "time_pos=" + expected);
        
        // when
        long actual = sut.getTrackPosition();
        
        // then
        assertThat(actual).isEqualTo(expected);
        verify(mockOSmPlayerIn).print(GET + "time_pos");
    }
    
    @Test
    public void setVolume() throws Exception {
        // prepare
        long expected = 56;

        // execute
        sut.setVolume(expected);
        // verify 
        verify(mockOSmPlayerIn).print(SET + "volume " + expected + ".0");
    }
    
    @Test
    public void getVolume() throws Exception {
        // prepare
        when(mockOSmPlayerOutErr.readLine()).thenReturn("ANS_volume=25.000000");
        // execute
        long actual = sut.getVolume();

        // verify
        assertThat(actual).isEqualTo(25);
    }
}
