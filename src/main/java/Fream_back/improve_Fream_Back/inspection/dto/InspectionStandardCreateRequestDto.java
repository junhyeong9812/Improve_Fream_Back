package Fream_back.improve_Fream_Back.inspection.dto;

import Fream_back.improve_Fream_Back.inspection.entity.InspectionCategory;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InspectionStandardCreateRequestDto {
    private InspectionCategory category; // 검수 기준 카테고리
    private String content; // 검수 기준 내용
    private List<MultipartFile> files; // 업로드된 파일
}