package Fream_back.improve_Fream_Back.utils;

import Fream_back.improve_Fream_Back.user.Jwt.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }
    public static String extractEmailOrAnonymous() {
        try {
            return extractEmailFromSecurityContext();
        } catch (IllegalStateException e) {
            // 인증 안 된 경우
            return "anonymous";
        }
    }
    // 나이/성별 등 추가 정보도 가져오려면?
    public static UserInfo extractUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof UserInfo) {
            return (UserInfo) authentication.getDetails();
        }
        return null;
    }
}