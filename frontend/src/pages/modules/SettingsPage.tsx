import { useState } from 'react';
import { 
  Building2, 
  ShieldCheck, 
  DollarSign, 
  Globe, 
  Clock, 
  Mail, 
  Phone, 
  MapPin, 
  CheckCircle2, 
  Award, 
  Key, 
  Save,
  Check
} from 'lucide-react';
import { BRAND_INFO } from '../../config/branding';

export function SettingsPage() {
  const [activeTab, setActiveTab] = useState<'profile' | 'banking' | 'rbac' | 'system'>('profile');
  const [saveSuccess, setSaveSuccess] = useState(false);

  const handleSimulatedSave = (e: React.FormEvent) => {
    e.preventDefault();
    setSaveSuccess(true);
    setTimeout(() => setSaveSuccess(false), 3000);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-brand-50 text-brand-700 border border-brand-200">
              System Administration
            </span>
            <span className="text-xs text-slate-400">•</span>
            <span className="text-xs text-slate-500 font-medium">Institution Code: BBSS-HYD-01</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">School Profile & Institutional Configuration</h1>
          <p className="text-sm text-slate-600 mt-1 max-w-2xl">
            Official campus registry, provincial board affiliations, banking reconciliation parameters, and security RBAC matrix for {BRAND_INFO.fullName}.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {saveSuccess ? (
            <div className="px-4 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-semibold flex items-center gap-2">
              <Check className="w-4 h-4 text-emerald-600" />
              <span>Configuration Saved</span>
            </div>
          ) : (
            <button
              onClick={(e) => handleSimulatedSave(e)}
              className="px-4 py-2 bg-brand-700 hover:bg-brand-800 text-white rounded-xl text-xs font-semibold flex items-center gap-2 shadow-sm transition-all"
            >
              <Save className="w-4 h-4" />
              <span>Save Configuration</span>
            </button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setActiveTab('profile')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'profile'
              ? 'bg-brand-700 text-white shadow-sm'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          Campus Profile & Affiliations
        </button>
        <button
          onClick={() => setActiveTab('banking')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'banking'
              ? 'bg-brand-700 text-white shadow-sm'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          Banking & 1Bill Gateway
        </button>
        <button
          onClick={() => setActiveTab('rbac')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'rbac'
              ? 'bg-brand-700 text-white shadow-sm'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          Role-Based Access Control (RBAC)
        </button>
        <button
          onClick={() => setActiveTab('system')}
          className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all ${
            activeTab === 'system'
              ? 'bg-brand-700 text-white shadow-sm'
              : 'text-slate-600 hover:bg-slate-100'
          }`}
        >
          System & Backup Security
        </button>
      </div>

      {/* TAB 1: Campus Profile & Affiliations */}
      {activeTab === 'profile' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <Building2 className="w-5 h-5 text-brand-700" />
              Campus Credentials & Location
            </h3>
            
            <div className="space-y-4 text-xs">
              <div>
                <span className="text-slate-500 block mb-1 font-medium">Institution Full Legal Name</span>
                <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl font-bold text-slate-800 text-sm">
                  {BRAND_INFO.fullName}
                </div>
              </div>

              <div>
                <span className="text-slate-500 block mb-1 font-medium">Official Tagline / Motto</span>
                <div className="p-3 bg-brand-50/50 border border-brand-100 rounded-xl font-semibold text-brand-800 italic">
                  "{BRAND_INFO.motto}"
                </div>
              </div>

              <div>
                <span className="text-slate-500 block mb-1 font-medium">Physical Campus Address</span>
                <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-800 flex items-start gap-2">
                  <MapPin className="w-4 h-4 text-brand-700 flex-shrink-0 mt-0.5" />
                  <span>{BRAND_INFO.address.primary}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <span className="text-slate-500 block mb-1 font-medium">Official Contact Phone</span>
                  <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-800 flex items-center gap-2">
                    <Phone className="w-3.5 h-3.5 text-brand-700" />
                    <span className="font-semibold">{BRAND_INFO.phone.primary}</span>
                  </div>
                </div>

                <div>
                  <span className="text-slate-500 block mb-1 font-medium">Official Inquiries Email</span>
                  <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl text-slate-800 flex items-center gap-2 truncate">
                    <Mail className="w-3.5 h-3.5 text-brand-700 flex-shrink-0" />
                    <span className="font-semibold truncate">{BRAND_INFO.email}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <Award className="w-5 h-5 text-brand-700" />
              Academic Accreditations & Leadership
            </h3>

            <div className="space-y-4 text-xs">
              <div className="p-4 rounded-xl bg-blue-50/60 border border-blue-200 text-slate-800 space-y-2">
                <span className="font-bold text-brand-900 block text-sm">Provincial Board Affiliation</span>
                <p className="text-xs text-brand-800 leading-relaxed">
                  {BRAND_INFO.affiliation}
                </p>
                <div className="flex items-center gap-2 text-[11px] text-brand-700 pt-1">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                  <span>Matriculation Examination Center Code: HYD-291</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                  <span className="text-slate-500 block text-[11px]">Head of Institution</span>
                  <span className="font-bold text-slate-800 text-xs mt-0.5 block">{BRAND_INFO.leadership.principal}</span>
                  <span className="text-[10px] text-brand-700 font-medium">Principal</span>
                </div>
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                  <span className="text-slate-500 block text-[11px]">Operations & Discipline</span>
                  <span className="font-bold text-slate-800 text-xs mt-0.5 block">{BRAND_INFO.leadership.vicePrincipal}</span>
                  <span className="text-[10px] text-brand-700 font-medium">Vice Principal</span>
                </div>
              </div>

              <div>
                <span className="text-slate-500 block mb-1 font-medium">Approved Grading Scheme</span>
                <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl">
                  <span className="font-semibold text-slate-800 block mb-1">BISE Hyderabad Standard Scale:</span>
                  <div className="grid grid-cols-3 gap-1.5 text-[11px]">
                    <span className="text-emerald-700">A-1: 80% - 100%</span>
                    <span className="text-brand-700">A: 70% - 79%</span>
                    <span className="text-sky-700">B: 60% - 69%</span>
                    <span className="text-amber-700">C: 50% - 59%</span>
                    <span className="text-orange-700">D: 40% - 49%</span>
                    <span className="text-rose-700">F: Below 40%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: Banking & 1Bill Gateway */}
      {activeTab === 'banking' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <DollarSign className="w-5 h-5 text-emerald-600" />
              Designated Bank Accounts for Challan Deposits
            </h3>

            <div className="space-y-3">
              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 space-y-2 text-xs">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-900 text-sm">{BRAND_INFO.banking.primaryBank}</span>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-100 text-emerald-800">
                    Primary Challan Bank
                  </span>
                </div>
                <div className="grid grid-cols-2 gap-2 pt-1 text-slate-600">
                  <div>
                    <span className="text-slate-400 block text-[10px]">Branch:</span>
                    <span className="font-semibold text-slate-800">{BRAND_INFO.banking.branch}</span>
                  </div>
                  <div>
                    <span className="text-slate-400 block text-[10px]">Account Title:</span>
                    <span className="font-semibold text-slate-800">{BRAND_INFO.banking.accountTitle}</span>
                  </div>
                  <div className="col-span-2">
                    <span className="text-slate-400 block text-[10px]">Account Number:</span>
                    <span className="font-mono font-bold text-brand-800">{BRAND_INFO.banking.accountNumber}</span>
                  </div>
                </div>
              </div>

              <div className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 space-y-2 text-xs">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-slate-900 text-sm">{BRAND_INFO.banking.secondaryBank}</span>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-blue-100 text-brand-800">
                    Islamic Banking Partner
                  </span>
                </div>
                <div className="grid grid-cols-2 gap-2 pt-1 text-slate-600">
                  <div>
                    <span className="text-slate-400 block text-[10px]">Branch:</span>
                    <span className="font-semibold text-slate-800">Latifabad Unit #7 Branch</span>
                  </div>
                  <div>
                    <span className="text-slate-400 block text-[10px]">Account Title:</span>
                    <span className="font-semibold text-slate-800">{BRAND_INFO.banking.accountTitle}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <Globe className="w-5 h-5 text-brand-700" />
              1Bill Digital Collection & Kuickpay Integration
            </h3>

            <div className="space-y-4 text-xs">
              <div className="p-4 rounded-xl bg-brand-50/60 border border-brand-200 text-slate-800 space-y-2">
                <span className="font-bold text-brand-900 block text-sm">1Bill Consumer ID Generation</span>
                <p className="text-xs text-brand-800 leading-relaxed">
                  Online fee payments across all 38 Pakistani banks, JazzCash, EasyPaisa, and SadaPay are automatically routed using the Bluebells Prefix.
                </p>
                <div className="p-2.5 bg-white rounded-lg border border-brand-200 font-mono text-center font-bold text-brand-900">
                  Prefix: 10048291 + [Challan No / Student GR]
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between py-1.5 border-b border-slate-100">
                  <span className="text-slate-600">Late Fee Surcharge:</span>
                  <span className="font-bold text-slate-800">Rs. 300 (applied after 10th of month)</span>
                </div>
                <div className="flex justify-between py-1.5 border-b border-slate-100">
                  <span className="text-slate-600">Reconciliation Interval:</span>
                  <span className="font-bold text-slate-800">Real-time Webhook + Midnight Batch</span>
                </div>
                <div className="flex justify-between py-1.5">
                  <span className="text-slate-600">Challan Layout Format:</span>
                  <span className="font-bold text-slate-800">3-Copy (Bank, School, Parent) A4 Landscape</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: RBAC & Permissions */}
      {activeTab === 'rbac' && (
        <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <div className="flex items-center gap-2">
              <Key className="w-5 h-5 text-brand-700" />
              <h3 className="text-base font-bold text-slate-900">Role-Based Access Control (RBAC) Matrix</h3>
            </div>
            <span className="text-xs text-slate-500 font-medium">8 Defined Roles with Direct Permission Mapping</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-xs text-left">
              <thead className="bg-slate-50 text-slate-700 uppercase font-semibold text-[11px] border-b border-slate-200">
                <tr>
                  <th className="px-4 py-3">Role Code</th>
                  <th className="px-4 py-3">Display Name</th>
                  <th className="px-4 py-3">Core Scope</th>
                  <th className="px-4 py-3">Primary Permissions Granted</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_SUPER_ADMIN</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Super Administrator</td>
                  <td className="px-4 py-3 text-slate-600">Full system & database oversight</td>
                  <td className="px-4 py-3"><span className="px-2 py-0.5 rounded-full bg-purple-100 text-purple-800 font-medium">ALL_PERMISSIONS</span></td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_ADMIN</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">School Administrator</td>
                  <td className="px-4 py-3 text-slate-600">Operational administration & users</td>
                  <td className="px-4 py-3 text-slate-700">USER_MANAGE, STUDENT_WRITE, FEE_MANAGE</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_PRINCIPAL</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Principal (Huma Arif)</td>
                  <td className="px-4 py-3 text-slate-600">Academic approvals & report signoff</td>
                  <td className="px-4 py-3 text-slate-700">ACADEMIC_APPROVE, REPORT_SIGN, EXAM_MANAGE</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_VICE_PRINCIPAL</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Vice Principal (Hana Khan)</td>
                  <td className="px-4 py-3 text-slate-600">Campus discipline & daily operations</td>
                  <td className="px-4 py-3 text-slate-700">ATTENDANCE_OVERSEE, TIMETABLE_MANAGE, DISCIPLINE</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_TEACHER</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Teaching Faculty</td>
                  <td className="px-4 py-3 text-slate-600">Classroom attendance & gradebook</td>
                  <td className="px-4 py-3 text-slate-700">ATTENDANCE_RECORD, GRADE_SUBMIT, LMS_CREATE</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_STUDENT</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Enrolled Student</td>
                  <td className="px-4 py-3 text-slate-600">Self syllabus, homework & progress</td>
                  <td className="px-4 py-3 text-slate-700">LMS_VIEW, ASSIGNMENT_SUBMIT, REPORT_VIEW</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_PARENT</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Guardian / Parent</td>
                  <td className="px-4 py-3 text-slate-600">Children's attendance, fees & card</td>
                  <td className="px-4 py-3 text-slate-700">CHILD_VIEW, FEE_PAY, ATTENDANCE_VIEW</td>
                </tr>
                <tr className="hover:bg-slate-50/60 transition-colors">
                  <td className="px-4 py-3 font-mono font-bold text-brand-800">ROLE_ACCOUNTANT</td>
                  <td className="px-4 py-3 font-semibold text-slate-800">Finance & Accounts</td>
                  <td className="px-4 py-3 text-slate-600">Bank deposits & fee collection</td>
                  <td className="px-4 py-3 text-slate-700">FEE_COLLECT, CHALLAN_GENERATE, LEDGER_EXPORT</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 4: System & Security */}
      {activeTab === 'system' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <Clock className="w-5 h-5 text-brand-700" />
              Regional & Environment Parameters
            </h3>

            <div className="space-y-3 text-xs">
              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                  <span className="text-slate-500 block text-[11px]">System Currency</span>
                  <span className="font-bold text-slate-900 text-xs mt-0.5 block">PKR (Rs.)</span>
                  <span className="text-[10px] text-slate-500">Pakistani Rupee</span>
                </div>
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                  <span className="text-slate-500 block text-[11px]">System Timezone</span>
                  <span className="font-bold text-slate-900 text-xs mt-0.5 block">Asia/Karachi</span>
                  <span className="text-[10px] text-slate-500">UTC+05:00</span>
                </div>
              </div>

              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-slate-500 block text-[11px]">Academic Year & Term</span>
                <span className="font-bold text-slate-900 text-xs mt-0.5 block">Session 2026-2027 (Term 1)</span>
                <span className="text-[10px] text-slate-500">Started August 2026 • Midterm Nov 2026</span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h3 className="text-base font-bold text-slate-900 flex items-center gap-2 border-b border-slate-100 pb-3">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              Database Engine & Disaster Recovery
            </h3>

            <div className="space-y-3 text-xs">
              <div className="p-3.5 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-900 space-y-1">
                <span className="font-bold block">ACID-Compliant Relational Database</span>
                <p className="text-[11px] leading-relaxed">
                  MySQL 8.x InnoDB Engine with UTF8MB4 charset support, foreign key cascade constraints, and row-level locking.
                </p>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-700 space-y-1">
                <span className="font-bold block">Automated Daily Backups</span>
                <p className="text-[11px] leading-relaxed">
                  Scheduled daily 02:00 AM PKT snapshot with SHA256 integrity verification. Verified 100% table and row parity.
                </p>
              </div>

              <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-700 space-y-1">
                <span className="font-bold block">Data Protection & Privacy</span>
                <p className="text-[11px] leading-relaxed">
                  Passwords hashed with BCrypt (12 rounds). JWT tokens signed with HMAC-SHA512.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SettingsPage;
