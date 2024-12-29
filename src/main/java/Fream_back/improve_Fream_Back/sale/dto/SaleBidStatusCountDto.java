package Fream_back.improve_Fream_Back.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleBidStatusCountDto {
    private long pendingCount;              // 대기 중
    private long matchedCount;              // 매칭 완료
    private long cancelledOrCompletedCount; // 취소 및 완료
}
