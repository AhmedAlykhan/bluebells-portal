import React, { useState } from 'react';
import { X, Check, CheckCheck, AlertCircle, Loader2 } from 'lucide-react';
import { apiClient } from '../../api/client';

interface MarkAttendanceModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

interface StudentItem {
  enrollmentId: number;
  rollNumber: string;
  name: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
  remarks: string;
}

const SECTION_STUDENTS: Record<number, StudentItem[]> = {
  1: [
    { enrollmentId: 1, rollNumber: '01', name: 'Ali Khan', status: 'PRESENT', remarks: 'On time' },
    { enrollmentId: 2, rollNumber: '02', name: 'Shahid Afridi', status: 'PRESENT', remarks: '' },
    { enrollmentId: 3, rollNumber: '03', name: 'Ayesha Siddiqui', status: 'PRESENT', remarks: '' },
  ],
  2: [
    { enrollmentId: 4, rollNumber: '01', name: 'Bilal Hassan', status: 'PRESENT', remarks: '' },
  ],
  3: [
    { enrollmentId: 5, rollNumber: '01', name: 'Sara Khan', status: 'PRESENT', remarks: '' },
  ]
};

export const MarkAttendanceModal: React.FC<MarkAttendanceModalProps> = ({
  isOpen,
  onClose,
  onSuccess
}) => {
  const [sectionId, setSectionId] = useState<number>(1);
  const [attendanceDate, setAttendanceDate] = useState<string>(
    new Date().toISOString().split('T')[0]
  );
  const [students, setStudents] = useState<StudentItem[]>(SECTION_STUDENTS[1]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSectionChange = (newSecId: number) => {
    setSectionId(newSecId);
    setStudents(SECTION_STUDENTS[newSecId] || []);
  };

  const handleStatusChange = (enrollmentId: number, status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED') => {
    setStudents(prev => prev.map(s => s.enrollmentId === enrollmentId ? { ...s, status } : s));
  };

  const markAll = (status: 'PRESENT' | 'ABSENT') => {
    setStudents(prev => prev.map(s => ({ ...s, status })));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const payload = {
      sectionId: Number(sectionId),
      attendanceDate,
      items: students.map(s => ({
        enrollmentId: s.enrollmentId,
        status: s.status,
        remarks: s.remarks || (s.status === 'PRESENT' ? 'Recorded via portal' : s.status)
      }))
    };

    try {
      await apiClient.post('/attendance/mark', payload);
      setSuccess(`Attendance recorded successfully for ${students.length} students on ${attendanceDate}.`);
      setTimeout(() => {
        setSuccess(null);
        onSuccess();
        onClose();
      }, 1500);
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { detail?: string } } })?.response?.data?.detail 
        || 'Attendance submission failed. If already recorded, use correction workflow.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-2xl rounded-2xl shadow-2xl border border-slate-200 overflow-hidden animate-in fade-in zoom-in-95">
        <div className="px-6 py-4 bg-brand-900 text-white flex items-center justify-between">
          <div>
            <h3 className="font-bold text-base">Mark Daily Attendance</h3>
            <p className="text-xs text-brand-200">Latifabad Campus • Classroom Register</p>
          </div>
          <button onClick={onClose} className="text-white/70 hover:text-white p-1 rounded-lg">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-xs text-red-700 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-lg text-xs text-emerald-800 flex items-center gap-2">
              <Check className="w-4 h-4 flex-shrink-0 text-emerald-600" />
              <span>{success}</span>
            </div>
          )}

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
            <div>
              <label className="block font-semibold text-slate-700 mb-1">Class & Section</label>
              <select
                value={sectionId}
                onChange={(e) => handleSectionChange(Number(e.target.value))}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-brand-500 focus:outline-none"
              >
                <option value={1}>Class 9 - Section A (Room 201)</option>
                <option value={2}>Class 9 - Section B (Room 202)</option>
                <option value={3}>Class 8 - Section Rose (Room 101)</option>
              </select>
            </div>

            <div>
              <label className="block font-semibold text-slate-700 mb-1">Date</label>
              <input
                type="date"
                required
                value={attendanceDate}
                onChange={(e) => setAttendanceDate(e.target.value)}
                className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-brand-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="flex items-center justify-between pt-2">
            <span className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Enrolled Students ({students.length})
            </span>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => markAll('PRESENT')}
                className="px-2.5 py-1 bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200 rounded text-[11px] font-semibold flex items-center gap-1 transition-colors"
              >
                <CheckCheck className="w-3.5 h-3.5" />
                <span>Mark All Present</span>
              </button>
            </div>
          </div>

          <div className="border border-slate-200 rounded-xl overflow-hidden max-h-60 overflow-y-auto divide-y divide-slate-100 text-xs">
            {students.map((student) => (
              <div key={student.enrollmentId} className="p-3 flex items-center justify-between bg-white hover:bg-slate-50 transition-colors">
                <div>
                  <span className="font-mono text-slate-400 mr-2">#{student.rollNumber}</span>
                  <span className="font-semibold text-slate-800">{student.name}</span>
                </div>
                <div className="flex gap-1.5">
                  {(['PRESENT', 'ABSENT', 'LATE', 'EXCUSED'] as const).map((st) => {
                    const isSelected = student.status === st;
                    const colors = {
                      PRESENT: isSelected ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-emerald-50',
                      ABSENT: isSelected ? 'bg-red-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-red-50',
                      LATE: isSelected ? 'bg-amber-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-amber-50',
                      EXCUSED: isSelected ? 'bg-blue-600 text-white' : 'bg-slate-100 text-slate-600 hover:bg-blue-50',
                    }[st];
                    return (
                      <button
                        key={st}
                        type="button"
                        onClick={() => handleStatusChange(student.enrollmentId, st)}
                        className={`px-2 py-1 rounded text-[10px] font-bold uppercase transition-colors ${colors}`}
                      >
                        {st.slice(0, 3)}
                      </button>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>

          <div className="pt-4 border-t border-slate-200 flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg text-xs font-semibold hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-2 bg-brand-700 hover:bg-brand-800 text-white rounded-lg text-xs font-semibold shadow transition-colors flex items-center gap-2 disabled:opacity-50"
            >
              {loading ? (
                <>
                  <Loader2 className="w-3.5 h-3.5 animate-spin" />
                  <span>Submitting Attendance...</span>
                </>
              ) : (
                <span>Confirm & Submit Attendance</span>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
