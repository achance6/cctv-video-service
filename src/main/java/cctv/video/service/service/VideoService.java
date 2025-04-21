package cctv.video.service.service;

import cctv.video.service.domain.Video;
import cctv.video.service.mapper.VideoMapper;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;


@Singleton
public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);
    private static final String DYNAMODB_TABLE_NAME = "CctvVideo";
    private final VideoMapper videoMapper;

    public VideoService(VideoMapper videoMapper) {
        this.videoMapper = videoMapper;
    }

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

            return videoMapper.mapDynamoDbItemToVideo(videoItem);
        }
    }

    public Set<Video> getVideos(@Nullable String uploader) {
        Set<Video> videos = new HashSet<>();

        ScanRequest.Builder scanRequestBuilder = ScanRequest.builder().tableName(DYNAMODB_TABLE_NAME);
        ScanRequest scanRequest;
        if (uploader == null || uploader.isBlank()) {
            scanRequest = scanRequestBuilder.build();
        } else {
            scanRequest = scanRequestBuilder
                    .filterExpression("Uploader = :uploader")
                    .expressionAttributeValues(Map.of(":uploader", AttributeValue.fromS(uploader)))
                    .build();
        }

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            LOGGER.info("Response from DynamoDB scan: {}", scanResponse);

            for (Map<String, AttributeValue> item : scanResponse.items()) {
                videos.add(videoMapper.mapDynamoDbItemToVideo(item));
            }
        }

        return videos;
    }

    public SdkHttpResponse deleteVideo(UUID videoId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.fromS(videoId.toString()));

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .key(item)
                .build();

        try (DynamoDbClient dynamoDbClient = DynamoDbClient.create()) {
            DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);
            LOGGER.info("Response from DynamoDB delete: {}", deleteItemResponse);
            return deleteItemResponse.sdkHttpResponse();
        }
    }
}
