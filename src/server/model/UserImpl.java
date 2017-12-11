package server.model;

import common.UserInterface;

public class UserImpl implements UserInterface {
    private  String name;
    private String pass;
    private String filename;
    private int filesize;
    private String fileowner;
    private String accessPerm;
    FileCatalogDAO fileCatalogDAO;

    /**
     * arguments passed from @findUser
     * Creates a new user. The UserImpl object will have a database connection.
     * @param name The name of user.
     * @param pass    The password for user.
     * @param fileCatalogDAO     The DAO used to store updates to the database.
     */
    public UserImpl(String name, String pass,FileCatalogDAO fileCatalogDAO){
        //User details
        this.name = name;
        this.pass = pass;
        this.fileCatalogDAO = fileCatalogDAO;


    }

    public UserImpl(String filename, int filesize, String fileowner, String accessPerm){
        //FileInfo details from DB
        this.filename = filename;
        this.filesize = filesize;
        this.fileowner = fileowner;
        this.accessPerm = accessPerm;
    }

    //Used by user to set initial file info
    public UserImpl() {

    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return pass;
    }





}
