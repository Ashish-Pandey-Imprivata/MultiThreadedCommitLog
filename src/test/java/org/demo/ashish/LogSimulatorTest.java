package org.demo.ashish;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Due to nature of this program, there is not much that can be tested
 * using JUnit...
 *
 * Mockito framework may be used to Mock File operations and threads
 *
 */

public class LogSimulatorTest {

    @Test
    @DisplayName("Properties file not provided")
    public void testPropertiesReader () {
        LogSimulator simulator = new LogSimulator();

        String[] args = {"TEST"};
        simulator.Initialize(args);

        Properties prop = simulator.getEnvProps();
        assertEquals (prop.size(), 4);
    }
}