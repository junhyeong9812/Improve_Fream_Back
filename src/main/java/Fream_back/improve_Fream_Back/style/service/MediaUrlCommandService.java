package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.MediaUrl;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.repository.MediaUrlRepository;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaUrlCommandService {

    private final MediaUrlRepository mediaUrlRepository;
    private final FileUtils fileUtils; // 파일 저장 유틸리티

    private static final String MEDIA_DIRECTORY = System.getProperty("user.dir") +  "/styles/";

    public MediaUrl saveMediaFile(Style style, MultipartFile mediaFile) {
        // 파일 저장
        String mediaDirectory = MEDIA_DIRECTORY + style.getId() + "/";
        String mediaUrl = fileUtils.saveFile(mediaDirectory, "media_", mediaFile);

        // MediaUrl 엔티티 생성 및 저장
        MediaUrl savedMediaUrl = MediaUrl.builder()
                .style(style)
                .url(mediaUrl)
                .build();

        return mediaUrlRepository.save(savedMediaUrl);
    }
    public void deleteMediaUrl(MediaUrl mediaUrl) {
        // 파일 삭제
        fileUtils.deleteFile(MEDIA_DIRECTORY + mediaUrl.getStyle().getId() + "/", mediaUrl.getUrl());

        // MediaUrl 엔티티 삭제
        mediaUrl.getStyle().removeMediaUrl(mediaUrl); // 연관 관계 해제
        mediaUrlRepository.delete(mediaUrl);
    }
}
