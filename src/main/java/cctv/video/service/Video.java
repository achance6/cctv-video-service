package cctv.video.service;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;
import java.util.Date;
import java.util.List;

@Serdeable
public record Video(UUID uuid, String title, String description, List<String> tags, Date creationDate,
                    String uploader) {

}
