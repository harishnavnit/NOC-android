package com.github.nkzawa.socketio.androidchat;

/**
 * Created by harish on 04/06/16.
 */
public class User {
    private long loginHash;
    private String username, password;

    /**
     * Default constructor
     */
    User() {
        username = "";
        password = "";
        loginHash = generateLoginHash(password);
    }

    /**
     * Multiple argument constructor
     */
    User(String user, String pass) {
        username = user;
        password = pass;
        loginHash = generateLoginHash(pass);
    }

    /**
     * Access the user's username
     * @return String
     */
    public String getUserName() {
        return username;
    }

    /**
     * Access the user's password
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Access the user's loginHash
     * @return double
     */
    public double getLoginHash() {
        return loginHash;
    }

    /**
     * Set the user's username to name
     * @param name
     */
    public void setUserName(String name) {
        username = name;
    }

    /**
     * Set the user's password to pass
     * @param pass
     */
    public void setPassword(String pass) {
        password = pass;
    }

    /**
     * Set the user's loginHash to hash
     * @param hash
     */
    public void setLoginHash(long hash) {
        loginHash = hash;
    }

    /**
     * Generate a loginHash for the user
     * @return long
     */
    public long generateLoginHash(String key) {
        return key.hashCode();
    }
}
