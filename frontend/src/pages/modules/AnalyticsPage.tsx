import React, { useState, useEffect } from 'react';
import { 
  BarChart3, 
  Users, 
  CheckCircle2, 
  CreditCard, 
  BookOpen, 
  Briefcase, 
  Download, 
  Printer, 
  Filter, 
  Layers,
  GraduationCap,
  RefreshCw
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

export const AnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const isLeadership = user?.roles?.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL'].includes(r));
  const isAccountantOnly = user?.roles?.includes('ROLE_ACCOUNTANT') && !isLeadership;

  const [activeTab, setActiveTab] = useState<'overview' | 'students' | 'attendance' | 'academics' | 'financial' | 'lms' | 'staff'>(
    isAccountantOnly ? 'financial' : 'overview'
  );
  const [loading, setLoading] = useState(true);
  const [exporting, setExporting] = useState(false);
  const [analyticsData, setAnalyticsData] = useState<any>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const fetchAnalytics = () => {
    setLoading(true);
    const params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    const endpoint = isAccountantOnly ? '/analytics/financial' : '/analytics/overview';
    apiClient.get(endpoint, { params })
      .then(res => {
        if (res.data?.data) {
          if (isAccountantOnly) {
            setAnalyticsData({ financialAnalytics: res.data.data });
          } else {
            setAnalyticsData(res.data.data);
          }
        }
      })
      .catch(err => {
        console.error('Failed to load management analytics:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    if (isAccountantOnly) {
      setActiveTab('financial');
    }
  }, [isAccountantOnly]);

  useEffect(() => {
    fetchAnalytics();
  }, [startDate, endDate]);

  const handleExportExcel = async () => {
    setExporting(true);
    try {
      const response = await apiClient.get('/analytics/export/excel', {
        responseType: 'blob',
        params: { startDate, endDate }
      });
      const blob = new Blob([response.data], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'Bluebells_Management_Analytics_Report.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      alert('Failed to export executive analytics spreadsheet. Please try again.');
    } finally {
      setExporting(false);
    }
  };

  const students = analyticsData?.studentAnalytics || {};
  const attendance = analyticsData?.attendanceAnalytics || {};
  const academics = analyticsData?.academicAnalytics || {};
  const financial = analyticsData?.financialAnalytics || {};
  const lms = analyticsData?.lmsAnalytics || {};
  const staff = analyticsData?.staffAnalytics || {};

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-brand-700 uppercase tracking-widest mb-1">
            <BarChart3 className="w-4 h-4" />
            <span>Institutional Intelligence & Executive Reporting</span>
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Management Analytics</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Real-time multi-dimensional operational metrics grounded strictly in live institutional database records.
          </p>
        </div>

        <div className="flex items-center gap-2.5 print:hidden">
          <button
            onClick={handleExportExcel}
            disabled={exporting}
            className="flex items-center gap-2 px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold transition-colors shadow-sm disabled:opacity-50"
          >
            {exporting ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Download className="w-4 h-4" />}
            <span>Export Excel (.xlsx)</span>
          </button>
          <button
            onClick={() => window.print()}
            className="flex items-center gap-2 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition-colors border border-slate-200"
          >
            <Printer className="w-4 h-4" />
            <span>Print Report</span>
          </button>
        </div>
      </div>

      {/* Filter Toolbar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4 print:hidden">
        <div className="flex items-center gap-4 flex-wrap">
          <div className="flex items-center gap-2">
            <Filter className="w-4 h-4 text-slate-400" />
            <span className="text-xs font-bold text-slate-700">Filter Range:</span>
          </div>
          <div className="flex items-center gap-2">
            <label className="text-xs text-slate-500 font-medium">From:</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="text-xs border border-slate-300 rounded-lg px-2.5 py-1.5 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>
          <div className="flex items-center gap-2">
            <label className="text-xs text-slate-500 font-medium">To:</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="text-xs border border-slate-300 rounded-lg px-2.5 py-1.5 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>
          {(startDate || endDate) && (
            <button
              onClick={() => { setStartDate(''); setEndDate(''); }}
              className="text-xs text-brand-700 hover:underline font-semibold"
            >
              Clear Filters
            </button>
          )}
        </div>

        <button
          onClick={fetchAnalytics}
          className="flex items-center gap-1.5 text-xs font-bold text-brand-700 hover:text-brand-900 bg-brand-50 hover:bg-brand-100 px-3 py-1.5 rounded-lg transition-colors"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Live Metrics</span>
        </button>
      </div>

      {/* Navigation Sub-Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 overflow-x-auto pb-1 print:hidden">
        {[
          { id: 'overview', label: 'Overview Executive Summary', icon: Layers },
          { id: 'students', label: 'Student Demographics', icon: Users },
          { id: 'attendance', label: 'Attendance & Absenteeism', icon: CheckCircle2 },
          { id: 'academics', label: 'Academic Performance', icon: GraduationCap },
          { id: 'financial', label: 'Financial & Fee Ledger', icon: CreditCard },
          { id: 'lms', label: 'LMS & Coursework Engagement', icon: BookOpen },
          { id: 'staff', label: 'Faculty & Workload', icon: Briefcase },
        ].filter(tab => !isAccountantOnly || tab.id === 'financial').map((tab) => {
          const Icon = tab.icon;
          const active = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center gap-2 px-4 py-2.5 text-xs font-bold rounded-xl transition-all whitespace-nowrap ${
                active 
                  ? 'bg-[#123B70] text-white shadow-sm' 
                  : 'text-slate-600 hover:text-brand-800 hover:bg-slate-100'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {loading ? (
        <div className="py-20 flex flex-col items-center justify-center bg-white rounded-2xl border border-slate-200">
          <RefreshCw className="w-8 h-8 animate-spin text-brand-600 mb-3" />
          <p className="text-sm font-semibold text-slate-600">Aggregating live school database records...</p>
        </div>
      ) : (
        <div className="space-y-6">
          {/* OVERVIEW TAB */}
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Executive KPI Grid */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center justify-between text-slate-500 mb-2">
                    <span className="text-xs font-bold uppercase tracking-wider">Active Students</span>
                    <Users className="w-5 h-5 text-brand-600" />
                  </div>
                  <div className="text-3xl font-black text-slate-900">{students.totalActiveStudents || 0}</div>
                  <div className="text-xs text-slate-500 mt-1">
                    +{students.newAdmissionsCount || 0} New Admissions | {students.withdrawalsCount || 0} Withdrawals
                  </div>
                </div>

                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center justify-between text-slate-500 mb-2">
                    <span className="text-xs font-bold uppercase tracking-wider">Average Attendance</span>
                    <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                  </div>
                  <div className="text-3xl font-black text-slate-900">
                    {attendance.overallAttendanceRate != null ? `${attendance.overallAttendanceRate}%` : 'N/A'}
                  </div>
                  <div className="text-xs text-slate-500 mt-1">
                    {attendance.chronicAbsenteesCount || 0} Chronic Absentees Alert
                  </div>
                </div>

                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center justify-between text-slate-500 mb-2">
                    <span className="text-xs font-bold uppercase tracking-wider">Fee Collection</span>
                    <CreditCard className="w-5 h-5 text-blue-600" />
                  </div>
                  <div className="text-3xl font-black text-slate-900">
                    {financial.collectionRatePercentage != null ? `${financial.collectionRatePercentage}%` : '0%'}
                  </div>
                  <div className="text-xs text-slate-500 mt-1">
                    PKR {Number(financial.totalCollected || 0).toLocaleString()} of PKR {Number(financial.totalBilled || 0).toLocaleString()}
                  </div>
                </div>

                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="flex items-center justify-between text-slate-500 mb-2">
                    <span className="text-xs font-bold uppercase tracking-wider">LMS Submissions</span>
                    <BookOpen className="w-5 h-5 text-purple-600" />
                  </div>
                  <div className="text-3xl font-black text-slate-900">
                    {lms.submissionCompletionRate != null ? `${lms.submissionCompletionRate}%` : '0%'}
                  </div>
                  <div className="text-xs text-slate-500 mt-1">
                    {lms.totalSubmissions || 0} Submissions across {lms.totalActiveCourses || 0} Courses
                  </div>
                </div>
              </div>

              {/* Class Strength Summary Table */}
              <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-4 border-b border-slate-200 bg-slate-50/50 flex items-center justify-between">
                  <h3 className="font-bold text-sm text-slate-800">Class-wise Enrollment & Distribution</h3>
                  <span className="text-xs text-slate-500">{students.classWiseStrength?.length || 0} Grade Levels</span>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse text-xs">
                    <thead>
                      <tr className="bg-slate-100 text-slate-600 font-bold uppercase tracking-wider border-b border-slate-200">
                        <th className="p-3">Class / Grade</th>
                        <th className="p-3">Total Students</th>
                        <th className="p-3">Strength Indicator</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {(students.classWiseStrength || []).map((c: any, idx: number) => (
                        <tr key={idx} className="hover:bg-slate-50">
                          <td className="p-3 font-bold text-slate-800">{c.categoryName}</td>
                          <td className="p-3 text-slate-700">{c.count} students</td>
                          <td className="p-3">
                            <div className="w-full bg-slate-200 h-2 rounded-full overflow-hidden max-w-xs">
                              <div 
                                className="bg-[#123B70] h-full rounded-full" 
                                style={{ width: `${Math.min(100, (c.count / (students.totalActiveStudents || 1)) * 100 * 3)}%` }}
                              />
                            </div>
                          </td>
                        </tr>
                      ))}
                      {(!students.classWiseStrength || students.classWiseStrength.length === 0) && (
                        <tr>
                          <td colSpan={3} className="p-6 text-center text-slate-400">No enrollment records available.</td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* STUDENTS TAB */}
          {activeTab === 'students' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">Gender Demographics</h4>
                  <div className="space-y-3">
                    {(students.genderDistribution || []).map((g: any, i: number) => (
                      <div key={i}>
                        <div className="flex justify-between text-xs font-semibold mb-1">
                          <span className="capitalize text-slate-700">{g.gender}</span>
                          <span className="text-slate-900">{g.count} ({g.percentage}%)</span>
                        </div>
                        <div className="w-full bg-slate-200 h-2 rounded-full overflow-hidden">
                          <div 
                            className={`h-full rounded-full ${g.gender?.toLowerCase() === 'female' ? 'bg-pink-500' : 'bg-blue-600'}`} 
                            style={{ width: `${g.percentage}%` }}
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm col-span-2">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">Section Distribution</h4>
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                    {(students.sectionWiseDistribution || []).map((s: any, idx: number) => (
                      <div key={idx} className="bg-slate-50 p-3 rounded-xl border border-slate-200">
                        <div className="text-xs font-bold text-slate-800">{s.className} - {s.sectionName}</div>
                        <div className="text-lg font-black text-brand-900 mt-1">{s.studentCount} Students</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* ATTENDANCE TAB */}
          {activeTab === 'attendance' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">Chronic Absenteeism Watchlist</h4>
                  <p className="text-xs text-slate-500 mb-4">Students with repeated absences requiring urgent parent outreach.</p>
                  <div className="space-y-2">
                    {(attendance.chronicAbsentees || []).map((st: any, idx: number) => (
                      <div key={idx} className="flex items-center justify-between p-3 bg-red-50/60 border border-red-200 rounded-xl text-xs">
                        <div>
                          <div className="font-bold text-slate-900">{st.studentName}</div>
                          <div className="text-slate-500">{st.admissionNumber} | {st.className} - {st.sectionName}</div>
                        </div>
                        <div className="text-right">
                          <span className="bg-red-100 text-red-800 font-bold px-2.5 py-1 rounded-full text-[11px]">
                            {st.absentDays} Days Absent
                          </span>
                        </div>
                      </div>
                    ))}
                    {(!attendance.chronicAbsentees || attendance.chronicAbsentees.length === 0) && (
                      <div className="text-center py-8 text-slate-400 text-xs">No chronic absentees recorded.</div>
                    )}
                  </div>
                </div>

                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3">Attendance Distribution Summary</h4>
                  <div className="space-y-3">
                    <div className="p-3 bg-slate-50 rounded-xl flex justify-between items-center text-xs">
                      <span className="font-semibold text-slate-700">Total Student Days Logged:</span>
                      <span className="font-bold text-slate-900">{attendance.totalAttendanceRecords || 0}</span>
                    </div>
                    <div className="p-3 bg-emerald-50 rounded-xl flex justify-between items-center text-xs">
                      <span className="font-semibold text-emerald-800">Present Count:</span>
                      <span className="font-bold text-emerald-900">{attendance.presentCount || 0}</span>
                    </div>
                    <div className="p-3 bg-amber-50 rounded-xl flex justify-between items-center text-xs">
                      <span className="font-semibold text-amber-800">Late Count:</span>
                      <span className="font-bold text-amber-900">{attendance.lateCount || 0}</span>
                    </div>
                    <div className="p-3 bg-red-50 rounded-xl flex justify-between items-center text-xs">
                      <span className="font-semibold text-red-800">Absent Count:</span>
                      <span className="font-bold text-red-900">{attendance.absentCount || 0}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* FINANCIAL TAB */}
          {activeTab === 'financial' && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Invoiced (Billed)</div>
                  <div className="text-2xl font-black text-slate-900 mt-2">PKR {Number(financial.totalBilled || 0).toLocaleString()}</div>
                </div>
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Collected</div>
                  <div className="text-2xl font-black text-emerald-700 mt-2">PKR {Number(financial.totalCollected || 0).toLocaleString()}</div>
                </div>
                <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                  <div className="text-xs font-bold uppercase tracking-wider text-slate-500">Outstanding Receivable</div>
                  <div className="text-2xl font-black text-amber-700 mt-2">PKR {Number(financial.totalPendingBalance || 0).toLocaleString()}</div>
                </div>
              </div>

              {/* Class-wise fee status */}
              <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="p-4 border-b border-slate-200 bg-slate-50 font-bold text-xs text-slate-700 uppercase tracking-wider">
                  Class-wise Fee Collection & Balance Ledger
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse text-xs">
                    <thead>
                      <tr className="bg-slate-100 text-slate-600 font-bold uppercase border-b border-slate-200">
                        <th className="p-3">Class</th>
                        <th className="p-3">Billed</th>
                        <th className="p-3">Collected</th>
                        <th className="p-3">Balance</th>
                        <th className="p-3">Collection Rate</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-200">
                      {(financial.classWiseBalances || []).map((c: any, i: number) => (
                        <tr key={i} className="hover:bg-slate-50">
                          <td className="p-3 font-bold text-slate-900">{c.className}</td>
                          <td className="p-3 text-slate-700">PKR {Number(c.billedAmount || 0).toLocaleString()}</td>
                          <td className="p-3 text-emerald-700 font-medium">PKR {Number(c.collectedAmount || 0).toLocaleString()}</td>
                          <td className="p-3 text-red-600 font-semibold">PKR {Number(c.pendingBalance || 0).toLocaleString()}</td>
                          <td className="p-3 font-bold">{c.collectionRate}%</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* ACADEMICS TAB */}
          {activeTab === 'academics' && (
            <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm space-y-4">
              <h3 className="font-bold text-sm text-slate-800">Academic Support & At-Risk Watchlist</h3>
              <p className="text-xs text-slate-500">Identified students who scored below 40% across examination terms.</p>
              <div className="divide-y divide-slate-100">
                {(academics.atRiskStudents || []).map((s: any, idx: number) => (
                  <div key={idx} className="py-3 flex items-center justify-between text-xs">
                    <div>
                      <span className="font-bold text-slate-900">{s.studentName}</span>
                      <span className="text-slate-500 ml-2">({s.admissionNumber}) - {s.className}</span>
                    </div>
                    <span className="bg-red-50 text-red-700 px-2.5 py-1 rounded-full font-bold">
                      Average: {s.averageMarks}%
                    </span>
                  </div>
                ))}
                {(!academics.atRiskStudents || academics.atRiskStudents.length === 0) && (
                  <p className="text-center py-6 text-slate-400 text-xs">No students currently flagged in academic risk watchlist.</p>
                )}
              </div>
            </div>
          )}

          {/* LMS TAB */}
          {activeTab === 'lms' && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                <div className="text-xs font-bold uppercase text-slate-500">Active Courses</div>
                <div className="text-3xl font-black text-slate-900 mt-2">{lms.totalActiveCourses || 0}</div>
              </div>
              <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                <div className="text-xs font-bold uppercase text-slate-500">Assignments Published</div>
                <div className="text-3xl font-black text-slate-900 mt-2">{lms.totalAssignments || 0}</div>
              </div>
              <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
                <div className="text-xs font-bold uppercase text-slate-500">Submission Completion Rate</div>
                <div className="text-3xl font-black text-purple-700 mt-2">{lms.submissionCompletionRate || 0}%</div>
              </div>
            </div>
          )}

          {/* STAFF TAB */}
          {activeTab === 'staff' && (
            <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
              <h3 className="font-bold text-sm text-slate-800 mb-3">Faculty Department Breakdown & Workload</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
                {(staff.departmentBreakdown || []).map((d: any, i: number) => (
                  <div key={i} className="p-4 bg-slate-50 rounded-xl border border-slate-200">
                    <div className="text-xs font-bold text-slate-500 uppercase">{d.departmentName}</div>
                    <div className="text-2xl font-black text-slate-900 mt-1">{d.staffCount} Staff</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
