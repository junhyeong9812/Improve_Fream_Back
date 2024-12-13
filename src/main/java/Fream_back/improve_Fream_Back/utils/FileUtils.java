package Fream_back.improve_Fream_Back.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUtils {

    // 파일 저장
    public String saveFile(String directory, String fileName, MultipartFile file) throws IOException {
        // 경로 확인 및 디렉토리 생성
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 고유 파일명 생성
        String uniqueFileName = generateUniqueFileName(fileName);

        // 파일 저장
        String fullPath = directory + File.separator + uniqueFileName;
        file.transferTo(new File(fullPath));

        return uniqueFileName; // 저장된 파일명 반환
    }

    // 파일 삭제
    public boolean deleteFile(String directory, String fileName) {
        File file = new File(directory + File.separator + fileName);
        return file.exists() && file.delete();
    }

    // 파일 URL 반환
    public String getFileUrl(String directory, String fileName) {
        return directory + "/" + fileName; // 실제 URL 경로는 프론트와 협의 필요
    }

    // 고유 파일명 생성
    private String generateUniqueFileName(String baseName) {
        String uuid = UUID.randomUUID().toString();
        String extension = baseName.substring(baseName.lastIndexOf(".")); // 확장자 추출
        return uuid + "_" + baseName.replaceAll("\\s+", "_") + extension;
    }
}
