package com.crakama.server.model;

public class FileCatalog implements FileInterface{

    private String username,owner,access;
    private int size;
    private FileDao fileDao;

    /**
     * Constructor executed with need of DB connection
     * @param name
     * @param fileDao
     */
    public FileCatalog(String name, String owner,String access, int fsize, FileDao fileDao){
        this.username = name;
        this.owner = owner;
        this.access = access;
        this.size = fsize;
        this.fileDao = fileDao;
    }

    /**
     * Original contructor called with no need of DB connection, hence value null
     * @param
     */
    public FileCatalog(String filename, String fowner, String access, int fsize) {
        this(filename,fowner,access,fsize,null);
    }

    public FileCatalog(String filename, String owner, FileDao fileDao) {
        this(filename,owner,null,0,null);
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getAccessMode() {
        return access;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getOwner() {
        return owner;
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
        builder.append(", File: ");
        builder.append(username);
        builder.append(" owned by: ");
        builder.append(owner);
        return builder.toString();
    }

}
