package com.shopping.comm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GcsService {

    // 아까 yml에 적어둔 버킷 이름을 가져옵니다.
    @Value("${gcp.bucket.name}")
    private String bucketName;

    public String uploadImage(MultipartFile file) throws IOException {
        // 1. 우리가 resources 폴더에 넣어둔 'gcp-key.json' 마스터키를 꺼내서 구글에 인증!
        ClassPathResource resource = new ClassPathResource("gcp-key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        // 2. 파일 이름 중복 방지 (똑같은 이름의 사진이 올라오면 덮어씌워지는 걸 막기 위해 UUID 사용)
        // 예: "df82a1..._설아티셔츠.png"
        String uuid = UUID.randomUUID().toString();
        String ext = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // 구글 클라우드 안의 'items' 라는 폴더 안에 저장되도록 경로 설정
        String blobName = "items/" + uuid + "_" + originalFilename;

        // 3. 사진을 구글 클라우드 스토리지로 전송 🚀
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, blobName)
                .setContentType(ext)
                .build();
        storage.create(blobInfo, file.getInputStream());

        // 4. 업로드 완료 후, 누구나 볼 수 있는 '절대 주소(URL)'를 만들어서 반환!
        return "https://storage.googleapis.com/" + bucketName + "/" + blobName;
    }

    // 🚀 [추가] GCS 클라우드에 있는 사진 삭제 로직
    public void deleteImage(String imgUrl) throws IOException {
        // 1. 방어 로직: URL이 비어있으면 그냥 패스!
        if (imgUrl == null || imgUrl.isEmpty()) return;

        // 2. 전체 URL에서 도메인과 버킷 이름을 잘라내고 '진짜 파일 경로'만 추출
        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        if (!imgUrl.startsWith(prefix)) {
            System.out.println("⚠️ GCS URL 형식이 아닙니다. 삭제 건너뜀: " + imgUrl);
            return; // 우리가 올린 구글 클라우드 주소가 아니면(예: 기본이미지) 지우면 안 돼!
        }
        String blobName = imgUrl.substring(prefix.length()); // 예: "items/uuid_설아티셔츠.png"

        // 3. 구글 클라우드 마스터키 인증 (업로드 때랑 동일)
        ClassPathResource resource = new ClassPathResource("gcp-key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        // 4. 구글 클라우드에 삭제 요청! 💥
        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        
        if (deleted) {
            System.out.println("✅ GCS 이미지 삭제 완벽 성공: " + blobName);
        }
    }
}