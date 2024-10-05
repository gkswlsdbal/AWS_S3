package greed.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ImageDTO {

    @Data
    @Builder
    public static class UploadResponse{
        private String filename;
        private String path;
        private String date;

    }

    @Data
    @Builder
    public static class ImagesResponse{
        private List<Image> images;

    }

    @Data
    @AllArgsConstructor
    public static class Image{
        private String filename;
        private String url;
        private String uploadDate;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteRequest{
        private long memberId;
        private String filename;
    }

    @Data
    @Builder
    public static class DeleteResponse{
        private String filename;
        private Boolean success;
        @JsonProperty(defaultValue = "")
        private String message;
    }
}
