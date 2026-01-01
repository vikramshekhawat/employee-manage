# Employee Salary Management System - Frontend

A modern, responsive React.js frontend for the Employee Salary Management System with an attractive UI built using Tailwind CSS.

## Features

- ✨ **Modern UI**: Beautiful, responsive design with Tailwind CSS
- 🔐 **Authentication**: Secure login system
- 📊 **Interactive Dashboard**: Real-time statistics with Chart.js
- 👥 **Employee Management**: Complete CRUD operations
- 💰 **Transaction Management**: Record advances, leaves, and overtime
- 📈 **Salary Management**: Preview, generate, and send salary slips via SMS
- 🎨 **Attractive Components**: Reusable, styled components
- 📱 **Responsive**: Mobile-friendly design
- 🔔 **Toast Notifications**: User-friendly feedback

## Technology Stack

- **React 18**: Modern React with hooks
- **React Router 6**: Client-side routing
- **Axios**: HTTP client for API calls
- **Tailwind CSS**: Utility-first CSS framework
- **Chart.js**: Interactive charts
- **React Icons**: Icon library
- **React Toastify**: Toast notifications
- **date-fns**: Date formatting

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── auth/
│   │   │   ├── Login.jsx
│   │   │   └── PrivateRoute.jsx
│   │   ├── common/
│   │   │   ├── Badge.jsx
│   │   │   ├── Button.jsx
│   │   │   ├── Card.jsx
│   │   │   ├── Input.jsx
│   │   │   ├── Loading.jsx
│   │   │   ├── Modal.jsx
│   │   │   ├── Select.jsx
│   │   │   └── Table.jsx
│   │   └── layout/
│   │       └── Layout.jsx
│   ├── config/
│   │   └── api.js
│   ├── pages/
│   │   ├── Dashboard.jsx
│   │   ├── Employees.jsx
│   │   ├── Transactions.jsx
│   │   └── Salaries.jsx
│   ├── services/
│   │   ├── api.service.js
│   │   ├── auth.service.js
│   │   ├── dashboard.service.js
│   │   ├── employee.service.js
│   │   ├── advance.service.js
│   │   ├── leave.service.js
│   │   ├── overtime.service.js
│   │   └── salary.service.js
│   ├── App.js
│   ├── index.js
│   └── index.css
├── package.json
├── tailwind.config.js
└── postcss.config.js
```

## Setup Instructions

### Prerequisites

- Node.js 16+ and npm
- Backend server running on http://localhost:8080

### Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Configure API URL (optional):

Create a `.env` file in the frontend directory:
```env
REACT_APP_API_URL=http://localhost:8080/api
```

4. Start the development server:
```bash
npm start
```

The application will open at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

This creates an optimized production build in the `build/` directory.

## Features Guide

### 1. Authentication

- **Login Page**: Modern login interface with gradient background
- **Default Credentials**: admin / admin123
- **Protected Routes**: All pages except login require authentication
- **Session Management**: Token-based authentication

### 2. Dashboard

- **Statistics Cards**: 
  - Total Employees
  - Active Employees
  - Current Month Salary
  - Pending Salary Generations
- **Charts**:
  - Salary comparison (Bar chart)
  - Employee status (Doughnut chart)
- **Quick Actions**: Direct links to main features

### 3. Employee Management

- **List View**: Sortable table with all employees
- **Add Employee**: Modal form with validation
- **Edit Employee**: Update employee details
- **Deactivate**: Soft delete (keeps historical data)
- **Validation**: Real-time form validation
- **Status Badges**: Active/Inactive indicators

### 4. Transaction Management

- **Tabs**: Switch between Advances, Leaves, and Overtime
- **Employee Selection**: Dropdown to select employee
- **Add Transaction**: Modal forms for each transaction type
- **Date Tracking**: Calendar input for transaction dates
- **Auto-calculation**: Overtime amount calculated automatically
- **Delete**: Remove transactions before salary generation

#### Transaction Types:

**Advances**
- Amount
- Date
- Description (optional)

**Leaves**
- Date
- Type (Paid/Unpaid)
- Description (optional)

**Overtime**
- Date
- Hours
- Rate per hour
- Auto-calculated total

### 5. Salary Management

#### Preview Salary
- Select employee, month, and year
- View detailed breakdown:
  - Base salary
  - Overtime additions
  - Advance deductions
  - PF deductions
  - Leave deductions
  - Final salary
- Date-wise transaction details

#### Generate Salary
- Confirm preview before generation
- One-time generation per employee per month
- Historical data preservation

#### Salary History
- View all past salaries for an employee
- Complete breakdown for each month
- SMS status tracking

#### Send SMS
- Send salary slip via SMS
- Twilio integration
- Delivery status tracking

## Component Documentation

### Common Components

#### Button
```jsx
<Button 
  variant="primary|secondary|success|danger|outline"
  size="sm|md|lg"
  fullWidth={boolean}
  loading={boolean}
  onClick={function}
>
  Button Text
</Button>
```

#### Card
```jsx
<Card 
  title="Card Title"
  subtitle="Optional subtitle"
  action={<Button>Action</Button>}
>
  Card content
</Card>
```

#### Input
```jsx
<Input
  label="Label"
  name="fieldName"
  type="text|number|date|password"
  value={value}
  onChange={handleChange}
  error={errorMessage}
  required={boolean}
/>
```

#### Select
```jsx
<Select
  label="Label"
  name="fieldName"
  value={value}
  onChange={handleChange}
  options={[{value: '1', label: 'Option 1'}]}
  error={errorMessage}
/>
```

#### Modal
```jsx
<Modal
  isOpen={boolean}
  onClose={function}
  title="Modal Title"
  size="sm|md|lg|xl"
>
  Modal content
</Modal>
```

#### Table
```jsx
<Table
  columns={[
    { header: 'Name', accessor: 'name' },
    { header: 'Actions', render: (row) => <Button>Edit</Button> }
  ]}
  data={dataArray}
  onRowClick={function}
/>
```

## API Integration

All API calls are centralized in service files:

- **auth.service.js**: Authentication
- **dashboard.service.js**: Dashboard stats
- **employee.service.js**: Employee CRUD
- **advance.service.js**: Advance management
- **leave.service.js**: Leave management
- **overtime.service.js**: Overtime management
- **salary.service.js**: Salary operations

### Example API Call

```javascript
import employeeService from '../services/employee.service';

const fetchEmployees = async () => {
  try {
    const response = await employeeService.getAllEmployees();
    if (response.success) {
      setEmployees(response.data);
    }
  } catch (error) {
    toast.error('Failed to fetch employees');
  }
};
```

## Styling Guide

### Tailwind CSS Classes

The application uses Tailwind CSS for styling. Common patterns:

- **Colors**: `bg-primary-600`, `text-primary-600`
- **Spacing**: `p-4`, `m-4`, `space-x-2`
- **Layout**: `flex`, `grid`, `grid-cols-3`
- **Responsive**: `md:grid-cols-2`, `lg:grid-cols-4`
- **Hover**: `hover:bg-gray-100`
- **Transitions**: `transition-all`, `duration-200`

### Custom CSS

Custom animations and utilities in `index.css`:

- **Fade In**: `.fade-in` class
- **Card Hover**: `.card-hover` class
- **Loading Spinner**: `.animate-spin`

## Best Practices

1. **Component Organization**: Reusable components in `components/common/`
2. **Service Layer**: All API calls in dedicated service files
3. **Error Handling**: Try-catch with toast notifications
4. **Loading States**: Loading indicators for async operations
5. **Form Validation**: Client-side and server-side validation
6. **Responsive Design**: Mobile-first approach
7. **Code Reusability**: DRY principle
8. **State Management**: React hooks (useState, useEffect)

## Troubleshooting

### Common Issues

**1. CORS Error**
- Ensure backend CORS is configured to allow `http://localhost:3000`

**2. API Connection Failed**
- Check backend server is running on port 8080
- Verify API_BASE_URL in `src/config/api.js`

**3. Build Errors**
- Clear node_modules: `rm -rf node_modules package-lock.json`
- Reinstall: `npm install`

**4. Styling Not Loading**
- Ensure Tailwind CSS is properly configured
- Check `tailwind.config.js` content paths

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Performance Optimization

- **Code Splitting**: React.lazy for route-based splitting
- **Memoization**: React.memo for expensive components
- **Debouncing**: For search/filter operations
- **Lazy Loading**: Images and heavy components
- **Bundle Size**: Optimized production build

## Contributing

1. Follow the existing code structure
2. Use meaningful component and variable names
3. Add comments for complex logic
4. Test thoroughly before committing
5. Ensure responsive design

## License

© 2025 Employee Salary Management System

## Support

For issues or questions, please contact the development team.

