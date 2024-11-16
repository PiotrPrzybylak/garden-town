package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class BookingProposal {
    List<SubPayment> subPayments = new ArrayList<>();
    BigDecimal excess;
}
