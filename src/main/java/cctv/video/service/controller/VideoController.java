package cctv.video.service.controller;

import cctv.video.service.domain.Video;
import cctv.video.service.service.VideoService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/video")
public class VideoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

    private final VideoService videoService;

    @Inject
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Get
    public String getVideo() {
        return "";
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
