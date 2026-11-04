import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  BookOpen, 
  GraduationCap, 
  Users, 
  Calendar, 
  CheckSquare, 
  FileText, 
  Award, 
  CreditCard, 
  BarChart3, 
  Settings 
} from 'lucide-react';

interface SidebarProps {
  activeRole: string;
}

export const Sidebar: React.FC<SidebarProps> = ({ activeRole }) => {
  const isStudent = activeRole === 'ROLE_STUDENT';
  const isParent = activeRole === 'ROLE_PARENT';
  const isTeacher = activeRole === 'ROLE_TEACHER';
  const isAccountant = activeRole === 'ROLE_ACCOUNTANT';

  const allNavItems = [
    { label: isStudent ? 'Student Dashboard' : (isParent ? 'Parent Dashboard' : (isTeacher ? 'Teacher Workspace' : (isAccountant ? 'Finance Workspace' : 'Campus Dashboard'))), path: '/', icon: LayoutDashboard, roles: ['ALL'] },
    { label: 'Academics', path: '/academics', icon: BookOpen, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER'] },
    { label: 'Admissions', path: '/admissions', icon: GraduationCap, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL'] },
    { label: 'Student Directory', path: '/students', icon: Users, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT'] },
    { label: isStudent ? 'My Timetable' : (isParent ? 'Class Timetable' : 'Timetable'), path: '/timetable', icon: Calendar, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: isStudent ? 'My Attendance' : (isParent ? 'Child Attendance' : 'Attendance'), path: '/attendance', icon: CheckSquare, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: isStudent ? 'My Courses & LMS' : 'LMS & Courses', path: '/lms', icon: FileText, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT'] },
    { label: isStudent ? 'My Report Cards' : (isParent ? 'Ward Report Cards' : 'Examinations'), path: '/examinations', icon: Award, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: isStudent ? 'My Fee Invoices' : (isParent ? 'Fee Vouchers' : 'Fees & Finance'), path: '/fees', icon: CreditCard, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT', 'ROLE_PARENT', 'ROLE_STUDENT'] },
    { label: 'Reports & Analytics', path: '/reports', icon: BarChart3, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT'] },
    { label: 'School Settings', path: '/settings', icon: Settings, roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN'] },
  ];

  const navItems = allNavItems.filter((item) => 
    item.roles.includes('ALL') || item.roles.includes(activeRole)
  );

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 min-h-[calc(100vh-65px)] flex flex-col justify-between py-4">
      <nav className="px-3 space-y-1">
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-brand-700 text-white shadow'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
                }`
              }
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="px-4 py-3 border-t border-slate-800 text-xs text-slate-500">
        <p className="font-semibold text-slate-400">Bluebells LMS v1.0.0</p>
        <p>Latifabad, Hyderabad</p>
        <div className="mt-2 inline-flex items-center px-2 py-0.5 rounded bg-slate-800 text-[10px] text-slate-400">
          Role: {activeRole.replace('ROLE_', '')}
        </div>
      </div>
    </aside>
  );
};
