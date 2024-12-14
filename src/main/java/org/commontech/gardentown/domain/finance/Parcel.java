package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.commontech.gardentown.domain.finance.Event.Type.FEES;
import static org.commontech.gardentown.domain.finance.Event.Type.MANUAL_PAYMENT;
import static org.commontech.gardentown.domain.finance.Event.Type.PAYMENT;
import static org.commontech.gardentown.domain.finance.Event.Type.REBALANCE;
import static org.commontech.gardentown.domain.finance.Event.Type.START;

public class Parcel {

    public final String number;
    private LocalDate start;
    private List<SubAccount> subAccounts = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    BigDecimal excessPayment = BigDecimal.ZERO;
    public UUID id;
    public int size;

    public Parcel(UUID id, String number, LocalDate start, int size) {
        this.id = id;
        this.number = number;
        this.start = start;
        this.size = size;
        for (SubAccountType type : SubAccountType.values()) {
            subAccounts.add(new SubAccount(type, BigDecimal.ZERO));
        }
        events.add(new Event(START, start, new Start(), getBalance(), null));
    }

    public Balance getBalance() {
        return new Balance(subAccounts.stream().map((s) -> new SubAccount(s.getType(), s.getAmount())).toList(), excessPayment);
    }

    public void chargeFee(SubAccountType subAccountType, BigDecimal amount) {
        SubAccount subAccount = getSubAccount(subAccountType);
        if (subAccountType == SubAccountType.GARDEN) {
            amount = amount.multiply(new BigDecimal(size));
        }
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
        events.add(new Event(FEES, date, new FeesCharged(), getBalance(), events.getLast()));
        if (excessPayment.compareTo(BigDecimal.ZERO) > 0) {
            rebalance(date);
        }
    }

    private void rebalance(LocalDate date) {
        BookingProposal bookingProposal = new Payment(excessPayment).proposeBooking(this);
        bookingProposal.subPayments.forEach(this::addSubPayment);
        excessPayment = bookingProposal.excess;
        events.add(new Event(REBALANCE, date, new RebalanceOperation(), getBalance(), events.getLast()));
    }

    private void chargeFee(Fee fee) {
        chargeFee(fee.subAccountType(), fee.amount());
    }

    public void addPayment(LocalDate date, Payment payment) {
        boolean needsPayments = sum().compareTo(BigDecimal.ZERO) < 0;
        excessPayment = excessPayment.add(payment.amount());
        events.add(new Event(PAYMENT, date, new PaymentOperation(payment), getBalance(), events.getLast()));
        if (needsPayments) {
            rebalance(date);
        }
    }

    private void addSubPayment(SubPayment subPayment) {
        getSubAccount(subPayment.type()).addPayment(subPayment.amount());
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addPayment(LocalDate date, BookingProposal bookingProposal) {
        bookingProposal.subPayments.forEach(this::addSubPayment);
        excessPayment = bookingProposal.excess;
        events.add(new Event(MANUAL_PAYMENT, date, null, getBalance(), events.getLast()));
    }

}
