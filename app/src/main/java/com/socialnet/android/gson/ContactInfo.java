package com.socialnet.android.gson;

import java.util.List;

public class ContactInfo {
    private List<ContactRecord> contactRecords;

    public List<ContactRecord> getContactRecords() {
        return contactRecords;
    }

    public void setContactRecords(List<ContactRecord> contactRecords) {
        this.contactRecords = contactRecords;
    }
}
