package cctv.video.service.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Serdeable
@Introspected
public record Video(
        @NonNull UUID videoId,
        @NonNull @NotBlank String title,
        @Nullable @NotBlank String description,
        @Nullable List<@NotBlank String> tags,
        @NonNull LocalDateTime creationDateTime,
        @NonNull @NotBlank String uploader,
        @NonNull @PositiveOrZero Integer viewCount
) {

}
