package org.commontech.gardentown.view;

import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.infrastructure.adapter.outgoing.persistence.InMemoryGarden;
import org.commontech.gardentown.port.incoming.ParcelDetailsView;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
record InMemoryParcelDetailsView(InMemoryGarden garden) implements ParcelDetailsView {
    @Override
    public ParcelDetails get(UUID id) {
        Parcel parcel = garden.garden.getParcelById(id);
        Lease lease = garden.garden.getLeases().get(parcel.number);
        return new ParcelDetails(parcel, lease);
    }
}
