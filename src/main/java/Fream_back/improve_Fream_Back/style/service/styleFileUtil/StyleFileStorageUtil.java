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
     * 임시 파일 저장 메서드
     * 업로드된 MultipartFile을 임시 경로에 저장하고 경로를 반환합니다.
     *
     * @param file 업로드된 이미지 또는 동영상 파일
     * @return 저장된 임시 파일 경로
     * @throws IOException 파일 저장 실패 시 예외
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
     * 실제 파일 저장 메서드
     * 임시 파일을 실제 경로로 이동합니다.
     *
     * @param tempFilePath 임시 파일 경로
     * @param styleId      스타일 ID
     * @return 최종 저장된 파일 경로
     * @throws IOException 파일 이동 실패 시 예외
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
     * 파일 삭제 메서드
     * 주어진 경로의 파일을 삭제합니다.
     *
     * @param filePath 삭제할 파일의 경로
     * @throws IOException 파일 삭제 실패 시 예외
     */
    public void deleteFile(String filePath) throws IOException {
        Path fileToDelete = Paths.get(filePath);
        Files.deleteIfExists(fileToDelete);
    }
}
