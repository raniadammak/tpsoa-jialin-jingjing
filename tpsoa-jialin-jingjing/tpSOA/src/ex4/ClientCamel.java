package ex4;


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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsConsumer;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.JmsExchange;
import org.apache.camel.impl.DefaultCamelContext;

public class ClientCamel {

	private ConnectionFactory connectionFactory;
	private Destination destinationRequete;
	private Destination destinationReponse;
	private Session session;
	private Connection connection;
	CamelContext context;
	
	public static void main(String[] args) {
		try {
			ClientCamel client = new ClientCamel();
			client.connect();
			client.sendMessages();
			client.waitForMessage();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void waitForMessage() throws Exception {
		JmsEndpoint responseEndPoint = (JmsEndpoint)
		context.getEndpoint("jms-test:fournisser.MyReponse");
		JmsConsumer consumer = responseEndPoint.createConsumer(new Processor() {
			public void process(Exchange e) throws Exception {
				System.out.println("Réponse ");
			System.out.println("Réponse reçue : " + e.getIn().getBody());
			}});
		consumer.start();
//		connection.start();
//		MessageConsumer consumer = session.createConsumer(destinationReponse);
//		consumer.setMessageListener(new MessageListener() {
//			@Override
//			public void onMessage(Message m) {
//				TextMessage textMsg = (TextMessage) m;
//				try {
//					traiterMessage(textMsg);
//				} catch (JMSException e) {
//					e.printStackTrace();
//				}
//			}
//
//		});

	}

	private void traiterMessage(TextMessage m) throws JMSException {
		System.out.println("réponse obtenue, requête ID: "
				+ m.getJMSMessageID() + "---> " + m.getText());
	}

	private void sendMessages() throws Exception {
		context = new DefaultCamelContext();
		context.addComponent("jms-test", 
				 JmsComponent.jmsComponentAutoAcknowledge(connectionFactory)); 
		ProducerTemplate<JmsExchange> pt = context.createProducerTemplate();
		pt.sendBody("jms-test:fournisser.MyRequete", "Article_1");
		
		context.start();
		
//		MessageProducer producer = session.createProducer(destinationRequete);
//		BufferedReader reader = new BufferedReader(new InputStreamReader(
//				System.in));
//		while (true) {
//			System.out.println("\nTapper un ID à chercher (QUIT pour arreter)");
//			String messageInsere = reader.readLine();
//			if (messageInsere.equals("QUIT")) {
//				break;
//			}
//			System.out.println("ID: " + messageInsere);
//			TextMessage message = session.createTextMessage(messageInsere);
//			producer.send(message);
//			System.out.println("requête envoyé, ID =  "
//					+ message.getJMSMessageID());
//			waitForMessage();
//		}
//		// Fermeture de la connexion
//		session.close();
//		connection.close();
//		System.out.println("\nclose");
	}

	private void connect() throws Exception {
		// Initialise les attributs connectionFactory et destination.
		// Création d'un contexte JNDI
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
