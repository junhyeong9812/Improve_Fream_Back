package Fream_back.improve_Fream_Back.faq.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // FAQ ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FAQCategory category; // FAQ 카테고리 (ENUM)

    @Column(nullable = false)
    private String question; // 질문 (Q)

    @Lob
    @Column(nullable = false)
    private String answer; // 답변 (A)
}
