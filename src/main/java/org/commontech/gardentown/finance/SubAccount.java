package org.commontech.gardentown.finance;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
class SubAccount {
    SubAccountType type;
    BigDecimal amount;

    public void chargeFee(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }
}
