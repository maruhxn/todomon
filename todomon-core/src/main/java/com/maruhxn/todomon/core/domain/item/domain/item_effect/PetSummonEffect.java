package com.maruhxn.todomon.core.domain.item.domain.item_effect;

import com.maruhxn.todomon.core.domain.item.dto.request.ItemEffectReq;
import com.maruhxn.todomon.core.domain.member.domain.Member;
import com.maruhxn.todomon.core.domain.pet.application.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("petSummonEffect")
@RequiredArgsConstructor
public class PetSummonEffect implements ItemEffect {

    private final PetService petService;

    @Override
    public void applyEffect(Member member, ItemEffectReq itemEffectReq) {
        petService.create(member.getId());
    }
}
