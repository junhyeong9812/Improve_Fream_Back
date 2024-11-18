package Fream_back.improve_Fream_Back.product.service.fileStorageUtil;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final String TEMP_DIR = "temp/";
    private static final String IMAGE_DIR = "images/";

    /**
     * 임시 파일 저장 메서드
     * 업로드된 MultipartFile을 임시 경로에 저장하고 경로를 반환합니다.
     *
     * @param file 업로드된 이미지 파일
     * @return 저장된 임시 파일 경로
     * @throws IOException 파일 저장 실패 시 예외
     */
    public String saveTemporaryFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path tempFilePath = Paths.get(TEMP_DIR + uniqueFileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(tempFilePath.getParent())) {
            Files.createDirectories(tempFilePath.getParent());
        }

        // 파일 저장
        file.transferTo(tempFilePath.toFile());

        return TEMP_DIR + uniqueFileName;
    }

    /**
     * 실제 파일 저장 메서드
     * 임시 파일을 실제 경로로 이동합니다.
     *
     * @param tempFilePath 임시 파일 경로
     * @param productId    상품 ID
     * @return 최종 저장된 파일 경로
     * @throws IOException 파일 이동 실패 시 예외
     */
    public String moveToPermanentStorage(String tempFilePath, Long productId) throws IOException {
        String uniqueFileName = "product_" + productId + "_" + UUID.randomUUID() + ".jpg";
        Path permanentFilePath = Paths.get(IMAGE_DIR + uniqueFileName);

        // 디렉토리가 없으면 생성
        if (!Files.exists(permanentFilePath.getParent())) {
            Files.createDirectories(permanentFilePath.getParent());
        }

        // 파일 이동
        Path tempPath = Paths.get(tempFilePath);
        Files.move(tempPath, permanentFilePath);

        return IMAGE_DIR + uniqueFileName;
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
