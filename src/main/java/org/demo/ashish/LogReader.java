package org.demo.ashish;

public class LogReader extends Thread {

    private String sCommitID;
    private FileReadHandler obj;

    public LogReader(String sID) {
        this.sCommitID = sID;
        obj = FileReadHandler.getInstance();
    }

    /**
     * Reader threads continue to read from data structure which is a hashmap of Queues
     * Each key represents a CID, and Queue contains ordered list of elements read from
     * commit log file
     */

    public void run() {
        try {
            System.out.println("Starting a Log Reader (" + this.sCommitID + ") "
                    + Thread.currentThread().getName());

            String sMessage = null, sUniqueID = null;
            boolean running = true;
            while (running) {
                FileReadHandler.Node theNode = readLog();
                if (theNode != null) {
                    sMessage = theNode.getsContent();
                    sUniqueID = theNode.getsUniqueID();
                }
                else
                    sMessage = null;

                if (sMessage != null) {
                    System.out.println(Thread.currentThread().getName() + "(" + this.sCommitID + ") read: " + sUniqueID
                            + " message: " + sMessage);
                    sleep(1500);
                } else {
                    try {
                        sleep(5000);
                    } catch (InterruptedException ex) {
                        running = false;
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("Exception in Reader " + Thread.currentThread().getName() +  " (" + sCommitID + "): " + ex);
        }
    }

    /**
     * Since multiple threads are going to read this data structure concurrently
     * this method must be declared as synchronized
     *
     * @return: Node value if there are any unread nodes, else null
     */
    public synchronized FileReadHandler.Node readLog() {
        if (!obj.gethReadLines().isEmpty())
            return obj.gethReadLines().get(sCommitID).remove();
        else
            return null;
    }

}
