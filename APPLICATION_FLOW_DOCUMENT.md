# Employee Salary Management System - Application Flow Document

## Table of Contents
1. [Introduction](#introduction)
2. [System Flow Overview](#system-flow-overview)
3. [Feature-wise Business Flows](#feature-wise-business-flows)
4. [Sequence Diagrams](#sequence-diagrams)
5. [Data Flow Diagrams](#data-flow-diagrams)
6. [State Transition Diagrams](#state-transition-diagrams)
7. [Integration Flows](#integration-flows)
8. [Error Handling Flows](#error-handling-flows)
9. [End-to-End Workflows](#end-to-end-workflows)

---

## 1. Introduction

This document provides comprehensive flow diagrams and detailed explanations of all business processes in the Employee Salary Management System. It covers user interactions, system processing, data flows, and integration points.

### Purpose
- Visualize system behavior and business processes
- Document decision points and branching logic
- Illustrate data transformations
- Show integration with external systems
- Provide reference for developers and stakeholders

### Notation
- **Rectangles**: Processes/Actions
- **Diamonds**: Decision points
- **Cylinders**: Data stores
- **Arrows**: Flow direction
- **Dashed lines**: Asynchronous operations

---

## 2. System Flow Overview

### High-Level Application Flow

```mermaid
graph TB
    start[User Access Application] --> auth[Authentication]
    auth -->|Success| dashboard[Dashboard View]
    auth -->|Failure| start
    
    dashboard --> empMgmt[Employee Management]
    dashboard --> transactionMgmt[Transaction Management]
    dashboard --> salaryMgmt[Salary Management]
    
    empMgmt --> empCreate[Create Employee]
    empMgmt --> empUpdate[Update Employee]
    empMgmt --> empView[View Employees]
    empMgmt --> empDeactivate[Deactivate Employee]
    
    transactionMgmt --> advanceMgmt[Advance Management]
    transactionMgmt --> leaveMgmt[Leave Management]
    transactionMgmt --> overtimeMgmt[Overtime Management]
    
    salaryMgmt --> salaryPreview[Preview Salary]
    salaryMgmt --> salaryGenerate[Generate Salary]
    salaryMgmt --> salaryHistory[View History]
    salaryMgmt --> salarySMS[Send SMS]
```

### User Journey Map

```mermaid
graph LR
    subgraph monthStart [Month Start]
        recordTrans[Record Daily Transactions<br/>Advances/Leaves/Overtime]
    end
    
    subgraph monthEnd [Month End]
        preview[Preview Salary Calculation]
        verify[Verify Details]
        generate[Generate Salary]
        sendSMS[Send SMS Notification]
    end
    
    subgraph ongoing [Ongoing]
        viewDash[View Dashboard]
        manageEmp[Manage Employees]
        viewHistory[View Salary History]
    end
    
    recordTrans --> preview
    preview --> verify
    verify -->|Approved| generate
    verify -->|Issues| recordTrans
    generate --> sendSMS
    
    viewDash -.-> manageEmp
    generate -.-> viewHistory
```

---

## 3. Feature-wise Business Flows

### 3.1 Employee Management Flow

#### Create Employee Flow

```mermaid
flowchart TD
    start([User Initiates Create Employee]) --> input[Enter Employee Details<br/>Name, Mobile, Base Salary, PF%]
    input --> validateClient[Client-side Validation]
    
    validateClient -->|Invalid| showError1[Show Validation Errors]
    showError1 --> input
    
    validateClient -->|Valid| submit[Submit to API<br/>POST /api/employees]
    submit --> validateServer[Server-side Validation]
    
    validateServer -->|Invalid| return400[Return 400 Bad Request<br/>with error details]
    return400 --> displayError[Display Errors to User]
    displayError --> input
    
    validateServer -->|Valid| checkMobile{Mobile Number<br/>Already Exists?}
    
    checkMobile -->|Yes| return400Mobile[Return 400 Bad Request<br/>Mobile already registered]
    return400Mobile --> displayError
    
    checkMobile -->|No| saveDB[Save to Database<br/>employees table]
    saveDB --> setDefaults[Set Defaults<br/>active=true<br/>timestamps]
    setDefaults --> return200[Return 200 OK<br/>with Employee data]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> refreshList[Refresh Employee List]
    refreshList --> done([End])
```

**Key Points:**
- Mobile number uniqueness is enforced at database level
- Default values: active=true, auto-timestamps
- Both client and server validation
- Immediate feedback to user

#### Update Employee Flow

```mermaid
flowchart TD
    start([User Selects Employee]) --> loadData[Load Employee Data<br/>GET /api/employees/:id]
    loadData --> display[Display in Edit Form]
    display --> modify[User Modifies Fields]
    modify --> validate[Validate Changes]
    
    validate -->|Invalid| showError[Show Validation Errors]
    showError --> modify
    
    validate -->|Valid| submit[Submit Update<br/>PUT /api/employees/:id]
    submit --> checkExists{Employee<br/>Exists?}
    
    checkExists -->|No| return404[Return 404 Not Found]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkExists -->|Yes| checkMobile{Mobile Changed &<br/>New Mobile Exists?}
    
    checkMobile -->|Yes| return400[Return 400 Bad Request]
    return400 --> displayError
    
    checkMobile -->|No| updateDB[Update Database Record]
    updateDB --> updateTimestamp[Update updated_at timestamp]
    updateTimestamp --> return200[Return 200 OK]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> refresh[Refresh Employee Details]
    refresh --> done
```

**Key Points:**
- Loads current data before editing
- Mobile uniqueness checked only if changed
- Updated timestamp automatically maintained
- Cannot change employee ID

#### Deactivate Employee Flow

```mermaid
flowchart TD
    start([User Clicks Deactivate]) --> confirm{Confirm<br/>Action?}
    confirm -->|No| cancel([Cancel])
    
    confirm -->|Yes| submit[Submit Request<br/>DELETE /api/employees/:id]
    submit --> checkExists{Employee<br/>Exists?}
    
    checkExists -->|No| return404[Return 404 Not Found]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkExists -->|Yes| softDelete[Set active = false<br/>Soft Delete]
    softDelete --> updateTimestamp[Update updated_at]
    updateTimestamp --> return200[Return 200 OK]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> removeFromList[Remove from Active List]
    removeFromList --> done
```

**Key Points:**
- Soft delete (no data removal)
- Confirmation required
- Employee data preserved for historical records
- Cannot generate new salaries for inactive employees

---

### 3.2 Advance Management Flow

```mermaid
flowchart TD
    start([User Records Advance]) --> selectEmp[Select Employee]
    selectEmp --> input[Enter Details<br/>Amount, Date, Description]
    input --> validate[Validate Input<br/>Amount > 0<br/>Valid Date<br/>Employee Exists]
    
    validate -->|Invalid| showError[Show Validation Errors]
    showError --> input
    
    validate -->|Valid| submit[Submit to API<br/>POST /api/advances]
    submit --> checkEmployee{Employee<br/>Exists & Active?}
    
    checkEmployee -->|No| return404[Return 404/400]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkEmployee -->|Yes| save[Save to Database<br/>advances table]
    save --> return200[Return 200 OK<br/>with Advance data]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> updateView[Update Advance List]
    updateView --> notify[Notify: Will affect<br/>next salary calculation]
    notify --> done
```

**Business Rules:**
1. Amount must be positive
2. Date can be past or present (not future)
3. Multiple advances allowed per day
4. Only active employees can receive advances
5. Advances are deducted from salary in the month they are recorded

**View Advances Flow:**

```mermaid
flowchart TD
    start([User Views Advances]) --> selectEmp[Select Employee]
    selectEmp --> request[GET /api/advances/employee/:id]
    request --> fetch[Fetch All Advances<br/>for Employee]
    fetch --> sort[Sort by Date<br/>Descending]
    sort --> display[Display in Table<br/>Date, Amount, Description]
    display --> optDelete{User Wants<br/>to Delete?}
    
    optDelete -->|No| done([End])
    optDelete -->|Yes| confirmDelete{Confirm<br/>Delete?}
    
    confirmDelete -->|No| display
    confirmDelete -->|Yes| deleteReq[DELETE /api/advances/:id]
    deleteReq --> remove[Remove from Database]
    remove --> return200[Return 200 OK]
    return200 --> refresh[Refresh Advance List]
    refresh --> done
```

---

### 3.3 Leave Management Flow

```mermaid
flowchart TD
    start([User Records Leave]) --> selectEmp[Select Employee]
    selectEmp --> input[Enter Leave Details<br/>Date, Type, Description]
    input --> selectType{Leave<br/>Type}
    
    selectType -->|PAID| setPaid[Set Type = PAID]
    selectType -->|UNPAID| setUnpaid[Set Type = UNPAID]
    
    setPaid --> validate[Validate Input]
    setUnpaid --> validate
    
    validate -->|Invalid| showError[Show Validation Errors]
    showError --> input
    
    validate -->|Valid| submit[Submit to API<br/>POST /api/leaves]
    submit --> checkEmployee{Employee<br/>Exists & Active?}
    
    checkEmployee -->|No| return404[Return 404/400]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkEmployee -->|Yes| save[Save to Database<br/>leaves table]
    save --> checkType{Leave<br/>Type?}
    
    checkType -->|PAID| notifyPaid[Notify: No salary deduction]
    checkType -->|UNPAID| notifyUnpaid[Notify: Will deduct<br/>from salary]
    
    notifyPaid --> return200[Return 200 OK]
    notifyUnpaid --> return200
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> updateView[Update Leave List]
    updateView --> done
```

**Business Rules:**
1. Two leave types: PAID and UNPAID
2. Only UNPAID leaves affect salary calculation
3. Leave date validation (cannot be future date typically)
4. Multiple leaves can be recorded per day (e.g., half-day scenarios)
5. Leave deduction formula: (Base Salary / Days in Month) × Number of Unpaid Leaves

**Leave Type Decision Matrix:**

| Leave Type | Salary Impact | Calculation |
|------------|---------------|-------------|
| PAID | None | No deduction |
| UNPAID | Deduction | (Base Salary ÷ 30) × Days |

---

### 3.4 Overtime Management Flow

```mermaid
flowchart TD
    start([User Records Overtime]) --> selectEmp[Select Employee]
    selectEmp --> input[Enter Overtime Details<br/>Date, Hours, Rate/Hour]
    input --> validate[Validate Input<br/>Hours > 0<br/>Rate > 0<br/>Valid Date]
    
    validate -->|Invalid| showError[Show Validation Errors]
    showError --> input
    
    validate -->|Valid| calculate[Calculate Total Amount<br/>Total = Hours × Rate]
    calculate --> preview[Show Preview<br/>to User]
    preview --> confirm{User<br/>Confirms?}
    
    confirm -->|No| input
    confirm -->|Yes| submit[Submit to API<br/>POST /api/overtimes]
    submit --> checkEmployee{Employee<br/>Exists & Active?}
    
    checkEmployee -->|No| return404[Return 404/400]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkEmployee -->|Yes| save[Save to Database<br/>overtime table<br/>with calculated total]
    save --> return200[Return 200 OK<br/>with Overtime data]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> updateView[Update Overtime List]
    updateView --> notify[Notify: Will be added<br/>to next salary]
    notify --> done
```

**Business Rules:**
1. Hours and rate must be positive
2. Total amount calculated automatically: Hours × Rate Per Hour
3. Multiple overtime entries allowed per day
4. Overtime is added to salary in the month it is recorded
5. Rate per hour can vary by entry (different rates for different days)

**Overtime Calculation Example:**

```
Date: 2025-01-15
Hours: 3.5
Rate per Hour: Rs 500
Total Amount: 3.5 × 500 = Rs 1,750
```

---

### 3.5 Salary Calculation Flow

#### Salary Preview Flow

```mermaid
flowchart TD
    start([User Requests Salary Preview]) --> selectEmp[Select Employee]
    selectEmp --> selectMonth[Select Month & Year]
    selectMonth --> submit[Submit Request<br/>POST /api/salaries/preview]
    
    submit --> checkEmployee{Employee<br/>Exists?}
    checkEmployee -->|No| return404[Return 404 Not Found]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkEmployee -->|Yes| getBaseSalary[Fetch Base Salary<br/>from Employee Record]
    getBaseSalary --> calculateMonth[Calculate Month Date Range<br/>Start Date, End Date]
    
    calculateMonth --> fetchOT[Fetch Overtime Records<br/>for Month]
    fetchOT --> sumOT[Sum Overtime Amounts]
    
    sumOT --> fetchAdv[Fetch Advance Records<br/>for Month]
    fetchAdv --> sumAdv[Sum Advance Amounts]
    
    sumAdv --> fetchLeaves[Fetch UNPAID Leave Records<br/>for Month]
    fetchLeaves --> countLeaves[Count Unpaid Leave Days]
    countLeaves --> calcLeave[Calculate Leave Deduction<br/>Base Salary ÷ Days in Month × Leave Days]
    
    calcLeave --> calcPF[Calculate PF Deduction<br/>Base Salary × PF% ÷ 100]
    
    calcPF --> calcFinal[Calculate Final Salary<br/>Base + Overtime - Advances - PF - Leaves]
    
    calcFinal --> buildBreakdown[Build Date-wise Breakdown<br/>Group by Date and Type]
    
    buildBreakdown --> return200[Return 200 OK<br/>with Preview Data]
    return200 --> displayPreview[Display Salary Preview<br/>with Breakdown]
    displayPreview --> reviewByUser{User Reviews<br/>Data}
    
    reviewByUser -->|Needs Correction| goToTransactions[Go to Transactions<br/>to Modify Data]
    goToTransactions --> done
    
    reviewByUser -->|Approved| offerGenerate[Offer Generate Salary<br/>Button]
    offerGenerate --> done
```

**Salary Calculation Formula:**

```
1. Base Salary = Employee's current base salary
2. Total Overtime = Sum of (Hours × Rate) for all overtime in month
3. Total Advances = Sum of all advance amounts in month
4. Unpaid Leave Days = Count of UNPAID leaves in month
5. Leave Deduction = (Base Salary ÷ Days in Month) × Unpaid Leave Days
6. PF Deduction = Base Salary × (PF Percentage ÷ 100)

Final Salary = Base Salary 
             + Total Overtime 
             - Total Advances 
             - PF Deduction 
             - Leave Deduction
```

#### Salary Generation Flow

```mermaid
flowchart TD
    start([User Clicks Generate Salary]) --> getPreview[Get Preview Data<br/>Already Validated]
    getPreview --> submit[Submit Request<br/>POST /api/salaries/generate]
    
    submit --> checkDuplicate{Salary Already<br/>Generated for<br/>Month?}
    
    checkDuplicate -->|Yes| return400[Return 400 Bad Request<br/>Duplicate salary]
    return400 --> displayError[Display Error]
    displayError --> done([End])
    
    checkDuplicate -->|No| performCalc[Perform Salary Calculation<br/>Same as Preview]
    
    performCalc --> createSalary[Create Salary Record]
    createSalary --> setSalaryFields[Set All Fields<br/>Base, Overtime, Advances,<br/>PF, Leaves, Final Amount]
    setSalaryFields --> setFlags[Set sms_sent = false<br/>sms_sent_at = null]
    
    setFlags --> saveSalary[Save to salaries table]
    saveSalary --> getSalaryID[Get Generated Salary ID]
    
    getSalaryID --> loopDetails{For Each<br/>Date-wise<br/>Transaction}
    
    loopDetails -->|Overtime| createOTDetail[Create OVERTIME Detail<br/>with positive amount]
    loopDetails -->|Advance| createAdvDetail[Create ADVANCE Detail<br/>with negative amount]
    loopDetails -->|Leave| createLeaveDetail[Create LEAVE Detail<br/>with negative amount]
    
    createOTDetail --> saveDetail[Save to salary_details]
    createAdvDetail --> saveDetail
    createLeaveDetail --> saveDetail
    
    saveDetail --> moreDetails{More<br/>Details?}
    moreDetails -->|Yes| loopDetails
    moreDetails -->|No| return200[Return 200 OK<br/>with Salary Record]
    
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> offerSMS[Offer Send SMS Option]
    offerSMS --> updateDashboard[Update Dashboard Stats]
    updateDashboard --> done
```

**Key Points:**
1. Duplicate prevention: One salary per employee per month
2. Denormalized storage: All amounts stored in salary record
3. Detailed audit trail: salary_details stores date-wise breakdown
4. Amounts stored with 2 decimal precision
5. Historical accuracy: Base salary snapshot stored

---

### 3.6 SMS Notification Flow

```mermaid
flowchart TD
    start([User Clicks Send SMS]) --> getSalary[Fetch Salary Record<br/>GET /api/salaries/:id]
    getSalary --> checkSalary{Salary<br/>Exists?}
    
    checkSalary -->|No| return404[Return 404 Not Found]
    return404 --> displayError[Display Error]
    displayError --> done([End])
    
    checkSalary -->|Yes| checkSent{SMS Already<br/>Sent?}
    checkSent -->|Yes| confirmResend{Confirm<br/>Resend?}
    confirmResend -->|No| done
    confirmResend -->|Yes| proceedSend
    checkSent -->|No| proceedSend[Proceed to Send]
    
    proceedSend --> submit[Submit Request<br/>POST /api/salaries/:id/send-sms]
    submit --> checkConfig{Twilio<br/>Configured?}
    
    checkConfig -->|No| return500[Return 500 Error<br/>Twilio not configured]
    return500 --> displayError
    
    checkConfig -->|Yes| initTwilio[Initialize Twilio<br/>with Account SID & Auth Token]
    initTwilio --> getDetails[Fetch Salary & Details<br/>from Database]
    getDetails --> formatPhone[Format Phone Number<br/>Add +91 for 10 digit numbers]
    
    formatPhone --> buildSMS[Build SMS Content<br/>Format Salary Slip]
    buildSMS --> callTwilio[Call Twilio API<br/>Send SMS]
    
    callTwilio --> twilioResponse{Twilio<br/>Success?}
    
    twilioResponse -->|Failure| updateFailed[Set sms_sent = false]
    updateFailed --> return500SMS[Return 500 Error<br/>SMS send failed]
    return500SMS --> displayError
    
    twilioResponse -->|Success| updateSuccess[Update Salary Record<br/>sms_sent = true<br/>sms_sent_at = now]
    updateSuccess --> return200[Return 200 OK<br/>SMS sent successfully]
    return200 --> displaySuccess[Display Success Message]
    displaySuccess --> showConfirmation[Show SMS Sent<br/>Confirmation]
    showConfirmation --> done
```

**SMS Content Format:**

```
Salary Slip - Jan 2025
Emp: John Doe
Base: Rs 50000.00

Date-wise Details:
18/01: OVERTIME +Rs 1750.00 (3.5 hrs @ 500.0/hr)
15/01: ADVANCE -Rs 5000.00 (Emergency advance)
20/01: LEAVE -Rs 1666.67 (Unpaid leave)

Overtime: +Rs 1750.00
Advances: -Rs 5000.00
PF: -Rs 6000.00
Leaves: -Rs 3333.33
Final: Rs 37416.67
```

**Phone Number Formatting Rules:**

```
Input: 9876543210 → Output: +919876543210
Input: +919876543210 → Output: +919876543210
Input: 919876543210 → Output: +919876543210
```

---

### 3.7 Dashboard Flow

```mermaid
flowchart TD
    start([User Opens Dashboard]) --> request[GET /api/dashboard]
    request --> getCurrentMonth[Get Current Month & Year]
    getCurrentMonth --> getLastMonth[Calculate Last Month & Year]
    
    getLastMonth --> countTotal[Count Total Employees<br/>from employees table]
    countTotal --> countActive[Count Active Employees<br/>where active = true]
    
    countActive --> sumCurrent[Sum Final Salaries<br/>for Current Month]
    sumCurrent --> sumLast[Sum Final Salaries<br/>for Last Month]
    
    sumLast --> getActiveEmps[Get All Active Employees]
    getActiveEmps --> loopEmps{For Each<br/>Active Employee}
    
    loopEmps --> checkSalary{Salary Generated<br/>for Current Month?}
    checkSalary -->|No| countPending[Increment Pending Count]
    checkSalary -->|Yes| nextEmp
    countPending --> nextEmp[Next Employee]
    
    nextEmp --> moreEmps{More<br/>Employees?}
    moreEmps -->|Yes| loopEmps
    moreEmps -->|No| buildResponse[Build Dashboard Response]
    
    buildResponse --> return200[Return 200 OK<br/>with Dashboard Data]
    return200 --> display[Display Dashboard<br/>Stats & Charts]
    display --> done([End])
```

**Dashboard Metrics:**

1. **Total Employees**: Count of all employee records
2. **Active Employees**: Count where active = true
3. **Total Salary This Month**: Sum of final_salary for current month
4. **Total Salary Last Month**: Sum of final_salary for previous month
5. **Pending Salary Generations**: Count of active employees without salary record for current month

---

## 4. Sequence Diagrams

### 4.1 Complete Salary Generation Sequence

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Controller
    participant Service
    participant Repository
    participant Database
    participant TwilioAPI
    
    User->>Frontend: Click Generate Salary
    Frontend->>Controller: POST /api/salaries/generate
    Controller->>Service: generateSalary(employeeId, month, year)
    
    Service->>Repository: findByEmployeeIdAndMonthAndYear()
    Repository->>Database: SELECT from salaries
    Database-->>Repository: Check result
    Repository-->>Service: Optional<Salary>
    
    alt Salary Already Exists
        Service-->>Controller: throw IllegalArgumentException
        Controller-->>Frontend: 400 Bad Request
        Frontend-->>User: Display Error
    else Salary Not Generated
        Service->>Repository: findById(employeeId)
        Repository->>Database: SELECT from employees
        Database-->>Repository: Employee data
        Repository-->>Service: Employee
        
        Service->>Repository: findByEmployeeIdAndMonthAndYear()
        Repository->>Database: SELECT from overtimes
        Database-->>Repository: Overtime list
        Repository-->>Service: List<Overtime>
        
        Service->>Repository: findByEmployeeIdAndMonthAndYear()
        Repository->>Database: SELECT from advances
        Database-->>Repository: Advance list
        Repository-->>Service: List<Advance>
        
        Service->>Repository: findUnpaidLeavesByEmployeeIdAndMonthAndYear()
        Repository->>Database: SELECT from leaves WHERE type = UNPAID
        Database-->>Repository: Leave list
        Repository-->>Service: List<Leave>
        
        Service->>Service: Calculate Salary<br/>(Base + OT - Adv - PF - Leaves)
        
        Service->>Repository: save(salary)
        Repository->>Database: INSERT INTO salaries
        Database-->>Repository: Saved salary with ID
        Repository-->>Service: Salary
        
        loop For each transaction detail
            Service->>Repository: save(salaryDetail)
            Repository->>Database: INSERT INTO salary_details
            Database-->>Repository: Saved detail
            Repository-->>Service: SalaryDetail
        end
        
        Service-->>Controller: Salary
        Controller-->>Frontend: 200 OK with Salary data
        Frontend-->>User: Display Success
        
        User->>Frontend: Click Send SMS
        Frontend->>Controller: POST /api/salaries/:id/send-sms
        Controller->>Service: sendSalarySms(salaryId)
        
        Service->>Repository: findById(salaryId)
        Repository->>Database: SELECT from salaries
        Database-->>Repository: Salary data
        Repository-->>Service: Salary
        
        Service->>Service: Format Phone Number
        Service->>Service: Build SMS Content
        
        Service->>TwilioAPI: Send SMS
        TwilioAPI-->>Service: Success/Failure
        
        alt SMS Success
            Service->>Repository: save(salary with sms_sent=true)
            Repository->>Database: UPDATE salaries
            Database-->>Repository: Updated
            Repository-->>Service: Updated Salary
            Service-->>Controller: Success
            Controller-->>Frontend: 200 OK
            Frontend-->>User: SMS Sent Successfully
        else SMS Failure
            Service->>Repository: save(salary with sms_sent=false)
            Repository->>Database: UPDATE salaries
            Database-->>Repository: Updated
            Repository-->>Service: Updated Salary
            Service-->>Controller: throw RuntimeException
            Controller-->>Frontend: 500 Internal Server Error
            Frontend-->>User: SMS Send Failed
        end
    end
```

### 4.2 Employee Creation Sequence

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Controller
    participant ValidationLayer
    participant Service
    participant Repository
    participant Database
    
    User->>Frontend: Fill Employee Form
    User->>Frontend: Submit
    Frontend->>Frontend: Client-side Validation
    
    alt Validation Fails
        Frontend-->>User: Display Validation Errors
    else Validation Passes
        Frontend->>Controller: POST /api/employees
        Controller->>ValidationLayer: @Valid EmployeeRequest
        
        alt Server Validation Fails
            ValidationLayer-->>Controller: MethodArgumentNotValidException
            Controller->>Controller: GlobalExceptionHandler
            Controller-->>Frontend: 400 Bad Request with errors
            Frontend-->>User: Display Field Errors
        else Server Validation Passes
            Controller->>Service: createEmployee(request)
            Service->>Service: Map Request to Entity
            Service->>Repository: save(employee)
            Repository->>Database: INSERT INTO employees
            
            alt Mobile Duplicate (DB Constraint)
                Database-->>Repository: Constraint Violation
                Repository-->>Service: DataIntegrityViolationException
                Service-->>Controller: Exception
                Controller-->>Frontend: 400 Bad Request
                Frontend-->>User: Mobile already registered
            else Insert Success
                Database-->>Repository: Saved employee
                Repository-->>Service: Employee
                Service->>Service: Map Entity to Response
                Service-->>Controller: EmployeeResponse
                Controller-->>Frontend: 200 OK with data
                Frontend-->>User: Display Success Message
                Frontend->>Frontend: Refresh Employee List
            end
        end
    end
```

### 4.3 Transaction Recording Sequence (Advance/Leave/Overtime)

```mermaid
sequenceDiagram
    actor User
    participant Frontend
    participant Controller
    participant Service
    participant Repository
    participant Database
    
    User->>Frontend: Select Transaction Type<br/>(Advance/Leave/Overtime)
    User->>Frontend: Select Employee
    User->>Frontend: Enter Details
    User->>Frontend: Submit
    
    Frontend->>Controller: POST /api/{transactions}
    Controller->>Controller: Validate Request
    Controller->>Service: create{Transaction}(request)
    
    Service->>Repository: findById(employeeId)
    Repository->>Database: SELECT from employees
    Database-->>Repository: Employee data
    Repository-->>Service: Employee
    
    alt Employee Not Found
        Service-->>Controller: throw ResourceNotFoundException
        Controller-->>Frontend: 404 Not Found
        Frontend-->>User: Employee not found
    else Employee Inactive
        Service-->>Controller: throw IllegalArgumentException
        Controller-->>Frontend: 400 Bad Request
        Frontend-->>User: Cannot add transaction for inactive employee
    else Employee Active
        Service->>Service: Map Request to Entity
        
        alt Transaction is Overtime
            Service->>Service: Calculate Total<br/>Hours × RatePerHour
        end
        
        Service->>Repository: save(transaction)
        Repository->>Database: INSERT INTO {table}
        Database-->>Repository: Saved transaction
        Repository-->>Service: Transaction Entity
        Service->>Service: Map to Response
        Service-->>Controller: Response DTO
        Controller-->>Frontend: 200 OK with data
        Frontend-->>User: Display Success
        Frontend->>Frontend: Refresh Transaction List
        Frontend->>Frontend: Show Impact on Salary Note
    end
```

---

## 5. Data Flow Diagrams

### 5.1 Level 0 - Context Diagram

```mermaid
graph TB
    subgraph external [External Entities]
        admin[System Admin]
        twilio[Twilio SMS Service]
    end
    
    subgraph system [Employee Salary<br/>Management System]
        process[Process]
    end
    
    subgraph data [Data Store]
        db[(Database)]
    end
    
    admin -->|Employee Data<br/>Transactions<br/>Salary Requests| process
    process -->|Employee Details<br/>Salary Slips<br/>Reports| admin
    process -->|SMS Content<br/>Phone Numbers| twilio
    twilio -->|Delivery Status| process
    process <-->|Read/Write| db
```

### 5.2 Level 1 - Major Processes

```mermaid
graph TB
    admin[System Admin]
    
    subgraph system [Employee Salary Management System]
        p1[1.0<br/>Manage<br/>Employees]
        p2[2.0<br/>Record<br/>Transactions]
        p3[3.0<br/>Calculate<br/>Salary]
        p4[4.0<br/>Send SMS<br/>Notifications]
        p5[5.0<br/>Generate<br/>Reports]
    end
    
    d1[(D1: Employees)]
    d2[(D2: Transactions)]
    d3[(D3: Salaries)]
    
    twilio[Twilio API]
    
    admin -->|Employee Info| p1
    p1 -->|Employee Details| admin
    p1 <-->|Employee Data| d1
    
    admin -->|Transaction Data| p2
    p2 -->|Transaction Confirmation| admin
    p2 <-->|Transaction Records| d2
    p2 -->|Employee Reference| d1
    
    admin -->|Salary Request| p3
    p3 -->|Salary Preview| admin
    d1 -->|Employee Data| p3
    d2 -->|Transaction Data| p3
    p3 -->|Salary Records| d3
    
    admin -->|SMS Request| p4
    d3 -->|Salary Data| p4
    d1 -->|Employee Contact| p4
    p4 -->|SMS Content| twilio
    twilio -->|Status| p4
    p4 -->|SMS Status| d3
    p4 -->|Confirmation| admin
    
    admin -->|Report Request| p5
    d1 -->|Employee Data| p5
    d3 -->|Salary Data| p5
    p5 -->|Dashboard Stats| admin
```

### 5.3 Level 2 - Salary Calculation Process Detail

```mermaid
graph TB
    admin[Admin User]
    
    subgraph salaryProcess [3.0 Calculate Salary]
        p31[3.1<br/>Fetch Employee<br/>Base Salary]
        p32[3.2<br/>Aggregate<br/>Overtime]
        p33[3.3<br/>Aggregate<br/>Advances]
        p34[3.4<br/>Calculate<br/>Leave Deduction]
        p35[3.5<br/>Calculate<br/>PF Deduction]
        p36[3.6<br/>Compute<br/>Final Salary]
        p37[3.7<br/>Create<br/>Salary Details]
        p38[3.8<br/>Save<br/>Salary Record]
    end
    
    d1[(D1: Employees)]
    d2[(D2: Transactions<br/>Overtime, Advances, Leaves)]
    d3[(D3: Salaries)]
    d4[(D4: Salary Details)]
    
    admin -->|Employee ID, Month, Year| p31
    
    d1 -->|Base Salary, PF%| p31
    p31 -->|Base Salary| p35
    p31 -->|Base Salary| p36
    
    d2 -->|Overtime Records| p32
    p32 -->|Total Overtime| p36
    p32 -->|OT Details| p37
    
    d2 -->|Advance Records| p33
    p33 -->|Total Advances| p36
    p33 -->|Advance Details| p37
    
    d2 -->|Unpaid Leaves| p34
    p31 -->|Base Salary| p34
    p34 -->|Leave Deduction| p36
    p34 -->|Leave Details| p37
    
    p35 -->|PF Deduction| p36
    
    p36 -->|Final Salary| p38
    p37 -->|Detail Records| p38
    
    p38 -->|Salary Record| d3
    p38 -->|Detail Records| d4
    p38 -->|Salary Preview/Confirmation| admin
```

---

## 6. State Transition Diagrams

### 6.1 Employee State Transitions

```mermaid
stateDiagram-v2
    [*] --> Created: Create Employee
    Created --> Active: Auto-activation<br/>(active=true by default)
    Active --> Active: Update Details
    Active --> Inactive: Deactivate Employee
    Inactive --> Active: Reactivate Employee<br/>(Manual DB update)
    Active --> [*]: Hard Delete<br/>(Not Implemented)
    
    note right of Active
        Can have transactions
        Can generate salary
        Visible in active lists
    end note
    
    note right of Inactive
        No new transactions
        No new salary generation
        Historical data preserved
    end note
```

### 6.2 Salary Record State Transitions

```mermaid
stateDiagram-v2
    [*] --> NotGenerated: Month Begins
    NotGenerated --> Preview: Request Preview
    Preview --> NotGenerated: Cancel/Modify Data
    Preview --> Generated: Generate Salary
    Generated --> SMSPending: Save Complete
    SMSPending --> SMSSent: Send SMS Success
    SMSPending --> SMSFailed: Send SMS Failure
    SMSSent --> SMSResent: Resend SMS
    SMSFailed --> SMSRetry: Retry Send
    SMSRetry --> SMSSent: Success
    SMSRetry --> SMSFailed: Failure
    SMSSent --> [*]: End of Workflow
    
    note right of Generated
        sms_sent = false
        sms_sent_at = null
    end note
    
    note right of SMSSent
        sms_sent = true
        sms_sent_at = timestamp
    end note
    
    note left of NotGenerated
        Transactions can be
        added/modified/deleted
    end note
    
    note left of Generated
        Transactions locked
        (modification not recommended)
    end note
```

### 6.3 SMS Delivery State Machine

```mermaid
stateDiagram-v2
    [*] --> Initialized: Trigger Send SMS
    Initialized --> ConfigCheck: Check Twilio Config
    ConfigCheck --> ConfigError: Not Configured
    ConfigCheck --> PhoneFormat: Config Valid
    PhoneFormat --> ContentBuild: Format Phone Number
    ContentBuild --> TwilioCall: Build SMS Content
    TwilioCall --> Sending: Call Twilio API
    Sending --> Success: HTTP 200 + SID
    Sending --> NetworkError: Network Failure
    Sending --> APIError: Twilio API Error
    Success --> UpdateDB: Update sms_sent=true
    UpdateDB --> [*]: Complete
    NetworkError --> UpdateFailed: Update sms_sent=false
    APIError --> UpdateFailed
    ConfigError --> [*]: Abort
    UpdateFailed --> [*]: Complete with Error
    
    note right of Success
        SMS queued by Twilio
        Delivery status tracked
        by Twilio webhook (future)
    end note
```

---

## 7. Integration Flows

### 7.1 Twilio SMS Integration Flow

```mermaid
flowchart TD
    start([SMS Send Request]) --> loadConfig[Load Twilio Configuration<br/>Account SID, Auth Token, Phone]
    loadConfig --> validateConfig{All Credentials<br/>Present?}
    
    validateConfig -->|No| logError[Log Configuration Error]
    logError --> returnError[Return Error Response]
    returnError --> done([End])
    
    validateConfig -->|Yes| initSDK[Initialize Twilio SDK<br/>Twilio.init]
    initSDK --> fetchSalary[Fetch Salary Record<br/>from Database]
    fetchSalary --> fetchDetails[Fetch Salary Details<br/>from Database]
    fetchDetails --> getEmployee[Get Employee Info<br/>Name, Mobile]
    
    getEmployee --> formatPhone[Format Phone Number]
    formatPhone --> validatePhone{Valid Phone<br/>Format?}
    
    validatePhone -->|No| logPhoneError[Log Phone Format Error]
    logPhoneError --> returnError
    
    validatePhone -->|Yes| buildContent[Build SMS Content<br/>Format Salary Slip Text]
    buildContent --> createMessage[Create Twilio Message<br/>Message.creator]
    createMessage --> setParams[Set Parameters<br/>To, From, Body]
    setParams --> sendAPI[Call create Method<br/>Send to Twilio API]
    
    sendAPI --> twilioProcess{Twilio API<br/>Response}
    
    twilioProcess -->|Success| getSID[Get Message SID]
    getSID --> updateSuccess[Update Database<br/>sms_sent = true<br/>sms_sent_at = now]
    updateSuccess --> logSuccess[Log Success with SID]
    logSuccess --> returnSuccess[Return Success Response]
    returnSuccess --> done
    
    twilioProcess -->|Error| parseError[Parse Error Message]
    parseError --> updateFailed[Update Database<br/>sms_sent = false]
    updateFailed --> logFailure[Log Failure with Error]
    logFailure --> returnError
```

**Twilio API Call Details:**

```java
Message message = Message.creator(
    new PhoneNumber("+919876543210"),  // To
    new PhoneNumber(twilioPhoneNumber), // From
    smsContent                           // Body
).create();
```

**Error Scenarios:**
1. **Configuration Error**: Missing credentials → Return 500
2. **Phone Format Error**: Invalid phone number → Return 400
3. **Network Error**: Cannot reach Twilio → Return 500
4. **API Error**: Twilio rejects request → Return 500
5. **Rate Limit**: Twilio rate limit exceeded → Return 429

### 7.2 Database Transaction Flow

```mermaid
flowchart TD
    start([Service Method Called]) --> transStart[@Transactional<br/>Begin Transaction]
    transStart --> bizLogic[Execute Business Logic]
    bizLogic --> query1[Database Query 1]
    query1 --> query2[Database Query 2]
    query2 --> calculation[Perform Calculations]
    calculation --> save1[Database Save 1]
    save1 --> save2[Database Save 2]
    save2 --> checkException{Exception<br/>Occurred?}
    
    checkException -->|Yes| rollback[Rollback Transaction]
    rollback --> logError[Log Error]
    logError --> throwException[Throw Exception]
    throwException --> done([End])
    
    checkException -->|No| commit[Commit Transaction]
    commit --> returnSuccess[Return Success]
    returnSuccess --> done
```

**Transaction Management:**
- `@Transactional` on service methods
- Automatic rollback on RuntimeException
- Commit on successful completion
- Read-only transactions for queries

---

## 8. Error Handling Flows

### 8.1 Global Exception Handling Flow

```mermaid
flowchart TD
    start([Exception Occurs]) --> checkType{Exception<br/>Type?}
    
    checkType -->|ResourceNotFoundException| handle404[GlobalExceptionHandler<br/>handleResourceNotFoundException]
    handle404 --> build404[Build ApiResponse<br/>success = false]
    build404 --> return404[Return 404 Not Found]
    return404 --> logError404[Log Error]
    logError404 --> done([End])
    
    checkType -->|IllegalArgumentException| handle400[GlobalExceptionHandler<br/>handleIllegalArgumentException]
    handle400 --> build400[Build ApiResponse<br/>success = false]
    build400 --> return400[Return 400 Bad Request]
    return400 --> logError400[Log Error]
    logError400 --> done
    
    checkType -->|MethodArgumentNotValidException| handleValidation[GlobalExceptionHandler<br/>handleValidationExceptions]
    handleValidation --> extractErrors[Extract Field Errors<br/>from BindingResult]
    extractErrors --> buildMap[Build Error Map<br/>field: message]
    buildMap --> buildValidation[Build ApiResponse<br/>success = false<br/>data = error map]
    buildValidation --> return400Val[Return 400 Bad Request]
    return400Val --> logErrorVal[Log Validation Errors]
    logErrorVal --> done
    
    checkType -->|Exception Generic| handle500[GlobalExceptionHandler<br/>handleGenericException]
    handle500 --> build500[Build ApiResponse<br/>success = false]
    build500 --> return500[Return 500 Internal Server Error]
    return500 --> logError500[Log Full Stack Trace]
    logError500 --> alertAdmin[Alert Administrator<br/>Critical Error]
    alertAdmin --> done
```

### 8.2 Validation Error Flow

```mermaid
flowchart TD
    start([Request Received]) --> parseRequest[Parse Request Body]
    parseRequest --> validate[@Valid Annotation<br/>Triggers Validation]
    validate --> checkConstraints{All Constraints<br/>Satisfied?}
    
    checkConstraints -->|Yes| proceed[Proceed to Controller Method]
    proceed --> done([Continue Processing])
    
    checkConstraints -->|No| collectErrors[Collect All Validation Errors]
    collectErrors --> buildErrorMap[Build Error Map]
    
    buildErrorMap --> exampleErrors[Example Errors:<br/>name: must not be blank<br/>mobile: must match pattern<br/>baseSalary: must be greater than 0]
    
    exampleErrors --> throwException[Throw MethodArgumentNotValidException]
    throwException --> globalHandler[Global Exception Handler]
    globalHandler --> formatResponse[Format Error Response]
    formatResponse --> returnToClient[Return 400 with Error Map]
    returnToClient --> clientDisplay[Client Displays Field Errors]
    clientDisplay --> done
```

**Validation Annotations Used:**
- `@NotNull`: Field cannot be null
- `@NotBlank`: String cannot be empty
- `@Min`: Minimum numeric value
- `@Max`: Maximum numeric value
- `@Pattern`: Regex pattern match
- `@Valid`: Nested object validation

---

## 9. End-to-End Workflows

### 9.1 Complete Monthly Salary Workflow

```mermaid
flowchart TD
    monthStart([Month Begins]) --> dailyOps[Daily Operations]
    
    dailyOps --> recordAdv{Employee Requests<br/>Advance?}
    recordAdv -->|Yes| createAdv[Record Advance<br/>POST /api/advances]
    recordAdv -->|No| checkLeave
    createAdv --> checkLeave{Employee Takes<br/>Leave?}
    
    checkLeave -->|Yes| recordLeave[Record Leave<br/>POST /api/leaves<br/>Type: PAID/UNPAID]
    checkLeave -->|No| checkOT
    recordLeave --> checkOT{Employee Works<br/>Overtime?}
    
    checkOT -->|Yes| recordOT[Record Overtime<br/>POST /api/overtimes]
    checkOT -->|No| nextDay{More Days<br/>in Month?}
    recordOT --> nextDay
    
    nextDay -->|Yes| dailyOps
    nextDay -->|No| monthEnd[Month End Approaches]
    
    monthEnd --> reviewData[Admin Reviews<br/>Transaction Data]
    reviewData --> corrections{Need<br/>Corrections?}
    corrections -->|Yes| modifyData[Modify/Delete Transactions]
    modifyData --> reviewData
    
    corrections -->|No| previewSalary[Preview Salary<br/>POST /api/salaries/preview]
    previewSalary --> reviewPreview[Review Salary Calculation<br/>& Date-wise Breakdown]
    reviewPreview --> approvePreview{Approve<br/>Calculation?}
    
    approvePreview -->|No| modifyData
    approvePreview -->|Yes| generateSalary[Generate Salary<br/>POST /api/salaries/generate]
    
    generateSalary --> checkGenerated{Generation<br/>Success?}
    checkGenerated -->|No| investigateError[Investigate Error<br/>Fix Issues]
    investigateError --> generateSalary
    
    checkGenerated -->|Yes| sendSMS[Send SMS<br/>POST /api/salaries/:id/send-sms]
    sendSMS --> checkSMS{SMS<br/>Success?}
    
    checkSMS -->|No| retrySMS{Retry<br/>SMS?}
    retrySMS -->|Yes| sendSMS
    retrySMS -->|No| manualNotify[Notify Employee Manually]
    
    checkSMS -->|Yes| employeeReceives[Employee Receives<br/>Salary Slip SMS]
    manualNotify --> updateRecords[Update Salary Records]
    employeeReceives --> updateRecords
    
    updateRecords --> nextEmployee{More<br/>Employees?}
    nextEmployee -->|Yes| previewSalary
    nextEmployee -->|No| generateReports[Generate Monthly Reports<br/>Dashboard Statistics]
    
    generateReports --> archiveData[Archive Monthly Data]
    archiveData --> nextMonth([Next Month Begins])
```

### 9.2 New Employee Onboarding Workflow

```mermaid
flowchart TD
    start([New Employee Joins]) --> gatherInfo[Gather Employee Information<br/>Name, Mobile, Base Salary, PF%]
    gatherInfo --> verifyInfo[Verify Information Accuracy]
    verifyInfo --> checkDuplicate{Mobile Number<br/>Already Exists?}
    
    checkDuplicate -->|Yes| updateExisting[Update Existing Record<br/>or Resolve Conflict]
    updateExisting --> done([End])
    
    checkDuplicate -->|No| createEmployee[Create Employee<br/>POST /api/employees]
    createEmployee --> verifyCreation{Created<br/>Successfully?}
    
    verifyCreation -->|No| fixErrors[Fix Validation Errors]
    fixErrors --> createEmployee
    
    verifyCreation -->|Yes| confirmActive[Confirm Employee Status<br/>active = true]
    confirmActive --> setupTransactions[Setup Transaction Tracking<br/>Ready for Advances/Leaves/OT]
    setupTransactions --> informEmployee[Inform Employee<br/>Mobile # Registered]
    informEmployee --> onboardingComplete[Onboarding Complete<br/>Employee in System]
    onboardingComplete --> waitForMonth[Wait for Month End]
    waitForMonth --> firstSalary[Generate First Salary<br/>Pro-rated if Mid-Month]
    firstSalary --> done
```

### 9.3 Employee Exit Workflow

```mermaid
flowchart TD
    start([Employee Decides to Leave]) --> noticeAdmin[Notify Administrator]
    noticeAdmin --> calcFinal[Calculate Final Settlement<br/>Pro-rated Salary + Dues]
    calcFinal --> reviewTransactions[Review All Pending Transactions<br/>Advances, Leaves, Overtime]
    reviewTransactions --> finalSalary[Generate Final Salary<br/>Include Exit Month]
    finalSalary --> sendFinalSMS[Send Final Salary SMS]
    sendFinalSMS --> settlePayment[Settle Payment with Employee]
    settlePayment --> deactivateEmployee[Deactivate Employee<br/>DELETE /api/employees/:id<br/>Sets active = false]
    deactivateEmployee --> verifyDeactivation{Deactivation<br/>Success?}
    
    verifyDeactivation -->|No| investigateIssue[Investigate Issue]
    investigateIssue --> deactivateEmployee
    
    verifyDeactivation -->|Yes| archiveData[Archive Employee Data<br/>Historical Records Preserved]
    archiveData --> updateDashboard[Update Dashboard<br/>Reduce Active Count]
    updateDashboard --> notifyTeam[Notify Relevant Teams]
    notifyTeam --> exitComplete([Exit Process Complete])
```

### 9.4 Error Resolution Workflow

```mermaid
flowchart TD
    start([Error Detected]) --> identifyError[Identify Error Type<br/>& Source]
    identifyError --> checkType{Error<br/>Category?}
    
    checkType -->|Data Entry Error| reviewTransaction[Review Transaction Details]
    reviewTransaction --> deleteWrong[Delete Incorrect Record<br/>DELETE /api/{resource}/:id]
    deleteWrong --> enterCorrect[Enter Correct Data<br/>POST /api/{resource}]
    enterCorrect --> verify[Verify Correction]
    verify --> done([Error Resolved])
    
    checkType -->|Calculation Error| checkGenerated{Salary Already<br/>Generated?}
    checkGenerated -->|No| fixTransactions[Fix Transaction Data]
    fixTransactions --> recalculate[Recalculate Preview]
    recalculate --> verify
    
    checkGenerated -->|Yes| documentAdjustment[Document Adjustment]
    documentAdjustment --> nextMonthCorrection[Apply Correction in<br/>Next Month Salary]
    nextMonthCorrection --> notifyEmployee[Notify Employee<br/>of Correction]
    notifyEmployee --> done
    
    checkType -->|SMS Delivery Error| checkConfig{Twilio<br/>Configured?}
    checkConfig -->|No| configureTwilio[Configure Twilio Credentials]
    configureTwilio --> retrySMS[Retry SMS Send]
    retrySMS --> checkSuccess{Success?}
    checkSuccess -->|Yes| done
    checkSuccess -->|No| manualNotify[Send Manual Notification]
    manualNotify --> done
    
    checkConfig -->|Yes| checkPhoneFormat{Phone Number<br/>Valid?}
    checkPhoneFormat -->|No| updatePhone[Update Employee<br/>Phone Number]
    updatePhone --> retrySMS
    checkPhoneFormat -->|Yes| checkTwilioStatus[Check Twilio<br/>Service Status]
    checkTwilioStatus --> contactSupport[Contact Twilio Support]
    contactSupport --> manualNotify
    
    checkType -->|System Error| checkLogs[Review System Logs]
    checkLogs --> identifyBug[Identify Bug/Issue]
    identifyBug --> reportBug[Report to Development Team]
    reportBug --> implementFix[Implement Fix]
    implementFix --> testFix[Test Fix]
    testFix --> deployFix[Deploy Fix]
    deployFix --> done
```

---

## Appendix: Flow Glossary

### Key Terms

- **Active Employee**: Employee with `active = true`, eligible for transactions and salary generation
- **Inactive Employee**: Employee with `active = false`, preserved for historical records
- **Soft Delete**: Setting `active = false` instead of removing record
- **Hard Delete**: Permanent removal from database (not implemented)
- **Transaction**: General term for Advance, Leave, or Overtime record
- **Date-wise Breakdown**: Detailed listing of all transactions by date
- **Salary Preview**: Calculation without saving to database
- **Salary Generation**: Calculation with database persistence
- **SMS Delivery Status**: Tracked via `sms_sent` and `sms_sent_at` fields
- **Denormalization**: Storing calculated values (e.g., salary components) for historical accuracy
- **Idempotency**: Preventing duplicate salary generation for same employee-month-year

### Business Calculations

#### Daily Salary
```
Daily Salary = Base Salary ÷ Number of Days in Month
```

#### Overtime Amount
```
Overtime Amount = Hours × Rate Per Hour
```

#### Leave Deduction
```
Leave Deduction = Daily Salary × Number of Unpaid Leave Days
```

#### PF Deduction
```
PF Deduction = Base Salary × (PF Percentage ÷ 100)
```

#### Final Salary
```
Final Salary = Base Salary
             + Total Overtime
             - Total Advances
             - PF Deduction
             - Leave Deduction
```

### HTTP Status Codes Used

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, POST, PUT operations |
| 400 | Bad Request | Validation errors, business logic errors |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Unexpected server errors, integration failures |

### Common Flow Patterns

1. **CRUD Pattern**: Create → Read → Update → Delete
2. **Preview-Generate Pattern**: Preview → Review → Generate → Confirm
3. **Request-Validate-Process-Respond**: Standard API flow
4. **Fetch-Calculate-Save**: Salary calculation pattern
5. **Trigger-Check-Execute-Update**: SMS sending pattern

---

## Document Metadata

- **Document Type**: Application Flow Document
- **Version**: 1.0
- **Date**: January 1, 2025
- **Purpose**: Technical reference for developers and stakeholders
- **Related Documents**: System Design Document, API Documentation, POSTMAN Collection
- **Maintained By**: Development Team
- **Last Updated**: January 1, 2025

---

## Change Log

| Version | Date | Changes | Author |
|---------|------|---------|--------|
| 1.0 | 2025-01-01 | Initial document creation | System Team |

---

## Notes

This flow document should be updated whenever:
- New features are added
- Business logic changes
- API endpoints are modified
- Integration points are updated
- Error handling is improved

All diagrams use Mermaid syntax for easy maintenance and version control.

