import { useState, useEffect, useCallback } from 'react';
import { 
  Download, 
  Search, 
  Building2, 
  ShieldCheck, 
  Award, 
  PlusCircle, 
  CheckSquare, 
  Printer 
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { NewAdmissionModal } from '../components/modals/NewAdmissionModal';
import { MarkAttendanceModal } from '../components/modals/MarkAttendanceModal';
import { RecordPaymentModal } from '../components/modals/RecordPaymentModal';
import { FeeChallanModal } from '../components/modals/FeeChallanModal';
import { ReportCardModal } from '../components/modals/ReportCardModal';
import { GenerateReportCardModal } from '../components/modals/GenerateReportCardModal';
import { 
  getReportCardByStudentKey, 
  getReportCardByName, 
  ReportCardData 
} from '../data/reportCardsData';
import { ForbiddenPage } from '../components/auth/ForbiddenPage';
import { apiClient } from '../api/client';

async function downloadReportCsv(endpoint: string, filename: string) {
  try {
    const res = await apiClient.get(endpoint, { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([res.data], { type: 'text/csv' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (err) {
    console.error('Failed to export CSV', err);
  }
}

// ============================================================================
// 1. ACADEMICS PAGE
// ============================================================================
export function AcademicsPage() {
  const [activeTab, setActiveTab] = useState<'classes' | 'subjects' | 'faculty'>('classes');

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">
            Active Session: 2026-2027
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">Academic Structure & Curriculum</h2>
          <p className="text-sm text-slate-500">
            Class levels, sections, subjects, and teacher faculty assignments for Latifabad Campus.
          </p>
        </div>
        <div className="flex bg-slate-100 p-1 rounded-lg">
          <button 
            onClick={() => setActiveTab('classes')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${activeTab === 'classes' ? 'bg-white text-brand-900 shadow' : 'text-slate-600'}`}
          >
            Classes & Sections
          </button>
          <button 
            onClick={() => setActiveTab('subjects')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${activeTab === 'subjects' ? 'bg-white text-brand-900 shadow' : 'text-slate-600'}`}
          >
            Curriculum
          </button>
          <button 
            onClick={() => setActiveTab('faculty')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${activeTab === 'faculty' ? 'bg-white text-brand-900 shadow' : 'text-slate-600'}`}
          >
            Faculty Allocations
          </button>
        </div>
      </div>

      {activeTab === 'classes' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="px-2 py-1 text-xs font-bold rounded bg-purple-100 text-purple-800">SECONDARY</span>
              <span className="text-xs text-slate-400">CLS-9</span>
            </div>
            <h3 className="text-lg font-bold text-slate-800 mt-3">Class 9 (Matric)</h3>
            <p className="text-xs text-slate-500 mt-1">Sindh Textbook Board Matriculation Part I</p>
            <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs">
              <div className="flex justify-between text-slate-600">
                <span>Section 9-A (Room 201):</span>
                <span className="font-semibold text-slate-800">32 / 35 Students</span>
              </div>
              <div className="flex justify-between text-slate-600">
                <span>Section 9-B (Room 202):</span>
                <span className="font-semibold text-slate-800">28 / 35 Students</span>
              </div>
              <div className="text-brand-700 font-semibold pt-1">Class Teacher: Sir Ahmed Khan</div>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="px-2 py-1 text-xs font-bold rounded bg-purple-100 text-purple-800">SECONDARY</span>
              <span className="text-xs text-slate-400">CLS-10</span>
            </div>
            <h3 className="text-lg font-bold text-slate-800 mt-3">Class 10 (Matric)</h3>
            <p className="text-xs text-slate-500 mt-1">Sindh Textbook Board Matriculation Part II</p>
            <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs">
              <div className="flex justify-between text-slate-600">
                <span>Section 10-A (Room 301):</span>
                <span className="font-semibold text-slate-800">34 / 35 Students</span>
              </div>
              <div className="text-brand-700 font-semibold pt-1">Class Teacher: Miss Fatima Shah</div>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="px-2 py-1 text-xs font-bold rounded bg-emerald-100 text-emerald-800">MIDDLE</span>
              <span className="text-xs text-slate-400">CLS-8</span>
            </div>
            <h3 className="text-lg font-bold text-slate-800 mt-3">Class 8</h3>
            <p className="text-xs text-slate-500 mt-1">Middle School Secondary Prep</p>
            <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs">
              <div className="flex justify-between text-slate-600">
                <span>Section Rose (Room 101):</span>
                <span className="font-semibold text-slate-800">29 / 30 Students</span>
              </div>
              <div className="text-brand-700 font-semibold pt-1">Class Teacher: Sir Asif</div>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="px-2 py-1 text-xs font-bold rounded bg-blue-100 text-blue-800">PRIMARY</span>
              <span className="text-xs text-slate-400">CLS-5</span>
            </div>
            <h3 className="text-lg font-bold text-slate-800 mt-3">Class 5</h3>
            <p className="text-xs text-slate-500 mt-1">Primary School Certificate</p>
            <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs">
              <div className="flex justify-between text-slate-600">
                <span>Section Jasmine:</span>
                <span className="font-semibold text-slate-800">25 / 30 Students</span>
              </div>
              <div className="text-brand-700 font-semibold pt-1">Class Teacher: Miss Hina</div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'subjects' && (
        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
          <div className="p-4 border-b border-slate-200 bg-slate-50">
            <h3 className="text-sm font-bold text-slate-800">Class 9 Matriculation Curriculum (Sindh Board)</h3>
          </div>
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3">Code</th>
                <th className="p-3">Subject Name</th>
                <th className="p-3">Classification</th>
                <th className="p-3">Total Marks</th>
                <th className="p-3">Assigned Faculty</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">MATH-9</td>
                <td className="p-3 font-semibold text-slate-800">Mathematics</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700">Compulsory</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Sir Ahmed Khan (M.Sc)</td>
              </tr>
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">PHY-9</td>
                <td className="p-3 font-semibold text-slate-800">Physics (Theory + Practical)</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700">Compulsory</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Miss Fatima Shah (M.Sc)</td>
              </tr>
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">CHM-9</td>
                <td className="p-3 font-semibold text-slate-800">Chemistry</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700">Compulsory</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Sir Asif (B.S)</td>
              </tr>
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">ENG-9</td>
                <td className="p-3 font-semibold text-slate-800">English Compulsory</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700">Compulsory</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Miss Ayesha (M.A)</td>
              </tr>
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">SND-9</td>
                <td className="p-3 font-semibold text-slate-800">Sindhi Salees</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-emerald-50 text-emerald-700">Regional Compulsory</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Sir Ghulam Nabi</td>
              </tr>
              <tr>
                <td className="p-3 font-mono font-semibold text-slate-700">CS-9</td>
                <td className="p-3 font-semibold text-slate-800">Computer Science</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-amber-50 text-amber-700">Elective Science</span></td>
                <td className="p-3 font-bold text-slate-800">100</td>
                <td className="p-3 text-slate-600">Sir Asif</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'faculty' && (
        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <h3 className="text-base font-bold text-slate-800 mb-4">Teaching Faculty Assignments</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="p-4 rounded-lg bg-slate-50 border border-slate-200">
              <p className="font-bold text-slate-800">Sir Ahmed Khan</p>
              <p className="text-xs text-slate-500">Senior Faculty • Mathematics</p>
              <div className="mt-2 text-xs text-slate-600 space-y-1">
                <p>• Class 9-A: Mathematics (Class Teacher)</p>
                <p>• Class 10-A: Mathematics</p>
              </div>
            </div>
            <div className="p-4 rounded-lg bg-slate-50 border border-slate-200">
              <p className="font-bold text-slate-800">Miss Fatima Shah</p>
              <p className="text-xs text-slate-500">Senior Faculty • Physics</p>
              <div className="mt-2 text-xs text-slate-600 space-y-1">
                <p>• Class 9-A: Physics</p>
                <p>• Class 10-A: Physics (Class Teacher)</p>
              </div>
            </div>
            <div className="p-4 rounded-lg bg-slate-50 border border-slate-200">
              <p className="font-bold text-slate-800">Hana Khan</p>
              <p className="text-xs text-slate-500">Vice Principal</p>
              <div className="mt-2 text-xs text-slate-600 space-y-1">
                <p>• Academic Coordination & Timetable</p>
                <p>• Faculty & Student Affairs</p>
              </div>
            </div>
            <div className="p-4 rounded-lg bg-slate-50 border border-slate-200">
              <p className="font-bold text-slate-800">Huma Arif</p>
              <p className="text-xs text-slate-500">Academic Principal</p>
              <div className="mt-2 text-xs text-slate-600 space-y-1">
                <p>• Overall Institutional Supervision</p>
                <p>• Examination Moderation & Approval</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// ============================================================================
// 2. ADMISSIONS PAGE
// ============================================================================
export function AdmissionsPage() {
  const [showModal, setShowModal] = useState(false);
  const applications = [
    { id: 'APP-2026-0001', name: 'Shahid Afridi', class: 'Class 9', guardian: 'Abdul Rehman Afridi', phone: '+92 333 7654321', status: 'ENROLLED', badge: 'bg-emerald-100 text-emerald-800', note: 'Enrolled: BSS-2026-0003' },
    { id: 'APP-2026-0002', name: 'Hamza Tariq', class: 'Class 5', guardian: 'Tariq Mahmood', phone: '+92 321 9876543', status: 'APPROVED', badge: 'bg-blue-100 text-blue-800', note: 'Passed admission test (88%)' },
    { id: 'APP-2026-0003', name: 'Zainab Fatima', class: 'Class 9', guardian: 'Dr. Mansoor', phone: '+92 345 5566778', status: 'SUBMITTED', badge: 'bg-amber-100 text-amber-800', note: 'Awaiting interview date' },
  ];

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
            Admissions 2026-2027 Open
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">Student Admissions & Inquiries</h2>
          <p className="text-sm text-slate-500">
            Online admission workflow, B-Form verification, entrance assessment, and enrollment conversion.
          </p>
        </div>
        <button 
          onClick={() => setShowModal(true)}
          className="px-4 py-2 bg-brand-700 text-white font-semibold rounded-lg shadow text-sm hover:bg-brand-800 transition-colors flex items-center gap-1.5"
        >
          <PlusCircle className="w-4 h-4" />
          <span>+ New Admission Application</span>
        </button>
      </div>

      <NewAdmissionModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onSuccess={() => {}}
      />

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-medium text-slate-500">Total Applications</span>
          <div className="text-2xl font-bold text-slate-800 mt-2">48</div>
          <p className="text-xs text-emerald-600 mt-1">Session 2026-2027</p>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-medium text-slate-500">Enrolled Students</span>
          <div className="text-2xl font-bold text-emerald-600 mt-2">32</div>
          <p className="text-xs text-slate-500 mt-1">Assigned to Sections</p>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-medium text-slate-500">In Review / Testing</span>
          <div className="text-2xl font-bold text-amber-600 mt-2">16</div>
          <p className="text-xs text-slate-500 mt-1">Pending Entrance Assessment</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="p-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-800">Recent Applications</h3>
          <div className="relative">
            <Search className="w-4 h-4 text-slate-400 absolute left-2.5 top-2" />
            <input 
              type="text" 
              placeholder="Search applicant..." 
              className="pl-8 pr-3 py-1 text-xs border border-slate-200 rounded-md focus:outline-none focus:ring-1 focus:ring-brand-500"
            />
          </div>
        </div>
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3">Application #</th>
              <th className="p-3">Applicant Name</th>
              <th className="p-3">Applied Class</th>
              <th className="p-3">Guardian Name</th>
              <th className="p-3">Phone</th>
              <th className="p-3">Status</th>
              <th className="p-3">Notes</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {applications.map((app) => (
              <tr key={app.id}>
                <td className="p-3 font-mono font-bold text-brand-700">{app.id}</td>
                <td className="p-3 font-semibold text-slate-800">{app.name}</td>
                <td className="p-3 text-slate-600">{app.class}</td>
                <td className="p-3 text-slate-600">{app.guardian}</td>
                <td className="p-3 text-slate-500">{app.phone}</td>
                <td className="p-3">
                  <span className={`px-2 py-0.5 rounded-full font-semibold ${app.badge}`}>
                    {app.status}
                  </span>
                </td>
                <td className="p-3 text-slate-500 italic">{app.note}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ============================================================================
// 3. STUDENTS & PARENTS PAGE
// ============================================================================
interface DisplayStudent {
  id: string;
  name: string;
  class: string;
  roll: string;
  gender: string;
  blood: string;
  guardian: string;
  phone: string;
  status: string;
}

const DEFAULT_STUDENTS: DisplayStudent[] = [
  { id: 'BSS-2026-0001', name: 'Ali Khan', class: 'Class 9-A', roll: '1', gender: 'Male', blood: 'B+', guardian: 'Mohammad Tariq Khan', phone: '+92 300 1234567', status: 'ACTIVE' },
  { id: 'BSS-2026-0002', name: 'Sara Khan', class: 'Class 8-A', roll: '1', gender: 'Female', blood: 'O+', guardian: 'Abdul Hameed Khan', phone: '+92 300 9876543', status: 'ACTIVE' },
  { id: 'BSS-2026-0003', name: 'Shahid Afridi', class: 'Class 9-A', roll: '2', gender: 'Male', blood: 'A+', guardian: 'Abdul Rehman Afridi', phone: '+92 333 7654321', status: 'ACTIVE' },
  { id: 'BSS-2026-0004', name: 'Ayesha Siddiqui', class: 'Class 9-A', roll: '3', gender: 'Female', blood: 'B+', guardian: 'Dr. Siddiqui', phone: '+92 301 9876543', status: 'ACTIVE' },
  { id: 'BSS-2026-0005', name: 'Bilal Hassan', class: 'Class 9-B', roll: '1', gender: 'Male', blood: 'AB+', guardian: 'Hassan Raza', phone: '+92 302 4455667', status: 'ACTIVE' },
];

export function StudentsPage() {
  const [students, setStudents] = useState<DisplayStudent[]>(DEFAULT_STUDENTS);
  const [searchTerm, setSearchTerm] = useState('');
  const [exporting, setExporting] = useState(false);

  const loadStudents = useCallback(async (query = '') => {
    try {
      const res = await apiClient.get('/students', { params: query ? { query } : {} });
      const items = res.data?.data?.content || res.data?.data;
      if (Array.isArray(items) && items.length > 0) {
        interface RawStudent {
          admissionNumber: string;
          fullName?: string;
          firstName?: string;
          lastName?: string;
          activeClassName?: string;
          activeSectionName?: string;
          rollNumber?: string;
          gender?: string;
          bloodGroup?: string;
          status?: string;
        }
        const mapped = items.map((st: RawStudent) => ({
          id: st.admissionNumber,
          name: st.fullName || `${st.firstName || ''} ${st.lastName || ''}`.trim() || 'Student',
          class: st.activeClassName ? `${st.activeClassName}${st.activeSectionName ? ' - ' + st.activeSectionName : ''}` : 'Enrolled',
          roll: st.rollNumber || '01',
          gender: st.gender || 'N/A',
          blood: st.bloodGroup || 'O+',
          guardian: 'Parent / Guardian',
          phone: '+92 316 9681932',
          status: st.status || 'ACTIVE'
        }));
        setStudents(mapped);
      }
    } catch {
      // Keep default fallback
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      loadStudents(searchTerm);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchTerm, loadStudents]);

  const handleExportCsv = async () => {
    setExporting(true);
    await downloadReportCsv('/reports/students/export-csv', 'students_roster_sanitized.csv');
    setExporting(false);
  };

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Students & Guardian Directory</h2>
          <p className="text-sm text-slate-500">
            Registered students, parent-child links, emergency contacts, and enrollment profiles.
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-2.5 top-2.5" />
            <input
              type="text"
              placeholder="Search students..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-8 pr-3 py-1.5 text-xs border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-brand-500 w-44"
            />
          </div>
          <button 
            onClick={handleExportCsv}
            disabled={exporting}
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" /> {exporting ? 'Exporting...' : 'Export Directory (CSV)'}
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3">Student Reg #</th>
              <th className="p-3">Full Name</th>
              <th className="p-3">Class & Section</th>
              <th className="p-3">Roll #</th>
              <th className="p-3">Blood Group</th>
              <th className="p-3">Primary Guardian</th>
              <th className="p-3">Contact Phone</th>
              <th className="p-3">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {students.map((st) => (
              <tr key={st.id} className="hover:bg-slate-50/50">
                <td className="p-3 font-mono font-bold text-brand-700">{st.id}</td>
                <td className="p-3 font-semibold text-slate-800">{st.name}</td>
                <td className="p-3 text-slate-600">{st.class}</td>
                <td className="p-3 font-bold text-slate-700">{st.roll}</td>
                <td className="p-3"><span className="px-1.5 py-0.5 rounded bg-red-50 text-red-700 font-semibold">{st.blood}</span></td>
                <td className="p-3 text-slate-700">{st.guardian}</td>
                <td className="p-3 text-slate-500">{st.phone}</td>
                <td className="p-3">
                  <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
                    {st.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ============================================================================
// 4. TIMETABLE PAGE
// ============================================================================
export function TimetablePage() {
  const schedule = [
    { period: 'Period 1 (08:30 - 09:15)', mon: 'Mathematics (Sir Ahmed)', tue: 'Mathematics (Sir Ahmed)', wed: 'Mathematics (Sir Ahmed)', thu: 'Mathematics (Sir Ahmed)', fri: 'Mathematics (Sir Ahmed)' },
    { period: 'Period 2 (09:15 - 10:00)', mon: 'Physics (Miss Fatima)', tue: 'Physics (Miss Fatima)', wed: 'Urdu (Sir Ghulam)', thu: 'Chemistry (Sir Asif)', fri: 'Pakistan Studies' },
    { period: 'Break (10:00 - 10:30)', mon: 'Refreshment Break', tue: 'Refreshment Break', wed: 'Refreshment Break', thu: 'Refreshment Break', fri: 'Refreshment Break' },
    { period: 'Period 3 (10:30 - 11:15)', mon: 'English (Miss Ayesha)', tue: 'English (Miss Ayesha)', wed: 'Physics Lab', thu: 'Sindhi Salees', fri: 'Islamiat' },
    { period: 'Period 4 (11:15 - 12:00)', mon: 'Computer Science', tue: 'Biology', wed: 'Physics Lab', thu: 'Computer Lab', fri: 'Jummah Prayer Assembly' },
  ];

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">
            Room 201 • Capacity 35
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">Class 9-A Weekly Timetable</h2>
          <p className="text-sm text-slate-500">
            3-way clash-checked automated schedule. Instructor: Sir Ahmed Khan.
          </p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-600 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3 w-40">Period & Timing</th>
              <th className="p-3">Monday</th>
              <th className="p-3">Tuesday</th>
              <th className="p-3">Wednesday</th>
              <th className="p-3">Thursday</th>
              <th className="p-3">Friday</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {schedule.map((row, idx) => (
              <tr key={idx} className={row.period.includes('Break') ? 'bg-amber-50/50 font-semibold text-amber-900' : ''}>
                <td className="p-3 font-semibold text-slate-700 bg-slate-50">{row.period}</td>
                <td className="p-3 text-slate-800">{row.mon}</td>
                <td className="p-3 text-slate-800">{row.tue}</td>
                <td className="p-3 text-slate-800">{row.wed}</td>
                <td className="p-3 text-slate-800">{row.thu}</td>
                <td className="p-3 text-slate-800">{row.fri}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ============================================================================
// 5. ATTENDANCE PAGE
// ============================================================================
export function AttendancePage() {
  const { user } = useAuth();
  const [showMarkModal, setShowMarkModal] = useState(false);
  const [exporting, setExporting] = useState(false);
  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const handleExportAttendance = async () => {
    setExporting(true);
    await downloadReportCsv('/reports/attendance/export-csv', 'attendance_log.csv');
    setExporting(false);
  };

  const allAttendance = [
    { date: '2026-10-28', student: 'Ali Khan', roll: '1', class: '9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time' },
    { date: '2026-10-28', student: 'Shahid Afridi', roll: '2', class: '9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time' },
    { date: '2026-10-28', student: 'Ayesha Siddiqui', roll: '3', class: '9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'On time' },
    { date: '2026-10-27', student: 'Ali Khan', roll: '1', class: '9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'Lab demonstration' },
    { date: '2026-10-27', student: 'Shahid Afridi', roll: '2', class: '9-A', status: 'LATE', badge: 'bg-amber-100 text-amber-800', remark: 'Autobahn traffic delay' },
    { date: '2026-10-26', student: 'Ali Khan', roll: '1', class: '9-A', status: 'PRESENT', badge: 'bg-emerald-100 text-emerald-800', remark: 'Regular session' },
    { date: '2026-10-26', student: 'Ayesha Siddiqui', roll: '3', class: '9-A', status: 'EXCUSED', badge: 'bg-blue-100 text-blue-800', remark: 'Medical appointment' },
  ];

  const attendance = isStudent 
    ? allAttendance.filter(a => a.student === (user?.fullName || 'Ali Khan'))
    : (isParent ? allAttendance.filter(a => a.student === 'Ali Khan') : allAttendance);

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
            Attendance Rate: {isStudent ? '96.2%' : '94.8%'}
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">
            {isStudent ? 'My Daily Attendance Record' : (isParent ? 'Student Attendance Tracking' : 'Daily Attendance & Tracking')}
          </h2>
          <p className="text-sm text-slate-500">
            {isStudent 
              ? 'Official classroom attendance log certified by Class Teacher (Sir Ahmed Khan).' 
              : 'Real-time roll call, bulk attendance marking, and correction audit logging.'}
          </p>
        </div>
        {!isStudent && !isParent && (
          <div className="flex flex-wrap items-center gap-2">
            <button 
              onClick={handleExportAttendance}
              disabled={exporting}
              className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-lg text-xs transition-colors flex items-center gap-1.5 disabled:opacity-50"
            >
              <Download className="w-4 h-4" />
              <span>{exporting ? 'Exporting...' : 'Export Attendance Log'}</span>
            </button>
            <button 
              onClick={() => setShowMarkModal(true)}
              className="px-4 py-2 bg-brand-700 text-white font-semibold rounded-lg shadow text-xs hover:bg-brand-800 transition-colors flex items-center gap-1.5"
            >
              <CheckSquare className="w-4 h-4" />
              <span>Mark Today's Attendance</span>
            </button>
          </div>
        )}
      </div>

      <MarkAttendanceModal
        isOpen={showMarkModal}
        onClose={() => setShowMarkModal(false)}
        onSuccess={() => {}}
      />

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="p-4 border-b border-slate-200 bg-slate-50">
          <h3 className="text-sm font-bold text-slate-800">Recent Attendance Records (Latifabad Campus)</h3>
        </div>
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3">Date</th>
              <th className="p-3">Student Name</th>
              <th className="p-3">Roll #</th>
              <th className="p-3">Section</th>
              <th className="p-3">Status</th>
              <th className="p-3">Teacher Remark</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {attendance.map((att, idx) => (
              <tr key={idx}>
                <td className="p-3 font-mono text-slate-600">{att.date}</td>
                <td className="p-3 font-semibold text-slate-800">{att.student}</td>
                <td className="p-3 font-bold text-slate-700">{att.roll}</td>
                <td className="p-3 text-slate-600">{att.class}</td>
                <td className="p-3">
                  <span className={`px-2 py-0.5 rounded-full font-semibold ${att.badge}`}>
                    {att.status}
                  </span>
                </td>
                <td className="p-3 text-slate-500">{att.remark}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ============================================================================
// 6. LMS & COURSES PAGE (Full-featured Master LMS)
// ============================================================================
export { LmsPage } from './lms/LmsPage';


// ============================================================================
// 7. EXAMINATIONS & REPORT CARDS PAGE
// ============================================================================
export function ExaminationsPage() {
  const { user } = useAuth();
  const [showReportCard, setShowReportCard] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedCardData, setSelectedCardData] = useState<ReportCardData | undefined>(undefined);
  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const allReportCards = [
    { rank: 1, key: 'ali-khan', name: 'Ali Khan', id: 'BSS-2024-0104', section: '9-A', obtained: '672.00', total: '750.00', pct: '89.60%', grade: 'A-1', att: '96.2%', remark: 'Outstanding performance. First Position.' },
    { rank: 2, key: 'shahid-afridi', name: 'Shahid Afridi', id: 'BSS-2026-0003', section: '9-A', obtained: '626.00', total: '750.00', pct: '83.50%', grade: 'A-1', att: '92.3%', remark: 'Very good conceptual clarity and analytical grasp.' },
    { rank: 3, key: 'ayesha-siddiqui', name: 'Ayesha Siddiqui', id: 'BSS-2026-0004', section: '9-A', obtained: '601.00', total: '750.00', pct: '80.10%', grade: 'A-1', att: '94.2%', remark: 'Diligent and consistent academic excellence.' },
  ];

  const reportCards = isStudent
    ? allReportCards.filter(r => r.name.toLowerCase().includes(user?.fullName?.toLowerCase() || 'ali khan'))
    : (isParent ? allReportCards.filter(r => r.name === 'Ali Khan') : allReportCards);

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
            Results Published & Locked
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">
            {isStudent ? 'My Midterm Examination Transcript' : 'Mid-Term Examination 2026-2027'}
          </h2>
          <p className="text-sm text-slate-500">
            Official Sindh Board grading criteria (A+ through F), class rankings, and downloadable report cards.
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
          className="px-4 py-2 bg-brand-700 hover:bg-brand-800 text-white rounded-lg text-xs font-semibold shadow transition-colors flex items-center gap-1.5"
        >
          <Award className="w-4 h-4 text-amber-400" />
          <span>{isStudent ? 'View My Report Card' : 'Generate & Select Report Card'}</span>
        </button>
      </div>

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

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="p-4 border-b border-slate-200 bg-slate-50">
          <h3 className="text-sm font-bold text-slate-800">Published Class 9-A Examination Results</h3>
        </div>
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3">Rank</th>
              <th className="p-3">Student Name</th>
              <th className="p-3">Reg #</th>
              <th className="p-3">Marks Obtained</th>
              <th className="p-3">Total Marks</th>
              <th className="p-3">Percentage</th>
              <th className="p-3">Sindh Board Grade</th>
              <th className="p-3">Attendance</th>
              <th className="p-3">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {reportCards.map((rc) => (
              <tr key={rc.rank}>
                <td className="p-3 font-bold text-brand-700">#{rc.rank}</td>
                <td className="p-3 font-semibold text-slate-800">{rc.name}</td>
                <td className="p-3 font-mono text-slate-600">{rc.id}</td>
                <td className="p-3 font-bold text-emerald-700">{rc.obtained}</td>
                <td className="p-3 text-slate-600">{rc.total}</td>
                <td className="p-3 font-bold text-slate-800">{rc.pct}</td>
                <td className="p-3"><span className="px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 font-bold">{rc.grade}</span></td>
                <td className="p-3 text-slate-600">{rc.att}</td>
                <td className="p-3">
                  <button
                    onClick={() => {
                      setSelectedCardData(getReportCardByStudentKey(rc.key));
                      setShowReportCard(true);
                    }}
                    className="px-2.5 py-1 bg-brand-50 hover:bg-brand-100 text-brand-800 rounded font-semibold text-[11px] transition-colors"
                  >
                    View Report Card
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
        <h3 className="text-xs font-bold text-slate-700 uppercase mb-3">Sindh Board Grading Standard Reference</h3>
        <div className="grid grid-cols-2 sm:grid-cols-6 gap-3 text-xs">
          <div className="p-2.5 rounded bg-emerald-50 border border-emerald-100">
            <span className="font-bold text-emerald-800 text-sm">A+ (80% - 100%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Distinction (4.00 GP)</p>
          </div>
          <div className="p-2.5 rounded bg-blue-50 border border-blue-100">
            <span className="font-bold text-blue-800 text-sm">A (70% - 79.9%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Excellent (3.70 GP)</p>
          </div>
          <div className="p-2.5 rounded bg-purple-50 border border-purple-100">
            <span className="font-bold text-purple-800 text-sm">B (60% - 69.9%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Good (3.00 GP)</p>
          </div>
          <div className="p-2.5 rounded bg-amber-50 border border-amber-100">
            <span className="font-bold text-amber-800 text-sm">C (50% - 59.9%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Satisfactory (2.00 GP)</p>
          </div>
          <div className="p-2.5 rounded bg-orange-50 border border-orange-100">
            <span className="font-bold text-orange-800 text-sm">D (40% - 49.9%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Pass (1.00 GP)</p>
          </div>
          <div className="p-2.5 rounded bg-red-50 border border-red-100">
            <span className="font-bold text-red-800 text-sm">F (&lt; 40%)</span>
            <p className="text-slate-600 text-[11px] mt-0.5">Fail (0.00 GP)</p>
          </div>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 8. FEES & FINANCIAL LEDGER PAGE
// ============================================================================
interface DisplayInvoice {
  num: string;
  student: string;
  month: string;
  total: string;
  paid: string;
  bal: string;
  due: string;
  status: string;
  badge: string;
}

const DEFAULT_INVOICES: DisplayInvoice[] = [
  { num: 'INV-202610-0001', student: 'Ali Khan', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 10,000', bal: 'Rs. 0', due: '2026-10-10', status: 'PAID', badge: 'bg-emerald-100 text-emerald-800' },
  { num: 'INV-202610-0002', student: 'Shahid Afridi', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 5,000', bal: 'Rs. 5,000', due: '2026-10-10', status: 'PARTIAL', badge: 'bg-amber-100 text-amber-800' },
  { num: 'INV-202610-0003', student: 'Bilal Hassan', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 0', bal: 'Rs. 10,000', due: '2026-10-10', status: 'UNPAID', badge: 'bg-red-100 text-red-800' },
];

export function FeesPage() {
  const { user } = useAuth();
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showChallanModal, setShowChallanModal] = useState(false);
  const [selectedChallanInvoice, setSelectedChallanInvoice] = useState<string>('INV-202610-0001');
  const [invoices, setInvoices] = useState<DisplayInvoice[]>(DEFAULT_INVOICES);
  const [exporting, setExporting] = useState(false);
  const isTeacher = user?.roles?.includes('ROLE_TEACHER');
  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const loadInvoices = useCallback(async () => {
    try {
      const endpoint = (isStudent || isParent) ? '/finance/my-invoices' : '/finance/invoices';
      const res = await apiClient.get(endpoint);
      if (res.data?.data && Array.isArray(res.data.data) && res.data.data.length > 0) {
        interface RawInvoice {
          invoiceNumber: string;
          studentName?: string;
          billingMonth: string;
          totalAmount: number;
          paidAmount: number;
          balanceDue: number;
          dueDate: string;
          status: string;
        }
        const mapped = res.data.data.map((inv: RawInvoice) => {
          let badge = 'bg-amber-100 text-amber-800';
          if (inv.status === 'PAID') badge = 'bg-emerald-100 text-emerald-800';
          else if (inv.status === 'UNPAID') badge = 'bg-red-100 text-red-800';
          return {
            num: inv.invoiceNumber,
            student: inv.studentName || 'Enrolled Student',
            month: inv.billingMonth,
            total: `Rs. ${Number(inv.totalAmount).toLocaleString()}`,
            paid: `Rs. ${Number(inv.paidAmount).toLocaleString()}`,
            bal: `Rs. ${Number(inv.balanceDue).toLocaleString()}`,
            due: inv.dueDate,
            status: inv.status,
            badge
          };
        });
        setInvoices(mapped);
      }
    } catch {
      // Keep default fallback
    }
  }, [isStudent, isParent]);

  useEffect(() => {
    if (!isTeacher) {
      loadInvoices();
    }
  }, [isTeacher, loadInvoices]);

  if (isTeacher) {
    return <ForbiddenPage />;
  }

  const handleExportFees = async () => {
    setExporting(true);
    await downloadReportCsv('/reports/fees/export-csv', 'fee_ledger_sanitized.csv');
    setExporting(false);
  };

  const displayedInvoices = (isStudent || isParent)
    ? invoices.filter(inv => inv.student.toLowerCase().includes('ali khan') || inv.num.startsWith('INV-202610-0001') || inv.num.startsWith('INV-202611-0001'))
    : invoices;

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
            Currency: PKR (DECIMAL(12,2))
          </span>
          <h2 className="text-xl font-bold text-slate-800 mt-2">
            {isStudent ? 'My Tuition Fee Vouchers & Receipts' : (isParent ? "Ali Khan's Fee Challans & History" : 'Fee Ledger & Financial Invoicing')}
          </h2>
          <p className="text-sm text-slate-500">
            {isStudent 
              ? 'Bank Alfalah tuition fee challan, monthly invoices, and verified payment receipts.'
              : (isParent
                ? 'Official Bank Alfalah tuition challan, fee record, and payment clearance for Ali Khan (Class 9-A).'
                : 'Class fee structures, monthly tuition challans, FIFO payment allocation, and receipts.')}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          {!isStudent && !isParent && (
            <>
              <button 
                onClick={handleExportFees}
                disabled={exporting}
                className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold rounded-lg text-xs transition-colors flex items-center gap-1.5 disabled:opacity-50"
              >
                <Download className="w-4 h-4" />
                <span>{exporting ? 'Exporting...' : 'Export Fee Ledger'}</span>
              </button>
              <button 
                onClick={() => setShowPaymentModal(true)}
                className="px-4 py-2 bg-brand-700 text-white font-semibold rounded-lg shadow text-xs hover:bg-brand-800 transition-colors flex items-center gap-1.5"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Record Payment</span>
              </button>
            </>
          )}
          <button 
            onClick={() => {
              setSelectedChallanInvoice((isStudent || isParent) ? 'INV-202610-0001' : (invoices[0]?.num || 'INV-202610-0001'));
              setShowChallanModal(true);
            }}
            className="px-4 py-2 bg-purple-700 hover:bg-purple-800 text-white font-semibold rounded-lg shadow text-xs transition-colors flex items-center gap-1.5"
          >
            <Printer className="w-4 h-4" />
            <span>{isStudent ? 'Print My Fee Challan' : (isParent ? "Print Ali's Fee Challan" : 'Print Official Challan Voucher')}</span>
          </button>
        </div>
      </div>

      <RecordPaymentModal
        isOpen={showPaymentModal}
        onClose={() => setShowPaymentModal(false)}
        onSuccess={loadInvoices}
      />

      <FeeChallanModal
        isOpen={showChallanModal}
        onClose={() => setShowChallanModal(false)}
        invoiceNumber={selectedChallanInvoice}
      />

      {/* KPI Cards: Only for Finance Staff / Admin */}
      {!isStudent && !isParent && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-medium text-slate-500">Total Billed (October 2026)</span>
            <div className="text-2xl font-bold text-slate-800 mt-2">Rs. 30,000</div>
            <p className="text-xs text-slate-500 mt-1">3 Active Challans</p>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-medium text-slate-500">Total Collected</span>
            <div className="text-2xl font-bold text-emerald-600 mt-2">Rs. 15,000</div>
            <p className="text-xs text-emerald-600 mt-1">50.0% Collection Rate</p>
          </div>
          <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-medium text-slate-500">Outstanding Receivables</span>
            <div className="text-2xl font-bold text-red-600 mt-2">Rs. 15,000</div>
            <p className="text-xs text-slate-500 mt-1">Pending Clearance</p>
          </div>
        </div>
      )}

      {/* Student / Parent Specific Current Voucher Card */}
      {(isStudent || isParent) && (
        <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-5">
          <div className="flex items-center justify-between">
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-emerald-800">Account Status</span>
              <h3 className="text-xl font-extrabold text-emerald-950 mt-1">
                {isStudent ? 'My Tuition Fees Cleared' : "Ali Khan's Tuition Fees Cleared"}
              </h3>
              <p className="text-xs text-emerald-700 mt-0.5">October 2026 Challan (INV-202610-0001) marked PAID via Online Transfer.</p>
            </div>
            <div className="text-right">
              <span className="text-xs text-emerald-700 font-medium block">Current Balance</span>
              <span className="text-2xl font-black text-emerald-900 font-mono">Rs. 0.00</span>
            </div>
          </div>
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="p-4 border-b border-slate-200 bg-slate-50">
          <h3 className="text-sm font-bold text-slate-800">
            {isStudent ? 'My Monthly Tuition Challans' : (isParent ? "Ali Khan's Monthly Tuition Challans" : 'Monthly Tuition Invoices')}
          </h3>
        </div>
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 uppercase border-b border-slate-200">
            <tr>
              <th className="p-3">Invoice Number</th>
              <th className="p-3">Student Name</th>
              <th className="p-3">Billing Month</th>
              <th className="p-3">Total Amount</th>
              <th className="p-3">Paid Amount</th>
              <th className="p-3">Balance Due</th>
              <th className="p-3">Due Date</th>
              <th className="p-3">Status</th>
              <th className="p-3 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {displayedInvoices.map((inv) => (
              <tr key={inv.num}>
                <td className="p-3 font-mono font-bold text-brand-700">{inv.num}</td>
                <td className="p-3 font-semibold text-slate-800">{inv.student}</td>
                <td className="p-3 text-slate-600">{inv.month}</td>
                <td className="p-3 font-bold text-slate-800">{inv.total}</td>
                <td className="p-3 text-emerald-700 font-semibold">{inv.paid}</td>
                <td className="p-3 text-red-700 font-bold">{inv.bal}</td>
                <td className="p-3 text-slate-500">{inv.due}</td>
                <td className="p-3">
                  <span className={`px-2 py-0.5 rounded-full font-semibold ${inv.badge}`}>
                    {inv.status}
                  </span>
                </td>
                <td className="p-3 text-right">
                  <button
                    onClick={() => {
                      setSelectedChallanInvoice(inv.num);
                      setShowChallanModal(true);
                    }}
                    className="px-2.5 py-1 bg-purple-50 hover:bg-purple-100 text-purple-700 font-semibold rounded text-xs inline-flex items-center gap-1 border border-purple-200 transition-colors"
                    title="View / Print 3-Copy Bank Challan"
                  >
                    <Printer className="w-3.5 h-3.5" />
                    <span>Challan</span>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
        <h3 className="text-xs font-bold text-slate-700 uppercase mb-3">
          {isStudent ? 'My Verified Payment Receipts' : 'Recent Payment Receipts'}
        </h3>
        <div className="space-y-2 text-xs">
          <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 flex items-center justify-between">
            <div>
              <p className="font-bold text-slate-800">REC-202610-0001 • Rs. 10,000</p>
              <p className="text-slate-500">Ali Khan (INV-202610-0001) • Online Bank Transfer (Meezan Bank #PK98234710)</p>
            </div>
            <span className="px-2 py-1 rounded bg-emerald-100 text-emerald-800 font-semibold text-[11px]">CLEARED</span>
          </div>
          {!isStudent && !isParent && (
            <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 flex items-center justify-between">
              <div>
                <p className="font-bold text-slate-800">REC-202610-0002 • Rs. 5,000</p>
                <p className="text-slate-500">Shahid Afridi (INV-202610-0002) • Cash Collection at Accounts Office</p>
              </div>
              <span className="px-2 py-1 rounded bg-emerald-100 text-emerald-800 font-semibold text-[11px]">CLEARED</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 9. REPORTS & ANALYTICS PAGE
// ============================================================================
export function ReportsPage() {
  const [dashboard, setDashboard] = useState<{
    totalStudents: number;
    totalTeachers: number;
    totalFeeInvoiced: number;
    totalFeeCollected: number;
    totalFeeOutstanding: number;
    attendanceRateToday: number;
    currentSessionName: string;
  } | null>(null);
  const [exportingType, setExportingType] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get('/reports/dashboard')
      .then(res => {
        if (res.data?.data) {
          setDashboard(res.data.data);
        }
      })
      .catch(() => {});
  }, []);

  const handleExport = async (type: string, endpoint: string, filename: string) => {
    setExportingType(type);
    await downloadReportCsv(endpoint, filename);
    setExportingType(null);
  };

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800">Executive Reports & Operational Analytics</h2>
          <p className="text-sm text-slate-500">
            Institutional metrics, attendance summaries, financial collection graphs, and sanitized CSV exports.
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <button 
            onClick={() => handleExport('students', '/reports/students/export-csv', 'students_roster.csv')}
            disabled={exportingType !== null}
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{exportingType === 'students' ? 'Exporting...' : 'Export Students CSV'}</span>
          </button>
          <button 
            onClick={() => handleExport('attendance', '/reports/attendance/export-csv', 'attendance_log.csv')}
            disabled={exportingType !== null}
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors disabled:opacity-50"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{exportingType === 'attendance' ? 'Exporting...' : 'Export Attendance CSV'}</span>
          </button>
          <button 
            onClick={() => handleExport('fees', '/reports/fees/export-csv', 'fee_ledger.csv')}
            disabled={exportingType !== null}
            className="px-3 py-1.5 bg-brand-700 hover:bg-brand-800 text-white text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors disabled:opacity-50 shadow-sm"
          >
            <Download className="w-3.5 h-3.5" />
            <span>{exportingType === 'fees' ? 'Exporting...' : 'Export Fee Ledger CSV'}</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-2">Student Enrollment Distribution</h3>
          <p className="text-xs text-slate-500 mb-4">
            {dashboard ? `${dashboard.totalStudents} enrolled students • ${dashboard.totalTeachers} faculty members` : 'Total: 482 active students across Latifabad campus'}
          </p>
          <div className="space-y-2 text-xs">
            <div className="flex justify-between"><span>Secondary (Class 9-10):</span><span className="font-bold text-slate-800">128 (26.5%)</span></div>
            <div className="flex justify-between"><span>Middle (Class 6-8):</span><span className="font-bold text-slate-800">142 (29.5%)</span></div>
            <div className="flex justify-between"><span>Primary (Class 1-5):</span><span className="font-bold text-slate-800">156 (32.3%)</span></div>
            <div className="flex justify-between"><span>Pre-Primary (PG-Prep):</span><span className="font-bold text-slate-800">56 (11.6%)</span></div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-2">Campus Attendance Performance</h3>
          <p className="text-xs text-slate-500 mb-4">
            {dashboard?.currentSessionName ? `Session: ${dashboard.currentSessionName}` : 'Daily average for current academic month'}
          </p>
          <div className="space-y-2 text-xs">
            <div className="flex justify-between">
              <span>Overall Rate Today:</span>
              <span className="font-bold text-emerald-600">
                {dashboard?.attendanceRateToday != null ? `${Number(dashboard.attendanceRateToday).toFixed(1)}%` : '94.8%'}
              </span>
            </div>
            <div className="flex justify-between"><span>Late Arrival Rate:</span><span className="font-bold text-amber-600">3.2%</span></div>
            <div className="flex justify-between"><span>Unexcused Absences:</span><span className="font-bold text-red-600">2.0%</span></div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-2">Fee Collection Target</h3>
          <p className="text-xs text-slate-500 mb-4">Current Financial Ledger Summary</p>
          <div className="space-y-2 text-xs">
            <div className="flex justify-between">
              <span>Total Invoiced:</span>
              <span className="font-bold text-slate-800">
                {dashboard?.totalFeeInvoiced != null ? `Rs. ${Number(dashboard.totalFeeInvoiced).toLocaleString()}` : 'Rs. 1,700,000'}
              </span>
            </div>
            <div className="flex justify-between">
              <span>Realized Collections:</span>
              <span className="font-bold text-emerald-600">
                {dashboard?.totalFeeCollected != null ? `Rs. ${Number(dashboard.totalFeeCollected).toLocaleString()}` : 'Rs. 1,420,000'}
              </span>
            </div>
            <div className="flex justify-between">
              <span>Outstanding Balance:</span>
              <span className="font-bold text-amber-700">
                {dashboard?.totalFeeOutstanding != null ? `Rs. ${Number(dashboard.totalFeeOutstanding).toLocaleString()}` : 'Rs. 280,000'}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// ============================================================================
// 10. SETTINGS & PROFILE PAGE
// ============================================================================
export function SettingsPage() {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <h2 className="text-xl font-bold text-slate-800">School Profile & Institutional Configuration</h2>
        <p className="text-sm text-slate-500 mt-1">
          Official campus registry, contact details, regional compliance, and system roles.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
            <Building2 className="w-5 h-5 text-brand-700" />
            Campus Credentials
          </h3>
          <div className="space-y-3 text-xs">
            <div>
              <span className="text-slate-500 block">Institution Name:</span>
              <span className="font-bold text-slate-800 text-sm">Bluebells Schooling System</span>
            </div>
            <div>
              <span className="text-slate-500 block">Campus Location:</span>
              <span className="font-semibold text-slate-800">
                Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh, Pakistan (71800)
              </span>
            </div>
            <div className="grid grid-cols-2 gap-3 pt-2">
              <div>
                <span className="text-slate-500 block">Official Phone:</span>
                <span className="font-semibold text-slate-800">+92 316 9681932</span>
              </div>
              <div>
                <span className="text-slate-500 block">Official Email:</span>
                <span className="font-semibold text-slate-800">Bluebells.schoolingsystem@gmail.com</span>
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3 pt-2">
              <div>
                <span className="text-slate-500 block">System Currency:</span>
                <span className="font-semibold text-slate-800">PKR (Pakistani Rupee, Rs.)</span>
              </div>
              <div>
                <span className="text-slate-500 block">System Timezone:</span>
                <span className="font-semibold text-slate-800">Asia/Karachi (UTC+5)</span>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4">
          <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-emerald-600" />
            Security & Backup Infrastructure
          </h3>
          <div className="space-y-3 text-xs">
            <div className="p-3 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-900">
              <span className="font-bold">Role-Based Access Control (RBAC):</span>
              <p className="mt-0.5 text-[11px]">7 Roles enforced: Super Admin, School Admin, Principal, Teacher, Parent, Student, Accountant.</p>
            </div>
            <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 text-slate-700">
              <span className="font-bold">Database Engine:</span>
              <p className="mt-0.5 text-[11px]">MySQL 8.x/26.x InnoDB with ACID transactions, foreign keys, and UTF8MB4.</p>
            </div>
            <div className="p-3 rounded-lg bg-slate-50 border border-slate-200 text-slate-700">
              <span className="font-bold">Automated Disaster Recovery:</span>
              <p className="mt-0.5 text-[11px]">Drill tested with 100% table and row parity. SHA256 checksums enabled.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
