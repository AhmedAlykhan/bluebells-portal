export interface FeeChallanItem {
  id: number;
  description: string;
  amount: number;
}

export interface FeeChallanData {
  id: string;
  challanNumber: string;
  invoiceNumber: string;
  studentId: number;
  studentName: string;
  fatherName: string;
  admissionNumber: string;
  rollNumber: string;
  className: string;
  sectionName: string;
  billingMonth: string;
  issueDate: string;
  dueDate: string;
  validUptoDate: string;
  status: 'PAID' | 'UNPAID' | 'PARTIAL';
  paidDate?: string;
  paymentMode?: string;
  transactionRef?: string;
  items: FeeChallanItem[];
  lateFeeFine: number;
  subTotal: number;
  totalAmount: number;
  totalAfterDueDate: number;
  amountInWords: string;
  amountInWordsAfterDueDate: string;
}

export const DEMO_FEE_CHALLANS: Record<string, FeeChallanData> = {
  'INV-202610-0001': {
    id: 'INV-202610-0001',
    challanNumber: 'BSS-CH-202610-0001',
    invoiceNumber: 'INV-202610-0001',
    studentId: 1,
    studentName: 'Ali Khan',
    fatherName: 'Mohammad Tariq Khan',
    admissionNumber: 'BSS-2024-0104',
    rollNumber: '09-A-12',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    billingMonth: 'October 2026',
    issueDate: '01 Oct 2026',
    dueDate: '10 Oct 2026',
    validUptoDate: '25 Oct 2026',
    status: 'PAID',
    paidDate: '05 Oct 2026',
    paymentMode: 'Online Bank Transfer (Meezan Bank)',
    transactionRef: 'FT2627810482910',
    items: [
      { id: 1, description: 'Monthly Tuition Fee', amount: 8500 },
      { id: 2, description: 'Science & Computer Lab Charges', amount: 500 },
      { id: 3, description: 'Utility & Generator Backup Surcharge', amount: 500 },
      { id: 4, description: 'Examination & Assessment Charges', amount: 500 },
    ],
    lateFeeFine: 500,
    subTotal: 10000,
    totalAmount: 10000,
    totalAfterDueDate: 10500,
    amountInWords: 'Ten Thousand Rupees Only',
    amountInWordsAfterDueDate: 'Ten Thousand Five Hundred Rupees Only',
  },
  'INV-202611-0001': {
    id: 'INV-202611-0001',
    challanNumber: 'BSS-CH-202611-0001',
    invoiceNumber: 'INV-202611-0001',
    studentId: 1,
    studentName: 'Ali Khan',
    fatherName: 'Mohammad Tariq Khan',
    admissionNumber: 'BSS-2024-0104',
    rollNumber: '09-A-12',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    billingMonth: 'November 2026',
    issueDate: '01 Nov 2026',
    dueDate: '10 Nov 2026',
    validUptoDate: '25 Nov 2026',
    status: 'UNPAID',
    items: [
      { id: 1, description: 'Monthly Tuition Fee', amount: 8500 },
      { id: 2, description: 'Science & Computer Lab Charges', amount: 500 },
      { id: 3, description: 'Utility & Generator Backup Surcharge', amount: 500 },
      { id: 4, description: 'Library & Sports Fund', amount: 500 },
    ],
    lateFeeFine: 500,
    subTotal: 10000,
    totalAmount: 10000,
    totalAfterDueDate: 10500,
    amountInWords: 'Ten Thousand Rupees Only',
    amountInWordsAfterDueDate: 'Ten Thousand Five Hundred Rupees Only',
  },
  'INV-202610-0002': {
    id: 'INV-202610-0002',
    challanNumber: 'BSS-CH-202610-0002',
    invoiceNumber: 'INV-202610-0002',
    studentId: 2,
    studentName: 'Shahid Afridi',
    fatherName: 'Mohammad Riaz Afridi',
    admissionNumber: 'BSS-2026-0003',
    rollNumber: '09-A-02',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    billingMonth: 'October 2026',
    issueDate: '01 Oct 2026',
    dueDate: '10 Oct 2026',
    validUptoDate: '25 Oct 2026',
    status: 'UNPAID',
    items: [
      { id: 1, description: 'Monthly Tuition Fee', amount: 8500 },
      { id: 2, description: 'Science & Computer Lab Charges', amount: 500 },
      { id: 3, description: 'Utility & Generator Backup Surcharge', amount: 500 },
      { id: 4, description: 'Examination & Assessment Charges', amount: 500 },
    ],
    lateFeeFine: 500,
    subTotal: 10000,
    totalAmount: 10000,
    totalAfterDueDate: 10500,
    amountInWords: 'Ten Thousand Rupees Only',
    amountInWordsAfterDueDate: 'Ten Thousand Five Hundred Rupees Only',
  },
  'INV-202610-0003': {
    id: 'INV-202610-0003',
    challanNumber: 'BSS-CH-202610-0003',
    invoiceNumber: 'INV-202610-0003',
    studentId: 3,
    studentName: 'Sara Khan',
    fatherName: 'Mohammad Tariq Khan',
    admissionNumber: 'BSS-2023-0042',
    rollNumber: '08-R-05',
    className: 'Class 8 (Middle School)',
    sectionName: 'Section Rose',
    billingMonth: 'October 2026',
    issueDate: '01 Oct 2026',
    dueDate: '10 Oct 2026',
    validUptoDate: '25 Oct 2026',
    status: 'PAID',
    paidDate: '07 Oct 2026',
    paymentMode: 'Cash at Bank Alfalah Counter',
    transactionRef: 'BA-HYD-98124',
    items: [
      { id: 1, description: 'Monthly Tuition Fee', amount: 8500 },
      { id: 2, description: 'General Science Lab Charges', amount: 500 },
      { id: 3, description: 'Utility & Generator Backup Surcharge', amount: 500 },
      { id: 4, description: 'Art & Activity Material Charges', amount: 500 },
    ],
    lateFeeFine: 500,
    subTotal: 10000,
    totalAmount: 10000,
    totalAfterDueDate: 10500,
    amountInWords: 'Ten Thousand Rupees Only',
    amountInWordsAfterDueDate: 'Ten Thousand Five Hundred Rupees Only',
  },
};

export const BANK_COLLECTION_DETAILS = {
  bankName: 'Bank Alfalah Limited',
  branchName: 'Autobahn Road Branch (0219), Latifabad, Hyderabad',
  branchCode: '0219',
  accountTitle: 'BLUEBELLS SCHOOLING SYSTEM (PVT) LTD',
  accountNumber: '0219-1004829104',
  iban: 'PK36ALFH02191004829104',
  altBankName: 'Meezan Bank Limited',
  altAccountTitle: 'BLUEBELLS SCHOOLING SYSTEM',
  altAccountNumber: '0812-0104829301',
  altIban: 'PK89MEZN08120104829301',
  oneBillConsumerId: '1004829124',
  kuickpayId: '928410',
  campusAddress: 'Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad',
  campusPhone: '+92 316 9681932 / +92 22 3812940',
  schoolReg: 'SE&LD / BISEH Reg No: HYD-SEC-2024-918',
};
