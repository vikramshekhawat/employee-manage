import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FiUsers, FiUserCheck, FiDollarSign, FiTrendingUp, FiAlertCircle } from 'react-icons/fi';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';
import dashboardService from '../services/dashboard.service';
import Card from '../components/common/Card';
import Loading from '../components/common/Loading';
import { toast } from 'react-toastify';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement);

const Dashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const response = await dashboardService.getDashboardData();
      if (response.success) {
        setDashboardData(response.data);
      }
    } catch (error) {
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loading fullScreen text="Loading Dashboard..." />;
  }

  const stats = [
    {
      title: 'Total Employees',
      value: dashboardData?.totalEmployees || 0,
      icon: FiUsers,
      color: 'bg-blue-500',
      textColor: 'text-blue-500',
      bgColor: 'bg-blue-50',
    },
    {
      title: 'Active Employees',
      value: dashboardData?.activeEmployees || 0,
      icon: FiUserCheck,
      color: 'bg-green-500',
      textColor: 'text-green-500',
      bgColor: 'bg-green-50',
    },
    {
      title: 'This Month Salary',
      value: `₹${(dashboardData?.totalSalaryThisMonth || 0).toLocaleString()}`,
      icon: FiDollarSign,
      color: 'bg-purple-500',
      textColor: 'text-purple-500',
      bgColor: 'bg-purple-50',
    },
    {
      title: 'Pending Salaries',
      value: dashboardData?.pendingSalaryGenerations || 0,
      icon: FiAlertCircle,
      color: 'bg-red-500',
      textColor: 'text-red-500',
      bgColor: 'bg-red-50',
    },
  ];

  const salaryComparisonData = {
    labels: ['Last Month', 'This Month'],
    datasets: [
      {
        label: 'Total Salary (₹)',
        data: [
          dashboardData?.totalSalaryLastMonth || 0,
          dashboardData?.totalSalaryThisMonth || 0,
        ],
        backgroundColor: ['rgba(59, 130, 246, 0.8)', 'rgba(16, 185, 129, 0.8)'],
        borderColor: ['rgb(59, 130, 246)', 'rgb(16, 185, 129)'],
        borderWidth: 2,
      },
    ],
  };

  const employeeStatusData = {
    labels: ['Active', 'Inactive'],
    datasets: [
      {
        data: [
          dashboardData?.activeEmployees || 0,
          (dashboardData?.totalEmployees || 0) - (dashboardData?.activeEmployees || 0),
        ],
        backgroundColor: ['rgba(16, 185, 129, 0.8)', 'rgba(239, 68, 68, 0.8)'],
        borderColor: ['rgb(16, 185, 129)', 'rgb(239, 68, 68)'],
        borderWidth: 2,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
    },
  };

  return (
    <div className="space-y-6 fade-in">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-gray-600 mt-1">Welcome back! Here's your overview.</p>
        </div>
        <div className="text-right">
          <p className="text-sm text-gray-500">
            {new Date().toLocaleDateString('en-US', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}
          </p>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <Card key={index} className="card-hover">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-800 mt-2">{stat.value}</p>
                </div>
                <div className={`p-4 rounded-full ${stat.bgColor}`}>
                  <Icon className={`w-6 h-6 ${stat.textColor}`} />
                </div>
              </div>
            </Card>
          );
        })}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Salary Comparison">
          <div className="h-64">
            <Bar data={salaryComparisonData} options={chartOptions} />
          </div>
        </Card>

        <Card title="Employee Status">
          <div className="h-64 flex items-center justify-center">
            <div className="w-64">
              <Doughnut data={employeeStatusData} options={chartOptions} />
            </div>
          </div>
        </Card>
      </div>

      {/* Quick Actions */}
      <Card title="Quick Actions">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link
            to="/employees"
            className="p-6 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg text-white hover:shadow-lg transition-all transform hover:-translate-y-1"
          >
            <FiUsers className="w-8 h-8 mb-3" />
            <h3 className="font-semibold text-lg">Manage Employees</h3>
            <p className="text-sm text-blue-100 mt-1">Add or update employee records</p>
          </Link>

          <Link
            to="/transactions"
            className="p-6 bg-gradient-to-br from-green-500 to-green-600 rounded-lg text-white hover:shadow-lg transition-all transform hover:-translate-y-1"
          >
            <FiAlertCircle className="w-8 h-8 mb-3" />
            <h3 className="font-semibold text-lg">Record Transactions</h3>
            <p className="text-sm text-green-100 mt-1">Add advances, leaves, overtime</p>
          </Link>

          <Link
            to="/salaries"
            className="p-6 bg-gradient-to-br from-purple-500 to-purple-600 rounded-lg text-white hover:shadow-lg transition-all transform hover:-translate-y-1"
          >
            <FiDollarSign className="w-8 h-8 mb-3" />
            <h3 className="font-semibold text-lg">Generate Salaries</h3>
            <p className="text-sm text-purple-100 mt-1">Calculate and generate salaries</p>
          </Link>

          <Link
            to="/salaries"
            className="p-6 bg-gradient-to-br from-orange-500 to-orange-600 rounded-lg text-white hover:shadow-lg transition-all transform hover:-translate-y-1"
          >
            <FiTrendingUp className="w-8 h-8 mb-3" />
            <h3 className="font-semibold text-lg">View Reports</h3>
            <p className="text-sm text-orange-100 mt-1">Salary history and reports</p>
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default Dashboard;

