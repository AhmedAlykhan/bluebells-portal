import React, { useEffect } from 'react';
import { X, Printer, Award } from 'lucide-react';

export interface SubjectResult {
  code: string;
  name: string;
  totalMarks: number;
  obtainedMarks: number;
  grade: string;
  remarks: string;
}

export interface ReportCardData {
  studentName: string;
  rollNumber: string;
  admissionNumber: string;
  className: string;
  sectionName: string;
  session: string;
  examTerm: string;
  bFormNumber: string;
  attendancePercentage: number;
  totalWorkingDays: number;
  daysPresent: number;
  subjects: SubjectResult[];
  overallTotal: number;
  overallObtained: number;
  overallPercentage: number;
  overallGrade: string;
  overallRemarks: string;
  positionInSection?: string;
  issuedDate: string;
}

interface ReportCardModalProps {
  isOpen: boolean;
  onClose: () => void;
  data?: ReportCardData;
}

const DEFAULT_REPORT_CARD: ReportCardData = {
  studentName: 'Ali Khan',
  rollNumber: '09-A-12',
  admissionNumber: 'BSS-2024-0104',
  className: 'Class 9 (Matriculation Part I)',
  sectionName: 'Section A',
  session: '2026-2027',
  examTerm: 'Midterm Examination 2026',
  bFormNumber: '41303-1234567-1',
  attendancePercentage: 96.2,
  totalWorkingDays: 52,
  daysPresent: 50,
  subjects: [
    { code: 'MATH-09', name: 'Mathematics (Sindh Textbook Board)', totalMarks: 100, obtainedMarks: 94, grade: 'A-1', remarks: 'Outstanding analytical ability' },
    { code: 'PHY-09', name: 'Physics (Theory & Practical)', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Excellent lab demonstration' },
    { code: 'CHEM-09', name: 'Chemistry', totalMarks: 100, obtainedMarks: 85, grade: 'A', remarks: 'Strong theoretical grasp' },
    { code: 'ENG-09', name: 'English Compulsory', totalMarks: 100, obtainedMarks: 89, grade: 'A-1', remarks: 'Fluent comprehension and essay writing' },
    { code: 'URD-09', name: 'Urdu Salees', totalMarks: 100, obtainedMarks: 82, grade: 'A', remarks: 'Good classical vocabulary' },
    { code: 'SND-09', name: 'Sindhi Compulsory (Normal)', totalMarks: 75, obtainedMarks: 68, grade: 'A-1', remarks: 'Remarkable grammar & handwriting' },
    { code: 'ISL-09', name: 'Islamiat / Pakistan Studies', totalMarks: 75, obtainedMarks: 70, grade: 'A-1', remarks: 'Comprehensive understanding' },
    { code: 'CS-09', name: 'Computer Science (Python & DB)', totalMarks: 100, obtainedMarks: 96, grade: 'A-1', remarks: 'Exemplary programming coursework' },
  ],
  overallTotal: 750,
  overallObtained: 672,
  overallPercentage: 89.6,
  overallGrade: 'A-1 Distinction',
  overallRemarks: 'Promoted with Honours. Exceptional discipline and academic aptitude throughout the term.',
  positionInSection: '1st in Section',
  issuedDate: '28 October 2026'
};

export const ReportCardModal: React.FC<ReportCardModalProps> = ({
  isOpen,
  onClose,
  data = DEFAULT_REPORT_CARD
}) => {
  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div 
      className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose();
        }
      }}
    >
      <div 
        className="relative bg-white w-full max-w-4xl rounded-2xl shadow-2xl border border-slate-200 overflow-hidden my-8"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Sticky Top Header Bar */}
        <div className="sticky top-0 z-20 px-6 py-4 bg-slate-900 text-white flex items-center justify-between shadow-md print:hidden">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-amber-400" />
            <span className="font-semibold text-sm">Official Academic Transcript & Report Card</span>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-brand-700 hover:bg-brand-600 text-white rounded-lg text-xs font-semibold shadow transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>Print / Download PDF</span>
            </button>
            <button
              onClick={onClose}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-800 hover:bg-rose-600 text-slate-200 hover:text-white rounded-lg text-xs font-semibold border border-slate-700 hover:border-rose-500 transition-colors"
              title="Close modal (Esc)"
            >
              <X className="w-4 h-4" />
              <span>Close (Esc)</span>
            </button>
          </div>
        </div>

        {/* Printable Transcript Document */}
        <div className="p-8 bg-white print:p-0">
          {/* Institutional Crest & Details */}
          <div className="border-b-2 border-brand-800 pb-5 text-center">
            <div className="flex items-center justify-center gap-3 mb-2">
              <div className="w-12 h-12 rounded-xl bg-brand-900 text-white flex items-center justify-center font-bold text-2xl shadow">
                B
              </div>
              <div className="text-left">
                <h1 className="text-2xl font-black text-brand-950 tracking-tight uppercase">
                  Bluebells Schooling System
                </h1>
                <p className="text-xs font-semibold text-slate-600">
                  Campus: Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh (71800)
                </p>
                <p className="text-[11px] text-slate-500 font-mono">
                  Board of Secondary Education Hyderabad (BISEH) Affiliation No: HYD-SEC-2024-918
                </p>
              </div>
            </div>
            <div className="mt-4 inline-block px-4 py-1 rounded-full bg-brand-50 border border-brand-200 text-brand-900 text-xs font-bold uppercase tracking-wider">
              {data.examTerm} • Session {data.session}
            </div>
          </div>

          {/* Student Profile Grid */}
          <div className="mt-5 grid grid-cols-2 sm:grid-cols-4 gap-4 p-4 bg-slate-50 rounded-xl border border-slate-200 text-xs">
            <div>
              <span className="text-slate-400 block font-medium">Student Name:</span>
              <span className="font-bold text-slate-800 text-sm">{data.studentName}</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Admission No:</span>
              <span className="font-bold text-slate-800 font-mono">{data.admissionNumber}</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Roll Number:</span>
              <span className="font-bold text-slate-800 font-mono">{data.rollNumber}</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">B-Form / CNIC:</span>
              <span className="font-bold text-slate-800 font-mono">{data.bFormNumber}</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Class & Section:</span>
              <span className="font-bold text-slate-800">{data.className} ({data.sectionName})</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Term Attendance:</span>
              <span className="font-bold text-emerald-700">{data.attendancePercentage}% ({data.daysPresent}/{data.totalWorkingDays} Days)</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Position in Class:</span>
              <span className="font-bold text-brand-800">{data.positionInSection || 'N/A'}</span>
            </div>
            <div>
              <span className="text-slate-400 block font-medium">Issue Date:</span>
              <span className="font-bold text-slate-800">{data.issuedDate}</span>
            </div>
          </div>

          {/* Marks Breakdown Table */}
          <div className="mt-6 border border-slate-200 rounded-xl overflow-hidden shadow-sm">
            <table className="w-full text-left text-xs">
              <thead className="bg-brand-900 text-white uppercase text-[10px] tracking-wider font-semibold">
                <tr>
                  <th className="py-3 px-3">Subject & Course</th>
                  <th className="py-3 px-3 text-center">Max Marks</th>
                  <th className="py-3 px-3 text-center">Marks Obtained</th>
                  <th className="py-3 px-3 text-center">%</th>
                  <th className="py-3 px-3 text-center">Grade</th>
                  <th className="py-3 px-4">Subject Remarks</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {data.subjects.map((sub, idx) => {
                  const pct = Math.round((sub.obtainedMarks / sub.totalMarks) * 100);
                  return (
                    <tr key={idx} className={idx % 2 === 0 ? 'bg-white' : 'bg-slate-50/50'}>
                      <td className="py-2.5 px-3 font-semibold text-slate-800">
                        {sub.name}
                        <span className="block text-[10px] text-slate-400 font-mono">{sub.code}</span>
                      </td>
                      <td className="py-2.5 px-3 text-center text-slate-600 font-mono">{sub.totalMarks}</td>
                      <td className="py-2.5 px-3 text-center font-bold text-slate-900 font-mono">{sub.obtainedMarks}</td>
                      <td className="py-2.5 px-3 text-center text-slate-700 font-mono">{pct}%</td>
                      <td className="py-2.5 px-3 text-center">
                        <span className="inline-block px-2 py-0.5 rounded font-bold text-xs bg-brand-100 text-brand-900">
                          {sub.grade}
                        </span>
                      </td>
                      <td className="py-2.5 px-4 text-slate-600 italic text-[11px]">{sub.remarks}</td>
                    </tr>
                  );
                })}
              </tbody>
              <tfoot className="bg-slate-100 font-bold border-t-2 border-slate-300 text-slate-900">
                <tr>
                  <td className="py-3 px-3 uppercase">Total Aggregate</td>
                  <td className="py-3 px-3 text-center font-mono">{data.overallTotal}</td>
                  <td className="py-3 px-3 text-center text-brand-900 font-mono text-sm">{data.overallObtained}</td>
                  <td className="py-3 px-3 text-center text-brand-900 font-mono text-sm">{data.overallPercentage}%</td>
                  <td className="py-3 px-3 text-center">
                    <span className="px-2.5 py-1 bg-emerald-600 text-white rounded font-extrabold text-xs">
                      {data.overallGrade}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-xs font-semibold text-emerald-800">PASSED WITH DISTINCTION</td>
                </tr>
              </tfoot>
            </table>
          </div>

          {/* Institutional Grading Scale & Remarks */}
          <div className="mt-5 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 text-[10px] text-slate-600">
              <span className="font-bold text-slate-800 block mb-1">Sindh Board Grading Scale:</span>
              <div className="grid grid-cols-5 gap-1 text-center font-mono">
                <div className="bg-white p-1 rounded border">A-1: 80-100%</div>
                <div className="bg-white p-1 rounded border">A: 70-79%</div>
                <div className="bg-white p-1 rounded border">B: 60-69%</div>
                <div className="bg-white p-1 rounded border">C: 50-59%</div>
                <div className="bg-white p-1 rounded border">D: 40-49%</div>
              </div>
            </div>

            <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-200 text-xs text-emerald-950">
              <span className="font-bold block mb-1">Principal & Academic Remarks:</span>
              <p className="italic">{data.overallRemarks}</p>
            </div>
          </div>

          {/* Signatures & Seal */}
          <div className="mt-10 pt-6 border-t border-slate-200 grid grid-cols-2 sm:grid-cols-4 gap-4 text-center text-xs">
            <div>
              <div className="h-10 border-b border-dashed border-slate-400 mx-auto w-28 sm:w-36 mb-1"></div>
              <p className="font-bold text-slate-800">Class Teacher</p>
              <p className="text-[10px] text-slate-500">Sir Ahmed Khan</p>
            </div>
            <div>
              <div className="h-10 border-b border-dashed border-slate-400 mx-auto w-28 sm:w-36 mb-1"></div>
              <p className="font-bold text-slate-800">Vice Principal</p>
              <p className="text-[10px] text-slate-500">Hana Khan</p>
            </div>
            <div>
              <div className="w-14 h-14 rounded-full border-2 border-brand-800 mx-auto flex items-center justify-center text-[8px] font-black uppercase text-brand-900 rotate-[-12deg] mb-1">
                Official Seal
              </div>
              <p className="font-bold text-slate-800">Verified & Sealed</p>
              <p className="text-[10px] text-slate-400">Exam Controller</p>
            </div>
            <div>
              <div className="h-10 border-b border-dashed border-slate-400 mx-auto w-28 sm:w-36 mb-1"></div>
              <p className="font-bold text-slate-800">Principal</p>
              <p className="text-[10px] text-slate-500">Huma Arif</p>
            </div>
          </div>
        </div>

        {/* Bottom Action Footer (hidden in print) */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-3 print:hidden">
          <p className="text-xs text-slate-500">
            Press <kbd className="px-1.5 py-0.5 rounded bg-slate-200 text-slate-700 font-mono text-[10px]">Esc</kbd> or click outside to dismiss this transcript view.
          </p>
          <div className="flex items-center gap-3">
            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-4 py-2 bg-brand-700 hover:bg-brand-600 text-white rounded-lg text-xs font-semibold shadow transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>Print Transcript</span>
            </button>
            <button
              onClick={onClose}
              className="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-lg text-xs font-semibold transition-colors"
            >
              <X className="w-4 h-4" />
              <span>Close</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
