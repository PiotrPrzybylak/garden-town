package org.commontech.gardentown.domain.finance;

import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class Lease {
    public Holder holder;
    public Optional<Holder> coholder;
}
