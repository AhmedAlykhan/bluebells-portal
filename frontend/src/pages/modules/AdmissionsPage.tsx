import React, { useState, useEffect } from 'react';
import { 
  PlusCircle, 
  Search
} from 'lucide-react';
import { NewAdmissionModal } from '../../components/modals/NewAdmissionModal';
import { apiClient } from '../../api/client';

interface Application {
  id: string;
  name: string;
  class: string;
  guardian: string;
  phone: string;
  status: 'SUBMITTED' | 'IN_TESTING' | 'APPROVED' | 'ENROLLED' | 'REJECTED';
  badge: string;
  note: string;
  submissionDate: string;
}

export const AdmissionsPage: React.FC = () => {
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const [applications, setApplications] = useState<Application[]>([
    { id: 'APP-2026-0001', name: 'Shahid Afridi', class: 'Class 9', guardian: 'Abdul Rehman Afridi', phone: '+92 333 7654321', status: 'ENROLLED', badge: 'bg-emerald-100 text-emerald-800', note: 'Enrolled in 9-A • BSS-2026-0003', submissionDate: '2026-10-15' },
    { id: 'APP-2026-0002', name: 'Hamza Tariq', class: 'Class 5', guardian: 'Tariq Mehmood', phone: '+92 321 9876543', status: 'APPROVED', badge: 'bg-blue-100 text-blue-800', note: 'Passed admission entrance test (88%)', submissionDate: '2026-10-18' },
    { id: 'APP-2026-0003', name: 'Zainab Fatima', class: 'Class 9', guardian: 'Dr. Mansoor', phone: '+92 345 5566778', status: 'IN_TESTING', badge: 'bg-amber-100 text-amber-800', note: 'Scheduled for interview on Saturday', submissionDate: '2026-10-22' },
    { id: 'APP-2026-0004', name: 'Bilal Hassan', class: 'Class 9', guardian: 'Hassan Raza', phone: '+92 302 4455667', status: 'ENROLLED', badge: 'bg-emerald-100 text-emerald-800', note: 'Enrolled in 9-B • BSS-2026-0005', submissionDate: '2026-10-10' },
  ]);

  const loadApplications = () => {
    apiClient.get('/admissions/applications')
      .then(res => {
        interface RawApp {
          id: number;
          applicationNumber?: string;
          studentName?: string;
          targetGrade?: string;
          guardianName?: string;
          guardianPhone?: string;
          status?: string;
          notes?: string;
          createdAt?: string;
        }
        if (Array.isArray(res.data?.data) && res.data.data.length > 0) {
          const mapped: Application[] = res.data.data.map((a: RawApp) => {
            let badge = 'bg-amber-100 text-amber-800';
            if (a.status === 'ENROLLED') badge = 'bg-emerald-100 text-emerald-800';
            else if (a.status === 'APPROVED') badge = 'bg-blue-100 text-blue-800';
            return {
              id: a.applicationNumber || `APP-2026-00${a.id}`,
              name: a.studentName || 'Student Applicant',
              class: a.targetGrade || 'Class 9',
              guardian: a.guardianName || 'Parent',
              phone: a.guardianPhone || '+92 316 9681932',
              status: (a.status as Application['status']) || 'SUBMITTED',
              badge,
              note: a.notes || 'Under admissions review',
              submissionDate: a.createdAt?.split('T')[0] || '2026-10-20',
            };
          });
          setApplications(mapped);
        }
      })
      .catch(() => {});
  };

  useEffect(() => {
    loadApplications();
  }, []);

  const filteredApps = applications.filter(a => {
    const matchesSearch = a.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          a.id.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          a.guardian.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || a.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
            Admissions 2026-2027 Open
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            Student Admissions & Inquiry Desk
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            Process B-Form verifications, schedule entrance evaluations, and enroll students into sections.
          </p>
        </div>

        <button 
          onClick={() => setShowModal(true)}
          className="px-4 py-2.5 bg-[#123B70] hover:bg-[#2167B2] text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-2"
        >
          <PlusCircle className="w-4 h-4" />
          <span>+ New Admission Application</span>
        </button>
      </div>

      {/* KPI Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Inquiries & Apps</span>
          <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-1">48 Applications</div>
          <p className="text-xs text-brand-600 font-semibold mt-1">Session 2026-2027</p>
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Enrolled & Allocated</span>
          <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-1">32 Students</div>
          <p className="text-xs text-slate-500 mt-1">Assigned to Active Sections</p>
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
          <span className="text-xs font-bold uppercase tracking-wider text-slate-500">In Review & Testing</span>
          <div className="text-2xl sm:text-3xl font-black text-amber-700 mt-1">16 Applicants</div>
          <p className="text-xs text-slate-500 mt-1">Scheduled for Entrance Assessment</p>
        </div>
      </div>

      {/* Applications Directory Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 sm:p-5 border-b border-slate-200 bg-[#F8FAFC] flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <h2 className="text-sm font-bold text-slate-800">Recent Applications Roster</h2>
          
          <div className="flex flex-wrap items-center gap-2.5">
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
              <input
                type="text"
                placeholder="Search applicant or guardian..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-8 pr-3 py-1.5 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 w-52 bg-white"
              />
            </div>

            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-1.5 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 bg-white text-slate-700"
            >
              <option value="ALL">All Statuses</option>
              <option value="ENROLLED">Enrolled</option>
              <option value="APPROVED">Approved</option>
              <option value="IN_TESTING">In Testing</option>
              <option value="SUBMITTED">Submitted</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3.5">Application #</th>
                <th className="p-3.5">Applicant Name</th>
                <th className="p-3.5">Applied Grade</th>
                <th className="p-3.5">Guardian Details</th>
                <th className="p-3.5">Date</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5">Notes & Allocation</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredApps.map((app) => (
                <tr key={app.id} className="hover:bg-slate-50/70 transition-colors">
                  <td className="p-3.5 font-mono font-bold text-brand-800">{app.id}</td>
                  <td className="p-3.5 font-bold text-slate-800">{app.name}</td>
                  <td className="p-3.5 font-semibold text-slate-700">{app.class}</td>
                  <td className="p-3.5">
                    <p className="font-medium text-slate-800">{app.guardian}</p>
                    <p className="text-[11px] text-slate-500 font-mono">{app.phone}</p>
                  </td>
                  <td className="p-3.5 text-slate-500 font-mono">{app.submissionDate}</td>
                  <td className="p-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full font-bold text-[10px] ${app.badge}`}>
                      {app.status}
                    </span>
                  </td>
                  <td className="p-3.5 text-slate-600 italic text-[11px]">{app.note}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <NewAdmissionModal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        onSuccess={loadApplications}
      />
    </div>
  );
};
