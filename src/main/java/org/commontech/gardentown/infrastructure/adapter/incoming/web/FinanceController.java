package org.commontech.gardentown.infrastructure.adapter.incoming.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commontech.gardentown.domain.Garden;
import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
class FinanceController {

    private final Garden garden = new Garden();

    FinanceController() {
        garden.getParcels().add(new Parcel(UUID.randomUUID(),"I-1", LocalDate.now(), 200));
        garden.getParcels().add(new Parcel(UUID.randomUUID(), "I-2", LocalDate.now(), 255));
        garden.getParcels().add(new Parcel(UUID.randomUUID(), "IV-5a", LocalDate.now(), 302));

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
    }

    @GetMapping("/")
    String showBalance(Model model) {
        Parcel parcel = garden.getParcels().get(3);
        model.addAttribute("parcel", parcel);
        model.addAttribute("subaccounts", SubAccountType.values());

        return "balance";
    }


    @GetMapping("/parcels")
    String parcels(Model model) {
        List<Parcel> parcels = garden.getParcels();
        parcels.sort(Comparator.comparing(o -> o.number));
        model.addAttribute("parcels", parcels);
        model.addAttribute("subaccounts", SubAccountType.values());
        return "parcels";
    }

    @GetMapping("/parcels/{id}")
    String parcel(@PathVariable UUID id, Model model) {
        Parcel parcel = getParcelById(id);
        model.addAttribute("parcel", parcel);
        model.addAttribute("subaccounts", SubAccountType.values());
        return "balance";
    }

    private Parcel getParcelById(UUID id) {
        for (Parcel parcel : garden.getParcels()) {
            if (parcel.id.equals(id)) return parcel;
        }
        return null;
    }

    @PostMapping("/payments")
    String addPayment(UUID id, BigDecimal amount) {
        Parcel parcel = getParcelById(id);
        parcel.addPayment(LocalDate.now(), new Payment(amount));
        return "redirect:/parcels/" + id;
    }

    @PostMapping("/fees")
    String addFees(UUID id, HttpServletRequest request) {
        List<Parcel> parcels = id != null ? List.of(getParcelById(id)) : garden.getParcels();
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

    @GetMapping("/upload")
    void upload() {
    }

    @PostMapping("/upload")
    String upload(@RequestParam("file") MultipartFile file) throws IOException {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet rentersData = workbook.getSheetAt(4);
        Map<String, String> renters = new HashMap<>();
        int nullRenterCounter = 0;
        for (Row renter : rentersData) {
            Cell cell = renter.getCell(0);
            if (cell == null) {
                nullRenterCounter++;
                continue;
            }
            String number = cell.getStringCellValue();
            renters.put(number, renter.getCell(2).getStringCellValue());
        }
        log.info("Null renters found: {}.", nullRenterCounter);
        log.info("Renters found: {}.", renters.size());


        Sheet parcelsData = workbook.getSheetAt(34);
        Map<String, Integer> parcels = new HashMap<>();
        int nullParcelCounter = 0;
        for (Row parcel : parcelsData) {
            Cell area = parcel.getCell(2);
            if (area == null)  {
                nullParcelCounter++;
                continue;
            }
            if (area.getCellType() != CellType.NUMERIC) continue;
            String number = parcel.getCell(0).getStringCellValue();
            parcels.put(number, (int) area.getNumericCellValue());
        }


        log.info("Null renters found: {}.", nullParcelCounter);
        log.info("Parcels found: {}.", parcels.size());

        parcels.forEach((number, area) -> {
                    String renterLastName = renters.get(number);
                    if (renterLastName == null) {
                        log.error("Renter not found for parcel: {}", number);
                    }
                    log.info("Importing parcel {} with renter: {}.", number, renterLastName);
                    garden.getParcels().add(new Parcel(UUID.randomUUID(), number, LocalDate.now(), area));

                }

        );
        return "redirect:/parcels";
    }

}
