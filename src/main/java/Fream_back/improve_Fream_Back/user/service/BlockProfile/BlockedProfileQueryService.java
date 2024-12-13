package Fream_back.improve_Fream_Back.user.service.BlockProfile;

import Fream_back.improve_Fream_Back.user.dto.BlockedProfileDto;
import Fream_back.improve_Fream_Back.user.entity.BlockedProfile;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import Fream_back.improve_Fream_Back.user.repository.BlockedProfileRepository;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockedProfileQueryService {

    private final BlockedProfileRepository blockedProfileRepository;
    private final ProfileQueryService profileQueryService;

    @Transactional(readOnly = true)
    public List<BlockedProfileDto> getBlockedProfiles(Profile profile) {
        List<BlockedProfile> blockedProfiles = blockedProfileRepository.findAllByProfileWithBlocked(profile);

        return blockedProfiles.stream()
                .map(bp -> new BlockedProfileDto(
                        bp.getBlockedProfile().getId(),
                        bp.getBlockedProfile().getProfileName(),
                        bp.getBlockedProfile().getProfileImageUrl()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BlockedProfileDto> getBlockedProfiles(String email) {
        Profile profile = profileQueryService.getProfileByEmail(email); // 단일 프로필 가져오기 로직 재사용

        List<BlockedProfile> blockedProfiles = blockedProfileRepository.findAllByProfileWithBlocked(profile);
        return blockedProfiles.stream()
                .map(bp -> new BlockedProfileDto(
                        bp.getBlockedProfile().getId(),
                        bp.getBlockedProfile().getProfileName(),
                        bp.getBlockedProfile().getProfileImageUrl()))
                .toList();
    }
}