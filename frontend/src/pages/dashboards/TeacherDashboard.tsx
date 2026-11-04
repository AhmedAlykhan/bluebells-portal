import React, { useState } from 'react';
import { 
  Users, 
  Calendar, 
  CheckSquare, 
  BookOpen, 
  Award, 
  FileCheck
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';
import { Link } from 'react-router-dom';

export const TeacherDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showMarkAttendance, setShowMarkAttendance] = useState(false);

  const teacherName = user?.fullName || 'Sir Ahmed Khan';
  const designation = 'Senior Faculty - Mathematics (Secondary)';

  const stats = [
    { label: 'Assigned Classes', value: '2 Sections', sub: 'Class 9-A & Class 10-A', icon: Users, color: 'text-blue-600 bg-blue-50' },
    { label: 'Today Attendance', value: '96.4%', sub: 'Class 9-A recorded', icon: CheckSquare, color: 'text-emerald-600 bg-emerald-50' },
    { label: 'Coursework Pending', value: '4 Submissions', sub: 'Matric Algebra homework', icon: BookOpen, color: 'text-amber-600 bg-amber-50' },
    { label: 'Weekly Periods', value: '24 Periods', sub: 'Sindh Textbook Board', icon: Calendar, color: 'text-purple-600 bg-purple-50' },
  ];

  const teachingSchedule = [
    { period: 'Period 1 (08:30 - 09:15)', classTitle: 'Class 9-A (Matric Part I)', subject: 'Mathematics - Quadratic Equations', room: 'Room 201', attendanceRecorded: true },
    { period: 'Period 2 (09:15 - 10:00)', classTitle: 'Class 10-A (Matric Part II)', subject: 'Mathematics - Trigonometry', room: 'Room 301', attendanceRecorded: false },
    { period: 'Period 4 (11:15 - 12:00)', classTitle: 'Class 9-B (Matric Part I)', subject: 'Mathematics - Matrices', room: 'Room 202', attendanceRecorded: false },
    { period: 'Period 6 (13:15 - 14:00)', classTitle: 'Class 10-A (Matric Part II)', subject: 'Math Tutorial / Problem Solving', room: 'Room 301', attendanceRecorded: false },
  ];

  return (
    <div className="space-y-6">
      {/* Teacher Welcome Banner */}
      <div className="bg-gradient-to-r from-teal-900 to-emerald-800 rounded-2xl p-6 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-white/20 text-white mb-2 inline-block">
            Faculty & Teacher Workspace
          </span>
          <h2 className="text-2xl font-bold tracking-tight">
            Welcome, {teacherName}
          </h2>
          <p className="text-emerald-100 text-sm mt-1 max-w-2xl">
            {designation} • Latifabad Campus, Hyderabad. Classroom management, daily attendance, and coursework grading.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setShowMarkAttendance(true)}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-white text-emerald-950 font-semibold rounded-lg shadow text-xs hover:bg-emerald-50 transition-colors"
          >
            <CheckSquare className="w-4 h-4 text-emerald-600" />
            <span>Mark Today's Attendance</span>
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
                <p className="text-xs text-slate-500 mt-1">{stat.sub}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Today's Teaching Periods & Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Calendar className="w-5 h-5 text-brand-700" />
              Today's Teaching Schedule
            </h3>
            <span className="text-xs text-slate-500 font-mono">
              Latifabad Secondary Wing
            </span>
          </div>

          <div className="divide-y divide-slate-100 text-xs">
            {teachingSchedule.map((item, idx) => (
              <div key={idx} className="py-3.5 flex items-center justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-slate-800 text-sm">{item.classTitle}</span>
                    <span className="px-2 py-0.5 rounded bg-slate-100 font-mono text-[10px] text-slate-600">
                      {item.room}
                    </span>
                  </div>
                  <p className="text-slate-600 mt-0.5">{item.subject}</p>
                  <p className="text-slate-400 font-mono text-[11px] mt-0.5">{item.period}</p>
                </div>

                <div className="flex items-center gap-2">
                  {item.attendanceRecorded ? (
                    <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 font-bold text-[10px]">
                      Attendance Marked
                    </span>
                  ) : (
                    <button
                      onClick={() => setShowMarkAttendance(true)}
                      className="px-2.5 py-1 rounded bg-brand-50 hover:bg-brand-100 text-brand-800 font-semibold text-[11px] transition-colors"
                    >
                      Mark Attendance
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Academic Actions */}
        <div className="space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
            <h3 className="text-base font-bold text-slate-800 mb-4 flex items-center gap-2">
              <FileCheck className="w-5 h-5 text-brand-700" />
              Faculty Quick Tasks
            </h3>
            <div className="space-y-3 text-xs">
              <Link
                to="/examinations"
                className="p-3 rounded-lg border border-slate-200 hover:border-brand-300 hover:bg-brand-50/20 block transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-slate-800">Enter Midterm Marks</span>
                  <Award className="w-4 h-4 text-amber-500" />
                </div>
                <p className="text-slate-500 mt-0.5 text-[11px]">Sindh Board Matriculation Mathematics tabulations</p>
              </Link>

              <Link
                to="/lms"
                className="p-3 rounded-lg border border-slate-200 hover:border-brand-300 hover:bg-brand-50/20 block transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-slate-800">Grade Coursework</span>
                  <BookOpen className="w-4 h-4 text-blue-500" />
                </div>
                <p className="text-slate-500 mt-0.5 text-[11px]">Review 4 pending homework submissions</p>
              </Link>

              <Link
                to="/attendance"
                className="p-3 rounded-lg border border-slate-200 hover:border-brand-300 hover:bg-brand-50/20 block transition-all"
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-slate-800">Class Attendance Registers</span>
                  <CheckSquare className="w-4 h-4 text-emerald-500" />
                </div>
                <p className="text-slate-500 mt-0.5 text-[11px]">Monthly registers and percentage sheets</p>
              </Link>
            </div>
          </div>
        </div>
      </div>

      <MarkAttendanceModal
        isOpen={showMarkAttendance}
        onClose={() => setShowMarkAttendance(false)}
        onSuccess={() => {}}
      />
    </div>
  );
};
