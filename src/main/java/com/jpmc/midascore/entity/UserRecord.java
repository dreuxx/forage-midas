package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class UserRecord {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private float balance;

    public UserRecord() {}

    public UserRecord(Long id, String username, float balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    // 🔥 Agrega ESTE nuevo constructor
    public UserRecord(String username, float balance) {
        this.username = username;
        this.balance = balance;
    }
    // Getters y setters...
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public float getBalance() {
        return balance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
