package Fream_back.improve_Fream_Back.user.repository;

import Fream_back.improve_Fream_Back.user.entity.Follow;
import Fream_back.improve_Fream_Back.user.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(Profile follower, Profile following);

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :profileId")
    Page<Follow> findFollowersByProfileId(@Param("profileId") Long profileId, Pageable pageable);

    @Query("SELECT f FROM Follow f WHERE f.following.id = :profileId")
    Page<Follow> findFollowingsByProfileId(@Param("profileId") Long profileId, Pageable pageable);
}
