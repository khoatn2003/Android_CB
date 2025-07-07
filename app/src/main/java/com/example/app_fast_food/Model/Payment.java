package com.example.app_fast_food.Model;

import java.io.Serializable;

public class Payment implements Serializable {
    private int paymentId;
    private int orderId;
    private String method;
    private String paymentDate;

    public Payment(int paymentId, int orderId, String method, String paymentDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.method = method;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getMethod() {
        return method;
    }

    public String getPaymentDate() {
        return paymentDate;
    }
}

