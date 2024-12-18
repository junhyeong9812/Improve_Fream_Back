//package Fream_back.improve_Fream_Back.style.entity;
//
//import Fream_back.improve_Fream_Back.user.entity.User;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class StyleLike {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "style_id")
//    private Style style; // 좋아요 대상 스타일
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user; // 좋아요를 누른 사용자
//
//    // 연관관계 메서드
//    public void assignStyle(Style style) {
//        this.style = style;
//    }
//
//    public void assignUser(User user) {
//        this.user = user;
//    }
//}
