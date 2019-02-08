package ca.polymtl.inf8480.tp1.shared;

import java.util.ArrayList;

/**
 * Created by jelacs on 08/02/19.
 */
public class Group {

    private String groupName;
    private ArrayList<String> users;

    public Group(String groupName){
        this.groupName = groupName;
        this.users = new ArrayList();
    }

    public Group(String groupName, String userName){
        super();
        this.users.add(userName);
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void addUser(String user){
        users.add(user);
    }
}
