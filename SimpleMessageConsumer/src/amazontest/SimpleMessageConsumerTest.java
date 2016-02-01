package amazontest;

import java.io.File;

import amazonconsumer.SimpleDemandConsumer;
import amazonsqs.SimpleQueueService;
import database.AmazonDynamoDBDAO;
import utils.XMLToStringConvertor;
/**
 * @author Sujata Rajput
 */
public class SimpleMessageConsumerTest {

	// Test HW_Demand message
	//TODO : Make file path platform independent by using getClass().getResource(url).toURI() method
	
	public static String testHWDemandXml  = XMLToStringConvertor.convertXMLToString
			(new File ("C:/Users/Sujata Rajput/AmazonMessageConsumer/SimpleMessageConsumer/src/amazontest/HW_Demand_Test6.xml"));

	public static void main(String[] args) throws Exception {
		AmazonDynamoDBDAO.createTables();
		SimpleQueueService.createQueues();
		SimpleQueueService.sendMessages(testHWDemandXml);
		SimpleDemandConsumer.consumeHWDemandMessage();
		System.out.println("+++++++++++++++++++++++++++++++++++++++");
		System.out.println("Consumer finished processing testHWDemandXml : " + testHWDemandXml);
		System.out.println("+++++++++++++++++++++++++++++++++++++++");

	}

}
