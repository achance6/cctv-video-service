package cctv.video.service.service;

import cctv.video.service.constraints.NullOrNotBlank;
import cctv.video.service.domain.Video;
import cctv.video.service.mapper.VideoMapper;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;


@Singleton
public class VideoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);
    private static final String DYNAMODB_TABLE_NAME = "CctvVideo";
    private final VideoMapper videoMapper;
    private final DynamoDbClient dynamoDbClient;

    public VideoService(VideoMapper videoMapper, DynamoDbClient dynamoDbClient) {
        this.videoMapper = videoMapper;
        this.dynamoDbClient = dynamoDbClient;
    }

    public void storeVideo(@NonNull @Valid Video video) {
        Map<String, AttributeValue> item = videoMapper.mapVideoToDynamoDbItem(video);

        PutItemRequest request = PutItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .item(item)
                .build();

        PutItemResponse response = dynamoDbClient.putItem(request);
        LOGGER.info("Response from DynamoDB putItem: {}", response);
    }

    @NonNull
    public Optional<Video> getVideo(@NonNull UUID videoId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.fromS(videoId.toString()));

        GetItemRequest request = GetItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .key(item)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        LOGGER.info("Response from DynamoDB getItem: {}", response);
        if (!response.hasItem()) {
            return Optional.empty();
        }
        var videoItem = response.item();

        return Optional.of(videoMapper.mapDynamoDbItemToVideo(videoItem));
    }

    @NonNull
    public Set<Video> getVideos(
            @NullOrNotBlank String uploader,
            @NullOrNotBlank String search
    ) {

        Map<String, AttributeValue> expressionValues = new HashMap<>();

        ScanRequest.Builder scanRequestBuilder = ScanRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME);

        boolean filtered = false;
        if (uploader != null) {
            scanRequestBuilder.filterExpression("Uploader = :uploader");
            expressionValues.put(":uploader", AttributeValue.fromS(uploader));
            filtered = true;
        }
        if (search != null) {
            scanRequestBuilder.filterExpression("contains(Title,:search)");
            expressionValues.put(":search", AttributeValue.fromS(search));
            filtered = true;
        }
        if (filtered) {
            scanRequestBuilder.expressionAttributeValues(expressionValues);
        }

        var scanRequest = scanRequestBuilder.build();
        ScanResponse response = dynamoDbClient.scan(scanRequest);

        List<Map<String, AttributeValue>> items = response.items();

        return items.stream()
                .map(videoMapper::mapDynamoDbItemToVideo)
                .collect(Collectors.toSet());
    }

    @NonNull
    public SdkHttpResponse deleteVideo(@NonNull UUID videoId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.fromS(videoId.toString()));

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(DYNAMODB_TABLE_NAME)
                .key(item)
                .build();

        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);
        LOGGER.info("Response from DynamoDB delete: {}", deleteItemResponse);
        return deleteItemResponse.sdkHttpResponse();
    }

    @NonNull
    public Optional<Video> incrementVideoView(@NonNull UUID videoId) {
        var videoOptional = getVideo(videoId);
        if (videoOptional.isEmpty()) {
            return Optional.empty();
        }
        var video = videoOptional.get();
        video = new Video(video.videoId(), video.title(), video.description(), video.tags(), video.creationDateTime(), video.uploader(),
                video.viewCount() + 1);
        storeVideo(video);
        return Optional.of(video);
    }
}
