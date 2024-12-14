package org.commontech.gardentown.infrastructure.adapter.outgoing.persistence;

import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class InMemoryGarden implements Garden {

    public org.commontech.gardentown.domain.Garden garden = new org.commontech.gardentown.domain.Garden();

    @Override
    public Parcel geParcelById(UUID id) {
        return garden.getParcelById(id);
    }
}
