package org.commontech.gardentown.finance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

record Parcel(List<SubAccount> subAccounts) {

    public void chargeFee(SubAccountType subAccountType, BigDecimal amount) {
        SubAccount subAccount = getSubAccount(subAccountType);
        subAccount.chargeFee(amount);
    }

    private SubAccount getSubAccount(SubAccountType type) {
        Optional<SubAccount> maybeSubAccount = subAccounts.stream().filter(subAccount -> subAccount.type.equals(type)).findFirst();
        SubAccount subAccount = maybeSubAccount.orElse(new SubAccount(type, BigDecimal.ZERO));
        if (maybeSubAccount.isEmpty()) {
            subAccounts.add(subAccount);
        }
        return subAccount;
    }
}
