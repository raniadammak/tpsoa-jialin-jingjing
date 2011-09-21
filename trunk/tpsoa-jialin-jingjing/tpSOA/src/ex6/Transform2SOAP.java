package ex6;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Transform2SOAP {
	public static void main(String[] args) {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Source xslSource = new StreamSource("src/ex6/xml2soap1.xsl");
		try {
			Transformer xml2soap = tFactory.newTransformer(xslSource);
			StreamSource xmlSource = new StreamSource("src/ex6/produit.xml");
			Result outputTarget = new StreamResult("src/ex6/soapP.xml");
			System.out
					.println("transformation de produit.xml en soapP.xml par xml2soap.xsl");
			xml2soap.transform(xmlSource, outputTarget);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
