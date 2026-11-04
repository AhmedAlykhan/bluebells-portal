import React, { useState, useEffect } from 'react';
import { 
  CheckCircle2, 
  CreditCard, 
  BookOpen, 
  Calendar, 
  Award, 
  FileText,
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { apiClient } from '../../api/client';
import { Link } from 'react-router-dom';

export const StudentDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [studentProfile, setStudentProfile] = useState<{
    fullName?: string;
    className?: string;
    rollNumber?: string;
  }>({
    fullName: user?.fullName || 'Ali Khan',
    className: 'Class 9-A (Matriculation Science)',
    rollNumber: '09-A-12',
  });

  useEffect(() => {
    async function fetchProfile() {
      try {
        const res = await apiClient.get('/students/me');
        if (res.data?.data) {
          const d = res.data.data;
          setStudentProfile({
            fullName: d.fullName || user?.fullName || 'Ali Khan',
            className: d.activeClassName 
              ? `${d.activeClassName}${d.activeSectionName ? ' - ' + d.activeSectionName : ''}` 
              : 'Class 9-A (Matriculation Science)',
            rollNumber: d.rollNumber || '01',
          });
        }
      } catch {
        // Fallback to initial values
      }
    }
    fetchProfile();
  }, [user]);

  const studentName = studentProfile.fullName || user?.fullName || 'Ali Khan';
  const rollNumber = studentProfile.rollNumber || '01';
  const className = studentProfile.className || 'Class 9-A (Matriculation Science)';

  const stats = [
    { label: 'My Attendance Rate', value: '96.2%', sub: '50 of 52 days present', icon: CheckCircle2, color: 'text-emerald-600 bg-emerald-50' },
    { label: 'Midterm Grade', value: 'Grade A-1', sub: '89.6% aggregate distinction', icon: Award, color: 'text-amber-600 bg-amber-50' },
    { label: 'October Fee Status', value: 'PAID', sub: 'Voucher # INV-202610-0104', icon: CreditCard, color: 'text-blue-600 bg-blue-50' },
    { label: 'Active Courses', value: '8 Subjects', sub: 'Sindh Textbook Board', icon: BookOpen, color: 'text-purple-600 bg-purple-50' },
  ];

  const todaySchedule = [
    { period: 'Period 1 (08:30 - 09:15)', subject: 'Mathematics', teacher: 'Sir Ahmed Khan', room: 'Room 201', active: true },
    { period: 'Period 2 (09:15 - 10:00)', subject: 'Physics', teacher: 'Miss Fatima Shah', room: 'Physics Lab', active: false },
    { period: 'Period 3 (10:00 - 10:45)', subject: 'English Compulsory', teacher: 'Sir Bilal', room: 'Room 201', active: false },
    { period: 'Recess (10:45 - 11:15)', subject: 'Morning Break & Refreshment', teacher: 'Campus Grounds', room: 'Cafeteria', active: false },
    { period: 'Period 4 (11:15 - 12:00)', subject: 'Computer Science', teacher: 'Sir Asif', room: 'Computer Lab 1', active: false },
    { period: 'Period 5 (12:00 - 12:45)', subject: 'Sindhi / Urdu', teacher: 'Madam Rubina', room: 'Room 201', active: false },
  ];

  const recentCoursework = [
    { id: 1, title: 'Quadratic Equations Exercise 3.2', subject: 'Mathematics', dueDate: '30 Oct 2026', status: 'SUBMITTED', marks: '10/10' },
    { id: 2, title: 'Newton Laws of Motion Lab Report', subject: 'Physics', dueDate: '02 Nov 2026', status: 'PENDING', marks: '--' },
    { id: 3, title: 'Sindhi Essay: Sindhu Darya Ain Thar', subject: 'Sindhi', dueDate: '05 Nov 2026', status: 'PENDING', marks: '--' },
  ];

  return (
    <div className="space-y-6">
      {/* Student Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-900 to-indigo-800 rounded-2xl p-6 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-2">
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-white/20 text-white">
              Student Portal
            </span>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-200">
              Active Enrollment
            </span>
          </div>
          <h2 className="text-2xl font-bold tracking-tight">
            Welcome back, {studentName}
          </h2>
          <p className="text-blue-100 text-sm mt-1 max-w-2xl">
            {className} • Roll # {rollNumber} • Bluebells Schooling System Latifabad Campus
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setShowReportCard(true)}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-white text-blue-900 font-semibold rounded-lg shadow text-xs hover:bg-blue-50 transition-colors"
          >
            <Award className="w-4 h-4 text-amber-500" />
            <span>View Sindh Board Report Card</span>
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

      {/* Today Schedule & Coursework */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Today's Timetable */}
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Calendar className="w-5 h-5 text-brand-700" />
              Today's Class Schedule ({className})
            </h3>
            <Link to="/timetable" className="text-xs font-semibold text-brand-700 hover:text-brand-800 flex items-center gap-1">
              <span>Full Week Timetable</span>
              <ChevronRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="divide-y divide-slate-100">
            {todaySchedule.map((slot, idx) => (
              <div key={idx} className="py-3 flex items-center justify-between">
                <div className="flex items-start gap-3">
                  <div className={`mt-0.5 w-2 h-2 rounded-full ${slot.active ? 'bg-emerald-500 ring-4 ring-emerald-100' : 'bg-slate-300'}`} />
                  <div>
                    <p className="text-sm font-semibold text-slate-800 flex items-center gap-2">
                      <span>{slot.subject}</span>
                      {slot.active && (
                        <span className="px-1.5 py-0.2 rounded text-[10px] bg-emerald-100 text-emerald-800 font-bold uppercase">
                          In Progress
                        </span>
                      )}
                    </p>
                    <p className="text-xs text-slate-500">
                      Instructor: {slot.teacher} • Location: {slot.room}
                    </p>
                  </div>
                </div>
                <span className="text-xs font-mono font-medium text-slate-500 bg-slate-50 px-2 py-1 rounded">
                  {slot.period.split(' ')[2] || slot.period}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Links & Assignments */}
        <div className="space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
            <h3 className="text-base font-bold text-slate-800 mb-3 flex items-center gap-2">
              <FileText className="w-5 h-5 text-blue-600" />
              Recent LMS Assignments
            </h3>
            <div className="space-y-3">
              {recentCoursework.map((hw) => (
                <div key={hw.id} className="p-3 bg-slate-50 rounded-lg border border-slate-100 text-xs">
                  <div className="flex items-start justify-between gap-1">
                    <span className="font-semibold text-slate-800 leading-tight">{hw.title}</span>
                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                      hw.status === 'SUBMITTED' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'
                    }`}>
                      {hw.status}
                    </span>
                  </div>
                  <div className="mt-2 flex items-center justify-between text-slate-500 text-[11px]">
                    <span>{hw.subject}</span>
                    <span className="font-mono">Due: {hw.dueDate}</span>
                  </div>
                </div>
              ))}
            </div>
            <Link
              to="/lms"
              className="mt-4 block w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-center font-semibold text-xs rounded-lg transition-colors"
            >
              Go to LMS Homework Portal
            </Link>
          </div>

          <div className="bg-gradient-to-br from-brand-50 to-blue-50 border border-brand-200 rounded-xl p-5">
            <h4 className="font-bold text-sm text-brand-950 mb-1">Student Academic Support</h4>
            <p className="text-xs text-brand-800 mb-3 leading-relaxed">
              Have questions regarding syllabus or Board registration? Contact your class teacher or the campus coordinator.
            </p>
            <div className="flex gap-2">
              <Link
                to="/fees"
                className="inline-flex items-center gap-1 px-3 py-1.5 bg-brand-700 hover:bg-brand-800 text-white rounded-lg text-xs font-semibold shadow transition-colors"
              >
                <CreditCard className="w-3.5 h-3.5" />
                <span>My Fee Invoices</span>
              </Link>
              <Link
                to="/attendance"
                className="inline-flex items-center gap-1 px-3 py-1.5 bg-white text-slate-700 hover:bg-slate-100 border border-slate-200 rounded-lg text-xs font-semibold transition-colors"
              >
                <span>Attendance Log</span>
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Report Card Modal */}
      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
      />
    </div>
  );
};
