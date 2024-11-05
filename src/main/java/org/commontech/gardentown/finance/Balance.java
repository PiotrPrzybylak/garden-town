package org.commontech.gardentown.finance;

import java.math.BigDecimal;
import java.util.List;

record Balance(List<SubAccount> subAccounts, BigDecimal excess) {


    public BigDecimal sum() {
        BigDecimal total = BigDecimal.ZERO;
        for (SubAccount subAccount : subAccounts) {
            total = total.add(subAccount.amount);
        }
        return total.add(excess);
    }

}
