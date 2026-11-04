import React, { useState, useEffect } from 'react';
import { X, Award, User, GraduationCap, Calendar, FileText } from 'lucide-react';
import { 
  CLASSES_LIST, 
  STUDENT_REPORT_CARDS, 
  ReportCardData,
  ClassOption 
} from '../../data/reportCardsData';

interface GenerateReportCardModalProps {
  isOpen: boolean;
  onClose: () => void;
  onGenerate: (data: ReportCardData) => void;
}

export const GenerateReportCardModal: React.FC<GenerateReportCardModalProps> = ({
  isOpen,
  onClose,
  onGenerate,
}) => {
  const [selectedClassId, setSelectedClassId] = useState<string>('CLS-9');
  const [selectedSection, setSelectedSection] = useState<string>('Section A');
  const [selectedStudentKey, setSelectedStudentKey] = useState<string>('ali-khan');
  const [selectedTerm, setSelectedTerm] = useState<string>('Midterm Examination 2026');

  // Sync section and student when class changes
  useEffect(() => {
    const currentClass = CLASSES_LIST.find((c: ClassOption) => c.id === selectedClassId) || CLASSES_LIST[0];
    if (currentClass) {
      setSelectedSection(currentClass.sections[0] || 'Section A');
      if (currentClass.students.length > 0) {
        setSelectedStudentKey(currentClass.students[0].key);
      }
    }
  }, [selectedClassId]);

  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const currentClass = CLASSES_LIST.find((c: ClassOption) => c.id === selectedClassId) || CLASSES_LIST[0];
  const currentStudents = currentClass.students;
  const previewData = STUDENT_REPORT_CARDS[selectedStudentKey] || STUDENT_REPORT_CARDS['ali-khan'];

  const handleGenerateClick = () => {
    onGenerate({
      ...previewData,
      examTerm: selectedTerm,
    });
  };

  return (
    <div 
      className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div 
        className="relative bg-white w-full max-w-xl rounded-2xl shadow-2xl border border-slate-200 overflow-hidden my-8"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="px-6 py-4 bg-slate-900 text-white flex items-center justify-between shadow-md">
          <div className="flex items-center gap-2">
            <Award className="w-5 h-5 text-amber-400" />
            <div>
              <h3 className="font-bold text-sm">Generate Student Report Card</h3>
              <p className="text-xs text-slate-400">Select grade, class section, and student to generate Sindh Board transcript</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-white rounded-lg transition-colors"
            title="Close (Esc)"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <div className="p-6 space-y-4 text-xs">
          {/* Class / Grade Selector */}
          <div>
            <label className="font-bold text-slate-700 flex items-center gap-1.5 mb-1.5">
              <GraduationCap className="w-4 h-4 text-brand-700" />
              <span>Select Grade Level / Class:</span>
            </label>
            <select
              value={selectedClassId}
              onChange={(e) => setSelectedClassId(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 font-semibold focus:outline-none focus:ring-2 focus:ring-brand-600"
            >
              {CLASSES_LIST.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Section Selector */}
            <div>
              <label className="font-bold text-slate-700 block mb-1.5">
                Section / Room:
              </label>
              <select
                value={selectedSection}
                onChange={(e) => setSelectedSection(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 font-semibold focus:outline-none focus:ring-2 focus:ring-brand-600"
              >
                {currentClass.sections.map((sec) => (
                  <option key={sec} value={sec}>
                    {sec}
                  </option>
                ))}
              </select>
            </div>

            {/* Exam Term */}
            <div>
              <label className="font-bold text-slate-700 flex items-center gap-1 mb-1.5">
                <Calendar className="w-3.5 h-3.5 text-slate-500" />
                <span>Examination Term:</span>
              </label>
              <select
                value={selectedTerm}
                onChange={(e) => setSelectedTerm(e.target.value)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 font-semibold focus:outline-none focus:ring-2 focus:ring-brand-600"
              >
                <option value="Midterm Examination 2026">Midterm Examination 2026</option>
                <option value="Annual Final Examination 2027">Annual Final Examination 2027</option>
                <option value="First Term Assessment 2026">First Term Assessment 2026</option>
              </select>
            </div>
          </div>

          {/* Student Selector */}
          <div>
            <label className="font-bold text-slate-700 flex items-center gap-1.5 mb-1.5">
              <User className="w-4 h-4 text-brand-700" />
              <span>Select Student:</span>
            </label>
            <select
              value={selectedStudentKey}
              onChange={(e) => setSelectedStudentKey(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-300 rounded-lg text-slate-800 font-bold focus:outline-none focus:ring-2 focus:ring-brand-600 text-sm"
            >
              {currentStudents.map((st) => (
                <option key={st.key} value={st.key}>
                  {st.name} — Roll: {st.roll} ({st.section})
                </option>
              ))}
            </select>
          </div>

          {/* Student Preview Card */}
          <div className="p-4 bg-brand-50/50 rounded-xl border border-brand-200">
            <div className="flex items-center justify-between pb-2 mb-2 border-b border-brand-100">
              <div>
                <span className="text-[10px] font-bold uppercase tracking-wider text-brand-700">Selected Candidate</span>
                <h4 className="text-base font-extrabold text-brand-950">{previewData.studentName}</h4>
              </div>
              <span className="px-2.5 py-1 rounded bg-emerald-100 text-emerald-800 font-extrabold text-xs">
                {previewData.overallGrade}
              </span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-[11px] text-slate-600">
              <div>
                <span className="text-slate-400 block">Class & Sec:</span>
                <span className="font-bold text-slate-800">{previewData.className.split(' ')[0]} {previewData.className.split(' ')[1]} ({previewData.sectionName})</span>
              </div>
              <div>
                <span className="text-slate-400 block">Roll Number:</span>
                <span className="font-bold text-slate-800 font-mono">{previewData.rollNumber}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Reg Number:</span>
                <span className="font-bold text-slate-800 font-mono">{previewData.admissionNumber}</span>
              </div>
              <div>
                <span className="text-slate-400 block">Term Aggregate:</span>
                <span className="font-bold text-brand-900">{previewData.overallObtained}/{previewData.overallTotal} ({previewData.overallPercentage}%)</span>
              </div>
            </div>
            <p className="mt-2 text-[10px] text-slate-500 italic border-t border-brand-100/60 pt-1.5">
              "{previewData.overallRemarks}"
            </p>
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-between">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 rounded-lg text-xs font-semibold transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleGenerateClick}
            className="px-5 py-2 bg-brand-700 hover:bg-brand-800 text-white font-bold rounded-lg text-xs shadow-md transition-colors flex items-center gap-1.5"
          >
            <FileText className="w-4 h-4 text-amber-300" />
            <span>Generate Official Report Card</span>
          </button>
        </div>
      </div>
    </div>
  );
};
