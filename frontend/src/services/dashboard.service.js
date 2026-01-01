import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const dashboardService = {
  getDashboardData: async () => {
    return await apiClient.get(API_ENDPOINTS.DASHBOARD);
  },
};

export default dashboardService;

