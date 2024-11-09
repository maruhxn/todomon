package com.maruhxn.todomon.core.domain.social.dao;

import com.maruhxn.todomon.core.domain.member.domain.Member;
import com.maruhxn.todomon.core.domain.social.domain.Follow;
import com.maruhxn.todomon.core.domain.social.domain.FollowRequestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollower_IdAndFollowee_Id(Long followerId, Long followeeId);

    @Query("SELECT f from Follow f JOIN FETCH f.followee WHERE f.id = :followId")
    Optional<Follow> findOneByIdWithMember(Long followId);

    @EntityGraph(attributePaths = {"followee.titleName"})
    List<Follow> findByFollowerAndStatusOrderByCreatedAtDesc(Member follower, FollowRequestStatus followRequestStatus); // follower가 전달값과 일치하는 Follow 모두 찾기

    @EntityGraph(attributePaths = {"follower.titleName"})
    List<Follow> findByFolloweeAndStatusOrderByCreatedAtDesc(Member followee, FollowRequestStatus followRequestStatus); // followee가 전달값과 일치하는 Follow 모두 찾기

    boolean existsByFollowerAndFolloweeAndStatus(Member follower, Member followee, FollowRequestStatus followRequestStatus);

}
