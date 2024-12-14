package org.commontech.gardentown.infrastructure.adapter.incoming.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.commontech.gardentown.domain.Garden;
import org.commontech.gardentown.domain.finance.BookingProposal;
import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Holder;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.commontech.gardentown.domain.finance.SubPayment;
import org.commontech.gardentown.infrastructure.adapter.outgoing.spreadsheet.SpreadSheetImporter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
class FinanceController {

    private final SpreadSheetImporter spreadSheetImporter;
    private final Garden garden = new Garden();

    FinanceController(SpreadSheetImporter spreadSheetImporter) {
        this.spreadSheetImporter = spreadSheetImporter;
        garden.getParcels().add(new Parcel(UUID.randomUUID(), "I-1", LocalDate.now(), 200));
        garden.getLeases().put("I-1", new Lease(new Holder("John", "Smith", "", "", ""), Optional.empty()));
        garden.getParcels().add(new Parcel(UUID.randomUUID(), "I-2", LocalDate.now(), 255));
        garden.getLeases().put("I-2", new Lease(new Holder("", "", "", "", ""), Optional.empty()));
        garden.getParcels().add(new Parcel(UUID.randomUUID(), "IV-5a", LocalDate.now(), 302));
        garden.getLeases().put("IV-5a", new Lease(new Holder("", "", "", "", ""), Optional.empty()));


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
        garden.getParcels().add(parcel);
        garden.getLeases().put("II-111", new Lease(new Holder("", "", "", "", ""), Optional.empty()));
    }

    @GetMapping({"/parcels", "/"})
    String parcels(Model model) {
        List<Parcel> parcels = garden.getParcels();
        parcels.sort(Comparator.comparing(o -> o.number));
        model.addAttribute("parcels", parcels);
        model.addAttribute("subaccounts", SubAccountType.values());
        return "parcels";
    }

    @GetMapping("/parcels/{id}")
    String parcel(@PathVariable UUID id, Model model, BigDecimal amount) {
        Parcel parcel = garden.getParcelById(id);
        model.addAttribute("parcel", parcel);
        model.addAttribute("subaccounts", SubAccountType.values());
        model.addAttribute("lease", garden.getLeases().get(parcel.number));

        if (amount != null) {
            BookingProposal bookingProposal = new Payment(amount).proposeBooking(parcel);
            Map<SubAccountType, BigDecimal> subpayments = new HashMap<>();
            bookingProposal.subPayments.forEach(subPayment -> subpayments.put(subPayment.type(), subPayment.amount()));
            System.out.println(subpayments);
            model.addAttribute("subpayments", subpayments);
            model.addAttribute("excess", bookingProposal.excess);
        }

        return "balance";
    }

    @PostMapping("/payments")
    String addPayment(UUID id, HttpServletRequest request) {
        Parcel parcel = garden.getParcelById(id);
        addPaymentToParcel(parcel, request);
        return "redirect:/parcels/" + id;
    }

    @PostMapping("/fees")
    String addFees(UUID id, HttpServletRequest request) {
        List<Parcel> parcels = id != null ? List.of(garden.getParcelById(id)) : garden.getParcels();
        for (Parcel parcel : parcels) {
            addFeesToParcel(parcel, request);
        }
        return "redirect:/parcels";
    }

    private static void addFeesToParcel(Parcel parcel, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        SubAccountType[] subAccountTypes = SubAccountType.values();
        Fee[] fees = new Fee[subAccountTypes.length];
        for (int i = 0; i < subAccountTypes.length; i++) {
            SubAccountType type = subAccountTypes[i];
            BigDecimal paramValue = new BigDecimal(parameterMap.get(type.name())[0]);
            BigDecimal amount = (type == SubAccountType.GARDEN) ? paramValue.multiply(new BigDecimal(parcel.size)) : paramValue;
            fees[i] = new Fee(type, amount);
        }
        parcel.chargeFees(LocalDate.now(), new Fees(fees));
    }

    private static void addPaymentToParcel(Parcel parcel, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        SubAccountType[] subAccountTypes = SubAccountType.values();
        List<SubPayment> subPayments = new ArrayList<>();
        for (int i = 0; i < subAccountTypes.length; i++) {
            SubAccountType type = subAccountTypes[i];
            String[] values = parameterMap.get(type.name());
            if (values.length > 0 && StringUtils.hasText(values[0])) {
                BigDecimal amount = new BigDecimal(values[0]);
                subPayments.add(new SubPayment(type, amount));
            }
        }
        BookingProposal bookingProposal = new BookingProposal();
        bookingProposal.subPayments = subPayments;
        bookingProposal.excess = new BigDecimal(parameterMap.get("excess")[0]);
        parcel.addPayment(LocalDate.now(), bookingProposal);
    }

    @GetMapping("/upload")
    void upload() {
    }

    @PostMapping("/upload")
    String upload(@RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            spreadSheetImporter.importFromSpreadSheet(inputStream, garden);
        }
        return "redirect:/parcels";
    }


    @GetMapping("/delete_all")
    void deleteAllForm() {
    }

    @PostMapping("/delete_all")
    String deleteAll() {
        garden.clean();
        return "redirect:/";
    }


}
