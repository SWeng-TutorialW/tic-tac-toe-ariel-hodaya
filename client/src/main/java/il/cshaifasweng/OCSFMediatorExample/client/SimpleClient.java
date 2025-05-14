package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import java.io.IOException;

public class SimpleClient extends AbstractClient {

    public static String sign;
    public static PrimaryController primaryController;
    private static SimpleClient client = null;

    private SimpleClient(String host, int port) throws IOException {
        super(host, port);
    }

    public static SimpleClient getClient() throws IOException {
        if (client == null) {
            client = new SimpleClient("localhost", 3000);
        }
        return client;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        String message = msg.toString();
        System.out.println(">> Received from server: " + message);

        if (msg instanceof Warning) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
            return;
        }

        if (message.contains("client added successfully with sign ")) {
            handleSignAssignment(message);
        } else if (message.equals("all clients are connected")) {
            handleStartPermission();
        } else if (message.equals("0move")) {
            Platform.runLater(() -> primaryController.writingSign(sign));
        } else if (message.equals("new game")) {
            Platform.runLater(primaryController::resetBoard);
        } else if (message.matches("\\d,\\d[XO]\\d")) {
            parseCompactMove(message);
        } else if (message.contains("there is")) {
            parseVerboseMove(message);
        } else {
            System.err.println("!! Unknown message format: " + message);
        }
    }

    private void handleSignAssignment(String message) {
        if (message.contains("X")) {
            sign = "X";
            System.out.println(">> You are assigned: X");
        } else if (message.contains("O")) {
            sign = "O";
            System.out.println(">> You are assigned: O");
        }

        Platform.runLater(() -> primaryController.disableBoard());
    }

    private void handleStartPermission() {
        System.out.println(">> Game ready: both players connected.");
        if ("X".equals(sign)) {
            Platform.runLater(() -> primaryController.enableBoard());
        }
    }

    private void parseCompactMove(String message) {
        try {
            String movePart = message.substring(0, message.length() - 1);
            int moveNumber = Integer.parseInt(message.substring(message.length() - 1));
            String[] parts = movePart.split(",");

            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1].substring(0, 1));
            String sgn = parts[1].substring(1);

            EventBus.getDefault().post(new MoveEvent(row, col, sgn, moveNumber));
        } catch (Exception e) {
            System.err.println("!! Could not parse compact move: " + e.getMessage());
        }
    }

    private void parseVerboseMove(String message) {
        try {
            String[] parts = message.split("there is");
            String[] pos = parts[0].trim().split(",");
            int row = Integer.parseInt(pos[0].trim());
            int col = Integer.parseInt(pos[1].trim().substring(0, 1));
            String sign = parts[1].split("and the move is")[0].trim();
            int move = Integer.parseInt(parts[1].split("and the move is")[1].trim());

            EventBus.getDefault().post(new MoveEvent(row, col, sign, move));
        } catch (Exception e) {
            System.err.println("!! Could not parse verbose move: " + e.getMessage());
        }
    }
}

