package Fream_back.improve_Fream_Back.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderBidStatusCountDto {
    private long pendingCount;              // 대기 중
    private long matchedCount;              // 매칭 완료
    private long cancelledOrCompletedCount; // 취소 및 완료
}
