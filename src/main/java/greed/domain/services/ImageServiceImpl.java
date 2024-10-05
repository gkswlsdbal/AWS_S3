package greed.domain.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import greed.application.exceptions.CustomException;
import greed.application.exceptions.code.ImageErrorCode;
import greed.common.enums.FileType;
import greed.domain.dtos.ImageDTO;
import greed.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;


    @Override
    public ImageDTO.UploadResponse uploadImage(MultipartFile multipartFile, long memberId, String path) throws Exception {

        if (multipartFile.getSize() > MAX_FILE_SIZE) { // 파일 크기 유효성 검사
           throw new CustomException(ImageErrorCode.FILE_SIZE_EXCEED);
        }

        String filename = multipartFile.getOriginalFilename();
        String extension = FileUtils.getFileExtension(filename);
        FileType.validate(FileType.IMAGE, extension);  // 파일 확장자 유효성 검사

        String filePath = makeBucketFilePath(String.valueOf(memberId), path, filename);
        File file = convertMultiToFile(multipartFile);
        s3Client.putObject(new PutObjectRequest(bucketName, filePath, file));
        String s3Path = makeBucketFilePath(bucketName, filePath);

        LocalDateTime date = LocalDateTime.now();

        System.out.println("Uploaded image: " + filename);
        System.out.println("S3 Path: " + s3Path);
        System.out.println("Upload Date: " + formatDate(date));

        file.delete();
        return ImageDTO.UploadResponse.builder()
                                      .filename(filename)
                                      .path(s3Path)
                                      .date(formatDate(date))
                                      .build();
    }

    @Override
    public ImageDTO.ImagesResponse search() {
        List<ImageDTO.Image> imageList = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects(bucketName);

        if (objectListing.getObjectSummaries().isEmpty()) // s3 버킷 비어있거나 정보를 못가져왔을 때의 유효성 검사
            return ImageDTO.ImagesResponse.builder().images(imageList).build();

        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
                String filename = os.getKey();
                String uploadDate = formatDate(LocalDateTime.ofInstant(os.getLastModified().toInstant(), ZoneId.systemDefault()));
                String fileUrl = s3Client.getUrl(bucketName, filename).toString();
                ImageDTO.Image image = new ImageDTO.Image(filename, fileUrl, uploadDate);
                imageList.add(image);
        }
        ImageDTO.ImagesResponse.builder().images(imageList).build();

        return  ImageDTO.ImagesResponse.builder().images(imageList).build();
    }

    @Override
    public ImageDTO.DeleteResponse delete(ImageDTO.DeleteRequest request) {
        long id = request.getMemberId();
        String filename = request.getFilename();
        if ( id <= 0 ||filename.isBlank()) {    //사용자 id 및 파일 이름 유효성 검사
            return ImageDTO.DeleteResponse.builder().filename(filename)
                                                    .success(false)
                                                    .message("Filename is required or ID value is invalid.")
                                                    .build();
        }

        try {
            // S3에서 파일 삭제
            s3Client.deleteObject(bucketName, makeBucketFilePath(String.valueOf(request.getMemberId()), request.getFilename()));
            return ImageDTO.DeleteResponse.builder().filename(filename)
                                                    .success(true)
                                                    .message("success")
                                                    .build();
        } catch (AmazonS3Exception e) {
            // 삭제 실패 시 예외 처리
            System.out.println(e.getMessage());
            return ImageDTO.DeleteResponse.builder().filename(filename)
                                                    .success(false)
                                                    .message("Failed to delete file")
                                                    .build();
        }
    }

    private File convertMultiToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        return file;
    }

    private String makeBucketFilePath(String... args) {
        return String.join("/", args);
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }
}
