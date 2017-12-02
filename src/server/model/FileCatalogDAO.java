package server.model;

import common.UserInterface;

import java.sql.*;

/**
 * This Database Handler [data access object (DAO)], it encapsulates all database calls in the catalogue application.
 * JDBC and database details are hidden from client and Server objects.
 */

public class FileCatalogDAO {
    private PreparedStatement createUserStmt;
    Connection conn;
    private static final String TABLE_NAME="USER";
    private static final String USER_COLUMN_NAME="USERNAME";
    private static final String BRA =" (";
    private static final String PASS_COLUMN_NAME ="PASS";

    public FileCatalogDAO(String datasource, String dbms) throws SQLException, ClassNotFoundException {
        conn = createDatasource(datasource,dbms);
        queryStatements(conn);
    }

    private Connection createDatasource(String datasource, String dbms) throws
            ClassNotFoundException, SQLException{
        conn = connectToFileCatalogDB(datasource,dbms);
        if (!tableExists(conn)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE " + TABLE_NAME
                    + " (" + USER_COLUMN_NAME + " VARCHAR(32) PRIMARY KEY, "
                    + PASS_COLUMN_NAME + " VARCHAR(32))");

        }
        return conn;
    }

    private void queryStatements(Connection conn) throws SQLException {
        createUserStmt = conn.prepareStatement("INSERT INTO " + TABLE_NAME + " VALUES(?, ?)");


    }


    private boolean tableExists(Connection connection) throws SQLException {
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(tableNameColumn).equals(TABLE_NAME)) {
                    return true;
                }
            }
            return false;
        }
    }




    //TO DO write SQL statement to add user to the database

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
