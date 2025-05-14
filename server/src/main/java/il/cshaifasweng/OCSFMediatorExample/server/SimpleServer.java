package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleServer extends AbstractServer {

	private static final List<SubscribedClient> connectedClients = new ArrayList<>();
	private final String[] signs = {"X", "O"};
	private int playersJoined = 0;
	private int selectedFirstPlayer;
	private int moveCount = 0;

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client)  {
		String text = msg.toString();
		System.out.println(">> Incoming message: " + text);

		if (text.startsWith("#warning")) {
			sendWarningToClient(client);
		} else if (text.startsWith("add client")) {
			registerNewClient(client);
		} else if (text.startsWith("remove client")) {
			unregisterClient(client);
		} //else if (text.equals("game over")) {
		//handleGameOver();
		//}
		else if (text.length() == 4) {
			processMoveMessage(text);
		} else {
			System.out.println("!! Unknown message format received: " + text);
		}
	}

	private void sendWarningToClient(ConnectionToClient client) {
		try {
			Warning warning = new Warning("Warning from server!");
			client.sendToClient(warning);
			System.out.printf(">> Sent warning to client at %s%n", client.getInetAddress().getHostAddress());
		} catch (IOException e) {
			System.err.println("!! Failed to send warning.");
			e.printStackTrace();
		}
	}

	private void registerNewClient(ConnectionToClient client) {
		connectedClients.add(new SubscribedClient(client));
		try {
			if (playersJoined == 0) {
				selectedFirstPlayer = new Random().nextInt(2);
				client.sendToClient("client added successfully with sign " + signs[selectedFirstPlayer]);
				System.out.println(">> First player assigned: " + signs[selectedFirstPlayer]);
			} else if (playersJoined == 1) {
				client.sendToClient("client added successfully with sign " + signs[1 - selectedFirstPlayer]);
				System.out.println(">> Second player assigned: " + signs[1 - selectedFirstPlayer]);
			}
			playersJoined++;

			if (playersJoined == 2) {
				System.out.println(">> Game ready: Two players connected.");
				broadcastMessage("all clients are connected");
				broadcastMessage(moveCount + "move");
			}
		} catch (IOException e) {
			System.err.println("!! Error assigning player sign.");
			e.printStackTrace();
		}
	}


	private void unregisterClient(ConnectionToClient client) {
		moveCount=0;

		try {
			// Close connection
			client.close();
		} catch (IOException e) {
			System.err.println("!! Error while closing client connection.");
			e.printStackTrace();
		}

		// Remove from list
		connectedClients.removeIf(sc -> sc.getClient().equals(client));
		System.out.println(">> Client disconnected and removed.");

		// Decrease playersJoined to allow re-join
		if (playersJoined > 0) {
			playersJoined--;
		}


	}

	private void handleGameOver() {
		System.out.println(">> Game ended. Resetting for next match.");
		moveCount = 0;
		playersJoined = 0;
		connectedClients.clear();
		broadcastMessage("new game");
	}

	private void processMoveMessage(String msg) {
		moveCount++;
		System.out.printf(">> Move #%d received: %s%n", moveCount, msg);
		broadcastMessage(msg + moveCount);
	}

	private void broadcastMessage(String msg) {
		for (SubscribedClient client : connectedClients) {
			try {
				client.getClient().sendToClient(msg);
			} catch (IOException e) {
				System.err.println("!! Failed to deliver message to a client.");
				e.printStackTrace();
			}
		}
	}
}
