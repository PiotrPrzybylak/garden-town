package org.commontech.gardentown.port.incoming;

import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public interface ImportGardenUseCase {
    void importLeases(Map<String, Lease> leases);

    void importParcels(Collection<Parcel> parcels);

    void setInitialBalance(String parcelNumber, BigDecimal balance);
}
