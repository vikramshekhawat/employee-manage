import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const advanceService = {
  createAdvance: async (advanceData) => {
    return await apiClient.post(API_ENDPOINTS.ADVANCES, advanceData);
  },

  getAdvancesByEmployee: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.ADVANCES_BY_EMPLOYEE(employeeId));
  },

  deleteAdvance: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.ADVANCE_BY_ID(id));
  },
};

export default advanceService;

