package Fream_back.improve_Fream_Back.event.service;

import Fream_back.improve_Fream_Back.event.dto.CreateEventRequest;
import Fream_back.improve_Fream_Back.event.dto.UpdateEventRequest;
import Fream_back.improve_Fream_Back.event.entity.Event;
import Fream_back.improve_Fream_Back.event.entity.SimpleImage;
import Fream_back.improve_Fream_Back.event.repository.EventRepository;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.service.brand.BrandQueryService;
import Fream_back.improve_Fream_Back.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventCommandService {
    private final EventRepository eventRepository;
    private final SimpleImageCommandService simpleImageCommandService;
    private final SimpleImageQueryService simpleImageQueryService;
    private final BrandQueryService brandQueryService;
    private final FileUtils fileUtils; // 파일 저장 유틸

    private final String BASE_DIRECTORY = System.getProperty("user.dir") + "/event/";

    /**
     * 이벤트 생성
     */
    public Long createEvent(CreateEventRequest request,
                            MultipartFile thumbnailFile,
                            List<MultipartFile> simpleImageFiles) {
        // 1. Brand 연관관계 처리 등 로직
        Brand brand = brandQueryService.findById(request.getBrandId());


        // 2. Event 엔티티 생성
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .thumbnailFileName("")
                .brand(brand)
                .build();

        eventRepository.save(event);

        // 3. 썸네일 파일 저장 (thumbnailFile이 있을 경우에만)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String directory = BASE_DIRECTORY + event.getId() + "/";
            String savedThumbnailName = fileUtils.saveFile(directory, "thumbnail_" + event.getId(), thumbnailFile);

// DB에는 파일명만 저장
            event.updateThumbnailFileName(savedThumbnailName);
        }

        // 4. 심플이미지 저장
        if (simpleImageFiles != null && !simpleImageFiles.isEmpty()) {
            simpleImageCommandService.createSimpleImages(event, simpleImageFiles);
        }

        return event.getId();
    }

    /**
     * 이벤트 수정
     */
    public Long updateEvent(Long eventId,
                            UpdateEventRequest request,
                            MultipartFile thumbnailFile,
                            List<MultipartFile> simpleImageFiles) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트가 존재하지 않습니다."));

        // 1) 이벤트 기본 필드 업데이트 (title, description, startDate, endDate)
        event.updateEvent(request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate());

        // 2) 기존 SimpleImage 전부 삭제 (파일 + DB)
        List<SimpleImage> oldImages = simpleImageQueryService.findByEventId(eventId);
        for (SimpleImage oldImg : oldImages) {
            String directory = BASE_DIRECTORY + event.getId() + "/";
            fileUtils.deleteFile(directory, oldImg.getSavedFileName());
        }
        // DB에서도 삭제 필요. 보통 커맨드 서비스 내에서 repository.deleteAll(oldImages) or
        // simpleImageCommandService.deleteAllByEvent(eventId);
        // 로직을 호출.

        // 3) 썸네일 교체
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // [3-1] 기존 썸네일 파일 삭제
            if (event.getThumbnailFileName() != null && !event.getThumbnailFileName().isEmpty()) {
                String directory = BASE_DIRECTORY + event.getId() + "/";
                fileUtils.deleteFile(directory, event.getThumbnailFileName());
            }

            // [3-2] 새 썸네일 저장
            String directory = BASE_DIRECTORY + eventId + "/";
            String savedThumbnailName = fileUtils.saveFile(directory,
                    "thumbnail_" + eventId, thumbnailFile);

            // DB에는 파일명만 업데이트
            event.updateThumbnailFileName(savedThumbnailName);

        } else {
            // 새 썸네일이 없으면, 기존 썸네일 유지/삭제 등 정책 결정
            // 여기서는 "삭제"로 가정
            if (event.getThumbnailFileName() != null && !event.getThumbnailFileName().isEmpty()) {
                String directory = BASE_DIRECTORY + event.getId() + "/";
                fileUtils.deleteFile(directory, event.getThumbnailFileName());
            }
            event.updateThumbnailFileName("");
        }

        // 4) 심플이미지 재등록
        if (simpleImageFiles != null && !simpleImageFiles.isEmpty()) {
            simpleImageCommandService.createSimpleImages(event, simpleImageFiles);
        }

        return event.getId();
    }

    public void deleteEvent(Long eventId) {
        // 1) 이벤트 조회
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다."));

        // 2) 썸네일 파일 삭제
        if (event.getThumbnailFileName() != null && !event.getThumbnailFileName().isEmpty()) {
            String directory = BASE_DIRECTORY + eventId + "/";
            fileUtils.deleteFile(directory, event.getThumbnailFileName());
        }

        // 3) 심플이미지 파일 삭제
        List<SimpleImage> images = simpleImageQueryService.findByEventId(eventId);
        for (SimpleImage image : images) {
            String directory = BASE_DIRECTORY + eventId + "/";
            fileUtils.deleteFile(directory, image.getSavedFileName());
        }
        // DB에서 삭제
        simpleImageCommandService.deleteAllByEvent(eventId);

        // 4) 이벤트 엔티티 삭제
        eventRepository.delete(event);
    }
}

