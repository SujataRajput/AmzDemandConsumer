package database;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;

/**
 * @author Sujata Rajput
 */
@SuppressWarnings("deprecation")
public class AmazonDynamoDBDAO {

	public static AmazonDynamoDBClient dynamoDB;
	public static String productTable = "product";
	public static String demandTable = "demand";

	public static void init() throws Exception {

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
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		dynamoDB.setRegion(usWest2);
	}

	@SuppressWarnings("deprecation")
	public static void createTables() throws Exception {
		init();

		try {

			// Create productTable if it does not exist yet
			if (Tables.doesTableExist(dynamoDB, productTable)) {
				System.out.println("Table " + productTable + " is already ACTIVE");
			} else {
				// Create a table with a primary hash key named 'name', which holds a string
				CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(productTable)
						.withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
						.withAttributeDefinitions(new AttributeDefinition().withAttributeName("name").withAttributeType(ScalarAttributeType.S))
						.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
				TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				// Wait for it to become active
				System.out.println("Waiting for " + productTable + " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, productTable);
			}

			// Describe Product table
			DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(productTable);
			TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
			System.out.println("Table Description: " + tableDescription);

			addProducts();  // Adding products is out of scope for this project.                

			// Create demandTable if it does not exist yet
			if (Tables.doesTableExist(dynamoDB, demandTable)) {
				System.out.println("Table " + demandTable + " is already ACTIVE");
			} else {
				// Create a table with a primary hash key named 'name', which holds a string
				CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(demandTable)
						.withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
						.withAttributeDefinitions(new AttributeDefinition().withAttributeName("name").withAttributeType(ScalarAttributeType.S))
						.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
				TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				// Wait for it to become active
				System.out.println("Waiting for " + demandTable + " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, demandTable);
			}

			// Describe our new table
			DescribeTableRequest describeDemandTableRequest = new DescribeTableRequest().withTableName(demandTable);
			TableDescription demandtableDescription = dynamoDB.describeTable(describeDemandTableRequest).getTable();
			System.out.println("Table Description: " + demandtableDescription);

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	public static boolean isProductIdInDB(int previousProductID){

		GetItemResult getItemResult = null;

		try {

			Map<String, AttributeValue> queryItem = new HashMap<String, AttributeValue>(); 
			queryItem.put("name", new AttributeValue(Integer.toString(previousProductID)));
			GetItemRequest getItemRequest = new GetItemRequest(productTable, queryItem);
			getItemResult = dynamoDB.getItem(getItemRequest);
			System.out.println("Result: " + getItemResult);


		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return (getItemResult.getItem() != null);
	}

	public static void persistDemandMessageInDB (String json) throws Exception {

		try {
			// Add a demand message
			if(json != null && !(json.isEmpty())) {
				Map<String, AttributeValue> demand = newItem(json);
				PutItemRequest putDemandRequest = new PutItemRequest(demandTable, demand);
				PutItemResult putDemandResult = dynamoDB.putItem(putDemandRequest);
				System.out.println("Demand Message: " + json + " added in Demand table");
			} else {
				System.out.println("Json Message is either null or empty");
			}

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public static void addProducts() throws Exception {

		try {			// Add a products
			for (int i=1; i<10; i++) {
				Map<String, AttributeValue> item = newItem(Integer.toString(i));
				PutItemRequest putItemRequest = new PutItemRequest(productTable, item);
				PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
	
	private static Map<String, AttributeValue> newItem(String name) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("name", new AttributeValue(name));       
		return item;
	}

}
