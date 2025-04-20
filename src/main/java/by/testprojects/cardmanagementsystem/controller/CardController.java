package by.testprojects.cardmanagementsystem.controller;

import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import by.testprojects.cardmanagementsystem.entity.dto.TransactionDto;
import by.testprojects.cardmanagementsystem.entity.mapper.CardMapper;
import by.testprojects.cardmanagementsystem.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping("/{userId}")
    public ResponseEntity<CardResponseDto> createCard(@PathVariable Long userId,
                                                      @Valid @RequestBody CreateCardRequest request) {
        CardResponseDto card = cardService.createCard(request, userId);
        return ResponseEntity.created(URI.create("/api/cards/" + card.getId()))
                .body(card);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable UUID id) {
        return ResponseEntity.ok(cardMapper.toDto(cardService.getCardById(id)));
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponseDto> updateCardStatus(
            @PathVariable UUID id,
            @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, status));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getCardTransactions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCardTransactions(id));
    }

    @GetMapping
    public ResponseEntity<Page<CardResponseDto>> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @AuthenticationPrincipal User principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        Page<CardResponseDto> cards = cardService.getUserCards(principal, pageable);
        return ResponseEntity.ok(cards);
    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length == 2) {
            return new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]);
        }
        return Sort.Order.asc("id");
    }

}
