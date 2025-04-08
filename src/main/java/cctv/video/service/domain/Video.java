package cctv.video.service.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Serdeable
public record Video(@NonNull UUID uuid, @NonNull String title, String description, List<String> tags,
                    @NonNull LocalDateTime creationDate,
                    @NonNull String uploader) {

}
