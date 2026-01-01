// API Configuration
export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const API_ENDPOINTS = {
  // Auth
  LOGIN: '/auth/login',
  
  // Employees
  EMPLOYEES: '/employees',
  EMPLOYEE_BY_ID: (id) => `/employees/${id}`,
  
  // Advances
  ADVANCES: '/advances',
  ADVANCE_BY_ID: (id) => `/advances/${id}`,
  ADVANCES_BY_EMPLOYEE: (employeeId) => `/advances/employee/${employeeId}`,
  
  // Leaves
  LEAVES: '/leaves',
  LEAVE_BY_ID: (id) => `/leaves/${id}`,
  LEAVES_BY_EMPLOYEE: (employeeId) => `/leaves/employee/${employeeId}`,
  
  // Overtimes
  OVERTIMES: '/overtimes',
  OVERTIME_BY_ID: (id) => `/overtimes/${id}`,
  OVERTIMES_BY_EMPLOYEE: (employeeId) => `/overtimes/employee/${employeeId}`,
  
  // Salaries
  SALARY_PREVIEW: '/salaries/preview',
  SALARY_GENERATE: '/salaries/generate',
  SALARY_HISTORY: (employeeId) => `/salaries/employee/${employeeId}`,
  SALARY_SEND_SMS: (salaryId) => `/salaries/${salaryId}/send-sms`,
  
  // Dashboard
  DASHBOARD: '/dashboard',
};

