package org.commontech.gardentown.port.incoming;

import org.commontech.gardentown.domain.finance.BookingProposal;

import java.util.UUID;

public interface BookPaymentUseCase {
    void apply(UUID id, BookingProposal bookingProposal);
}
