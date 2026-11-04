import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Bell, 
  LogOut, 
  ChevronDown, 
  Check, 
  Globe, 
  Menu, 
  ShieldCheck,
  Building2,
  BookOpen,
  CreditCard,
  Users,
  GraduationCap
} from 'lucide-react';
import { SchoolProfile, User } from '../../types';
import { NotificationDrawer } from '../notifications/NotificationDrawer';
import { BluebellsLogo } from '../brand/BluebellsLogo';
import { apiClient } from '../../api/client';

interface HeaderProps {
  schoolProfile?: SchoolProfile;
  activeRole: string;
  currentUser?: User | null;
  onLogout?: () => void;
  onSwitchPersona?: (username: string) => void;
  onToggleMobileSidebar?: () => void;
}

export const Header: React.FC<HeaderProps> = ({ 
  schoolProfile: _schoolProfile, 
  activeRole, 
  currentUser,
  onLogout,
  onSwitchPersona,
  onToggleMobileSidebar
}) => {
  const navigate = useNavigate();
  const [showPersonaMenu, setShowPersonaMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [unreadCount, setUnreadCount] = useState<number>(2);
  const [switchingUser, setSwitchingUser] = useState<string | null>(null);

  useEffect(() => {
    apiClient.get<{ data: { unreadCount: number } }>('/notifications/my/unread-count')
      .then(res => {
        if (typeof res.data?.data?.unreadCount === 'number') {
          setUnreadCount(res.data.data.unreadCount);
        }
      })
      .catch(() => {});
  }, [currentUser]);

  const personas = [
    { username: 'admin', label: 'Super Admin', desc: 'Platform Administrator', role: 'ROLE_SUPER_ADMIN', icon: ShieldCheck },
    { username: 'principal', label: 'Principal', desc: 'Huma Arif • Institutional Oversight', role: 'ROLE_PRINCIPAL', icon: Building2 },
    { username: 'teacher.ahmed', label: 'Teacher (Class 9-A)', desc: 'Sir Ahmed • Math & Attendance', role: 'ROLE_TEACHER', icon: BookOpen },
    { username: 'teacher.fatima', label: 'Teacher (Class 10-A)', desc: 'Miss Fatima • Physics & Lab', role: 'ROLE_TEACHER', icon: BookOpen },
    { username: 'accountant', label: 'Accountant', desc: 'Syed Tariq • Fee Ledger & Receipts', role: 'ROLE_ACCOUNTANT', icon: CreditCard },
    { username: 'parent.khan', label: 'Parent (Guardian)', desc: 'Mohammad Tariq Khan (Father of Ali)', role: 'ROLE_PARENT', icon: Users },
    { username: 'student.ali', label: 'Student (Class 9-A)', desc: 'Ali Khan • Roll #01', role: 'ROLE_STUDENT', icon: GraduationCap },
  ];

  const handlePersonaClick = async (username: string) => {
    setShowPersonaMenu(false);
    if (onSwitchPersona) {
      setSwitchingUser(username);
      try {
        await onSwitchPersona(username);
      } finally {
        setSwitchingUser(null);
      }
    }
  };

  return (
    <header className="bg-white border-b border-slate-200/80 sticky top-0 z-30 px-4 sm:px-6 lg:px-8 py-3 flex items-center justify-between shadow-sm">
      <div className="flex items-center gap-3">
        {/* Mobile Hamburger Menu Toggle */}
        <button
          onClick={onToggleMobileSidebar}
          className="lg:hidden p-2 rounded-xl text-slate-600 hover:text-brand-700 hover:bg-slate-100 transition-colors"
          aria-label="Toggle Navigation Menu"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div onClick={() => navigate('/')} className="cursor-pointer">
          <BluebellsLogo variant="horizontal" size="md" />
        </div>
      </div>

      <div className="flex items-center gap-2.5 sm:gap-3.5">
        {/* Quick Link to Public School Website */}
        <button
          onClick={() => navigate('/welcome')}
          className="hidden md:inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-xs font-semibold text-slate-700 transition-colors"
          title="Visit Public Website"
        >
          <Globe className="w-3.5 h-3.5 text-brand-600" />
          <span>School Website</span>
        </button>

        {/* Quick Persona Switcher Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowPersonaMenu(!showPersonaMenu)}
            disabled={!!switchingUser}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#EAF3FC] hover:bg-blue-100 border border-brand-200 text-xs font-bold text-[#123B70] transition-colors shadow-sm disabled:opacity-50"
            title="Switch User Role"
          >
            <span>{switchingUser ? 'Switching...' : 'Switch Role ▾'}</span>
            <ChevronDown className="w-3.5 h-3.5 text-brand-700" />
          </button>

          {showPersonaMenu && (
            <div className="absolute right-0 mt-2 w-80 bg-white rounded-2xl shadow-2xl border border-slate-200 py-2 z-50 animate-fade-in">
              <div className="px-4 py-2 border-b border-slate-100 flex items-center justify-between">
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                  Switch Active Role
                </span>
                <span className="text-[10px] text-emerald-700 font-semibold bg-emerald-50 px-2 py-0.5 rounded">
                  Instant Access
                </span>
              </div>
              <div className="divide-y divide-slate-50 max-h-84 overflow-y-auto">
                {personas.map((p) => {
                  const isCurrent = currentUser?.username === p.username;
                  const Icon = p.icon;
                  return (
                    <button
                      key={p.username}
                      onClick={() => handlePersonaClick(p.username)}
                      className={`w-full px-4 py-2.5 text-left text-xs flex items-center justify-between hover:bg-slate-50 transition-colors ${
                        isCurrent ? 'bg-blue-50/70 font-semibold text-brand-900' : 'text-slate-700'
                      }`}
                    >
                      <div className="flex items-center gap-2.5 min-w-0">
                        <div className="p-1.5 rounded-lg bg-slate-100 text-brand-700 flex-shrink-0">
                          <Icon className="w-3.5 h-3.5" />
                        </div>
                        <div className="min-w-0">
                          <p className="font-bold text-slate-800 leading-tight truncate">{p.label}</p>
                          <p className="text-[10px] text-slate-400 mt-0.5 truncate">{p.desc}</p>
                        </div>
                      </div>
                      {isCurrent && <Check className="w-4 h-4 text-brand-700 flex-shrink-0 ml-2" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        {/* Role Badge */}
        <div className="hidden sm:inline-flex items-center px-2.5 py-1 rounded-full bg-slate-100 text-xs font-semibold text-slate-700">
          <span className="text-[11px] text-slate-500 mr-1">Role:</span>
          <span className="text-[11px] font-bold text-[#123B70] uppercase">
            {activeRole.replace('ROLE_', '').replace('_', ' ')}
          </span>
        </div>

        {/* Notifications Button */}
        <button 
          aria-label="Notifications"
          onClick={() => setShowNotifications(true)}
          className="relative p-2 text-slate-600 hover:text-brand-700 hover:bg-slate-100 rounded-xl transition-colors"
        >
          <Bell className="w-4 h-4" />
          {unreadCount > 0 && (
            <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-red-500 text-white rounded-full text-[9px] font-bold flex items-center justify-center px-1">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>

        {/* Profile & Logout */}
        <div className="flex items-center gap-3 border-l border-slate-200 pl-3">
          <div className="w-8 h-8 rounded-full bg-brand-50 border border-brand-200 flex items-center justify-center text-brand-700 font-bold text-xs">
            {currentUser?.fullName?.charAt(0) || 'U'}
          </div>
          <div className="hidden lg:block text-left">
            <p className="text-xs font-bold text-slate-800 leading-tight">
              {currentUser?.fullName || 'Platform Administrator'}
            </p>
            <p className="text-[10px] text-slate-400 font-mono">
              @{currentUser?.username || 'admin'}
            </p>
          </div>
          <button 
            onClick={onLogout}
            title="Log Out of Portal"
            className="px-2.5 py-1.5 text-red-600 bg-red-50 hover:bg-red-100 border border-red-200 rounded-xl transition-colors flex items-center gap-1.5 text-xs font-semibold shadow-sm"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Log Out</span>
          </button>
        </div>
      </div>

      {/* Slide-out Notification Drawer */}
      <NotificationDrawer 
        isOpen={showNotifications} 
        onClose={() => setShowNotifications(false)} 
        onCountChange={setUnreadCount}
      />
    </header>
  );
};
