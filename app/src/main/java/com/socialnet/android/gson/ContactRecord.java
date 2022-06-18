package com.socialnet.android.gson;


import com.socialnet.android.db.ContactRecordEntity;

import java.util.Date;

public class ContactRecord {
    private String toPerson;
    private String dateStr;
    private Date date;

    public static ContactRecordEntity convert2ContactRecordEntity(ContactRecord contactRecord) {
        if (contactRecord == null) {
            return null;
        }
        ContactRecordEntity contactRecordEntity = new ContactRecordEntity();
        contactRecordEntity.setDateStr(contactRecord.getDateStr());
        contactRecordEntity.setFriendName(contactRecord.getToPerson());
        return contactRecordEntity;
    }

    public static class Builder {
        private ContactRecord contactRecord;
        public Builder() {
            contactRecord = new ContactRecord();
        }
        public Builder toPerson(String to) {
            contactRecord.setToPerson(to);
            return this;
        }
        public Builder dateStr(String date) {
            contactRecord.setDateStr(date);
            return this;
        }
        public Builder date(Date date) {
            contactRecord.setDate(date);
            return this;
        }
        public ContactRecord build() {
            return contactRecord;
        }
    }

    public String getToPerson() {
        return toPerson;
    }

    public void setToPerson(String toPerson) {
        this.toPerson = toPerson;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
