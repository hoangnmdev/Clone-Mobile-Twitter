package vn.edu.usth.twitter;

public class User {
    private String email;
    private String password;
    private String name;
    private String tagName;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String name, String tagName){
        this.email = email;
        this.password = password;
        this.name = name;
        this.tagName = tagName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
