package org.commontech.gardentown.domain.finance;

import java.time.LocalDate;

public class ParcelBuilder {

    public Parcel build() {
        return new Parcel(null,"1", LocalDate.parse("2024-01-01"), 100);
    }
}
