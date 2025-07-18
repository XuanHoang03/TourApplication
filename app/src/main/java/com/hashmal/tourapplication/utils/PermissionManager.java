package com.hashmal.tourapplication.utils;

import com.hashmal.tourapplication.enums.RoleEnum;
import com.hashmal.tourapplication.model.UserRole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionManager {
    private static PermissionManager instance;
    private final Map<RoleEnum, Set<String>> rolePermissions;
    private RoleEnum currentUserRole;

    private PermissionManager() {
        rolePermissions = new HashMap<>();
        initializePermissions();
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    private void initializePermissions() {
        // Admin permissions
        Set<String> adminPermissions = new HashSet<>();
        adminPermissions.add("view_dashboard");
        adminPermissions.add("manage_users");
        adminPermissions.add("manage_staff");
        adminPermissions.add("manage_tours");
        adminPermissions.add("view_profile");
        adminPermissions.add("edit_profile");
        rolePermissions.put(RoleEnum.SYSTEM_ADMIN, adminPermissions);

        // Staff permissions
        Set<String> staffPermissions = new HashSet<>();
        staffPermissions.add("manage_bookings");
        staffPermissions.add("view_profile");
        staffPermissions.add("edit_profile");
        staffPermissions.add("view_tour_guide");
        staffPermissions.add("chat");
        rolePermissions.put(RoleEnum.TOUR_OPERATOR, staffPermissions);


        Set<String> tourGuidePermission = new HashSet<>();

        tourGuidePermission.add("edit_profile");
        tourGuidePermission.add("view_profile");
        tourGuidePermission.add("manage_work_calendar");
        tourGuidePermission.add("manage_tours");
        tourGuidePermission.add("manage_current_work");
        tourGuidePermission.add("chat");
        rolePermissions.put(RoleEnum.TOUR_GUIDE, tourGuidePermission);
    }

    public void setCurrentUserRole(RoleEnum role) {
        this.currentUserRole = role;
    }

    public boolean hasPermission(String permission) {
        if (currentUserRole == null) return false;
        Set<String> permissions = rolePermissions.get(currentUserRole);
        return permissions != null && permissions.contains(permission);
    }
    public boolean canManageWorkCalendar() {
        return hasPermission("manage_work_calendar");
    }

    public boolean canChat() {
        return hasPermission("chat");
    }
    public boolean canManageCurrentWork() {
        return hasPermission("manage_current_work");
    }
    public boolean canManageUsers() {
        return hasPermission("manage_users");
    }

    public boolean canManageStaff() {
        return hasPermission("manage_staff");
    }

    public boolean canManageTours() {
        return hasPermission("manage_tours");
    }

    public boolean canManageBookings() {
        return hasPermission("manage_bookings");
    }

    public boolean canViewDashboard() {
        return hasPermission("view_dashboard");
    }

    public boolean canViewProfile() {
        return hasPermission("view_profile");
    }

    public boolean canViewTourGuide() {
        return hasPermission("view_tour_guide");
    }

    public boolean canEditProfile() {
        return hasPermission("edit_profile");
    }
} 