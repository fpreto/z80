package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.mmu.IO;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * This class is a mock for IO interface and allow to queue input data to be read and
 * assert for all outputs.
 */
public class IOTestHelper implements IO {

    private final HashSet<Integer> directPrintPorts = new HashSet<>();
    private final HashMap<Integer, LinkedList<Integer>> inQueues = new HashMap<>();
    private final HashMap<Integer, LinkedList<Integer>> outQueues = new HashMap<>();

    @Override
    public int in(int port) {
        LinkedList<Integer> queue = getInQueueForPort(port);

        if (queue.size() == 0) {
            throw new RuntimeException("IO IN read without data available");
        }

        return queue.pollFirst();
    }

    @Override
    public void out(int port, int value) {
        if (!directPrintPorts.contains(port)) {
            LinkedList<Integer> queue = getOutQueueForPort(port);
            queue.addLast(value);
        } else {
            System.out.print((char)value);
            System.out.flush();
        }
    }

    /**
     * Queue data for future read
     *
     * @param port target port
     * @param value data to be read
     */
    public void queueForRead(int port, int value) {
        LinkedList<Integer> queue = getInQueueForPort(port);
        queue.addLast(value);
    }

    /**
     * Assert that all the input data has been read
     */
    public void assertInputEmpty() {
        for (HashMap.Entry<Integer, LinkedList<Integer>> entry : inQueues.entrySet()) {
            if (entry.getValue().size() != 0) {
                throw new AssertionError("Input not empty. Port " + entry.getKey() + " has " + entry.getValue().size() + " bytes unread.");
            }
        }
    }

    /**
     * Assert that all output data has been asserted
     */
    public void assertOutputEmpty() {
        for (HashMap.Entry<Integer, LinkedList<Integer>> entry : outQueues.entrySet()) {
            if (entry.getValue().size() != 0) {
                throw new AssertionError("Output queue not empty. Port " + entry.getKey() + " has " + entry.getValue().size() + " bytes not asserted.");
            }
        }
    }

    /**
     * Assert that an output value written in a given port matches.
     * @param port
     * @param value
     */
    public void assertOuputData(int port, int value) {
        LinkedList<Integer> queue = getOutQueueForPort(port);

        if (queue.size() == 0) {
            throw new AssertionError("Output is empty");
        }

        int queueValue = queue.pollFirst();
        assertEquals("Writen data in out is different", value, queueValue);
    }

    /**
     * Configure port to output directly to system out instead of queue for future assertion.
     * @param port
     */
    public void printPort(int port) {
        directPrintPorts.add(port);
    }

    private LinkedList<Integer> getInQueueForPort(int port) {
        return inQueues.computeIfAbsent(port, key -> new LinkedList<>());
    }

    private LinkedList<Integer> getOutQueueForPort(int port) {
        return outQueues.computeIfAbsent(port, key -> new LinkedList<>());
    }
}
