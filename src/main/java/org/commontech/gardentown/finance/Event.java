package org.commontech.gardentown.finance;

record Event(Type type, Balance balance) {
    public enum Type {START}
}
