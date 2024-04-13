package Chomusuke.functionalities;

public class User {

    private String username;

    public User(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){ //useless cauz builder is supposed to do the job
        this.username = username;
    }

}
