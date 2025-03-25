package com.stephen_parinas.budget_tracker.dto;

public class VerifyUserDto {
    private String email;
    private String verificationCode;

    // Getters
    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
