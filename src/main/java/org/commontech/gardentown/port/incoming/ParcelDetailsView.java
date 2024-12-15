package org.commontech.gardentown.port.incoming;

import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;

import java.util.UUID;

public interface ParcelDetailsView {

    ParcelDetails get(UUID id);

    record ParcelDetails(Parcel parcel, Lease lease) {

    }
}
