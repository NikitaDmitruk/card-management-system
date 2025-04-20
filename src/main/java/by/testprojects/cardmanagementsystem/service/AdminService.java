package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.SetCardLimitRequest;
import by.testprojects.cardmanagementsystem.entity.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    List<UserDto> getAllUsers();
    void deleteUser(Long userId);
    List<CardResponseDto> getCardsByUserId(Long userId);
    void setCardLimits(UUID cardId, SetCardLimitRequest request);
}
