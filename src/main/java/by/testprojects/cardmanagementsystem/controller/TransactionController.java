package by.testprojects.cardmanagementsystem.controller;

import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.entity.dto.*;
import by.testprojects.cardmanagementsystem.service.TransactionService;
import by.testprojects.cardmanagementsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
//
//    @PostMapping("/deposit")
//    public ResponseEntity<TransactionDto> deposit(
//            @Valid @RequestBody TransactionRequestDto request) {
//        return ResponseEntity.ok(
//                transactionService.createDeposit(
//                        request.getCardId(),
//                        request.getAmount(),
//                        request.getDescription()
//                )
//        );
//    }

    @PostMapping("/withdrawal")
    public ResponseEntity<WithdrawalResponseDto> withdrawal(
            @Valid @RequestBody WithdrawalRequestDto request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                transactionService.withdraw(request, user)
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(
            @Valid @RequestBody TransferRequestDto request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.transferBetweenSameOwner(request, user));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @RequestParam(required = false) UUID cardId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User principal
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDto> transactions = transactionService.getFilteredTransactions(principal, cardId, fromDate, toDate, pageable);
        return ResponseEntity.ok(transactions);
    }

}