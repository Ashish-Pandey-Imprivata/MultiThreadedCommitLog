package org.demo.ashish;

import java.io.*;

/**
 * This class would be a singleton and would be having methods to write a log to a file in thread safe manner
 */


public class FileWriteHandler {

    private static FileWriter fCommitLogHandleWriter;


    private static boolean APPEND = true;
    private static int LINE_SIZE = 80;

    private volatile static FileWriteHandler obj;

    private FileWriteHandler() {
        UniqueIDGenerator.getInstance();
        LINE_SIZE   = 80; // assuming that most logs would be around this length
    }

    /**
     * Threadsafe singleton object for Write operation
     * @return
     */
    public static FileWriteHandler getInstance() {
        if (obj == null) {
            // Following block must be threadsafe
            synchronized (FileWriteHandler.class) {
                // Since multiple threads can reach this step
                // following null check is needed again
                if (obj == null)
                    obj = new FileWriteHandler();
            }
        }

        return obj;
    }


    /**
     *
     * @param sFileName
     * @throws IOException: File IO exception should be handled at Writer/Reader level
     *                      so that those programs manage it within themselves.
     */
    public static void setFileHandle(String sFileName) throws IOException {
        if (sFileName == null || sFileName.trim().length() == 0) {
            System.out.println("Invalid file name: " + sFileName);
            return;
        }

        if (fCommitLogHandleWriter == null) {
            // Following block must be threadsafe
            synchronized (FileWriteHandler.class) {
                // Since multiple threads can reach this step
                // following null check is needed again
                if (fCommitLogHandleWriter == null) {
                    File file = new File(sFileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    fCommitLogHandleWriter = new FileWriter(sFileName, FileWriteHandler.APPEND);
                }
            }
        }
    }

    public static int writeLog(String sCommitID, String sMessage) throws IOException {

        // for better optimization of String operations, using an optimal size of 80
        StringBuffer finalString = new StringBuffer(LINE_SIZE);
        finalString.append(sCommitID).append(": ");
        finalString.append(UniqueIDGenerator.getID()).append(": ");
        finalString.append(sMessage).append("\n");

        // write the log in thread safe manner
        synchronized (FileWriteHandler.class) {
            fCommitLogHandleWriter.write(finalString.toString());
            fCommitLogHandleWriter.flush();
        }

        // Returning the length of string written for validation purposes
        return finalString.toString().length();
    }

}
