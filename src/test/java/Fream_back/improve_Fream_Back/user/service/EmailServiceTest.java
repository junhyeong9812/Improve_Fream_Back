package Fream_back.improve_Fream_Back.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendEmail() {
        // Given
        String to = "pickjog@gmail.com"; // 테스트용 수신 이메일
        String subject = "테스트 이메일";
        String content = "이것은 테스트 이메일입니다. 잘 도착했는지 확인해주세요.";

        // When & Then
        emailService.sendEmail(to, subject, content);

        // 이메일이 실제로 전송되었는지 테스트
        System.out.println("이메일 전송 테스트 완료: " + to);
    }
}