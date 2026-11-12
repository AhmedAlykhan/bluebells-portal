import React, { useState, useEffect } from 'react';
import { 
  Contact, 
  QrCode, 
  Printer, 
  RefreshCw, 
  ShieldCheck, 
  Plus, 
  User, 
  Phone, 
  MapPin, 
  CheckCircle,
  RotateCcw
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

interface StudentIdCardDto {
  id: number;
  studentId: number;
  admissionNumber: string;
  studentName: string;
  fatherName: string;
  className: string;
  sectionName: string;
  rollNumber: string;
  academicSession: string;
  bloodGroup: string;
  emergencyContact: string;
  cardNumber: string;
  issueDate: string;
  expiryDate: string;
  status: string;
  reissueCount: number;
  qrVerificationToken: string;
  qrPayload: string;
  schoolAddress: string;
  schoolPhone: string;
  principalName: string;
}

export const StudentIdCardsPage: React.FC = () => {
  const { user } = useAuth();
  const roles = user?.roles || [];
  const isStaff = roles.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER'].includes(r));
  const isParent = roles.includes('ROLE_PARENT');
  const isStudent = roles.includes('ROLE_STUDENT');

  const [selectedCard, setSelectedCard] = useState<StudentIdCardDto | null>(null);
  const [students, setStudents] = useState<any[]>([]);
  const [selectedStudentId, setSelectedStudentId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showReissueModal, setShowReissueModal] = useState(false);
  const [reissueReason, setReissueReason] = useState('LOST');
  const [activeSide, setActiveSide] = useState<'front' | 'back'>('front');

  useEffect(() => {
    setLoading(true);
    if (isStaff) {
      apiClient.get('/students')
        .then(res => {
          const list = Array.isArray(res.data?.data) ? res.data.data : (res.data?.data?.content || []);
          setStudents(list);
          if (list.length > 0) {
            setSelectedStudentId(list[0].id);
          } else {
            setLoading(false);
          }
        })
        .catch(err => {
          console.error(err);
          setLoading(false);
        });
    } else if (isParent) {
      apiClient.get('/guardians/my-children')
        .then(res => {
          const list = Array.isArray(res.data?.data) ? res.data.data : [];
          setStudents(list);
          if (list.length > 0) {
            setSelectedStudentId(list[0].id);
          } else {
            setLoading(false);
          }
        })
        .catch(err => {
          console.error(err);
          setLoading(false);
        });
    } else if (isStudent) {
      apiClient.get('/students/me')
        .then(res => {
          const st = res.data?.data;
          if (st?.id) {
            setStudents([st]);
            setSelectedStudentId(st.id);
          } else {
            setLoading(false);
          }
        })
        .catch(err => {
          console.error(err);
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, [isStaff, isParent, isStudent]);

  const fetchCard = () => {
    if (!selectedStudentId) {
      setLoading(false);
      return;
    }
    setLoading(true);
    apiClient.get(`/id-cards/students/${selectedStudentId}`)
      .then(res => {
        if (res.data?.data) {
          setSelectedCard(res.data.data);
        } else {
          setSelectedCard(null);
        }
      })
      .catch(err => {
        console.warn('No active card or fetch failed:', err);
        setSelectedCard(null);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (selectedStudentId) {
      fetchCard();
    }
  }, [selectedStudentId]);

  const handleGenerateCard = async () => {
    if (!selectedStudentId) return;
    setGenerating(true);
    try {
      const res = await apiClient.post('/id-cards/generate', {
        studentId: selectedStudentId
      });
      if (res.data?.data) {
        setSelectedCard(res.data.data);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to generate student ID card.');
    } finally {
      setGenerating(false);
    }
  };

  const handleReissueCard = async () => {
    if (!selectedCard) return;
    setGenerating(true);
    try {
      const res = await apiClient.post(`/id-cards/${selectedCard.id}/reissue`, {
        reason: reissueReason
      });
      if (res.data?.data) {
        setSelectedCard(res.data.data);
        setShowReissueModal(false);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to reissue student ID card.');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4 print:hidden">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-brand-700 uppercase tracking-widest mb-1">
            <Contact className="w-4 h-4" />
            <span>Identity & Credential Management</span>
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Digital & Printable Student ID Cards</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Tamper-resistant digital ID badges featuring verified student credentials and safe cryptographic QR verification.
          </p>
        </div>

        <div className="flex items-center gap-2 self-start md:self-auto">
          {selectedCard && (
            <button
              onClick={() => window.print()}
              className="flex items-center gap-2 px-4 py-2.5 bg-slate-900 hover:bg-slate-800 text-white rounded-xl text-xs font-bold transition-colors shadow-sm"
            >
              <Printer className="w-4 h-4" />
              <span>Print Official Badge</span>
            </button>
          )}

          {isStaff && selectedCard && (
            <button
              onClick={() => setShowReissueModal(true)}
              className="flex items-center gap-1.5 px-3 py-2.5 bg-amber-50 hover:bg-amber-100 text-amber-900 border border-amber-300 rounded-xl text-xs font-bold transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5 text-amber-700" />
              <span>Reissue Card</span>
            </button>
          )}
        </div>
      </div>

      {/* Selector Toolbar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4 print:hidden">
        <div className="flex items-center gap-3">
          <span className="text-xs font-bold text-slate-600">Select Student:</span>
          {students.length > 0 ? (
            <select
              value={selectedStudentId || ''}
              onChange={(e) => setSelectedStudentId(Number(e.target.value))}
              className="text-xs font-bold bg-slate-50 border border-slate-300 rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-brand-500 text-slate-800"
            >
              {students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.fullName || `${s.firstName || ''} ${s.lastName || ''}`.trim() || `Student #${s.id}`} {s.admissionNumber ? `(${s.admissionNumber})` : ''}
                </option>
              ))}
            </select>
          ) : (
            <span className="text-xs font-bold text-brand-900 bg-brand-50 px-3 py-1 rounded-md">
              Student ID #{selectedStudentId || 'Active Profile'}
            </span>
          )}
        </div>

        <div className="flex items-center gap-3">
          {selectedCard && (
            <div className="flex items-center bg-slate-100 p-1 rounded-xl text-xs font-bold">
              <button
                onClick={() => setActiveSide('front')}
                className={`px-3 py-1 rounded-lg transition-colors ${activeSide === 'front' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}
              >
                Front Side
              </button>
              <button
                onClick={() => setActiveSide('back')}
                className={`px-3 py-1 rounded-lg transition-colors ${activeSide === 'back' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500'}`}
              >
                Back (QR & Verification)
              </button>
            </div>
          )}

          {isStaff && !selectedCard && !loading && (
            <button
              onClick={handleGenerateCard}
              disabled={generating}
              className="flex items-center gap-1.5 px-4 py-2 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-all shadow-md disabled:opacity-50"
            >
              {generating ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Plus className="w-3.5 h-3.5" />}
              <span>Generate Official ID Card</span>
            </button>
          )}
        </div>
      </div>

      {/* ID Card Display Area */}
      {loading ? (
        <div className="py-24 flex flex-col items-center justify-center bg-white rounded-2xl border border-slate-200">
          <RefreshCw className="w-8 h-8 animate-spin text-brand-600 mb-3" />
          <p className="text-sm font-semibold text-slate-600">Retrieving digital ID badge...</p>
        </div>
      ) : !selectedCard ? (
        <div className="py-20 text-center bg-white rounded-2xl border border-slate-200 p-8">
          <Contact className="w-14 h-14 text-slate-300 mx-auto mb-3" />
          <h3 className="font-bold text-slate-800 text-lg">No Active ID Card Generated</h3>
          <p className="text-xs text-slate-500 max-w-md mx-auto mt-1 mb-6">
            An official student identity badge has not yet been minted for this student for the current academic session.
          </p>
          {isStaff && (
            <button
              onClick={handleGenerateCard}
              disabled={generating}
              className="inline-flex items-center gap-2 px-5 py-2.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-all shadow-md disabled:opacity-50"
            >
              {generating ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Plus className="w-4 h-4" />}
              <span>Generate ID Badge Now</span>
            </button>
          )}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center py-6">
          {/* Card Frame (CR80 Standard Ratio approx 85.6mm x 53.98mm) */}
          <div className="w-[360px] sm:w-[400px] h-[550px] bg-white rounded-2xl shadow-2xl border-2 border-slate-300 overflow-hidden relative flex flex-col justify-between font-sans print:shadow-none print:border print:w-[320px] print:h-[480px]">
            
            {activeSide === 'front' ? (
              /* FRONT OF CARD */
              <div className="flex flex-col justify-between h-full bg-gradient-to-b from-white via-slate-50 to-slate-100">
                {/* Header Branding */}
                <div className="bg-[#123B70] text-white p-4 text-center border-b-4 border-amber-400 relative">
                  <div className="flex items-center justify-center gap-2.5 mb-1">
                    <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center text-[#123B70] font-black text-sm shadow">
                      BSS
                    </div>
                    <div className="text-left">
                      <h2 className="text-xs font-black tracking-widest uppercase">Bluebells Schooling System</h2>
                      <p className="text-[9px] tracking-wider text-amber-300 font-semibold uppercase">Excellence in Education</p>
                    </div>
                  </div>
                  <div className="text-[10px] text-brand-100 tracking-wider uppercase font-bold mt-1">
                    Student Identity Card • {selectedCard.academicSession}
                  </div>
                </div>

                {/* Photo & Identity Core */}
                <div className="flex flex-col items-center px-6 py-2">
                  <div className="relative mb-3">
                    <div className="w-24 h-24 rounded-full border-4 border-amber-400 overflow-hidden bg-slate-200 shadow-md flex items-center justify-center text-slate-400">
                      <User className="w-14 h-14" />
                    </div>
                    <span className="absolute bottom-0 right-0 w-6 h-6 bg-emerald-500 rounded-full border-2 border-white flex items-center justify-center text-white" title="Active">
                      <CheckCircle className="w-3.5 h-3.5" />
                    </span>
                  </div>

                  <h3 className="text-lg font-black text-slate-900 tracking-tight text-center leading-tight">
                    {selectedCard.studentName}
                  </h3>
                  <p className="text-xs font-bold text-slate-500 mt-0.5">
                    S/O, D/O: <span className="text-slate-800">{selectedCard.fatherName}</span>
                  </p>

                  <div className="mt-3 px-3 py-1 bg-brand-50 border border-brand-200 rounded-full text-brand-900 text-xs font-black">
                    {selectedCard.className} • Section {selectedCard.sectionName}
                  </div>
                </div>

                {/* Details Table */}
                <div className="px-6 space-y-1.5 text-xs">
                  <div className="flex justify-between border-b border-slate-200 py-1">
                    <span className="text-slate-500 font-semibold">Admission No:</span>
                    <span className="font-bold text-slate-900 font-mono">{selectedCard.admissionNumber}</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 py-1">
                    <span className="text-slate-500 font-semibold">Roll Number:</span>
                    <span className="font-bold text-slate-900">{selectedCard.rollNumber || 'N/A'}</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 py-1">
                    <span className="text-slate-500 font-semibold">Blood Group:</span>
                    <span className="font-bold text-red-600 bg-red-50 px-2 py-0.5 rounded text-[11px]">{selectedCard.bloodGroup}</span>
                  </div>
                  <div className="flex justify-between border-b border-slate-200 py-1">
                    <span className="text-slate-500 font-semibold">Emergency Contact:</span>
                    <span className="font-bold text-slate-900 font-mono text-[11px]">{selectedCard.emergencyContact}</span>
                  </div>
                  <div className="flex justify-between py-1">
                    <span className="text-slate-500 font-semibold">Valid Thru:</span>
                    <span className="font-bold text-slate-800">{selectedCard.expiryDate}</span>
                  </div>
                </div>

                {/* Card Footer / Signatory */}
                <div className="bg-slate-100 p-4 border-t border-slate-200 flex items-center justify-between text-center">
                  <div className="text-left text-[9px] text-slate-400 font-mono">
                    <div>Card: {selectedCard.cardNumber}</div>
                    {selectedCard.reissueCount > 0 && (
                      <div className="text-amber-700 font-bold">Replacement #{selectedCard.reissueCount}</div>
                    )}
                  </div>
                  <div className="text-right">
                    <div className="text-xs font-serif italic text-slate-800 font-bold border-b border-slate-400 pb-0.5">
                      {selectedCard.principalName}
                    </div>
                    <div className="text-[9px] text-slate-500 uppercase font-bold tracking-wider mt-0.5">
                      Academic Principal
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              /* BACK OF CARD */
              <div className="flex flex-col justify-between h-full bg-slate-50 p-6 text-center">
                {/* Back Header */}
                <div>
                  <h4 className="text-xs font-black uppercase text-slate-800 tracking-wider">
                    Official Institutional Card Terms
                  </h4>
                  <p className="text-[10px] text-slate-500 mt-1 leading-relaxed">
                    This card is the property of Bluebells Schooling System. If found, please return to the school administration office or contact campus dispatch.
                  </p>
                </div>

                {/* Safe QR Verification Box */}
                <div className="my-auto flex flex-col items-center">
                  <div className="bg-white p-3 rounded-2xl border-2 border-slate-300 shadow-md">
                    {/* Visual QR Code representation with zero sensitive PII */}
                    <div className="w-36 h-36 bg-slate-900 rounded-xl p-2 flex flex-col items-center justify-center text-white relative">
                      <QrCode className="w-28 h-28 text-white" />
                      <span className="text-[8px] tracking-widest uppercase font-mono text-amber-300 font-bold">
                        BSS VERIFIED ID
                      </span>
                    </div>
                  </div>
                  <div className="mt-2 text-[10px] text-slate-600 font-mono">
                    Token: {selectedCard.qrVerificationToken.substring(0, 22)}...
                  </div>
                  <div className="flex items-center gap-1 text-[10px] text-emerald-700 font-bold mt-1">
                    <ShieldCheck className="w-3.5 h-3.5 text-emerald-600" />
                    <span>Zero PII Exposure • Cryptographically Safe</span>
                  </div>
                </div>

                {/* School Address & Helpline */}
                <div className="border-t border-slate-200 pt-3 text-[10px] text-slate-500 space-y-1">
                  <div className="flex items-center justify-center gap-1">
                    <MapPin className="w-3 h-3 text-slate-400" />
                    <span>{selectedCard.schoolAddress}</span>
                  </div>
                  <div className="flex items-center justify-center gap-1 font-bold text-slate-700">
                    <Phone className="w-3 h-3 text-slate-400" />
                    <span>Campus Helpline: {selectedCard.schoolPhone}</span>
                  </div>
                  <div className="text-[9px] text-slate-400">
                    Verify Online: https://portal.bluebells.edu.pk/verify-id
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Reissue Modal */}
      {showReissueModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-md p-6">
            <div className="flex items-center gap-2 mb-2 text-amber-700 font-bold text-sm">
              <RotateCcw className="w-5 h-5 text-amber-500" />
              <h3>Reissue Student Identity Card</h3>
            </div>
            <p className="text-xs text-slate-500 mb-4">
              Reissuing this ID card will mark the original card as <strong>REISSUED</strong>, invalidate its former QR signature, and mint a new replacement badge with an audit sequence (-R{Number(selectedCard?.reissueCount || 0) + 1}).
            </p>

            <div className="space-y-3 mb-5">
              <label className="block text-xs font-bold text-slate-700">Audit Reissue Reason *</label>
              <select
                value={reissueReason}
                onChange={(e) => setReissueReason(e.target.value)}
                className="w-full text-xs border border-slate-300 rounded-xl px-3 py-2 focus:ring-2 focus:ring-brand-500"
              >
                <option value="LOST">Lost by Student</option>
                <option value="DAMAGED">Physically Damaged / Broken Lamination</option>
                <option value="PROMOTED">Promoted to New Class / Section</option>
                <option value="DETAILS_UPDATED">Updated Contact or Residential Details</option>
              </select>
            </div>

            <div className="flex items-center justify-end gap-2 text-xs">
              <button
                onClick={() => setShowReissueModal(false)}
                className="px-4 py-2 border border-slate-300 rounded-xl text-slate-700 font-bold hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                onClick={handleReissueCard}
                disabled={generating}
                className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white rounded-xl font-bold shadow-md disabled:opacity-50"
              >
                {generating ? 'Reissuing...' : 'Confirm Reissue'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
