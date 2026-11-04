import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { SchoolProfile } from '../types';
import { 
  ShieldCheck, 
  User as UserIcon, 
  Lock, 
  GraduationCap, 
  BookOpen, 
  Users, 
  CreditCard,
  Building2,
  LogIn
} from 'lucide-react';

interface LoginPageProps {
  schoolProfile?: SchoolProfile;
}

export const LoginPage: React.FC<LoginPageProps> = ({ schoolProfile }) => {
  const { login, isLoading, error } = useAuth();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('Admin@12345');
  const [localError, setLocalError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalError(null);
    try {
      await login(username, password);
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
    } catch (err: unknown) {
      setLocalError((err as Error).message);
    }
  };

  const personas = [
    { role: 'Super Administrator', user: 'admin', desc: 'Full school ops, configuration & audit', icon: ShieldCheck, color: 'text-purple-600 bg-purple-50 hover:bg-purple-100 border-purple-200' },
    { role: 'Academic Principal', user: 'principal', desc: 'Huma Arif • Academic leadership & institutional oversight', icon: Building2, color: 'text-brand-700 bg-brand-50 hover:bg-brand-100 border-brand-200' },
    { role: 'Teacher (Mathematics)', user: 'teacher.ahmed', desc: 'Sir Ahmed Khan • Class 9-A teacher & LMS', icon: BookOpen, color: 'text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border-emerald-200' },
    { role: 'Accountant', user: 'accountant', desc: 'Syed Tariq • Fee ledger, receipts & challans', icon: CreditCard, color: 'text-amber-700 bg-amber-50 hover:bg-amber-100 border-amber-200' },
    { role: 'Parent (Guardian)', user: 'parent.khan', desc: 'Mohammad Tariq Khan • Father of Ali Khan (Class 9-A)', icon: Users, color: 'text-blue-700 bg-blue-50 hover:bg-blue-100 border-blue-200' },
    { role: 'Student (Class 9-A)', user: 'student.ali', desc: 'Ali Khan • Homework submissions & report card', icon: GraduationCap, color: 'text-indigo-700 bg-indigo-50 hover:bg-indigo-100 border-indigo-200' },
  ];

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div className="w-16 h-16 rounded-2xl bg-brand-700 text-white font-bold text-3xl mx-auto flex items-center justify-center shadow-lg mb-4">
          B
        </div>
        <h2 className="text-2xl font-bold tracking-tight text-slate-800">
          {schoolProfile?.name || 'Bluebells Schooling System'}
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          {schoolProfile?.address || 'Adjacent Autobahn Road, Latifabad Unit 2, Hyderabad, Sindh'}
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-xl">
        <div className="bg-white py-8 px-6 shadow-xl rounded-2xl sm:px-10 border border-slate-200">
          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Username or Email
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <UserIcon className="w-4 h-4" />
                </div>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="block w-full pl-10 pr-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500"
                  placeholder="e.g. admin or teacher.ahmed"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Password
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                  <Lock className="w-4 h-4" />
                </div>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="block w-full pl-10 pr-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500"
                  placeholder="••••••••"
                />
              </div>
            </div>

            {(error || localError) && (
              <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700">
                {error || localError}
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex justify-center items-center gap-2 py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-semibold text-white bg-brand-700 hover:bg-brand-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-500 transition-colors disabled:opacity-50"
            >
              <LogIn className="w-4 h-4" />
              {isLoading ? 'Authenticating...' : 'Sign In to Portal'}
            </button>
          </form>

          <div className="mt-8 pt-6 border-t border-slate-200">
            <div className="text-center mb-4">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-500">
                Or Quick Switch Demo Role (1-Click Login)
              </span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              {personas.map((p) => {
                const Icon = p.icon;
                return (
                  <button
                    key={p.user}
                    type="button"
                    onClick={() => handleQuickLogin(p.user)}
                    className={`p-3 rounded-xl border text-left flex items-start gap-3 transition-all ${p.color}`}
                  >
                    <div className="p-2 rounded-lg bg-white shadow-sm flex-shrink-0">
                      <Icon className="w-4 h-4" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-bold text-slate-800 leading-tight">{p.role}</p>
                      <p className="text-[11px] text-slate-500 truncate mt-0.5">{p.desc}</p>
                      <p className="text-[10px] font-mono font-semibold text-slate-400 mt-1">user: {p.user}</p>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        <div className="text-center text-xs text-slate-400 mt-6">
          © 2026 Bluebells Schooling System • Latifabad, Hyderabad, Sindh, Pakistan
        </div>
      </div>
    </div>
  );
};
