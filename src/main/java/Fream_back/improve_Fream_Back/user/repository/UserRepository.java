package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 특정 loginId와 비밀번호가 일치하는 사용자 조회 (로그인에 사용)
    Optional<User> findByLoginIdAndPassword(String loginId, String password);

    // 전화번호로 사용자 조회 (전화번호로 아이디 찾기)
    Optional<User> findByPhoneNumber(String phoneNumber);

    // 이메일로 사용자 조회 (이메일로 아이디 찾기)
    Optional<User> findByEmail(String email);

    // loginId와 전화번호 또는 이메일을 사용해 사용자 조회 (비밀번호 재설정 요청에 사용)
    Optional<User> findByLoginIdAndPhoneNumberOrEmail(String loginId, String phoneNumber, String email);

    // loginId로 사용자 조회 (비밀번호 업데이트용)
    Optional<User> findByLoginId(String loginId);
}
