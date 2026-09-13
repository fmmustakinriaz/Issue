# Department Issue Tracking System (Java Swing + MySQL)

A desktop application for university departments to track and manage issues. Built with **Java Swing** for the UI and **MySQL** for the database.

## Features

- **Three roles**: Student, Staff, Admin
- **Students** can submit issues and view all department issues
- **Staff** can view all issues, update issue status, and add comments
- **Admin** can view all issues, assign issues to staff, manage users (change roles), and manage categories
- **Issue categories** and **priorities** (Low, Medium, High)
- **Issue status**: Open, In Progress, Resolved, Closed
- **Comments** on issues
- **Dashboard** with issue statistics (total, open, in progress, resolved, closed, high priority)
- **All users see all issues** — when one user submits an issue, others can see it

## Prerequisites

1. **Java JDK 11 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version`

2. **MySQL Server 8.0+**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Verify: `mysql --version`

3. **MySQL Connector/J** (JDBC driver)
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Place the `mysql-connector-j-x.x.x.jar` file in the `lib/` folder

## Setup Instructions

### Step 1: Set Up MySQL Database

1. Start MySQL server.
2. Open a MySQL terminal (or MySQL Workbench) and run:

```sql
SOURCE /path/to/issue_tracker/database/schema.sql;
```

Or copy the contents of `database/schema.sql` and paste it into MySQL Workbench.

This creates the `issue_tracker` database with all tables and seed data (default categories and an admin user).

### Step 2: Configure Database Connection

Edit `src/com/university/issuetracker/util/DatabaseConnection.java` and update these values if your MySQL credentials differ:

```java
private static final String URL = "jdbc:mysql://localhost:3306/issue_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

Change `USER` and `PASSWORD` to match your MySQL username and password.

### Step 3: Add MySQL JDBC Driver

Download `mysql-connector-j-x.x.x.jar` from the MySQL website and place it in:

```
issue_tracker/lib/
```

### Step 4: Compile and Run

**On Linux/Mac:**
```bash
cd issue_tracker
./compile.sh
```

**On Windows (Command Prompt):**
```cmd
cd issue_tracker
mkdir build
dir /s /b src\*.java > sources.txt
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d build @sources.txt
del sources.txt
java -cp "build;lib\mysql-connector-j-8.0.33.jar" com.university.issuetracker.Main
```

**Manual compilation (any OS):**
```bash
cd issue_tracker
mkdir build
# Linux/Mac (use : as classpath separator)
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d build $(find src -name "*.java")
java -cp "build:lib/mysql-connector-j-8.0.33.jar" com.university.issuetracker.Main

# Windows (use ; as classpath separator)
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d build $(dir /s /b src\*.java)
java -cp "build;lib\mysql-connector-j-8.0.33.jar" com.university.issuetracker.Main
```

## Default Login

An admin account is pre-seeded:
- **Email**: admin@university.edu
- **Password**: admin123

You can also create new accounts from the registration screen by choosing any role.

## Project Structure

```
issue_tracker/
├── database/
│   └── schema.sql              # MySQL database schema + seed data
├── lib/
│   └── mysql-connector-j-*.jar  # MySQL JDBC driver (you add this)
├── src/
│   └── com/university/issuetracker/
│       ├── Main.java            # Application entry point
│       ├── model/
│       │   ├── User.java
│       │   ├── Issue.java
│       │   ├── Category.java
│       │   └── Comment.java
│       ├── dao/
│       │   ├── UserDAO.java
│       │   ├── IssueDAO.java
│       │   ├── CategoryDAO.java
│       │   └── CommentDAO.java
│       ├── util/
│       │   ├── DatabaseConnection.java
│       │   ├── PasswordUtil.java
│       │   └── SessionManager.java
│       └── ui/
│           ├── LoginFrame.java
│           ├── RegisterFrame.java
│           ├── MainFrame.java
│           ├── DashboardPanel.java
│           ├── IssueListPanel.java
│           ├── SubmitIssuePanel.java
│           ├── IssueDetailDialog.java
│           ├── ManageUsersPanel.java
│           └── ManageCategoriesPanel.java
├── compile.sh                  # Compile & run script
└── README.md
```

## How It Works

1. **Login/Register**: Users sign in or create an account with a chosen role (student, staff, or admin).
2. **Dashboard**: Shows issue statistics (total, open, in progress, resolved, closed, high priority) and recent issues.
3. **Issues**: All users can see all department issues. Filter by status or search by title.
4. **Submit Issue** (students): Fill in title, description, category, and priority.
5. **Issue Detail**: View full issue details, update status/priority/assignment (staff and admin), and add comments.
6. **Manage Users** (admin): View all users and change their roles.
7. **Manage Categories** (admin): Create, update, and delete issue categories.

## Troubleshooting

- **"MySQL JDBC Driver not found"**: Make sure you placed the `mysql-connector-j-*.jar` file in the `lib/` folder.
- **"Access denied for user"**: Check your MySQL username and password in `DatabaseConnection.java`.
- **"Unknown database 'issue_tracker'"**: Run `schema.sql` in MySQL first.
- **"Communications link failure"**: Make sure MySQL server is running on localhost:3306.
