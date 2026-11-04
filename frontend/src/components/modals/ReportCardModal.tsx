import React, { useState, useEffect } from 'react';
import { X, Printer, Award, CheckCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { 
  STUDENT_REPORT_CARDS, 
  ReportCardData, 
  SubjectResult 
} from '../../data/reportCardsData';

export type { SubjectResult, ReportCardData };

interface ReportCardModalProps {
  isOpen: boolean;
  onClose: () => void;
  data?: ReportCardData;
}

export const ReportCardModal: React.FC<ReportCardModalProps> = ({
  isOpen,
  onClose,
  data: initialData
}) => {
  const { user } = useAuth();
  const isRestricted = user?.roles?.includes('ROLE_STUDENT') || user?.roles?.includes('ROLE_PARENT');

  const [currentData, setCurrentData] = useState<ReportCardData>(
    initialData || STUDENT_REPORT_CARDS['ali-khan']
  );
  const [selectedStudentKey, setSelectedStudentKey] = useState<string>(
    initialData?.id || 'ali-khan'
  );
  const [printSize, setPrintSize] = useState<'A3' | 'A4'>('A3');

  useEffect(() => {
    if (isRestricted) {
      setCurrentData(STUDENT_REPORT_CARDS['ali-khan']);
      setSelectedStudentKey('ali-khan');
    } else if (initialData) {
      setCurrentData(initialData);
      if (initialData.id) setSelectedStudentKey(initialData.id);
    }
  }, [initialData, isRestricted]);

  useEffect(() => {
    if (!isOpen) return;

    document.body.classList.add('has-active-print-modal');

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      document.body.classList.remove('has-active-print-modal');
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const data = currentData;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div 
      className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/80 backdrop-blur-sm flex items-center justify-center p-2 sm:p-4 print:p-0 print:bg-white print:static"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose();
        }
      }}
    >
      {/* Dynamic Print CSS for Page Sizing */}
      <style>{`
        @media print {
          @page {
            size: ${printSize === 'A3' ? 'A3 portrait' : 'A4 portrait'};
            margin: ${printSize === 'A3' ? '10mm' : '6mm'};
          }
          body {
            background: white !important;
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
          }
        }
      `}</style>

      <div 
        className="relative bg-white w-full max-w-5xl rounded-2xl shadow-2xl border border-slate-200 overflow-hidden my-4 print:my-0 print:border-none print:shadow-none print:w-full print:max-w-none"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Sticky Modal Top Controls (Hidden in Print) */}
        <div className="sticky top-0 z-30 px-6 py-3.5 bg-slate-900 text-white flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 shadow-md print:hidden">
          <div className="flex items-center gap-2.5">
            <span className="p-1.5 rounded-lg bg-amber-500/20 text-amber-400 border border-amber-500/30">
              <Award className="w-5 h-5 text-amber-400" />
            </span>
            <div>
              <span className="font-bold text-sm block">Official Academic Transcript & Progress Report</span>
              <span className="text-[11px] text-slate-400">Board of Secondary Education Hyderabad (BISEH) Standard</span>
            </div>
          </div>

          <div className="flex items-center gap-2 flex-wrap w-full sm:w-auto justify-end">
            {/* Paper Size Selector */}
            <div className="flex items-center bg-slate-800 p-1 rounded-lg border border-slate-700 text-xs">
              <span className="text-slate-400 px-2 font-medium">Page Size:</span>
              <button
                onClick={() => setPrintSize('A3')}
                className={`px-2.5 py-1 rounded font-bold transition-all ${
                  printSize === 'A3' 
                    ? 'bg-brand-700 text-white shadow' 
                    : 'text-slate-400 hover:text-white'
                }`}
                title="A3 Page Size (Official Institutional Standard)"
              >
                A3 Format (Standard)
              </button>
              <button
                onClick={() => setPrintSize('A4')}
                className={`px-2.5 py-1 rounded font-bold transition-all ${
                  printSize === 'A4' 
                    ? 'bg-brand-700 text-white shadow' 
                    : 'text-slate-400 hover:text-white'
                }`}
                title="A4 Page Size (Compact)"
              >
                A4 Format
              </button>
            </div>

            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-brand-700 hover:bg-brand-600 text-white rounded-lg text-xs font-bold shadow transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>Print Transcript ({printSize})</span>
            </button>

            <button
              onClick={onClose}
              className="inline-flex items-center gap-1 px-3 py-1.5 bg-slate-800 hover:bg-rose-600 text-slate-200 hover:text-white rounded-lg text-xs font-semibold border border-slate-700 hover:border-rose-500 transition-colors"
              title="Close modal (Esc)"
            >
              <X className="w-4 h-4" />
              <span>Close</span>
            </button>
          </div>
        </div>

        {/* Student Quick Switcher Toolbar (Hidden in Print) */}
        <div className="bg-slate-800 px-6 py-2 text-xs text-white flex flex-wrap items-center justify-between gap-3 border-b border-slate-700 print:hidden">
          {isRestricted ? (
            <div className="flex items-center gap-2">
              <span className="text-slate-400 font-semibold">Official Student Record:</span>
              <span className="px-3 py-1 bg-slate-900 border border-slate-600 rounded-md text-amber-300 font-bold text-xs flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                Ali Khan (09-A-12) — Class 9-A (Science) • Rank #1 (89.6%)
              </span>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <span className="text-slate-400 font-semibold">Select Student / Class Record:</span>
              <select
                value={selectedStudentKey}
                onChange={(e) => {
                  const key = e.target.value;
                  setSelectedStudentKey(key);
                  if (STUDENT_REPORT_CARDS[key]) {
                    setCurrentData(STUDENT_REPORT_CARDS[key]);
                  }
                }}
                className="px-3 py-1 bg-slate-900 border border-slate-600 rounded-md text-amber-300 font-bold text-xs focus:outline-none focus:ring-2 focus:ring-amber-400"
              >
                <optgroup label="Class 9 (Matriculation Part I)">
                  <option value="ali-khan">Ali Khan (09-A-12) — Rank #1 (89.6%)</option>
                  <option value="shahid-afridi">Shahid Afridi (09-A-02) — Rank #2 (83.5%)</option>
                  <option value="ayesha-siddiqui">Ayesha Siddiqui (09-A-03) — Rank #3 (80.1%)</option>
                </optgroup>
                <optgroup label="Class 10 (Matriculation Part II)">
                  <option value="bilal-hassan">Bilal Hassan (10-A-01) — Rank #1 (89.0%)</option>
                </optgroup>
                <optgroup label="Class 8 (Middle School)">
                  <option value="sara-khan">Sara Khan (08-R-05) — Rank #1 (91.7%)</option>
                </optgroup>
                <optgroup label="Class 5 (Primary School)">
                  <option value="zainab-bibi">Zainab Bibi (05-J-08) — Rank #1 (93.6%)</option>
                </optgroup>
              </select>
            </div>
          )}
          <span className="text-[11px] text-emerald-400 font-semibold bg-emerald-950/60 px-2.5 py-0.5 rounded border border-emerald-800/60 flex items-center gap-1">
            <CheckCircle className="w-3.5 h-3.5 text-emerald-400" />
            Verified Term Examination Record
          </span>
        </div>

        {/* =========================================================================
            A3 PRINTABLE TRANSCRIPT DOCUMENT
            Ornate Board Certificate Frame with Golden/Navy Double Border
           ========================================================================= */}
        <div 
          id="printable-report-card" 
          className="p-6 sm:p-10 bg-white print:p-0 print:m-0 print:w-full print:h-full print:max-h-none"
        >
          {/* Certificate Double Border Wrapper */}
          <div className="border-4 border-slate-900 p-2 rounded-xl print:border-4 print:border-slate-900 print:rounded-none">
            <div className="border-2 border-amber-600/80 p-5 sm:p-8 rounded-lg relative bg-white">
              {/* Corner Ornamental Accents */}
              <div className="absolute top-1 left-1 w-4 h-4 border-t-2 border-l-2 border-amber-700"></div>
              <div className="absolute top-1 right-1 w-4 h-4 border-t-2 border-r-2 border-amber-700"></div>
              <div className="absolute bottom-1 left-1 w-4 h-4 border-b-2 border-l-2 border-amber-700"></div>
              <div className="absolute bottom-1 right-1 w-4 h-4 border-b-2 border-r-2 border-amber-700"></div>

              {/* Watermark in Background (print visible) */}
              <div className="absolute inset-0 flex items-center justify-center pointer-events-none opacity-[0.03] select-none">
                <span className="text-[140px] font-black uppercase text-slate-950">BLUEBELLS</span>
              </div>

              {/* 1. Institutional Crest & Header */}
              <div className="relative z-10 border-b-2 border-slate-900 pb-4 mb-4 text-center">
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
                  {/* Left: School Crest */}
                  <div className="flex items-center gap-3">
                    <div className="w-16 h-16 rounded-2xl bg-slate-950 text-white flex flex-col items-center justify-center shadow-lg border-2 border-amber-500">
                      <span className="font-serif font-black text-2xl leading-none">B</span>
                      <span className="text-[8px] font-bold tracking-widest uppercase text-amber-300">ESTD 2010</span>
                    </div>
                    <div className="text-left hidden sm:block">
                      <p className="text-[10px] font-bold text-amber-700 uppercase tracking-widest">Excellence in Education</p>
                      <p className="text-[11px] font-mono text-slate-500">Hyderabad Registration: HYD-SEC-2024-918</p>
                    </div>
                  </div>

                  {/* Center: Main Institutional Titles */}
                  <div className="flex-1 text-center">
                    <h1 className="text-2xl sm:text-3xl font-black text-slate-950 uppercase tracking-tight font-serif">
                      Bluebells Schooling System
                    </h1>
                    <p className="text-xs font-bold text-slate-800 font-serif mt-0.5">
                      بلو بیلز اسکولنگ سسٹم — لطیف آباد، حیدرآباد
                    </p>
                    <p className="text-xs text-slate-600 font-medium mt-0.5">
                      Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh (71800)
                    </p>
                    <p className="text-[11px] text-slate-500 font-semibold mt-0.5">
                      Affiliated with Board of Secondary Education Hyderabad (BISEH) • Reg: SEMIS-413020091
                    </p>
                  </div>

                  {/* Right: Verification Stamp & QR Code Placeholder */}
                  <div className="text-right hidden sm:block">
                    <div className="w-16 h-16 border-2 border-slate-800 rounded-lg p-1 mx-auto flex flex-col items-center justify-center bg-slate-50 text-center">
                      <span className="text-[8px] font-black text-slate-700 uppercase leading-none">QR AUTH</span>
                      <div className="w-8 h-8 bg-slate-800 rounded mt-0.5 flex items-center justify-center text-white text-[7px] font-mono">
                        VERIFIED
                      </div>
                      <span className="text-[7px] text-slate-500 font-mono mt-0.5">BSS-2026</span>
                    </div>
                    <span className="text-[9px] text-slate-400 block font-mono mt-0.5">Official Copy</span>
                  </div>
                </div>

                {/* Document Banner */}
                <div className="mt-3 inline-block px-6 py-1.5 rounded-full bg-slate-950 text-white text-xs font-black uppercase tracking-widest shadow-sm">
                  {data.examTerm} • Academic Session {data.session}
                </div>
              </div>

              {/* 2. Student Bio Information Grid */}
              <div className="relative z-10 border border-slate-300 rounded-lg p-3.5 mb-4 bg-slate-50/70 text-xs text-slate-800">
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-x-4 gap-y-2">
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Student Full Name:</span>
                    <span className="font-black text-slate-950 text-sm">{data.studentName}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Father / Guardian Name:</span>
                    <span className="font-bold text-slate-900">{data.studentName.includes('Khan') ? 'Mohammad Tariq Khan' : 'Mohammad Riaz Afridi'}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Class & Section:</span>
                    <span className="font-black text-slate-900">{data.className} — {data.sectionName}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Roll Number:</span>
                    <span className="font-black font-mono text-slate-950 text-sm bg-white px-2 py-0.5 rounded border border-slate-300 inline-block">
                      {data.rollNumber}
                    </span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Admission / GR Number:</span>
                    <span className="font-bold font-mono text-slate-900">{data.admissionNumber}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">B-Form / CNIC:</span>
                    <span className="font-mono font-bold text-slate-900">{data.bFormNumber}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Term Attendance:</span>
                    <span className="font-bold text-emerald-800">
                      {data.attendancePercentage}% ({data.daysPresent} Present / {data.totalWorkingDays} Days)
                    </span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block font-semibold uppercase">Class Ranking:</span>
                    <span className="font-black text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200 inline-block">
                      {data.positionInSection || '1st in Section'}
                    </span>
                  </div>
                </div>
              </div>

              {/* 3. Academic Marks Evaluation Table */}
              <div className="relative z-10 border-2 border-slate-900 rounded-lg overflow-hidden shadow-sm mb-4">
                <table className="w-full text-left text-xs">
                  <thead className="bg-slate-950 text-white uppercase text-[10px] tracking-wider font-bold">
                    <tr>
                      <th className="py-2.5 px-3">#</th>
                      <th className="py-2.5 px-3">Subject & Curriculum Component</th>
                      <th className="py-2.5 px-2 text-center font-mono">Max Marks</th>
                      <th className="py-2.5 px-2 text-center font-mono">Min Pass</th>
                      <th className="py-2.5 px-2 text-center font-mono">Obtained</th>
                      <th className="py-2.5 px-2 text-center font-mono">%</th>
                      <th className="py-2.5 px-2 text-center">Grade</th>
                      <th className="py-2.5 px-3">Faculty Academic Observation & Remarks</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {data.subjects.map((sub, idx) => {
                      const pct = Math.round((sub.obtainedMarks / sub.totalMarks) * 100);
                      const minPass = Math.round(sub.totalMarks * 0.40);
                      const isDistinction = pct >= 80;

                      return (
                        <tr key={idx} className={idx % 2 === 0 ? 'bg-white' : 'bg-slate-50/70'}>
                          <td className="py-2 px-3 text-slate-400 font-mono text-[11px]">{idx + 1}</td>
                          <td className="py-2 px-3 font-bold text-slate-900">
                            {sub.name}
                            <span className="block text-[9.5px] text-slate-500 font-mono font-normal">
                              Course Code: {sub.code} • Sindh Board Prescribed
                            </span>
                          </td>
                          <td className="py-2 px-2 text-center text-slate-700 font-mono font-semibold">{sub.totalMarks}</td>
                          <td className="py-2 px-2 text-center text-slate-500 font-mono">{minPass}</td>
                          <td className={`py-2 px-2 text-center font-black font-mono text-sm ${isDistinction ? 'text-purple-950 bg-purple-50/60' : 'text-slate-950'}`}>
                            {sub.obtainedMarks}
                          </td>
                          <td className="py-2 px-2 text-center text-slate-800 font-mono font-bold">{pct}%</td>
                          <td className="py-2 px-2 text-center">
                            <span className={`inline-block px-2.5 py-0.5 rounded font-black text-xs ${
                              sub.grade.includes('A-1') 
                                ? 'bg-purple-100 text-purple-950 border border-purple-300' 
                                : sub.grade.includes('A')
                                ? 'bg-blue-100 text-blue-900 border border-blue-200'
                                : 'bg-slate-100 text-slate-800'
                            }`}>
                              {sub.grade}
                            </span>
                          </td>
                          <td className="py-2 px-3 text-slate-700 italic text-[11px]">{sub.remarks}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                  {/* Aggregate Summary Footer */}
                  <tfoot className="bg-slate-100 border-t-2 border-slate-900 font-bold text-slate-950">
                    <tr className="border-b border-slate-300">
                      <td colSpan={2} className="py-2.5 px-3 uppercase text-xs font-black tracking-wide">
                        Cumulative Aggregate Marks:
                      </td>
                      <td className="py-2.5 px-2 text-center font-mono font-black text-sm">{data.overallTotal}</td>
                      <td className="py-2.5 px-2 text-center text-slate-500 font-mono">300</td>
                      <td className="py-2.5 px-2 text-center font-mono font-black text-base text-purple-900 bg-purple-100/50">
                        {data.overallObtained}
                      </td>
                      <td className="py-2.5 px-2 text-center font-mono font-black text-base text-purple-900">
                        {data.overallPercentage}%
                      </td>
                      <td className="py-2.5 px-2 text-center">
                        <span className="px-3 py-1 bg-emerald-700 text-white rounded font-black text-xs shadow-sm">
                          {data.overallGrade}
                        </span>
                      </td>
                      <td className="py-2.5 px-3 text-xs font-black text-emerald-800 uppercase tracking-wide">
                        PASSED WITH HONOURS & DISTINCTION
                      </td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              {/* 4. Affective Domain & Board Grading Matrix */}
              <div className="relative z-10 grid grid-cols-1 md:grid-cols-2 gap-4 mb-5 text-xs">
                {/* Left: Behavioral & Co-Curricular Assessment */}
                <div className="border border-slate-300 rounded-lg p-3 bg-slate-50 text-[10.5px]">
                  <span className="font-bold text-slate-900 block mb-1.5 uppercase text-[10px] tracking-wide border-b border-slate-200 pb-1">
                    Affective Domain & Co-Curricular Assessment:
                  </span>
                  <div className="grid grid-cols-2 gap-2 text-slate-700">
                    <div className="flex justify-between items-center bg-white p-1.5 rounded border border-slate-200">
                      <span>Discipline & Conduct:</span>
                      <span className="font-bold text-amber-700">★★★★★ (Exemplary)</span>
                    </div>
                    <div className="flex justify-between items-center bg-white p-1.5 rounded border border-slate-200">
                      <span>Punctuality:</span>
                      <span className="font-bold text-amber-700">★★★★★ (Excellent)</span>
                    </div>
                    <div className="flex justify-between items-center bg-white p-1.5 rounded border border-slate-200">
                      <span>Homework & Regularity:</span>
                      <span className="font-bold text-amber-700">★★★★★ (Diligent)</span>
                    </div>
                    <div className="flex justify-between items-center bg-white p-1.5 rounded border border-slate-200">
                      <span>Sports & Activities:</span>
                      <span className="font-bold text-amber-700">★★★★☆ (Active)</span>
                    </div>
                  </div>
                </div>

                {/* Right: Sindh Board Official Grading Formula */}
                <div className="border border-slate-300 rounded-lg p-3 bg-slate-50 text-[10px]">
                  <span className="font-bold text-slate-900 block mb-1.5 uppercase text-[10px] tracking-wide border-b border-slate-200 pb-1">
                    Sindh Board / BISE Hyderabad Grading Standard:
                  </span>
                  <div className="grid grid-cols-5 gap-1 text-center font-mono text-[9px]">
                    <div className="bg-purple-50 border border-purple-200 p-1 rounded font-bold text-purple-950">
                      A-1: 80-100%<br/><span className="text-[8px] font-normal text-purple-700">Distinction</span>
                    </div>
                    <div className="bg-blue-50 border border-blue-200 p-1 rounded font-bold text-blue-950">
                      A: 70-79%<br/><span className="text-[8px] font-normal text-blue-700">Excellent</span>
                    </div>
                    <div className="bg-white border border-slate-200 p-1 rounded font-bold text-slate-800">
                      B: 60-69%<br/><span className="text-[8px] font-normal text-slate-600">Very Good</span>
                    </div>
                    <div className="bg-white border border-slate-200 p-1 rounded font-bold text-slate-800">
                      C: 50-59%<br/><span className="text-[8px] font-normal text-slate-600">Good</span>
                    </div>
                    <div className="bg-rose-50 border border-rose-200 p-1 rounded font-bold text-rose-950">
                      D: 40-49%<br/><span className="text-[8px] font-normal text-rose-700">Pass</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* 5. Principal's Evaluation & Observations */}
              <div className="relative z-10 border border-emerald-300 rounded-lg p-3 bg-emerald-50/60 text-xs mb-6 text-emerald-950">
                <span className="font-black block uppercase text-[10px] tracking-wide text-emerald-900 mb-0.5">
                  Principal & Institutional Remarks:
                </span>
                <p className="italic leading-relaxed">
                  "{data.overallRemarks}"
                </p>
              </div>

              {/* 6. Signatures & Official Institutional Stamp Seal */}
              <div className="relative z-10 pt-4 border-t-2 border-slate-900 grid grid-cols-2 sm:grid-cols-4 gap-4 text-center text-xs">
                {/* Class Teacher */}
                <div>
                  <div className="h-10 border-b border-dashed border-slate-500 mx-auto w-32 mb-1 flex items-end justify-center">
                    <span className="font-serif italic text-slate-600 text-xs">Ahmed Khan</span>
                  </div>
                  <p className="font-bold text-slate-900 text-xs">Class Teacher</p>
                  <p className="text-[10px] text-slate-500">Sir Ahmed Khan (M.Sc)</p>
                </div>

                {/* Vice Principal */}
                <div>
                  <div className="h-10 border-b border-dashed border-slate-500 mx-auto w-32 mb-1 flex items-end justify-center">
                    <span className="font-serif italic text-slate-600 text-xs">Hana Khan</span>
                  </div>
                  <p className="font-bold text-slate-900 text-xs">Vice Principal</p>
                  <p className="text-[10px] text-slate-500">Hana Khan</p>
                </div>

                {/* Exam Controller Official Seal */}
                <div>
                  <div className="w-16 h-16 rounded-full border-2 border-brand-900 mx-auto flex flex-col items-center justify-center text-[7.5px] font-black uppercase text-brand-950 rotate-[-10deg] bg-amber-50/50 shadow-inner mb-1">
                    <span>* BISEH *</span>
                    <span className="text-[9px] text-amber-800 font-bold">SEAL</span>
                    <span>VERIFIED</span>
                  </div>
                  <p className="font-bold text-slate-900 text-xs">Exam Controller</p>
                  <p className="text-[10px] text-slate-400 font-mono">Date: {data.issuedDate}</p>
                </div>

                {/* Principal */}
                <div>
                  <div className="h-10 border-b border-dashed border-slate-500 mx-auto w-32 mb-1 flex items-end justify-center">
                    <span className="font-serif italic text-slate-600 text-xs">Huma Arif</span>
                  </div>
                  <p className="font-bold text-slate-900 text-xs">Academic Principal</p>
                  <p className="text-[10px] text-slate-500">Huma Arif (M.Ed)</p>
                </div>
              </div>

              {/* Bottom Security Notice */}
              <div className="mt-4 pt-2 border-t border-slate-200 text-center text-[8px] text-slate-400 font-mono">
                This is a computer-generated official transcript issued by Bluebells Schooling System under BISE Hyderabad guidelines. Any alteration renders this document null and void.
              </div>
            </div>
          </div>
        </div>

        {/* Modal Bottom Footer (Hidden in print) */}
        <div className="px-6 py-3.5 bg-slate-50 border-t border-slate-200 flex flex-col sm:flex-row items-center justify-between gap-3 print:hidden">
          <p className="text-xs text-slate-500">
            Press <kbd className="px-1.5 py-0.5 rounded bg-slate-200 text-slate-700 font-mono text-[10px]">Esc</kbd> or click outside to dismiss.
          </p>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-4 py-2 bg-brand-700 hover:bg-brand-600 text-white rounded-lg text-xs font-bold shadow transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>Print Transcript ({printSize})</span>
            </button>
            <button
              onClick={onClose}
              className="inline-flex items-center gap-1 px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-lg text-xs font-semibold transition-colors"
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
