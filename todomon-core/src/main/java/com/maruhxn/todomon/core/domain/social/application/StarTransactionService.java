package com.maruhxn.todomon.core.domain.social.application;

import com.maruhxn.todomon.core.domain.member.dao.MemberRepository;
import com.maruhxn.todomon.core.domain.member.domain.Member;
import com.maruhxn.todomon.core.domain.social.dao.StarTransactionQueryRepository;
import com.maruhxn.todomon.core.domain.social.dao.StarTransactionRepository;
import com.maruhxn.todomon.core.domain.social.domain.StarTransaction;
import com.maruhxn.todomon.core.domain.social.dto.response.ReceivedStarItem;
import com.maruhxn.todomon.core.global.common.dto.PageItem;
import com.maruhxn.todomon.core.global.common.dto.request.PagingCond;
import com.maruhxn.todomon.core.global.error.ErrorCode;
import com.maruhxn.todomon.core.global.error.exception.BadRequestException;
import com.maruhxn.todomon.core.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.maruhxn.todomon.core.domain.social.domain.StarTransactionStatus.RECEIVED;
import static com.maruhxn.todomon.core.domain.social.domain.StarTransactionStatus.SENT;

@Service
@Transactional
@RequiredArgsConstructor
public class StarTransactionService {

    private final MemberRepository memberRepository;
    private final StarTransactionRepository starTransactionRepository;
    private final FollowQueryService followQueryService;
    private final StarTransactionQueryRepository starTransactionQueryRepository;

    public void sendStar(Long senderId, Long receiverId, LocalDateTime now) {
        if (Objects.equals(senderId, receiverId)) throw new BadRequestException(ErrorCode.BAD_REQUEST);

        this.checkIsAlreadySent(senderId, receiverId, now);

        // 수신자 존재하는지 확인
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER, "수신자 정보가 존재하지 않습니다."));

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        // 팔로우 요청이 수락된 경우에만 별을 보낼 수 있음.
        followQueryService.checkIsFollow(senderId, receiverId);

        starTransactionRepository.save(StarTransaction.createTransaction(sender, receiver));
    }

    // 24시간 내로 이미 보낸 적이 있는지 확인
    private void checkIsAlreadySent(Long senderId, Long receiverId, LocalDateTime now) {
        if (starTransactionQueryRepository
                .existsStarsCreatedWithinLast24Hours(senderId, receiverId, now.minusHours(24), now))
            throw new BadRequestException(ErrorCode.ALREADY_SENT_STAR);
    }

    public void receiveOneStar(Long receiverId, Long transactionId) {
        StarTransaction transaction = starTransactionRepository.findByIdAndReceiver_Id(transactionId, receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_STAR_TRANSACTION));

        if (transaction.isReceived()) throw new BadRequestException(ErrorCode.ALREADY_RECEIVED);

        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        receiver.addStar(1);
        transaction.updateStatus(RECEIVED);
    }

    public void receiveAllStars(Long receiverId) {
        List<StarTransaction> transactions = starTransactionRepository.findAllByReceiver_IdAndStatus(receiverId, SENT);

        transactions.forEach(tx -> tx.updateStatus(RECEIVED));

        memberRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER))
                .addStar(transactions.size());
    }

    @Transactional(readOnly = true)
    public PageItem<ReceivedStarItem> getReceivedStars(Long memberId, PagingCond pagingCond) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        PageRequest pageRequest = PageRequest.of(pagingCond.getPageNumber(), 10);

        return PageItem.from(starTransactionQueryRepository
                .findReceivedStarWithPaging(findMember.getId(), pageRequest));
    }
}
