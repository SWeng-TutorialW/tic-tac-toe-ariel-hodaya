package il.cshaifasweng.OCSFMediatorExample.entities;


public class PlayerJoinedEvent {
    private final String playerId;

    public PlayerJoinedEvent(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
