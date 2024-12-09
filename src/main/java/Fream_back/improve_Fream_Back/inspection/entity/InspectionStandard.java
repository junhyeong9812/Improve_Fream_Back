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
}
