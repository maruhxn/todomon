package com.maruhxn.todomon.core.domain.social.application;

import com.maruhxn.todomon.core.domain.member.implement.MemberReader;
import com.maruhxn.todomon.core.domain.social.dto.response.FollowRequestItem;
import com.maruhxn.todomon.core.domain.social.dto.response.FollowerItem;
import com.maruhxn.todomon.core.domain.social.dto.response.FollowingItem;
import com.maruhxn.todomon.core.domain.social.implement.FollowReader;
import com.maruhxn.todomon.core.global.common.dto.PageItem;
import com.maruhxn.todomon.core.global.common.dto.request.PagingCond;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowQueryService {

    private final MemberReader memberReader;
    private final FollowReader followReader;

    // 팔로우 요청들을 조회한다.
    public PageItem<FollowRequestItem> getPendingFollowRequests(Long memberId, PagingCond pagingCond) {
        return PageItem.from(followReader.findPendingRequests(memberId, pagingCond));
    }

    public PageItem<FollowingItem> getFollowingList(Long memberId, PagingCond pagingCond) {
        memberReader.findById(memberId);
        return PageItem.from(followReader.findFollowings(memberId, pagingCond));
    }

    public PageItem<? extends FollowerItem> getFollowerList(Long loginMemberId, Long memberId, PagingCond pagingCond) {
        boolean isMe = loginMemberId.equals(memberId);
        memberReader.findById(memberId);

        Page<? extends FollowerItem> results;
        if (isMe) {
            results = followReader.findMyFollowers(loginMemberId, pagingCond);
        } else {
            results = followReader.findFollowersByMemberId(memberId, pagingCond);
        }
        return PageItem.from(results);
    }
}
