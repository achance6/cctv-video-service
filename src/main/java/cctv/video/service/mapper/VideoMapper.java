package cctv.video.service.mapper;

import cctv.video.service.domain.Video;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class VideoMapper {

    public Video mapDynamoDbItemToVideo(Map<String, AttributeValue> item) {
        return new Video(
                UUID.fromString(item.get("VideoId").s()),
                item.get("Title").s(),
                item.get("Description").s(),
                item.get("Tags").ss(),
                LocalDateTime.parse(item.get("CreationDateTime").s()),
                item.get("Uploader").s(),
                Integer.parseInt(item.get("ViewCount").n())
        );
    }

    public Map<String, AttributeValue> mapVideoToDynamoDbItem(Video video) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.builder().s(video.uuid().toString()).build());
        item.put("Title", AttributeValue.builder().s(video.title()).build());
        item.put("Description", AttributeValue.builder().s(video.description()).build());
        item.put("Tags", AttributeValue.builder().ss(video.tags()).build());
        item.put("CreationDateTime", AttributeValue.builder().s(video.creationDate().toString()).build());
        item.put("Uploader", AttributeValue.builder().s(video.uploader()).build());
        item.put("ViewCount", AttributeValue.fromN(String.valueOf(video.viewCount())));
        return item;
    }
}
