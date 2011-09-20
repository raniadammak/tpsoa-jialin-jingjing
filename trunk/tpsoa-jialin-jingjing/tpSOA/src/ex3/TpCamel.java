package ex3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileMessage;
import org.apache.camel.impl.DefaultCamelContext;

public class TpCamel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			CamelContext context = new DefaultCamelContext();
			context.addRoutes(new RouteBuilder() {

				public void configure() throws Exception {
					from("file://c:/temp/in").process(new Processor() {

						public void process(Exchange e) throws Exception {
							FileMessage fileIn = (FileMessage) e.getIn();
							System.out.println("Echange reçu : " + fileIn);
							File file = fileIn.getFile();
							System.out.println("------Contenu du fichier---- ");
							BufferedReader reader = null;
							String line = "";

							reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
							do {
								line = reader.readLine();
								if (line != null)
									System.out.println(line);
								else
									break;
							} while (reader != null);
							reader.close();
						}
					
					}).to("file://c:/temp/out");
				}

			});
			context.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

}
