import React, { useState, useEffect } from 'react';
import { 
  Building2, 
  Users, 
  GraduationCap, 
  Award, 
  CheckCircle2, 
  TrendingUp, 
  Calendar, 
  FileCheck, 
  BookOpen
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { GenerateReportCardModal } from '../../components/modals/GenerateReportCardModal';
import { ReportCardData } from '../../data/reportCardsData';
import { apiClient } from '../../api/client';

export const PrincipalDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedReportCardData, setSelectedReportCardData] = useState<ReportCardData | undefined>(undefined);
  const [dashboardStats, setDashboardStats] = useState({
    totalStudents: 482,
    totalTeachers: 38,
    todayAttendance: 95.2,
    pendingApprovals: 4,
  });

  useEffect(() => {
    apiClient.get('/reports/dashboard')
      .then(res => {
        if (res.data?.data) {
          const d = res.data.data;
          setDashboardStats(prev => ({
            ...prev,
            totalStudents: d.totalStudents || 482,
            totalTeachers: d.totalTeachers || 38,
            todayAttendance: d.attendanceRateToday ? Number(d.attendanceRateToday) : 95.2,
          }));
        }
      })
      .catch(() => {});
  }, []);

  const classPerformance = [
    { class: 'Class 9-A (Matric Science)', students: 32, avgAttendance: '96.2%', passRate: '100%', teacher: 'Sir Ahmed Khan', topStudent: 'Ali Khan (89.6%)' },
    { class: 'Class 10-A (Matric Science)', students: 34, avgAttendance: '94.8%', passRate: '97.1%', teacher: 'Miss Fatima Shah', topStudent: 'Hamza Farooq (88.4%)' },
    { class: 'Class 8 Rose (Middle)', students: 29, avgAttendance: '93.5%', passRate: '93.1%', teacher: 'Sir Asif', topStudent: 'Sara Khan (86.2%)' },
    { class: 'Class 5 Jasmine (Primary)', students: 25, avgAttendance: '95.0%', passRate: '96.0%', teacher: 'Miss Hina', topStudent: 'Zainab Bibi (91.0%)' },
  ];

  const pendingApprovals = [
    { id: 'APR-2026-08', type: 'Examination Moderation', detail: 'Mid-term Class 9 Physics Question Paper & Rubric', submitter: 'Miss Fatima Shah', status: 'PENDING_REVIEW' },
    { id: 'APR-2026-09', type: 'Admission Enrollment', detail: 'Entrance Test Passed (88%) - Shahid Afridi (Class 9)', submitter: 'Admissions Desk', status: 'APPROVED' },
    { id: 'APR-2026-10', type: 'Academic Timetable Revision', detail: 'Adjusted Lab Double-Period for Class 10', submitter: 'Hana Khan (Vice Principal)', status: 'PENDING_REVIEW' },
  ];

  return (
    <div className="space-y-6">
      {/* Principal Welcome Banner */}
      <div className="bg-gradient-to-r from-[#123B70] via-[#1A5392] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <Building2 className="w-3.5 h-3.5 text-brand-300" />
            <span>Academic Leadership & Oversight • Latifabad Campus</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome, Principal {user?.fullName || 'Huma Arif'}
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            School-wide academic quality assurance, BISE Hyderabad matriculation preparation, faculty evaluation, and institutional governance.
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={() => setShowGenerateModal(true)}
            className="px-4 py-2.5 bg-white text-[#123B70] hover:bg-brand-50 font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <Award className="w-4 h-4 text-amber-500" />
            <span>Generate Official Report Card</span>
          </button>
        </div>
      </div>

      {/* Leadership Executive KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Enrolled Students</span>
            <div className="p-2.5 rounded-xl bg-blue-50 text-brand-700">
              <GraduationCap className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl sm:text-3xl font-black text-slate-800">{dashboardStats.totalStudents}</span>
            <p className="text-xs text-emerald-600 font-semibold mt-1 flex items-center gap-1">
              <TrendingUp className="w-3.5 h-3.5" /> 100% Active in 2026-2027
            </p>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Teaching Faculty</span>
            <div className="p-2.5 rounded-xl bg-purple-50 text-purple-700">
              <Users className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl sm:text-3xl font-black text-slate-800">{dashboardStats.totalTeachers}</span>
            <p className="text-xs text-slate-500 mt-1">100% Sections Assigned</p>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Student Attendance Today</span>
            <div className="p-2.5 rounded-xl bg-emerald-50 text-emerald-700">
              <CheckCircle2 className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl sm:text-3xl font-black text-emerald-700">{dashboardStats.todayAttendance}%</span>
            <p className="text-xs text-slate-500 mt-1">Campus Average: 94.8% Target</p>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Matric Exam Readiness</span>
            <div className="p-2.5 rounded-xl bg-amber-50 text-amber-700">
              <Award className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl sm:text-3xl font-black text-slate-800">100%</span>
            <p className="text-xs text-brand-600 font-semibold mt-1">BISE Hyderabad Syllabus on track</p>
          </div>
        </div>
      </div>

      {/* Main Grid: Class Academic Performance & Institutional Governance */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Class Performance Comparison */}
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-brand-700" />
                <span>Class Academic & Attendance Performance</span>
              </h2>
              <p className="text-xs text-slate-500 mt-0.5">Real-time indicators across active campus sections</p>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-blue-50 text-brand-700 text-xs font-semibold">
              Term 1 (2026-2027)
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Class & Section</th>
                  <th className="p-3.5">Class Teacher</th>
                  <th className="p-3.5">Attendance</th>
                  <th className="p-3.5">Pass Rate</th>
                  <th className="p-3.5">Top Performer</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {classPerformance.map((cls, idx) => (
                  <tr key={idx} className="hover:bg-slate-50/70 transition-colors">
                    <td className="p-3.5 font-bold text-slate-800">{cls.class}</td>
                    <td className="p-3.5 text-slate-600 font-medium">{cls.teacher}</td>
                    <td className="p-3.5">
                      <span className="px-2 py-0.5 rounded-full font-bold bg-emerald-50 text-emerald-700">
                        {cls.avgAttendance}
                      </span>
                    </td>
                    <td className="p-3.5 font-semibold text-slate-800">{cls.passRate}</td>
                    <td className="p-3.5 text-brand-700 font-semibold">{cls.topStudent}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Management Approvals & Academic Calendar */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <h2 className="text-sm font-bold text-slate-800 mb-3 flex items-center gap-2">
              <FileCheck className="w-4 h-4 text-emerald-600" />
              <span>Pending Principal Approvals</span>
            </h2>

            <div className="space-y-3">
              {pendingApprovals.map((item) => (
                <div key={item.id} className="p-3 rounded-xl bg-[#F8FAFC] border border-slate-200 text-xs space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-slate-800">{item.type}</span>
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      item.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'
                    }`}>
                      {item.status === 'APPROVED' ? 'Cleared' : 'Awaiting Sign-off'}
                    </span>
                  </div>
                  <p className="text-slate-600">{item.detail}</p>
                  <p className="text-[10px] text-slate-400 font-mono">By: {item.submitter}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <h2 className="text-sm font-bold text-slate-800 mb-3 flex items-center gap-2">
              <Calendar className="w-4 h-4 text-brand-700" />
              <span>Key Academic Dates</span>
            </h2>
            <ul className="text-xs text-slate-600 space-y-2">
              <li className="flex justify-between items-center py-1 border-b border-slate-100">
                <span>Mid-term Exams Commencement:</span>
                <strong className="text-slate-800">Dec 01, 2026</strong>
              </li>
              <li className="flex justify-between items-center py-1 border-b border-slate-100">
                <span>Parent-Teacher Meeting (PTM):</span>
                <strong className="text-slate-800">Dec 20, 2026</strong>
              </li>
              <li className="flex justify-between items-center py-1">
                <span>BISE Matric Registration Deadline:</span>
                <strong className="text-brand-700">Jan 15, 2027</strong>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Modals */}
      <GenerateReportCardModal
        isOpen={showGenerateModal}
        onClose={() => setShowGenerateModal(false)}
        onGenerate={(data) => {
          setSelectedReportCardData(data);
          setShowGenerateModal(false);
          setShowReportCard(true);
        }}
      />

      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
        data={selectedReportCardData}
      />
    </div>
  );
};
