package cctv.video.service.service;

import cctv.video.service.domain.Video;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

    private static Map<String, AttributeValue> createItem(Video video) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.builder().s(video.uuid().toString()).build());
        item.put("Title", AttributeValue.builder().s(video.title()).build());
        item.put("Description", AttributeValue.builder().s(video.description()).build());
        item.put("Tags", AttributeValue.builder().ss(video.tags()).build());
        item.put("CreationDateTime", AttributeValue.builder().s(video.creationDate().toString()).build());
        item.put("Uploader", AttributeValue.builder().s(video.uploader()).build());
        return item;
    }

    public void storeVideo(Video video) {
        Map<String, AttributeValue> item = createItem(video);

        PutItemRequest request = PutItemRequest.builder()
                .tableName("CctvVideo")
                .item(item)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            PutItemResponse result = dynamoDbClient.putItem(request);
            LOGGER.info("Response from DynamoDB putItem: {}", result);
        }

    }
}
