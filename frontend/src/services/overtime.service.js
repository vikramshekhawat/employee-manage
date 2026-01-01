import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const overtimeService = {
  createOvertime: async (overtimeData) => {
    return await apiClient.post(API_ENDPOINTS.OVERTIMES, overtimeData);
  },

  getOvertimesByEmployee: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.OVERTIMES_BY_EMPLOYEE(employeeId));
  },

  deleteOvertime: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.OVERTIME_BY_ID(id));
  },
};

export default overtimeService;

