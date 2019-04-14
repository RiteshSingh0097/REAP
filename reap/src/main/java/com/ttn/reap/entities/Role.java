package com.ttn.reap.entities;

public enum Role {

    ADMIN("ADMIN"),
    USER("USER"),
    PRACTICE_HEAD("Practice Head"),
    SUPERVISOR("SUPERVISOR");

    String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }




}
