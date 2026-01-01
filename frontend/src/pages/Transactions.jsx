import React, { useState, useEffect } from 'react';
import { FiPlus, FiTrash2, FiDollarSign, FiCalendar, FiClock } from 'react-icons/fi';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import employeeService from '../services/employee.service';
import advanceService from '../services/advance.service';
import leaveService from '../services/leave.service';
import overtimeService from '../services/overtime.service';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Input from '../components/common/Input';
import Select from '../components/common/Select';
import Loading from '../components/common/Loading';
import Badge from '../components/common/Badge';

const Transactions = () => {
  const [activeTab, setActiveTab] = useState('advances');
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetchEmployees();
  }, []);

  useEffect(() => {
    if (selectedEmployee) {
      fetchTransactions();
    } else {
      setTransactions([]); // Clear transactions when no employee selected
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedEmployee, activeTab]);

  const fetchEmployees = async () => {
    try {
      const response = await employeeService.getAllEmployees();
      if (response.success) {
        const activeEmployees = response.data.filter(emp => emp.active);
        setEmployees(activeEmployees);
      }
    } catch (error) {
      toast.error('Failed to fetch employees');
    }
  };

  const fetchTransactions = async () => {
    if (!selectedEmployee) return;
    
    setLoading(true);
    setTransactions([]); // Clear previous data before fetching new
    try {
      let response;
      switch (activeTab) {
        case 'advances':
          response = await advanceService.getAdvancesByEmployee(selectedEmployee);
          break;
        case 'leaves':
          response = await leaveService.getLeavesByEmployee(selectedEmployee);
          break;
        case 'overtimes':
          response = await overtimeService.getOvertimesByEmployee(selectedEmployee);
          break;
        default:
          response = { success: true, data: [] };
      }
      if (response.success) {
        setTransactions(response.data || []); // Ensure it's always an array
      }
    } catch (error) {
      toast.error(`Failed to fetch ${activeTab}`);
      setTransactions([]); // Clear on error
    } finally {
      setLoading(false);
    }
  };

  // Validate that transaction data matches the current tab
  const isValidDataForTab = (transaction) => {
    if (!transaction) return false;
    
    switch (activeTab) {
      case 'advances':
        return 'advanceDate' in transaction && 'amount' in transaction;
      case 'leaves':
        return 'leaveDate' in transaction && 'leaveType' in transaction;
      case 'overtimes':
        return 'overtimeDate' in transaction && 'hours' in transaction;
      default:
        return false;
    }
  };

  // Filter transactions to only show valid data for current tab
  const getValidTransactions = () => {
    return transactions.filter(isValidDataForTab);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: '' });
    
    // Auto-calculate overtime total
    if (activeTab === 'overtimes' && (name === 'hours' || name === 'ratePerHour')) {
      const hours = name === 'hours' ? parseFloat(value) : parseFloat(formData.hours);
      const rate = name === 'ratePerHour' ? parseFloat(value) : parseFloat(formData.ratePerHour);
      if (hours && rate) {
        setFormData(prev => ({ ...prev, totalAmount: (hours * rate).toFixed(2) }));
      }
    }
  };

  const validate = () => {
    const newErrors = {};
    
    switch (activeTab) {
      case 'advances':
        if (!formData.amount) newErrors.amount = 'Amount is required';
        else if (formData.amount <= 0) newErrors.amount = 'Amount must be positive';
        if (!formData.advanceDate) newErrors.advanceDate = 'Date is required';
        break;
      case 'leaves':
        if (!formData.leaveDate) newErrors.leaveDate = 'Date is required';
        if (!formData.leaveType) newErrors.leaveType = 'Leave type is required';
        break;
      case 'overtimes':
        if (!formData.hours) newErrors.hours = 'Hours is required';
        else if (formData.hours <= 0) newErrors.hours = 'Hours must be positive';
        if (!formData.ratePerHour) newErrors.ratePerHour = 'Rate per hour is required';
        else if (formData.ratePerHour <= 0) newErrors.ratePerHour = 'Rate must be positive';
        if (!formData.overtimeDate) newErrors.overtimeDate = 'Date is required';
        break;
      default:
        break;
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
      const data = { ...formData, employeeId: parseInt(selectedEmployee) };
      
      switch (activeTab) {
        case 'advances':
          data.amount = parseFloat(data.amount);
          await advanceService.createAdvance(data);
          toast.success('Advance recorded successfully');
          break;
        case 'leaves':
          await leaveService.createLeave(data);
          toast.success('Leave recorded successfully');
          break;
        case 'overtimes':
          data.hours = parseFloat(data.hours);
          data.ratePerHour = parseFloat(data.ratePerHour);
          await overtimeService.createOvertime(data);
          toast.success('Overtime recorded successfully');
          break;
        default:
          break;
      }
      
      await fetchTransactions();
      handleCloseModal();
    } catch (error) {
      toast.error(error.message || 'Operation failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm(`Are you sure you want to delete this ${activeTab.slice(0, -1)}?`)) {
      try {
        switch (activeTab) {
          case 'advances':
            await advanceService.deleteAdvance(id);
            break;
          case 'leaves':
            await leaveService.deleteLeave(id);
            break;
          case 'overtimes':
            await overtimeService.deleteOvertime(id);
            break;
          default:
            break;
        }
        toast.success(`${activeTab.slice(0, -1)} deleted successfully`);
        await fetchTransactions();
      } catch (error) {
        toast.error('Delete operation failed');
      }
    }
  };

  const handleAddNew = () => {
    if (!selectedEmployee) {
      toast.warning('Please select an employee first');
      return;
    }
    
    setFormData({
      [activeTab === 'advances' ? 'advanceDate' : activeTab === 'leaves' ? 'leaveDate' : 'overtimeDate']: 
        new Date().toISOString().split('T')[0],
    });
    setErrors({});
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setFormData({});
    setErrors({});
  };

  const tabs = [
    { id: 'advances', label: 'Advances', icon: FiDollarSign },
    { id: 'leaves', label: 'Leaves', icon: FiCalendar },
    { id: 'overtimes', label: 'Overtime', icon: FiClock },
  ];

  const getColumns = () => {
    switch (activeTab) {
      case 'advances':
        return [
          { header: 'ID', accessor: 'id' },
          {
            header: 'Date',
            render: (row) => {
              try {
                return row.advanceDate ? format(new Date(row.advanceDate), 'dd MMM yyyy') : 'N/A';
              } catch (error) {
                return 'Invalid Date';
              }
            },
          },
          {
            header: 'Amount',
            render: (row) => row.amount ? `₹${row.amount.toLocaleString()}` : '₹0',
          },
          { header: 'Description', accessor: 'description' },
          {
            header: 'Actions',
            render: (row) => (
              <button
                onClick={() => handleDelete(row.id)}
                className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-all"
              >
                <FiTrash2 className="w-4 h-4" />
              </button>
            ),
          },
        ];
      case 'leaves':
        return [
          { header: 'ID', accessor: 'id' },
          {
            header: 'Date',
            render: (row) => {
              try {
                return row.leaveDate ? format(new Date(row.leaveDate), 'dd MMM yyyy') : 'N/A';
              } catch (error) {
                return 'Invalid Date';
              }
            },
          },
          {
            header: 'Type',
            render: (row) => (
              <Badge variant={row.leaveType === 'PAID' ? 'success' : 'warning'}>
                {row.leaveType || 'N/A'}
              </Badge>
            ),
          },
          { header: 'Description', accessor: 'description' },
          {
            header: 'Actions',
            render: (row) => (
              <button
                onClick={() => handleDelete(row.id)}
                className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-all"
              >
                <FiTrash2 className="w-4 h-4" />
              </button>
            ),
          },
        ];
      case 'overtimes':
        return [
          { header: 'ID', accessor: 'id' },
          {
            header: 'Date',
            render: (row) => {
              try {
                return row.overtimeDate ? format(new Date(row.overtimeDate), 'dd MMM yyyy') : 'N/A';
              } catch (error) {
                return 'Invalid Date';
              }
            },
          },
          { 
            header: 'Hours', 
            render: (row) => row.hours ? `${row.hours} hrs` : '0 hrs'
          },
          {
            header: 'Rate/Hour',
            render: (row) => row.ratePerHour ? `₹${row.ratePerHour.toLocaleString()}` : '₹0',
          },
          {
            header: 'Total',
            render: (row) => row.totalAmount ? `₹${row.totalAmount.toLocaleString()}` : '₹0',
          },
          {
            header: 'Actions',
            render: (row) => (
              <button
                onClick={() => handleDelete(row.id)}
                className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-all"
              >
                <FiTrash2 className="w-4 h-4" />
              </button>
            ),
          },
        ];
      default:
        return [];
    }
  };

  const renderForm = () => {
    switch (activeTab) {
      case 'advances':
        return (
          <>
            <Input
              label="Amount (₹)"
              name="amount"
              type="number"
              value={formData.amount || ''}
              onChange={handleInputChange}
              error={errors.amount}
              placeholder="Enter advance amount"
              step="0.01"
              required
            />
            <Input
              label="Date"
              name="advanceDate"
              type="date"
              value={formData.advanceDate || ''}
              onChange={handleInputChange}
              error={errors.advanceDate}
              required
            />
            <Input
              label="Description"
              name="description"
              value={formData.description || ''}
              onChange={handleInputChange}
              placeholder="Optional description"
            />
          </>
        );
      case 'leaves':
        return (
          <>
            <Input
              label="Date"
              name="leaveDate"
              type="date"
              value={formData.leaveDate || ''}
              onChange={handleInputChange}
              error={errors.leaveDate}
              required
            />
            <Select
              label="Leave Type"
              name="leaveType"
              value={formData.leaveType || ''}
              onChange={handleInputChange}
              error={errors.leaveType}
              options={[
                { value: 'PAID', label: 'Paid Leave' },
                { value: 'UNPAID', label: 'Unpaid Leave' },
              ]}
              required
            />
            <Input
              label="Description"
              name="description"
              value={formData.description || ''}
              onChange={handleInputChange}
              placeholder="Optional description"
            />
          </>
        );
      case 'overtimes':
        return (
          <>
            <Input
              label="Date"
              name="overtimeDate"
              type="date"
              value={formData.overtimeDate || ''}
              onChange={handleInputChange}
              error={errors.overtimeDate}
              required
            />
            <Input
              label="Hours"
              name="hours"
              type="number"
              value={formData.hours || ''}
              onChange={handleInputChange}
              error={errors.hours}
              placeholder="Enter overtime hours"
              step="0.1"
              required
            />
            <Input
              label="Rate per Hour (₹)"
              name="ratePerHour"
              type="number"
              value={formData.ratePerHour || ''}
              onChange={handleInputChange}
              error={errors.ratePerHour}
              placeholder="Enter rate per hour"
              step="0.01"
              required
            />
            {formData.totalAmount && (
              <div className="p-4 bg-primary-50 rounded-lg">
                <p className="text-sm text-gray-600">Total Amount:</p>
                <p className="text-2xl font-bold text-primary-600">
                  ₹{parseFloat(formData.totalAmount).toLocaleString()}
                </p>
              </div>
            )}
          </>
        );
      default:
        return null;
    }
  };

  return (
    <div className="space-y-6 fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Transactions</h1>
          <p className="text-gray-600 mt-1">Record advances, leaves, and overtime</p>
        </div>
        <Button onClick={handleAddNew} variant="primary">
          <div className="flex items-center space-x-2">
            <FiPlus className="w-5 h-5" />
            <span>Add {activeTab.slice(0, -1)}</span>
          </div>
        </Button>
      </div>

      {/* Employee Selection */}
      <Card>
        <Select
          label="Select Employee"
          name="employee"
          value={selectedEmployee}
          onChange={(e) => setSelectedEmployee(e.target.value)}
          options={employees.map(emp => ({
            value: emp.id.toString(),
            label: `${emp.name} (${emp.mobile})`,
          }))}
          placeholder="Choose an employee"
        />
      </Card>

      {/* Tabs */}
      <div className="flex space-x-2 border-b border-gray-200">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center space-x-2 px-6 py-3 font-medium transition-all ${
                activeTab === tab.id
                  ? 'text-primary-600 border-b-2 border-primary-600'
                  : 'text-gray-600 hover:text-gray-800'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Transactions Table */}
      {selectedEmployee ? (
        <Card>
          {loading ? (
            <Loading text={`Loading ${activeTab}...`} />
          ) : (
            <Table 
              key={`${activeTab}-${selectedEmployee}`} 
              columns={getColumns()} 
              data={getValidTransactions()} 
            />
          )}
        </Card>
      ) : (
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500">Please select an employee to view transactions</p>
          </div>
        </Card>
      )}

      {/* Add Transaction Modal */}
      <Modal
        isOpen={showModal}
        onClose={handleCloseModal}
        title={`Add ${activeTab.slice(0, -1)}`}
        size="md"
      >
        <form onSubmit={handleSubmit}>
          {renderForm()}
          <div className="flex justify-end space-x-3 mt-6">
            <Button type="button" variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" loading={loading}>
              Record {activeTab.slice(0, -1)}
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Transactions;

