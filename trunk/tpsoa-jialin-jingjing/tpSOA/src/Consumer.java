
import javax.jms.Connection; 
import javax.jms.ConnectionFactory; 
import javax.jms.Destination; 
import javax.jms.JMSException;
import javax.jms.Message; 
import javax.jms.MessageConsumer; 
import javax.jms.Session; 
import javax.jms.TextMessage; 
import javax.naming.Context;
import javax.naming.InitialContext; 
 
public class Consumer {      
	private ConnectionFactory connectionFactory; 
	private Destination destination; 
        
    public static void main(String[] args) { 
    	try { 
    		Consumer consumer = new Consumer(); 
            consumer.connect(); 
            consumer.waitForMessage(); 
            }
    	catch (Throwable t) { 
            t.printStackTrace(); 
            } 
	} 
 
	private void waitForMessage() throws Exception { 
		// Cr�er une connexion au syst�me de messagerie 
        // Et affiche les messages au fur et � mesure de leur arriv�e dans la queue 
    	// Cr�ation d'une Connexion et d'une Session
    	Connection connection = connectionFactory.createConnection();
    	connection.start();
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	// Cr�ation d'un MessageConsumer
    	MessageConsumer consumer = session.createConsumer(destination);
    	// R�ception des messages jusqu�� obtention d�un message non texte
    	while (true) {
    		Message m = consumer.receive(100000);
    	    if (m instanceof TextMessage) {
    			traiterMessage(m); // traiterMessage( m : TextMessage ) : void
    	    	}
    	    else {
	    		break;
	    		}
    	}
    	// Fermeture de la connexion
    	connection.close();
	} 
 
	private void traiterMessage(Message m) throws JMSException {
		System.out.println("Message re�u"); 
    	System.out.println( ((TextMessage) m).getText() );    
	}
       
    private void connect() throws Exception { 
    	// Initialise les attributs connectionFactory et destination. 
    	// Cr�ation d'un contexte JNDI
    	Context jndiContext = new InitialContext();
    	// Lookup de la fabrique de connexion et de la destination
    	connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
    	destination = (Destination) jndiContext.lookup("MyQueue");
	} 
}
