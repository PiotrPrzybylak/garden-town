package org.commontech.gardentown.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
class FinanceController {

    private final Garden garden = new Garden();

    FinanceController() {
        garden.getParcels().add(new Parcel("I-1", LocalDate.now(), 200));
        garden.getParcels().add(new Parcel("I-2", LocalDate.now(), 255));
        garden.getParcels().add(new Parcel("IV-5a", LocalDate.now(), 302));

        Parcel parcel = new Parcel("II-111", LocalDate.parse("2024-01-01"), 255);
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
        garden.getParcels().add(parcel);
    }

    @GetMapping("/")
    String showBalance(Model model) {
        Parcel parcel = getParcelById("II-111");
        model.addAttribute("parcel", parcel);
        model.addAttribute("subaccounts", SubAccountType.values());

        return "balance";
    }


    @GetMapping("/parcels")
    String parcels(Model model) {
        model.addAttribute("parcels", garden.getParcels());
        return "parcels";
    }

    @GetMapping("/parcels/{id}")
    String parcel(@PathVariable String id, Model model) {
        Parcel parcel = getParcelById(id);
        model.addAttribute("parcel", parcel);
        model.addAttribute("subaccounts", SubAccountType.values());
        return "balance";
    }

    private Parcel getParcelById(String id) {
        for (Parcel parcel : garden.getParcels()) {
            if (parcel.id.equals(id)) return parcel;
        }
        return null;
    }

    @PostMapping("/payments")
    String showBalance(String id, BigDecimal amount) {
        Parcel parcel = getParcelById(id);
        parcel.addPayment(LocalDate.now(), new Payment(amount));
        return "redirect:/parcels/" + id;
    }

}
