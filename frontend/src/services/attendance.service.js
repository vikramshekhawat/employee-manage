import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const attendanceService = {
  createOrUpdateAttendance: async (attendanceData) => {
    return await apiClient.post(API_ENDPOINTS.ATTENDANCES, attendanceData);
  },

  getAttendanceByEmployee: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.ATTENDANCES_BY_EMPLOYEE(employeeId));
  },

  getAttendanceByEmployeeAndMonth: async (employeeId, month, year) => {
    return await apiClient.get(API_ENDPOINTS.ATTENDANCE_BY_EMPLOYEE_MONTH(employeeId, month, year));
  },

  deleteAttendance: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.ATTENDANCE_BY_ID(id));
  },
};

export default attendanceService;

