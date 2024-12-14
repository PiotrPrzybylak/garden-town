package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookingProposal {
    public List<SubPayment> subPayments = new ArrayList<>();
    public BigDecimal excess;
}
