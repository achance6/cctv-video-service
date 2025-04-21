package cctv.video.service.controller;

import cctv.video.service.domain.Video;
import cctv.video.service.service.VideoService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

@Controller("/video")
public class VideoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Get("/{videoId}")
    public HttpResponse<Video> getVideo(@PathVariable @NonNull String videoId) {
        LOGGER.info("Received /video GET request with videoId {}", videoId);
        var video = videoService.getVideo(UUID.fromString(videoId));
        return HttpResponse.ok(video);
    }

    @Delete("/{videoId}")
    public HttpResponse<String> deleteVideo(@PathVariable @NonNull String videoId) {
        LOGGER.info("Received /video DELETE request with videoId {}", videoId);
        var sdkHttpResponse = videoService.deleteVideo(UUID.fromString(videoId));
        if (sdkHttpResponse.isSuccessful()) {
            return HttpResponse.ok("Video with id " + videoId + " deleted");
        } else {
            return HttpResponse.serverError(sdkHttpResponse.toString());
        }

    }

    @Get("/videos")
    public HttpResponse<Set<Video>> getVideos(@QueryValue @Nullable String uploader) {
        LOGGER.info("Received /video/videos GET request");
        var videos = videoService.getVideos(uploader);
        return HttpResponse.ok(videos);
    }

    @Post
    public HttpResponse<Video> storeVideo(@Body Video video) {
        LOGGER.info("Received /video request with video: {}", video);
        try {
            videoService.storeVideo(video);
        } catch (Exception e) {
            LOGGER.error("Error in storeVideo :: ", e);
            return HttpResponse.serverError();
        }
        return HttpResponse.created(video);
    }
}
