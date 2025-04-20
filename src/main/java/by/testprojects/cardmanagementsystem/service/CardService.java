package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import by.testprojects.cardmanagementsystem.entity.dto.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CardService {
    CardResponseDto createCard(CreateCardRequest request, Long userId);

    Card getCardById(UUID id);

    Page<CardResponseDto> getAllCards(Pageable pageable);

    CardResponseDto updateCardStatus(UUID id, CardStatus newStatus);

    void deleteCard(UUID id);

    BigDecimal getCardBalance(UUID id);

    List<TransactionDto> getCardTransactions(UUID id);

    List<CardResponseDto> getCardsByUserId(Long userId);

    Card saveCard(Card card);

    List<Card> getAllCardsByUserId(Long id);

    Page<CardResponseDto> getUserCards(User principal, Pageable pageable);
}