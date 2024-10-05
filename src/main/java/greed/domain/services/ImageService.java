package greed.domain.services;

import greed.domain.dtos.ImageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageDTO.UploadResponse uploadImage(MultipartFile multipartFile, long memberId, String path) throws Exception;

    ImageDTO.ImagesResponse search() throws Exception;

    ImageDTO.DeleteResponse delete(ImageDTO.DeleteRequest request) throws Exception;

}
