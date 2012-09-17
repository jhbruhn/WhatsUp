package org.alternadev.whatsup;

import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 * 
 */
public class App {
	static final String PHONE_NUMBER = "";
	static final String IMEI = ""; //MAC-Address on iPhone
	static final String USERNAME = "";

	public static void main(String[] args) throws IOException {
		WhatsAPI api = new WhatsAPI(PHONE_NUMBER, IMEI, USERNAME);
		api.connect();
		api.login();
		api.sendMessage(System.currentTimeMillis() + "-1", "",
				"Was geht los darein?!");
		
		while (true) {
			api.pollMessages();
			List<ProtocolNode> list = api.getMessages();

			for (ProtocolNode msg : list) {
				System.out.println(msg.nodeString());
			}
		}

	}
}
