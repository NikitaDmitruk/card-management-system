package by.testprojects.cardmanagementsystem;

import java.math.BigDecimal;

public interface Constants {

    static final String BEARER_PREFIX = "Bearer ";
    static final String AUTHORITY_PREFIX = "ROLE_";
    final String EXPIRY_DATE_FORMAT_FOR_CLIENT = "MM/yy";
    final BigDecimal DEFAULT_CARD_DAILY_WITHDRAWAL_LIMIT = new BigDecimal("150.00");
    final BigDecimal DEFAULT_MONTHLY_WITHDRAWAL_LIMIT = new BigDecimal("5000.00");
    final BigDecimal DEFAULT_MONTHLY_TRANSFER_LIMIT = new BigDecimal("5000.00");
    final BigDecimal DEFAULT_DAILY_TRANSFER_LIMIT = new BigDecimal("150.00");
    String TRANSACTION_STATUS_COMPLETED = "COMPLETED";

}
