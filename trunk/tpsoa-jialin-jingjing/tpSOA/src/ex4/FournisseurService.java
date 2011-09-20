package ex4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class FournisseurService {

	private ConnectionFactory connectionFactory;
	private Destination destinationRequete;
	private Destination destinationReponse;
	private Session session;
	private Connection connection;
	
	public static void main(String[] args) {
		try {

			FournisseurService service = new FournisseurService();
			service.connect();
			service.waitForMessage();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void waitForMessage() throws Exception {
		connection.start();
		MessageConsumer consumer = session.createConsumer(destinationRequete);
		// Réception des messages jusqu’à obtention d’un message non texte
		while (true) {
			System.out.println("\nAttente de la prochaine requete...");
			Message m = consumer.receive(10000);
			if (m instanceof TextMessage) {
				exchangeMessage(); // traiterMessage( m : TextMessage ) : void
			} else {
				break;
			}
		}
		// Fermeture de la connexion
		session.close();
		connection.close();
		System.out.println("\nclose");
	}

	private void sendMessages(String reponse) throws Exception {
		if (reponse != null && reponse != "") {
			MessageProducer producer = session.createProducer(destinationReponse);

			TextMessage message = session.createTextMessage(reponse);
			producer.send(message);
			System.out.println("\nPrix envoyé: " + reponse);
		}	
	}

	private void connect() throws Exception {
		Context jndiContext = new InitialContext();
		// Lookup de la fabrique de connexion et de la destination
		connectionFactory = (ConnectionFactory) jndiContext
				.lookup("connectionFactory");
		destinationRequete = (Destination) jndiContext.lookup("MyRequete");
		destinationReponse = (Destination) jndiContext.lookup("MyReponse");
		connection = connectionFactory.createConnection();
		session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
	}
	
	public float getPrix(String idProduit) {
		return 5.0f;
	}

	public void exchangeMessage() {
		try {
			CamelContext context = new DefaultCamelContext();
			context.addComponent("jms-test", 
					 JmsComponent.jmsComponentAutoAcknowledge(connectionFactory)); 
			
			context.addRoutes(new RouteBuilder() {
				
				public void configure() throws Exception {
					from("jms-test:fournisser.MyRequete").process(new Processor() {
						public void process(Exchange e) throws Exception {
							System.out.println("ffffffffffffffffffffff");
							TextMessage textIn = (TextMessage) e.getIn();
							String produitId = textIn.getText();
							System.out.println("\nProduit ID : " + produitId);
							float prix = getPrix( produitId);
							
							sendMessages(Float.toString(prix));
							
						}

					}).to("jms-test:fournisser.MyReponse");
				}

			});
			context.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}