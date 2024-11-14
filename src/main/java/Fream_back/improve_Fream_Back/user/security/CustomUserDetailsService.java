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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username으로 사용자 검색
        Fream_back.improve_Fream_Back.user.entity.User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // UserDetails 객체 반환
        return User.builder()
                .username(user.getLoginId())  // 로그인 ID 설정
                .password(user.getPassword())  // 패스워드 설정
                .roles("USER")  // 권한 설정 (필요에 따라 다르게 설정 가능)
                .build();
    }
}