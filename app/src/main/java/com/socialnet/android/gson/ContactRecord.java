package com.socialnet.android.gson;

import java.util.Date;

public class ContactRecord {
    private String fromPerson;
    private String toPerson;
    private String date;

    public static class Builder {
        private ContactRecord contactRecord;
        public Builder() {
            contactRecord = new ContactRecord();
        }
        public Builder fromPerson(String from) {
            contactRecord.setFromPerson(from);
            return this;
        }
        public Builder toPerson(String to) {
            contactRecord.setToPerson(to);
            return this;
        }
        public Builder date(String date) {
            contactRecord.setDate(date);
            return this;
        }
        public ContactRecord build() {
            return contactRecord;
        }
    }

    public String getFromPerson() {
        return fromPerson;
    }

    public void setFromPerson(String fromPerson) {
        this.fromPerson = fromPerson;
    }

    public String getToPerson() {
        return toPerson;
    }

    public void setToPerson(String toPerson) {
        this.toPerson = toPerson;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
