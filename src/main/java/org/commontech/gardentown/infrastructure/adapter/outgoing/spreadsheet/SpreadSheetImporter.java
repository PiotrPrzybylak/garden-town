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
import org.commontech.gardentown.domain.finance.Holder;
import org.commontech.gardentown.domain.finance.Lease;
import org.commontech.gardentown.domain.finance.Parcel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SpreadSheetImporter {


    public void importFromSpreadSheet(InputStream inputStream, Garden garden) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);

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
            garden.getLeases().putIfAbsent(number, new Lease(new Holder(holderFirstName, holderLastName, holderPESEL, holderPhone, holderEmail), Optional.empty()));
        }
        log.info("Null renters found: {}.", nullRenterCounter);
        log.info("Renters found: {}.", garden.getLeases().size());


        Sheet parcelsData = workbook.getSheetAt(34);
        Map<String, Integer> parcels = new HashMap<>();
        int nullParcelCounter = 0;
        for (Row parcel : parcelsData) {
            Cell area = parcel.getCell(2);
            if (area == null) {
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
                    Lease lease = garden.getLeases().get(number);
                    if (lease == null) {
                        log.error("Renter not found for parcel: {}", number);
                    }
                    log.info("Importing parcel {} with renter: {}.", number, lease.holder);
                    garden.getParcels().add(new Parcel(UUID.randomUUID(), number, LocalDate.now(), area));

                }

        );
    }

}
