import React, { useState } from 'react';
import { 
  Calendar, 
  Clock, 
  Users, 
  CheckSquare
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';

export const VicePrincipalDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showMarkAttendance, setShowMarkAttendance] = useState(false);

  const todayPeriods = [
    { period: 'Period 1 (08:30 - 09:15)', class: 'Class 9-A', subject: 'Mathematics', teacher: 'Sir Ahmed Khan', room: 'Room 201', status: 'IN_SESSION' },
    { period: 'Period 1 (08:30 - 09:15)', class: 'Class 10-A', subject: 'Physics', teacher: 'Miss Fatima Shah', room: 'Room 301', status: 'IN_SESSION' },
    { period: 'Period 2 (09:15 - 10:00)', class: 'Class 9-A', subject: 'Physics Lab', teacher: 'Miss Fatima Shah', room: 'Physics Lab', status: 'UPCOMING' },
    { period: 'Period 2 (09:15 - 10:00)', class: 'Class 8 Rose', subject: 'English', teacher: 'Miss Ayesha', room: 'Room 101', status: 'UPCOMING' },
  ];

  const facultyCoverage = [
    { name: 'Sir Ahmed Khan', subject: 'Mathematics (9-A, 10-A)', attendance: 'Present', statusBadge: 'bg-emerald-100 text-emerald-800' },
    { name: 'Miss Fatima Shah', subject: 'Physics & Lab (9-A, 10-A)', attendance: 'Present', statusBadge: 'bg-emerald-100 text-emerald-800' },
    { name: 'Sir Asif', subject: 'Chemistry & Computer (9-A, 8)', attendance: 'Present', statusBadge: 'bg-emerald-100 text-emerald-800' },
    { name: 'Miss Ayesha', subject: 'English Compulsory (9, 10)', attendance: 'Present', statusBadge: 'bg-emerald-100 text-emerald-800' },
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-[#1A5392] to-[#2167B2] rounded-3xl p-6 sm:p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-white/15 text-brand-100 mb-3 border border-white/20">
            <Clock className="w-3.5 h-3.5 text-brand-300" />
            <span>Academic Coordination & Daily Operations</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
            Welcome, Vice Principal {user?.fullName || 'Hana Khan'}
          </h1>
          <p className="text-brand-100 text-xs sm:text-sm mt-1.5 max-w-2xl leading-relaxed">
            Real-time bell schedule oversight, faculty period assignments, daily classroom attendance, and student discipline monitoring.
          </p>
        </div>

        <button
          onClick={() => setShowMarkAttendance(true)}
          className="px-4 py-2.5 bg-white text-[#123B70] hover:bg-brand-50 font-bold rounded-xl shadow text-xs transition-all flex items-center gap-2"
        >
          <CheckSquare className="w-4 h-4 text-brand-700" />
          <span>Mark / Review Attendance</span>
        </button>
      </div>

      {/* Quick Metrics */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Scheduled Periods Today</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-2">24 Periods</div>
          <p className="text-xs text-emerald-600 font-semibold mt-1">100% Teacher Coverage</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Classrooms Active</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-2">8 Sections</div>
          <p className="text-xs text-slate-500 mt-1">Early Years through Matric</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Teacher Presence</span>
          <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-2">38 / 38</div>
          <p className="text-xs text-slate-500 mt-1">No Substitutions Needed</p>
        </div>

        <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Class Roll-Call Rate</span>
          <div className="text-2xl sm:text-3xl font-black text-brand-700 mt-2">95.4%</div>
          <p className="text-xs text-slate-500 mt-1">459 / 482 Present</p>
        </div>
      </div>

      {/* Timetable Grid & Faculty Coverage */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-bold text-slate-800 flex items-center gap-2">
              <Calendar className="w-4 h-4 text-brand-700" />
              <span>Today's Live Bell Schedule</span>
            </h2>
            <span className="text-xs text-slate-400 font-mono">Current Time: 09:10 AM</span>
          </div>

          <div className="space-y-3">
            {todayPeriods.map((slot, idx) => (
              <div key={idx} className="p-3.5 rounded-xl border border-slate-200 bg-[#F8FAFC] flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                <div>
                  <span className="text-xs font-bold text-brand-800">{slot.period}</span>
                  <h3 className="text-sm font-bold text-slate-800 mt-0.5">{slot.subject} • {slot.class}</h3>
                  <p className="text-xs text-slate-500">Instructor: {slot.teacher} • Location: {slot.room}</p>
                </div>
                <span className={`px-2.5 py-1 rounded-full text-xs font-bold self-start sm:self-center ${
                  slot.status === 'IN_SESSION' ? 'bg-emerald-100 text-emerald-800 animate-pulse' : 'bg-slate-200 text-slate-700'
                }`}>
                  {slot.status === 'IN_SESSION' ? 'Active Now' : 'Upcoming'}
                </span>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
          <h2 className="text-sm font-bold text-slate-800 mb-4 flex items-center gap-2">
            <Users className="w-4 h-4 text-brand-700" />
            <span>Faculty Roster & Attendance</span>
          </h2>
          <div className="divide-y divide-slate-100 text-xs">
            {facultyCoverage.map((fac, idx) => (
              <div key={idx} className="py-2.5 flex items-center justify-between">
                <div>
                  <p className="font-bold text-slate-800">{fac.name}</p>
                  <p className="text-[11px] text-slate-500">{fac.subject}</p>
                </div>
                <span className={`px-2 py-0.5 rounded-full font-semibold ${fac.statusBadge}`}>
                  {fac.attendance}
                </span>
              </div>
            ))}
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
