import React, { useState } from 'react';
import { 
  CreditCard, 
  TrendingUp, 
  FileText, 
  AlertTriangle,
  PlusCircle
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { RecordPaymentModal } from '../../components/modals/RecordPaymentModal';
import { Link } from 'react-router-dom';

export const AccountantDashboard: React.FC = () => {
  const { user } = useAuth();
  const [showPaymentModal, setShowPaymentModal] = useState(false);

  const accountantName = user?.fullName || 'Syed Tariq';

  const stats = [
    { label: 'October Collection', value: 'Rs. 1,420,000', sub: '84% target reached', icon: CreditCard, color: 'text-purple-600 bg-purple-50' },
    { label: 'Outstanding Balance', value: 'Rs. 270,000', sub: '32 students overdue', icon: AlertTriangle, color: 'text-amber-600 bg-amber-50' },
    { label: 'Invoices Generated', value: '482 Vouchers', sub: 'October 2026 session', icon: FileText, color: 'text-blue-600 bg-blue-50' },
    { label: 'Today Collections', value: 'Rs. 42,500', sub: '5 receipts issued', icon: TrendingUp, color: 'text-emerald-600 bg-emerald-50' },
  ];

  const recentTransactions = [
    { receiptNo: 'REC-202610-091', student: 'Ali Khan (09-A-12)', amount: 'Rs. 8,500', method: 'Cash', date: '28 Oct 2026', status: 'COMPLETED' },
    { receiptNo: 'REC-202610-090', student: 'Sara Khan (08-R-05)', amount: 'Rs. 7,500', method: 'Bank Alfalah', date: '28 Oct 2026', status: 'COMPLETED' },
    { receiptNo: 'REC-202610-089', student: 'Shahid Afridi (09-A-02)', amount: 'Rs. 8,500', method: 'JazzCash', date: '27 Oct 2026', status: 'COMPLETED' },
    { receiptNo: 'REC-202610-088', student: 'Bilal Hassan (09-B-01)', amount: 'Rs. 8,500', method: 'Cash', date: '27 Oct 2026', status: 'COMPLETED' },
  ];

  return (
    <div className="space-y-6">
      {/* Accountant Welcome Banner */}
      <div className="bg-gradient-to-r from-purple-900 to-indigo-800 rounded-2xl p-6 text-white shadow-lg flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-white/20 text-white mb-2 inline-block">
            Accounts & Finance Management
          </span>
          <h2 className="text-2xl font-bold tracking-tight">
            Welcome, {accountantName}
          </h2>
          <p className="text-purple-100 text-sm mt-1 max-w-2xl">
            Accounts Office • Bluebells Schooling System Latifabad Campus. Student fee collection, challan issuance, double-entry ledger bookkeeping.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setShowPaymentModal(true)}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg shadow text-xs transition-colors"
          >
            <PlusCircle className="w-4 h-4" />
            <span>Record Student Payment</span>
          </button>
        </div>
      </div>

      {/* Financial KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {stats.map((stat, idx) => {
          const Icon = stat.icon;
          return (
            <div key={idx} className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm flex flex-col justify-between">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-slate-500">{stat.label}</span>
                <div className={`p-2 rounded-lg ${stat.color}`}>
                  <Icon className="w-5 h-5" />
                </div>
              </div>
              <div className="mt-4">
                <span className="text-2xl font-bold text-slate-800">{stat.value}</span>
                <p className="text-xs text-slate-500 mt-1">{stat.sub}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Recent Collections Table */}
      <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
            <CreditCard className="w-5 h-5 text-purple-700" />
            Recent Fee Receipts & Collections
          </h3>
          <Link to="/fees" className="text-xs font-semibold text-brand-700 hover:text-brand-800">
            View Fee Ledger & Invoices →
          </Link>
        </div>

        <div className="border border-slate-200 rounded-xl overflow-hidden text-xs">
          <table className="w-full text-left">
            <thead className="bg-slate-50 uppercase text-[10px] font-semibold text-slate-500">
              <tr>
                <th className="py-3 px-4">Receipt Ref</th>
                <th className="py-3 px-4">Student & Section</th>
                <th className="py-3 px-4">Amount</th>
                <th className="py-3 px-4">Channel</th>
                <th className="py-3 px-4">Date</th>
                <th className="py-3 px-4">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {recentTransactions.map((tx, idx) => (
                <tr key={idx} className="hover:bg-slate-50 transition-colors">
                  <td className="py-3 px-4 font-mono font-bold text-brand-900">{tx.receiptNo}</td>
                  <td className="py-3 px-4 font-semibold text-slate-800">{tx.student}</td>
                  <td className="py-3 px-4 font-bold text-slate-900 font-mono">{tx.amount}</td>
                  <td className="py-3 px-4 text-slate-600">{tx.method}</td>
                  <td className="py-3 px-4 text-slate-500 font-mono">{tx.date}</td>
                  <td className="py-3 px-4">
                    <span className="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 font-bold text-[10px]">
                      {tx.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <RecordPaymentModal
        isOpen={showPaymentModal}
        onClose={() => setShowPaymentModal(false)}
        onSuccess={() => {}}
      />
    </div>
  );
};
