package Fream_back.improve_Fream_Back.inspection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 검수 기준 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionCategory category; // 검수 기준 카테고리 (ENUM)

    @Lob
    @Column(nullable = false)
    private String content; // 검수 기준 내용

    /**
     * 검수 기준 업데이트 메서드
     *
     * @param category 새로운 카테고리
     * @param content  새로운 내용
     */
    public void update(InspectionCategory category, String content) {
        this.category = category;
        this.content = content;
    }
}
