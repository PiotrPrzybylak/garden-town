package org.commontech.gardentown.port.incoming;

import org.commontech.gardentown.domain.finance.Fees;

import java.util.UUID;

public interface ChargeFeesUseCase {
   void apply(UUID id,  Fees fees);
}
