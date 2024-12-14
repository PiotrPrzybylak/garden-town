package org.commontech.gardentown.domain.finance;

import org.commontech.gardentown.port.incoming.BookPaymentUseCase;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
record BookPaymentService(Garden garden) implements BookPaymentUseCase {
    @Override
    public void apply(UUID id, BookingProposal bookingProposal) {
        Parcel parcel = garden.getParcelById(id);
        parcel.addPayment(LocalDate.now(), bookingProposal);
    }
}
