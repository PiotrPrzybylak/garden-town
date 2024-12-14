package org.commontech.gardentown.view;

import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.infrastructure.adapter.outgoing.persistence.InMemoryGarden;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
record InMemoryParcelsView(InMemoryGarden garden) implements org.commontech.gardentown.port.incoming.ParcelsView {
    @Override
    public List<Parcel> get() {
        return garden.garden.getParcels();
    }
}
