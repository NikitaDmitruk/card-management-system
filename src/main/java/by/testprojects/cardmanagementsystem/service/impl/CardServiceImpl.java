package by.testprojects.cardmanagementsystem.service.impl;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.entity.Role;
import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import by.testprojects.cardmanagementsystem.entity.dto.TransactionDto;
import by.testprojects.cardmanagementsystem.entity.mapper.CardMapper;
import by.testprojects.cardmanagementsystem.entity.mapper.TransactionMapper;
import by.testprojects.cardmanagementsystem.exception.CardNotFoundException;
import by.testprojects.cardmanagementsystem.exception.CardOperationException;
import by.testprojects.cardmanagementsystem.repository.CardRepository;
import by.testprojects.cardmanagementsystem.repository.TransactionRepository;
import by.testprojects.cardmanagementsystem.entity.User;
import by.testprojects.cardmanagementsystem.security.service.JwtService;
import by.testprojects.cardmanagementsystem.service.CardService;
import by.testprojects.cardmanagementsystem.service.UserService;
import by.testprojects.cardmanagementsystem.service.CardGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final CardGeneratorService cardGeneratorService;
    private final UserService userService;

    @Override
    @Transactional
    public CardResponseDto createCard(CreateCardRequest request, Long userId) {
        User user = userService.findById(userId);
        Card card = cardGeneratorService.generateCard(request, user);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDto(savedCard);
    }

    @Override
    @Transactional
    public Card saveCard(Card card) {
        if (cardRepository.existsById(card.getId())) {
            throw new CardOperationException("Card already exists");
        }
        return cardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public Card getCardById(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponseDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(cardMapper::toDto);
    }

    @Override
    @Transactional
    public CardResponseDto updateCardStatus(UUID id, CardStatus newStatus) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new CardOperationException("Cannot change status of expired card");
        }

        card.setStatus(newStatus);
        Card updatedCard = cardRepository.save(card);
        return cardMapper.toDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        if (card.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new CardOperationException("Cannot delete card with non-zero balance");
        }

        cardRepository.delete(card);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCardBalance(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return card.getBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getCardTransactions(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        return transactionRepository.findByCardId(id)
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CardResponseDto> getCardsByUserId(Long userId) {
        User user = userService.findById(userId);
        return user.getCards().stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<Card> getAllCardsByUserId(Long id) {
        return cardRepository.getAllByUserId(id);
    }

    @Override
    public Page<CardResponseDto> getUserCards(User principal, Pageable pageable) {
        Long userId = principal.getId();
        Set<Role> roles = principal.getRoles();
        boolean isAdmin = roles.contains(Role.ADMIN);

        Page<Card> cards = isAdmin
                ? cardRepository.findAll(pageable)
                : cardRepository.getAllByUserId(userId, pageable);

        return cards.map(cardMapper::toDto);
    }
}
