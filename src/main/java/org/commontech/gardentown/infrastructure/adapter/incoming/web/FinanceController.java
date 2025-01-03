package org.commontech.gardentown.infrastructure.adapter.incoming.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.commontech.gardentown.domain.finance.BookingProposal;
import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.commontech.gardentown.domain.finance.SubPayment;
import org.commontech.gardentown.infrastructure.adapter.incoming.spreadsheet.SpreadSheetImporter;
import org.commontech.gardentown.port.incoming.BookPaymentUseCase;
import org.commontech.gardentown.port.incoming.ChargeFeesUseCase;
import org.commontech.gardentown.port.incoming.DeleteGardenUseCase;
import org.commontech.gardentown.port.incoming.ParcelDetailsView;
import org.commontech.gardentown.port.incoming.ParcelsView;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
class FinanceController {

    private final SpreadSheetImporter spreadSheetImporter;
    private final ChargeFeesUseCase chargeFeesUseCase;
    private final ParcelsView parcelsView;
    private final BookPaymentUseCase bookPaymentUseCase;
    private final ParcelDetailsView parcelDetailsView;
    private final DeleteGardenUseCase deleteGardenUseCase;

    FinanceController(SpreadSheetImporter spreadSheetImporter,
                      ChargeFeesUseCase chargeFeesUseCase,
                      ParcelsView parcelsView,
                      BookPaymentUseCase bookPaymentUseCase,
                      ParcelDetailsView parcelDetailsView,
                      DeleteGardenUseCase deleteGardenUseCase) {
        this.spreadSheetImporter = spreadSheetImporter;
        this.chargeFeesUseCase = chargeFeesUseCase;
        this.parcelsView = parcelsView;
        this.bookPaymentUseCase = bookPaymentUseCase;
        this.parcelDetailsView = parcelDetailsView;
        this.deleteGardenUseCase = deleteGardenUseCase;
    }

    @GetMapping({"/parcels", "/"})
    String parcels(Model model) {
        List<Parcel> parcels = parcelsView.get();
        parcels.sort(Comparator.comparing(o -> o.number));
        model.addAttribute("parcels", parcels);
        model.addAttribute("subaccounts", SubAccountType.values());
        return "parcels";
    }

    @GetMapping("/parcels/{id}")
    String parcel(@PathVariable UUID id, Model model, BigDecimal amount) {
        ParcelDetailsView.ParcelDetails details = parcelDetailsView.get(id);
        model.addAttribute("parcel", details.parcel());
        model.addAttribute("subaccounts", SubAccountType.values());
        model.addAttribute("lease", details.lease());

        if (amount != null) {
            BookingProposal bookingProposal = new Payment(amount).proposeBooking(details.parcel());
            Map<SubAccountType, BigDecimal> subpayments = new HashMap<>();
            bookingProposal.subPayments.forEach(subPayment -> subpayments.put(subPayment.type(), subPayment.amount()));
            model.addAttribute("subpayments", subpayments);
            model.addAttribute("excess", bookingProposal.excess);
        }

        return "balance";
    }

    @PostMapping("/payments")
    String addPayment(UUID id, HttpServletRequest request) {
        bookPaymentUseCase.apply(id, getBookingProposal(request));
        return "redirect:/parcels/" + id;
    }

    @PostMapping("/fees")
    String addFees(UUID id, HttpServletRequest request) {
        if (id != null) {
            chargeFeesUseCase.chargeForParcel(id, getFees(request));
        } else {
            chargeFeesUseCase.chargeForAll(getFees(request));
        }
        return "redirect:/parcels";
    }

    private static Fees getFees(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        SubAccountType[] subAccountTypes = SubAccountType.values();
        Fee[] fees = new Fee[subAccountTypes.length];
        for (int i = 0; i < subAccountTypes.length; i++) {
            SubAccountType type = subAccountTypes[i];
            BigDecimal amount = new BigDecimal(parameterMap.get(type.name())[0]);
            fees[i] = new Fee(type, amount);
        }
        return new Fees(fees);
    }

    private static BookingProposal getBookingProposal(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        SubAccountType[] subAccountTypes = SubAccountType.values();
        List<SubPayment> subPayments = new ArrayList<>();
        for (SubAccountType type : subAccountTypes) {
            String[] values = parameterMap.get(type.name());
            if (values.length > 0 && StringUtils.hasText(values[0])) {
                BigDecimal amount = new BigDecimal(values[0]);
                subPayments.add(new SubPayment(type, amount));
            }
        }
        BookingProposal bookingProposal = new BookingProposal();
        bookingProposal.subPayments = subPayments;
        bookingProposal.excess = new BigDecimal(parameterMap.get("excess")[0]);
        return bookingProposal;
    }

    @GetMapping("/upload")
    void upload() {
    }

    @PostMapping("/upload")
    String upload(@RequestParam("file") MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            spreadSheetImporter.importFromSpreadSheet(inputStream);
        }
        return "redirect:/parcels";
    }


    @GetMapping("/delete_all")
    void deleteAllForm() {
    }

    @PostMapping("/delete_all")
    String deleteAll() {
        deleteGardenUseCase.apply();
        return "redirect:/";
    }


}
