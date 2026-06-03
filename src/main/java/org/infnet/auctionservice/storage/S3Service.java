package org.infnet.auctionservice.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements BucketStorageService {
    private final S3Client s3Client;

    @Value("${spring.s3.bucket-name}")
    private String bucketName;
    @Value("${spring.s3.endpoint}")
    private String endpoint;

    @Override
    public void deleteImage(String id) {
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + ".jpg";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("%s/%s/%s", endpoint, bucketName, fileName);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload para o S3/MinIO", e);
        }
    }
}
