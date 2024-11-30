package org.commontech.gardentown.domain;

import lombok.Getter;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Garden {
    private List<Parcel> parcels = new ArrayList<>();
    private Map<String, Lease> leases = new HashMap<>();
}
