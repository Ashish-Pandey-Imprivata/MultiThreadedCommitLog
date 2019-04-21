package org.demo.ashish;

public class UniqueIDGenerator {
    private static int iSequence;
    private static int iLineNumber;

    private static int ID_STARTING_AT = 1000;

    private volatile static UniqueIDGenerator obj;

    private UniqueIDGenerator() {
    }

    /**
     * Thread safe singleton object creation
     * @return
     */
    public static UniqueIDGenerator getInstance() {
        if (obj == null) {
            // Following block must be threadsafe
            synchronized (UniqueIDGenerator.class) {
                // Since multiple threads can reach this step
                // following null check is needed again
                if (obj == null)
                    obj = new UniqueIDGenerator();

                iSequence = ID_STARTING_AT;
                iLineNumber = 0;
            }
        }

        return obj;
    }

    /**
     * This is a unique ID generator using a singleton class. This approach is
     * limited in scope as the ID generation would start from same starting point
     * every time this program is started.
     *
     * In actual application, we would be using a database sequence ID
     *
     * @return: A "next number" based on a starting value.
     */
    public static synchronized int getID() {
        iSequence++;
        return iSequence;
    }

}
