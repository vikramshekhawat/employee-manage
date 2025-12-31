# Employee Salary Management System - Backend

A comprehensive Spring Boot backend application for managing employee salaries, advances, leaves, overtime, and automated salary SMS notifications.

## Features

- **Employee Management**: Add, update, and deactivate employees
- **Advance Salary Management**: Track date-wise salary advances
- **Leave Management**: Manage paid and unpaid leaves
- **Overtime Management**: Record and calculate overtime hours
- **Salary Calculation**: Automated salary calculation with date-wise breakdown
- **SMS Notifications**: Send salary details via SMS using Twilio
- **Dashboard**: Overview statistics and insights

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: Spring Data JPA / Hibernate
- **SMS Gateway**: Twilio
- **Validation**: Jakarta Validation

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Gradle 7.0 or higher (or use Gradle Wrapper)
- Twilio Account (for SMS functionality)

## Setup Instructions

### 1. Database Configuration

Create a MySQL database:
```sql
CREATE DATABASE emp_manage_db;
```

Or let the application create it automatically by setting `createDatabaseIfNotExist=true` in the connection URL.

### 2. Application Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Twilio Configuration

Set environment variables or update `application.properties`:

```properties
twilio.account.sid=your_twilio_account_sid
twilio.auth.token=your_twilio_auth_token
twilio.phone.number=your_twilio_phone_number
```

Or set as environment variables:
```bash
export TWILIO_ACCOUNT_SID=your_account_sid
export TWILIO_AUTH_TOKEN=your_auth_token
export TWILIO_PHONE_NUMBER=your_phone_number
```

### 4. Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

Or using Gradle wrapper on Windows:
```bash
gradlew.bat build
gradlew.bat bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/login` - Admin login

### Employee Management
- `POST /api/employees` - Create new employee
- `GET /api/employees` - Get all active employees
- `GET /api/employees/{id}` - Get employee by ID
- `PUT /api/employees/{id}` - Update employee
- `PUT /api/employees/{id}/deactivate` - Deactivate employee

### Advance Salary Management
- `POST /api/advances` - Create advance
- `GET /api/advances/employee/{employeeId}` - Get all advances for employee
- `GET /api/advances/employee/{employeeId}/month/{month}/year/{year}` - Get advances for specific month

### Leave Management
- `POST /api/leaves` - Create leave record
- `GET /api/leaves/employee/{employeeId}` - Get all leaves for employee
- `GET /api/leaves/employee/{employeeId}/month/{month}/year/{year}` - Get leaves for specific month

### Overtime Management
- `POST /api/overtimes` - Create overtime record
- `GET /api/overtimes/employee/{employeeId}` - Get all overtime records
- `GET /api/overtimes/employee/{employeeId}/month/{month}/year/{year}` - Get overtime for specific month

### Salary Management
- `POST /api/salary/preview` - Preview salary calculation
- `POST /api/salary/generate` - Generate salary and send SMS
- `GET /api/salary/employee/{employeeId}` - Get salary history
- `POST /api/salary/{salaryId}/resend-sms` - Resend salary SMS

### Dashboard
- `GET /api/dashboard` - Get dashboard statistics

## Salary Calculation Formula

```
Final Salary = Base Salary + Overtime - Advances - PF - Leave Deduction
```

Where:
- **Base Salary**: Employee's monthly base salary
- **Overtime**: Sum of all overtime amounts for the month
- **Advances**: Sum of all advance amounts for the month
- **PF**: Provident Fund deduction (Base Salary × PF Percentage / 100)
- **Leave Deduction**: Unpaid leave days × Daily Salary (Base Salary / 30)

## SMS Format

The salary SMS includes:
- Employee name and month/year
- Base salary
- Date-wise breakdown (overtime, advances, leaves)
- Total overtime
- Total advances
- PF deduction
- Leave deduction
- Final payable amount

## Default Admin Credentials

- **Username**: `admin`
- **Password**: `admin123`

> **Note**: In production, implement proper authentication with Spring Security and JWT tokens.

## Project Structure

```
src/main/java/com/empmanage/
├── config/          # Configuration classes (CORS, Twilio)
├── controller/     # REST controllers
├── dto/            # Data Transfer Objects
│   ├── request/    # Request DTOs
│   └── response/   # Response DTOs
├── entity/         # JPA entities
├── exception/      # Exception handling
├── repository/     # Spring Data JPA repositories
└── service/        # Business logic services
```

## Database Schema

The application uses JPA auto-ddl to create tables. Main entities:

- **employees**: Employee information
- **advances**: Salary advances
- **leaves**: Leave records
- **overtimes**: Overtime records
- **salaries**: Generated salary records
- **salary_details**: Date-wise salary breakdown

## Development

### Running Tests
```bash
./gradlew test
```

### Building JAR
```bash
./gradlew bootJar
```

The JAR file will be created in `build/libs/`

## Deployment

### Production Considerations

1. **Security**: Implement Spring Security with JWT authentication
2. **Database**: Use connection pooling and proper database credentials
3. **CORS**: Restrict CORS to specific frontend domains
4. **Error Handling**: Configure proper logging
5. **Environment Variables**: Use environment variables for sensitive data
6. **HTTPS**: Enable HTTPS in production

### Deployment Options

- **AWS EC2**: Deploy as a JAR file
- **Render**: Deploy directly from Git repository
- **Docker**: Containerize the application

## Future Enhancements

- Employee self-login portal
- PDF payslip generation
- WhatsApp notifications
- Role-based access control
- Advanced reporting and analytics

## License

This project is proprietary software.

## Support

For issues and questions, please contact the development team.


