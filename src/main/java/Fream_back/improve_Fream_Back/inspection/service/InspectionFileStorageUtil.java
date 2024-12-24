package Fream_back.improve_Fream_Back.inspection.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InspectionFileStorageUtil {

    private static final String INSPECTION_STORAGE_DIR =System.getProperty("user.dir") +  "/Inspection/";

    // 파일 저장 (단일)
    public String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(INSPECTION_STORAGE_DIR + uniqueFileName);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        file.transferTo(filePath.toFile());
        return INSPECTION_STORAGE_DIR + uniqueFileName;
    }

    // 파일 저장 (다중)
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            filePaths.add(saveFile(file));
        }
        return filePaths;
    }

    // 파일 삭제 (다중)
    public void deleteFiles(List<String> filePaths) throws IOException {
        for (String filePath : filePaths) {
            String normalizedPath = filePath.replace("/", File.separator).replace("\\", File.separator);
            Path path = Paths.get(normalizedPath);
            if (Files.exists(path)) {
                Files.deleteIfExists(path);
            }
        }
    }

    // 파일 존재 여부 확인
    public boolean hasFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty();
    }

    // HTML content 내 이미지 경로 추출
    public List<String> extractImagePaths(String content) {
        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List<String> paths = new ArrayList<>();

        while (matcher.find()) {
            paths.add(matcher.group(1));
        }
        return paths;
    }

    // HTML content 내 이미지 경로 업데이트
    public String updateImagePaths(String content, List<String> filePaths) {
        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuffer updatedContent = new StringBuffer();

        int index = 0;
        while (matcher.find() && index < filePaths.size()) {
            matcher.appendReplacement(updatedContent, matcher.group(0).replace(matcher.group(1), filePaths.get(index++)));
        }
        matcher.appendTail(updatedContent);
        return updatedContent.toString();
    }
}
