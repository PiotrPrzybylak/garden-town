package org.commontech.gardentown.finance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    void shouldCoverNegativeBalance() {
        Parcel parcel = new Parcel(List.of(new SubAccount(SubAccountType.GARDEN_BASIC_CHARGE, new BigDecimal("-80"))));
        Payment payment = new Payment(new BigDecimal("100.00"));

        BookingProposal bookingProposal = payment.proposeBooking(parcel);

        List<SubPayment> subPayments = bookingProposal.subPayments;
        assertThat(subPayments).containsExactly(
                new SubPayment(SubAccountType.GARDEN_BASIC_CHARGE, new BigDecimal("80"))
        );
    }


    @Test
    void shouldCoverNegativeBalanceUpToTotalPaymentAmount() {

        Parcel parcel = new Parcel(List.of(
                new SubAccount(SubAccountType.GARDEN_BASIC_CHARGE, new BigDecimal("-80")),
                new SubAccount(SubAccountType.ELECTRICITY, new BigDecimal("-80"))
        ));
        Payment payment = new Payment(new BigDecimal("100.00"));

        BookingProposal bookingProposal = payment.proposeBooking(parcel);

        List<SubPayment> subPayments = bookingProposal.subPayments;
        assertThat(subPayments).containsExactly(
                new SubPayment(SubAccountType.GARDEN_BASIC_CHARGE, new BigDecimal("80")),
                new SubPayment(SubAccountType.ELECTRICITY, new BigDecimal("20.00"))

        );
    }

}
