package com.example.storeordersmongo.model;

import java.time.Instant;

public class PaymentInfo {

    private Boolean paid;
    private String paymentMethod;
    private String transactionReference;
    private Instant paidAt;

    public PaymentInfo() {
    }

    public PaymentInfo(Boolean paid, String paymentMethod, String transactionReference, Instant paidAt) {
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.paidAt = paidAt;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }
}
