package com.bingchunmoli.api.img;

import com.bingchunmoli.api.bean.ApiConstant;
import com.bingchunmoli.api.config.ApiConfig;
import com.bingchunmoli.api.controller.advice.ResponseControllerAdvice;
import com.bingchunmoli.api.img.controller.ImgController;
import com.bingchunmoli.api.img.service.ImgService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImgControllerTest {
    private static final byte[] IMAGE_BYTES = {1, 2, 3, 4};

    @TempDir
    private Path directory;
    private final ImgService service = mock(ImgService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        final ApiConfig config = new ApiConfig();
        config.setMobilePath("mobile");
        config.setPcPath("pc");
        mockMvc = MockMvcBuilders.standaloneSetup(new ImgController(service, config))
                .setControllerAdvice(new ResponseControllerAdvice()).build();
    }

    @ParameterizedTest
    @CsvSource({"jpg,image/jpeg", "png,image/png", "webp,image/webp"})
    void streamsOriginalMobileFileWithMatchingContentType(final String extension, final String contentType) {
        final File image = createImage(extension);
        when(service.getRandomImageFile(ApiConstant.REDIS_MOBILE_IMG_KEY, "mobile")).thenReturn(image);

        assertDoesNotThrow(() -> mockMvc.perform(get("/img/mobile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().bytes(IMAGE_BYTES)));
        verify(service).getRandomImageFile(ApiConstant.REDIS_MOBILE_IMG_KEY, "mobile");
        verifyNoMoreInteractions(service);
    }

    @Test
    void streamsPcFile() {
        when(service.getRandomImageFile(ApiConstant.REDIS_PC_IMG_KEY, "pc")).thenReturn(createImage("jpg"));

        assertDoesNotThrow(() -> mockMvc.perform(get("/img/pc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(IMAGE_BYTES)));
    }

    @ParameterizedTest
    @CsvSource({"'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) Mobile',mobile",
            "'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',pc"})
    void selectsFileUsingUserAgent(final String userAgent, final String path) {
        final String key = "mobile".equals(path) ? ApiConstant.REDIS_MOBILE_IMG_KEY : ApiConstant.REDIS_PC_IMG_KEY;
        when(service.getRandomImageFile(key, path)).thenReturn(createImage("jpg"));

        assertDoesNotThrow(() -> mockMvc.perform(get("/img").header("userAgent", userAgent))
                .andExpect(status().isOk()).andExpect(content().bytes(IMAGE_BYTES)));
        verify(service).getRandomImageFile(key, path);
    }

    @Test
    void rejectsMissingRequiredUserAgent() {
        assertDoesNotThrow(() -> mockMvc.perform(get("/img")).andExpect(status().isBadRequest()));
        verifyNoInteractions(service);
    }

    private File createImage(final String extension) {
        return assertDoesNotThrow(() -> Files.write(directory.resolve("image." + extension), IMAGE_BYTES).toFile());
    }
}
