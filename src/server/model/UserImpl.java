package server.model;

import common.UserInterface;

public class UserImpl implements UserInterface {
    private  String name, pass;
    FileCatalogDAO fileCatalogDAO;

    /**
     * arguments passed from @findUser
     * Creates a new user. The UserImpl object will have a database connection.
     * @param name The name of user.
     * @param pass    The password for user.
     * @param fileCatalogDAO     The DAO used to store updates to the database.
     */
    public UserImpl(String name, String pass, FileCatalogDAO fileCatalogDAO){
        this.name = name;
        this.pass = pass;
        this.fileCatalogDAO = fileCatalogDAO;
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
