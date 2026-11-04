import React, { useState } from 'react';
import { Search } from 'lucide-react';

export const AcademicsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'classes' | 'subjects' | 'faculty'>('classes');
  const [searchQuery, setSearchQuery] = useState('');

  const classes = [
    {
      id: 'cls-9',
      name: 'Class 9 (Matriculation)',
      code: 'CLS-9',
      stage: 'SECONDARY',
      badge: 'bg-purple-100 text-purple-800',
      curriculum: 'Sindh Textbook Board Matriculation Part I',
      sections: [
        { name: 'Section 9-A', room: 'Room 201', capacity: 35, enrolled: 32, teacher: 'Sir Ahmed Khan' },
        { name: 'Section 9-B', room: 'Room 202', capacity: 35, enrolled: 28, teacher: 'Miss Fatima Shah' },
      ],
    },
    {
      id: 'cls-10',
      name: 'Class 10 (Matriculation)',
      code: 'CLS-10',
      stage: 'SECONDARY',
      badge: 'bg-purple-100 text-purple-800',
      curriculum: 'Sindh Textbook Board Matriculation Part II',
      sections: [
        { name: 'Section 10-A', room: 'Room 301', capacity: 35, enrolled: 34, teacher: 'Miss Fatima Shah' },
      ],
    },
    {
      id: 'cls-8',
      name: 'Class 8 (Middle)',
      code: 'CLS-8',
      stage: 'MIDDLE',
      badge: 'bg-emerald-100 text-emerald-800',
      curriculum: 'Middle School Secondary Foundation',
      sections: [
        { name: 'Rose', room: 'Room 101', capacity: 30, enrolled: 29, teacher: 'Sir Asif' },
      ],
    },
    {
      id: 'cls-5',
      name: 'Class 5 (Primary)',
      code: 'CLS-5',
      stage: 'PRIMARY',
      badge: 'bg-blue-100 text-blue-800',
      curriculum: 'Primary School Certificate Framework',
      sections: [
        { name: 'Jasmine', room: 'Room 102', capacity: 30, enrolled: 25, teacher: 'Miss Hina' },
      ],
    },
  ];

  const subjects = [
    { code: 'MATH-9', name: 'Mathematics', classification: 'Compulsory', marks: 100, faculty: 'Sir Ahmed Khan (M.Sc)', syllabus: 'Ch 1 Matrices to Ch 16 Theorems' },
    { code: 'PHY-9', name: 'Physics (Theory + Practical)', classification: 'Compulsory Science', marks: 100, faculty: 'Miss Fatima Shah (M.Sc)', syllabus: 'Physical Quantities, Kinematics, Dynamics' },
    { code: 'CHM-9', name: 'Chemistry (Theory + Practical)', classification: 'Compulsory Science', marks: 100, faculty: 'Sir Asif (B.S)', syllabus: 'Atomic Structure, Periodic Table, Chemical Bonding' },
    { code: 'ENG-9', name: 'English Compulsory', classification: 'Language Compulsory', marks: 100, faculty: 'Miss Ayesha (M.A)', syllabus: 'Grammar, Essay Writing, Prose & Poetry' },
    { code: 'SND-9', name: 'Sindhi Salees', classification: 'Regional Compulsory', marks: 100, faculty: 'Sir Ghulam Nabi', syllabus: 'Sindhi Literature, Grammar, Comprehension' },
    { code: 'CS-9', name: 'Computer Science', classification: 'Elective Science', marks: 100, faculty: 'Sir Asif (B.S)', syllabus: 'Fundamentals, Operating Systems, Office Automation' },
    { code: 'ISL-9', name: 'Islamiat Compulsory', classification: 'Compulsory Ethics', marks: 50, faculty: 'Qari Abdul Rasheed', syllabus: 'Surah Al-Anfal, Hadith, Basic Islamic Teachings' },
    { code: 'PST-9', name: 'Pakistan Studies', classification: 'Social Sciences', marks: 50, faculty: 'Sir Mansoor', syllabus: 'Ideology of Pakistan, Historical Landmarks' },
  ];

  const filteredSubjects = subjects.filter(s => 
    s.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
    s.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
    s.faculty.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-brand-700 border border-brand-200">
            Active Session: 2026-2027 • BISE Hyderabad Affiliated
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            Academic Structure & Curriculum
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            Class levels, sections, subjects, credit weights, and syllabus tracking for Latifabad Campus.
          </p>
        </div>

        {/* Tab Buttons */}
        <div className="flex bg-[#F1F5F9] p-1 rounded-xl">
          <button 
            onClick={() => setActiveTab('classes')}
            className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
              activeTab === 'classes' ? 'bg-white text-[#123B70] shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Classes & Sections
          </button>
          <button 
            onClick={() => setActiveTab('subjects')}
            className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
              activeTab === 'subjects' ? 'bg-white text-[#123B70] shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Curriculum & Subjects
          </button>
          <button 
            onClick={() => setActiveTab('faculty')}
            className={`px-3 py-1.5 text-xs font-bold rounded-lg transition-all ${
              activeTab === 'faculty' ? 'bg-white text-[#123B70] shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Faculty Allocations
          </button>
        </div>
      </div>

      {/* Classes & Sections Tab */}
      {activeTab === 'classes' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
          {classes.map((cls) => (
            <div key={cls.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between">
                  <span className={`px-2 py-0.5 text-[10px] font-bold rounded-md ${cls.badge}`}>
                    {cls.stage}
                  </span>
                  <span className="text-xs font-mono font-semibold text-slate-400">{cls.code}</span>
                </div>
                <h3 className="text-base font-bold text-slate-800 mt-3">{cls.name}</h3>
                <p className="text-xs text-slate-500 mt-1">{cls.curriculum}</p>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs">
                {cls.sections.map((sec, idx) => (
                  <div key={idx} className="p-2.5 rounded-xl bg-[#F8FAFC] border border-slate-100 space-y-1">
                    <div className="flex justify-between font-bold text-slate-800">
                      <span>{sec.name} ({sec.room})</span>
                      <span className="text-brand-700">{sec.enrolled}/{sec.capacity}</span>
                    </div>
                    <p className="text-[11px] text-slate-500">Class Teacher: <strong>{sec.teacher}</strong></p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Curriculum & Subjects Tab */}
      {activeTab === 'subjects' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 sm:p-5 border-b border-slate-200 bg-[#F8FAFC] flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 className="text-sm font-bold text-slate-800">Class 9 Matriculation Curriculum (Sindh Board)</h2>
              <p className="text-xs text-slate-500">Board of Intermediate & Secondary Education (BISE) Hyderabad Syllabus</p>
            </div>
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
              <input
                type="text"
                placeholder="Search subject or faculty..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 w-full sm:w-64 bg-white"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="p-3.5">Subject Code</th>
                  <th className="p-3.5">Subject Title</th>
                  <th className="p-3.5">Classification</th>
                  <th className="p-3.5">Max Marks</th>
                  <th className="p-3.5">Course Instructor</th>
                  <th className="p-3.5">Core Syllabus Focus</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredSubjects.map((sub) => (
                  <tr key={sub.code} className="hover:bg-slate-50/70 transition-colors">
                    <td className="p-3.5 font-mono font-bold text-brand-800">{sub.code}</td>
                    <td className="p-3.5 font-bold text-slate-800">{sub.name}</td>
                    <td className="p-3.5">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-blue-50 text-blue-700 border border-blue-200">
                        {sub.classification}
                      </span>
                    </td>
                    <td className="p-3.5 font-black text-slate-800">{sub.marks}</td>
                    <td className="p-3.5 text-slate-700 font-medium">{sub.faculty}</td>
                    <td className="p-3.5 text-slate-500">{sub.syllabus}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Faculty Allocations Tab */}
      {activeTab === 'faculty' && (
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-slate-800 mb-2">Teaching Faculty & Subject Allocations</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-200 space-y-2">
              <p className="font-bold text-slate-800 text-sm">Sir Ahmed Khan</p>
              <p className="text-xs text-brand-700 font-semibold">Senior Faculty • Mathematics</p>
              <div className="text-xs text-slate-600 space-y-1 pt-2 border-t border-slate-200">
                <p>• Class 9-A: Mathematics (Class Teacher)</p>
                <p>• Class 10-A: Mathematics</p>
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-200 space-y-2">
              <p className="font-bold text-slate-800 text-sm">Miss Fatima Shah</p>
              <p className="text-xs text-brand-700 font-semibold">Senior Faculty • Physics & Labs</p>
              <div className="text-xs text-slate-600 space-y-1 pt-2 border-t border-slate-200">
                <p>• Class 9-A: Physics & Practical Lab</p>
                <p>• Class 10-A: Physics (Class Teacher)</p>
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-200 space-y-2">
              <p className="font-bold text-slate-800 text-sm">Sir Asif</p>
              <p className="text-xs text-brand-700 font-semibold">Faculty • Chemistry & Computer</p>
              <div className="text-xs text-slate-600 space-y-1 pt-2 border-t border-slate-200">
                <p>• Class 9-A: Chemistry & Computer</p>
                <p>• Class 8 Rose: Science & Computing</p>
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-[#F8FAFC] border border-slate-200 space-y-2">
              <p className="font-bold text-slate-800 text-sm">Huma Arif</p>
              <p className="text-xs text-brand-700 font-semibold">Academic Principal</p>
              <div className="text-xs text-slate-600 space-y-1 pt-2 border-t border-slate-200">
                <p>• Institutional Quality Assurance</p>
                <p>• Examination Moderation & Approval</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
