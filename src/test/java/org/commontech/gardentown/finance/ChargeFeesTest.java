package org.commontech.gardentown.finance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ChargeFeesTest {

    @Test
    void chargingFees() {

        Parcel parcel = new Parcel(new ArrayList<>());

        for (SubAccountType subAccountType : SubAccountType.values()) {
            parcel.chargeFee(subAccountType, new BigDecimal("1.23"));
        }

        assertThat(parcel.subAccounts()).containsExactly(
                new SubAccount(SubAccountType.GARDEN, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("-1.23"))
        );

    }
}
