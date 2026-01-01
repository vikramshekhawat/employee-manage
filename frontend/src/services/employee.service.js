import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const employeeService = {
  getAllEmployees: async () => {
    return await apiClient.get(API_ENDPOINTS.EMPLOYEES);
  },

  getEmployeeById: async (id) => {
    return await apiClient.get(API_ENDPOINTS.EMPLOYEE_BY_ID(id));
  },

  createEmployee: async (employeeData) => {
    return await apiClient.post(API_ENDPOINTS.EMPLOYEES, employeeData);
  },

  updateEmployee: async (id, employeeData) => {
    return await apiClient.put(API_ENDPOINTS.EMPLOYEE_BY_ID(id), employeeData);
  },

  deleteEmployee: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.EMPLOYEE_BY_ID(id));
  },
};

export default employeeService;

