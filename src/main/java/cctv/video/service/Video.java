package cctv.video.service;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;
import java.util.Date;
import java.util.List;

@Serdeable
public record Video(String uuid, String title, String description, String tags, String creationDate,
                    String uploader) {

}
