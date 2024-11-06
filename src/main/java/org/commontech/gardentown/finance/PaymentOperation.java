package org.commontech.gardentown.finance;

public record PaymentOperation(Payment payment) implements Operation {

    @Override
    public String toString() {
        return payment.amount().toString();
    }
}
