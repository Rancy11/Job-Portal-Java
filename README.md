
# 💼 Job Portal - Java MVC Project

This is a backend-focused **Job Portal** application built with **Java**, **JDBC**, and follows the **MVC (Model-View-Controller)** architecture. Employers can post jobs and job seekers can register, log in, and apply. The system also includes applicant authentication and uses a **Binary Search Tree (BST)** for managing applicant data efficiently.

---

## 📁 Project Structure

```

JAVA-PROJECT/
├── controller/               # Handles logic and DB communication
│   ├── DatabaseConnection.java
│   └── JobPortalController.java
│
├── model/                    # Core data models and BST logic
│   ├── Applicant.java
│   ├── ApplicantBST.java
│   ├── Company.java
│   ├── Job.java
│   └── TreeNode.java
│
├── view/                     # Console-based UI logic
│   └── JobPortalView\.java
│
├── Main.java                 # Entry point
└── JAVA-PROJECT.iml          # IntelliJ project file

````



## ⚙️ Features

- Applicant authentication (username/password)
- Employer:
  - Post new job listings
- Job Seeker:
  - Register and log in
  - View available jobs
  - Apply for jobs
- Applicant data managed using a Binary Search Tree (BST)
- JDBC-based connection to MySQL database
- Modular MVC structure for maintainable code

---

## 🚀 Getting Started

### ✅ Prerequisites

- Java JDK 8 or above
- MySQL database
- IDE (e.g., IntelliJ IDEA, Eclipse)

### 🔧 Installation & Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/sulabhsaluja/Job-Portal-Java.git
   cd Job-Portal-Java


2. **Set Up MySQL Database**

   * Create a new database.
   * Add required tables for applicants, jobs, etc.
   * Update the credentials in `controller/DatabaseConnection.java`:

     ```java
     String url = "jdbc:mysql://localhost:3306/your_database";
     String user = "your_username";
     String password = "your_password";
     ```

3. **Run the Application**

   * Open the project in your Java IDE.
   * Run `Main.java`.



## 🧩 Dependencies

* Java (JDK 8+)
* JDBC
* MySQL

---

## 📌 Notes

* This is a console-based application; UI is managed through standard input/output.
* BST is used to store applicant data in-memory.
* Passwords are stored in plain text (for demonstration purposes only — use hashing for production).

---

## 📷 Screenshots

**Role Selection**

![Role Selection](https://github.com/user-attachments/assets/62075385-64ae-40d4-9e26-640c231045bb)

**Employer Registration**

![Employer Registration](https://github.com/user-attachments/assets/3c726c51-9998-4877-99c1-abb3ecf367c2)

**Job Seeker Registration**

![Job Seeker Registration](https://github.com/user-attachments/assets/30e4f75b-dc42-4c1e-8310-e088b502da1c)


**Posting a Job (Employer)**

![Posting a Job (Employer)](https://github.com/user-attachments/assets/495a134a-6c71-4fe0-a654-ae3f249246a9)


**Viewing Available Jobs (Job Seeker)**

![Viewing Available Jobs (Job Seeker)](https://github.com/user-attachments/assets/a7eb1d94-3c02-4127-bb39-64b66cfd2aa3)


**Viewing Applicants (Employer)**

![Viewing Applicants (Employer)](https://github.com/user-attachments/assets/38c9b95e-441b-4ede-9e45-69628327e6b6)

**Updating Application Status**

![Updating Application Status](https://github.com/user-attachments/assets/8caab5cb-78b6-49e1-a0fa-e9cb13ad4088)


---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---


