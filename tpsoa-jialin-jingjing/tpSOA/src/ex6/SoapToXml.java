package ex6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;

import org.springframework.core.Constants;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SoapToXml {
	    private static final String DEFAULT_DATA_FILENAME   = "./PO.xml";
	    private static final String URI  = "urn:oreilly-jaws-samples";
	 
	    private String m_hostURL;
	    private String m_dataFileName;
	 
	    public SoapToXml(String dataFileName) throws Exception
	    {
	        m_dataFileName    = dataFileName;
	        System.out.println();
	        System.out.println("_________________________________________________________");
	        System.out.println("Starting GenericHTTPSoapClient:");
	        System.out.println("    host url        = " + m_hostURL);
	        System.out.println("    data file       = " + m_dataFileName);
	        System.out.println("___________________________________________________________");
	        System.out.println();
	    }

	    public void sendSOAPMessage()
	    {
	        try
	        {  
	            FileReader sr = new FileReader (m_dataFileName);
	            InputSource is = new InputSource(sr);
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);

	            if (doc == null) {
	                throw new Exception();
	            }
	 
	            // create a vector for collecting the header elements
	            Vector headerElements = new Vector();
	 
	            // Create a header element in a namespace
	            org.w3c.dom.Element headerElement = doc.createElementNS(URI,"jaws:MessageHeader");
	 
	            headerElement.setAttributeNS(URI,"SOAP-ENV:mustUnderstand","1");
	 
	            // Create subnodes within the MessageHeader
	            org.w3c.dom.Element ele = doc.createElement("From");
	            org.w3c.dom.Text textNode = doc.createTextNode("Me");
	            org.w3c.dom.Node tempNode = ele.appendChild(textNode);
	            tempNode = headerElement.appendChild(ele);
	 
	            ele = doc.createElement("To");
	            textNode = doc.createTextNode("You");
	            tempNode = ele.appendChild(textNode);
	            tempNode = headerElement.appendChild(ele);
	 
	            ele = doc.createElement("MessageId");
	            textNode = doc.createTextNode("9999");
	            tempNode = ele.appendChild(textNode);
	            tempNode = headerElement.appendChild(ele);
	            headerElements.add(headerElement);
	            // create a vector for collecting the body elements
	            Vector bodyElements = new Vector();

	            bodyElements.add(doc.getDocumentElement ());
	 
	            //Create the SOAP envelope
	            soap.Envelope envelope = new org.apache.soap.Envelope();
	 
	            //Add the SOAP header element to the envelope
	            org.apache.soap.Header header = new org.apache.soap.Header();
	            header.setHeaderEntries(headerElements);
	            envelope.setHeader(header);
	 
	            //Create the SOAP body element
	            org.apache.soap.Body body = new org.apache.soap.Body();
	            body.setBodyEntries(bodyElements);
	 
	            //Add the SOAP body element to the envelope
	            envelope.setBody(body);
	 
	            // Build the Message.
	            org.apache.soap.messaging.Message msg = new org.apache.soap.messaging.Message();
	 
	            msg.send (new java.net.URL(m_hostURL), URI, envelope);
	            System.out.println("Sent SOAP Message with Apache HTTP SOAP Client.");

	            System.out.println("Waiting for response....");
	            org.apache.soap.transport.SOAPTransport st = msg.getSOAPTransport ();
	            BufferedReader br = st.receive ();
	            String line = br.readLine();
	            if(line == null)
	            {
	                System.out.println("HTTP POST was successful. \n");
	            }
	            else
	            {
	               while (line != null)
	               {   
	            	 　　System.out.println(line);
	   	　　　　　　　　　　line = br.readLine();
	               }
	            }
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	    public static void main(String args[]) {

	    }

}
