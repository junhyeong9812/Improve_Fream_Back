package Fream_back.improve_Fream_Back.style.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id", nullable = false)
    private Style style;

    // 연관관계 메서드
    public void assignStyle(Style style) {
        this.style = style;
    }
    public void unassignStyle() {
        this.style = null;
    }

}

