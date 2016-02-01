package amazonsqs;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Fill in your AWS access credentials in the provided credentials file
 * template, and be sure to move the file to the default location
 * (C:\\Users\\Sujata Rajput\\.aws\\credentials) where the this code will load the credentials from.
 *
 * @author Sujata Rajput
 */
public class SimpleQueueService {

	public static AmazonSQS sqs ;
	public static String myQueueUrl; 

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
	public static void createQueues() throws Exception {
		init();
		try {
			// Create a queue
			System.out.println("Creating a new SQS queue called AmazonHWDemandQueue.\n");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest("AmazonHWDemandQueue");
			myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
				
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

	public static void sendMessages(String xmlString) throws Exception {       	
		try {

			System.out.println("Sending a message to AmazonHWDemandQueue.\n");
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, xmlString));

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
	// Delete a message
	public static void deleteProcessedMessage (Message message) {
		System.out.println("Deleting a message.\n");
		if(message!= null) {
		String messageReceiptHandle = message.getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
		} else {
			System.out.println(" Message is null");
		}

	}
	// Delete a queue : Out of Scope for this project 
	private void deleteQueue(){
		System.out.println("Deleting the test queue.\n");
		sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
	}

}
