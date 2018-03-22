package com.crakama.server.model;

public class FileCatalog implements FileInterface{

    private String username,pass;
    private FileDao fileDao;

    /**
     * Constructor executed with need of DB connection
     * @param name
     * @param pass
     * @param fileDao
     */
    public FileCatalog(String name, String pass, FileDao fileDao){
        this.username = name;
        this.pass = pass;
        this.fileDao = fileDao;
    }

    /**
     * Original contructor called with no need of DB connection, hence value null
     * @param name
     * @param pass
     */
    public FileCatalog(String name, String pass){
        this(name,pass,null);
    }

    public FileCatalog(String resp) {
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
