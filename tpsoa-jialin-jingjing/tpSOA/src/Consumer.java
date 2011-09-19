
import javax.jms.Connection; 
import javax.jms.ConnectionFactory; 
import javax.jms.Destination; 
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
		// Créer une connexion au système de messagerie 
        // Et affiche les messages au fur et à mesure de leur arrivée dans la queue 
    	// Création d'une Connexion et d'une Session
    	Connection connection = connectionFactory.createConnection();
    	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    	// Création d'un MessageConsumer
    	MessageConsumer consumer = session.createConsumer(destination);
    	// Réception des messages jusqu’à obtention d’un message non texte
    	while (true) {
    		Message m = consumer.receive(100000);
    		System.out.println();
    		System.out.println("instance ");
    	    if (m instanceof TextMessage) {
    			System.out.println("instance ");
    			traiterMessage(m); // traiterMessage( m : TextMessage ) : void
    	    	}
    	    else {
	    		break;
	    		}
    	}
    	// Fermeture de la connexion
    	connection.close();
	} 
 
	private void traiterMessage(Message m) {
		System.out.println("Message traite ");
    	System.out.println(m.toString());   
	}
       
    private void connect() throws Exception { 
    	// Initialise les attributs connectionFactory et destination. 
    	// Création d'un contexte JNDI
    	Context jndiContext = new InitialContext();
    	// Lookup de la fabrique de connexion et de la destination
    	connectionFactory = (ConnectionFactory) jndiContext.lookup("connectionFactory");
    	destination = (Destination) jndiContext.lookup("MyQueue");
	} 
}
