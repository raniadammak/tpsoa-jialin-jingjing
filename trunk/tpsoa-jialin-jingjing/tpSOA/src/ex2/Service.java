package ex2;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Service {

	private ConnectionFactory connectionFactory;
	private Destination destinationRequete;
	private Destination destinationReponse;
	private Session session;
	private Connection connection;

	public static void main(String[] args) {
		try {

			Service service = new Service();
			service.connect();
			service.waitForMessage();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private String reverse(String chaine) {
		String result = "";
		for (int i = chaine.length() - 1; i >= 0; i--) {
			result += chaine.charAt(i);
		}
		return result;
	}

	private void waitForMessage() throws Exception {
		// Cr�er une connexion au syst�me de messagerie
		// Et affiche les messages au fur et � mesure de leur arriv�e dans la
		// queue
		// Cr�ation d'une Connexion et d'une Session
		//connection = connectionFactory.createConnection();
		connection.start();
		//System.out.println("\nDestination: " + destination.toString());
		//session = connection.createSession(false,
		//		Session.AUTO_ACKNOWLEDGE);
		// Cr�ation d'un MessageConsumer
		MessageConsumer consumer = session.createConsumer(destinationRequete);
		// R�ception des messages jusqu�� obtention d�un message non texte
		while (true) {
			System.out.println("\nAttente de la prochaine requete...");
			Message m = consumer.receive(10000);
			if (m instanceof TextMessage) {
				traiterMessage(m); // traiterMessage( m : TextMessage ) : void
			} else {
				break;
			}
		}
		// Fermeture de la connexion
		session.close();
		connection.close();
		System.out.println("\nclose");
	}

	private void traiterMessage(Message m) throws Exception {
		String requete = ((TextMessage) m).getText();
		String reponse = reverse(requete);
		System.out.println("requ�te obtenue :" + requete);
		sendMessages(reponse);
	}

	private void sendMessages(String reponse) throws Exception {
		// Cr�er une connexion au syst�me de messagerie
		// Emet des messages au fur et � mesure que l�utilisateur les saisit
		// Cr�ation d'une Connexion et d'une Session:
		if (reponse != "") {
			/*Connection connection = connectionFactory.createConnection();

			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);*/
			// Cr�ation d'un MessageProducer et d'un message de type Text
			MessageProducer producer = session.createProducer(destinationReponse);

			TextMessage message = session.createTextMessage(reponse);
			// Envoi d'un message
			producer.send(message);
			System.out.println("Message envoy�: " + reponse);
			//TextMessage m = session.createTextMessage(reponse);
			//m.setJMSCorrelationID(requestId);
			// Fermeture de la connexion
			//connection.close();
		}	
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
		//System.out.println("\nDestination: " + destination.toString());
		session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
	}
}
