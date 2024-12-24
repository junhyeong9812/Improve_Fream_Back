package Fream_back.improve_Fream_Back.notice.service;

import Fream_back.improve_Fream_Back.notice.entity.NoticeImage;
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
import java.util.stream.Collectors;

@Component
public class NoticeFileStorageUtil {

    private static final String NOTICE_STORAGE_DIR = System.getProperty("user.dir") +  "/notice/";

    // 단일 파일 저장
    public String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(NOTICE_STORAGE_DIR + uniqueFileName);

        // 디렉터리 확인 및 생성
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        file.transferTo(filePath.toFile());
        return NOTICE_STORAGE_DIR + uniqueFileName;
    }

    // 다중 파일 저장
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return saveFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    // 단일 파일 삭제
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }

    // 다중 파일 삭제
    public void deleteFiles(List<String> filePaths) throws IOException {

        for (String filePath : filePaths) {
            String normalizedPath = filePath.replace("/", File.separator).replace("\\", File.separator);
            Path path = Paths.get(normalizedPath);
            if (Files.exists(path)) {
                deleteFile(filePath);
            }
        }
    }

    // 기존 이미지 삭제 처리
    public void handleImageDeletion(List<NoticeImage> existingImages, List<String> existingImageUrls) throws IOException {
        List<String> imagesToDelete = existingImages.stream()
                .filter(image -> !existingImageUrls.contains(image.getImageUrl())) // 삭제할 이미지 필터링
                .map(NoticeImage::getImageUrl) // 이미지 URL 추출
                .collect(Collectors.toList());

        deleteFiles(imagesToDelete); // 파일 삭제
    }

    // 비디오 파일 여부 확인
    public boolean isVideo(String filePath) {
        String lowerCasePath = filePath.toLowerCase();
        return lowerCasePath.endsWith(".mp4") || lowerCasePath.endsWith(".avi") || lowerCasePath.endsWith(".mov");
    }

    // 파일 존재 여부 확인
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    // 파일 경로 반환
    public Path getFilePath(String fileName) {
        return Paths.get(NOTICE_STORAGE_DIR + fileName);
    }

    public List<String> extractImagePaths(String content) {
        List<String> paths = new ArrayList<>();
        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            paths.add(matcher.group(1)); // src 값만 추출
        }
        return paths;
    }
    public String updateImagePaths(String content, List<String> newPaths) {
        String regex = "<img\\s+[^>]*src=\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        StringBuffer updatedContent = new StringBuffer();

        int index = 0;
        while (matcher.find() && index < newPaths.size()) {
            matcher.appendReplacement(updatedContent, matcher.group(0).replace(matcher.group(1), newPaths.get(index++)));
        }
        matcher.appendTail(updatedContent);

        return updatedContent.toString();
    }

}
