import React, { useState } from 'react';
import { 
  Users, 
  CheckCircle2, 
  CreditCard, 
  Award, 
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { Link } from 'react-router-dom';

interface Child {
  id: number;
  name: string;
  className: string;
  rollNumber: string;
  admissionNumber: string;
  attendanceRate: string;
  termResult: string;
  feeStatus: 'PAID' | 'DUE';
  feeAmount: string;
  classTeacher: string;
}

const CHILDREN: Child[] = [
  {
    id: 1,
    name: 'Ali Khan',
    className: 'Class 9-A (Matriculation Part I)',
    rollNumber: '09-A-12',
    admissionNumber: 'BSS-2024-0104',
    attendanceRate: '96.2%',
    termResult: 'Grade A-1 (89.6%)',
    feeStatus: 'PAID',
    feeAmount: 'Rs. 8,500',
    classTeacher: 'Sir Ahmed Khan'
  },
  {
    id: 5,
    name: 'Sara Khan',
    className: 'Class 8-Rose (Middle School)',
    rollNumber: '08-R-05',
    admissionNumber: 'BSS-2025-0211',
    attendanceRate: '94.5%',
    termResult: 'Grade A (84.0%)',
    feeStatus: 'PAID',
    feeAmount: 'Rs. 7,500',
    classTeacher: 'Sir Asif'
  }
];

export const ParentDashboard: React.FC = () => {
  const { user } = useAuth();
  const [selectedChildIndex, setSelectedChildIndex] = useState(0);
  const [showReportCard, setShowReportCard] = useState(false);

  const parentName = user?.fullName || 'Mohammad Tariq Khan';
  const child = CHILDREN[selectedChildIndex];

  return (
    <div className="space-y-6">
      {/* Parent Welcome Banner */}
      <div className="bg-gradient-to-r from-emerald-900 to-teal-800 rounded-2xl p-6 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-white/20 text-white mb-2 inline-block">
            Guardian & Parent Portal
          </span>
          <h2 className="text-2xl font-bold tracking-tight">
            Welcome, {parentName}
          </h2>
          <p className="text-emerald-100 text-sm mt-1 max-w-2xl">
            Latifabad Campus • Overview for your enrolled wards at Bluebells Schooling System Hyderabad.
          </p>
        </div>

        {/* Child Selector Tabs */}
        <div className="flex bg-emerald-950/40 p-1.5 rounded-xl border border-emerald-700/50">
          {CHILDREN.map((c, idx) => (
            <button
              key={c.id}
              onClick={() => setSelectedChildIndex(idx)}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                selectedChildIndex === idx
                  ? 'bg-white text-emerald-950 shadow'
                  : 'text-emerald-200 hover:text-white'
              }`}
            >
              {c.name} ({c.className.split(' ')[1]})
            </button>
          ))}
        </div>
      </div>

      {/* Selected Child Info & KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Student Attendance</span>
            <div className="p-2 rounded-lg text-emerald-600 bg-emerald-50">
              <CheckCircle2 className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl font-bold text-slate-800">{child.attendanceRate}</span>
            <p className="text-xs text-slate-500 mt-1">Excellent consistency</p>
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Academic Standing</span>
            <div className="p-2 rounded-lg text-amber-600 bg-amber-50">
              <Award className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl font-bold text-slate-800">{child.termResult.split(' ')[1]}</span>
            <p className="text-xs text-slate-500 mt-1">{child.termResult}</p>
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Tuition Fee Ledger</span>
            <div className="p-2 rounded-lg text-blue-600 bg-blue-50">
              <CreditCard className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-2xl font-bold text-emerald-600">{child.feeStatus}</span>
            <p className="text-xs text-slate-500 mt-1">{child.feeAmount} • Cleared</p>
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-slate-500">Class In-charge</span>
            <div className="p-2 rounded-lg text-purple-600 bg-purple-50">
              <Users className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-4">
            <span className="text-lg font-bold text-slate-800">{child.classTeacher}</span>
            <p className="text-xs text-slate-500 mt-1">{child.className}</p>
          </div>
        </div>
      </div>

      {/* Main Details Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-base font-bold text-slate-800">
                Academic Performance: {child.name}
              </h3>
              <p className="text-xs text-slate-500">
                Admission No: {child.admissionNumber} • Roll: {child.rollNumber} • {child.className}
              </p>
            </div>
            <button
              onClick={() => setShowReportCard(true)}
              className="px-3 py-1.5 bg-brand-700 hover:bg-brand-800 text-white rounded-lg text-xs font-semibold shadow transition-colors flex items-center gap-1.5"
            >
              <Award className="w-4 h-4 text-amber-400" />
              <span>View Sindh Board Report Card</span>
            </button>
          </div>

          <div className="p-4 bg-slate-50 rounded-xl border border-slate-200 text-xs space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-slate-600">Midterm Examination Status:</span>
              <span className="font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                Results Published & Certified
              </span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-slate-600">Overall Term Aggregate:</span>
              <span className="font-bold text-slate-800">672 / 750 (89.6%) - Position: 1st</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-slate-600">Teacher Remarks:</span>
              <span className="italic text-slate-700">"Demonstrates superior academic diligence and outstanding analytical mindset."</span>
            </div>
          </div>

          <div className="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
            <Link
              to="/attendance"
              className="p-4 rounded-xl border border-slate-200 hover:border-brand-300 hover:bg-brand-50/30 transition-all flex items-center justify-between"
            >
              <div>
                <p className="font-bold text-slate-800">Attendance Log</p>
                <p className="text-slate-500 text-[11px] mt-0.5">View daily presence, leaves, and records</p>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </Link>

            <Link
              to="/timetable"
              className="p-4 rounded-xl border border-slate-200 hover:border-brand-300 hover:bg-brand-50/30 transition-all flex items-center justify-between"
            >
              <div>
                <p className="font-bold text-slate-800">Weekly Class Timetable</p>
                <p className="text-slate-500 text-[11px] mt-0.5">Bell schedule and subject periods</p>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </Link>
          </div>
        </div>

        {/* Tuition Fee Voucher Box */}
        <div className="space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
            <h3 className="text-base font-bold text-slate-800 mb-3 flex items-center gap-2">
              <CreditCard className="w-5 h-5 text-brand-700" />
              Tuition Fee Voucher
            </h3>
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 text-xs space-y-2">
              <div className="flex justify-between">
                <span className="text-slate-500">Challan Ref:</span>
                <span className="font-mono font-bold text-slate-800">INV-202610-0104</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Due Date:</span>
                <span className="text-slate-800">10 October 2026</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Billed Amount:</span>
                <span className="font-bold text-slate-800">{child.feeAmount}</span>
              </div>
              <div className="flex justify-between items-center pt-2 border-t border-slate-200">
                <span className="text-slate-500">Payment Status:</span>
                <span className="px-2 py-0.5 rounded font-bold text-[10px] bg-emerald-100 text-emerald-800">
                  PAID IN FULL
                </span>
              </div>
            </div>

            <Link
              to="/fees"
              className="mt-4 block w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-center font-semibold text-xs rounded-lg transition-colors"
            >
              View Full Fee Ledger & Receipts
            </Link>
          </div>
        </div>
      </div>

      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
      />
    </div>
  );
};
