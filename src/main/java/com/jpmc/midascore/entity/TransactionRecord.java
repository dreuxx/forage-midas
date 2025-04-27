package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord recipient;

    private float amount;

    private float preBalance;

    private float incentiveAmount; // ✅ Agregado

    public TransactionRecord() {}

    // Nuevo constructor incluyendo incentiveAmount
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float preBalance, float incentiveAmount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.preBalance = preBalance;
        this.incentiveAmount = incentiveAmount;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public float getPreBalance() {
        return preBalance;
    }

    public float getIncentiveAmount() {
        return incentiveAmount;
    }
}
