package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else if(msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			int numberOfSubscribers = SubscribersList.size();
			try {
				System.out.println(numberOfSubscribers);
				if (numberOfSubscribers == 1) {
					client.sendToClient("client added successfully - O");
					numberOfSubscribers++;
				} else if (numberOfSubscribers == 2) {
					client.sendToClient("client added successfully - X");
					numberOfSubscribers++;
				}
				if (numberOfSubscribers == 2) {
					System.out.println(numberOfSubscribers);
					System.out.println("sent all client are connected");
					sendToAllClients("all clients are connected");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("Client move")) {
			System.out.println(msgString);


		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
