package Fream_back.improve_Fream_Back.faq.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FAQFileStorageUtil {

    private static final String FAQ_STORAGE_DIR = "FAQ/";

    /**
     * 파일 저장 (다중)
     */
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(this::saveFileInternal)
                .collect(Collectors.toList());
    }

    /**
     * 파일 유무 확인
     */
    public boolean hasFiles(List<MultipartFile> files) {
        return files != null && !files.isEmpty();
    }

    /**
     * HTML 내용 내 <img src> 경로 수정
     */
    public String updateImagePaths(String content, List<String> filePaths) {
        if (content == null || filePaths == null || filePaths.isEmpty()) return content;

        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        StringBuffer updatedContent = new StringBuffer();
        while (matcher.find() && !filePaths.isEmpty()) {
            String originalSrc = matcher.group(1);
            String newSrc = filePaths.remove(0);
            matcher.appendReplacement(updatedContent, matcher.group(0).replace(originalSrc, newSrc));
        }
        matcher.appendTail(updatedContent);

        return updatedContent.toString();
    }
    public List<String> extractImagePaths(String content) {
        List<String> imagePaths = new ArrayList<>();
        if (content == null) return imagePaths;

        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            imagePaths.add(matcher.group(1)); // src 값만 추출
        }
        return imagePaths;
    }

    /**
     * 파일 삭제 (다중)
     */
    public void deleteFiles(List<String> filePaths) throws IOException {
        for (String filePath : filePaths) {
            Files.deleteIfExists(Paths.get(filePath));
        }
    }

    private String saveFileInternal(MultipartFile file) {
        try {
            String extension = "";
            String originalFileName = file.getOriginalFilename();
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(FAQ_STORAGE_DIR + uniqueFileName);

            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            file.transferTo(filePath.toFile());
            return FAQ_STORAGE_DIR + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }
}
