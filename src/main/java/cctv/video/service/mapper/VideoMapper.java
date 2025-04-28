package cctv.video.service.mapper;

import cctv.video.service.domain.Video;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class VideoMapper {

    @NonNull
    public Video mapDynamoDbItemToVideo(@NonNull @NotEmpty Map<String, AttributeValue> item) {
        try {
            return new Video(
                    UUID.fromString(item.get("VideoId").s()),
                    item.get("Title").s(),
                    item.get("Description").s(),
                    item.get("Tags").ss(),
                    LocalDateTime.parse(item.get("CreationDateTime").s()),
                    item.get("Uploader").s(),
                    Integer.parseInt(item.get("ViewCount").n())
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Item input can't be mapped to a Video");
        }
    }

    @NonNull
    @NotEmpty
    public Map<String, AttributeValue> mapVideoToDynamoDbItem(@NonNull Video video) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("VideoId", AttributeValue.fromS(video.videoId().toString()));
        item.put("Title", AttributeValue.fromS(video.title()));
        item.put("Description", AttributeValue.fromS(video.description()));
        item.put("Tags", AttributeValue.fromSs(video.tags()));
        item.put("CreationDateTime", AttributeValue.fromS(video.creationDateTime().toString()));
        item.put("Uploader", AttributeValue.fromS(video.uploader()));
        item.put("ViewCount", AttributeValue.fromN(String.valueOf(video.viewCount())));
        return item;
    }
}
