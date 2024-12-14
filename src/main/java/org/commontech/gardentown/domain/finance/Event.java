package org.commontech.gardentown.domain.finance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

record Event(Type type, LocalDate date, Operation operation, Balance balance, Event previous) {
    public enum Type {START, FEES, PAYMENT, REBALANCE, MANUAL_PAYMENT}

    public Balance diff() {
        List<SubAccount> subAccounts = new ArrayList<>();

        List<SubAccount> currentBalance = balance.subAccounts();
        List<SubAccount> previousBalance = previous.balance.subAccounts();
        for (int i = 0; i < currentBalance.size(); i++) {
            SubAccount current = currentBalance.get(i);
            SubAccount previous = previousBalance.get(i);
            subAccounts.add(new SubAccount(current.getType(), current.amount.subtract(previous.amount)));
        }
        return new Balance(subAccounts, balance.excess().subtract(previous.balance.excess()));
    }
}
