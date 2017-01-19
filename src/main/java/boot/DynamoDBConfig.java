package boot;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.util.*;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.*;
import com.amazonaws.regions.*;

import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DynamoDBConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
 
    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;
 
    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;
 
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDBClient amazonDynamoDB 
          = new AmazonDynamoDBClient(amazonAWSCredentials());
        
        amazonDynamoDB.withRegion(Regions.US_EAST_1); 

        if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
            amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
        }
         
        return amazonDynamoDB;
    }
 
    @Bean
    public AWSCredentials amazonAWSCredentials() {
        // use ~/.aws/credentials file as the following format
        // [default]
        // aws_access_key_id=id
        // aws_secret_access_key=secrect
        return new ProfileCredentialsProvider().getCredentials();

        // use id/secrect pair in the application
        // return new BasicAWSCredentials(
        //   amazonAWSAccessKey, amazonAWSSecretKey);
    }

    @Bean
    public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB amazonDynamoDB) {

        log.trace("Entering dynamoDbMapper()");
        return new DynamoDBMapper(amazonDynamoDB);
    }    
}