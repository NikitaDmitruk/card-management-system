package by.testprojects.cardmanagementsystem.entity.dto;

import by.testprojects.cardmanagementsystem.entity.CardType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardRequest {

    // Имя держателя карты
    @NotBlank(message = "Имя держателя карты обязательно")
    @Size(
            min = 2,
            max = 100,
            message = "Имя держателя карты должно содержать от 2 до 100 символов"
    )
    @Pattern(
            regexp = "^[A-Z]{2,} [A-Z]{2,}$",
            message = "Имя владельца карты должно быть указано в формате ИМЯ ФАМИЛИЯ (заглавные латинские буквы, без ударений)."
    )
    private String cardHolder;

    // Тип карты
    @NotNull(message = "Тип карты обязателен")
    private CardType cardType; // VISA, MASTERCARD и т.д.

    // Срок действия карты (в годах)
    @NotNull(message = "Срок действия карты обязателен")
    @Min(
            value = 1,
            message = "Минимальный срок действия карты - 1 год"
    )
    @Max(
            value = 5,
            message = "Максимальный срок действия карты - 5 лет"
    )
    private Integer durationYears;

    // Начальный баланс
    @NotNull(message = "Начальный баланс обязателен")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Начальный баланс должен быть больше 0"
    )
    @Digits(
            integer = 10,
            fraction = 2,
            message = "Начальный баланс должен иметь не более 10 цифр до запятой и 2 после"
    )
    private BigDecimal initialBalance;
}
