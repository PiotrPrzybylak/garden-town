package org.commontech.gardentown.view;

import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.port.incoming.ParcelsView;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
record InMemoryParcelsView(Garden garden) implements ParcelsView {
    @Override
    public List<Parcel> get() {
        return garden.getParcels();
    }
}
