package greed.application.controllers;

import greed.domain.dtos.ImageDTO;
import greed.domain.services.ImageService;
import greed.domain.services.ImageServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageDTO.UploadResponse> uploadFile(@RequestParam("member-id") @Positive long memberId,
                                                              @RequestParam("file") MultipartFile multipartFile,
                                                              @RequestParam("path") String path) throws Exception {
        ImageDTO.UploadResponse uploadResponse = imageService.uploadImage(multipartFile, memberId, path);
        return ResponseEntity.ok().body(uploadResponse);
    }

    @GetMapping
    public ResponseEntity<ImageDTO.ImagesResponse> search() throws Exception {
        ImageDTO.ImagesResponse ImagesResponse = imageService.search();
        return ResponseEntity.ok().body(ImagesResponse);
    }

    @DeleteMapping
    public ResponseEntity<ImageDTO.DeleteResponse> delete(@RequestBody ImageDTO.DeleteRequest request) throws Exception {
        ImageDTO.DeleteResponse deleteResponse = imageService.delete(request);
        return deleteResponse.getSuccess() ? ResponseEntity.ok().body(deleteResponse) : ResponseEntity.badRequest().body(deleteResponse);
    }
}
