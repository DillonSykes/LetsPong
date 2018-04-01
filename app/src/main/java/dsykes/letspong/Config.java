package dsykes.letspong;

/**
 * Created by jaydo on 3/8/2018.
 */

public class Config {
    //JSON URL
    public static final String DATA_URL_GET_USERS = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getUsers.php";
    public static final String DATA_URL_GET_MATCH_ID = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getLastMatchID.php";

    //Tags used in the JSON String
    public static final String TAG_USERID = "UID";
    public static final String TAG_USERNAME = "Username";
    public static final String TAG_MAX_MATCH_ID = "max(MatchID)";

    //JSON array name
    public static final String JSON_ARRAY = "result";
}
