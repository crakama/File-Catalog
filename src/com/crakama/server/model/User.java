package com.crakama.server.model;

/**
 * Represents User Object in the catalog
 * Holds data related to specific User and methods to get user details
 * Return by Value used because server uses it ONLY to pass data back to client and
 * has no behaviour or link back to server if viewed from the client side
 */
public class User implements UserInterface {

    private String username,pass;
    private FileDao fileDao;

    /**
     * Constructor executed with need of DB connection
     * @param name
     * @param pass
     * @param fileDao
     */
    public User(String name, String pass, FileDao fileDao){
        this.username = name;
        this.pass = pass;
        this.fileDao = fileDao;
    }

    /**
     * Original contructor called with no need of DB connection, hence value null
     * @param name
     * @param pass
     */
    public  User(String name, String pass){
        this(name,pass,null);
    }

    public User(String resp) {
    }

    @Override
    public String getUserName() {
        return username;
    }

    /**
     * Prints out details of this class object as specified in the method
     * Any call to User class is converted to string and specific details stated here are returned.
     * @return
     * findUserByName in FileDao returns a version of this object with username and password to other
     * classes like Controller/ServerStub.
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(", User: ");
        builder.append(username);
        builder.append(" with password: ");
        builder.append(pass);
        return builder.toString();
    }

    public String getPassword() {
        return pass;
    }
}
