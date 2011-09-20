package ex4;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
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
	CamelContext context;
	
	public static void main(String[] args) {
		try {
			ClientCamel client = new ClientCamel();
			client.connect();
			client.sendMessages();
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
	}

	private void sendMessages() throws Exception {
		context = new DefaultCamelContext();
		context.addComponent("jms-test", 
				 JmsComponent.jmsComponentAutoAcknowledge(connectionFactory)); 
		ProducerTemplate<JmsExchange> pt = context.createProducerTemplate();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			System.out
					.println("\nTapper un ID à chercher (QUIT pour arreter)");
			String messageInsere = reader.readLine();
			if (messageInsere.equals("QUIT")) {
				break;
			}
			System.out.println("requête envoyé, ID = " + messageInsere);
			pt.sendBody("jms-test:fournisser.MyRequete", messageInsere);
			context.start();
			waitForMessage();
		}
	}

	private void connect() throws Exception {
		Context jndiContext = new InitialContext();
		// Lookup de la fabrique de connexion et de la destination
		connectionFactory = (ConnectionFactory) jndiContext
				.lookup("connectionFactory");
	}

}
