package org.demo.ashish;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LogReaderTest {


    @Test
    public void shouldProcessWritingToFile() throws Exception {

        FileReadHandler mock = mock (FileReadHandler.class);

        Mockito.

        when(mock.gethReadLines()).thenReturn (new HashMap<>());
//        when(mock.processLine("C:1003:str")).thenReturn ("1003");

        LogReader logReader = new LogReader("A");
        assertNull(logReader.readLog());
    }

}
