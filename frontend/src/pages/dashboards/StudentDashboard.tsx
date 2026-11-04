import React, { useState, useEffect } from 'react';
import { 
  GraduationCap, 
  Award, 
  Clock, 
  Sparkles, 
  ChevronRight,
  Printer
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { FeeChallanModal } from '../../components/modals/FeeChallanModal';
import { getReportCardByName } from '../../data/reportCardsData';
import { apiClient } from '../../api/client';

export const StudentDashboard: React.FC = () => {
  useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showChallan, setShowChallan] = useState(false);
  const [studentInfo, setStudentInfo] = useState({
    name: 'Ali Khan',
    rollNumber: '01',
    className: 'Class 9',
    sectionName: 'Section A',
    admissionNumber: 'BSS-2024-0104',
    attendanceRate: '96.2%',
    midtermGrade: 'A-1 (89.6%)',
    feeStatus: 'PAID',
  });

  useEffect(() => {
    apiClient.get('/students/me')
      .then(res => {
        if (res.data?.data) {
          const st = res.data.data;
          setStudentInfo(prev => ({
            ...prev,
            name: st.fullName || `${st.firstName || ''} ${st.lastName || ''}`.trim() || 'Ali Khan',
            rollNumber: st.rollNumber || '01',
            className: st.activeClassName || 'Class 9',
            sectionName: st.activeSectionName || 'Section A',
            admissionNumber: st.admissionNumber || 'BSS-2024-0104',
          }));
        }
      })
      .catch(() => {});
  }, []);

  const todayClasses = [
    { period: 'Period 1 (08:30 - 09:15)', subject: 'Mathematics', teacher: 'Sir Ahmed Khan', room: 'Room 201', active: false },
    { period: 'Period 2 (09:15 - 10:00)', subject: 'Physics', teacher: 'Miss Fatima Shah', room: 'Room 201', active: true },
    { period: 'Break (10:00 - 10:30)', subject: 'Refreshment Break', teacher: 'Campus Grounds', room: 'Courtyard', active: false },
    { period: 'Period 3 (10:30 - 11:15)', subject: 'English Compulsory', teacher: 'Miss Ayesha', room: 'Room 201', active: false },
    { period: 'Period 4 (11:15 - 12:00)', subject: 'Computer Science Lab', teacher: 'Sir Asif', room: 'IT Lab 2', active: false },
  ];

  const learningMilestones = [
    { subject: 'Mathematics (9-A)', progress: 75, completed: '12 / 16 Lessons', badge: 'Math Champion', color: 'bg-blue-600' },
    { subject: 'Physics (9-A)', progress: 68, completed: '10 / 15 Lessons', badge: 'Lab Scholar', color: 'bg-emerald-600' },
    { subject: 'Computer Science (9-A)', progress: 85, completed: '14 / 16 Lessons', badge: 'Coder Elite', color: 'bg-purple-600' },
  ];

  return (
    <div className="space-y-6">
      {/* Student Welcome Hero Banner */}
      <div className="bg-gradient-to-r from-[#123B70] via-[#1A5392] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <GraduationCap className="w-3.5 h-3.5 text-brand-300" />
            <span>Student Portal</span>
            <span>•</span>
            <span>{studentInfo.className} - {studentInfo.sectionName}</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome back, {studentInfo.name}!
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            Roll #{studentInfo.rollNumber} • Reg: {studentInfo.admissionNumber} • Latifabad Campus. Track your daily lessons, submit homework photos, review exam results, and view tuition vouchers.
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={() => setShowReportCard(true)}
            className="px-4 py-2.5 bg-white text-[#123B70] hover:bg-brand-50 font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
          >
            <Award className="w-4 h-4 text-amber-500" />
            <span>View Sindh Board Report Card</span>
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

      {/* Student Key Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">My Attendance Rate</span>
          <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-2">{studentInfo.attendanceRate}</div>
          <p className="text-xs text-slate-500 mt-1">Certified by Sir Ahmed Khan</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Midterm Position</span>
          <div className="text-2xl sm:text-3xl font-black text-brand-700 mt-2">1st Position</div>
          <p className="text-xs text-emerald-600 font-semibold mt-1">Grade {studentInfo.midtermGrade}</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">October Fee Status</span>
          <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-2">Rs. 0.00</div>
          <p className="text-xs text-emerald-600 font-semibold mt-1">October 2026 Cleared (PAID)</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">LMS Homework</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-2">1 Due Soon</div>
          <p className="text-xs text-amber-700 font-semibold mt-1">Ch 3 Logarithms (Due Oct 30)</p>
        </div>
      </div>

      {/* Distinctive Feature: Bluebells Learning Journey */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-brand-100 shadow-md">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-6 border-b border-slate-100 gap-2">
          <div>
            <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-brand-50 text-brand-700 text-xs font-bold">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Bluebells Learning Journey</span>
            </div>
            <h2 className="text-lg font-bold text-[#123B70] mt-1">Subject Mastery & Academic Milestones</h2>
            <p className="text-xs text-slate-500">Based on verified curriculum completion and graded homework</p>
          </div>
          <a
            href="/lms"
            className="text-xs font-bold text-brand-700 hover:text-brand-900 inline-flex items-center gap-1"
          >
            <span>Open All Courses</span>
            <ChevronRight className="w-4 h-4" />
          </a>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-6">
          {learningMilestones.map((item, idx) => (
            <div key={idx} className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-200 space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="font-bold text-xs text-slate-800">{item.subject}</h3>
                <span className="px-2 py-0.5 rounded-full bg-white text-[10px] font-bold text-brand-700 border border-slate-200">
                  {item.badge}
                </span>
              </div>
              <div>
                <div className="flex justify-between text-xs text-slate-600 mb-1">
                  <span>Progress</span>
                  <span className="font-bold text-slate-800">{item.progress}%</span>
                </div>
                <div className="w-full h-2 rounded-full bg-slate-200 overflow-hidden">
                  <div className={`h-full rounded-full ${item.color}`} style={{ width: `${item.progress}%` }} />
                </div>
              </div>
              <p className="text-[11px] text-slate-500">{item.completed} completed this term</p>
            </div>
          ))}
        </div>
      </div>

      {/* Distinctive Feature: Weekly Learning Snapshot & Daily Schedule */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Today's Bell Schedule */}
        <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
              <Clock className="w-4 h-4 text-brand-700" />
              <span>Today's Classroom Bell Schedule</span>
            </h2>
            <span className="text-xs text-slate-500 font-semibold">{studentInfo.className} - {studentInfo.sectionName}</span>
          </div>

          <div className="space-y-2.5">
            {todayClasses.map((c, idx) => (
              <div 
                key={idx}
                className={`p-3 rounded-xl border text-xs flex items-center justify-between ${
                  c.active ? 'bg-blue-50/80 border-brand-200 font-semibold' : 'bg-[#F8FAFC] border-slate-200'
                }`}
              >
                <div>
                  <span className="text-[11px] font-bold text-brand-800 block">{c.period}</span>
                  <span className="text-sm font-bold text-slate-800">{c.subject}</span>
                  <span className="text-[11px] text-slate-500 block">{c.teacher} • {c.room}</span>
                </div>
                {c.active && (
                  <span className="px-2.5 py-1 rounded-full text-[10px] font-bold bg-brand-600 text-white animate-pulse">
                    Current Period
                  </span>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Weekly Learning Snapshot */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <div className="flex items-center gap-2 text-sm font-bold text-slate-800">
            <Award className="w-4 h-4 text-amber-500" />
            <span>Weekly Learning Snapshot</span>
          </div>

          <div className="space-y-3 text-xs">
            <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200">
              <p className="font-bold text-emerald-900">100% Attendance This Week</p>
              <p className="text-emerald-700 text-[11px] mt-0.5">Prompt arrival in all 5 morning assembly sessions.</p>
            </div>

            <div className="p-3 rounded-xl bg-blue-50 border border-blue-200">
              <p className="font-bold text-blue-900">Latest Graded Homework</p>
              <p className="text-blue-800 text-[11px] mt-0.5">Physics Lab Report: <strong>9.5 / 10</strong></p>
              <p className="text-slate-600 italic text-[10px] mt-1">"Excellent neatness and accurate ray diagrams." — Miss Fatima</p>
            </div>

            <div className="p-3 rounded-xl bg-[#F8FAFC] border border-slate-200">
              <p className="font-bold text-slate-800">Suggested Focus Area</p>
              <p className="text-slate-600 text-[11px] mt-0.5">Revise Logarithm Law IV exercises ahead of upcoming Monday class test.</p>
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
        data={getReportCardByName('Ali Khan')}
      />

      <FeeChallanModal
        isOpen={showChallan}
        onClose={() => setShowChallan(false)}
        invoiceNumber="INV-202610-0001"
      />
    </div>
  );
};
