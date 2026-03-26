package com.shopping.comm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class GcsService {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    // yml 파일에 설정한 credentials.base64 값을 가져옵니다.
    @Value("${gcp.credentials.base64}")
    private String gcpKeyBase64;

    // 🚀 [핵심] Base64 문자열을 디코딩하여 Storage 객체를 생성하는 공통 메서드
    private Storage getStorage() throws IOException {
        // 1. Base64로 인코딩된 GCP 키 문자열을 바이트 배열로 디코딩
        byte[] decodedKey = Base64.getDecoder().decode(gcpKeyBase64);

        // 2. 바이트 배열을 InputStream으로 변환하여 구글 인증에 사용
        ByteArrayInputStream keyStream = new ByteArrayInputStream(decodedKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream);

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // 공통 메서드를 호출하여 인증된 Storage 객체를 가져옵니다.
        Storage storage = getStorage();

        String uuid = UUID.randomUUID().toString();
        String ext = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        String blobName = "items/" + uuid + "_" + originalFilename;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName)
                .setContentType(ext)
                .build();
        storage.create(blobInfo, file.getInputStream());

        return "https://storage.googleapis.com/" + bucketName + "/" + blobName;
    }

    public void deleteImage(String imgUrl) throws IOException {
        if (imgUrl == null || imgUrl.isEmpty()) return;

        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        if (!imgUrl.startsWith(prefix)) {
            System.out.println("⚠️ GCS URL 형식이 아닙니다. 삭제 건너뜀: " + imgUrl);
            return;
        }
        String blobName = imgUrl.substring(prefix.length());

        // 공통 메서드를 호출하여 인증된 Storage 객체를 가져옵니다.
        Storage storage = getStorage();

        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        
        if (deleted) {
            System.out.println("✅ GCS 이미지 삭제 완벽 성공: " + blobName);
        }
    }
}