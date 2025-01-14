package com.maruhxn.todomon.core.domain.item.application;

import com.maruhxn.todomon.core.domain.item.domain.InventoryItem;
import com.maruhxn.todomon.core.domain.item.domain.Item;
import com.maruhxn.todomon.core.domain.item.dto.request.CreateItemRequest;
import com.maruhxn.todomon.core.domain.item.dto.request.ItemEffectReq;
import com.maruhxn.todomon.core.domain.item.dto.request.UpdateItemRequest;
import com.maruhxn.todomon.core.domain.item.dto.response.InventoryItemDto;
import com.maruhxn.todomon.core.domain.item.dto.response.ItemDto;
import com.maruhxn.todomon.core.domain.item.implement.*;
import com.maruhxn.todomon.core.domain.member.domain.Member;
import com.maruhxn.todomon.core.domain.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final MemberReader memberReader;

    private final InventoryItemWriter inventoryItemWriter;
    private final InventoryItemReader inventoryItemReader;

    private final ItemReader itemReader;
    private final ItemApplier itemApplier;
    private final ItemWriter itemWriter;

    public void createItem(CreateItemRequest req) {
        Item item = CreateItemRequest.toEntity(req);
        itemWriter.create(item);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems() {
        return itemReader.findAllItems().stream()
                .map(ItemDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ItemDto getItemDto(Long itemId) {
        return ItemDto.from(itemReader.findItemById(itemId));
    }

    @Transactional(readOnly = true)
    public List<InventoryItemDto> getInventoryItems(Long memberId) {
        return inventoryItemReader.findAllByMemberId(memberId).stream()
                .map(InventoryItemDto::from).toList();
    }

    public void updateItem(Long itemId, UpdateItemRequest req) {
        Item item = itemReader.findItemById(itemId);
        item.update(req);
    }

    public void deleteItem(Long itemId) {
        Item item = itemReader.findItemById(itemId);
        itemWriter.remove(item);
    }

    public void useInventoryItem(Long memberId, String itemName, ItemEffectReq req) {
        Member member = memberReader.findById(memberId);
        InventoryItem inventoryItem = inventoryItemReader.findByMemberIdAndItemName(memberId, itemName);

        Item targetItem = inventoryItem.getItem();
        itemApplier.apply(targetItem, member, req);
        inventoryItemWriter.consume(inventoryItem);
    }

}
