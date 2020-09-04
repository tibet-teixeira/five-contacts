package tibet.fivecontacts.model;

import java.io.Serializable;
import java.lang.reflect.Array;

public class User implements Serializable {
    private String name;
    private String login;
    private String password;
    private String email;

    /**
     *
     * @param name
     * @param login
     * @param password
     */
    public User(String name, String login, String password) {
        this(name, login, password, "");
    }

    /**
     *
     * @param name
     * @param login
     * @param password
     * @param email
     */
    public User(String name, String login, String password, String email) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}