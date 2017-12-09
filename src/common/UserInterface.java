package common;

import java.io.Serializable;

public interface UserInterface extends Serializable {
    // Get user records
    String getName();

    String getPassword();

    // Get file records
    String getFileName();
    String getSize();
    String getAccessPerm();

    String getFileOwner();
}
