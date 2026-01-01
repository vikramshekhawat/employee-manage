import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const authService = {
  login: async (username, password) => {
    const response = await apiClient.post(API_ENDPOINTS.LOGIN, {
      username,
      password,
    });
    
    if (response.success && response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('username', response.data.username);
    }
    
    return response;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },

  getUsername: () => {
    return localStorage.getItem('username');
  },
};

export default authService;

