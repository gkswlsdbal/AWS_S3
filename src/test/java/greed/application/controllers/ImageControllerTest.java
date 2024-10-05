package greed.application.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.fasterxml.jackson.databind.ObjectMapper;
import greed.domain.dtos.ImageDTO;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    @Order(1)
    public void notExtension() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/bird.jpg"));
        MockMultipartFile multipartFile = new MockMultipartFile("file", "bird", MediaType.IMAGE_JPEG_VALUE, content);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/images/upload")
                .file(multipartFile)
                .param("member-id","1")
                .param("path","/upload/path")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void extensionCheck() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/bird.jpg"));
        MockMultipartFile multipartFile = new MockMultipartFile("file", "bird.pdf", MediaType.IMAGE_JPEG_VALUE, content);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/images/upload")
                .file(multipartFile)
                .param("member-id","1")
                .param("path","/upload/path")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(3)
    public void fileSizeExceedCheck() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/LargeImage.jpg"));
        MockMultipartFile multipartFile = new MockMultipartFile("file", "large.jpg", MediaType.IMAGE_JPEG_VALUE, content);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/images/upload")
                .file(multipartFile)
                .param("member-id","1234")
                .param("path","user_uploads")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(4)
    public void upload() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/resources/bird.jpg")); // 파일 경로 수정
        MockMultipartFile multipartFile = new MockMultipartFile("file", "bird.jpg", MediaType.IMAGE_JPEG_VALUE, content);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/images/upload")
                .file(multipartFile)
                .param("member-id","1234")
                .param("path","user_uploads")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(5)
    public void  search() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(6)
    public void  delete() throws Exception {
        ImageDTO.DeleteRequest request = new ImageDTO.DeleteRequest(1234,"user_uploads/bird.jpg");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(7)
    public void  blankByDelete() throws Exception {
        ImageDTO.DeleteRequest request = new ImageDTO.DeleteRequest(1234," ");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(8)
    public void  memberIdErrorByDelete() throws Exception {
        ImageDTO.DeleteRequest request = new ImageDTO.DeleteRequest(-1,"user_uploads/bird.jpg");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print());
    }


}