package org.commontech.gardentown.domain;

import lombok.Getter;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class Garden {
    private final List<Parcel> parcels = new ArrayList<>();
    private final Map<String, Lease> leases = new HashMap<>();

    public Parcel getParcelById(UUID id) {
        for (Parcel parcel : getParcels()) {
            if (parcel.id.equals(id)) return parcel;
        }
        return null;
    }

    public Parcel getParcelByNumber(String number) {
        for (Parcel parcel : getParcels()) {
            if (parcel.number.equals(number)) return parcel;
        }
        return null;
    }

    public void clean() {
        parcels.clear();
        leases.clear();
    }
}
