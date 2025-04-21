package cctv.video.service.mapper;

import cctv.video.service.domain.Video;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
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
                item.get("Uploader").s()
        );
    }
}
