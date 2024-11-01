package org.commontech.gardentown.finance;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;

@Controller
class FinanceController {

    @GetMapping("/")
    String showBalance(Model model) {
        Parcel parcel = new Parcel(new ArrayList<>());
        parcel.chargeFee(SubAccountType.MEMBERSHIP, new BigDecimal("6"));
        parcel.chargeFee(SubAccountType.GARDEN, new BigDecimal("255"));
        parcel.chargeFee(SubAccountType.WATER_LOSS, new BigDecimal("25"));
        parcel.chargeFee(SubAccountType.WATER_USAGE, new BigDecimal("0"));
        parcel.chargeFee(SubAccountType.ELECTRICITY_LOSS, new BigDecimal("30"));
        parcel.chargeFee(SubAccountType.ELECTRICITY_USAGE, new BigDecimal("0"));
        parcel.chargeFee(SubAccountType.TRASH, new BigDecimal("350"));
        model.addAttribute(parcel);
        return "balance";
    }

}
