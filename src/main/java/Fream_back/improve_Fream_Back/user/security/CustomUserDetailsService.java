package Fream_back.improve_Fream_Back.user.security;

import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;  // 사용자 정보를 가져오는 Repository

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // UserDetailsService 구현: 로그인 시 사용자 정보를 반환
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // username으로 사용자 검색
        Fream_back.improve_Fream_Back.user.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // UserDetails 객체 반환
        return User.builder()
                .username(user.getEmail()) // 이메일을 username으로 설정
                .password(user.getPassword()) // 암호화된 비밀번호
                .roles("USER")  // 권한 설정 (필요에 따라 다르게 설정 가능)
                .build();
    }
}