package com.digi.uniprr.enums;

public enum Users {
    EDITOR(3),AUTHOR(4), REVIEWER(5);

    private int roleId;

    public int getRoleId() {
        return roleId;
    }

    private Users(int roleId) {
        this.roleId = roleId;
    }
}
