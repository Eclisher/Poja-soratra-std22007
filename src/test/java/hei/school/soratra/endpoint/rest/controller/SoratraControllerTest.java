package hei.school.soratra.endpoint.rest.controller;

import hei.school.soratra.endpoint.rest.controller.health.SoratraController;
import hei.school.soratra.file.BucketComponent;
import hei.school.soratra.repository.model.ListURL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SoratraControllerTest {

    @Mock
    private BucketComponent bucketComponent;

    @InjectMocks
    private SoratraController soratraController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetSoratra_Success() throws IOException {
        when(bucketComponent.presign(any(), any())).thenReturn(new java.net.URL("https://original.url"), new java.net.URL("https://transformed.url"));

        ResponseEntity<ListURL> responseEntity = soratraController.convertSoratra("test_id");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetSoratra_Failure() throws IOException {
        when(bucketComponent.presign(any(), any())).thenThrow(new RuntimeException("Failed to generate presigned URL"));

        ResponseEntity<ListURL> responseEntity = soratraController.convertSoratra("test_id");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }
}
