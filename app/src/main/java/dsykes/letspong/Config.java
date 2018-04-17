package dsykes.letspong;

/**
 * Created by jaydo on 3/8/2018.
 */

public class Config {
    //JSON URL
    public static final String DATA_URL_GET_USERS = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getUsers.php";
    public static final String DATA_URL_GET_MATCH_ID = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getLastMatchID.php";
    public static final String DATA_URL_GET_MATCH_DATA = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getMatch.php";
    public static final String DATA_URL_GET_RECORD = "http://ec2-34-229-88-91.compute-1.amazonaws.com/getRecord.php";
    //Tags used in the JSON String
    public static final String TAG_USERID = "UID";
    public static final String TAG_USERNAME = "Username";
    public static final String TAG_UID1 = "Player1UID";
    public static final String TAG_UID2 = "Player2UID";
    public static final String TAG_MATCH_TYPE = "matchType";
    public static final String TAG_WINNER = "Winner";
    public static final String TAG_MATCH_ID = "MatchID";
    public static final String TAG_P1_SCORE = "player1Score";
    public static final String TAG_P2_SCORE = "player2Score";


    public static final String TAG_MAX_MATCH_ID = "max(MatchID)";

    //JSON array name
    public static final String JSON_ARRAY = "result";
}
