package org.commontech.gardentown.infrastructure.adapter.outgoing.spreadsheet;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commontech.gardentown.domain.Garden;
import org.commontech.gardentown.domain.finance.Fee;
import org.commontech.gardentown.domain.finance.Fees;
import org.commontech.gardentown.domain.finance.Holder;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.commontech.gardentown.domain.finance.Payment;
import org.commontech.gardentown.domain.finance.SubAccountType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SpreadSheetImporter {


    public void importFromSpreadSheet(InputStream inputStream, Garden garden) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            garden.getLeases().putAll(importLeases(workbook));
            garden.getParcels().addAll(importParcels(workbook).values());
            Map<String, BigDecimal> balances = importBalances(workbook);
            System.out.println(balances);
            balances.forEach((number, balance) -> {
                Parcel parcel = garden.getParcelByNumber(number);
                if (parcel == null) {
                    log.error("No parcel found for number: {}.", number);
                    return;
                }
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    parcel.chargeFees(LocalDate.now(), new Fees(new Fee(SubAccountType.OTHER, balance)));
                }
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    parcel.addPayment(LocalDate.now(), new Payment(balance));
                }
            });
        }
    }

    private Map<String, BigDecimal> importBalances(Workbook workbook) {
        Sheet parcelsData = workbook.getSheetAt(5);
        Map<String, BigDecimal> balances = new HashMap<>();
        for (Row parcel : parcelsData) {
            if (parcel.getRowNum() < 2) {
                continue;
            }
            Cell balance = parcel.getCell(20);
            if (balance == null || balance.getCellType() != CellType.FORMULA) {
                continue;
            }
            Cell cellWithNumber = parcel.getCell(0);
            if (cellWithNumber == null) {
                continue;
            }

            if (cellWithNumber.getCellType() != CellType.STRING) {
                log.error("{} - {}", cellWithNumber, cellWithNumber.getCellType());
                continue;
            }

            String number = cellWithNumber.getStringCellValue();
            balances.put(number, new BigDecimal(balance.getNumericCellValue()));
            log.info("Balance for parcel {} : {}.", number, balances.get(number));
        }

        return balances;
    }

    private static Map<String, Parcel> importParcels(Workbook workbook) {
        Sheet parcelsData = workbook.getSheetAt(34);
        Map<String, Parcel> parcels = new HashMap<>();
        int nullParcelCounter = 0;
        for (Row parcel : parcelsData) {
            Cell area = parcel.getCell(2);
            if (area == null) {
                nullParcelCounter++;
                continue;
            }
            if (area.getCellType() != CellType.NUMERIC) continue;
            String number = parcel.getCell(0).getStringCellValue();
            parcels.put(number, new Parcel(UUID.randomUUID(), number, LocalDate.now(), (int) area.getNumericCellValue()));
        }

        log.info("Parcels found: {}.", parcels.size());
        return parcels;
    }

    private static Map<String, Lease> importLeases(Workbook workbook) {
        Map<String, Lease> leases = new HashMap<>();
        Sheet leasesData = workbook.getSheetAt(4);
        int nullRenterCounter = 0;
        int totalRenterCounter = 0;
        for (Row lease : leasesData) {
            totalRenterCounter++;
            Cell cellWithNumber = lease.getCell(0);
            log.info("READING row {} cell 0: {}", totalRenterCounter, cellWithNumber);
            if (cellWithNumber == null) {
                nullRenterCounter++;
                continue;
            }
            if (cellWithNumber.getRowIndex() == 0) {
                continue;
            }

            DataFormatter dataFormatter = new DataFormatter();
            String number = cellWithNumber.getStringCellValue();
            String holderFirstName = lease.getCell(2).getStringCellValue();
            String holderLastName = lease.getCell(3).getStringCellValue();
            String holderPESEL = dataFormatter.formatCellValue(lease.getCell(4));
            String holderPhone = Integer.valueOf((int) lease.getCell(5).getNumericCellValue()).toString();
            String holderEmail = lease.getCell(6).getStringCellValue();
            leases.putIfAbsent(number, new Lease(new Holder(holderFirstName, holderLastName, holderPESEL, holderPhone, holderEmail), Optional.empty()));
        }
        log.info("Null renters found: {}.", nullRenterCounter);
        log.info("Renters found: {}.", leases.size());
        return leases;
    }

}
