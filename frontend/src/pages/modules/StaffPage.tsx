import React, { useState } from 'react';
import { Search } from 'lucide-react';

interface StaffMember {
  id: string;
  name: string;
  designation: string;
  department: string;
  qualification: string;
  assignedClasses: string[];
  subjects: string[];
  email: string;
  phone: string;
  status: 'ACTIVE' | 'ON_LEAVE';
  joiningDate: string;
}

export const StaffPage: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');

  const staffList: StaffMember[] = [
    {
      id: 'EMP-001',
      name: 'Huma Arif',
      designation: 'Academic Principal',
      department: 'Executive Administration',
      qualification: 'M.Ed (Educational Leadership), M.A. English',
      assignedClasses: ['Institutional Oversight', 'All Sections'],
      subjects: ['Curriculum Moderation', 'Staff Mentorship'],
      email: 'huma.arif@bluebells.edu.pk',
      phone: '+92 316 9681932',
      status: 'ACTIVE',
      joiningDate: '2018-08-01',
    },
    {
      id: 'EMP-002',
      name: 'Hana Khan',
      designation: 'Vice Principal & Head of Academics',
      department: 'Academic Coordination',
      qualification: 'M.A. Educational Administration, B.Ed',
      assignedClasses: ['Middle & Secondary Wings'],
      subjects: ['Timetable Optimization', 'Examinations'],
      email: 'hana.khan@bluebells.edu.pk',
      phone: '+92 316 9681932',
      status: 'ACTIVE',
      joiningDate: '2019-01-15',
    },
    {
      id: 'EMP-003',
      name: 'Sir Ahmed Khan',
      designation: 'Senior Teacher & Head of Mathematics',
      department: 'Mathematics & Science',
      qualification: 'M.Sc (Applied Mathematics), B.Ed',
      assignedClasses: ['Class 9-A (Class Teacher)', 'Class 10-A'],
      subjects: ['Mathematics (Sindh Textbook Board)'],
      email: 'ahmed.khan@bluebells.edu.pk',
      phone: '+92 300 2345678',
      status: 'ACTIVE',
      joiningDate: '2020-07-01',
    },
    {
      id: 'EMP-004',
      name: 'Miss Fatima Shah',
      designation: 'Senior Faculty & Laboratory Incharge',
      department: 'Physical Sciences',
      qualification: 'M.Sc (Physics), B.Sc (Hons)',
      assignedClasses: ['Class 9-A', 'Class 10-A (Class Teacher)'],
      subjects: ['Physics Theory & Practical Labs'],
      email: 'fatima.shah@bluebells.edu.pk',
      phone: '+92 333 3456789',
      status: 'ACTIVE',
      joiningDate: '2021-02-10',
    },
    {
      id: 'EMP-005',
      name: 'Sir Asif',
      designation: 'Faculty & Computer Lab Administrator',
      department: 'Computer Science & Chemistry',
      qualification: 'B.S (Computer Science), B.Sc Chemistry',
      assignedClasses: ['Class 9-A', 'Class 8 Rose'],
      subjects: ['Computer Science', 'Chemistry'],
      email: 'asif@bluebells.edu.pk',
      phone: '+92 345 4567890',
      status: 'ACTIVE',
      joiningDate: '2021-08-15',
    },
    {
      id: 'EMP-006',
      name: 'Syed Tariq',
      designation: 'Finance Officer & Chief Accountant',
      department: 'Accounts & Finance',
      qualification: 'M.Com, PIPFA Certified',
      assignedClasses: ['Institutional Financial Desk'],
      subjects: ['Fee Ledger & Bank Alfalah Challans'],
      email: 'tariq.accounts@bluebells.edu.pk',
      phone: '+92 312 5678901',
      status: 'ACTIVE',
      joiningDate: '2019-06-01',
    },
  ];

  const filteredStaff = staffList.filter(s => {
    const matchesSearch = s.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          s.designation.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          s.department.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesDept = departmentFilter === 'ALL' || s.department.includes(departmentFilter);
    return matchesSearch && matchesDept;
  });

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-50 text-purple-800 border border-purple-200">
            Total Staff: 38 Faculty & Administrative Members
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            Staff & Faculty Directory
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            Faculty credentials, assigned teaching subjects, class responsibilities, and department rosters.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          <div className="relative">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-2.5" />
            <input
              type="text"
              placeholder="Search faculty or dept..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 pr-3 py-1.5 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 w-52 bg-white"
            />
          </div>

          <select
            value={departmentFilter}
            onChange={(e) => setDepartmentFilter(e.target.value)}
            className="px-3 py-1.5 text-xs border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 bg-white text-slate-700"
          >
            <option value="ALL">All Departments</option>
            <option value="Executive">Executive Administration</option>
            <option value="Mathematics">Mathematics & Science</option>
            <option value="Physical">Physical Sciences</option>
            <option value="Computer">Computer Science</option>
            <option value="Accounts">Accounts & Finance</option>
          </select>
        </div>
      </div>

      {/* Staff Grid Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {filteredStaff.map((staff) => (
          <div key={staff.id} className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between">
                <div>
                  <span className="text-[10px] font-mono font-bold text-brand-700">{staff.id}</span>
                  <h3 className="text-base font-bold text-slate-800">{staff.name}</h3>
                  <p className="text-xs font-semibold text-brand-700">{staff.designation}</p>
                  <p className="text-[11px] text-slate-400">{staff.department}</p>
                </div>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">
                  {staff.status}
                </span>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 space-y-2 text-xs text-slate-600">
                <p><strong>Qualification:</strong> {staff.qualification}</p>
                <div>
                  <strong className="block text-slate-700">Assigned Classes:</strong>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {staff.assignedClasses.map((c, i) => (
                      <span key={i} className="px-2 py-0.5 rounded bg-slate-100 text-slate-700 text-[10px] font-medium">
                        {c}
                      </span>
                    ))}
                  </div>
                </div>
                <div>
                  <strong className="block text-slate-700">Subjects Taught:</strong>
                  <p className="text-[11px] text-slate-500 mt-0.5">{staff.subjects.join(', ')}</p>
                </div>
              </div>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
              <span className="font-mono text-[11px]">{staff.phone}</span>
              <span className="text-[10px] text-slate-400">Joined: {staff.joiningDate}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
