

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection; 
import javax.jms.ConnectionFactory; 
import javax.jms.DeliveryMode; 
import javax.jms.Destination; 
import javax.jms.MessageProducer; 
import javax.jms.Session; 
import javax.jms.TextMessage; 
import javax.naming.Context;
import javax.naming.InitialContext; 
 
 
public class Producer { 
        
       private ConnectionFactory connectionFactory; 
       private Destination destination; 
        
       public static void main(String[] args) { 
              try { 
                     Producer producer = new Producer(); 
                     producer.connect(); 
                     producer.sendMessages(); 
              } catch (Throwable t) { 
                     t.printStackTrace(); 
              } 
       } 
        
       private void sendMessages() throws Exception { 
              // Créer une connexion au système de messagerie
    	   
              // Emet des messages au fur et à mesure que l’utilisateur les saisit 
    	   // Création d'une Connexion et d'une Session:
    	   Connection connection = connectionFactory.createConnection();
    	   Session session = connection.createSession(false, 0);
    	   // Création d'un MessageProducer et d'un message de type Text
    	   MessageProducer producer = session.createProducer(destination);
    	   BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	   System.out.println("Message?"); 
    	   String messageInsere = reader.readLine(); 
    	   TextMessage message = session.createTextMessage(messageInsere);
    	   // Envoi d'un message
    	   producer.send(message);
    	   // Fermeture de la connexion
    	   connection.close();
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