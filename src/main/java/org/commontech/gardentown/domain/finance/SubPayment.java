package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;

public record SubPayment(SubAccountType type, BigDecimal amount) {
}
