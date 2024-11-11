package Fream_back.improve_Fream_Back.base.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * BaseEntity
 *
 * BaseTimeEntity를 확장하여 생성자와 수정자를 추가로 관리하는 엔티티입니다.
 * 작성자 정보가 필요한 엔티티에서 사용됩니다.
 */
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdBy; // 작성자 (최초 생성한 사용자)

    @LastModifiedBy
    private String modifiedBy; // 수정자 (마지막 수정한 사용자)
}
