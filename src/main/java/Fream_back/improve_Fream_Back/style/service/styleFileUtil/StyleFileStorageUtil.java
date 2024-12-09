package Fream_back.improve_Fream_Back.style.service.styleFileUtil;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class StyleFileStorageUtil {

    private static final String STYLE_TEMP_DIR = "styleTemp/";
    private static final String STYLE_IMAGE_DIR = "styleImages/";

    /**
     * [임시 파일 저장 로직]
     * - 업로드된 파일을 고유한 이름으로 임시 저장소에 저장.
     * - 저장된 파일 경로를 반환.
     */
    public String saveTemporaryFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();

        // 파일명에서 확장자 추출
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path tempFilePath = Paths.get( STYLE_TEMP_DIR + uniqueFileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(tempFilePath.getParent())) {
            Files.createDirectories(tempFilePath.getParent());
        }

        // 파일 저장
        file.transferTo(tempFilePath.toFile());
        return  STYLE_TEMP_DIR + uniqueFileName;
    }

    /**
     * [실제 파일 저장 로직]
     * - 임시 파일을 실제 저장소로 이동.
     * - 스타일 ID와 UUID를 사용해 고유 파일명을 생성 후 저장.
     * - 최종 저장된 파일 경로를 반환.
     */
    public String moveToPermanentStorage(String tempFilePath, Long styleId) throws IOException {
        // tempFilePath null 체크
        if (tempFilePath == null || tempFilePath.isBlank()) {
            throw new IllegalArgumentException("tempFilePath cannot be null or empty.");
        }

        // 파일명에서 확장자 추출
        String extension = "";
        if (tempFilePath.contains(".")) {
            extension = tempFilePath.substring(tempFilePath.lastIndexOf("."));
        }

        String uniqueFileName = "style_" + styleId + "_" + UUID.randomUUID() + extension;
        Path permanentFilePath = Paths.get( STYLE_IMAGE_DIR + uniqueFileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(permanentFilePath.getParent())) {
            Files.createDirectories(permanentFilePath.getParent());
        }

        // 파일 이동
        Path tempPath = Paths.get(tempFilePath);
        Files.move(tempPath, permanentFilePath, StandardCopyOption.REPLACE_EXISTING);

        return  STYLE_IMAGE_DIR + uniqueFileName;
    }

    /**
     * [파일 삭제 로직]
     * - 지정된 파일 경로의 파일을 삭제.
     * - 파일이 없으면 삭제하지 않고 종료.
     */
    public void deleteFile(String filePath) throws IOException {
        Path fileToDelete = Paths.get(filePath);
        Files.deleteIfExists(fileToDelete);
    }
}
