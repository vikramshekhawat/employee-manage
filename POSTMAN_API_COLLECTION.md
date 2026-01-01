# Employee Salary Management System - Postman API Collection

**Base URL:** `http://localhost:8080`

All APIs return responses in the following format:
```json
{
  "success": true/false,
  "message": "Response message",
  "data": { ... }
}
```

---

## 1. Authentication APIs

### 1.1 Login
**POST** `/api/auth/login`

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "dummy-token",
    "username": "admin"
  }
}
```

**Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null
}
```

---

## 2. Employee Management APIs

### 2.1 Create Employee
**POST** `/api/employees`

**Request Body:**
```json
{
  "name": "John Doe",
  "mobile": "9876543210",
  "baseSalary": 50000,
  "pfPercentage": 12
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "mobile": "9876543210",
    "baseSalary": 50000.00,
    "pfPercentage": 12.00,
    "active": true,
    "createdAt": "2025-12-31T10:00:00",
    "updatedAt": "2025-12-31T10:00:00"
  }
}
```

### 2.2 Get All Employees
**GET** `/api/employees`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "mobile": "9876543210",
      "baseSalary": 50000.00,
      "pfPercentage": 12.00,
      "active": true,
      "createdAt": "2025-12-31T10:00:00",
      "updatedAt": "2025-12-31T10:00:00"
    }
  ]
}
```

### 2.3 Get Employee by ID
**GET** `/api/employees/{id}`

**Example:** `GET /api/employees/1`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "John Doe",
    "mobile": "9876543210",
    "baseSalary": 50000.00,
    "pfPercentage": 12.00,
    "active": true,
    "createdAt": "2025-12-31T10:00:00",
    "updatedAt": "2025-12-31T10:00:00"
  }
}
```

### 2.4 Update Employee
**PUT** `/api/employees/{id}`

**Example:** `PUT /api/employees/1`

**Request Body:**
```json
{
  "name": "John Updated",
  "mobile": "9876543210",
  "baseSalary": 60000,
  "pfPercentage": 12
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Employee updated successfully",
  "data": {
    "id": 1,
    "name": "John Updated",
    "mobile": "9876543210",
    "baseSalary": 60000.00,
    "pfPercentage": 12.00,
    "active": true,
    "createdAt": "2025-12-31T10:00:00",
    "updatedAt": "2025-12-31T10:05:00"
  }
}
```

### 2.5 Deactivate Employee
**PUT** `/api/employees/{id}/deactivate`

**Example:** `PUT /api/employees/1/deactivate`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Employee deactivated successfully",
  "data": null
}
```

---

## 3. Advance Salary Management APIs

### 3.1 Create Advance
**POST** `/api/advances`

**Request Body:**
```json
{
  "employeeId": 1,
  "amount": 5000,
  "advanceDate": "2025-12-15",
  "description": "Emergency advance for medical expenses"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Advance created successfully",
  "data": {
    "id": 1,
    "employee": {
      "id": 1,
      "name": "John Doe"
    },
    "amount": 5000.00,
    "advanceDate": "2025-12-15",
    "description": "Emergency advance for medical expenses"
  }
}
```

### 3.2 Get All Advances for Employee
**GET** `/api/advances/employee/{employeeId}`

**Example:** `GET /api/advances/employee/1`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "amount": 5000.00,
      "advanceDate": "2025-12-15",
      "description": "Emergency advance"
    }
  ]
}
```

### 3.3 Get Advances for Specific Month
**GET** `/api/advances/employee/{employeeId}/month/{month}/year/{year}`

**Example:** `GET /api/advances/employee/1/month/12/year/2025`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "amount": 5000.00,
      "advanceDate": "2025-12-15",
      "description": "Emergency advance"
    }
  ]
}
```

---

## 4. Leave Management APIs

### 4.1 Create Leave
**POST** `/api/leaves`

**Request Body:**
```json
{
  "employeeId": 1,
  "leaveDate": "2025-12-20",
  "leaveType": "UNPAID",
  "description": "Sick leave"
}
```

**Note:** `leaveType` can be `"PAID"` or `"UNPAID"`

**Success Response (201):**
```json
{
  "success": true,
  "message": "Leave created successfully",
  "data": {
    "id": 1,
    "employee": {
      "id": 1,
      "name": "John Doe"
    },
    "leaveDate": "2025-12-20",
    "leaveType": "UNPAID",
    "description": "Sick leave"
  }
}
```

### 4.2 Get All Leaves for Employee
**GET** `/api/leaves/employee/{employeeId}`

**Example:** `GET /api/leaves/employee/1`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "leaveDate": "2025-12-20",
      "leaveType": "UNPAID",
      "description": "Sick leave"
    }
  ]
}
```

### 4.3 Get Leaves for Specific Month
**GET** `/api/leaves/employee/{employeeId}/month/{month}/year/{year}`

**Example:** `GET /api/leaves/employee/1/month/12/year/2025`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "leaveDate": "2025-12-20",
      "leaveType": "UNPAID",
      "description": "Sick leave"
    }
  ]
}
```

---

## 5. Overtime Management APIs

### 5.1 Create Overtime
**POST** `/api/overtimes`

**Request Body:**
```json
{
  "employeeId": 1,
  "overtimeDate": "2025-12-10",
  "hours": 4,
  "ratePerHour": 500
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Overtime created successfully",
  "data": {
    "id": 1,
    "employee": {
      "id": 1,
      "name": "John Doe"
    },
    "overtimeDate": "2025-12-10",
    "hours": 4.00,
    "ratePerHour": 500.00,
    "totalAmount": 2000.00
  }
}
```

### 5.2 Get All Overtimes for Employee
**GET** `/api/overtimes/employee/{employeeId}`

**Example:** `GET /api/overtimes/employee/1`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "overtimeDate": "2025-12-10",
      "hours": 4.00,
      "ratePerHour": 500.00,
      "totalAmount": 2000.00
    }
  ]
}
```

### 5.3 Get Overtimes for Specific Month
**GET** `/api/overtimes/employee/{employeeId}/month/{month}/year/{year}`

**Example:** `GET /api/overtimes/employee/1/month/12/year/2025`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "overtimeDate": "2025-12-10",
      "hours": 4.00,
      "ratePerHour": 500.00,
      "totalAmount": 2000.00
    }
  ]
}
```

---

## 6. Salary Management APIs

### 6.1 Preview Salary
**POST** `/api/salary/preview`

**Request Body:**
```json
{
  "employeeId": 1,
  "month": 12,
  "year": 2025
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "employeeId": 1,
    "employeeName": "John Doe",
    "employeeMobile": "9876543210",
    "month": 12,
    "year": 2025,
    "baseSalary": 50000.00,
    "totalOvertime": 2000.00,
    "totalAdvances": 5000.00,
    "unpaidLeaveDays": 1,
    "leaveDeduction": 1666.67,
    "pfDeduction": 6000.00,
    "finalSalary": 39333.33,
    "dateWiseBreakdown": [
      {
        "type": "OVERTIME",
        "date": "2025-12-10",
        "amount": 2000.00,
        "description": "4 hrs @ 500/hr"
      },
      {
        "type": "ADVANCE",
        "date": "2025-12-15",
        "amount": -5000.00,
        "description": "Emergency advance"
      },
      {
        "type": "LEAVE",
        "date": "2025-12-20",
        "amount": -1666.67,
        "description": "Unpaid Leave: Sick leave"
      }
    ]
  }
}
```

### 6.2 Generate Salary
**POST** `/api/salary/generate`

**Request Body:**
```json
{
  "employeeId": 1,
  "month": 12,
  "year": 2025
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Salary generated and SMS sent successfully",
  "data": {
    "id": 1,
    "employee": {
      "id": 1,
      "name": "John Doe"
    },
    "month": 12,
    "year": 2025,
    "baseSalary": 50000.00,
    "totalOvertime": 2000.00,
    "totalAdvances": 5000.00,
    "totalLeaves": 1666.67,
    "pfDeduction": 6000.00,
    "finalSalary": 39333.33,
    "smsSent": true,
    "smsSentAt": "2025-12-31T10:30:00"
  }
}
```

### 6.3 Get Salary History
**GET** `/api/salary/employee/{employeeId}`

**Example:** `GET /api/salary/employee/1`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "month": 12,
      "year": 2025,
      "baseSalary": 50000.00,
      "totalOvertime": 2000.00,
      "totalAdvances": 5000.00,
      "totalLeaves": 1666.67,
      "pfDeduction": 6000.00,
      "finalSalary": 39333.33,
      "smsSent": true,
      "smsSentAt": "2025-12-31T10:30:00"
    }
  ]
}
```

### 6.4 Resend Salary SMS
**POST** `/api/salary/{salaryId}/resend-sms`

**Example:** `POST /api/salary/1/resend-sms`

**Success Response (200):**
```json
{
  "success": true,
  "message": "SMS resent successfully",
  "data": null
}
```

---

## 7. Dashboard API

### 7.1 Get Dashboard Statistics
**GET** `/api/dashboard`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "totalEmployees": 10,
    "activeEmployees": 8,
    "totalSalaryThisMonth": 400000.00,
    "totalSalaryLastMonth": 380000.00,
    "pendingSalaryGenerations": 2
  }
}
```

---

## Complete Test Flow Example

### Step 1: Login
```
POST http://localhost:8080/api/auth/login
Body: { "username": "admin", "password": "admin123" }
```

### Step 2: Create Employee
```
POST http://localhost:8080/api/employees
Body: {
  "name": "John Doe",
  "mobile": "9876543210",
  "baseSalary": 50000,
  "pfPercentage": 12
}
```
**Save the employee ID from response (e.g., 1)**

### Step 3: Add Overtime
```
POST http://localhost:8080/api/overtimes
Body: {
  "employeeId": 1,
  "overtimeDate": "2025-12-10",
  "hours": 4,
  "ratePerHour": 500
}
```

### Step 4: Add Advance
```
POST http://localhost:8080/api/advances
Body: {
  "employeeId": 1,
  "amount": 5000,
  "advanceDate": "2025-12-15",
  "description": "Emergency advance"
}
```

### Step 5: Add Leave
```
POST http://localhost:8080/api/leaves
Body: {
  "employeeId": 1,
  "leaveDate": "2025-12-20",
  "leaveType": "UNPAID",
  "description": "Sick leave"
}
```

### Step 6: Preview Salary
```
POST http://localhost:8080/api/salary/preview
Body: {
  "employeeId": 1,
  "month": 12,
  "year": 2025
}
```

### Step 7: Generate Salary
```
POST http://localhost:8080/api/salary/generate
Body: {
  "employeeId": 1,
  "month": 12,
  "year": 2025
}
```

### Step 8: View Dashboard
```
GET http://localhost:8080/api/dashboard
```

---

## Error Responses

### Validation Error (400)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "mobile": "Mobile number must be 10 digits",
    "baseSalary": "Base salary must be greater than 0"
  }
}
```

### Resource Not Found (404)
```json
{
  "success": false,
  "message": "Employee not found with id: 999",
  "data": null
}
```

### Internal Server Error (500)
```json
{
  "success": false,
  "message": "An error occurred: Database connection failed",
  "data": null
}
```

---

## Notes

1. **Date Format:** All dates should be in `YYYY-MM-DD` format (e.g., "2025-12-31")
2. **Month/Year:** Month should be 1-12, Year should be a valid year (e.g., 2025)
3. **Mobile Number:** Must be exactly 10 digits
4. **Amounts:** All monetary values are in decimal format (e.g., 50000.00)
5. **CORS:** All endpoints support CORS from any origin
6. **Content-Type:** All POST/PUT requests require `Content-Type: application/json` header

---

## Postman Collection Import

You can import this collection into Postman by creating a new collection and adding all the above endpoints. Make sure to:
- Set the base URL as an environment variable: `{{baseUrl}}` = `http://localhost:8080`
- Add `Content-Type: application/json` header to all POST/PUT requests
- Save employee IDs from responses to use in subsequent requests

