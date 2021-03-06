package com.crakama.server.model;

public class FileCatalog implements FileInterface{

    private String fname,owner,access;
    private long size;
    private FileDao fileDao;

    /**
     * Constructor executed with need of DB connection
     * @param name
     * @param fsize
     * @param fileDao
     */
    public FileCatalog(String name, String owner, String access, long fsize, FileDao fileDao){
        this.fname = name;
        this.owner = owner;
        this.access = access;
        this.size = fsize;
        this.fileDao = fileDao;
    }

    /**
     * Original contructor called with no need of DB connection, hence value null
     * @param
     * @param fsize
     */
    public FileCatalog(String filename, String fowner, String access, long fsize) {
        this(filename,fowner,access,fsize,null);
    }

    public FileCatalog(String filename, String owner, String access, FileDao fileDao) {
        this(filename,owner,access,0,null);
    }

    @Override
    public String getAccessMode() {
        return access;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getFileName() {
        return fname;
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
        builder.append(", File: [");
        builder.append(fname);
        builder.append(", Owner: ");
        builder.append(owner);
        builder.append(", Access Mode: ");
        builder.append(access);
        builder.append(", Size: ");
        builder.append(size);
        builder.append("]");
        return builder.toString();
    }
}
