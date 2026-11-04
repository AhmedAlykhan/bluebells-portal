import React, { useState } from 'react';
import { Printer } from 'lucide-react';

export const TimetablePage: React.FC = () => {
  const [selectedSection, setSelectedSection] = useState('9-A');

  const schedule9A = [
    { period: 'Period 1 (08:30 - 09:15)', mon: 'Mathematics (Sir Ahmed)', tue: 'Mathematics (Sir Ahmed)', wed: 'Mathematics (Sir Ahmed)', thu: 'Mathematics (Sir Ahmed)', fri: 'Mathematics (Sir Ahmed)' },
    { period: 'Period 2 (09:15 - 10:00)', mon: 'Physics (Miss Fatima)', tue: 'Physics (Miss Fatima)', wed: 'Urdu Normal (Sir Ghulam)', thu: 'Chemistry (Sir Asif)', fri: 'Pakistan Studies' },
    { period: 'Break (10:00 - 10:30)', mon: 'Refreshment Break', tue: 'Refreshment Break', wed: 'Refreshment Break', thu: 'Refreshment Break', fri: 'Refreshment Break' },
    { period: 'Period 3 (10:30 - 11:15)', mon: 'English (Miss Ayesha)', tue: 'English (Miss Ayesha)', wed: 'Physics Practical Lab', thu: 'Sindhi Salees', fri: 'Islamiat Compulsory' },
    { period: 'Period 4 (11:15 - 12:00)', mon: 'Computer Science', tue: 'Chemistry Lab', wed: 'Physics Practical Lab', thu: 'Computer Science Lab', fri: 'Jummah Prayer Assembly' },
  ];

  const schedule10A = [
    { period: 'Period 1 (08:30 - 09:15)', mon: 'Physics (Miss Fatima)', tue: 'Physics (Miss Fatima)', wed: 'Physics (Miss Fatima)', thu: 'Physics (Miss Fatima)', fri: 'Physics (Miss Fatima)' },
    { period: 'Period 2 (09:15 - 10:00)', mon: 'Mathematics (Sir Ahmed)', tue: 'Mathematics (Sir Ahmed)', wed: 'English (Miss Ayesha)', thu: 'Mathematics (Sir Ahmed)', fri: 'Mathematics (Sir Ahmed)' },
    { period: 'Break (10:00 - 10:30)', mon: 'Refreshment Break', tue: 'Refreshment Break', wed: 'Refreshment Break', thu: 'Refreshment Break', fri: 'Refreshment Break' },
    { period: 'Period 3 (10:30 - 11:15)', mon: 'Chemistry (Sir Asif)', tue: 'Chemistry (Sir Asif)', wed: 'Biology / CS Lab', thu: 'Pakistan Studies', fri: 'Sindhi Salees' },
    { period: 'Period 4 (11:15 - 12:00)', mon: 'Urdu Normal', tue: 'English Lab', wed: 'Biology / CS Lab', thu: 'Model Exam Practice', fri: 'Jummah Prayer Assembly' },
  ];

  const currentSchedule = selectedSection === '9-A' ? schedule9A : schedule10A;

  return (
    <div className="space-y-6">
      {/* Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-brand-700 border border-brand-200">
            Automated Conflict-Free Schedule • Room 201
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            Campus Timetable & Bell Schedule
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            Synchronized periods, lab allocations, faculty coverage, and Jummah prayer assembly timings.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <div className="flex bg-[#F1F5F9] p-1 rounded-xl">
            <button
              onClick={() => setSelectedSection('9-A')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
                selectedSection === '9-A' ? 'bg-white text-[#123B70] shadow-sm' : 'text-slate-600'
              }`}
            >
              Class 9-A (Matric)
            </button>
            <button
              onClick={() => setSelectedSection('10-A')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
                selectedSection === '10-A' ? 'bg-white text-[#123B70] shadow-sm' : 'text-slate-600'
              }`}
            >
              Class 10-A (Matric)
            </button>
          </div>

          <button
            onClick={() => window.print()}
            className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5"
          >
            <Printer className="w-3.5 h-3.5" />
            <span>Print Timetable</span>
          </button>
        </div>
      </div>

      {/* Timetable Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-[#F8FAFC] text-slate-600 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3.5 w-44">Period & Timing</th>
                <th className="p-3.5">Monday</th>
                <th className="p-3.5">Tuesday</th>
                <th className="p-3.5">Wednesday</th>
                <th className="p-3.5">Thursday</th>
                <th className="p-3.5">Friday</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {currentSchedule.map((row, idx) => {
                const isBreak = row.period.includes('Break');
                return (
                  <tr key={idx} className={isBreak ? 'bg-amber-50/60 font-bold text-amber-900' : 'hover:bg-slate-50/70 transition-colors'}>
                    <td className="p-3.5 font-bold text-slate-800 bg-[#F8FAFC]/50">{row.period}</td>
                    <td className="p-3.5 text-slate-700">{row.mon}</td>
                    <td className="p-3.5 text-slate-700">{row.tue}</td>
                    <td className="p-3.5 text-slate-700">{row.wed}</td>
                    <td className="p-3.5 text-slate-700">{row.thu}</td>
                    <td className="p-3.5 text-slate-700">{row.fri}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
