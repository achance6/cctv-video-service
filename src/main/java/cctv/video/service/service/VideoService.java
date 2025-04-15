package cctv.video.service.service;

import cctv.video.service.domain.Video;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.*;

@Singleton
public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);
    public static final String DYNAMODB_TABLE_NAME = "CctvVideo";

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
                .tableName(DYNAMODB_TABLE_NAME)
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
                .tableName(DYNAMODB_TABLE_NAME)
                .key(item)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            GetItemResponse response = dynamoDbClient.getItem(request);
            LOGGER.info("Response from DynamoDB getItem: {}", response);
            if (!response.hasItem()) {
                return null;
            }
            var videoItem = response.item();

            return mapDynamoDbItemToVideo(videoItem);
        }
    }

    public Set<Video> getVideos() {
        Set<Video> videos = new HashSet<>();

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            LOGGER.info("Response from DynamoDB scan: {}", scanResponse);

            for (Map<String, AttributeValue> item : scanResponse.items()) {
                videos.add(mapDynamoDbItemToVideo(item));
            }
        }

        return videos;
    }

    private Video mapDynamoDbItemToVideo(Map<String, AttributeValue> item) {
        return new Video(
                UUID.fromString(item.get("VideoId").s()),
                item.get("Title").s(),
                item.get("Description").s(),
                item.get("Tags").ss(),
                LocalDateTime.parse(item.get("CreationDateTime").s()),
                item.get("Uploader").s()
        );
    }

}
