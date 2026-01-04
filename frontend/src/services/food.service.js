import apiClient from './api.service';
import { API_ENDPOINTS } from '../config/api';

const foodService = {
  createOrUpdateFoodExpense: async (foodData) => {
    return await apiClient.post(API_ENDPOINTS.FOOD_EXPENSES, foodData);
  },

  getFoodExpenseByEmployee: async (employeeId) => {
    return await apiClient.get(API_ENDPOINTS.FOOD_EXPENSES_BY_EMPLOYEE(employeeId));
  },

  getFoodExpenseByEmployeeAndMonth: async (employeeId, month, year) => {
    return await apiClient.get(API_ENDPOINTS.FOOD_EXPENSE_BY_EMPLOYEE_MONTH(employeeId, month, year));
  },

  deleteFoodExpense: async (id) => {
    return await apiClient.delete(API_ENDPOINTS.FOOD_EXPENSE_BY_ID(id));
  },
};

export default foodService;

