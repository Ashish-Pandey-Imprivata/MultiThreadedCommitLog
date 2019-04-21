#Simulation of commit log

### Problem Statement

> In this simulation program, several writer threads can be configured to write to a single text file, and several reader threads
are trying to read various sections of the file.
Each reader/writer group of threads belong to a event group. 

> To keep things simple each entry in the commit log will have the following structure:

````
CID: <Unique-ID>: Data <CRLF>
````

> Where
CID - the commit entry ID
Unique-ID - a unique identifier for a commit log entry
Data - an arbitrary string that represents data

>Example entries are:

    A: 120: Some sample data was written

    B: 99: Sample Entry for CID=B

    A: 121: Transaction was committed here

    B: 100: Some other sample data being logged

> Restrictions

Unique ID must be unique across all log entries. Multiple Reader/Writer threads can be configured for each Commit Entry ID.
And these group of threads would read/write only those entries from/to the file.

## Instructions to start simulation 

To start a set of writer threads, please use following command:

````

java -cp ./commitLog.jar -Dpropertyfile=commitLog.properties -Dactor=Writer org.demo.ashish.LogSimulator

````

To start a set of reader threads, please use following command:

````

java -cp ./commitLog.jar -Dpropertyfile=commitLog.properties -Dactor=Reader org.demo.ashish.LogSimulator

````

## Assumptions

This simulation program achieves multi-threading when Writers are configured and writing to a single 
commit log file. However, when Readers do not directly interact with commit log file directly. 
In order to efficiently use File operations during read, a single thread reads the contents in a hashmap 
of Queues. Each commit ID group's log entries are stored in a Queue and accessed from this Hashmap via all the 
Readers in thread safe manner.

This simulation assumes a limited space in file system and by default only allows 50 lines of entries 
per Writer (configurable thru properties file to any number as appropriate). 

Commit log is accessible to both Writers/Readers in parallel, but it keeps growing and doesn't roll
to keep the commit lot file size within a limit.



## To Do

* Need to build additional concurrency between File Read operations writing to a data structure and 
Readers consuming from this structure. This needs to refactored **using Reactive Programming paradigm**
to ensure that Readers are invoked (or resume) when there is more data in data structure. Similarly,
File Reader should resume reading the file when additional data gets written to file by Writers.

* Also, code needs to be refactored to remove some of the Singleton classes that are now deemed unecessary
e.g. FileWriteHandler need not be Singleton in the way it was created and used. Main program creates 
the FileWriteHandler once, and only the write operation need to be synchronized. Similarly, UniqueIDGenerator
can read from a database sequence generator

* Currently the Readers terminate with "NoSuchElementException" when there are no more messages to read. 
These threads should continue to wait for more message until terminated.

* Time permitting need to write additional Mock/JUnit test cases
