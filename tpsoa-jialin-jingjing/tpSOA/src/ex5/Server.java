package ex5;

import org.apache.camel.CamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server
{
	public static void  main( String[] argv )
	{
		ApplicationContext appContext = new ClassPathXmlApplicationContext("camel-server.xml");
		CamelContext context = (CamelContext) appContext.getBean("camel");
		try {
			context.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
