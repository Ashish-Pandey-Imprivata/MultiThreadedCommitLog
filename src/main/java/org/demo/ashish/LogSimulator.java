package org.demo.ashish;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class LogSimulator {

    //   private String sEnvironment;
    private Properties envProps;

    private String sPropertyFileName;
    private String sCommitLogFileName;
    private String sAction;
    private HashMap <String, Integer> workerGroup;
    private int iMaxLinePerThread;

    public String getsCommitLogFileName() {
        return sCommitLogFileName;
    }

    public LogSimulator() {
        envProps = new Properties();
        // Defaulting to Writer if not provided at command line
        sAction  = "Writer";
    }

    public Properties getEnvProps() {
        return envProps;
    }

    public void setsPropertyFileName(String sPropertyFileName) {
        this.sPropertyFileName = sPropertyFileName;
    }

    public static void main(String[] args) {

        LogSimulator simulator = new LogSimulator();
        simulator.Initialize(args);
        FileWriteHandler objW = FileWriteHandler.getInstance();
        FileReadHandler objR = FileReadHandler.getInstance();

        try {
            objR.setFileHandle(simulator.getsCommitLogFileName());
            FileWriteHandler.setFileHandle(simulator.getsCommitLogFileName());
        }
        catch (IOException ex) {
            System.out.println(ex);
            return;
        }

        simulator.startWorkerThreads ();
    }

    private void startWorkerThreads () {

        // File reader worker threads rely upon File Read Handler thread
        // which is processing the file line by line and distributing the work
        // to reader threads
        if ("Reader".equals(sAction)) {
            FileReadHandler.getInstance().run();
        }

        for (String key : workerGroup.keySet()) {
            for (int i = 0; i < workerGroup.get(key); i++) {
                if ("Writer".equals(sAction))
                    new LogWriter(key, iMaxLinePerThread).start();
                else if ("Reader".equals(sAction))
                    new LogReader(key).start();
            }
        }
    }

    public void Initialize(String[] args) {

        if (System.getProperty("propertyfile") != null)
            sPropertyFileName = System.getProperty("propertyfile");

        if (System.getProperty("actor") != null)
            sAction  = System.getProperty("actor");

        if (sPropertyFileName != null) {
            try {
                File f = new File(sPropertyFileName);
                InputStream in = null;
                if (f.exists()) {
                    in = new FileInputStream(f);
                    envProps.load(in);
                    in.close();
                }
            } catch (Exception ex) {
                // Properties file is optional, hence ignoring the exception
            }
        } else {

            // Below can be handled in better manner by reading command line arguments
            // e.g. -DcommitID.A=3 -DcommitID.B=2 -Dfilename=./commit.log -DmaxLines=500 etc
            // But since the objective of this program was to showcase file
            // operations in multi-threaded manner, it was not done in the interest of time
            envProps.put("commitID.A", "3");
            envProps.put("commitID.B", "2");
            envProps.put("filename", "./commit.log");
            envProps.put("maxLinesPerThread", 50);
        }

        // properties starting with "commitID." string were created
        // so that it is easy to add more thread pools easily
        // Below for loop extracts the substring for CID by staring from
        // 9 position to exclude string "commitID."
        workerGroup = new HashMap<>();
        for (String key : envProps.keySet().toArray(new String[0])) {
            if (key.startsWith("commitID"))
                workerGroup.put(key.substring(9), Integer.parseInt(envProps.getProperty(key)));
        }
        sCommitLogFileName = envProps.getProperty("filename");

        // Below property is applicable for Writer workers only
        if (envProps.getProperty("maxLinesPerThread") != null)
            iMaxLinePerThread  = Integer.parseInt(envProps.getProperty("maxLinesPerThread"));

        System.out.println(sAction + " is working with below configurations/properties");
        envProps.keySet().forEach(k -> System.out.println("key: " + k + ", value: " + envProps.getProperty((String)k)));
    }
}
