package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.style.entity.MediaUrl;
import Fream_back.improve_Fream_Back.style.repository.MediaUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaUrlQueryService {

    private final MediaUrlRepository mediaUrlRepository;

    public List<MediaUrl> findMediaUrlsByStyleId(Long styleId) {
        return mediaUrlRepository.findByStyleId(styleId);
    }
}
