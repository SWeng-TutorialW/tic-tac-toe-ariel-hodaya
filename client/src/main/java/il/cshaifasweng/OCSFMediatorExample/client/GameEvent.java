package il.cshaifasweng.OCSFMediatorExample.client;


public class GameEvent {
    private String opponentId;
    private String turn;



    public GameEvent(String opponentId) {
        this.opponentId = opponentId;
        this.turn= "X";
    }

    public String getOpponentId() {
        return opponentId;
    }
}
