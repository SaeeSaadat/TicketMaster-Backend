package tech.ayot.ticket.backend.service.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.ayot.ticket.backend.dto.s3.request.PreSignedUrlRequest;

import java.net.URL;
import java.util.Date;

@RestController
@RequestMapping("/api/s3")
public class S3Service {

    private final AmazonS3 s3Client;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    @PostMapping("/pre-sign")
    public ResponseEntity<URL> preSign(@RequestBody @Valid PreSignedUrlRequest request) {
        Date expiration = new Date();
        long msec = expiration.getTime();
        msec += 1000 * 60 * 60; // 1 hour
        expiration.setTime(msec);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(request.bucketName(), request.objectName())
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL presignedURl = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return new ResponseEntity<>(presignedURl, HttpStatus.OK);
    }
}
