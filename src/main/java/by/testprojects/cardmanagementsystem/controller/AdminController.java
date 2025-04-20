package by.testprojects.cardmanagementsystem.controller;

import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.SetCardLimitRequest;
import by.testprojects.cardmanagementsystem.entity.dto.UserDto;
import by.testprojects.cardmanagementsystem.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Получить всех пользователей
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // Удалить пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Получить все карты пользователя
    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<CardResponseDto>> getUserCards(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getCardsByUserId(userId));
    }

    // Установить лимиты на карту
    @PutMapping("/cards/{cardId}/limits")
    public ResponseEntity<Void> setCardLimits(@PathVariable UUID cardId,
                                              @Valid @RequestBody SetCardLimitRequest request) {
        adminService.setCardLimits(cardId, request);
        return ResponseEntity.noContent().build();
    }
}
