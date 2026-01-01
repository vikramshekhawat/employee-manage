import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const leaveService = {
  createLeave: async (leaveData) => {
    return await apiClient.post(API_ENDPOINTS.LEAVES, leaveData);
  },

  getLeavesByEmployee: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.LEAVES_BY_EMPLOYEE(employeeId));
  },

  deleteLeave: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.LEAVE_BY_ID(id));
  },
};

export default leaveService;

