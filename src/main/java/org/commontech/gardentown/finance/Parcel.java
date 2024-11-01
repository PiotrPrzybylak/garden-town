package org.commontech.gardentown.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.commontech.gardentown.finance.Event.Type.START;

class Parcel {

    private LocalDate start;
    private List<SubAccount> subAccounts = new ArrayList<>();


    public Parcel(List<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public Parcel(LocalDate start) {
        this.start = start;
    }

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

    public BigDecimal sum() {
        BigDecimal total = BigDecimal.ZERO;
        for (SubAccount subAccount : subAccounts) {
            total = total.add(subAccount.amount);
        }
        return total;
    }

    public List<SubAccount> subAccounts() {
        return subAccounts;
    }

    public List<Event> history() {
        return List.of(new Event(START, new Balance(Arrays.stream(SubAccountType.values()).map((t) -> new SubAccount(t, BigDecimal.ZERO)).toList())));
    }
}
