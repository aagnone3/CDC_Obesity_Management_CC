package edu.gatech.johndoe.carecoordinator.util;

/**
 * Basic callback interface for receiving asynchronous data from a database
 */
public interface DatabaseCallback {
    void dataReceived(String data);
}
