package org.commontech.gardentown.port.outgoing;

import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Garden {

    Parcel getParcelById(UUID id);

    Parcel getParcelByNumber(String number);

    void delete();

    List<UUID> getAllParcelIds();

    void addParcels(Collection<Parcel> parcels);

    void addLeases(Map<String, Lease> leases);
}
