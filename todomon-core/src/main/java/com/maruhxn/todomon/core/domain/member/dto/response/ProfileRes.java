package com.maruhxn.todomon.core.domain.member.dto.response;

import com.maruhxn.todomon.core.domain.pet.domain.Pet;
import com.maruhxn.todomon.core.domain.pet.domain.Rarity;
import com.maruhxn.todomon.core.domain.social.domain.FollowRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileRes {

    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private boolean isSubscribed;
    private int level;
    private double gauge;
    private TitleNameItem title;
    private RepresentPetItem representPetItem;
    private FollowInfoItem followInfo;

    @Builder
    public ProfileRes(Long id, String username, String email, String profileImageUrl, boolean isSubscribed, int level, double gauge, TitleNameItem title, RepresentPetItem representPetItem, FollowInfoItem followInfo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.isSubscribed = isSubscribed;
        this.level = level;
        this.gauge = gauge;
        this.title = title;
        this.representPetItem = representPetItem;
        this.followInfo = followInfo;
    }

    public void setTitleNameItemToNullIfIsEmpty() {
        if (this.title.getId() == null) {
            this.title = null;
        }
    }

    public void setRepresentPetItemToNullIfIsEmpty() {
        if (this.representPetItem.getId() == null) {
            this.representPetItem = null;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TitleNameItem {

        private Long id;
        private String name;
        private String color;

        @Builder
        public TitleNameItem(Long id, String name, String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RepresentPetItem {
        private Long id;
        private String name;
        private Rarity rarity;
        private String appearance;
        private String color;
        private int level;

        @Builder
        public RepresentPetItem(Long id, String name, Rarity rarity, String appearance, String color, int level) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.appearance = appearance;
            this.color = color;
            this.level = level;
        }

        public static RepresentPetItem of(Pet representPet) {
            return RepresentPetItem.builder()
                    .id(representPet.getId())
                    .name(representPet.getName())
                    .rarity(representPet.getRarity())
                    .appearance(representPet.getAppearance())
                    .color(representPet.getColor())
                    .level(representPet.getLevel())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class FollowInfoItem {

        private Long followerCnt;
        private Long followingCnt;
        private Boolean isFollowing;
        private Long receivedRequestId;
        private FollowRequestStatus receivedFollowStatus;
        private FollowRequestStatus sentFollowStatus;

        public FollowInfoItem(Long followerCnt, Long followingCnt, Boolean isFollowing, Long receivedRequestId, FollowRequestStatus receivedFollowStatus, FollowRequestStatus sentFollowStatus) {
            this.followerCnt = followerCnt;
            this.followingCnt = followingCnt;
            this.isFollowing = isFollowing;
            this.receivedRequestId = receivedRequestId;
            this.receivedFollowStatus = receivedFollowStatus;
            this.sentFollowStatus = sentFollowStatus;
        }
    }
}