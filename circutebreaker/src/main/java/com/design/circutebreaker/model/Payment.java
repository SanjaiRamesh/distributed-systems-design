package com.design.circutebreaker.model;

import java.math.BigDecimal;

public class Payment {

    String firmRootId;
    BigDecimal amount;

    public String getFirmRootId() {
        return firmRootId;
    }

    public void setFirmRootId(String firmRootId) {
        this.firmRootId = firmRootId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
