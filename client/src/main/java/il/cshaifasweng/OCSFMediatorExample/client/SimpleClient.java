package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private String sign;
	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
        if (msg.getClass().equals(Warning.class)) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        }
        String message;
        if (msg.getClass().equals(String.class)) {
            message = msg.toString();
			System.out.println(message);
			if (message.equals("client added successfully - X")) {
                System.out.println(message);
				EventBus.getDefault().post(new GameEvent("O"));
				this.sign = "X";
            }
			if (message.equals("client added successfully - O")) {
				System.out.println(message);
				EventBus.getDefault().post(new GameEvent("X"));
				this.sign = "O";
			}
        } else {
            message = msg.toString();
            System.out.println(message);
        }
    }
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
