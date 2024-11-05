package org.commontech.gardentown.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.commontech.gardentown.finance.Event.Type.FEES;
import static org.commontech.gardentown.finance.Event.Type.PAYMENT;
import static org.commontech.gardentown.finance.Event.Type.REBALANCE;
import static org.commontech.gardentown.finance.Event.Type.START;

class Parcel {

    private LocalDate start;
    private List<SubAccount> subAccounts = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    BigDecimal excessPayment = BigDecimal.ZERO;


    public Parcel(List<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public Parcel(LocalDate start) {
        this.start = start;
        for (SubAccountType type : SubAccountType.values()) {
            subAccounts.add(new SubAccount(type, BigDecimal.ZERO));
        }
        events.add(new Event(START, start, getBalance()));
    }

    private Balance getBalance() {
        return new Balance(subAccounts.stream().map((s) -> new SubAccount(s.getType(), s.getAmount())).toList(), excessPayment);
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
        return events;
    }

    public void chargeFees(LocalDate date, Fees fees) {
        Arrays.stream(fees.fees()).forEach(this::chargeFee);
        events.add(new Event(FEES, date, getBalance()));
        if (excessPayment.compareTo(BigDecimal.ZERO) > 0) {
            BookingProposal bookingProposal = new Payment(excessPayment).proposeBooking(this);
            bookingProposal.subPayments.forEach(this::addSubPayment);
            excessPayment = bookingProposal.excess;
            events.add(new Event(REBALANCE, date, getBalance()));

        }
    }

    private void chargeFee(Fee fee) {
        chargeFee(fee.subAccountType(), fee.amount());
    }

    public void addPayment(LocalDate date, Payment payment) {
        BookingProposal bookingProposal = payment.proposeBooking(this);
        bookingProposal.subPayments.forEach(this::addSubPayment);
        excessPayment = excessPayment.add(bookingProposal.excess);
        events.add(new Event(PAYMENT, date, getBalance()));
    }

    private void addSubPayment(SubPayment subPayment) {
        getSubAccount(subPayment.type()).addPayment(subPayment.amount());
    }

    public List<Event> getEvents() {
        return events;
    }
}
