import React, { useState, useEffect } from 'react';
import { FiEye, FiCheck, FiSend, FiClock } from 'react-icons/fi';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import employeeService from '../services/employee.service';
import salaryService from '../services/salary.service';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Select from '../components/common/Select';
import Loading from '../components/common/Loading';
import Badge from '../components/common/Badge';

const Salaries = () => {
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState('');
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [salaryPreview, setSalaryPreview] = useState(null);
  const [salaryHistory, setSalaryHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showPreviewModal, setShowPreviewModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  useEffect(() => {
    fetchEmployees();
  }, []);

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

  const handlePreviewSalary = async () => {
    if (!selectedEmployee) {
      toast.warning('Please select an employee');
      return;
    }

    setLoading(true);
    try {
      const response = await salaryService.previewSalary(
        parseInt(selectedEmployee),
        month,
        year
      );
      if (response.success) {
        setSalaryPreview(response.data);
        setShowPreviewModal(true);
      }
    } catch (error) {
      toast.error(error.message || 'Failed to preview salary');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateSalary = async () => {
    if (!selectedEmployee) {
      toast.warning('Please select an employee');
      return;
    }

    if (window.confirm('Are you sure you want to generate salary? This action cannot be undone.')) {
      setLoading(true);
      try {
        const response = await salaryService.generateSalary(
          parseInt(selectedEmployee),
          month,
          year
        );
        if (response.success) {
          toast.success('Salary generated successfully!');
          setSalaryPreview(null);
          setShowPreviewModal(false);
        }
      } catch (error) {
        toast.error(error.message || 'Failed to generate salary');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleViewHistory = async (employeeId) => {
    setLoading(true);
    try {
      const response = await salaryService.getSalaryHistory(employeeId);
      if (response.success) {
        setSalaryHistory(response.data);
        setShowHistoryModal(true);
      }
    } catch (error) {
      toast.error('Failed to fetch salary history');
    } finally {
      setLoading(false);
    }
  };

  const handleSendSMS = async (salaryId) => {
    if (window.confirm('Do you want to send salary slip via SMS?')) {
      setLoading(true);
      try {
        const response = await salaryService.sendSalarySms(salaryId);
        if (response.success) {
          toast.success('SMS sent successfully!');
          // Refresh history if modal is open
          if (showHistoryModal && selectedEmployee) {
            handleViewHistory(parseInt(selectedEmployee));
          }
        }
      } catch (error) {
        toast.error(error.message || 'Failed to send SMS');
      } finally {
        setLoading(false);
      }
    }
  };

  const months = [
    { value: 1, label: 'January' },
    { value: 2, label: 'February' },
    { value: 3, label: 'March' },
    { value: 4, label: 'April' },
    { value: 5, label: 'May' },
    { value: 6, label: 'June' },
    { value: 7, label: 'July' },
    { value: 8, label: 'August' },
    { value: 9, label: 'September' },
    { value: 10, label: 'October' },
    { value: 11, label: 'November' },
    { value: 12, label: 'December' },
  ];

  const years = Array.from({ length: 5 }, (_, i) => {
    const yr = new Date().getFullYear() - 2 + i;
    return { value: yr, label: yr.toString() };
  });

  const historyColumns = [
    {
      header: 'Month/Year',
      render: (row) => `${months[row.month - 1]?.label} ${row.year}`,
    },
    {
      header: 'Base Salary',
      render: (row) => `₹${row.baseSalary.toLocaleString()}`,
    },
    {
      header: 'Overtime',
      render: (row) => `₹${row.totalOvertime.toLocaleString()}`,
    },
    {
      header: 'Advances',
      render: (row) => `-₹${row.totalAdvances.toLocaleString()}`,
    },
    {
      header: 'PF',
      render: (row) => `-₹${row.pfDeduction.toLocaleString()}`,
    },
    {
      header: 'Leaves',
      render: (row) => `-₹${row.totalLeaves.toLocaleString()}`,
    },
    {
      header: 'Final Salary',
      render: (row) => (
        <span className="font-bold text-green-600">
          ₹{row.finalSalary.toLocaleString()}
        </span>
      ),
    },
    {
      header: 'SMS',
      render: (row) => (
        <div className="flex items-center space-x-2">
          {row.smsSent ? (
            <Badge variant="success">Sent</Badge>
          ) : (
            <Button
              size="sm"
              variant="primary"
              onClick={() => handleSendSMS(row.id)}
            >
              <div className="flex items-center space-x-1">
                <FiSend className="w-3 h-3" />
                <span>Send</span>
              </div>
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6 fade-in">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Salary Management</h1>
          <p className="text-gray-600 mt-1">Preview, generate, and manage employee salaries</p>
        </div>
      </div>

      {/* Salary Calculation Form */}
      <Card title="Generate Salary">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Select
            label="Employee"
            name="employee"
            value={selectedEmployee}
            onChange={(e) => setSelectedEmployee(e.target.value)}
            options={employees.map(emp => ({
              value: emp.id.toString(),
              label: `${emp.name} (${emp.mobile})`,
            }))}
            placeholder="Select employee"
          />
          
          <Select
            label="Month"
            name="month"
            value={month}
            onChange={(e) => setMonth(parseInt(e.target.value))}
            options={months}
          />
          
          <Select
            label="Year"
            name="year"
            value={year}
            onChange={(e) => setYear(parseInt(e.target.value))}
            options={years}
          />
          
          <div className="flex items-end space-x-2">
            <Button
              onClick={handlePreviewSalary}
              variant="primary"
              loading={loading}
              fullWidth
            >
              <div className="flex items-center justify-center space-x-2">
                <FiEye className="w-4 h-4" />
                <span>Preview</span>
              </div>
            </Button>
          </div>
        </div>
      </Card>

      {/* Employee List with History */}
      <Card title="Employees">
        <Table
          columns={[
            { header: 'ID', accessor: 'id' },
            { header: 'Name', accessor: 'name' },
            { header: 'Mobile', accessor: 'mobile' },
            {
              header: 'Base Salary',
              render: (row) => `₹${row.baseSalary.toLocaleString()}`,
            },
            {
              header: 'Actions',
              render: (row) => (
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => handleViewHistory(row.id)}
                >
                  <div className="flex items-center space-x-2">
                    <FiClock className="w-4 h-4" />
                    <span>History</span>
                  </div>
                </Button>
              ),
            },
          ]}
          data={employees}
        />
      </Card>

      {/* Salary Preview Modal */}
      <Modal
        isOpen={showPreviewModal}
        onClose={() => setShowPreviewModal(false)}
        title="Salary Preview"
        size="lg"
      >
        {salaryPreview && (
          <div className="space-y-6">
            {/* Employee Info */}
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="font-semibold text-lg mb-2">Employee Information</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Name</p>
                  <p className="font-medium">{salaryPreview.employeeName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Mobile</p>
                  <p className="font-medium">{salaryPreview.employeeMobile}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Period</p>
                  <p className="font-medium">
                    {months[salaryPreview.month - 1]?.label} {salaryPreview.year}
                  </p>
                </div>
              </div>
            </div>

            {/* Salary Breakdown */}
            <div>
              <h3 className="font-semibold text-lg mb-4">Salary Breakdown</h3>
              <div className="space-y-3">
                <div className="flex justify-between items-center p-3 bg-green-50 rounded-lg">
                  <span className="font-medium">Base Salary</span>
                  <span className="font-bold text-green-600">
                    ₹{salaryPreview.baseSalary.toLocaleString()}
                  </span>
                </div>
                
                <div className="flex justify-between items-center p-3 bg-blue-50 rounded-lg">
                  <span className="font-medium">Overtime (+)</span>
                  <span className="font-bold text-blue-600">
                    +₹{salaryPreview.totalOvertime.toLocaleString()}
                  </span>
                </div>
                
                <div className="flex justify-between items-center p-3 bg-red-50 rounded-lg">
                  <span className="font-medium">Advances (-)</span>
                  <span className="font-bold text-red-600">
                    -₹{salaryPreview.totalAdvances.toLocaleString()}
                  </span>
                </div>
                
                <div className="flex justify-between items-center p-3 bg-red-50 rounded-lg">
                  <span className="font-medium">PF Deduction (-)</span>
                  <span className="font-bold text-red-600">
                    -₹{salaryPreview.pfDeduction.toLocaleString()}
                  </span>
                </div>
                
                <div className="flex justify-between items-center p-3 bg-red-50 rounded-lg">
                  <span className="font-medium">Leave Deduction (-) [{salaryPreview.unpaidLeaveDays} days]</span>
                  <span className="font-bold text-red-600">
                    -₹{salaryPreview.leaveDeduction.toLocaleString()}
                  </span>
                </div>
                
                <div className="flex justify-between items-center p-4 bg-primary-600 text-white rounded-lg">
                  <span className="font-bold text-lg">Final Salary</span>
                  <span className="font-bold text-2xl">
                    ₹{salaryPreview.finalSalary.toLocaleString()}
                  </span>
                </div>
              </div>
            </div>

            {/* Date-wise Breakdown */}
            {salaryPreview.dateWiseBreakdown && salaryPreview.dateWiseBreakdown.length > 0 && (
              <div>
                <h3 className="font-semibold text-lg mb-4">Date-wise Details</h3>
                <div className="max-h-64 overflow-y-auto border rounded-lg">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                        <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                        <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                        <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Description</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {salaryPreview.dateWiseBreakdown.map((item, index) => (
                        <tr key={index}>
                          <td className="px-4 py-2 whitespace-nowrap text-sm">
                            {format(new Date(item.date), 'dd MMM')}
                          </td>
                          <td className="px-4 py-2 whitespace-nowrap text-sm">
                            <Badge
                              variant={
                                item.type === 'OVERTIME' ? 'success' :
                                item.type === 'ADVANCE' ? 'warning' : 'danger'
                              }
                            >
                              {item.type}
                            </Badge>
                          </td>
                          <td className={`px-4 py-2 whitespace-nowrap text-sm font-semibold ${
                            item.amount >= 0 ? 'text-green-600' : 'text-red-600'
                          }`}>
                            {item.amount >= 0 ? '+' : ''}₹{Math.abs(item.amount).toLocaleString()}
                          </td>
                          <td className="px-4 py-2 text-sm text-gray-600">
                            {item.description}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="flex justify-end space-x-3 pt-4 border-t">
              <Button
                variant="secondary"
                onClick={() => setShowPreviewModal(false)}
              >
                Close
              </Button>
              <Button
                variant="success"
                onClick={handleGenerateSalary}
                loading={loading}
              >
                <div className="flex items-center space-x-2">
                  <FiCheck className="w-4 h-4" />
                  <span>Generate Salary</span>
                </div>
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Salary History Modal */}
      <Modal
        isOpen={showHistoryModal}
        onClose={() => setShowHistoryModal(false)}
        title="Salary History"
        size="xl"
      >
        {loading ? (
          <Loading text="Loading history..." />
        ) : (
          <Table columns={historyColumns} data={salaryHistory} />
        )}
      </Modal>
    </div>
  );
};

export default Salaries;

