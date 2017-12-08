package common;

import java.io.IOException;
import java.sql.SQLException;

/**
 * All communications to the server is controlled from here.
 * Methods to be accessed by clients are registers here
 */
public interface ServerTCPInterface {

    //void upLoadFile() throws SQLException, ClassNotFoundException, IOException;

    void sendResponce() throws SQLException, ClassNotFoundException, IOException;

}
