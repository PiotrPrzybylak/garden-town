package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;

record SubPayment(SubAccountType type, BigDecimal amount) {
}
