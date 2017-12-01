package common;

import java.io.Serializable;

public class Credentials implements Serializable {
    private final String username;
    private final String password;

    /**
     * Creates a new instance with the specified username and password.
     */
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }
}