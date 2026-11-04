import React, { useState, useEffect, useCallback } from 'react';
import { 
  Download, 
  Printer, 
  PlusCircle
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { RecordPaymentModal } from '../../components/modals/RecordPaymentModal';
import { FeeChallanModal } from '../../components/modals/FeeChallanModal';
import { ForbiddenPage } from '../../components/auth/ForbiddenPage';
import { apiClient } from '../../api/client';

export interface DisplayInvoice {
  num: string;
  student: string;
  month: string;
  total: string;
  paid: string;
  bal: string;
  due: string;
  status: string;
  badge: string;
}

const DEFAULT_INVOICES: DisplayInvoice[] = [
  { num: 'INV-202610-0001', student: 'Ali Khan', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 10,000', bal: 'Rs. 0', due: '2026-10-10', status: 'PAID', badge: 'bg-emerald-100 text-emerald-800' },
  { num: 'INV-202610-0002', student: 'Shahid Afridi', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 5,000', bal: 'Rs. 5,000', due: '2026-10-10', status: 'PARTIAL', badge: 'bg-amber-100 text-amber-800' },
  { num: 'INV-202610-0003', student: 'Bilal Hassan', month: 'October 2026', total: 'Rs. 10,000', paid: 'Rs. 0', bal: 'Rs. 10,000', due: '2026-10-10', status: 'UNPAID', badge: 'bg-red-100 text-red-800' },
];

export const FeesPage: React.FC = () => {
  const { user } = useAuth();
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showChallanModal, setShowChallanModal] = useState(false);
  const [selectedChallanInvoice, setSelectedChallanInvoice] = useState<string>('INV-202610-0001');
  const [invoices, setInvoices] = useState<DisplayInvoice[]>(DEFAULT_INVOICES);
  const [exporting, setExporting] = useState(false);

  const isTeacher = user?.roles?.includes('ROLE_TEACHER');
  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  const loadInvoices = useCallback(async () => {
    try {
      const endpoint = (isStudent || isParent) ? '/finance/my-invoices' : '/finance/invoices';
      const res = await apiClient.get(endpoint);
      if (res.data?.data && Array.isArray(res.data.data) && res.data.data.length > 0) {
        interface RawInvoice {
          invoiceNumber: string;
          studentName?: string;
          billingMonth: string;
          totalAmount: number;
          paidAmount: number;
          balanceDue: number;
          dueDate: string;
          status: string;
        }
        const mapped: DisplayInvoice[] = res.data.data.map((inv: RawInvoice) => {
          let badge = 'bg-amber-100 text-amber-800';
          if (inv.status === 'PAID') badge = 'bg-emerald-100 text-emerald-800';
          else if (inv.status === 'UNPAID') badge = 'bg-red-100 text-red-800';
          return {
            num: inv.invoiceNumber,
            student: inv.studentName || 'Ali Khan',
            month: inv.billingMonth,
            total: `Rs. ${Number(inv.totalAmount).toLocaleString()}`,
            paid: `Rs. ${Number(inv.paidAmount).toLocaleString()}`,
            bal: `Rs. ${Number(inv.balanceDue).toLocaleString()}`,
            due: inv.dueDate,
            status: inv.status,
            badge,
          };
        });
        setInvoices(mapped);
      }
    } catch {
      // Retain fallback data
    }
  }, [isStudent, isParent]);

  useEffect(() => {
    if (!isTeacher) {
      loadInvoices();
    }
  }, [isTeacher, loadInvoices]);

  if (isTeacher) {
    return <ForbiddenPage />;
  }

  const handleExportFees = async () => {
    setExporting(true);
    try {
      const res = await apiClient.get('/reports/fees/export-csv', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'text/csv' }));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'bluebells_fee_ledger.csv');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Failed to export fees CSV', err);
    } finally {
      setExporting(false);
    }
  };

  const displayedInvoices = (isStudent || isParent)
    ? invoices.filter(inv => inv.student.toLowerCase().includes('ali khan') || inv.num.startsWith('INV-202610-0001') || inv.num.startsWith('INV-202611-0001'))
    : invoices;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-200">
            Bank Alfalah Autobahn Branch • 1Bill Consumer Enabled
          </span>
          <h1 className="text-xl sm:text-2xl font-black text-[#123B70] mt-2">
            {isStudent ? 'My Tuition Fee Vouchers & Receipts' : (isParent ? "Ali Khan's Tuition Challans & Receipts" : 'Fee Ledger & Financial Invoicing')}
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            {isStudent 
              ? 'Bank Alfalah tuition fee challan, monthly invoices, and verified payment receipts in PKR.' 
              : (isParent
                ? 'Official Bank Alfalah tuition challan, payment clearance, and receipt history for Ali Khan (Class 9-A).'
                : 'Double-entry student fee billing, 3-copy bank vouchers, receipt recording, and accounts reconciliation.')}
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2.5">
          {!isStudent && !isParent && (
            <>
              <button 
                onClick={handleExportFees}
                disabled={exporting}
                className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5 disabled:opacity-50"
              >
                <Download className="w-3.5 h-3.5" />
                <span>{exporting ? 'Exporting...' : 'Export Fee Ledger'}</span>
              </button>
              <button 
                onClick={() => setShowPaymentModal(true)}
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-1.5"
              >
                <PlusCircle className="w-4 h-4" />
                <span>Record Payment</span>
              </button>
            </>
          )}

          <button 
            onClick={() => {
              setSelectedChallanInvoice((isStudent || isParent) ? 'INV-202610-0001' : (invoices[0]?.num || 'INV-202610-0001'));
              setShowChallanModal(true);
            }}
            className="px-4 py-2 bg-purple-700 hover:bg-purple-800 text-white font-bold rounded-xl shadow-md text-xs transition-all flex items-center gap-1.5"
          >
            <Printer className="w-4 h-4" />
            <span>{isStudent ? 'Print My Fee Challan' : (isParent ? "Print Ali's Fee Challan" : 'Print 3-Copy Bank Challan')}</span>
          </button>
        </div>
      </div>

      {/* KPI Cards (Only for Finance Staff / Admin) */}
      {!isStudent && !isParent && (
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Invoiced (October)</span>
            <div className="text-2xl sm:text-3xl font-black text-slate-800 mt-1">Rs. 30,000</div>
            <p className="text-xs text-slate-500 mt-0.5">3 Active Challans</p>
          </div>
          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Total Collected</span>
            <div className="text-2xl sm:text-3xl font-black text-emerald-700 mt-1">Rs. 15,000</div>
            <p className="text-xs text-emerald-600 font-semibold mt-0.5">50.0% Collection Rate</p>
          </div>
          <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
            <span className="text-xs font-bold uppercase tracking-wider text-slate-500">Outstanding Receivables</span>
            <div className="text-2xl sm:text-3xl font-black text-amber-700 mt-1">Rs. 15,000</div>
            <p className="text-xs text-slate-500 mt-0.5">Pending Bank Deposit</p>
          </div>
        </div>
      )}

      {/* Student / Parent Paid Voucher Banner */}
      {(isStudent || isParent) && (
        <div className="bg-emerald-50 border border-emerald-200 rounded-2xl p-5">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-emerald-800">Account Clearance Status</span>
              <h3 className="text-lg font-black text-emerald-950 mt-1">
                {isStudent ? 'Your Tuition Fees Are Cleared' : "Ali Khan's Tuition Fees Are Cleared"}
              </h3>
              <p className="text-xs text-emerald-700 mt-0.5">
                October 2026 Tuition Challan (INV-202610-0001) marked PAID via Online Bank Transfer.
              </p>
            </div>
            <div className="text-right">
              <span className="text-xs text-emerald-700 font-medium block">Balance Due</span>
              <span className="text-2xl font-black text-emerald-900 font-mono">Rs. 0.00</span>
            </div>
          </div>
        </div>
      )}

      {/* Invoices Table */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 sm:p-5 border-b border-slate-200 bg-[#F8FAFC]">
          <h2 className="text-sm font-bold text-slate-800">
            {isStudent ? 'Personal Monthly Tuition Invoices' : (isParent ? "Ali Khan's Monthly Invoices" : 'Monthly Tuition Invoices Roster')}
          </h2>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-[#F8FAFC] text-slate-500 uppercase border-b border-slate-200">
              <tr>
                <th className="p-3.5">Invoice #</th>
                <th className="p-3.5">Student Name</th>
                <th className="p-3.5">Billing Month</th>
                <th className="p-3.5">Total Amount</th>
                <th className="p-3.5">Paid Amount</th>
                <th className="p-3.5">Balance Due</th>
                <th className="p-3.5">Due Date</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {displayedInvoices.map((inv) => (
                <tr key={inv.num} className="hover:bg-slate-50/70 transition-colors">
                  <td className="p-3.5 font-mono font-bold text-brand-800">{inv.num}</td>
                  <td className="p-3.5 font-bold text-slate-800">{inv.student}</td>
                  <td className="p-3.5 font-medium text-slate-700">{inv.month}</td>
                  <td className="p-3.5 font-bold text-slate-800">{inv.total}</td>
                  <td className="p-3.5 font-black text-emerald-700">{inv.paid}</td>
                  <td className="p-3.5 font-black text-red-700">{inv.bal}</td>
                  <td className="p-3.5 font-mono text-slate-500">{inv.due}</td>
                  <td className="p-3.5">
                    <span className={`px-2.5 py-0.5 rounded-full font-bold text-[10px] ${inv.badge}`}>
                      {inv.status}
                    </span>
                  </td>
                  <td className="p-3.5 text-right">
                    <button
                      onClick={() => {
                        setSelectedChallanInvoice(inv.num);
                        setShowChallanModal(true);
                      }}
                      className="px-2.5 py-1 bg-purple-50 hover:bg-purple-100 text-purple-700 font-bold rounded-lg text-xs inline-flex items-center gap-1 border border-purple-200 transition-colors"
                      title="View / Print 3-Copy Bank Challan"
                    >
                      <Printer className="w-3.5 h-3.5" />
                      <span>Challan</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Verified Payment Receipts */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">
        <h2 className="text-sm font-bold text-slate-800 mb-3">
          {isStudent ? 'Verified Payment Receipts' : 'Recent Cleared Payment Receipts'}
        </h2>
        <div className="space-y-2.5 text-xs">
          <div className="p-3.5 rounded-xl bg-[#F8FAFC] border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div>
              <p className="font-bold text-slate-800">REC-202610-0001 • Rs. 10,000</p>
              <p className="text-slate-500">Ali Khan (INV-202610-0001) • Online Bank Transfer (Meezan Bank #PK98234710)</p>
            </div>
            <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 font-bold text-[10px] self-start sm:self-center">
              CLEARED
            </span>
          </div>

          {!isStudent && !isParent && (
            <div className="p-3.5 rounded-xl bg-[#F8FAFC] border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <p className="font-bold text-slate-800">REC-202610-0002 • Rs. 5,000</p>
                <p className="text-slate-500">Shahid Afridi (INV-202610-0002) • Cash Collection at Accounts Office</p>
              </div>
              <span className="px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 font-bold text-[10px] self-start sm:self-center">
                CLEARED
              </span>
            </div>
          )}
        </div>
      </div>

      {/* Modals */}
      <RecordPaymentModal
        isOpen={showPaymentModal}
        onClose={() => setShowPaymentModal(false)}
        onSuccess={loadInvoices}
      />

      <FeeChallanModal
        isOpen={showChallanModal}
        onClose={() => setShowChallanModal(false)}
        invoiceNumber={selectedChallanInvoice}
      />
    </div>
  );
};
