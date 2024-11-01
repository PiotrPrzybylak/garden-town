package org.commontech.gardentown.finance;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@ToString
class SubAccount {
    SubAccountType type;
    BigDecimal amount;
}
