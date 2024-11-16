package org.commontech.gardentown.domain;

import lombok.Getter;
import org.commontech.gardentown.domain.finance.Parcel;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Garden {
    private List<Parcel> parcels = new ArrayList<>();
}
