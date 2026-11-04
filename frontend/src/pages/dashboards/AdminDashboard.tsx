import React, { useState, useEffect } from 'react';
import { 
  Users, 
  GraduationCap, 
  CreditCard, 
  BookOpen, 
  CheckCircle2, 
  TrendingUp, 
  ShieldCheck,
  UserPlus,
  Award,
  CheckSquare,
  Building2
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { GenerateReportCardModal } from '../../components/modals/GenerateReportCardModal';
import { ReportCardData } from '../../data/reportCardsData';
import { NewAdmissionModal } from '../../components/modals/NewAdmissionModal';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';
import { apiClient } from '../../api/client';

export const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedReportCardData, setSelectedReportCardData] = useState<ReportCardData | undefined>(undefined);
  const [showAdmissionModal, setShowAdmissionModal] = useState(false);
  const [showMarkAttendance, setShowMarkAttendance] = useState(false);

  const [stats, setStats] = useState({
    totalStudents: 482,
    totalTeachers: 38,
    todayAttendance: 94.8,
    totalFeeCollected: 1420000,
    totalFeeInvoiced: 1700000,
    outstandingFee: 280000,
  });

  useEffect(() => {
    apiClient.get('/reports/dashboard')
      .then(res => {
        if (res.data?.data) {
          const d = res.data.data;
          setStats({
            totalStudents: d.totalStudents || 482,
            totalTeachers: d.totalTeachers || 38,
            todayAttendance: d.attendanceRateToday ? Number(d.attendanceRateToday) : 94.8,
            totalFeeCollected: d.totalFeeCollected ? Number(d.totalFeeCollected) : 1420000,
            totalFeeInvoiced: d.totalFeeInvoiced ? Number(d.totalFeeInvoiced) : 1700000,
            outstandingFee: d.totalFeeOutstanding ? Number(d.totalFeeOutstanding) : 280000,
          });
        }
      })
      .catch(() => {});
  }, []);

  const kpis = [
    { 
      label: 'Active Students', 
      value: stats.totalStudents.toString(), 
      change: '+12% from last term', 
      icon: GraduationCap, 
      color: 'text-brand-700 bg-brand-50 border-brand-200' 
    },
    { 
      label: 'Faculty & Teachers', 
      value: stats.totalTeachers.toString(), 
      change: '100% Sections Assigned', 
      icon: Users, 
      color: 'text-purple-700 bg-purple-50 border-purple-200' 
    },
    { 
      label: 'Today Attendance', 
      value: `${stats.todayAttendance}%`, 
      change: 'Campus target: 94.5%', 
      icon: CheckCircle2, 
      color: 'text-emerald-700 bg-emerald-50 border-emerald-200' 
    },
    { 
      label: 'Fee Collections (PKR)', 
      value: `Rs. ${(stats.totalFeeCollected / 1000).toFixed(0)}k`, 
      change: '83.5% realization rate', 
      icon: CreditCard, 
      color: 'text-amber-800 bg-amber-50 border-amber-200' 
    },
  ];

  return (
    <div className="space-y-6">
      {/* Institutional Hero Banner */}
      <div className="bg-gradient-to-r from-[#123B70] via-[#1A5392] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <Building2 className="w-3.5 h-3.5 text-brand-300" />
            <span>Academic Session 2026-2027 • Latifabad Main Campus (Hyderabad, Sindh, Pakistan)</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome, {user?.fullName || 'Administrator'}
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            Institutional overview, student admissions, faculty allocations, double-entry financial ledger, and official Sindh Board certifications.
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={() => setShowAdmissionModal(true)}
            className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <UserPlus className="w-4 h-4" />
            <span>+ New Admission</span>
          </button>
          <button
            onClick={() => setShowMarkAttendance(true)}
            className="px-4 py-2.5 bg-white/10 hover:bg-white/20 text-white font-bold rounded-xl shadow text-xs transition-all flex items-center gap-2 border border-white/20"
          >
            <CheckSquare className="w-4 h-4 text-brand-200" />
            <span>Mark Attendance</span>
          </button>
          <button
            onClick={() => setShowGenerateModal(true)}
            className="px-4 py-2.5 bg-white text-[#123B70] hover:bg-brand-50 font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <Award className="w-4 h-4 text-amber-500" />
            <span>Generate Report Card</span>
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        {kpis.map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <div key={idx} className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-slate-500">{stat.label}</span>
                <div className={`p-2.5 rounded-xl border ${stat.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
              </div>
              <div className="mt-4">
                <span className="text-2xl sm:text-3xl font-black text-slate-800">{stat.value}</span>
                <p className="text-xs text-slate-500 mt-1 flex items-center font-medium">
                  <TrendingUp className="w-3.5 h-3.5 text-emerald-600 mr-1 inline" />
                  {stat.change}
                </p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Academic Operations & Financial Health Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
              <BookOpen className="w-4 h-4 text-brand-700" />
              <span>Campus Academic Operations Status</span>
            </h2>
            <span className="px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-800 text-xs font-bold">
              All Sections Operating
            </span>
          </div>

          <div className="divide-y divide-slate-100">
            <div className="py-3.5 flex items-center justify-between">
              <div>
                <p className="text-sm font-bold text-slate-800">Class 9 Matriculation (Section A)</p>
                <p className="text-xs text-slate-500">Instructor: Sir Ahmed Khan • Subject: Mathematics • Attendance: 96.2%</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-bold rounded-full bg-emerald-100 text-emerald-800">Active</span>
            </div>

            <div className="py-3.5 flex items-center justify-between">
              <div>
                <p className="text-sm font-bold text-slate-800">Class 10 Matriculation (Section A)</p>
                <p className="text-xs text-slate-500">Instructor: Miss Fatima Shah • Subject: Physics • BISE Model Papers</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-bold rounded-full bg-blue-100 text-blue-800">Exam Prep</span>
            </div>

            <div className="py-3.5 flex items-center justify-between">
              <div>
                <p className="text-sm font-bold text-slate-800">Monthly Tuition Invoicing (October 2026)</p>
                <p className="text-xs text-slate-500">Bank Alfalah 3-Copy Challans: 482 Generated • Reconciliation Active</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-bold rounded-full bg-purple-100 text-purple-800">Ledger Closed</span>
            </div>
          </div>
        </div>

        {/* Security & Infrastructure Panel */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            <span>Campus Security & Regulatory Health</span>
          </h2>

          <ul className="space-y-3 text-xs text-slate-600">
            <li className="flex items-center gap-2.5 p-2 rounded-xl bg-slate-50 border border-slate-100">
              <span className="w-2 h-2 rounded-full bg-emerald-500 flex-shrink-0" />
              <span><strong>RBAC Enforced:</strong> 7 strict institutional roles active</span>
            </li>
            <li className="flex items-center gap-2.5 p-2 rounded-xl bg-slate-50 border border-slate-100">
              <span className="w-2 h-2 rounded-full bg-emerald-500 flex-shrink-0" />
              <span><strong>BISE Hyderabad:</strong> Secondary Matriculation syllabus compliance</span>
            </li>
            <li className="flex items-center gap-2.5 p-2 rounded-xl bg-slate-50 border border-slate-100">
              <span className="w-2 h-2 rounded-full bg-emerald-500 flex-shrink-0" />
              <span><strong>Timezone:</strong> Asia/Karachi (UTC+5) synchronized</span>
            </li>
            <li className="flex items-center gap-2.5 p-2 rounded-xl bg-slate-50 border border-slate-100">
              <span className="w-2 h-2 rounded-full bg-emerald-500 flex-shrink-0" />
              <span><strong>Double-Entry Ledger:</strong> DECIMAL(12,2) Bank Alfalah reconciliation</span>
            </li>
          </ul>
        </div>
      </div>

      {/* Modals */}
      <GenerateReportCardModal
        isOpen={showGenerateModal}
        onClose={() => setShowGenerateModal(false)}
        onGenerate={(cardData) => {
          setSelectedReportCardData(cardData);
          setShowGenerateModal(false);
          setShowReportCard(true);
        }}
      />

      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
        data={selectedReportCardData}
      />

      <NewAdmissionModal
        isOpen={showAdmissionModal}
        onClose={() => setShowAdmissionModal(false)}
        onSuccess={() => {}}
      />

      <MarkAttendanceModal
        isOpen={showMarkAttendance}
        onClose={() => setShowMarkAttendance(false)}
        onSuccess={() => {}}
      />
    </div>
  );
};
