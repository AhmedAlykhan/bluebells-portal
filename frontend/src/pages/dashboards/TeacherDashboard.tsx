import React, { useState } from 'react';
import { 
  BookOpen, 
  Users, 
  CheckSquare, 
  FileText, 
  Clock, 
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';

export const TeacherDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showMarkModal, setShowMarkModal] = useState(false);

  const myClasses = [
    { name: 'Class 9-A', subject: 'Mathematics (Sindh Board)', room: 'Room 201', count: 32, attendance: '96.2%', isClassTeacher: true },
    { name: 'Class 10-A', subject: 'Mathematics (Sindh Board)', room: 'Room 301', count: 34, attendance: '94.8%', isClassTeacher: false },
  ];

  const todaySchedule = [
    { period: 'Period 1 (08:30 - 09:15)', class: 'Class 9-A', subject: 'Mathematics • Ch 3 Logarithms', status: 'COMPLETED' },
    { period: 'Period 2 (09:15 - 10:00)', class: 'Class 10-A', subject: 'Mathematics • Ch 1 Matrices', status: 'IN_SESSION' },
    { period: 'Period 4 (11:15 - 12:00)', class: 'Class 9-A', subject: 'Mathematics Problem Solving Lab', status: 'UPCOMING' },
  ];

  const pendingGrading = [
    { title: 'Ch 3 Logarithms - Exercise 3.2', class: 'Class 9-A', submitted: 28, total: 32, due: '2026-10-30', pendingCount: 4 },
    { title: 'Ch 1 Algebraic Formulas Proofs', class: 'Class 10-A', submitted: 31, total: 34, due: '2026-10-28', pendingCount: 3 },
  ];

  return (
    <div className="space-y-6">
      {/* Teacher Workspace Header */}
      <div className="bg-gradient-to-r from-[#123B70] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <BookOpen className="w-3.5 h-3.5 text-brand-300" />
            <span>Faculty Workspace • Department of Mathematics</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome, {user?.fullName || 'Sir Ahmed Khan'}
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            Class teacher for <strong>Class 9-A</strong>. Manage student attendance, Sindh Board coursework, homework photo submissions, and midterm examinations.
          </p>
        </div>

        <div className="flex flex-wrap gap-2.5">
          <button
            onClick={() => setShowMarkModal(true)}
            className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl shadow text-xs transition-all flex items-center gap-2"
          >
            <CheckSquare className="w-4 h-4" />
            <span>Mark Today's Attendance</span>
          </button>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">My Students</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-2">66 Students</div>
          <p className="text-xs text-brand-600 font-semibold mt-1">Across 2 Secondary Sections</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Today's Class Attendance</span>
          <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-2">96.2%</div>
          <p className="text-xs text-slate-500 mt-1">31 / 32 Present in 9-A</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Homework to Grade</span>
          <div className="text-2xl sm:text-3xl font-black text-amber-700 mt-2">7 Submissions</div>
          <p className="text-xs text-slate-500 mt-1">Multi-photo assignments pending</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Weekly Teaching Periods</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-2">12 Periods</div>
          <p className="text-xs text-slate-500 mt-1">Mathematics Curriculum</p>
        </div>
      </div>

      {/* Main Grid: Teaching Schedule & Assigned Classes */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Today's Schedule */}
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
              <Clock className="w-4 h-4 text-brand-700" />
              <span>Today's Teaching Schedule (Monday)</span>
            </h2>
            <span className="text-xs text-slate-500 font-semibold">Active Term 1</span>
          </div>

          <div className="space-y-3">
            {todaySchedule.map((slot, idx) => (
              <div key={idx} className="p-3.5 rounded-xl border border-slate-200 bg-[#F8FAFC] flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <span className="text-xs font-bold text-brand-800">{slot.period}</span>
                  <p className="text-sm font-bold text-slate-800 mt-0.5">{slot.subject}</p>
                  <p className="text-xs text-slate-500">Class: <strong>{slot.class}</strong></p>
                </div>
                <span className={`px-2.5 py-1 rounded-full text-xs font-bold self-start sm:self-center ${
                  slot.status === 'COMPLETED' ? 'bg-slate-200 text-slate-700' :
                  slot.status === 'IN_SESSION' ? 'bg-emerald-100 text-emerald-800 animate-pulse' :
                  'bg-blue-50 text-brand-700'
                }`}>
                  {slot.status === 'COMPLETED' ? 'Completed' : slot.status === 'IN_SESSION' ? 'In Session Now' : 'Upcoming'}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Assigned Classes */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
            <Users className="w-4 h-4 text-brand-700" />
            <span>My Assigned Classes</span>
          </h2>

          <div className="space-y-3">
            {myClasses.map((cls, idx) => (
              <div key={idx} className="p-3.5 rounded-xl border border-slate-200 bg-[#F8FAFC] text-xs space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-slate-800 text-sm">{cls.name}</span>
                  {cls.isClassTeacher && (
                    <span className="px-2 py-0.5 rounded bg-brand-100 text-brand-800 font-bold text-[10px]">
                      Class Teacher
                    </span>
                  )}
                </div>
                <p className="text-slate-600 font-medium">{cls.subject}</p>
                <div className="flex justify-between text-slate-500 pt-1 border-t border-slate-200/60">
                  <span>Capacity: {cls.count} Students</span>
                  <span>Att: <strong>{cls.attendance}</strong></span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Homework Grading Queue */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
        <h2 className="text-sm font-bold text-slate-800 mb-3 flex items-center gap-2">
          <FileText className="w-4 h-4 text-brand-700" />
          <span>Active Assignment Submissions & Grading Queue</span>
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {pendingGrading.map((item, idx) => (
            <div key={idx} className="p-4 rounded-xl border border-slate-200 bg-[#F8FAFC] flex flex-col justify-between">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-brand-700">{item.class}</span>
                <h3 className="text-sm font-bold text-slate-800 mt-1">{item.title}</h3>
                <p className="text-xs text-slate-500 mt-0.5">Due: {item.due} • {item.submitted}/{item.total} submitted</p>
              </div>
              <div className="mt-4 pt-3 border-t border-slate-200 flex items-center justify-between">
                <span className="text-xs font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">
                  {item.pendingCount} Submissions Awaiting Grading
                </span>
                <a
                  href="/lms"
                  className="text-xs font-bold text-brand-700 hover:text-brand-900 inline-flex items-center gap-1"
                >
                  <span>Open LMS</span>
                  <ChevronRight className="w-3.5 h-3.5" />
                </a>
              </div>
            </div>
          ))}
        </div>
      </div>

      <MarkAttendanceModal
        isOpen={showMarkModal}
        onClose={() => setShowMarkModal(false)}
        onSuccess={() => {}}
      />
    </div>
  );
};
