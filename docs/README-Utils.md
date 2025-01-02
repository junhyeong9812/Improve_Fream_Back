# 유틸파일 내용 정리  

## 1. 유틸리티 파일 개요
- Utils는 프로젝트에서 공통적으로 사용되는 기능을 모듈화하여 제공합니다.
- 보안, 파일 처리, 랜덤 비밀번호 생성 등 여러 기능을 포함하고 있습니다.
- 개발 및 유지보수의 편리성을 위해 각 유틸리티 파일은 특정 목적에 따라 설계되었습니다.

## 2. 유틸리티 파일 설명

### 2.1 SecurityUtils
- **위치:** Fream_back.improve_Fream_Back.utils.SecurityUtils
- **목적:** Spring Security의 SecurityContext에서 인증된 사용자의 이메일 정보를 추출.
- **주요 기능:**
  - 인증된 사용자의 이메일 반환.
  - 인증 정보가 없을 경우 예외 발생.
- **코드 예시**
    ```java
    public class SecurityUtils {
        public static String extractEmailFromSecurityContext() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof String) {
                return (String) authentication.getPrincipal();
            }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }
    }
    ```
  - 사용방법
      ```
      java
      String email = SecurityUtils.extractEmailFromSecurityContext();
      ```
### 2.2 PasswordUtils
- **위치:** Fream_back.improve_Fream_Back.utils.PasswordUtils
- **목적:** 랜덤 비밀번호를 생성.
- **주요 기능:**
  - 8자리 랜덤 비밀번호 생성.

- **코드 예시**
    ```java
    public class SecurityUtils {
    public static String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
        }
    }
    ```
- **사용 방법**
    ```java
        String email = SecurityUtils.extractEmailFromSecurityContext();
    ``` 

### 2.3 FileUtils
- **위치:** Fream_back.improve_Fream_Back.utils.FileUtils
- **목적:** 파일 저장, 삭제, 존재 여부 확인 등 파일 관련 작업을 처리.
- **주요 기능:**
  - 파일 저장: 고유 파일명 생성 후 저장.
  - 파일 삭제: 지정된 경로의 파일 삭제.
  - 파일 존재 여부 확인.
- **코드 예시**
    ```java
    public class PasswordUtils {
        public static String generateRandomPassword() {
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return password.toString();
        }
    }
    ```
  - 사용방법
    - 파일 저장
    ```java
        String savedFileName = fileUtils.saveFile("/uploads", "IMG_", multipartFile);
    ```

    - 파일 삭제
    ```java
        boolean deleted = fileUtils.deleteFile("/uploads", "IMG_1234.png");
    ```

### 2.4 FAQ FileStorageUtil
- **위치:** Fream_back.improve_Fream_Back.faq.service.FAQFileStorageUtil
- **목적:** FAQ 관련 파일 저장 및 HTML 이미지 경로 처리.
- **주요 기능:**
    - 다중 파일 저장 및 삭제.
    - HTML 내용 내 이미지 경로 수정 및 추출.
- **코드 예시**
    ```java
        public String saveFile(MultipartFile file) {
        try {
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(FAQ_STORAGE_DIR + uniqueFileName);

            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());

            return FAQ_STORAGE_DIR + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }
    ```

- **사용 방법**
  - 파일 저장
  ```java
        String filePath = faqFileStorageUtil.saveFile(multipartFile);
    ```
  - HTML 이미지 경로 수정
  ```java
        String updatedContent = faqFileStorageUtil.updateImagePaths(htmlContent, filePaths);
    ```


### 2.5 NoticeFileStorageUtil
- **위치:** Fream_back.improve_Fream_Back.notice.service.NoticeFileStorageUtil
- **목적:** Notice(공지사항) 관련 파일 처리.
- **주요 기능:**
  - 단일 및 다중 파일 저장.
  - 파일 삭제 및 기존 이미지 삭제 처리.
  - HTML 이미지 경로 추출 및 수정.

- **코드 예시**
    ```java
    public String saveFile(MultipartFile file) throws IOException {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(NOTICE_STORAGE_DIR + uniqueFileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        file.transferTo(filePath.toFile());
        return NOTICE_STORAGE_DIR + uniqueFileName;
    }

    ```
  - **사용 방법**
  - 파일 저장
  ```java
    String filePath = noticeFileStorageUtil.saveFile(multipartFile);
    ```
  - HTML 이미지 경로 수정
  ```java
    String updatedContent = noticeFileStorageUtil.updateImagePaths(htmlContent, filePaths);
    ```

### 2.6 InspectionFileStorageUtil
- **위치:** Fream_back.improve_Fream_Back.inspection.service.InspectionFileStorageUtil
- **목적:** Inspection(점검) 관련 파일 처리.
- **주요 기능:**
  - 단일 및 다중 파일 저장.
  - 파일 삭제 및 존재 여부 확인.
  - HTML 이미지 경로 추출 및 수정.
- **코드 예시**
    ```java
    public String saveFile(MultipartFile file) throws IOException {
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(INSPECTION_STORAGE_DIR + uniqueFileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        file.transferTo(filePath.toFile());
        return INSPECTION_STORAGE_DIR + uniqueFileName;
    }
    ```
  - 사용 방법
    - 파일 저장
    ```java
        String filePath = inspectionFileStorageUtil.saveFile(multipartFile);
       ```
    - HTML 이미지 경로 수정 
    ```java
        String updatedContent = inspectionFileStorageUtil.updateImagePaths(htmlContent, filePaths);
    ```


## 3. 파일 구조
```text
Fream_back.improve_Fream_Back.utils
├── SecurityUtils.java
├── PasswordUtils.java
├── FileUtils.java
Fream_back.improve_Fream_Back.faq.service
└── FAQFileStorageUtil.java
Fream_back.improve_Fream_Back.notice.service
└── NoticeFileStorageUtil.java
Fream_back.improve_Fream_Back.inspection.service
└── InspectionFileStorageUtil.java
```


