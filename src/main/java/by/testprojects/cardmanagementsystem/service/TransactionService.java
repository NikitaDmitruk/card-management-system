package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.entity.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface TransactionService {

    TransferResponseDto transferBetweenSameOwner(TransferRequestDto transferRequest, User user);

    TransactionDto deposit(UUID cardId, BigDecimal amount, User user);

    WithdrawalResponseDto withdraw(WithdrawalRequestDto withdrawalRequest, User user);

    Page<TransactionDto> getFilteredTransactions(User principal, UUID cardId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

}
