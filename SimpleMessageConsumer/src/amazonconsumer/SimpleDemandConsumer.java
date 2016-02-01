package amazonconsumer;

import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import amazonsqs.SimpleQueueService;
import database.AmazonDynamoDBDAO;

import org.json.*;
import utils.StringToJSONConvertor;

/**
 * @author Sujata Rajput
 */
public class SimpleDemandConsumer {
	public static String myQueueUrl = "AmazonHWDemandQueue";
	public static AmazonSQS sqs;

	private static void init() throws Exception {

		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\Sujata Rajput\\.aws\\credentials), and is in valid format.",
							e);
		}

		sqs = new AmazonSQSClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		sqs.setRegion(usWest2);
	}

	public static boolean validDemandMessage(JSONObject inValue){

		boolean validMessage = true;
		int productID = 0 ;
		if (inValue != null) {
			// First we need to determine whether it is an JSONArray, JSONObject, or something else
			// Since we can have multiple XB:LineItems in demand message, it comes as an array.
			JSONObject jsonObj = inValue.optJSONObject("xb:Demand");
			JSONArray jsonArr = inValue.getJSONObject("xb:Demand").optJSONArray("xb:LineItem");

			if(jsonArr != null) {
				// More than one, print all Product IDs

				for (int i = 0; i < jsonArr.length(); i++)
				{   
					productID = jsonArr.getJSONObject(i).getInt("productId");
					if(AmazonDynamoDBDAO.isProductIdInDB(productID))
					{
						System.out.println("Product id " + productID +" exists in DB.");
						validMessage = true;
						continue;

					} else {
						System.out.println("Product id " + productID +" does not exist in DB.");
						validMessage = false;  // if one of the productId does not exist then exit the loop.
						break;
					}
				}

			}			
			else if(jsonObj != null) {
				// Only one xb:LineItem, print product ID
				productID = jsonObj.getJSONObject("xb:LineItem").getInt( "productId" );
				System.out.println("productID = " + productID);
				return (AmazonDynamoDBDAO.isProductIdInDB(productID));
			}

			else {
				// We should not be getting this situation, so throw just print stack trace
				System.out.println("Invalid input sent to validDemandMessage");
				return false;
			}

		} else {
			System.out.println("JSON Obj is null");
			return false; 
		}
		return validMessage;
	}

	public static void consumeHWDemandMessage() throws Exception {

		init();

		try {
			// Receive and Process messages from AmazonHWDemandQueue			
			System.out.println("Receiving messages from AmazonHWDemandQueue.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			if (messages!= null && !messages.isEmpty()) {
				for (Message message : messages) {
					System.out.println("  Message");
					System.out.println("    MessageId:     " + message.getMessageId());
					System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
					System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
					System.out.println("    Body:          " + message.getBody());
					for (Entry<String, String> entry : message.getAttributes().entrySet()) {
						System.out.println("  Attribute");
						System.out.println("    Name:  " + entry.getKey());
						System.out.println("    Value: " + entry.getValue());
					}
					JSONObject jsonObj = StringToJSONConvertor.convertStringToJSON(message.getBody());
					System.out.println(jsonObj);

					if(validDemandMessage(jsonObj)){	
						System.out.println("Valid Product Id. Adding in the demand table");
						AmazonDynamoDBDAO.persistDemandMessageInDB(message.getBody());
						SimpleQueueService.deleteProcessedMessage(message);
					}else{
						System.out.println("Either One Product Id or more product ids do not exist");
						System.out.println("Check your Message for errors");
						System.out.println("Keep the message in AmazonHWDemandQueue to re-process or debug if incorrect message");
					}						
				}
			} else {
				System.out.println("There are no messages in AmazonHWDemandQueue");
			}
			System.out.println();

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it " +
					"to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}
