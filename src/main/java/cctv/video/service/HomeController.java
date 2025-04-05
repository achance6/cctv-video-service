package cctv.video.service;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Get
    public Map<String, Object> index() {
        return Collections.singletonMap("message", "Hello World");
    }

    @Post("/video")
    public String storeVideo(@Body Video video) {
        LOGGER.info("Received /video request with video: {}", video);
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(video.uuid().toString()).build());
        item.put("title", AttributeValue.builder().s(video.title()).build());
        item.put("description", AttributeValue.builder().s(video.description()).build());
        item.put("tags", AttributeValue.builder().ss(video.tags()).build());
        item.put("create-date", AttributeValue.builder().s(video.creationDate().toString()).build());
        item.put("uploader", AttributeValue.builder().s(video.uploader()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("cctv-video-data")
                .item(item)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            PutItemResponse result = dynamoDbClient.putItem(request);
            LOGGER.info("Response from DynamoDB putItem: {}", result);
        }

        return HttpStatus.CREATED.name();
    }
}
