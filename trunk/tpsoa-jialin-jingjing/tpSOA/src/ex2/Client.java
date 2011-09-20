package ex2;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
import javax.naming.Context;
import javax.naming.InitialContext;

public class Client {

	private ConnectionFactory connectionFactory;
	private Destination destinationRequete;
	private Destination destinationReponse;
	private Session session;
	private Connection connection;

	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.connect();
			client.sendMessages();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void waitForMessage() throws Exception {
		// Cr�er une connexion au syst�me de messagerie
		// Et affiche les messages au fur et � mesure de leur arriv�e dans la
		// queue
		// Cr�ation d'une Connexion et d'une Session
		// Connection connection = connectionFactory.createConnection();
		connection.start();
		// Session session = connection.createSession(false,
		// Session.AUTO_ACKNOWLEDGE);
		// Cr�ation d'un MessageConsumer
		MessageConsumer consumer = session.createConsumer(destinationReponse);
		// R�ception des messages jusqu�� obtention d�un message non texte
		//consumer.setMessageListener(this);
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message m) {
				TextMessage textMsg = (TextMessage) m;
				try {
					traiterMessage(textMsg);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}

		});

		// Fermeture de la connexion
		// connection.close();
	}

	private void traiterMessage(TextMessage m) throws JMSException {
		System.out.println("r�ponse obtenue, requ�te ID: "
				+ m.getJMSMessageID() + "---> " + m.getText());
	}

	private void sendMessages() throws Exception {
		// Cr�er une connexion au syst�me de messagerie
		// Emet des messages au fur et � mesure que l�utilisateur les saisit
		// Cr�ation d'une Connexion et d'une Session:
		// Cr�ation d'un MessageProducer et d'un message de type Text
		MessageProducer producer = session.createProducer(destinationRequete);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			// System.out.println("\nDestination: " + destination.toString());
			System.out
					.println("\nTapper un text � reverser (QUIT pour arreter)");
			String messageInsere = reader.readLine();
			if (messageInsere.equals("QUIT")) {
				break;
			}
			TextMessage message = session.createTextMessage(messageInsere);
			// Envoi d'un message
			producer.send(message);
			System.out.println("requ�te envoy�, ID =  "
					+ message.getJMSMessageID());
			waitForMessage();
		}
		// Fermeture de la connexion
		session.close();
		connection.close();
		System.out.println("\nclose");
	}

	private void connect() throws Exception {
		// Initialise les attributs connectionFactory et destination.
		// Cr�ation d'un contexte JNDI
		Context jndiContext = new InitialContext();
		// Lookup de la fabrique de connexion et de la destination
		connectionFactory = (ConnectionFactory) jndiContext
				.lookup("connectionFactory");
		destinationRequete = (Destination) jndiContext.lookup("MyRequete");
		destinationReponse = (Destination) jndiContext.lookup("MyReponse");
		connection = connectionFactory.createConnection();

		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

}
