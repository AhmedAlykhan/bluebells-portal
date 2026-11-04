import React, { useState } from 'react';
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
  CheckSquare
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { GenerateReportCardModal } from '../../components/modals/GenerateReportCardModal';
import { ReportCardData } from '../../data/reportCardsData';
import { NewAdmissionModal } from '../../components/modals/NewAdmissionModal';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';

export const AdminDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedReportCardData, setSelectedReportCardData] = useState<ReportCardData | undefined>(undefined);
  const [showAdmissionModal, setShowAdmissionModal] = useState(false);
  const [showMarkAttendance, setShowMarkAttendance] = useState(false);

  const stats = [
    { label: 'Active Students', value: '482', change: '+12% from last term', icon: GraduationCap, color: 'text-blue-600 bg-blue-50' },
    { label: 'Faculty & Teachers', value: '38', change: 'All sections assigned', icon: Users, color: 'text-emerald-600 bg-emerald-50' },
    { label: 'Today Attendance', value: '94.8%', change: 'Normal range (Latifabad Campus)', icon: CheckCircle2, color: 'text-amber-600 bg-amber-50' },
    { label: 'Fee Collection (PKR)', value: 'Rs. 1,420,000', change: '84% monthly target achieved', icon: CreditCard, color: 'text-purple-600 bg-purple-50' },
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-brand-900 to-brand-700 rounded-2xl p-6 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-white/20 text-white mb-2">
            Academic Session 2026-2027
          </span>
          <h2 className="text-2xl font-bold tracking-tight">
            Welcome, {user?.fullName || 'Administrator'}
          </h2>
          <p className="text-brand-100 text-sm mt-1 max-w-2xl">
            Campus: Autobahn Road, Latifabad, Hyderabad, Sindh. Real-time school operations, attendance, grading, fee ledger, and LMS portal.
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setShowAdmissionModal(true)}
            className="px-3 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg shadow text-xs transition-colors flex items-center gap-1.5"
          >
            <UserPlus className="w-3.5 h-3.5" />
            <span>+ New Admission</span>
          </button>
          <button
            onClick={() => setShowMarkAttendance(true)}
            className="px-3 py-2 bg-brand-800 hover:bg-brand-900 text-white font-semibold rounded-lg shadow text-xs transition-colors flex items-center gap-1.5 border border-brand-600"
          >
            <CheckSquare className="w-3.5 h-3.5" />
            <span>Mark Attendance</span>
          </button>
          <button
            onClick={() => setShowGenerateModal(true)}
            className="px-3 py-2 bg-white text-brand-900 font-semibold rounded-lg shadow text-xs hover:bg-brand-50 transition-colors flex items-center gap-1.5"
          >
            <Award className="w-3.5 h-3.5 text-amber-500" />
            <span>Generate Report Card</span>
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {stats.map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <div key={idx} className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-500">{stat.label}</span>
                <div className={`p-2 rounded-lg ${stat.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
              </div>
              <div className="mt-4">
                <span className="text-2xl font-bold text-slate-800">{stat.value}</span>
                <p className="text-xs text-slate-500 mt-1 flex items-center">
                  <TrendingUp className="w-3.5 h-3.5 text-emerald-500 mr-1 inline" />
                  {stat.change}
                </p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Campus System Health & Quick Modules */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <h3 className="text-base font-bold text-slate-800 mb-4 flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-brand-700" />
            Active Academic Operations
          </h3>
          <div className="divide-y divide-slate-100">
            <div className="py-3 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-slate-800">Class 9 Matriculation Section A</p>
                <p className="text-xs text-slate-500">Subject: Mathematics • Teacher: Sir Ahmed • Attendance: 96%</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-emerald-100 text-emerald-800">Session Active</span>
            </div>
            <div className="py-3 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-slate-800">Class 10 Matriculation Section A</p>
                <p className="text-xs text-slate-500">Subject: Physics • Teacher: Miss Fatima • Midterm Exam Planned</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800">Exam Scheduled</span>
            </div>
            <div className="py-3 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-slate-800">Monthly Tuition Fee Invoicing (October 2026)</p>
                <p className="text-xs text-slate-500">Invoices Generated: 482 • Due Date: 10th of Month</p>
              </div>
              <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-purple-100 text-purple-800">Billing Cleared</span>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <h3 className="text-base font-bold text-slate-800 mb-4 flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-emerald-600" />
            Security & Compliance Status
          </h3>
          <ul className="space-y-3 text-xs text-slate-600">
            <li className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span><strong>RBAC Active:</strong> 7 strict roles enforced</span>
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span><strong>Database:</strong> MySQL 8.x ACID InnoDB verified</span>
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span><strong>Timezone:</strong> Asia/Karachi (UTC+5) synchronized</span>
            </li>
            <li className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
              <span><strong>Financial Ledgers:</strong> Double-entry DECIMAL(12,2)</span>
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
