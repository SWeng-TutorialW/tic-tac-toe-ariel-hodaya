package il.cshaifasweng.OCSFMediatorExample.server;

public class GameBoard {
    private final String[][] board;
    private String currentPlayer;

    public GameBoard() {
        board = new String[3][3];
        currentPlayer = "X";
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public boolean makeMove(int row, int col) {
        if (!board[row][col].isEmpty()) {
            return false;
        }
        board[row][col] = currentPlayer;
        return true;
    }

    public String checkWinner() {
        // Rows and Columns
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]))
                return board[i][0];
            if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]))
                return board[0][i];
        }

        // Diagonals
        if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
            return board[0][0];
        if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]))
            return board[0][2];

        return null;
    }

    public boolean isDraw() {
        for (String[] row : board) {
            for (String cell : row) {
                if (cell.isEmpty()) {
                    return false;
                }
            }
        }
        return checkWinner() == null;
    }

    public void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCell(int row, int col) {
        return board[row][col];
    }
}
