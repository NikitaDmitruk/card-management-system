package by.testprojects.cardmanagementsystem;

import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.entity.dto.CardDto;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import by.testprojects.cardmanagementsystem.entity.dto.TransactionDto;
import by.testprojects.cardmanagementsystem.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> createCard(
            @Valid @RequestBody CreateCardRequest request) {
        CardDto card = cardService.createCard(request);
        return ResponseEntity.created(URI.create("/api/cards/" + card.getId()))
                .body(card);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCard(@PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(
            @PathVariable UUID id,
            @RequestParam CardStatus status) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, status));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getCardTransactions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(cardService.getCardTransactions(id));
    }
}
