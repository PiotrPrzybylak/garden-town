package org.commontech.gardentown.domain.finance;

import java.math.BigDecimal;

public record Fee(SubAccountType subAccountType, BigDecimal amount) {
}
