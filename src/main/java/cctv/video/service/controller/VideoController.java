package cctv.video.service.controller;

import cctv.video.service.domain.Video;
import cctv.video.service.service.VideoService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller("/video")
public class VideoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

    private final VideoService videoService;

    @Inject
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Get("/{videoId}")
    public Video getVideo(@PathVariable @NonNull String videoId) {
        LOGGER.info("Received /video GET request with videoId {}", videoId);
        return videoService.getVideo(UUID.fromString(videoId));
    }

    @Post
    public String storeVideo(@Body Video video) {
        LOGGER.info("Received /video request with video: {}", video);
        try {
            videoService.storeVideo(video);
        } catch (Exception e) {
            LOGGER.error("Error in storeVideo :: ", e);
            return HttpStatus.INTERNAL_SERVER_ERROR.name();
        }
        return HttpStatus.CREATED.name();
    }
}
