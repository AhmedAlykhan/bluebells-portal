import { useState, useEffect } from 'react';
import { X, Printer, CheckCircle, Clock } from 'lucide-react';
import { 
  DEMO_FEE_CHALLANS, 
  BANK_COLLECTION_DETAILS, 
  FeeChallanData 
} from '../../data/feeChallanData';

interface FeeChallanModalProps {
  isOpen: boolean;
  onClose: () => void;
  invoiceNumber?: string;
}

export function FeeChallanModal({
  isOpen,
  onClose,
  invoiceNumber: initialInvoiceNum,
}: FeeChallanModalProps) {
  const [selectedInvoice, setSelectedInvoice] = useState<string>(
    initialInvoiceNum || 'INV-202610-0001'
  );

  useEffect(() => {
    if (initialInvoiceNum && DEMO_FEE_CHALLANS[initialInvoiceNum]) {
      setSelectedInvoice(initialInvoiceNum);
    }
  }, [initialInvoiceNum]);

  useEffect(() => {
    if (!isOpen) return;

    document.body.classList.add('has-active-print-modal');

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      document.body.classList.remove('has-active-print-modal');
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const challan: FeeChallanData = DEMO_FEE_CHALLANS[selectedInvoice] || DEMO_FEE_CHALLANS['INV-202610-0001'];

  const handlePrint = () => {
    window.print();
  };

  const renderSingleChallanCopy = (
    copyTitle: string, 
    copySubtitle: string, 
    badgeColor: string,
    isLast: boolean = false
  ) => (
    <div className={`flex-1 p-3 bg-white flex flex-col justify-between text-[10px] leading-tight text-slate-800 ${!isLast ? 'border-r border-dashed border-slate-400 print:border-r print:border-dashed print:border-slate-500' : ''}`}>
      {/* Copy Header */}
      <div>
        <div className="text-center pb-1.5 border-b border-slate-900">
          <div className="flex items-center justify-center gap-1.5 mb-0.5">
            <div className="w-6 h-6 rounded bg-slate-900 text-white font-black text-xs flex items-center justify-center">
              B
            </div>
            <div className="text-left">
              <h1 className="text-[12px] font-black uppercase tracking-tight text-slate-950 leading-none">
                Bluebells Schooling System
              </h1>
              <p className="text-[8px] text-slate-600 font-medium">
                Latifabad Unit 2, Hyderabad • BISEH Reg: HYD-SEC-2024-918
              </p>
            </div>
          </div>
          <div className={`mt-1 py-0.5 px-2 rounded text-[9px] font-black uppercase tracking-wider ${badgeColor} text-white inline-block`}>
            {copyTitle} — {copySubtitle}
          </div>
        </div>

        {/* Bank & Collection Accounts */}
        <div className="my-1.5 p-1.5 bg-slate-50 border border-slate-300 rounded text-[9px] space-y-0.5">
          <div className="flex justify-between items-center">
            <span className="font-bold text-slate-900">Bank Alfalah Ltd (Autobahn Branch):</span>
            <span className="font-mono font-bold text-slate-950">{BANK_COLLECTION_DETAILS.accountNumber}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="font-semibold text-slate-700">Meezan Bank Ltd (Islamic A/C):</span>
            <span className="font-mono text-slate-800">{BANK_COLLECTION_DETAILS.altAccountNumber}</span>
          </div>
          <div className="flex justify-between items-center pt-0.5 border-t border-slate-200 text-purple-900 font-bold">
            <span>1Bill / Digital Consumer ID:</span>
            <span className="font-mono bg-purple-100 px-1 rounded">{BANK_COLLECTION_DETAILS.oneBillConsumerId}</span>
          </div>
        </div>

        {/* Challan & Date Grid */}
        <div className="grid grid-cols-2 gap-1 mb-1.5 p-1 border border-slate-300 rounded bg-slate-50 text-[9px]">
          <div>
            <span className="text-slate-500 block text-[8px]">Challan Number:</span>
            <span className="font-mono font-black text-slate-900">{challan.challanNumber}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[8px]">Billing Month:</span>
            <span className="font-bold text-slate-900">{challan.billingMonth}</span>
          </div>
          <div>
            <span className="text-slate-500 block text-[8px]">Issue Date:</span>
            <span className="font-medium text-slate-800">{challan.issueDate}</span>
          </div>
          <div>
            <span className="text-red-700 block text-[8px] font-bold">Due Date (No Surcharge):</span>
            <span className="font-black text-red-800 font-mono">{challan.dueDate}</span>
          </div>
        </div>

        {/* Student Bio Grid */}
        <div className="border border-slate-300 rounded p-1.5 mb-1.5 bg-white text-[9px] space-y-0.5">
          <div className="flex justify-between">
            <span className="text-slate-500">Student Name:</span>
            <span className="font-bold text-slate-950 uppercase">{challan.studentName}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-500">Father's Name:</span>
            <span className="font-semibold text-slate-900">{challan.fatherName}</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-500">Class & Section:</span>
            <span className="font-bold text-slate-900">{challan.className} ({challan.sectionName})</span>
          </div>
          <div className="flex justify-between">
            <span className="text-slate-500">Roll No / Admission No:</span>
            <span className="font-mono font-bold text-slate-900">{challan.rollNumber} • {challan.admissionNumber}</span>
          </div>
        </div>

        {/* Fee Particulars Table */}
        <div className="border border-slate-400 rounded overflow-hidden mb-1.5">
          <table className="w-full text-left text-[9px]">
            <thead className="bg-slate-900 text-white font-bold text-[8px] uppercase">
              <tr>
                <th className="py-0.5 px-1.5">#</th>
                <th className="py-0.5 px-1.5">Fee Particulars</th>
                <th className="py-0.5 px-1.5 text-right">Amount (PKR)</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {challan.items.map((item, index) => (
                <tr key={item.id} className={index % 2 === 0 ? 'bg-white' : 'bg-slate-50'}>
                  <td className="py-0.5 px-1.5 text-slate-500 font-mono">{index + 1}</td>
                  <td className="py-0.5 px-1.5 font-medium text-slate-800">{item.description}</td>
                  <td className="py-0.5 px-1.5 text-right font-mono font-semibold">{item.amount.toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
            <tfoot className="border-t-2 border-slate-800 bg-slate-100 font-bold text-[9px]">
              <tr>
                <td colSpan={2} className="py-1 px-1.5 uppercase font-black">Net Total by Due Date:</td>
                <td className="py-1 px-1.5 text-right font-mono font-black text-slate-950 text-[10px]">
                  Rs. {challan.totalAmount.toLocaleString()}
                </td>
              </tr>
              <tr className="bg-rose-50 text-rose-950 border-t border-rose-200">
                <td colSpan={2} className="py-0.5 px-1.5 text-[8px]">Late Fee Surcharge (After {challan.dueDate}):</td>
                <td className="py-0.5 px-1.5 text-right font-mono text-[8px] font-bold">+Rs. {challan.lateFeeFine}</td>
              </tr>
              <tr className="bg-rose-100/70 text-rose-950 font-black">
                <td colSpan={2} className="py-0.5 px-1.5 uppercase text-[9px]">Payable After Due Date:</td>
                <td className="py-0.5 px-1.5 text-right font-mono text-[10px]">
                  Rs. {challan.totalAfterDueDate.toLocaleString()}
                </td>
              </tr>
            </tfoot>
          </table>
        </div>

        {/* Amount in Words */}
        <div className="p-1 bg-slate-50 border border-slate-200 rounded text-[8px] italic text-slate-600 mb-1.5">
          <strong className="not-italic text-slate-800">In Words: </strong>
          {challan.amountInWords}
        </div>

        {/* Instructions */}
        <div className="text-[7.5px] text-slate-600 space-y-0.5 border-t border-slate-200 pt-1">
          <p>• Pay at any online branch of Bank Alfalah or Meezan Bank across Pakistan.</p>
          <p>• Mobile Banking / ATM payments accepted via 1Bill Consumer ID: {BANK_COLLECTION_DETAILS.oneBillConsumerId}.</p>
          <p>• Late fee fine of Rs. {challan.lateFeeFine} is strictly applicable after {challan.dueDate}.</p>
          <p>• Fee once paid is non-refundable and non-transferable.</p>
        </div>
      </div>

      {/* Signature Space */}
      <div className="pt-4 mt-2 border-t border-slate-300 grid grid-cols-3 gap-1 text-center text-[7.5px] text-slate-600">
        <div>
          <div className="border-b border-dashed border-slate-400 h-3 mb-0.5"></div>
          <span>Depositor Sign</span>
        </div>
        <div>
          <div className="border-b border-dashed border-slate-400 h-3 mb-0.5"></div>
          <span>Accountant BSS</span>
        </div>
        <div>
          <div className="border-b border-dashed border-slate-400 h-3 mb-0.5"></div>
          <span>Bank Stamp / Sign</span>
        </div>
      </div>
    </div>
  );

  return (
    <div 
      className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/75 backdrop-blur-sm flex items-center justify-center p-2 sm:p-4 print:p-0 print:bg-white print:static"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose();
        }
      }}
    >
      {/* Dynamic Print CSS for Landscape 3-Copy Bank Challan */}
      <style>{`
        @media print {
          @page {
            size: A4 landscape;
            margin: 5mm;
          }
          body {
            background: white !important;
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
          }
        }
      `}</style>

      <div 
        className="relative bg-white w-full max-w-6xl rounded-2xl shadow-2xl border border-slate-200 overflow-hidden my-4 print:my-0 print:border-none print:shadow-none print:w-full print:max-w-none"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Modal Controls Toolbar (Hidden in print) */}
        <div className="px-6 py-4 bg-slate-900 text-white flex flex-col sm:flex-row sm:items-center justify-between gap-4 print:hidden shadow-md">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-purple-600 text-white font-bold flex items-center justify-center text-lg">
              Rs
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Official 3-Copy Bank Fee Challan</h2>
              <p className="text-xs text-slate-400">Standard Bank Deposit Voucher (Bank Copy, School Copy, Student Copy)</p>
            </div>
          </div>

          <div className="flex items-center gap-3 flex-wrap">
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 font-semibold">Challan / Student:</span>
              <select
                value={selectedInvoice}
                onChange={(e) => setSelectedInvoice(e.target.value)}
                className="px-3 py-1.5 bg-slate-800 border border-slate-700 rounded-lg text-xs font-bold text-amber-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
              >
                <option value="INV-202610-0001">Ali Khan (Class 9-A) — Oct 2026 (Paid)</option>
                <option value="INV-202611-0001">Ali Khan (Class 9-A) — Nov 2026 (Due)</option>
                <option value="INV-202610-0002">Shahid Afridi (Class 9-A) — Oct 2026</option>
                <option value="INV-202610-0003">Sara Khan (Class 8-R) — Oct 2026</option>
              </select>
            </div>

            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg text-xs font-bold shadow transition-colors"
            >
              <Printer className="w-4 h-4" />
              <span>Print 3-Copy Challan</span>
            </button>

            <button
              onClick={onClose}
              className="inline-flex items-center gap-1 px-3 py-2 bg-slate-800 hover:bg-rose-600 text-slate-200 hover:text-white rounded-lg text-xs font-semibold transition-colors"
              title="Close modal (Esc)"
            >
              <X className="w-4 h-4" />
              <span>Close</span>
            </button>
          </div>
        </div>

        {/* Invoice Info Bar on Screen (Hidden in print) */}
        <div className="bg-slate-100 px-6 py-2.5 border-b border-slate-200 flex flex-wrap items-center justify-between text-xs text-slate-600 print:hidden">
          <div className="flex items-center gap-4">
            <span>Student: <strong className="text-slate-900">{challan.studentName} ({challan.rollNumber})</strong></span>
            <span>Month: <strong className="text-slate-900">{challan.billingMonth}</strong></span>
            <span>Due Date: <strong className="text-red-700 font-mono">{challan.dueDate}</strong></span>
          </div>
          <div className="flex items-center gap-2">
            {challan.status === 'PAID' ? (
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 flex items-center gap-1">
                <CheckCircle className="w-3.5 h-3.5" />
                Paid: {challan.paymentMode} ({challan.paidDate})
              </span>
            ) : (
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-100 text-amber-800 flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" />
                Awaiting Bank Clearance
              </span>
            )}
            <span className="text-[11px] text-slate-500 font-mono">1Bill ID: {BANK_COLLECTION_DETAILS.oneBillConsumerId}</span>
          </div>
        </div>

        {/* Printable 3-Copy Bank Challan Voucher Document */}
        <div 
          id="printable-fee-challan" 
          className="p-4 sm:p-6 bg-slate-50 overflow-x-auto print:p-0 print:bg-white print:m-0 print:w-full"
        >
          {/* Print instruction header for paper orientation */}
          <div className="hidden print:block text-center text-[8px] text-slate-400 pb-1">
            Bluebells Schooling System • Official Computerized Fee Voucher • Bank Collection Copy
          </div>

          <div className="flex min-w-[850px] border-2 border-slate-900 rounded-lg overflow-hidden bg-white shadow-md print:shadow-none print:border-slate-800 print:min-w-0 print:w-full">
            {/* Copy 1: BANK COPY */}
            {renderSingleChallanCopy('Bank Copy', 'For Branch Deposit', 'bg-blue-800')}

            {/* Copy 2: SCHOOL COPY */}
            {renderSingleChallanCopy('School Copy', 'For Accounts Reconciliation', 'bg-purple-800')}

            {/* Copy 3: STUDENT COPY */}
            {renderSingleChallanCopy('Student / Parent Copy', 'Proof of Payment', 'bg-emerald-800', true)}
          </div>

          {/* Dotted cut indicator notice */}
          <div className="flex items-center justify-around text-[8px] font-mono text-slate-400 mt-2 px-4 print:text-slate-500">
            <span className="flex items-center gap-1">✂ Cut along dashed vertical separator for Bank Branch</span>
            <span className="flex items-center gap-1">✂ Retain Student Copy after receiving Bank Cashier Stamp</span>
          </div>
        </div>

        {/* Footer info (screen only) */}
        <div className="px-6 py-3 bg-slate-50 border-t border-slate-200 flex items-center justify-between text-xs text-slate-500 print:hidden">
          <p>
            Standard Pakistani A4 Landscape format. For high-volume printing, select <strong>Landscape</strong> orientation in your browser print dialog.
          </p>
          <p className="font-semibold text-slate-700">Bluebells Accounts Department</p>
        </div>
      </div>
    </div>
  );
}
