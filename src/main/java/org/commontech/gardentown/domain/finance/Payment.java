package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;
import java.util.List;

public record Payment(BigDecimal amount) {

    public BookingProposal proposeBooking(Parcel parcel) {

        BigDecimal total = amount;

        List<SubAccount> subAccounts = parcel.subAccounts();
        BookingProposal bookingProposal = new BookingProposal();
        for (SubAccount subAccount : subAccounts) {
            if (subAccount.amount.compareTo(BigDecimal.ZERO) < 0 && total.compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal subAmount = subAccount.amount.negate();
                if (subAmount.compareTo(total) > 0) {
                    subAmount = total;
                }
                bookingProposal.subPayments.add(new SubPayment(subAccount.type, subAmount));
                total = total.subtract(subAmount);

            }
        }
        bookingProposal.excess = total;
        return bookingProposal;
    }
}
