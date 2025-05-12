package il.cshaifasweng.OCSFMediatorExample.client;

import java.awt.*;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.w3c.dom.Text;

public class PrimaryController {


	@FXML
	private Text statusText;

	// 3x3 grid of buttons representing the Tic Tac Toe cells
	@FXML
	private Button btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22;

	// Game state variables
	private boolean xTurn = true; // Track whose turn it is
	private String[][] board = new String[3][3]; // 3x3 Tic Tac Toe grid


	FXML
	private void handleCellClick(javafx.event.ActionEvent event) {
		Button clickedButton = (Button) event.getSource();
		int row = Integer.parseInt(clickedButton.().substring(3, 4)); // Extract row index (0, 1, or 2)
		int col = Integer.parseInt(clickedButton.getId().substring(4, 5)); // Extract column index (0, 1, or 2)

		if (board[row][col] == null) { // If the cell is empty, make a move
			if (xTurn) {
				clickedButton.setText("X");
				board[row][col] = "X";
				statusText.setText("Player O's turn");
			} else {
				clickedButton.setText("O");
				board[row][col] = "O";
				statusText.setText("Player X's turn");
			}
			xTurn = !xTurn; // Switch turns
			checkWinner();
		}
	}


	@FXML
    void sendWarning(ActionEvent event) {
    	try {
			SimpleClient.getClient().sendToServer("#warning");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }



	@FXML
	void initialize(){
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
