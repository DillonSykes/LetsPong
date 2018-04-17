package dsykes.letspong;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by jaydo on 3/10/2018.
 */

public class Users {
    private String userName;
    private String UID;
    public Users(String username, String userId){
        userName = username;
        UID = userId;
    }
    public String getUserName(){
        return userName;
    }
    public String getUserId() {
        return UID;
    }
    public static String getCurrentUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }
    public static String getCurrentUID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static String findUserName(String UID, ArrayList<Users> users){
        String ender = "";
        for(Users aUser : users){
            if(UID.equals(aUser.getUserId())){
                ender = aUser.getUserName();
            }
        }
        return ender;
    }
}
