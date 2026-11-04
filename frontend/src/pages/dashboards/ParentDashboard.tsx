import React, { useState, useEffect } from 'react';
import { 
  Users, 
  GraduationCap, 
  Award, 
  CreditCard, 
  Printer, 
  Phone, 
  MessageSquare
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { FeeChallanModal } from '../../components/modals/FeeChallanModal';
import { getReportCardByName } from '../../data/reportCardsData';
import { apiClient } from '../../api/client';

interface Child {
  id: string;
  name: string;
  class: string;
  section: string;
  roll: string;
  admissionNo: string;
  attendance: string;
  midtermResult: string;
  feeStatus: string;
  feeBalance: string;
  teacher: string;
  teacherPhone: string;
}

export const ParentDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showChallan, setShowChallan] = useState(false);
  const [children, setChildren] = useState<Child[]>([
    {
      id: '1',
      name: 'Ali Khan',
      class: 'Class 9',
      section: 'Section A',
      roll: '01',
      admissionNo: 'BSS-2024-0104',
      attendance: '96.2%',
      midtermResult: '1st Position (89.6% • Grade A-1)',
      feeStatus: 'PAID',
      feeBalance: 'Rs. 0.00',
      teacher: 'Sir Ahmed Khan',
      teacherPhone: '+92 316 9681932',
    },
  ]);
  const [selectedChildIndex, setSelectedChildIndex] = useState(0);

  useEffect(() => {
    apiClient.get('/guardians/my-children')
      .then(res => {
        if (Array.isArray(res.data?.data) && res.data.data.length > 0) {
          interface ChildApi {
            id: number;
            fullName?: string;
            activeClassName?: string;
            activeSectionName?: string;
            rollNumber?: string;
            admissionNumber?: string;
          }
          const mapped: Child[] = res.data.data.map((c: ChildApi) => ({
            id: c.id.toString(),
            name: c.fullName || 'Ali Khan',
            class: c.activeClassName || 'Class 9',
            section: c.activeSectionName || 'Section A',
            roll: c.rollNumber || '01',
            admissionNo: c.admissionNumber || 'BSS-2024-0104',
            attendance: '96.2%',
            midtermResult: '1st Position (89.6% • Grade A-1)',
            feeStatus: 'PAID',
            feeBalance: 'Rs. 0.00',
            teacher: 'Sir Ahmed Khan',
            teacherPhone: '+92 316 9681932',
          }));
          setChildren(mapped);
        }
      })
      .catch(() => {});
  }, []);

  const currentChild = children[selectedChildIndex] || children[0];

  const recentReceipts = [
    { receiptNo: 'REC-202610-0001', month: 'October 2026 Tuition', amount: 'Rs. 10,000', method: 'Online Bank Transfer (Meezan #PK98234710)', status: 'CLEARED' },
    { receiptNo: 'REC-202609-0001', month: 'September 2026 Tuition', amount: 'Rs. 10,000', method: 'Bank Alfalah Autobahn Branch Deposit', status: 'CLEARED' },
  ];

  return (
    <div className="space-y-6">
      {/* Guardian Welcome Banner */}
      <div className="bg-gradient-to-r from-[#123B70] via-[#1A5392] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <Users className="w-3.5 h-3.5 text-brand-300" />
            <span>Guardian Portal • Latifabad Campus</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome, {user?.fullName || 'Mohammad Tariq Khan'}
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            Monitor your child's classroom attendance, Sindh Board academic transcripts, teacher observations, and official Bank Alfalah fee vouchers.
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={() => setShowReportCard(true)}
            className="px-4 py-2.5 bg-white text-[#123B70] hover:bg-brand-50 font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <Award className="w-4 h-4 text-amber-500" />
            <span>{currentChild.name}'s Report Card</span>
          </button>
          <button
            onClick={() => setShowChallan(true)}
            className="px-4 py-2.5 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <Printer className="w-4 h-4" />
            <span>Print Fee Challan</span>
          </button>
        </div>
      </div>

      {/* Child Selector Tabs (if multiple children) */}
      {children.length > 1 && (
        <div className="flex items-center gap-2 bg-white p-2 rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-bold text-slate-500 px-3 uppercase tracking-wider">Select Child:</span>
          {children.map((child, idx) => (
            <button
              key={child.id}
              onClick={() => setSelectedChildIndex(idx)}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                selectedChildIndex === idx
                  ? 'bg-[#123B70] text-white shadow'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {child.name} ({child.class})
            </button>
          ))}
        </div>
      )}

      {/* Child Summary Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-6 border-b border-slate-100 gap-4">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-brand-50 border border-brand-200 flex items-center justify-center text-brand-700">
              <GraduationCap className="w-7 h-7" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-xl font-bold text-slate-800">{currentChild.name}</h2>
                <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  Enrolled & Active
                </span>
              </div>
              <p className="text-xs text-slate-500 mt-0.5">
                {currentChild.class} - {currentChild.section} • Roll #{currentChild.roll} • Admission #{currentChild.admissionNo}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="text-right">
              <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">Class Teacher</span>
              <span className="text-xs font-bold text-slate-800 block">{currentChild.teacher}</span>
            </div>
            <a
              href={`tel:${currentChild.teacherPhone}`}
              className="p-2.5 rounded-xl bg-blue-50 text-brand-700 hover:bg-blue-100 transition-colors"
              title="Call Class Teacher"
            >
              <Phone className="w-4 h-4" />
            </a>
          </div>
        </div>

        {/* 4 Child Metrics */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 pt-6">
          <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-100">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Attendance Rate</span>
            <div className="text-2xl font-black text-emerald-700 mt-1">{currentChild.attendance}</div>
            <p className="text-[11px] text-slate-500 mt-0.5">Regular and on time</p>
          </div>

          <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-100">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Academic Standing</span>
            <div className="text-xl font-black text-brand-800 mt-1">1st Position</div>
            <p className="text-[11px] text-emerald-600 font-semibold mt-0.5">Sindh Board Grade A-1</p>
          </div>

          <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-100">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Fee Balance Due</span>
            <div className="text-2xl font-black text-emerald-700 mt-1">{currentChild.feeBalance}</div>
            <p className="text-[11px] text-emerald-600 font-semibold mt-0.5">Current Month Fully Paid</p>
          </div>

          <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-100">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Next Major Exam</span>
            <div className="text-xl font-black text-slate-800 mt-1">Dec 01, 2026</div>
            <p className="text-[11px] text-brand-600 font-semibold mt-0.5">First Term Midterm Exams</p>
          </div>
        </div>
      </div>

      {/* Fee Payment & Verified Receipts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2">
              <CreditCard className="w-4 h-4 text-brand-700" />
              <span>Tuition Fee Vouchers & Payment Receipts</span>
            </h3>
            <span className="text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full">
              Accounts Cleared
            </span>
          </div>

          <div className="space-y-3">
            {recentReceipts.map((rc, idx) => (
              <div key={idx} className="p-3.5 rounded-xl border border-slate-200 bg-[#F8FAFC] text-xs flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-slate-800">{rc.receiptNo}</span>
                    <span className="px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 font-bold text-[10px]">
                      {rc.status}
                    </span>
                  </div>
                  <p className="text-slate-600 font-medium mt-0.5">{rc.month} • {rc.amount}</p>
                  <p className="text-[10px] text-slate-400 font-mono mt-0.5">{rc.method}</p>
                </div>
                <button
                  onClick={() => setShowChallan(true)}
                  className="px-3 py-1.5 rounded-lg bg-purple-50 hover:bg-purple-100 text-purple-700 font-bold text-xs inline-flex items-center gap-1.5 transition-colors self-start sm:self-center"
                >
                  <Printer className="w-3.5 h-3.5" />
                  <span>View Voucher</span>
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Teacher Feedback & Remarks */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h3 className="text-sm font-bold text-slate-800 flex items-center gap-2">
            <MessageSquare className="w-4 h-4 text-brand-700" />
            <span>Class Teacher Feedback & Remarks</span>
          </h3>

          <div className="p-4 rounded-xl bg-blue-50/70 border border-brand-100 text-xs space-y-2">
            <div className="flex items-center justify-between text-brand-900 font-bold">
              <span>Sir Ahmed Khan (Class Teacher)</span>
              <span className="text-[10px] text-brand-600 font-normal">October Term Review</span>
            </div>
            <p className="text-slate-700 leading-relaxed italic">
              "Ali demonstrates exceptional mathematical rigor and analytical discipline. He consistently solves problem sets ahead of deadlines and maintains an exemplary 96.2% attendance record."
            </p>
          </div>

          <div className="p-4 rounded-xl bg-[#F8FAFC] border border-slate-200 text-xs space-y-1">
            <span className="font-bold text-slate-800 block">Upcoming Parent-Teacher Meeting</span>
            <p className="text-slate-600">Saturday, December 20, 2026 (09:00 AM – 01:00 PM) at Main Campus Latifabad.</p>
          </div>
        </div>
      </div>

      {/* Modals */}
      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
        data={getReportCardByName(currentChild.name)}
      />

      <FeeChallanModal
        isOpen={showChallan}
        onClose={() => setShowChallan(false)}
        invoiceNumber="INV-202610-0001"
      />
    </div>
  );
};
