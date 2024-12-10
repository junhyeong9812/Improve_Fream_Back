package Fream_back.improve_Fream_Back.inspection.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class InspectionFileStorageUtil {

    private static final String INSPECTION_STORAGE_DIR = "Inspection/";

    /**
     * 파일 저장
     *
     * @param file 업로드된 MultipartFile 객체
     * @return 저장된 파일 경로
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(INSPECTION_STORAGE_DIR + uniqueFileName);

        // 디렉토리가 존재하지 않을 경우 생성
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        // 파일 저장
        file.transferTo(filePath.toFile());
        return INSPECTION_STORAGE_DIR + uniqueFileName;
    }

    /**
     * 파일 삭제
     *
     * @param filePath 삭제할 파일 경로
     * @throws IOException 파일 삭제 중 오류 발생 시
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}