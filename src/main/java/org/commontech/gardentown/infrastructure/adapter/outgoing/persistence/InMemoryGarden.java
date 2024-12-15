package org.commontech.gardentown.infrastructure.adapter.outgoing.persistence;

import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Holder;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.commontech.gardentown.port.outgoing.Garden;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryGarden implements Garden {

    private final List<Parcel> parcels = new ArrayList<>();
    private final Map<String, Lease> leases = new HashMap<>();


    public InMemoryGarden() {
        parcels.add(new Parcel(UUID.randomUUID(), "I-1", LocalDate.now(), 200));
        leases.put("I-1", new Lease(new Holder("John", "Smith", "", "", ""), Optional.empty()));
        parcels.add(new Parcel(UUID.randomUUID(), "I-2", LocalDate.now(), 255));
        leases.put("I-2", new Lease(new Holder("", "", "", "", ""), Optional.empty()));
        parcels.add(new Parcel(UUID.randomUUID(), "IV-5a", LocalDate.now(), 302));
        leases.put("IV-5a", new Lease(new Holder("", "", "", "", ""), Optional.empty()));


        Parcel parcel = new Parcel(UUID.randomUUID(), "II-111", LocalDate.parse("2024-01-01"), 255);
        parcel.chargeFees(LocalDate.parse("2024-01-02"), new Fees(
                new Fee(SubAccountType.MEMBERSHIP, new BigDecimal("6")),
                new Fee(SubAccountType.GARDEN, new BigDecimal("255")),
                new Fee(SubAccountType.WATER_LOSS, new BigDecimal("25")),
                new Fee(SubAccountType.WATER_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("30")),
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0")),
                new Fee(SubAccountType.TRASH, new BigDecimal("175"))));
        parcel.addPayment(LocalDate.parse("2024-01-05"), new Payment(new BigDecimal("100")));
        parcel.addPayment(LocalDate.parse("2024-02-05"), new Payment(new BigDecimal("100")));
        parcel.addPayment(LocalDate.parse("2024-03-15"), new Payment(new BigDecimal("100")));
        parcel.addPayment(LocalDate.parse("2024-03-16"), new Payment(new BigDecimal("100")));
        parcel.addPayment(LocalDate.parse("2024-03-17"), new Payment(new BigDecimal("100")));
        parcel.addPayment(LocalDate.parse("2024-03-18"), new Payment(new BigDecimal("100")));
        parcel.chargeFees(LocalDate.parse("2024-07-01"), new Fees(new Fee(SubAccountType.TRASH, new BigDecimal("175"))));
        parcel.addPayment(LocalDate.parse("2024-07-18"), new Payment(new BigDecimal("100")));
        parcel.chargeFees(LocalDate.parse("2024-07-01"), new Fees(
                new Fee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("30")),
                new Fee(SubAccountType.WATER_USAGE, new BigDecimal("10.51"))
        ));
        parcels.add(parcel);
        leases.put("II-111", new Lease(new Holder("", "", "", "", ""), Optional.empty()));
    }

    @Override
    public Parcel getParcelById(UUID id) {
        for (Parcel parcel : parcels) {
            if (parcel.id.equals(id)) return parcel;
        }
        return null;
    }

    @Override
    public Parcel getParcelByNumber(String number) {
        for (Parcel parcel : parcels) {
            if (parcel.number.equals(number)) return parcel;
        }
        return null;
    }

    @Override
    public void delete() {
        parcels.clear();
        leases.clear();
    }

    @Override
    public List<UUID> getAllParcelIds() {
        return parcels.stream().map(p -> p.id).toList();
    }

    @Override
    public void addParcels(Collection<Parcel> parcels) {
        this.parcels.addAll(parcels);
    }

    @Override
    public void addLeases(Map<String, Lease> leases) {
        this.leases.putAll(leases);

    }

    @Override
    public Lease getLeaseByNumber(String number) {
        return leases.get(number);
    }

    @Override
    public List<Parcel> getParcels() {
        return parcels;
    }
}
