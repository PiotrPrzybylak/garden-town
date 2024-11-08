package org.commontech.gardentown.finance;

record PaymentOperation(Payment payment) implements Operation {

    @Override
    public String toString() {
        return payment.amount().toString();
    }
}
