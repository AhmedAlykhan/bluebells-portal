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
  Settings,
  Briefcase,
  FileSpreadsheet,
  X
} from 'lucide-react';

interface SidebarProps {
  activeRole: string;
  isOpenMobile?: boolean;
  onCloseMobile?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ 
  activeRole,
  isOpenMobile = false,
  onCloseMobile
}) => {
  const isStudent = activeRole === 'ROLE_STUDENT';
  const isParent = activeRole === 'ROLE_PARENT';
  const isTeacher = activeRole === 'ROLE_TEACHER';
  const isAccountant = activeRole === 'ROLE_ACCOUNTANT';
  const isPrincipal = activeRole === 'ROLE_PRINCIPAL';
  const isVicePrincipal = activeRole === 'ROLE_VICE_PRINCIPAL';

  interface NavGroup {
    title: string;
    items: {
      label: string;
      path: string;
      icon: React.ComponentType<{ className?: string }>;
      roles: string[];
    }[];
  }

  const dashboardLabel = isStudent 
    ? 'Student Dashboard' 
    : isParent 
      ? 'Parent Dashboard' 
      : isTeacher 
        ? 'Teacher Workspace' 
        : isAccountant 
          ? 'Finance Ledger' 
          : isPrincipal
            ? 'Principal Dashboard'
            : isVicePrincipal
              ? 'Vice Principal Desk'
              : 'Campus Dashboard';

  const navGroups: NavGroup[] = [
    {
      title: 'Overview',
      items: [
        { label: dashboardLabel, path: '/', icon: LayoutDashboard, roles: ['ALL'] },
      ],
    },
    {
      title: 'Academic Administration',
      items: [
        { 
          label: 'Academics', 
          path: '/academics', 
          icon: BookOpen, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER'] 
        },
        { 
          label: 'Admissions', 
          path: '/admissions', 
          icon: GraduationCap, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL'] 
        },
        { 
          label: 'Student Directory', 
          path: '/students', 
          icon: Users, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT'] 
        },
        { 
          label: 'Staff & Faculty', 
          path: '/staff', 
          icon: Briefcase, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL'] 
        },
        { 
          label: isStudent ? 'My Timetable' : (isParent ? 'Class Timetable' : 'Timetable'), 
          path: '/timetable', 
          icon: Calendar, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] 
        },
        { 
          label: isStudent ? 'My Attendance' : (isParent ? 'Child Attendance' : 'Attendance'), 
          path: '/attendance', 
          icon: CheckSquare, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] 
        },
        { 
          label: isStudent ? 'My Report Cards' : (isParent ? 'Ward Report Cards' : 'Examinations'), 
          path: '/examinations', 
          icon: Award, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] 
        },
      ],
    },
    {
      title: 'Learning Management',
      items: [
        { 
          label: isStudent ? 'My Courses & LMS' : (isParent ? 'Child Learning & LMS' : 'LMS & Courses'), 
          path: '/lms', 
          icon: FileText, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] 
        },
      ],
    },
    {
      title: 'Finance & Accounts',
      items: [
        { 
          label: isStudent ? 'My Fee Invoices' : (isParent ? 'Fee Vouchers' : 'Fees & Finance'), 
          path: '/fees', 
          icon: CreditCard, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT', 'ROLE_PARENT', 'ROLE_STUDENT'] 
        },
      ],
    },
    {
      title: 'Data Management',
      items: [
        { 
          label: 'Bulk Data Import', 
          path: '/admin/data-import', 
          icon: FileSpreadsheet, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL'] 
        },
      ],
    },
    {
      title: 'Institutional Control',
      items: [
        { 
          label: 'Reports & Analytics', 
          path: '/reports', 
          icon: BarChart3, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT'] 
        },
        { 
          label: 'School Settings', 
          path: '/settings', 
          icon: Settings, 
          roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL'] 
        },
      ],
    },
  ];

  const content = (
    <div className="flex flex-col justify-between h-full py-4">
      <div className="space-y-6 px-3">
        {navGroups.map((group, groupIdx) => {
          const visibleItems = group.items.filter(item =>
            item.roles.includes('ALL') || item.roles.includes(activeRole)
          );

          if (visibleItems.length === 0) return null;

          return (
            <div key={groupIdx}>
              <p className="px-3 text-[10px] font-black uppercase tracking-widest text-slate-400 mb-1.5">
                {group.title}
              </p>
              <div className="space-y-1">
                {visibleItems.map((item) => {
                  const Icon = item.icon;
                  return (
                    <NavLink
                      key={item.path}
                      to={item.path}
                      onClick={onCloseMobile}
                      className={({ isActive }) =>
                        `flex items-center gap-3 px-3 py-2 rounded-xl text-xs font-semibold transition-all ${
                          isActive
                            ? 'bg-[#123B70] text-white shadow-md'
                            : 'text-slate-600 hover:text-brand-800 hover:bg-slate-100'
                        }`
                      }
                    >
                      <Icon className="w-4 h-4 flex-shrink-0" />
                      <span>{item.label}</span>
                    </NavLink>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      <div className="px-4 py-3 mx-3 mt-6 border-t border-slate-200 text-xs text-slate-500 rounded-2xl bg-slate-50">
        <p className="font-bold text-[#123B70]">Bluebells Schooling System</p>
        <p className="text-[10px] text-slate-400">Latifabad, Hyderabad, Sindh</p>
        <div className="mt-2 flex items-center justify-between text-[10px] font-semibold">
          <span className="text-brand-700">BISE Hyderabad</span>
          <span className="px-1.5 py-0.5 rounded bg-white text-slate-600 border border-slate-200">
            v2.0
          </span>
        </div>
      </div>
    </div>
  );

  return (
    <>
      {/* Desktop Sidebar */}
      <aside className="hidden lg:block w-64 bg-white border-r border-slate-200/80 min-h-[calc(100vh-65px)]">
        {content}
      </aside>

      {/* Mobile Drawer Overlay */}
      {isOpenMobile && (
        <div className="fixed inset-0 z-50 lg:hidden flex">
          <div 
            className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm transition-opacity"
            onClick={onCloseMobile}
          />
          <div className="relative w-72 max-w-[85vw] bg-white h-full shadow-2xl z-10 flex flex-col overflow-y-auto animate-slide-in-right">
            <div className="p-4 border-b border-slate-200 flex items-center justify-between">
              <span className="text-xs font-bold text-[#123B70] uppercase tracking-wider">
                Bluebells Navigation
              </span>
              <button 
                onClick={onCloseMobile}
                className="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            {content}
          </div>
        </div>
      )}
    </>
  );
};
