package com.stephen_parinas.budget_tracker.model;

import com.stephen_parinas.budget_tracker.model.common.AuditableEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * The user entity.
 */
@Entity
@Table(name = "users")
public class User extends AuditableEntity implements UserDetails {

    /**
     * The first name of the user.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * The email address of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The hashed password of the user.
     */
    @Column(nullable = false)
    private String password;

    private boolean enabled;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private Instant verificationExpiration;

    public User() {
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public Instant getVerificationExpiration() {
        return verificationExpiration;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setVerificationExpiration(Instant verificationExpiration) {
        this.verificationExpiration = verificationExpiration;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the full name of the user.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
