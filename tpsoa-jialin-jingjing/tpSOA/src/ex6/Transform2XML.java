package ex6;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Transform2XML {
	public static void main(String[] args) {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Source xslSource = new StreamSource("src/ex6/soap2xml.xsl");
		try {
			Transformer xml2soap = tFactory.newTransformer(xslSource);
			StreamSource xmlSource = new StreamSource("src/ex6/soap.xml");
			Result outputTarget = new StreamResult("src/ex6/xmlP.xml");
			System.out
					.println("transformation de soap.xml en xmlP.xml par soap2xml.xsl");
			xml2soap.transform(xmlSource, outputTarget);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
