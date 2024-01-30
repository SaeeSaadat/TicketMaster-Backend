package tech.ayot.ticket.backend.dto.s3.request;

import jakarta.validation.constraints.NotBlank;

public record PreSignedUrlRequest(
    @NotBlank String bucketName,
    @NotBlank String objectName
) {
    public PreSignedUrlRequest(@NotBlank String bucketName, @NotBlank String objectName) {
        this.bucketName = bucketName;
        this.objectName = objectName;
    }
}
