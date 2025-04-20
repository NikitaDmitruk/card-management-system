package by.testprojects.cardmanagementsystem.entity.mapper;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.dto.CardResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "expiryDate", expression = "java(formatExpiryDate(card.getExpiryDate()))")
    CardResponseDto toDto(Card card);

    Card toCard(CreateCardRequest request);

    default LocalDate formatExpiryDate(LocalDate expiryDate) {
        return expiryDate.minusDays(1);
    }

}
