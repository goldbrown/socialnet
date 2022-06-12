package com.socialnet.android.gson;

public class FriendItem {
    private String friendName;
    private String count;
    private int countInt;

//    private String friendTag;

    public static class Builder {
        private FriendItem friendItem;
        public Builder() {
            friendItem = new FriendItem();
        }
        public FriendItem.Builder friendName(String friendName) {
            friendItem.setFriendName(friendName);
            return this;
        }
        public FriendItem.Builder count(String count) {
            friendItem.setCount(count);
            return this;
        }
        public FriendItem.Builder countInt(int countInt) {
            friendItem.setCountInt(countInt);
            return this;
        }
        public FriendItem build() {
            return friendItem;
        }
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

//    public String getFriendTag() {
//        return friendTag;
//    }
//
//    public void setFriendTag(String friendTag) {
//        this.friendTag = friendTag;
//    }
    public int getCountInt() {
        return countInt;
    }

    public void setCountInt(int countInt) {
        this.countInt = countInt;
    }
}
