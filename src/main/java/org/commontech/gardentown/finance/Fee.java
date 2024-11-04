package org.commontech.gardentown.finance;

import java.math.BigDecimal;

record Fee(SubAccountType subAccountType, BigDecimal amount) {
}
