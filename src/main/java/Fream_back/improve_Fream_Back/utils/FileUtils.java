package Fream_back.improve_Fream_Back.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUtils {

    // 파일 저장
    public String saveFile(String directory, String prefix, MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
            String uniqueFilename = prefix + UUID.randomUUID() + extension; // 고유 파일명 생성
            Path filePath = Paths.get(directory, uniqueFilename);

            // 디렉토리가 없으면 생성
            Files.createDirectories(filePath.getParent());

            // 파일 저장
            file.transferTo(filePath.toFile());

            return uniqueFilename; // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
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
