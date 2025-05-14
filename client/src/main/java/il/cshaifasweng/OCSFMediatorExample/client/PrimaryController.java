package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PrimaryController {

	@FXML private Label welcomeLabel;
	@FXML private Button Button00, Button01, Button02;
	@FXML private Button Button10, Button11, Button12;
	@FXML private Button Button20, Button21, Button22;

	public String[][] logicBoard = new String[3][3];
	private int move = 0;
	private Game game = new Game();

	// ---------------------- לוגיקת משחק ----------------------

	void resetBoard() {
		for (int row = 0; row < 3; row++)
			for (int col = 0; col < 3; col++)
				logicBoard[row][col] = "0";

		game = new Game();
		move = 0;
		try {
			updateButtons();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void updateLogicBoard(int row, int col, String sign) {
		logicBoard[row][col] = sign;
		game.setMove(col, row, sign);
		printBoard();
	}

	void printBoard() {
		for (String[] row : logicBoard) {
			for (String cell : row)
				System.out.print(cell + "\t");
			System.out.println();
		}
	}

	void initializeBoard() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				logicBoard[i][j] = "0";
		printBoard();
	}

	// ---------------------- שליטה על כפתורים ----------------------

	void disableBoard() {
		for (Button btn : getAllButtons()) btn.setDisable(true);
	}

	void enableBoard() {
		for (Button btn : getAllButtons()) btn.setDisable(false);
	}

	private Button[][] getButtonGrid() {
		return new Button[][] {
				{Button00, Button01, Button02},
				{Button10, Button11, Button12},
				{Button20, Button21, Button22}
		};
	}

	private Button[] getAllButtons() {
		return new Button[] {
				Button00, Button01, Button02,
				Button10, Button11, Button12,
				Button20, Button21, Button22
		};
	}

	void updateButtons() throws IOException {
		Button[][] buttons = getButtonGrid();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Button btn = buttons[i][j];
				String cell = logicBoard[i][j];

				if (!cell.equals("0")) {
					btn.setText(cell);
					btn.setDisable(true);
				} else {
					btn.setText(" ");
					boolean myTurn = move != -1 &&
							((SimpleClient.sign.equals("X") && move % 2 == 0) ||
									(SimpleClient.sign.equals("O") && move % 2 == 1));
					btn.setDisable(!myTurn);
				}
			}
		}

		if (game.checkForWin()) {
			disableBoard();
			String winner = game.getWinner();
			Platform.runLater(() -> welcomeLabel.setText(winner + " is the winner!"));
		}
	}

	@FXML
	void Tie() {
		if (game.checkForTie()) {
			Platform.runLater(() -> welcomeLabel.setText("it is a tie :)"));
		}
	}

	// ---------------------- שליחת מהלכים ----------------------

	private void sendMove(int row, int col, Button btn) throws IOException {
		if (SimpleClient.sign == null) return;
		btn.setText(SimpleClient.sign);
		logicBoard[row][col] = SimpleClient.sign;
		disableBoard();
		Tie();
		SimpleClient.getClient().sendToServer(row + "," + col + SimpleClient.sign);
	}

	@FXML void write00(ActionEvent e) throws IOException { sendMove(0, 0, Button00); }
	@FXML void write01(ActionEvent e) throws IOException { sendMove(0, 1, Button01); }
	@FXML void write02(ActionEvent e) throws IOException { sendMove(0, 2, Button02); }
	@FXML void write10(ActionEvent e) throws IOException { sendMove(1, 0, Button10); }
	@FXML void write11(ActionEvent e) throws IOException { sendMove(1, 1, Button11); }
	@FXML void write12(ActionEvent e) throws IOException { sendMove(1, 2, Button12); }
	@FXML void write20(ActionEvent e) throws IOException { sendMove(2, 0, Button20); }
	@FXML void write21(ActionEvent e) throws IOException { sendMove(2, 1, Button21); }
	@FXML void write22(ActionEvent e) throws IOException { sendMove(2, 2, Button22); }

	// ---------------------- אירועים ----------------------

	@Subscribe
	public void onMoveEvent(MoveEvent event) throws IOException {
		int row = event.getRow(), col = event.getCol();
		String sign = event.getSign();
		this.move = event.getMove();

		if (row >= 0 && row < 3 && col >= 0 && col < 3) {
			updateLogicBoard(row, col, sign);
			Platform.runLater(() -> {
				try {
					updateButtons();
					Tie();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	@Subscribe
	public void onServerMessage(String message) {
		if (message.equals("new game")) {
			Platform.runLater(() -> {
				resetBoard();
				welcomeLabel.setText("Welcome to Tic Tac Toe!");
			});
		}
	}

	@FXML
	void writingSign(String sign) {
		Platform.runLater(() -> {
			welcomeLabel.setText("your sign is " + sign);
		});
	}

	// ---------------------- התחלה וסיום ----------------------

	@FXML
	void initialize() {
		try {
			EventBus.getDefault().register(this);
			SimpleClient.primaryController = this;
			if (SimpleClient.sign == null) {
				SimpleClient.getClient().sendToServer("add client");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		initializeBoard();
	}

	@FXML
	void sendWarning(ActionEvent event) {
		try {
			SimpleClient.getClient().sendToServer("#warning");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onDestroy() {
		EventBus.getDefault().unregister(this);
	}
}

