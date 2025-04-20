package by.testprojects.cardmanagementsystem.service.impl;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardLimit;
import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.SetCardLimitRequest;
import by.testprojects.cardmanagementsystem.entity.dto.UserDto;
import by.testprojects.cardmanagementsystem.exception.CardNotFoundException;
import by.testprojects.cardmanagementsystem.repository.CardRepository;
import by.testprojects.cardmanagementsystem.service.AdminService;
import by.testprojects.cardmanagementsystem.service.CardService;
import by.testprojects.cardmanagementsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final CardService cardService;
    private final CardRepository cardRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(user -> new UserDto(user.getId(), user.getEmail()))
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteById(userId);
    }

    @Override
    public List<CardResponseDto> getCardsByUserId(Long userId) {
        return cardService.getCardsByUserId(userId);
    }

    @Override
    public void setCardLimits(UUID cardId, SetCardLimitRequest request) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        CardLimit limit = card.getCardLimit();
        limit.setDailyWithdrawalLimit(request.getDailyWithdrawalLimit());
        limit.setMonthlyWithdrawalLimit(request.getMonthlyWithdrawalLimit());
        limit.setDailyTransferLimit(request.getDailyTransferLimit());
        limit.setMonthlyTransferLimit(request.getMonthlyTransferLimit());
        cardRepository.save(card);
    }
}

