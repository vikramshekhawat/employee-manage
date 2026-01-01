# Employee Salary Management System - Complete Setup Guide

This guide provides complete instructions to set up and run both the backend (Spring Boot) and frontend (React.js) of the Employee Salary Management System.

## Prerequisites

### Backend Requirements
- Java 17 or higher
- MySQL 8.0 or higher
- Gradle 7.0+ (or use included Gradle Wrapper)

### Frontend Requirements
- Node.js 16+ and npm

## Backend Setup

### 1. Database Configuration

Start MySQL and create the database:

```sql
CREATE DATABASE emp_manage_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Or let the application create it automatically (configured in application.properties).

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Update these with your MySQL credentials
spring.datasource.username=root
spring.datasource.password=your_password

# Optional: Configure Twilio for SMS (leave empty to skip SMS)
twilio.account.sid=${TWILIO_ACCOUNT_SID:}
twilio.auth.token=${TWILIO_AUTH_TOKEN:}
twilio.phone.number=${TWILIO_PHONE_NUMBER:}
```

### 3. Build and Run Backend

Using Gradle Wrapper (recommended):

**Windows:**
```bash
cd emp-manage
.\gradlew.bat clean build
.\gradlew.bat bootRun
```

**Linux/Mac:**
```bash
cd emp-manage
./gradlew clean build
./gradlew bootRun
```

Or run the JAR directly:
```bash
java -jar build/libs/emp-manage-1.0.0.jar
```

The backend will start on `http://localhost:8080`

### 4. Verify Backend is Running

Open your browser and navigate to:
- Health Check: `http://localhost:8080/api/dashboard`

## Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Configure API URL (Optional)

Create a `.env` file in the frontend directory:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

### 4. Start Frontend Development Server

```bash
npm start
```

The frontend will start on `http://localhost:3000` and automatically open in your browser.

## Quick Start (Both Servers)

### Windows:
```bash
# Terminal 1 - Backend
cd emp-manage
.\gradlew.bat bootRun

# Terminal 2 - Frontend (in a new terminal)
cd frontend
npm install
npm start
```

### Linux/Mac:
```bash
# Terminal 1 - Backend
cd emp-manage
./gradlew bootRun

# Terminal 2 - Frontend (in a new terminal)
cd frontend
npm install
npm start
```

## Access the Application

1. Open browser and go to: `http://localhost:3000`
2. Login with default credentials:
   - **Username:** admin
   - **Password:** admin123

## Application Features

### 1. Dashboard
- View employee statistics
- Total and active employee counts
- Monthly salary comparisons
- Pending salary generations
- Interactive charts
- Quick action buttons

### 2. Employee Management
- Add new employees
- Edit employee details
- Deactivate employees
- View employee list
- Mobile number validation
- Base salary and PF percentage configuration

### 3. Transaction Management

**Advances:**
- Record salary advances
- Date-wise tracking
- Optional descriptions
- View all advances by employee
- Delete advances

**Leaves:**
- Record paid/unpaid leaves
- Date selection
- Leave type (PAID/UNPAID)
- Only unpaid leaves affect salary
- Delete leaves

**Overtime:**
- Record overtime hours
- Hourly rate configuration
- Auto-calculate total amount
- Date tracking
- Delete overtime entries

### 4. Salary Management

**Preview Salary:**
- Select employee, month, and year
- View complete breakdown:
  - Base salary
  - Overtime additions
  - Advance deductions
  - PF deductions
  - Leave deductions (unpaid only)
  - Final payable salary
- Date-wise transaction details

**Generate Salary:**
- Confirm preview before generation
- One salary per employee per month
- Cannot regenerate (prevents duplicates)
- Stores historical snapshot

**Salary History:**
- View all past salaries
- Month-wise breakdown
- Export capabilities (future enhancement)

**Send SMS:**
- Send salary slip via SMS using Twilio
- Formatted salary details
- Delivery status tracking
- Resend capability

## Testing the Application

### Step-by-Step Testing Guide

1. **Login**
   - Go to http://localhost:3000
   - Login with admin/admin123

2. **Add Employees**
   - Navigate to Employees
   - Click "Add Employee"
   - Fill in details:
     - Name: John Doe
     - Mobile: 9876543210
     - Base Salary: 50000
     - PF %: 12
   - Submit

3. **Record Transactions**
   - Navigate to Transactions
   - Select the employee from dropdown
   
   **Add Advance:**
   - Click "Add advance"
   - Amount: 5000
   - Date: Current date
   - Submit
   
   **Add Leave:**
   - Switch to Leaves tab
   - Click "Add leave"
   - Date: Any date in current month
   - Type: UNPAID
   - Submit
   
   **Add Overtime:**
   - Switch to Overtime tab
   - Click "Add overtime"
   - Date: Any date in current month
   - Hours: 3.5
   - Rate per hour: 500
   - Submit (Total: 1750 will be auto-calculated)

4. **Generate Salary**
   - Navigate to Salaries
   - Select employee, current month, and year
   - Click "Preview"
   - Review the salary breakdown
   - Click "Generate Salary"
   - Salary will be created

5. **Send SMS** (Optional - requires Twilio)
   - In Salary History modal
   - Click "Send" button next to generated salary
   - SMS will be sent to employee's mobile number

6. **View Dashboard**
   - Navigate to Dashboard
   - View updated statistics
   - See salary comparisons in charts

## API Endpoints

All API endpoints are documented in `POSTMAN_API_COLLECTION.md`

Base URL: `http://localhost:8080/api`

### Authentication
- POST `/auth/login` - Login

### Employees
- GET `/employees` - Get all employees
- GET `/employees/{id}` - Get employee by ID
- POST `/employees` - Create employee
- PUT `/employees/{id}` - Update employee
- DELETE `/employees/{id}` - Deactivate employee

### Transactions
- POST `/advances` - Create advance
- GET `/advances/employee/{id}` - Get advances by employee
- DELETE `/advances/{id}` - Delete advance

- POST `/leaves` - Create leave
- GET `/leaves/employee/{id}` - Get leaves by employee
- DELETE `/leaves/{id}` - Delete leave

- POST `/overtimes` - Create overtime
- GET `/overtimes/employee/{id}` - Get overtimes by employee
- DELETE `/overtimes/{id}` - Delete overtime

### Salaries
- POST `/salaries/preview` - Preview salary calculation
- POST `/salaries/generate` - Generate and save salary
- GET `/salaries/employee/{id}` - Get salary history
- POST `/salaries/{id}/send-sms` - Send salary SMS

### Dashboard
- GET `/dashboard` - Get dashboard statistics

## Troubleshooting

### Backend Issues

**Issue: Port 8080 already in use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # Linux/Mac

# Kill the process or change port in application.properties
server.port=8081
```

**Issue: Database connection failed**
- Verify MySQL is running
- Check credentials in application.properties
- Ensure database exists

**Issue: Build failed**
```bash
# Clean and rebuild
.\gradlew.bat clean build --refresh-dependencies
```

### Frontend Issues

**Issue: npm install fails**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

**Issue: Cannot connect to backend**
- Verify backend is running on port 8080
- Check browser console for CORS errors
- Verify API_BASE_URL in src/config/api.js

**Issue: Page not loading**
```bash
# Clear React cache
rm -rf node_modules/.cache
npm start
```

## Production Deployment

### Backend

1. Build production JAR:
```bash
.\gradlew.bat bootJar
```

2. Run with production profile:
```bash
java -jar build/libs/emp-manage-1.0.0.jar --spring.profiles.active=prod
```

### Frontend

1. Build production bundle:
```bash
npm run build
```

2. Deploy `build/` directory to:
   - Static hosting (Netlify, Vercel)
   - Web server (Nginx, Apache)
   - CDN (CloudFront, CloudFlare)

## Environment Variables

### Backend
```properties
# MySQL
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/emp_manage_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Twilio (Optional)
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=+1234567890
```

### Frontend
```env
REACT_APP_API_URL=http://localhost:8080/api
```

## Security Notes

⚠️ **Important for Production:**

1. Change default admin credentials
2. Implement JWT authentication (currently uses dummy token)
3. Add role-based access control
4. Use environment variables for sensitive data
5. Enable HTTPS
6. Restrict CORS to specific domains
7. Use strong database passwords
8. Enable API rate limiting

## Performance Tips

1. **Backend:**
   - Enable database connection pooling
   - Add caching for frequently accessed data
   - Optimize database queries
   - Enable compression

2. **Frontend:**
   - Use React.lazy for code splitting
   - Implement pagination for large lists
   - Add debouncing for search
   - Optimize images

## Documentation

- `SYSTEM_DESIGN_DOCUMENT.md` - Complete system architecture
- `APPLICATION_FLOW_DOCUMENT.md` - Detailed business flows
- `POSTMAN_API_COLLECTION.md` - API documentation
- `README.md` - Backend documentation
- `frontend/README.md` - Frontend documentation

## Support

For issues or questions:
1. Check documentation
2. Review troubleshooting section
3. Check backend logs
4. Check browser console for frontend errors

## License

© 2025 Employee Salary Management System

---

**Happy Coding! 🚀**

