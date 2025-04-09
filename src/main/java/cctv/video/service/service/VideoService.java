package cctv.video.service.service;

import cctv.video.service.domain.Video;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            PutItemResponse response = dynamoDbClient.putItem(request);
            LOGGER.info("Response from DynamoDB putItem: {}", response);
        }
    }

    public Video getVideo(UUID videoId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.fromS(videoId.toString()));

        GetItemRequest request = GetItemRequest.builder()
                .tableName("CctvVideo")
                .key(item)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            GetItemResponse response = dynamoDbClient.getItem(request);
            LOGGER.info("Response from DynamoDB getItem: {}", response);
            if (!response.hasItem()) {
                return null;
            }
            var videoItem = response.item();

            return new Video(UUID.fromString(videoItem.get("VideoId").s()), videoItem.get("Title").s(), videoItem.get("Description").s(), videoItem.get("Tags").ss(), LocalDateTime.parse(videoItem.get("CreationDateTime").s()), videoItem.get("Uploader").s());
        }
    }
}
