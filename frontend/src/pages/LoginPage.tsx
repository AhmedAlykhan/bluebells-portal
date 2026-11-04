import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { SchoolProfile } from '../types';
import { BluebellsLogo } from '../components/brand/BluebellsLogo';
import { 
  ShieldCheck, 
  User as UserIcon, 
  Lock, 
  GraduationCap, 
  BookOpen, 
  Users, 
  CreditCard,
  Building2,
  LogIn,
  Eye,
  EyeOff,
  ArrowLeft
} from 'lucide-react';

interface LoginPageProps {
  schoolProfile?: SchoolProfile;
}

export const LoginPage: React.FC<LoginPageProps> = ({ schoolProfile }) => {
  const navigate = useNavigate();
  const { login, isLoading, error } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('Admin@12345');
  const [showPassword, setShowPassword] = useState(false);
  const [localError, setLocalError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalError(null);
    try {
      await login(username, password);
      navigate('/');
    } catch (err: unknown) {
      setLocalError((err as Error).message);
    }
  };

  const handleQuickLogin = async (u: string) => {
    setUsername(u);
    setPassword('Admin@12345');
    setLocalError(null);
    try {
      await login(u, 'Admin@12345');
      navigate('/');
    } catch (err: unknown) {
      setLocalError((err as Error).message);
    }
  };

  const personas = [
    { 
      role: 'Super Administrator', 
      user: 'admin', 
      desc: 'Platform operations, system configuration & security audits', 
      icon: ShieldCheck, 
      color: 'text-purple-700 bg-purple-50 hover:bg-purple-100 border-purple-200' 
    },
    { 
      role: 'Academic Principal', 
      user: 'principal', 
      desc: 'Huma Arif • Institutional leadership, exam approvals & faculty oversight', 
      icon: Building2, 
      color: 'text-brand-800 bg-blue-50 hover:bg-blue-100 border-blue-200' 
    },
    { 
      role: 'Teacher (Mathematics)', 
      user: 'teacher.ahmed', 
      desc: 'Sir Ahmed Khan • Class 9-A teacher, attendance & LMS grading', 
      icon: BookOpen, 
      color: 'text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border-emerald-200' 
    },
    { 
      role: 'Accountant', 
      user: 'accountant', 
      desc: 'Syed Tariq • Fee ledger, 3-copy bank challans & receipts', 
      icon: CreditCard, 
      color: 'text-amber-800 bg-amber-50 hover:bg-amber-100 border-amber-200' 
    },
    { 
      role: 'Parent (Guardian)', 
      user: 'parent.khan', 
      desc: 'Mohammad Tariq Khan • Father of Ali Khan (Class 9-A)', 
      icon: Users, 
      color: 'text-sky-700 bg-sky-50 hover:bg-sky-100 border-sky-200' 
    },
    { 
      role: 'Student (Class 9-A)', 
      user: 'student.ali', 
      desc: 'Ali Khan • Homework submissions, timetable & report card', 
      icon: GraduationCap, 
      color: 'text-indigo-700 bg-indigo-50 hover:bg-indigo-100 border-indigo-200' 
    },
  ];

  return (
    <div className="min-h-screen bg-[#F5F8FC] flex flex-col justify-center py-8 sm:px-6 lg:px-8 selection:bg-brand-100 selection:text-brand-900">
      {/* Return to website navigation */}
      <div className="sm:mx-auto sm:w-full sm:max-w-xl px-4 mb-4 flex items-center justify-between">
        <button
          onClick={() => navigate('/welcome')}
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-brand-700 hover:text-brand-900 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to School Website</span>
        </button>
        <span className="text-[11px] font-semibold text-slate-400">
          Official Institutional Portal
        </span>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-xl text-center px-4">
        <div className="flex justify-center mb-3">
          <BluebellsLogo variant="full" size="lg" />
        </div>
        <p className="text-xs text-slate-500 max-w-md mx-auto">
          {schoolProfile?.address || 'Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh'}
        </p>
      </div>

      <div className="mt-6 sm:mx-auto sm:w-full sm:max-w-xl px-4">
        <div className="bg-white py-8 px-6 shadow-xl rounded-3xl sm:px-10 border border-slate-200/80">
          <div className="mb-6 pb-4 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-[#123B70]">Welcome to Portal</h2>
              <p className="text-xs text-slate-500">Sign in to access your role-specific academic dashboard</p>
            </div>
            <div className="w-8 h-8 rounded-full bg-brand-50 flex items-center justify-center text-brand-600">
              <ShieldCheck className="w-4 h-4" />
            </div>
          </div>

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Username or Email Address
              </label>
              <div className="relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <UserIcon className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="block w-full pl-10 pr-3 py-2.5 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 bg-[#F8FAFC]"
                  placeholder="e.g. admin or teacher.ahmed"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Password
              </label>
              <div className="relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="block w-full pl-10 pr-10 py-2.5 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 bg-[#F8FAFC]"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {(error || localError) && (
              <div className="p-3 rounded-xl bg-red-50 border border-red-200 text-xs text-red-700">
                {error || localError}
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex justify-center items-center gap-2 py-3 px-4 rounded-xl shadow-md text-xs font-bold text-white bg-[#123B70] hover:bg-[#2167B2] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-500 transition-all disabled:opacity-50"
            >
              <LogIn className="w-4 h-4" />
              <span>{isLoading ? 'Authenticating Credentials...' : 'Sign In to Portal'}</span>
            </button>
          </form>

          {/* Quick Persona Switcher for pair-programming and evaluations */}
          <div className="mt-8 pt-6 border-t border-slate-100">
            <div className="text-center mb-3">
              <span className="text-[11px] font-bold uppercase tracking-wider text-slate-500 bg-slate-50 px-3 py-1 rounded-full border border-slate-100">
                Or Quick Switch Demo Role
              </span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
              {personas.map((p) => {
                const Icon = p.icon;
                return (
                  <button
                    key={p.user}
                    type="button"
                    onClick={() => handleQuickLogin(p.user)}
                    className={`p-2.5 rounded-xl border text-left flex items-start gap-2.5 transition-all ${p.color}`}
                  >
                    <div className="p-2 rounded-lg bg-white shadow-sm flex-shrink-0">
                      <Icon className="w-4 h-4" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-bold text-slate-800 leading-tight">{p.role}</p>
                      <p className="text-[10px] text-slate-500 truncate mt-0.5">{p.desc}</p>
                      <p className="text-[9px] font-mono font-semibold text-slate-400 mt-0.5">user: {p.user}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        <div className="text-center text-xs text-slate-400 mt-6 space-y-1">
          <p>© {new Date().getFullYear()} Bluebells Schooling System • Latifabad, Hyderabad, Sindh, Pakistan</p>
          <p className="text-[11px]">Affiliated with Board of Intermediate & Secondary Education (BISE) Hyderabad</p>
        </div>
      </div>
    </div>
  );
};
