package org.commontech.gardentown.port.incoming;

import org.commontech.gardentown.domain.finance.Parcel;

import java.util.List;

public interface ParcelsView {
    List<Parcel> get();
}
