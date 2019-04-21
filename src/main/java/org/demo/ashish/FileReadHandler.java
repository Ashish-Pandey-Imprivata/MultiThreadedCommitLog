package org.demo.ashish;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileReadHandler extends Thread {

    public class Node {
        private String sUniqueID;
        private String sContent;

        public Node(String sUniqueID, String sContent) {
            this.sUniqueID = sUniqueID;
            this.sContent = sContent;
        }

        public String getsUniqueID() {
            return sUniqueID;
        }

        public String getsContent() {
            return sContent;
        }
    }

    private BufferedReader fCommitLogHandleReader;
    private static FileReadHandler obj;

    private HashMap<String, ConcurrentLinkedQueue<Node>> hReadLines;

    public HashMap<String, ConcurrentLinkedQueue<Node>> gethReadLines() {
        return hReadLines;
    }

    private FileReadHandler () {
        hReadLines = new HashMap<>();
    }

    public static FileReadHandler getInstance() {
        if (obj == null) {
            // Following block must be threadsafe
            synchronized (FileReadHandler.class) {
                // Since multiple threads can reach this step
                // following null check is needed again
                if (obj == null)
                    obj = new FileReadHandler();
            }
        }

        return obj;
    }

    /**
     *
     * @param sFileName
     * @throws IOException : File IO exception should be handled at Writer/Reader level
     *                      so that those programs manage it within themselves.
     */
    public void setFileHandle(String sFileName) throws IOException {
        if (sFileName == null || sFileName.trim().length() == 0) {
            System.out.println("Invalid file name: " + sFileName);
            return;
        }

        if (fCommitLogHandleReader == null) {
            fCommitLogHandleReader = new BufferedReader(new FileReader(sFileName));
        }
    }

    public void run () {
        boolean running = true;

        while (running) {
            try {
                String sLine = fCommitLogHandleReader.readLine();
                if (sLine != null && sLine.trim().length() > 0)
                    processLine(sLine);
                else
                    running = false;
            }
            catch (Exception ex) {
                System.out.println("Error while reading the file: " + ex.getMessage());
            }
        }

        hReadLines.keySet().forEach((k -> System.out.println("CID: (" + k + ") occurrences = " +
                hReadLines.get(k).size())));
    }

    /**
     *
     * This function parses the line of format "CID: UniqueID: Message String"
     * and adds in the corresponding linked list for each CID, which is stored in Hashmap
     * @param sLine is one line from commit log with a specific format.
     * @Returns: unique ID that's processed (it was meant to be used for Mock testing)
     */

    public String processLine (String sLine) {

        if (sLine == null || sLine.trim().length() == 0)
            return null;

        // Line would be of the format
        // A: 1001: Writing a message at Sat Apr 20...
        // Since there could be ":" parameters in message, it it split into 3 strings at most
        String [] arr = sLine.split(": ",3);

        if (arr.length < 3) {
            System.out.println("Invalid log format: " + sLine);
            return null;
        }

        ConcurrentLinkedQueue<Node> theNode = hReadLines.get(arr[0]);
        if (theNode == null) {
            theNode = new ConcurrentLinkedQueue<>();
            hReadLines.put(arr[0],theNode);
        }

        theNode.add(new Node (arr[1],arr[2]));

        return arr[1];
    }
}
