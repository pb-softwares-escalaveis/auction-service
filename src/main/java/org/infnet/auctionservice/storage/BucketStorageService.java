package org.infnet.auctionservice.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BucketStorageService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteImage(String id);
}
