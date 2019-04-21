package org.demo.ashish;

import java.io.IOException;
import java.util.Date;

public class LogWriter extends Thread {

    private String sCommitID;

    private int iLinesAdded;
    private int iMaxLinePerThread;

    public LogWriter(String sID) {
        this.sCommitID = sID;
        iLinesAdded = 0;
        iMaxLinePerThread = 10;
    }

    public LogWriter(String sID, int iMaxLine) {
        this.sCommitID = sID;
        iLinesAdded = 0;
        iMaxLinePerThread = iMaxLine;
    }

    public void setiMaxLinePerThread(int iMaxLinePerThread) {
        this.iMaxLinePerThread = iMaxLinePerThread;
    }


    /**
     * Each Log writer threads continue to write to commit log file, but for the practical
     * limit of the file system, this simulation program chose to stop writing after
     * a maximum number of (configurable) lines per thread has been written.
     *
     * In real world, scenario commit log file would be rotated (i.e. renamed with date/timestamp when
     * a maximum threshold size has reached and separate jobs can archive the logs in
     * hadoop or other storage units if necessary.
     *
     */
    public void run() {
        try {
            System.out.println("Starting a Log Writer (" + this.sCommitID + ") "
                    + Thread.currentThread().getName());

            int iBytesWritten = 1;
            while (iLinesAdded < iMaxLinePerThread) {
                iBytesWritten = writeLog();
                Thread.sleep(50);
            }

        } catch (Exception ex) {
            System.out.println("Exception in Writer " + Thread.currentThread().getName() +  " (" + sCommitID + "): " + ex);
        }
    }


    /**
     *
     * This function writes a random message to commit log in the specific format
     *
     * i.e. CID: UniqueID: Random message with date/time
     *
     * @return: Returns the number of bytes written
     */

    public int writeLog() {
        int iBytesWritten = 0;
        try {
            StringBuffer sContent = new StringBuffer("Event at ")
                                        .append(new Date().toString())
                                        .append(" via ")
                                        .append(Thread.currentThread().getName());
            iBytesWritten = FileWriteHandler.writeLog(this.sCommitID, sContent.toString());
            iLinesAdded++;
        } catch (IOException ex) {
            System.out.println("Unable to write to log file: " + ex.getMessage());
        }

        return iBytesWritten;
    }

}
