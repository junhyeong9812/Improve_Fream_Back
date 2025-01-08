package Fream_back.improve_Fream_Back.event.service;

import Fream_back.improve_Fream_Back.event.dto.CreateEventRequest;
import Fream_back.improve_Fream_Back.event.dto.UpdateEventRequest;
import Fream_back.improve_Fream_Back.event.entity.Event;
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
                .thumbnailImageUrl("")
                .brand(brand)
                .build();

        eventRepository.save(event);

        // 3. 썸네일 파일 저장 (thumbnailFile이 있을 경우에만)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String directory = BASE_DIRECTORY + event.getId() + "/";
            String savedThumbnailName = fileUtils.saveFile(directory, "thumbnail_" + event.getId(), thumbnailFile);
            String thumbnailUrl = directory + savedThumbnailName;

            event.updateEvent(null, null, null, null, thumbnailUrl);
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

        event.updateEvent(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                null // 썸네일은 아래에서 처리
        );

        // 썸네일 교체 로직
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 기존 썸네일 파일 삭제 로직 (선택사항)
            // ...

            String directory = BASE_DIRECTORY + event.getId() + "/";
            String savedThumbnailName = fileUtils.saveFile(directory, "thumbnail_" + event.getId(), thumbnailFile);
            String thumbnailUrl = directory + savedThumbnailName;
            event.updateEvent(null, null, null, null, thumbnailUrl);
        }

        // 심플이미지 교체 로직
        if (simpleImageFiles != null) {
            // 전체 삭제 후 재등록 방식이라면:
            // 1) 기존 SimpleImage 조회 + 파일 삭제
            // 2) DB에서 delete
            // 3) createSimpleImages(event, simpleImageFiles) 호출

            // 혹은 부분 업데이트라면, 필요한 로직만...
        }

        return event.getId();
    }

    // 기타 이벤트 삭제 로직, 상태 변경 로직 등등...
}

