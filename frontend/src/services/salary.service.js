import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const salaryService = {
  previewSalary: async (employeeId, month, year) => {
    return await apiClient.post(API_ENDPOINTS.SALARY_PREVIEW, {
      employeeId,
      month,
      year,
    });
  },

  generateSalary: async (employeeId, month, year) => {
    return await apiClient.post(API_ENDPOINTS.SALARY_GENERATE, {
      employeeId,
      month,
      year,
    });
  },

  getSalaryHistory: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.SALARY_HISTORY(employeeId));
  },

  sendSalarySms: async (salaryId) => {
    return await apiClient.post(API_ENDPOINTS.SALARY_SEND_SMS(salaryId));
  },
};

export default salaryService;

