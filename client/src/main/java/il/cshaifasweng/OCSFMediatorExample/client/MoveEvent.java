package il.cshaifasweng.OCSFMediatorExample.client;

public class MoveEvent {
    private final int row;
    private final int col;
    private final String sign;
    private final int move;

    public MoveEvent(int row, int col, String sign, int move) {
        this.row = row;
        this.col = col;
        this.sign = sign;
        this.move = move;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getSign() {
        return sign;
    }

    public int getMove() {
        return move;
    }
}