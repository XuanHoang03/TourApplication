package com.hashmal.tourapplication.enums;

import java.util.ArrayList;
import java.util.List;

public enum RoleEnum {
    ROLE(-1, "Role"),
    CUSTOMER(0,"Customer"),
    SYSTEM_ADMIN(11, "System Admin"),
    TOUR_GUIDE(1, "Tour Guide"),
    TOUR_OPERATOR(2, "Tour Operator"),
    GUEST(3, "GUEST");

    RoleEnum(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
        listRole = new ArrayList<>();
    }

    private Integer roleId;
    private String roleName;
    private List<RoleEnum> listRole;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<RoleEnum> getListRole() {
        return List.of(TOUR_GUIDE, TOUR_OPERATOR);
    }
}
