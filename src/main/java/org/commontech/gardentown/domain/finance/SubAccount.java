package org.commontech.gardentown.domain.finance;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class SubAccount {
    SubAccountType type;
    BigDecimal amount;

    public void chargeFee(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void addPayment(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }
}
