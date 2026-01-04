import React, { useState, useEffect } from 'react';
import { FiPlus, FiEdit2, FiUserX } from 'react-icons/fi';
import { toast } from 'react-toastify';
import employeeService from '../services/employee.service';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Input from '../components/common/Input';
import Loading from '../components/common/Loading';
import Badge from '../components/common/Badge';

const Employees = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [currentEmployee, setCurrentEmployee] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    mobile: '',
    baseSalary: '',
    pfAmount: '',
  });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const response = await employeeService.getAllEmployees();
      if (response.success) {
        setEmployees(response.data);
      }
    } catch (error) {
      toast.error('Failed to fetch employees');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: '' });
  };

  const validate = () => {
    const newErrors = {};
    
    // Name validation
    if (!formData.name || !formData.name.trim()) {
      newErrors.name = 'Name is required';
    }
    
    // Mobile validation - must be exactly 10 digits
    const mobileTrimmed = formData.mobile ? formData.mobile.trim() : '';
    if (!mobileTrimmed) {
      newErrors.mobile = 'Mobile number is required';
    } else if (!/^\d{10}$/.test(mobileTrimmed)) {
      newErrors.mobile = 'Mobile number must be exactly 10 digits';
    }
    
    // Base salary validation
    const baseSalaryValue = parseFloat(formData.baseSalary);
    if (!formData.baseSalary || formData.baseSalary.trim() === '') {
      newErrors.baseSalary = 'Base salary is required';
    } else if (isNaN(baseSalaryValue) || baseSalaryValue <= 0) {
      newErrors.baseSalary = 'Base salary must be a valid positive number';
    }
    
    // PF amount validation
    const pfAmountValue = parseFloat(formData.pfAmount);
    if (!formData.pfAmount || formData.pfAmount.trim() === '') {
      newErrors.pfAmount = 'PF amount is required';
    } else if (isNaN(pfAmountValue) || pfAmountValue < 0) {
      newErrors.pfAmount = 'PF amount must be a valid number (0 or greater)';
    }
    
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = validate();
    
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      const baseSalaryValue = parseFloat(formData.baseSalary);
      const pfAmountValue = parseFloat(formData.pfAmount);
      
      // Validate parsed values
      if (isNaN(baseSalaryValue) || baseSalaryValue <= 0) {
        setErrors({ baseSalary: 'Base salary must be a valid positive number' });
        setLoading(false);
        return;
      }
      
      if (isNaN(pfAmountValue) || pfAmountValue < 0) {
        setErrors({ pfAmount: 'PF amount must be a valid number (0 or greater)' });
        setLoading(false);
        return;
      }
      
      const employeeData = {
        name: formData.name.trim(),
        mobile: formData.mobile.trim(),
        baseSalary: baseSalaryValue,
        pfAmount: pfAmountValue,
      };

      if (editMode && currentEmployee) {
        await employeeService.updateEmployee(currentEmployee.id, employeeData);
        toast.success('Employee updated successfully');
      } else {
        await employeeService.createEmployee(employeeData);
        toast.success('Employee created successfully');
      }
      
      await fetchEmployees();
      handleCloseModal();
    } catch (error) {
      console.error('Employee operation error:', error);
      
      // Handle validation errors from backend
      // The error structure from API is: { success: false, message: "...", data: { field: "error" } }
      const errorData = error.data || error;
      
      if (errorData.data && typeof errorData.data === 'object') {
        // Validation errors are in errorData.data
        setErrors(errorData.data);
        // Show first error message as toast
        const firstError = Object.values(errorData.data)[0];
        if (firstError) {
          toast.error(firstError);
        } else if (errorData.message) {
          toast.error(errorData.message);
        }
      } else if (errorData && typeof errorData === 'object' && !errorData.success) {
        // Direct error object with field errors
        setErrors(errorData);
        if (errorData.message) {
          toast.error(errorData.message);
        }
      } else if (error.message) {
        toast.error(error.message);
        setErrors({});
      } else {
        toast.error('Operation failed. Please check the form for errors.');
        setErrors({});
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (employee) => {
    setEditMode(true);
    setCurrentEmployee(employee);
    setFormData({
      name: employee.name,
      mobile: employee.mobile,
      baseSalary: (employee.baseSalary || 0).toString(),
      pfAmount: (employee.pfAmount || 0).toString(),
    });
    setShowModal(true);
  };

  const handleDelete = async (employee) => {
    if (window.confirm(`Are you sure you want to deactivate ${employee.name}?`)) {
      try {
        await employeeService.deleteEmployee(employee.id);
        toast.success('Employee deactivated successfully');
        await fetchEmployees();
      } catch (error) {
        toast.error('Failed to deactivate employee');
      }
    }
  };

  const handleAddNew = () => {
    setEditMode(false);
    setCurrentEmployee(null);
    setFormData({
      name: '',
      mobile: '',
      baseSalary: '',
      pfAmount: '',
    });
    setErrors({});
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditMode(false);
    setCurrentEmployee(null);
    setFormData({
      name: '',
      mobile: '',
      baseSalary: '',
      pfAmount: '',
    });
    setErrors({});
  };

  const columns = [
    { header: 'ID', accessor: 'id' },
    { header: 'Name', accessor: 'name' },
    { header: 'Mobile', accessor: 'mobile' },
    {
      header: 'Base Salary',
      render: (row) => `₹${(row.baseSalary || 0).toLocaleString()}`,
    },
    {
      header: 'PF Amount',
      render: (row) => `₹${(row.pfAmount || 0).toLocaleString()}`,
    },
    {
      header: 'Status',
      render: (row) => (
        <Badge variant={row.active ? 'success' : 'danger'}>
          {row.active ? 'Active' : 'Inactive'}
        </Badge>
      ),
    },
    {
      header: 'Actions',
      render: (row) => (
        <div className="flex space-x-2">
          <button
            onClick={() => handleEdit(row)}
            className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
            title="Edit"
          >
            <FiEdit2 className="w-4 h-4" />
          </button>
          {row.active && (
            <button
              onClick={() => handleDelete(row)}
              className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-all"
              title="Deactivate"
            >
              <FiUserX className="w-4 h-4" />
            </button>
          )}
        </div>
      ),
    },
  ];

  if (loading && employees.length === 0) {
    return <Loading fullScreen text="Loading Employees..." />;
  }

  return (
    <div className="space-y-6 fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Employee Management</h1>
          <p className="text-gray-600 mt-1">Manage employee records and information</p>
        </div>
        <Button onClick={handleAddNew} variant="primary">
          <div className="flex items-center space-x-2">
            <FiPlus className="w-5 h-5" />
            <span>Add Employee</span>
          </div>
        </Button>
      </div>

      <Card>
        <Table columns={columns} data={employees} />
      </Card>

      {/* Add/Edit Modal */}
      <Modal
        isOpen={showModal}
        onClose={handleCloseModal}
        title={editMode ? 'Edit Employee' : 'Add New Employee'}
        size="md"
      >
        <form onSubmit={handleSubmit}>
          <Input
            label="Full Name"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            error={errors.name}
            placeholder="Enter employee name"
            required
          />

          <Input
            label="Mobile Number"
            name="mobile"
            value={formData.mobile}
            onChange={handleInputChange}
            error={errors.mobile}
            placeholder="Enter 10-digit mobile number"
            maxLength={10}
            required
          />

          <Input
            label="Base Salary (₹)"
            name="baseSalary"
            type="number"
            value={formData.baseSalary}
            onChange={handleInputChange}
            error={errors.baseSalary}
            placeholder="Enter base salary"
            step="0.01"
            required
          />

          <Input
            label="PF Amount (₹)"
            name="pfAmount"
            type="number"
            value={formData.pfAmount}
            onChange={handleInputChange}
            error={errors.pfAmount}
            placeholder="Enter PF amount"
            step="0.01"
            min="0"
            required
          />

          <div className="flex justify-end space-x-3 mt-6">
            <Button type="button" variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" loading={loading}>
              {editMode ? 'Update Employee' : 'Create Employee'}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Employees;

