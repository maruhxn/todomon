package com.maruhxn.todomon.core.domain.pet.application;

import com.maruhxn.todomon.core.domain.member.dao.MemberRepository;
import com.maruhxn.todomon.core.domain.member.domain.Member;
import com.maruhxn.todomon.core.domain.pet.dao.PetRepository;
import com.maruhxn.todomon.core.domain.pet.domain.Pet;
import com.maruhxn.todomon.core.global.auth.checker.IsMyPetOrAdmin;
import com.maruhxn.todomon.core.global.error.ErrorCode;
import com.maruhxn.todomon.core.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepresentPetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

    @IsMyPetOrAdmin
    public void setRepresentPet(Long memberId, Long petId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        if (petId == null) {
            findMember.setRepresentPet(null);
        } else {
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_PET));

            findMember.setRepresentPet(pet);
        }
    }
}
