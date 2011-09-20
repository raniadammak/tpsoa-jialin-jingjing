package ex4;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;


public class Client implements MessageListener {
	
	private ConnectionFactory connectionFactory;
	private Destination requestQueue;
	private Destination responseQueue;
	
	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.connect();
			client.subscribe();
			client.send();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void subscribe() throws Exception {
		Connection conn = connectionFactory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer mc = session.createConsumer(responseQueue);
		mc.setMessageListener(this);
	}

	private void send() throws Exception {
		Connection conn = connectionFactory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer mProd = session.createProducer(requestQueue);
		
		Scanner scanner = new Scanner(System.in);
		String msg;
		
		while(true) {
			System.out.println("\nTaper le texte à inverser (QUIT pour arreter) :");
			msg = scanner.nextLine();
			
			if (msg.equals("QUIT")) {
				break;
			}
			
			TextMessage tm = session.createTextMessage(msg);
			mProd.send(tm);
			
			System.out.println(" \n>> Requête envoyée,  ID = " + tm.getJMSMessageID());
		}
	}
	
	
	private void connect() throws Exception {
		InitialContext ic = new InitialContext();
		connectionFactory = (ConnectionFactory) ic.lookup("connectionFactory");
		requestQueue = (Destination) ic.lookup("MyRequete");
		responseQueue = (Destination) ic.lookup("MyReponse");
	}

	@Override
	public void onMessage(Message msg)  {
		if (msg instanceof TextMessage) {
			TextMessage tm = (TextMessage) msg;
			try {
				System.out.println("\n>> Réponse obtenue, requête " + msg.getJMSCorrelationID() + " --> " + tm.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		
	}

}
