import React, { useState, useEffect } from 'react';
import { Bell, User as UserIcon, LogOut, ChevronDown, Check } from 'lucide-react';
import { SchoolProfile, User } from '../../types';
import { NotificationDrawer } from '../notifications/NotificationDrawer';
import { apiClient } from '../../api/client';

interface HeaderProps {
  schoolProfile?: SchoolProfile;
  activeRole: string;
  currentUser?: User | null;
  onLogout?: () => void;
  onSwitchPersona?: (username: string) => void;
}

export const Header: React.FC<HeaderProps> = ({ 
  schoolProfile, 
  activeRole, 
  currentUser,
  onLogout,
  onSwitchPersona
}) => {
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
      .catch(() => {
        // Keep default badge
      });
  }, [currentUser]);

  const personas = [
    { username: 'admin', label: 'Super Admin (Administrator)', role: 'ROLE_SUPER_ADMIN' },
    { username: 'principal', label: 'Principal (Dr. Asim Farooqui)', role: 'ROLE_PRINCIPAL' },
    { username: 'teacher.ahmed', label: 'Teacher (Sir Ahmed - Math)', role: 'ROLE_TEACHER' },
    { username: 'teacher.fatima', label: 'Teacher (Miss Fatima - Physics)', role: 'ROLE_TEACHER' },
    { username: 'accountant', label: 'Accountant (Syed Tariq)', role: 'ROLE_ACCOUNTANT' },
    { username: 'parent.khan', label: 'Parent (Mohammad Tariq Khan)', role: 'ROLE_PARENT' },
    { username: 'student.ali', label: 'Student (Ali Khan - 9-A)', role: 'ROLE_STUDENT' },
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
    <header className="bg-white border-b border-slate-200 sticky top-0 z-30 px-4 lg:px-8 py-3 flex items-center justify-between shadow-sm">
      <div className="flex items-center space-x-3">
        <div className="w-10 h-10 rounded-lg bg-brand-700 flex items-center justify-center text-white font-bold text-xl shadow-md">
          B
        </div>
        <div>
          <h1 className="text-lg font-bold text-slate-800 leading-tight">
            {schoolProfile?.name || 'Bluebells Schooling System'}
          </h1>
          <p className="text-xs text-slate-500">
            {schoolProfile?.timezone ? `Hyderabad, Sindh • Timezone: ${schoolProfile.timezone}` : 'Hyderabad, Sindh, Pakistan'}
          </p>
        </div>
      </div>

      <div className="flex items-center space-x-3">
        {/* Quick Persona Switcher Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowPersonaMenu(!showPersonaMenu)}
            disabled={!!switchingUser}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-brand-50 hover:bg-brand-100 border border-brand-200 text-xs font-semibold text-brand-900 transition-colors shadow-sm disabled:opacity-50"
            title="Switch User Role"
          >
            <span>{switchingUser ? `Switching...` : 'Switch Persona ▾'}</span>
            <ChevronDown className="w-3.5 h-3.5 text-brand-700" />
          </button>

          {showPersonaMenu && (
            <div className="absolute right-0 mt-2 w-72 bg-white rounded-xl shadow-xl border border-slate-200 py-2 z-50 animate-in fade-in slide-in-from-top-1">
              <div className="px-3 py-1.5 border-b border-slate-100 text-[11px] font-bold uppercase tracking-wider text-slate-400">
                Switch Active Persona
              </div>
              <div className="divide-y divide-slate-50 max-h-80 overflow-y-auto">
                {personas.map((p) => {
                  const isCurrent = currentUser?.username === p.username;
                  return (
                    <button
                      key={p.username}
                      onClick={() => handlePersonaClick(p.username)}
                      className={`w-full px-3 py-2 text-left text-xs flex items-center justify-between hover:bg-slate-50 transition-colors ${
                        isCurrent ? 'bg-brand-50 font-semibold text-brand-900' : 'text-slate-700'
                      }`}
                    >
                      <div>
                        <p className="leading-tight">{p.label}</p>
                        <p className="text-[10px] text-slate-400 mt-0.5 font-mono">{p.username}</p>
                      </div>
                      {isCurrent && <Check className="w-4 h-4 text-brand-700 flex-shrink-0" />}
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        <div className="hidden sm:flex items-center px-2.5 py-1 rounded-full bg-slate-100 text-xs font-semibold text-slate-700">
          Role: <span className="ml-1 uppercase text-slate-900 font-bold">{activeRole.replace('ROLE_', '')}</span>
        </div>

        <button 
          aria-label="Notifications"
          onClick={() => setShowNotifications(true)}
          className="relative p-2 text-slate-600 hover:text-brand-600 hover:bg-slate-100 rounded-full transition-colors"
        >
          <Bell className="w-4 h-4" />
          {unreadCount > 0 && (
            <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-red-500 text-white rounded-full text-[9px] font-bold flex items-center justify-center px-1">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          )}
        </button>

        <div className="flex items-center space-x-3 border-l border-slate-200 pl-3">
          <div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-slate-700">
            <UserIcon className="w-4 h-4" />
          </div>
          <div className="hidden md:block text-left">
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
            className="px-3 py-1.5 text-red-600 bg-red-50 hover:bg-red-100 border border-red-200 rounded-lg transition-colors flex items-center gap-1.5 text-xs font-semibold shadow-sm"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span>Log Out</span>
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
