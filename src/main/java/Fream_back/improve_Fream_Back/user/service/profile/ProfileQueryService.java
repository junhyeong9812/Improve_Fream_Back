package Fream_back.improve_Fream_Back.user.service.profile;

import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.dto.ProfileInfoDto;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import Fream_back.improve_Fream_Back.user.service.BlockProfile.BlockedProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileQueryService {

    private final ProfileRepository profileRepository;
    private final BlockedProfileQueryService blockedProfileQueryService;

    @Transactional(readOnly = true)
    public ProfileInfoDto getProfileInfo(String email) {
        Profile profile = profileRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        List<BlockedProfileDto> blockedProfiles = blockedProfileQueryService.getBlockedProfiles(profile);


        return new ProfileInfoDto(
                profile.getProfileImageUrl(),
                profile.getProfileName(),
                profile.getName(),
                profile.getBio(),
                profile.isPublic(),
                blockedProfiles
        );
    }
    //이메일 기반 조회
    @Transactional(readOnly = true)
    public Profile getProfileByEmail(String email) {
        return profileRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));
    }
}
