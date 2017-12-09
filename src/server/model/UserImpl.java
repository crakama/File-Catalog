package server.model;

import common.FileInterface;
import common.UserInterface;

public class UserImpl implements UserInterface, FileInterface {
    private  String name, pass, filename, filesize,fileowner,accessPerm;
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

    public UserImpl(String filename, String filesize, String fileowner, String accessPerm){
        //File details
        this.filename = filename;
        this.filesize = filesize;
        this.fileowner = fileowner;
        this.accessPerm = accessPerm;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return pass;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public String getSize() {
        return filesize;
    }

    @Override
    public String getAccessPerm() {
        return accessPerm ;
    }

    @Override
    public String getFileOwner() {
        return fileowner;
    }


}
