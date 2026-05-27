package org.infnet.auctionservice.storage;

import org.springframework.web.multipart.MultipartFile;

public interface BucketStorageService {
    String uploadImage(MultipartFile file) throws Exception;
    void deleteImage(String id);
}
