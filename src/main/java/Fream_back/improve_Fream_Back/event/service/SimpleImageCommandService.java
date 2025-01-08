package Fream_back.improve_Fream_Back.event.service;

import Fream_back.improve_Fream_Back.event.entity.Event;
import Fream_back.improve_Fream_Back.event.entity.SimpleImage;
import Fream_back.improve_Fream_Back.event.repository.SimpleImageRepository;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SimpleImageCommandService {
    private final SimpleImageRepository simpleImageRepository;
    private final FileUtils fileUtils;

    public void createSimpleImages(Event event, List<MultipartFile> imageFiles) {
        // 예: /home/xxx/프로젝트경로/event/{eventId}/
        String directory = System.getProperty("user.dir") + "/event/" + event.getId() + "/";

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            // "simple_eventId_순번" 형태의 프리픽스
            String prefix = "simple_" + event.getId() + "_" + (i + 1);
            // fileUtils.saveFile() 에서 고유 파일명(확장자 포함)이 생성됨
            String savedFileName = fileUtils.saveFile(directory, prefix, file);

            // DB에는 "savedFileName"만 보관
            SimpleImage simpleImage = SimpleImage.builder()
                    .savedFileName(savedFileName)
                    .event(event)  // 빌더로 직접 연결해도 되고, event.addSimpleImage() 써도 됨
                    .build();

            event.addSimpleImage(simpleImage);
            simpleImageRepository.save(simpleImage);
        }
    }

    // 심플이미지 수정/삭제 등 추가 로직
}

