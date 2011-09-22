package ex7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Operer {
	private Commande commande;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Operer p = new Operer();
		p.readXml();
		try {
			p.writeXml();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeXml() throws FileNotFoundException, TransformerException{
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document=builder.newDocument(); 
			
			Element root=document.createElement("commande");
			document.appendChild(root); 
			root.setAttribute("xmlns:tns", "http://www.leuville.com/commande");
			root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			root.setAttribute("xsi:schemaLocation", "http://www.leuville.com/commande commande.xsd");
			
			Element elementR =document.createElement("reference");
			elementR.appendChild(document.createTextNode(commande.getReference()));
			root.appendChild(elementR);
			Element elementD =document.createElement("date");
			elementD.appendChild(document.createTextNode(commande.getDate().toString()));
			root.appendChild(elementD);
			
			Element elementP =document.createElement("lignes-produit");
			root.appendChild(elementP);
			
			List<LigneProduit> produits = commande.getLignesProduit().getLigneProduit();
			
			for(LigneProduit p: produits){
				Element produit =document.createElement("ligne-produit");
				elementP.appendChild(produit);
				Element produitR =document.createElement("reference-produit");
				produitR.appendChild(document.createTextNode(p.getReferenceProduit()));
				produit.appendChild(produitR);
				Element produitQ =document.createElement("Quantite");
				produitQ.appendChild(document.createTextNode(Integer.toString(p.getQuantite())));
				produit.appendChild(produitQ);
				Element produitP =document.createElement("prix");
				produitP.appendChild(document.createTextNode(Float.toString(p.getPrix())));
				produit.appendChild(produitP);
			}
			
			TransformerFactory tf=TransformerFactory.newInstance();
			Transformer transformer=tf.newTransformer();
			DOMSource source=new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.VERSION,"1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			
			PrintWriter pw=new PrintWriter(new FileOutputStream("src/ex7/newXML.xml"));
			StreamResult result=new StreamResult(pw);
			transformer.transform(source,result); 
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void readXml(){
		DocumentBuilderFactory  factory = DocumentBuilderFactory.newInstance();
	    commande = new Commande();
		LignesProduit lignesProduit = new LignesProduit();
		try {
			DocumentBuilder db =  factory.newDocumentBuilder();
			Document doc = db.parse(new File("src/ex7/commande.xml"));
			Element element = doc.getDocumentElement();
			NodeList childNodes = element.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				if (child.getNodeName() == "reference") {
					System.out.println("reference: " + child.getTextContent());
					commande.setReference(child.getTextContent());
				} else if (child.getNodeName() == "date") {
					System.out.println("Date: " + child.getTextContent());
					DatatypeFactory dtf = DatatypeFactory.newInstance();
					commande.setDate(dtf.newXMLGregorianCalendar(child
							.getTextContent()));

				} else if (child.getNodeName() == "lignes-produit") {
					NodeList childNodesP = child.getChildNodes();
					for (int j = 0; j < childNodesP.getLength(); j++) {
						Node child1 = childNodesP.item(j);
						if (child1.getNodeName() == "ligne-produit") {
							NodeList childs = child1.getChildNodes();
							LigneProduit produit = new LigneProduit();
							for (int k = 0; k < childs.getLength(); k++) {
								if (childs.item(k).getNodeName() == "reference-produit") {
									System.out.println("Produit: reference: "
											+ childs.item(k).getTextContent());
									produit.setReferenceProduit(childs.item(k)
											.getTextContent());
								} else if (childs.item(k).getNodeName() == "quantite") {
									System.out.println("Produit: quantity: "
											+ childs.item(k).getTextContent());
									produit.setQuantite(Integer.parseInt(childs
											.item(k).getTextContent()));
								} else if (childs.item(k).getNodeName() == "prix") {
									System.out.println("Produit: prix: "
											+ childs.item(k).getTextContent());
									produit.setPrix(Float.parseFloat(childs
											.item(k).getTextContent()));
								}
							}
							produit.setPrix(1.0f+j);
							lignesProduit.getLigneProduit().add(produit);
						}
					}

				}
			}

			commande.setLignesProduit(lignesProduit);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
