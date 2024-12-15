package org.commontech.gardentown.domain;

import lombok.extern.slf4j.Slf4j;
import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.commontech.gardentown.port.incoming.ImportGardenUseCase;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Service
record ImportGardenService(Garden garden) implements ImportGardenUseCase {
    @Override
    public void importLeases(Map<String, Lease> leases) {
        garden.addLeases(leases);
    }

    @Override
    public void importParcels(Collection<Parcel> parcels) {
        garden.addParcels(parcels);

    }

    @Override
    public void setInitialBalance(String parcelNumber, BigDecimal balance) {
        Parcel parcel = garden.getParcelByNumber(parcelNumber);
        if (parcel == null) {
            log.error("No parcel found for number: {}.", parcelNumber);
            return;
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            parcel.chargeFees(LocalDate.now(), new Fees(new Fee(SubAccountType.OTHER, balance.negate())));
        }
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            parcel.addPayment(LocalDate.now(), new Payment(balance));
        }
    }
}
