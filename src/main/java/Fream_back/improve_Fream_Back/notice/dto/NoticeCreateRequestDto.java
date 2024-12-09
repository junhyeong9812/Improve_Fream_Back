package Fream_back.improve_Fream_Back.notice.dto;

import Fream_back.improve_Fream_Back.notice.entity.NoticeCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class NoticeCreateRequestDto {
    private String title; // 제목
    private String content; // 내용
    private NoticeCategory category; // 카테고리
    private List<MultipartFile> files; // 업로드 파일
}
