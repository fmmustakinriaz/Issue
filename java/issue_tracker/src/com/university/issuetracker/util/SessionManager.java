package com.university.issuetracker.util;

import com.university.issuetracker.model.User;

public class SessionManager {
    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void clear() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static boolean isStudent() { return currentUser != null && "student".equals(currentUser.getRole()); }
    public static boolean isStaff() { return currentUser != null && "staff".equals(currentUser.getRole()); }
    public static boolean isAdmin() { return currentUser != null && "admin".equals(currentUser.getRole()); }
}
