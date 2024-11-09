package org.commontech.gardentown.finance;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Garden {
    private List<Parcel> parcels = new ArrayList<>();
}
