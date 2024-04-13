package Chomusuke.functionalities;

import java.util.ArrayList;

public class Team {

    private int teamLength;

    private ArrayList<User> teamcomp = new ArrayList<>();

    public Team(int teamLength,ArrayList<User> teamcomp){
        this.teamLength = teamLength;
        this.teamcomp = teamcomp;
    }

    public Team(int teamLength){
        this.teamLength = teamLength;
    }

    public void addTeamMember(User user){
        this.teamcomp.add(user);
    }

    public void removeTeamMember(User user){
        if (this.teamcomp.contains(user)) {
            this.teamcomp.remove(user);
        }
    }

    public void setTeamcomp(ArrayList<User> teamcomp){
        this.teamcomp = teamcomp;
    }

    public ArrayList<User> getTeamcomp(){
        return this.teamcomp;
    }

    public int getTeamLength(){
        return this.teamLength;
    }
}
