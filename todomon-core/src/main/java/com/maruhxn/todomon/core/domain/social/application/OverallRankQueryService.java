package com.maruhxn.todomon.core.domain.social.application;

import com.maruhxn.todomon.core.domain.social.dao.OverallRankQueryRepository;
import com.maruhxn.todomon.core.domain.social.dto.response.CollectedPetRankItem;
import com.maruhxn.todomon.core.domain.social.dto.response.DiligenceRankItem;
import com.maruhxn.todomon.core.domain.social.dto.response.TodoAchievementRankItem;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OverallRankQueryService {

    private final OverallRankQueryRepository overallRankQueryRepository;

    @Cacheable(value = "diligenceRankCache", key = "'top10Members'", cacheManager = "dailyCacheManager")
    public List<DiligenceRankItem> getTop10MembersByDiligenceLevelAndGauge() {
        return overallRankQueryRepository.findTop10MembersByDiligenceLevelAndGauge();
    }

    @Cacheable(value = "collectedPetRankCache", key = "'top10Members'", cacheManager = "dailyCacheManager")
    public List<CollectedPetRankItem> getTop10MembersByCollectedPetCnt() {
        return overallRankQueryRepository.findTop10MembersByCollectedPetCnt();
    }

    @Cacheable(value = "dailyAchievementRankCache", key = "'dailyAchievement'", cacheManager = "dailyCacheManager")
    public List<TodoAchievementRankItem> getRankingOfDailyAchievement() {
        return overallRankQueryRepository.findTop10MembersByYesterdayAchievement();
    }

    @Cacheable(value = "weeklyAchievementRankCache", key = "'weeklyAchievement'", cacheManager = "weeklyCacheManager")
    public List<TodoAchievementRankItem> getRankingOfWeeklyAchievement() {
        return overallRankQueryRepository.findTop10MembersByWeeklyAchievement();
    }
}
