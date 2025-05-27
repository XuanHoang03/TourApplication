package com.hashmal.tourapplication.utils;

import com.hashmal.tourapplication.model.UserRole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionManager {
    private static PermissionManager instance;
    private final Map<UserRole, Set<String>> rolePermissions;
    private UserRole currentUserRole;

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
        adminPermissions.add("manage_bookings");
        adminPermissions.add("view_profile");
        adminPermissions.add("edit_profile");
        rolePermissions.put(UserRole.ADMIN, adminPermissions);

        // Staff permissions
        Set<String> staffPermissions = new HashSet<>();
        staffPermissions.add("view_dashboard");
        staffPermissions.add("manage_tours");
        staffPermissions.add("manage_bookings");
        staffPermissions.add("view_profile");
        staffPermissions.add("edit_profile");
        rolePermissions.put(UserRole.STAFF, staffPermissions);

        // User permissions
        Set<String> userPermissions = new HashSet<>();
        userPermissions.add("view_tours");
        userPermissions.add("book_tours");
        userPermissions.add("view_bookings");
        userPermissions.add("view_profile");
        userPermissions.add("edit_profile");
        rolePermissions.put(UserRole.USER, userPermissions);
    }

    public void setCurrentUserRole(UserRole role) {
        this.currentUserRole = role;
    }

    public boolean hasPermission(String permission) {
        if (currentUserRole == null) return false;
        Set<String> permissions = rolePermissions.get(currentUserRole);
        return permissions != null && permissions.contains(permission);
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

    public boolean canEditProfile() {
        return hasPermission("edit_profile");
    }
} 