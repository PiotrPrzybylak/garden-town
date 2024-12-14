package org.commontech.gardentown.domain.finance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ChargeFeesTest {

    @Test
    void chargingFees() {

        Parcel parcel = new ParcelBuilder().build();

        parcel.chargeFees(LocalDate.parse("2024-01-02"), new Fees(
                new Fee(SubAccountType.MEMBERSHIP, new BigDecimal("1.23")),
                new Fee(SubAccountType.GARDEN, new BigDecimal("1.23")),
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("1.23")),
                new Fee(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("1.23")),
                new Fee(SubAccountType.WATER_USAGE, new BigDecimal("1.23")),
                new Fee(SubAccountType.WATER_LOSS, new BigDecimal("1.23")),
                new Fee(SubAccountType.TRASH, new BigDecimal("1.23"))));


        assertThat(parcel.subAccounts()).containsExactly(
                new SubAccount(SubAccountType.MEMBERSHIP, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.GARDEN, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.WATER_USAGE, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.WATER_LOSS, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.TRASH, new BigDecimal("-1.23")),
                new SubAccount(SubAccountType.OTHER, new BigDecimal("0"))
        );

    }
}
