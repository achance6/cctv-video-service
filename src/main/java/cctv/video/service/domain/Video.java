package cctv.video.service.domain;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Serdeable
public record Video(UUID uuid, String title, String description, List<String> tags, Date creationDate,
                    String uploader) {

}
