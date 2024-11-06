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

        assertThat(history).containsExactly(new Event(Event.Type.START, LocalDate.parse("2024-01-01"), new Start(),
                new Balance(List.of(
                        new SubAccount(SubAccountType.MEMBERSHIP, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.GARDEN, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.ELECTRICITY_USAGE, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.ELECTRICITY_LOSS, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.WATER_USAGE, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.WATER_LOSS, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.TRASH, BigDecimal.ZERO),
                        new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                ),
                        BigDecimal.ZERO
                )
        ));
    }


    @Test
    void test2() {
        Parcel parcel = new Parcel(LocalDate.parse("2024-01-01"));

        Fees fees = new Fees(

                new Fee(SubAccountType.MEMBERSHIP, new BigDecimal("6")),
                new Fee(SubAccountType.GARDEN, new BigDecimal("255")),
                new Fee(SubAccountType.WATER_LOSS, new BigDecimal("25")),
                new Fee(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("30")),
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.TRASH, new BigDecimal("350"))
        );
        parcel.chargeFees(LocalDate.parse("2024-01-02"), fees);

        List<Event> history = parcel.history();

        System.out.println(history);

        assertThat(history).containsExactly(
                new Event(Event.Type.START, LocalDate.parse("2024-01-01"), new Start(),
                        new Balance(List.of(
                                new SubAccount(SubAccountType.MEMBERSHIP, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.GARDEN, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.ELECTRICITY_USAGE, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.ELECTRICITY_LOSS, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.WATER_USAGE, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.WATER_LOSS, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.TRASH, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                        ),
                                BigDecimal.ZERO
                        )
                ),
                new Event(Event.Type.FEES, LocalDate.parse("2024-01-02"), new FeesCharged(),
                        new Balance(List.of(
                                new SubAccount(SubAccountType.MEMBERSHIP, new BigDecimal("-6")),
                                new SubAccount(SubAccountType.GARDEN, new BigDecimal("-255")),
                                new SubAccount(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("-30")),
                                new SubAccount(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.WATER_LOSS, new BigDecimal("-25")),
                                new SubAccount(SubAccountType.TRASH, new BigDecimal("-350")),
                                new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                        ),
                                BigDecimal.ZERO
                        )
                )
        );
    }


    @Test
    void test3() {
        Parcel parcel = new Parcel(LocalDate.parse("2024-01-01"));

        Fees fees = new Fees(

                new Fee(SubAccountType.MEMBERSHIP, new BigDecimal("6")),
                new Fee(SubAccountType.GARDEN, new BigDecimal("255")),
                new Fee(SubAccountType.WATER_LOSS, new BigDecimal("25")),
                new Fee(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("30")),
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.TRASH, new BigDecimal("350"))

        );
        parcel.chargeFees(LocalDate.parse("2024-01-02"), fees);

        parcel.addPayment(LocalDate.parse("2024-01-03"), new Payment(new BigDecimal("100")));

        List<Event> history = parcel.history();

        System.out.println(history);

        assertThat(history).containsExactly(
                new Event(Event.Type.START, LocalDate.parse("2024-01-01"), new Start(),
                        new Balance(List.of(
                                new SubAccount(SubAccountType.MEMBERSHIP, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.GARDEN, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.ELECTRICITY_USAGE, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.ELECTRICITY_LOSS, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.WATER_USAGE, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.WATER_LOSS, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.TRASH, BigDecimal.ZERO),
                                new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                        ),
                                BigDecimal.ZERO
                        )
                ),
                new Event(Event.Type.FEES, LocalDate.parse("2024-01-02"), new FeesCharged(),
                        new Balance(List.of(
                                new SubAccount(SubAccountType.MEMBERSHIP, new BigDecimal("-6")),
                                new SubAccount(SubAccountType.GARDEN, new BigDecimal("-255")),
                                new SubAccount(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("-30")),
                                new SubAccount(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.WATER_LOSS, new BigDecimal("-25")),
                                new SubAccount(SubAccountType.TRASH, new BigDecimal("-350")),
                                new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                        ),
                                BigDecimal.ZERO
                        )
                ),
                new Event(Event.Type.PAYMENT, LocalDate.parse("2024-01-03"), new PaymentOperation(new Payment(new BigDecimal("100"))),
                        new Balance(List.of(
                                new SubAccount(SubAccountType.MEMBERSHIP, new BigDecimal("0")),
                                new SubAccount(SubAccountType.GARDEN, new BigDecimal("-161")),
                                new SubAccount(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("-30")),
                                new SubAccount(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                                new SubAccount(SubAccountType.WATER_LOSS, new BigDecimal("-25")),
                                new SubAccount(SubAccountType.TRASH, new BigDecimal("-350")),
                                new SubAccount(SubAccountType.OTHER, BigDecimal.ZERO)
                        ),
                                BigDecimal.ZERO
                        )
                )
        );
    }
}