package server.model;

import common.UserInterface;

import java.sql.*;

/**
 * This Database Handler [data access object (DAO)], it encapsulates all database calls in the catalogue application.
 * JDBC and database details are hidden from client and Server objects.
 */

public class FileCatalogDAO {
    private PreparedStatement createUserStmt,deleteUserStmt,findUserStmt,
            uploadFileStmt, createFileInfo;
    Connection conn;
    private static final String USER_TABLE="USER";
    private static final String FILE_TABLE="file";
    private static final String FILEINFO_TABLE="fileinfo";
    private static final String USER_COLUMN_NAME="USERNAME";
    private static final String PASS_COLUMN_NAME ="PASS";
    private static final String FILEB ="files";
    private static final String FNAME ="fname";
    private static final String FSIZE ="fsize";
    private static final String FOWNER ="fowner";
    private static final String ACCESS_P ="faccessPerm";

    public FileCatalogDAO(String datasource, String dbms) throws SQLException, ClassNotFoundException {
        conn = createDatasource(datasource,dbms);
        queryStatements(conn);
    }

    public FileCatalogDAO() {

    }

    public FileCatalogDAO(String datasource, String dbms, byte[] filedata) throws SQLException, ClassNotFoundException {
        conn = connectToFileCatalogDB(datasource,dbms);
        uploadfileDB(datasource,dbms,filedata);
    }

    private Connection createDatasource(String datasource, String dbms) throws
            ClassNotFoundException, SQLException{
        conn = connectToFileCatalogDB(datasource,dbms);
        if (!tableExists(conn)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE " + USER_TABLE
                    + " (" + USER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + PASS_COLUMN_NAME + " VARCHAR(32))");

            statement.executeUpdate("CREATE TABLE " + FILE_TABLE
                    + " (" + FILEB + " LONGBLOB )");

            statement.executeUpdate("CREATE TABLE " + FILEINFO_TABLE
                    + " (" + FNAME + " VARCHAR(32), "
                    + FOWNER + " VARCHAR(32), "
                    + ACCESS_P + " VARCHAR(32), "
                    + FSIZE + " INT)");

        }
        return conn;
    }
    /**
     * @registerUser Creates a new user, userInterface is the user to be created
     * @param userInterface is an instance of UserImpl object but of type UserInterface,
     * referencing instance of the class as instance of the interface
     * @throwsSQLException
     */
    public void registerUser(UserInterface userInterface) throws SQLException {
        createUserStmt.setString(1, userInterface.getName());
        createUserStmt.setString(2,userInterface.getPassword());
        int rows = createUserStmt.executeUpdate();
        if(rows != 1){
            System.out.println("Unable to register new user!!");
        }
    }

    public void uploadFileInfo(UserInterface fileInterface) throws SQLException {
        createFileInfo.setString(1, fileInterface.getFName());
        createFileInfo.setString(2,fileInterface.getFowner());
        createFileInfo.setString(3, fileInterface.getFaccessMode());
        createFileInfo.setInt(4,fileInterface.getFsize());
        int rows = createFileInfo.executeUpdate();
        if(rows != 1){
            System.out.println("Unable to Add new file details!!");
        }
    }

    public UserImpl findUser(String username) throws SQLException{
        findUserStmt.setString(1,username);
        ResultSet rs = findUserStmt.executeQuery();
        if(rs.next()){
            return new UserImpl(username,rs.getString(PASS_COLUMN_NAME),this);
        }
        return null;
    }
    public void deleteUser(UserImpl userimpl) throws SQLException{
        deleteUserStmt.setString(1,userimpl.getName());
        deleteUserStmt.executeUpdate();
    }
    public void uploadfileDB(String datasource, String dbms, byte[] fdatabytes) throws SQLException, ClassNotFoundException {

        uploadFileStmt = conn.prepareStatement("INSERT INTO file (files) values (?)");
        uploadFileStmt.setBytes(1,fdatabytes);
        uploadFileStmt.executeUpdate();
    }

    private void queryStatements(Connection conn) throws SQLException {
        createUserStmt = conn.prepareStatement("INSERT INTO " + USER_TABLE + " VALUES(?, ?)");

        deleteUserStmt = conn.prepareStatement("DELETE FROM "
                + USER_TABLE
                + " WHERE name = ?");

        findUserStmt = conn.prepareStatement("SELECT * FROM "
                + USER_TABLE
                + " WHERE name = ?");
        createFileInfo = conn.prepareStatement("INSERT INTO " + FILEINFO_TABLE + " VALUES(?, ?, ?, ?)");

    }




    private boolean tableExists(Connection conn) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = conn.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(USER_TABLE)) {
                    return true;
                }
            }
            return false;
        }
    }




    //TO DO write SQL statement to add user to the database



    public Connection connectToFileCatalogDB(String datasource,String dbms) throws ClassNotFoundException, SQLException {
        if(dbms.equalsIgnoreCase("mysql")){
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + datasource, "root", "root");
            return conn;
        }
        System.out.println("Connection Returned Null: Unable to create datasource, unknown dbms.");
        return null;
    }



}
