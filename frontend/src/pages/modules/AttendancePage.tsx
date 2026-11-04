import React, { useState } from 'react';
import { 
  CheckSquare, 
  Download
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { MarkAttendanceModal } from '../../components/modals/MarkAttendanceModal';
import { apiClient } from '../../api/client';

export const AttendancePage: React.FC = () => {
  const { user } = useAuth();
  const [showMarkModal, setShowMarkModal] = useState(false);
  const [exporting, setExporting] = useState(false);

  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const allAttendance = [
    { date: '2026-10-28', student: 'Ali Khan', roll: '01', class: 'Class 9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time for assembly' },
    { date: '2026-10-28', student: 'Shahid Afridi', roll: '02', class: 'Class 9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time' },
    { date: '2026-10-28', student: 'Ayesha Siddiqui', roll: '03', class: 'Class 9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time' },
    { date: '2026-10-27', student: 'Ali Khan', roll: '01', class: 'Class 9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'Physics lab demonstration' },
    { date: '2026-10-27', student: 'Shahid Afridi', roll: '02', class: 'Class 9-A', status: 'LATE', badge: 'bg-amber-100 text-amber-800', remark: 'Autobahn traffic delay' },
    { date: '2026-10-26', student: 'Ali Khan', roll: '01', class: 'Class 9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'Regular classroom session' },
    { date: '2026-10-26', student: 'Ayesha Siddiqui', roll: '03', class: 'Class 9-A', status: 'EXCUSED', badge: 'bg-blue-100 text-blue-800', remark: 'Medical clinic appointment' },
  ];

  const attendance = isStudent 
    ? allAttendance.filter(a => a.student === (user?.fullName || 'Ali Khan'))
    : (isParent ? allAttendance.filter(a => a.student === 'Ali Khan') : allAttendance);

  const handleExportAttendance = async () => {
    setExporting(true);
    try {
      const res = await apiClient.get('/reports/attendance/export-csv', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'text/csv' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'bluebells_attendance_log.csv');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Failed to export attendance CSV', err);
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
            Current Rate: {isStudent ? '96.2%' : '94.8% (Campus Target Met)'}
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            {isStudent ? 'My Attendance Record' : (isParent ? 'Student Attendance Tracking' : 'Daily Roll-Call & Attendance Logs')}
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            {isStudent 
              ? 'Certified classroom attendance log verified by Class Teacher (Sir Ahmed Khan).' 
              : 'Real-time morning roll-call, biometric sync, and attendance correction logs for Latifabad Campus.'}
          </p>
        </div>

        {!isStudent && !isParent && (
          <div className="flex flex-wrap items-center gap-2.5">
            <button 
              onClick={handleExportAttendance}
              disabled={exporting}
              className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5 disabled:opacity-50"
            >
              <Download className="w-3.5 h-3.5" />
              <span>{exporting ? 'Exporting...' : 'Export Attendance (CSV)'}</span>
            </button>
            <button 
              onClick={() => setShowMarkModal(true)}
              className="px-4 py-2 bg-[#123B70] hover:bg-[#2167B2] text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
            >
              <CheckSquare className="w-4 h-4" />
              <span>Mark Today's Attendance</span>
            </button>
          </div>
        )}
      </div>

      {/* Attendance Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 sm:p-5 border-b border-slate-200 bg-[#F8FAFC]">
          <h2 className="text-sm font-bold text-slate-800">
            {isStudent ? 'Personal Attendance History' : 'Recent Daily Roll-Call Logs'}
          </h2>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3.5">Date</th>
                <th className="p-3.5">Student Name</th>
                <th className="p-3.5">Roll #</th>
                <th className="p-3.5">Section</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5">Teacher Remark</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {attendance.map((att, idx) => (
                <tr key={idx} className="hover:bg-slate-50/70 transition-colors">
                  <td className="p-3.5 font-mono text-slate-600 font-medium">{att.date}</td>
                  <td className="p-3.5 font-bold text-slate-800">{att.student}</td>
                  <td className="p-3.5 font-mono font-bold text-brand-800">{att.roll}</td>
                  <td className="p-3.5 font-medium text-slate-700">{att.class}</td>
                  <td className="p-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full font-bold text-[10px] ${att.badge}`}>
                      {att.status}
                    </span>
                  </td>
                  <td className="p-3.5 text-slate-500">{att.remark}</td>
                </tr>
              ))}
            </tbody>
          </table>
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
