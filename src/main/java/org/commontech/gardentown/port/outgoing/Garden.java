package org.commontech.gardentown.port.outgoing;

import org.commontech.gardentown.domain.finance.Parcel;

import java.util.UUID;

public interface Garden {

    Parcel getParcelById(UUID id);
}
