import React, { useState, useEffect } from 'react';
import { 
  Award, 
  FileCheck, 
  Printer, 
  ShieldCheck, 
  Plus, 
  CheckCircle, 
  X, 
  RefreshCw,
  QrCode,
  ShieldAlert
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

interface CertificateTemplate {
  id: number;
  title: string;
  certificateType: string;
  headerText: string;
  bodyTemplate: string;
  defaultSignatoryName: string;
  defaultSignatoryTitle: string;
  borderStyle: string;
  active: boolean;
}

interface IssuedCertificate {
  id: number;
  certificateNumber: string;
  verificationCode: string;
  templateId: number;
  studentId: number;
  certificateType: string;
  title: string;
  recipientName: string;
  admissionNumber: string;
  className: string;
  sectionName: string;
  academicSession: string;
  narrative: string;
  issueDate: string;
  issuingAuthorityName: string;
  issuingAuthorityTitle: string;
  status: string;
  borderStyle: string;
  verificationUrl: string;
}

export const CertificatesPage: React.FC = () => {
  const { user } = useAuth();
  const roles = user?.roles || [];
  const isStaff = roles.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER'].includes(r));
  const canIssue = roles.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL'].includes(r));

  const [certificates, setCertificates] = useState<IssuedCertificate[]>([]);
  const [templates, setTemplates] = useState<CertificateTemplate[]>([]);
  const [students, setStudents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'issued' | 'templates' | 'verify'>('issued');
  
  // Issue Modal State
  const [showIssueModal, setShowIssueModal] = useState(false);
  const [issueForm, setIssueForm] = useState({
    studentId: '',
    templateId: '',
    certificateType: 'MERIT',
    customTitle: '',
    customNarrative: '',
    issueDate: new Date().toISOString().split('T')[0]
  });
  const [issuing, setIssuing] = useState(false);

  // View / Print Certificate Modal
  const [viewingCert, setViewingCert] = useState<IssuedCertificate | null>(null);

  // Verification Search State
  const [verifyCodeInput, setVerifyCodeInput] = useState('');
  const [verifying, setVerifying] = useState(false);
  const [verifyResult, setVerifyResult] = useState<any>(null);

  const fetchCertificates = () => {
    setLoading(true);
    apiClient.get(isStaff ? '/certificates' : '/certificates')
      .then(res => {
        if (res.data?.data) {
          setCertificates(res.data.data);
        }
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const fetchTemplates = () => {
    apiClient.get('/certificates/templates')
      .then(res => {
        if (res.data?.data) {
          setTemplates(res.data.data);
          if (res.data.data.length > 0 && !issueForm.templateId) {
            setIssueForm(prev => ({
              ...prev,
              templateId: res.data.data[0].id.toString(),
              certificateType: res.data.data[0].certificateType
            }));
          }
        }
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    fetchCertificates();
    fetchTemplates();

    if (canIssue) {
      apiClient.get('/students')
        .then(res => {
          const list = Array.isArray(res.data?.data) ? res.data.data : (res.data?.data?.content || []);
          setStudents(list);
        })
        .catch(err => console.error(err));
    }
  }, [canIssue]);

  const handleIssueSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!issueForm.studentId || !issueForm.templateId) return;

    setIssuing(true);
    try {
      const res = await apiClient.post('/certificates/issue', {
        studentId: Number(issueForm.studentId),
        templateId: Number(issueForm.templateId),
        certificateType: issueForm.certificateType,
        customTitle: issueForm.customTitle || undefined,
        customNarrative: issueForm.customNarrative || undefined,
        issueDate: issueForm.issueDate
      });

      if (res.data?.data) {
        setShowIssueModal(false);
        fetchCertificates();
        setViewingCert(res.data.data);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to issue digital certificate.');
    } finally {
      setIssuing(false);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!verifyCodeInput.trim()) return;

    setVerifying(true);
    setVerifyResult(null);
    try {
      const res = await apiClient.get(`/certificates/verify/${verifyCodeInput.trim()}`);
      setVerifyResult(res.data?.data || null);
    } catch (err: any) {
      setVerifyResult({
        authentic: false,
        message: 'Certificate verification failed or invalid reference code.'
      });
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4 print:hidden">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-amber-700 uppercase tracking-widest mb-1">
            <Award className="w-4 h-4 text-amber-500" />
            <span>Digital Credentialing & Honors</span>
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Digital Certificates & Honors</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Authoritative, tamper-evident digital certificates with cryptographic verification and high-resolution print generation.
          </p>
        </div>

        <div className="flex items-center gap-2 self-start md:self-auto">
          {canIssue && (
            <button
              onClick={() => setShowIssueModal(true)}
              className="flex items-center gap-2 px-4 py-2.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-all shadow-md"
            >
              <Plus className="w-4 h-4" />
              <span>Issue New Certificate</span>
            </button>
          )}
        </div>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-200 print:hidden">
        <button
          onClick={() => setActiveTab('issued')}
          className={`flex items-center gap-2 px-4 py-2.5 text-xs font-bold border-b-2 transition-all ${
            activeTab === 'issued'
              ? 'border-brand-700 text-brand-900'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <FileCheck className="w-4 h-4" />
          <span>Issued Certificates ({certificates.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('templates')}
          className={`flex items-center gap-2 px-4 py-2.5 text-xs font-bold border-b-2 transition-all ${
            activeTab === 'templates'
              ? 'border-brand-700 text-brand-900'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <Award className="w-4 h-4" />
          <span>Certificate Templates ({templates.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('verify')}
          className={`flex items-center gap-2 px-4 py-2.5 text-xs font-bold border-b-2 transition-all ${
            activeTab === 'verify'
              ? 'border-brand-700 text-brand-900'
              : 'border-transparent text-slate-500 hover:text-slate-800'
          }`}
        >
          <ShieldCheck className="w-4 h-4 text-emerald-600" />
          <span>Online Public Verification</span>
        </button>
      </div>

      {/* TAB 1: ISSUED CERTIFICATES */}
      {activeTab === 'issued' && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-200 bg-slate-50/50 flex items-center justify-between">
            <span className="font-bold text-xs uppercase tracking-wider text-slate-600">
              Official Award Registry
            </span>
            <button
              onClick={fetchCertificates}
              className="text-xs text-brand-700 hover:underline flex items-center gap-1 font-semibold"
            >
              <RefreshCw className="w-3 h-3" />
              <span>Refresh Registry</span>
            </button>
          </div>

          {loading ? (
            <div className="py-20 flex flex-col items-center justify-center">
              <RefreshCw className="w-7 h-7 animate-spin text-brand-600 mb-2" />
              <p className="text-xs text-slate-500 font-semibold">Loading certificates...</p>
            </div>
          ) : certificates.length === 0 ? (
            <div className="py-16 text-center text-slate-400">
              <Award className="w-12 h-12 mx-auto text-slate-300 mb-2" />
              <p className="font-bold text-slate-600">No Certificates Issued Yet</p>
              <p className="text-xs text-slate-400 mt-1">Certificates issued to students will appear in this verified ledger.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-100 text-slate-600 font-bold uppercase tracking-wider border-b border-slate-200">
                    <th className="p-3.5">Certificate No.</th>
                    <th className="p-3.5">Recipient Student</th>
                    <th className="p-3.5">Honors Title</th>
                    <th className="p-3.5">Category</th>
                    <th className="p-3.5">Issue Date</th>
                    <th className="p-3.5">Status</th>
                    <th className="p-3.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {certificates.map((c) => (
                    <tr key={c.id} className="hover:bg-slate-50">
                      <td className="p-3.5 font-mono font-bold text-slate-900">{c.certificateNumber}</td>
                      <td className="p-3.5">
                        <div className="font-bold text-slate-900">{c.recipientName}</div>
                        <div className="text-[11px] text-slate-500">{c.admissionNumber} • {c.className}</div>
                      </td>
                      <td className="p-3.5 font-medium text-slate-800">{c.title}</td>
                      <td className="p-3.5">
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-black uppercase bg-amber-50 text-amber-900 border border-amber-200">
                          {c.certificateType}
                        </span>
                      </td>
                      <td className="p-3.5 text-slate-600">{c.issueDate}</td>
                      <td className="p-3.5">
                        <span className={`px-2 py-0.5 rounded-full text-[10px] font-black uppercase ${
                          c.status === 'ISSUED' ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                        }`}>
                          {c.status}
                        </span>
                      </td>
                      <td className="p-3.5 text-right space-x-2">
                        <button
                          onClick={() => setViewingCert(c)}
                          className="px-3 py-1 bg-brand-50 hover:bg-brand-100 text-brand-900 font-bold rounded-lg transition-colors"
                        >
                          View & Print
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: CERTIFICATE TEMPLATES */}
      {activeTab === 'templates' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {templates.map((tpl) => (
            <div key={tpl.id} className="bg-white rounded-2xl border border-slate-200 shadow-sm p-5 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className="px-2.5 py-1 bg-brand-50 border border-brand-200 text-brand-900 rounded-full text-[10px] font-black uppercase">
                    {tpl.certificateType}
                  </span>
                  <span className="text-[11px] font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded">
                    Active Template
                  </span>
                </div>
                <h3 className="font-bold text-base text-slate-900 mb-2">{tpl.title}</h3>
                <p className="text-xs text-slate-600 line-clamp-3 bg-slate-50 p-2.5 rounded-xl border border-slate-100 italic">
                  "{tpl.bodyTemplate}"
                </p>
              </div>

              <div className="pt-4 border-t border-slate-100 mt-4 text-[11px] text-slate-500">
                <div>Signatory: <strong className="text-slate-800">{tpl.defaultSignatoryName}</strong> ({tpl.defaultSignatoryTitle})</div>
                <div className="mt-0.5">Border Style: <strong className="text-slate-800">{tpl.borderStyle}</strong></div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* TAB 3: ONLINE PUBLIC VERIFICATION */}
      {activeTab === 'verify' && (
        <div className="max-w-xl mx-auto bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <div className="text-center mb-6">
            <ShieldCheck className="w-12 h-12 text-emerald-600 mx-auto mb-2" />
            <h3 className="font-black text-lg text-slate-900">Certificate Verification Portal</h3>
            <p className="text-xs text-slate-500 max-w-md mx-auto mt-1">
              Validate the authenticity of any Bluebells Schooling System award or certificate by entering its unique reference code.
            </p>
          </div>

          <form onSubmit={handleVerify} className="space-y-4">
            <div className="flex gap-2">
              <input
                type="text"
                placeholder="e.g. VRF-XXXX-BSS or BSS-CERT-..."
                value={verifyCodeInput}
                onChange={(e) => setVerifyCodeInput(e.target.value)}
                className="flex-1 border border-slate-300 rounded-xl px-4 py-2.5 text-xs font-mono uppercase focus:ring-2 focus:ring-brand-500"
                required
              />
              <button
                type="submit"
                disabled={verifying}
                className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl text-xs transition-colors shadow-sm disabled:opacity-50"
              >
                {verifying ? 'Verifying...' : 'Verify Now'}
              </button>
            </div>
          </form>

          {verifyResult && (
            <div className={`mt-6 p-5 rounded-2xl border text-xs ${
              verifyResult.authentic 
                ? 'bg-emerald-50/70 border-emerald-300 text-emerald-950' 
                : 'bg-red-50 border-red-300 text-red-950'
            }`}>
              <div className="flex items-center gap-2 font-bold mb-3">
                {verifyResult.authentic ? (
                  <>
                    <CheckCircle className="w-5 h-5 text-emerald-600" />
                    <span className="text-sm font-black text-emerald-900">Official Authenticity Verified</span>
                  </>
                ) : (
                  <>
                    <ShieldAlert className="w-5 h-5 text-red-600" />
                    <span className="text-sm font-black text-red-900">Verification Failed</span>
                  </>
                )}
              </div>

              {verifyResult.authentic ? (
                <div className="space-y-1.5 font-medium">
                  <div><strong>Awardee:</strong> {verifyResult.recipientName} ({verifyResult.admissionNumber})</div>
                  <div><strong>Honors Title:</strong> {verifyResult.title}</div>
                  <div><strong>Class & Session:</strong> {verifyResult.className} • {verifyResult.academicSession}</div>
                  <div><strong>Issue Date:</strong> {verifyResult.issueDate}</div>
                  <div><strong>Issuing Authority:</strong> {verifyResult.issuingAuthorityName} ({verifyResult.issuingAuthorityTitle})</div>
                  <div><strong>Status:</strong> <span className="font-bold text-emerald-700 uppercase">{verifyResult.status}</span></div>
                </div>
              ) : (
                <p>{verifyResult.message}</p>
              )}
            </div>
          )}
        </div>
      )}

      {/* ISSUE CERTIFICATE MODAL */}
      {showIssueModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-lg max-h-[90vh] overflow-y-auto">
            <div className="p-5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
              <div className="flex items-center gap-2">
                <Award className="w-5 h-5 text-amber-500" />
                <h3 className="font-bold text-sm text-slate-900">Issue Digital Certificate</h3>
              </div>
              <button onClick={() => setShowIssueModal(false)} className="text-slate-400 hover:text-slate-700">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleIssueSubmit} className="p-5 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Recipient Student *</label>
                <select
                  value={issueForm.studentId}
                  onChange={(e) => setIssueForm({ ...issueForm, studentId: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                >
                  <option value="">Select Student...</option>
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.fullName || `${s.firstName} ${s.lastName}`} ({s.admissionNumber})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Certificate Template *</label>
                <select
                  value={issueForm.templateId}
                  onChange={(e) => {
                    const sel = templates.find(t => t.id.toString() === e.target.value);
                    setIssueForm({
                      ...issueForm,
                      templateId: e.target.value,
                      certificateType: sel?.certificateType || 'MERIT'
                    });
                  }}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                >
                  {templates.map((t) => (
                    <option key={t.id} value={t.id}>
                      {t.title} ({t.certificateType})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Custom Honors Title (Optional)</label>
                <input
                  type="text"
                  placeholder="e.g. Distinction in Annual Science Olympiad"
                  value={issueForm.customTitle}
                  onChange={(e) => setIssueForm({ ...issueForm, customTitle: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Custom Narrative (Optional - Overrides Template Body)</label>
                <textarea
                  rows={3}
                  placeholder="Leave empty to automatically generate from template using student name, class, and session..."
                  value={issueForm.customNarrative}
                  onChange={(e) => setIssueForm({ ...issueForm, customNarrative: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Date of Issuance *</label>
                <input
                  type="date"
                  value={issueForm.issueDate}
                  onChange={(e) => setIssueForm({ ...issueForm, issueDate: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                />
              </div>

              <div className="pt-3 border-t border-slate-200 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowIssueModal(false)}
                  className="px-4 py-2 border border-slate-300 rounded-xl text-slate-700 font-bold hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={issuing}
                  className="px-5 py-2 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl font-bold shadow-md disabled:opacity-50"
                >
                  {issuing ? 'Issuing...' : 'Issue & Verify Certificate'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* VIEW & PRINT ORNATE CERTIFICATE MODAL */}
      {viewingCert && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 max-w-4xl w-full max-h-[95vh] overflow-y-auto">
            {/* Modal Controls */}
            <div className="p-4 border-b border-slate-200 flex items-center justify-between bg-slate-50 print:hidden">
              <span className="font-bold text-xs text-slate-700 font-mono">
                {viewingCert.certificateNumber} • {viewingCert.verificationCode}
              </span>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => window.print()}
                  className="flex items-center gap-1.5 px-3 py-1.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-lg text-xs font-bold transition-colors shadow-sm"
                >
                  <Printer className="w-3.5 h-3.5" />
                  <span>Print Certificate</span>
                </button>
                <button
                  onClick={() => setViewingCert(null)}
                  className="p-1.5 rounded-lg hover:bg-slate-200 text-slate-500"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* High-Fidelity Printable Certificate Canvas */}
            <div className="p-8 sm:p-12 flex items-center justify-center bg-amber-50/20">
              <div className="w-full max-w-3xl bg-white p-8 sm:p-12 border-8 border-double border-amber-600/80 rounded-xl shadow-lg relative text-center font-serif text-slate-800">
                {/* Ornate Gold Inner Border */}
                <div className="absolute inset-2 border-2 border-amber-400/50 pointer-events-none rounded-lg"></div>

                {/* Crest & Header */}
                <div className="mb-6">
                  <div className="w-16 h-16 rounded-full bg-[#123B70] border-2 border-amber-400 flex items-center justify-center text-amber-300 font-sans font-black text-xl shadow-md mx-auto mb-2">
                    BSS
                  </div>
                  <h2 className="text-xl sm:text-2xl font-black text-[#123B70] tracking-widest uppercase font-sans">
                    Bluebells Schooling System
                  </h2>
                  <p className="text-xs uppercase tracking-widest text-amber-700 font-sans font-bold">
                    Autobahn Road, Unit 2, Latifabad, Hyderabad, Sindh
                  </p>
                </div>

                {/* Certificate Title */}
                <div className="my-6">
                  <h1 className="text-2xl sm:text-3xl font-black tracking-wide text-slate-900 border-b-2 border-amber-400 pb-2 inline-block">
                    {viewingCert.title}
                  </h1>
                </div>

                {/* Award Presentation Statement */}
                <p className="text-sm italic text-slate-600 mb-3">This honor is proudly conferred upon</p>

                {/* Student Name */}
                <div className="text-2xl sm:text-4xl font-black text-[#123B70] font-sans my-4 tracking-tight border-b border-dotted border-slate-300 pb-2 max-w-md mx-auto">
                  {viewingCert.recipientName}
                </div>

                <p className="text-xs text-slate-500 mb-6 font-sans">
                  Admission No: <strong className="font-mono text-slate-800">{viewingCert.admissionNumber}</strong> • Class: <strong className="text-slate-800">{viewingCert.className}</strong> ({viewingCert.academicSession})
                </p>

                {/* Narrative Text */}
                <p className="text-sm sm:text-base leading-relaxed text-slate-700 max-w-xl mx-auto italic mb-10">
                  "{viewingCert.narrative}"
                </p>

                {/* Signatories & Verification Seal */}
                <div className="grid grid-cols-3 items-end pt-8 border-t border-slate-200 text-xs font-sans">
                  {/* Signatory 1 */}
                  <div className="text-center">
                    <div className="font-serif italic text-slate-800 text-sm font-bold border-b border-slate-400 pb-1 mx-4">
                      Huma Arif
                    </div>
                    <p className="text-[10px] text-slate-500 uppercase tracking-wider font-bold mt-1">Academic Principal</p>
                  </div>

                  {/* Verification QR / Seal */}
                  <div className="flex flex-col items-center">
                    <div className="w-16 h-16 bg-slate-900 text-white rounded-lg flex items-center justify-center p-1.5 shadow">
                      <QrCode className="w-12 h-12 text-white" />
                    </div>
                    <span className="font-mono text-[9px] text-slate-500 mt-1">{viewingCert.verificationCode}</span>
                  </div>

                  {/* Signatory 2 */}
                  <div className="text-center">
                    <div className="font-serif italic text-slate-800 text-sm font-bold border-b border-slate-400 pb-1 mx-4">
                      Hana Khan
                    </div>
                    <p className="text-[10px] text-slate-500 uppercase tracking-wider font-bold mt-1">Vice Principal</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
