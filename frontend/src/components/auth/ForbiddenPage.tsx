import React from 'react';
import { ShieldAlert, ArrowLeft, Home } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const ForbiddenPage: React.FC = () => {
  const { user } = useAuth();
  const primaryRole = user?.roles?.[0] || 'ROLE_ANONYMOUS';

  return (
    <div className="min-h-[70vh] flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl border border-red-200 shadow-xl p-8 text-center">
        <div className="w-16 h-16 bg-red-100 text-red-600 rounded-full flex items-center justify-center mx-auto mb-4">
          <ShieldAlert className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-bold text-slate-800 tracking-tight">Access Restricted</h2>
        <p className="text-sm text-slate-600 mt-2">
          Your current account role (<span className="font-mono font-bold text-red-700 bg-red-50 px-1.5 py-0.5 rounded">{primaryRole.replace('ROLE_', '')}</span>) 
          does not have permission to access this administrative module.
        </p>

        <div className="mt-4 p-3 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-500 text-left">
          <p className="font-semibold text-slate-700 mb-1">Campus Security Policy:</p>
          <p>Bluebells Schooling System enforces strict role-based access control (RBAC). Students and parents cannot access staff administration modules, fee configurations, or institutional settings.</p>
        </div>

        <div className="mt-6 flex flex-col sm:flex-row gap-3 justify-center">
          <Link
            to="/"
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-brand-700 hover:bg-brand-800 text-white text-xs font-semibold rounded-lg shadow transition-colors"
          >
            <Home className="w-4 h-4" />
            <span>Go to My Dashboard</span>
          </Link>
          <button
            onClick={() => window.history.back()}
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Go Back</span>
          </button>
        </div>
      </div>
    </div>
  );
};
