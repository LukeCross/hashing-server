# hashing-server
A simple file hashing server and client written in Java.

This code is from my final year Networks assignment.

########################################################

The assignment description is as follows:
Working in C, Java or another language you know, write a network server which will read a stream of data from a network connection and then, once the file has arrived, sends back a secure hash of the file (using SHA1, SHA256 or similar).  It should be multi-threaded or forked so that it can operate on multiple streams at once.  You will need to use shutdown() or similar in order to signal an end-of-transmission while leaving the connection open for the response.  You should construct the hash as the data arrives, rather than buffering it all to work with after the connection has closed.

If you are programming in Java, use a low-level socket library such as java.nio.

########################################################

I chose to implement the assignment in Java.

########################################################

HashingClient - A class that starts the client and connects it to the server

HashingServer - A class that starts the server and listens on a thread

HashingServerThread - A class representing the thread that the server runs on when a connection is established

HashingUtils - A Utility class consisting of static methods to hold reusable code
