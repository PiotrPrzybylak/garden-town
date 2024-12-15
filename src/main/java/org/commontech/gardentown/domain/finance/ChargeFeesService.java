package org.commontech.gardentown.domain.finance;

import org.commontech.gardentown.port.incoming.ChargeFeesUseCase;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
record ChargeFeesService(Garden garden) implements ChargeFeesUseCase {
    @Override
    public void chargeForParcel(UUID id, Fees fees) {
        Parcel parcel = garden.getParcelById(id);
        parcel.chargeFees(LocalDate.now(), fees);
    }

    @Override
    public void chargeForAll(Fees fees) {
        List<UUID> ids = garden.getAllParcelIds();
        ids.forEach(id -> chargeForParcel(id, fees));
    }
}
