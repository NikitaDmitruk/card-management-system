package by.testprojects.cardmanagementsystem.entity.mapper;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.Transaction;
import by.testprojects.cardmanagementsystem.entity.dto.TransactionDto;
import by.testprojects.cardmanagementsystem.entity.dto.TransferResponseDto;
import by.testprojects.cardmanagementsystem.entity.dto.WithdrawalResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "cardId", source = "transaction.card.id")
    TransactionDto toDto(Transaction transaction);

    @Mapping(target = "cardMaskedNumber", source = "transaction.card.maskedNumber")
    @Mapping(target = "cardBalance", source = "transaction.card.balance")
    WithdrawalResponseDto toWithdrawalResponse(Transaction transaction, String status);

    @Mapping(target = "transferId", source = "withdrawal.id")
    @Mapping(target = "fromCardMaskedNumber", source = "withdrawal.card.maskedNumber")
    @Mapping(target = "toCardMaskedNumber", source = "deposit.card.maskedNumber")
    @Mapping(target = "amount", source = "withdrawal.amount", qualifiedByName = "absoluteAmount")
    @Mapping(target = "description", expression = "java(joinedDescriptions(" +
            "withdrawal, " +
            "deposit))")
    @Mapping(target = "timestamp", source = "withdrawal.timestamp")
    @Mapping(target = "fromCardNewBalance", source = "fromCard.balance")
    @Mapping(target = "toCardNewBalance", source = "toCard.balance")
    @Mapping(target = "status", source = "status")
    TransferResponseDto toTransferResponse(
            Transaction withdrawal,
            Transaction deposit,
            Card fromCard,
            Card toCard,
            String status);

    @Named("absoluteAmount")
    default BigDecimal absoluteAmount(BigDecimal amount) {
        return amount.abs();
    }

    default String joinedDescriptions(Transaction withdrawal, Transaction deposit) {
        return new StringBuilder("From ")
                .append(withdrawal.getCard().getMaskedNumber())
                .append(" to ")
                .append(deposit.getCard().getMaskedNumber())
                .append(".")
                .toString();
    }
}
