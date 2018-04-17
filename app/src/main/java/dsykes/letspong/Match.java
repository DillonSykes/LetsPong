package dsykes.letspong;

/**
 * Created by jaydo on 3/28/2018.
 */

public class Match {
    private String player1Score;
    private String player2Score;
    private String matchType;
    private String winner;
    private String player1UID;
    private String player2UID;
    private String matchID;

    public Match(String player1Score, String player2Score, String matchType, String winner, String player1UID, String player2UID, String matchID) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.matchType = matchType;
        this.winner = winner;
        this.player1UID = player1UID;
        this.player2UID = player2UID;
        this.matchID = matchID;
    }

    public String getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(String player1Score) {
        this.player1Score = player1Score;
    }

    public String getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(String player2Score) {
        this.player2Score = player2Score;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getPlayer1UID() {
        return player1UID;
    }

    public void setPlayer1UID(String player1UID) {
        this.player1UID = player1UID;
    }

    public String getPlayer2UID() {
        return player2UID;
    }

    public void setPlayer2UID(String player2UID) {
        this.player2UID = player2UID;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public static String myOpp(Match m){
        String myUID = Users.getCurrentUID();
        if(myUID.equals(m.getPlayer1UID())){
            return m.getPlayer2UID();
        } else{
            return m.getPlayer1UID();
        }
    }
    public static String formatScore(Match m){
        String myUID = Users.getCurrentUID();
        if(myUID.equals(m.getPlayer1UID())){
            return m.getPlayer1Score() + " - " + m.getPlayer2Score();
        } else {
            return m.getPlayer2Score() + " - " + m.getPlayer1Score();
        }
    }
}
