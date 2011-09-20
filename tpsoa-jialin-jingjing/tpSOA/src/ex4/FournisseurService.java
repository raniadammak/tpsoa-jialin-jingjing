package ex4;

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
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.impl.DefaultCamelContext;

public class FournisseurService {

	private ConnectionFactory connectionFactory;
	private Destination destinationRequete;
	private Destination destinationReponse;
	private Session session;
	private Connection connection;
	CamelContext context;
	
	public static void main(String[] args) {
		try {

			FournisseurService service = new FournisseurService();
			service.connect();
			service.exchangeMessage();

		} catch (Throwable t) {
			t.printStackTrace();
		}
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
		context = new DefaultCamelContext();
		context.addComponent("jms-test", 
				 JmsComponent.jmsComponentAutoAcknowledge(connectionFactory)); 
	}
	
	public float getPrix(String idProduit) {
		return 5.0f;
	}

	public void exchangeMessage() {
		try {	
			System.out.println("\nAttente de la prochaine requete...");
			context.addRoutes(new RouteBuilder() {
				
				public void configure() throws Exception {
					from("jms-test:fournisser.MyRequete").process(new Processor() {
						public void process(Exchange e) throws Exception {
							JmsMessage textIn = (JmsMessage) e.getIn();
							Message m = textIn.getJmsMessage();
							String produitId = ((TextMessage)m).getText();
							System.out.println("\nrequête obtenue, produit ID : " + produitId);
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