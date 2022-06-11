package com.socialnet.android.gson;

public class FriendItem {
    private String friendName;
    private int count;
    private String friendTag;

    public static class Builder {
        private FriendItem friendItem;
        public Builder() {
            friendItem = new FriendItem();
        }
        public FriendItem.Builder friendName(String friendName) {
            friendItem.setFriendName(friendName);
            return this;
        }
        public FriendItem.Builder count(int count) {
            friendItem.setCount(count);
            return this;
        }
        public FriendItem.Builder friendTag(String friendTag) {
            friendItem.setFriendTag(friendTag);
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFriendTag() {
        return friendTag;
    }

    public void setFriendTag(String friendTag) {
        this.friendTag = friendTag;
    }
}
