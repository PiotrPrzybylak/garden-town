package org.commontech.gardentown.finance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelTest {

    @Test
    void test() {
        Parcel parcel = new Parcel(LocalDate.parse("2024-01-01"));

        List<Event> history = parcel.history();

        assertThat(history).containsExactly(new Event(Event.Type.START,
                new Balance(List.of(
                        new SubAccount(SubAccountType.MEMBERSHIP, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.GARDEN, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.ELECTRICITY_USAGE, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.ELECTRICITY_LOSS, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.WATER_USAGE, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.WATER_LOSS, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.TRASH, BigDecimal.ZERO)
                ))));
    }

}