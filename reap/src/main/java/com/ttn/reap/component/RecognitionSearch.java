package com.ttn.reap.component;

import org.springframework.stereotype.Component;

@Component
public class RecognitionSearch {

    private Integer currentUserId;
    private String fullName;

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getCurrentUserId(){
        return currentUserId;
    }

    @Override
    public String toString() {
        return "RecognitionSearch{" +
                "currentUserId=" + currentUserId +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
