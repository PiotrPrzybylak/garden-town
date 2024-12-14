package org.commontech.gardentown.domain.finance;

import org.commontech.gardentown.port.incoming.ChargeFeesUseCase;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
record ChargeFeesService(Garden garden) implements ChargeFeesUseCase {
    @Override
    public void apply(UUID id, Fees fees) {
        Parcel parcel = garden.geParcelById(id);
        parcel.chargeFees(LocalDate.now(), fees);
    }
}
