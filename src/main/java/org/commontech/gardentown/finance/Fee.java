package org.commontech.gardentown.finance;

import java.math.BigDecimal;

public record Fee(SubAccountType subAccountType, BigDecimal amount) {
}
