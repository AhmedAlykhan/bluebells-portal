import React, { useState } from 'react';
import { Award } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { ReportCardModal } from '../../components/modals/ReportCardModal';
import { GenerateReportCardModal } from '../../components/modals/GenerateReportCardModal';
import { 
  getReportCardByStudentKey, 
  getReportCardByName, 
  ReportCardData 
} from '../../data/reportCardsData';

export const ExaminationsPage: React.FC = () => {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedCardData, setSelectedCardData] = useState<ReportCardData | undefined>(undefined);

  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const allReportCards = [
    { rank: 1, key: 'ali-khan', name: 'Ali Khan', id: 'BSS-2024-0104', section: '9-A', obtained: '672.00', total: '750.00', pct: '89.60%', grade: 'A-1', att: '96.2%', remark: 'Outstanding performance. First Position in Class 9-A.' },
    { rank: 2, key: 'shahid-afridi', name: 'Shahid Afridi', id: 'BSS-2026-0003', section: '9-A', obtained: '626.00', total: '750.00', pct: '83.50%', grade: 'A-1', att: '92.3%', remark: 'Very good conceptual clarity and analytical grasp.' },
    { rank: 3, key: 'ayesha-siddiqui', name: 'Ayesha Siddiqui', id: 'BSS-2026-0004', section: '9-A', obtained: '601.00', total: '750.00', pct: '80.10%', grade: 'A-1', att: '94.2%', remark: 'Diligent and consistent academic excellence.' },
    { rank: 4, key: 'bilal-hassan', name: 'Bilal Hassan', id: 'BSS-2026-0005', section: '9-A', obtained: '550.00', total: '750.00', pct: '73.30%', grade: 'A', att: '90.5%', remark: 'Good effort. Focus on physics numericals.' },
  ];

  const reportCards = isStudent
    ? allReportCards.filter(r => r.name.toLowerCase().includes(user?.fullName?.toLowerCase() || 'ali khan'))
    : (isParent ? allReportCards.filter(r => r.name === 'Ali Khan') : allReportCards);

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
            Sindh Board (BISE Hyderabad) Standardized Grading
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            {isStudent ? 'My Midterm Academic Transcript' : 'Examinations & Student Report Cards'}
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            Official A3/A4 printable transcripts, subject marks tabulation, class positions, and certified principal remarks.
          </p>
        </div>

        <button
          onClick={() => {
            if (isStudent || isParent) {
              const studentName = user?.fullName || 'Ali Khan';
              setSelectedCardData(getReportCardByName(studentName));
              setShowReportCard(true);
            } else {
              setShowGenerateModal(true);
            }
          }}
          className="px-4 py-2.5 bg-[#123B70] hover:bg-[#2167B2] text-white rounded-xl text-xs font-bold shadow-md transition-all flex items-center gap-2"
        >
          <Award className="w-4 h-4 text-amber-400" />
          <span>{isStudent ? 'View My Official Report Card' : 'Generate & Print Report Card'}</span>
        </button>
      </div>

      {/* Results Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 sm:p-5 border-b border-slate-200 bg-[#F8FAFC] flex items-center justify-between">
          <h2 className="text-sm font-bold text-slate-800">
            {isStudent ? 'Published Personal Examination Results' : 'Class 9-A Mid-Term Examination Results (Published)'}
          </h2>
          <span className="text-xs text-slate-500 font-semibold">Total Marks: 750</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3.5">Rank</th>
                <th className="p-3.5">Student Name</th>
                <th className="p-3.5">Registration #</th>
                <th className="p-3.5">Marks Obtained</th>
                <th className="p-3.5">Max Marks</th>
                <th className="p-3.5">Percentage</th>
                <th className="p-3.5">Sindh Board Grade</th>
                <th className="p-3.5">Attendance</th>
                <th className="p-3.5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {reportCards.map((rc) => (
                <tr key={rc.rank} className="hover:bg-slate-50/70 transition-colors">
                  <td className="p-3.5 font-black text-brand-800">#{rc.rank}</td>
                  <td className="p-3.5 font-bold text-slate-800">{rc.name}</td>
                  <td className="p-3.5 font-mono text-slate-500">{rc.id}</td>
                  <td className="p-3.5 font-black text-emerald-700">{rc.obtained}</td>
                  <td className="p-3.5 text-slate-500">{rc.total}</td>
                  <td className="p-3.5 font-black text-slate-800">{rc.pct}</td>
                  <td className="p-3.5">
                    <span className="px-2.5 py-0.5 rounded-full bg-emerald-100 text-emerald-800 font-black text-[11px]">
                      {rc.grade}
                    </span>
                  </td>
                  <td className="p-3.5 text-slate-600 font-medium">{rc.att}</td>
                  <td className="p-3.5 text-right">
                    <button
                      onClick={() => {
                        setSelectedCardData(getReportCardByStudentKey(rc.key));
                        setShowReportCard(true);
                      }}
                      className="px-3 py-1.5 bg-brand-50 hover:bg-brand-100 text-brand-800 rounded-lg font-bold text-[11px] transition-colors inline-flex items-center gap-1 border border-brand-200"
                    >
                      <Award className="w-3.5 h-3.5 text-amber-500" />
                      <span>View Card</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Sindh Board Grading Scale Standard Reference */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
        <h2 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-4">
          BISE Hyderabad / Sindh Board Grading Scale Standard
        </h2>
        <div className="grid grid-cols-2 sm:grid-cols-6 gap-3 text-xs">
          <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200">
            <span className="font-black text-emerald-900 text-sm">A-1 (80% - 100%)</span>
            <p className="text-emerald-700 text-[11px] mt-0.5">Distinction (4.00 GP)</p>
          </div>
          <div className="p-3 rounded-xl bg-blue-50 border border-blue-200">
            <span className="font-black text-blue-900 text-sm">A (70% - 79.9%)</span>
            <p className="text-blue-700 text-[11px] mt-0.5">Excellent (3.70 GP)</p>
          </div>
          <div className="p-3 rounded-xl bg-purple-50 border border-purple-200">
            <span className="font-black text-purple-900 text-sm">B (60% - 69.9%)</span>
            <p className="text-purple-700 text-[11px] mt-0.5">Good (3.00 GP)</p>
          </div>
          <div className="p-3 rounded-xl bg-amber-50 border border-amber-200">
            <span className="font-black text-amber-900 text-sm">C (50% - 59.9%)</span>
            <p className="text-amber-700 text-[11px] mt-0.5">Satisfactory (2.00 GP)</p>
          </div>
          <div className="p-3 rounded-xl bg-orange-50 border border-orange-200">
            <span className="font-black text-orange-900 text-sm">D (40% - 49.9%)</span>
            <p className="text-orange-700 text-[11px] mt-0.5">Pass (1.00 GP)</p>
          </div>
          <div className="p-3 rounded-xl bg-red-50 border border-red-200">
            <span className="font-black text-red-900 text-sm">F (&lt; 40%)</span>
            <p className="text-red-700 text-[11px] mt-0.5">Fail (0.00 GP)</p>
          </div>
        </div>
      </div>

      {/* Modals */}
      <GenerateReportCardModal
        isOpen={showGenerateModal}
        onClose={() => setShowGenerateModal(false)}
        onGenerate={(data) => {
          setSelectedCardData(data);
          setShowGenerateModal(false);
          setShowReportCard(true);
        }}
      />

      <ReportCardModal
        isOpen={showReportCard}
        onClose={() => setShowReportCard(false)}
        data={selectedCardData}
      />
    </div>
  );
};
