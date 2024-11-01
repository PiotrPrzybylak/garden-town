package org.commontech.gardentown.finance;

import java.time.LocalDate;

record Event(Type type, LocalDate date, Balance balance) {
    public enum Type {START}
}
