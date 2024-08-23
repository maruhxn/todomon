package com.maruhxn.todomon.domain.social.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequestItem extends AbstractMemberInfoItem {
    private Long id;
    private Long senderId;


    @Builder
    public FollowRequestItem(Long id, Long senderId, String username, String profileImageUrl, TitleNameItem title) {
        super(username, profileImageUrl, title);
        this.id = id;
        this.senderId = senderId;
    }
}
