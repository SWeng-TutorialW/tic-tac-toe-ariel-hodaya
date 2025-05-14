package il.cshaifasweng.OCSFMediatorExample.client;

public class Game {

    private String[][] board = new String[3][3];
    private String winner;

    public Game() {
        resetBoard();
    }

    public void setMove(int col, int row, String sign) {
        board[row][col] = sign;
    }

    public String getBoard() {
        return flattenBoard();
    }

    private String flattenBoard() {
        StringBuilder result = new StringBuilder();
        for (String[] row : board)
            for (String cell : row)
                result.append(cell);
        return result.toString();
    }

    public boolean checkForWin() {
        return checkRows() || checkCols() || checkDiagonals();
    }

    public boolean checkForTie() {
        for (String[] row : board)
            for (String cell : row)
                if (cell.equals("0"))
                    return false;
        return !checkForWin();  // אם מישהו ניצח זה לא תיקו
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            String first = board[i][0];
            if (!first.equals("0") && first.equals(board[i][1]) && first.equals(board[i][2])) {
                winner = first;
                return true;
            }
        }
        return false;
    }

    private boolean checkCols() {
        for (int i = 0; i < 3; i++) {
            String first = board[0][i];
            if (!first.equals("0") && first.equals(board[1][i]) && first.equals(board[2][i])) {
                winner = first;
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        String mid = board[1][1];
        if (!mid.equals("0")) {
            if (mid.equals(board[0][0]) && mid.equals(board[2][2])) {
                winner = mid;
                return true;
            }
            if (mid.equals(board[0][2]) && mid.equals(board[2][0])) {
                winner = mid;
                return true;
            }
        }
        return false;
    }

    public String getWinner() {
        return winner;
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = "0";
        winner = null;
    }
}
