package com.ttn.reap.entities;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @NotNull(message = "First name must have a value")
    private String firstName;

    @NotNull(message = "Last name must have a value")
    private String lastName;
    private String fullName;
    @Transient
    private MultipartFile photo;
    private String photoPath;

    @Email
    private String email;

    @Size(min = 6, message = "Password should be at least 6 characters in length")
    private String password;

    private Boolean active = true;

    private String resetToken;

    private Integer goldShareable = 3;
    private Integer silverShareable = 2;
    private Integer bronzeShareable = 1;
    private Integer goldRedeemable = 0;
    private Integer silverRedeemable = 0;
    private Integer bronzeRedeemable = 0;

    private Integer points = 0;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Role> roleSet = new HashSet<>(Arrays.asList(Role.USER));

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Integer getGoldShareable() {
        return goldShareable;
    }

    public void setGoldShareable(Integer goldShareable) {
        this.goldShareable = goldShareable;
    }

    public Integer getSilverShareable() {
        return silverShareable;
    }

    public void setSilverShareable(Integer silverShareable) {
        this.silverShareable = silverShareable;
    }

    public Integer getBronzeShareable() {
        return bronzeShareable;
    }

    public void setBronzeShareable(Integer bronzeShareable) {
        this.bronzeShareable = bronzeShareable;
    }

    public Integer getGoldRedeemable() {
        return goldRedeemable;
    }

    public void setGoldRedeemable(Integer goldRedeemable) {
        this.goldRedeemable = goldRedeemable;
    }

    public Integer getSilverRedeemable() {
        return silverRedeemable;
    }

    public void setSilverRedeemable(Integer silverRedeemable) {
        this.silverRedeemable = silverRedeemable;
    }

    public Integer getBronzeRedeemable() {
        return bronzeRedeemable;
    }

    public void setBronzeRedeemable(Integer bronzeRedeemable) {
        this.bronzeRedeemable = bronzeRedeemable;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<Role> roleSet) {
        this.roleSet = roleSet;
    }
}
