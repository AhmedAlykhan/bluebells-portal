import React, { useState, useEffect } from 'react';
import { 
  HelpCircle, 
  MessageSquare, 
  Plus, 
  Send, 
  Lock, 
  X,
  ArrowUpRight,
  RefreshCw
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

interface SupportTicketMessage {
  id: number;
  senderName: string;
  senderRole: string;
  message: string;
  internalNote: boolean;
  attachmentUrl?: string;
  createdAt: string;
}

interface SupportTicket {
  id: number;
  ticketNumber: string;
  userId: number;
  userName: string;
  userRole: string;
  studentId?: number;
  studentName?: string;
  admissionNumber?: string;
  category: string;
  subject: string;
  description: string;
  priority: string;
  assignedDepartment: string;
  assignedToName?: string;
  status: string;
  resolutionNotes?: string;
  escalationLevel: number;
  createdAt: string;
  updatedAt: string;
  messages: SupportTicketMessage[];
}

export const SupportCentrePage: React.FC = () => {
  const { user } = useAuth();
  const roles = user?.roles || [];
  const isStaff = roles.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_ACCOUNTANT'].includes(r));

  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [selectedTicket, setSelectedTicket] = useState<SupportTicket | null>(null);
  const [loading, setLoading] = useState(true);
  const [filterCategory, setFilterCategory] = useState<string>('ALL');
  const [filterStatus, setFilterStatus] = useState<string>('ALL');

  // New Ticket Modal State
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createForm, setCreateForm] = useState({
    category: 'ACADEMIC',
    subject: '',
    description: '',
    priority: 'MEDIUM',
    studentId: ''
  });
  const [students, setStudents] = useState<any[]>([]);
  const [submitting, setSubmitting] = useState(false);

  // Reply State
  const [replyMessage, setReplyMessage] = useState('');
  const [isInternalNote, setIsInternalNote] = useState(false);
  const [replying, setReplying] = useState(false);

  // Status Update State
  const [resolutionNotes, setResolutionNotes] = useState('');
  const [showResolveModal, setShowResolveModal] = useState(false);

  const fetchTickets = () => {
    setLoading(true);
    const params: Record<string, string> = {};
    if (filterCategory !== 'ALL') params.category = filterCategory;
    if (filterStatus !== 'ALL') params.status = filterStatus;

    apiClient.get('/support/tickets', { params })
      .then(res => {
        if (res.data?.data) {
          setTickets(res.data.data);
          if (res.data.data.length > 0 && !selectedTicket) {
            setSelectedTicket(res.data.data[0]);
          }
        }
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  const fetchTicketDetails = (ticketId: number) => {
    apiClient.get(`/support/tickets/${ticketId}`)
      .then(res => {
        if (res.data?.data) {
          setSelectedTicket(res.data.data);
        }
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    fetchTickets();

    // Fetch linked students if parent or student
    if (user?.roles?.includes('ROLE_PARENT')) {
      apiClient.get('/guardians/my-children')
        .then(res => {
          const list = Array.isArray(res.data?.data) ? res.data.data : [];
          setStudents(list);
        })
        .catch(err => console.error(err));
    } else if (user?.roles?.includes('ROLE_STUDENT')) {
      apiClient.get('/students/me')
        .then(res => {
          if (res.data?.data) {
            setStudents([res.data.data]);
          }
        })
        .catch(err => console.error(err));
    }
  }, [filterCategory, filterStatus, user?.roles]);

  const handleCreateTicket = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!createForm.subject || !createForm.description) return;

    setSubmitting(true);
    try {
      const res = await apiClient.post('/support/tickets', {
        ...createForm,
        studentId: createForm.studentId ? Number(createForm.studentId) : undefined
      });

      if (res.data?.data) {
        setShowCreateModal(false);
        setCreateForm({
          category: 'ACADEMIC',
          subject: '',
          description: '',
          priority: 'MEDIUM',
          studentId: ''
        });
        fetchTickets();
        setSelectedTicket(res.data.data);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to submit support inquiry.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleSendReply = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!replyMessage.trim() || !selectedTicket) return;

    setReplying(true);
    try {
      await apiClient.post(`/support/tickets/${selectedTicket.id}/messages`, {
        message: replyMessage,
        internalNote: isInternalNote
      });
      setReplyMessage('');
      setIsInternalNote(false);
      fetchTicketDetails(selectedTicket.id);
      fetchTickets();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to send message.');
    } finally {
      setReplying(false);
    }
  };

  const handleUpdateStatus = async (newStatus: string) => {
    if (!selectedTicket) return;
    try {
      await apiClient.patch(`/support/tickets/${selectedTicket.id}/status`, {
        status: newStatus,
        resolutionNotes: resolutionNotes || undefined
      });
      setShowResolveModal(false);
      setResolutionNotes('');
      fetchTicketDetails(selectedTicket.id);
      fetchTickets();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to update ticket status.');
    }
  };

  const handleEscalate = async () => {
    if (!selectedTicket) return;
    const notes = prompt('Enter escalation rationale for executive leadership:');
    if (notes === null) return;

    try {
      await apiClient.post(`/support/tickets/${selectedTicket.id}/escalate?notes=${encodeURIComponent(notes)}`);
      fetchTicketDetails(selectedTicket.id);
      fetchTickets();
      alert('Ticket successfully escalated to Executive School Leadership.');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to escalate ticket.');
    }
  };

  const categories = [
    { id: 'ALL', label: 'All Inquiries' },
    { id: 'ACADEMIC', label: 'Academics & Curriculum' },
    { id: 'TEACHER', label: 'Faculty & Teacher Feedback' },
    { id: 'FEE', label: 'Fees & Accounts' },
    { id: 'TRANSPORT', label: 'School Transport' },
    { id: 'ADMINISTRATIVE', label: 'Administration & Admissions' },
    { id: 'TECHNICAL', label: 'Portal & Tech Support' },
    { id: 'SUGGESTION', label: 'School Improvement' }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-brand-700 uppercase tracking-widest mb-1">
            <HelpCircle className="w-4 h-4" />
            <span>Parent, Student & Faculty Helpdesk</span>
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Support & Complaint Centre</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Structured ticketing system ensuring accountable, confidential, and prompt inquiry resolution.
          </p>
        </div>

        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-all shadow-md self-start md:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Open Support Ticket</span>
        </button>
      </div>

      {/* Main Two-Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* LEFT COLUMN: TICKET LIST (4 COLS) */}
        <div className="lg:col-span-5 bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col h-[700px]">
          {/* Filters Bar */}
          <div className="p-4 border-b border-slate-200 bg-slate-50/50 space-y-2">
            <div className="flex items-center justify-between text-xs font-bold text-slate-700">
              <span>{isStaff ? 'Campus Ticket Queue' : 'My Inquiries'}</span>
              <button onClick={fetchTickets} className="text-brand-700 hover:underline flex items-center gap-1 font-semibold">
                <RefreshCw className="w-3 h-3" />
                <span>Refresh</span>
              </button>
            </div>

            <div className="flex items-center gap-2">
              <select
                value={filterCategory}
                onChange={(e) => setFilterCategory(e.target.value)}
                className="w-1/2 text-xs border border-slate-300 rounded-lg px-2.5 py-1.5 bg-white focus:ring-2 focus:ring-brand-500"
              >
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>{c.label}</option>
                ))}
              </select>

              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                className="w-1/2 text-xs border border-slate-300 rounded-lg px-2.5 py-1.5 bg-white focus:ring-2 focus:ring-brand-500"
              >
                <option value="ALL">All Statuses</option>
                <option value="OPEN">Open</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="AWAITING_RESPONSE">Awaiting Response</option>
                <option value="RESOLVED">Resolved</option>
                <option value="CLOSED">Closed</option>
              </select>
            </div>
          </div>

          {/* Ticket Items List */}
          <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
            {loading ? (
              <div className="py-20 flex flex-col items-center justify-center">
                <RefreshCw className="w-6 h-6 animate-spin text-brand-600 mb-2" />
                <p className="text-xs text-slate-500 font-semibold">Loading tickets...</p>
              </div>
            ) : tickets.length === 0 ? (
              <div className="py-20 text-center text-slate-400 p-4">
                <MessageSquare className="w-10 h-10 mx-auto text-slate-300 mb-2" />
                <p className="font-bold text-slate-600 text-xs">No Support Tickets Found</p>
                <p className="text-[11px] text-slate-400 mt-1">Open a new inquiry ticket for help or feedback.</p>
              </div>
            ) : (
              tickets.map((t) => {
                const isSelected = selectedTicket?.id === t.id;
                return (
                  <button
                    key={t.id}
                    onClick={() => {
                      setSelectedTicket(t);
                      fetchTicketDetails(t.id);
                    }}
                    className={`w-full text-left p-4 transition-colors flex flex-col gap-1.5 ${
                      isSelected ? 'bg-brand-50/60 border-l-4 border-[#123B70]' : 'hover:bg-slate-50'
                    }`}
                  >
                    <div className="flex items-center justify-between text-[11px]">
                      <span className="font-mono font-bold text-brand-900">{t.ticketNumber}</span>
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-black uppercase ${
                        t.status === 'OPEN' ? 'bg-blue-100 text-blue-800' :
                        t.status === 'IN_PROGRESS' ? 'bg-amber-100 text-amber-800' :
                        t.status === 'RESOLVED' ? 'bg-emerald-100 text-emerald-800' :
                        'bg-slate-100 text-slate-700'
                      }`}>
                        {t.status.replace(/_/g, ' ')}
                      </span>
                    </div>

                    <h4 className="font-bold text-xs text-slate-900 line-clamp-1">{t.subject}</h4>
                    <p className="text-[11px] text-slate-500 line-clamp-1">{t.description}</p>

                    <div className="flex items-center justify-between text-[10px] text-slate-400 mt-1">
                      <span>{t.userName} ({t.userRole.replace('ROLE_', '')})</span>
                      <span>{new Date(t.createdAt).toLocaleDateString()}</span>
                    </div>
                  </button>
                );
              })
            )}
          </div>
        </div>

        {/* RIGHT COLUMN: ACTIVE TICKET DETAILS & CONVERSATION (7 COLS) */}
        <div className="lg:col-span-7 bg-white rounded-2xl border border-slate-200 shadow-sm flex flex-col h-[700px] overflow-hidden">
          {selectedTicket ? (
            <>
              {/* Ticket Top Header */}
              <div className="p-5 border-b border-slate-200 bg-slate-50/50 flex flex-col gap-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-black text-sm text-[#123B70]">{selectedTicket.ticketNumber}</span>
                    <span className="text-xs bg-slate-200 text-slate-800 px-2 py-0.5 rounded font-bold uppercase">
                      {selectedTicket.category}
                    </span>
                    <span className={`text-[10px] px-2 py-0.5 rounded font-black uppercase ${
                      selectedTicket.priority === 'URGENT' || selectedTicket.priority === 'HIGH' 
                        ? 'bg-red-100 text-red-800' : 'bg-slate-100 text-slate-700'
                    }`}>
                      {selectedTicket.priority}
                    </span>
                  </div>

                  {isStaff && (
                    <div className="flex items-center gap-1.5">
                      {selectedTicket.status !== 'RESOLVED' && selectedTicket.status !== 'CLOSED' && (
                        <button
                          onClick={() => setShowResolveModal(true)}
                          className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold transition-colors shadow-sm"
                        >
                          Resolve Ticket
                        </button>
                      )}
                      <button
                        onClick={handleEscalate}
                        className="px-2.5 py-1 bg-amber-50 hover:bg-amber-100 text-amber-900 border border-amber-300 rounded-lg text-xs font-bold transition-colors flex items-center gap-1"
                      >
                        <ArrowUpRight className="w-3.5 h-3.5 text-amber-700" />
                        <span>Escalate</span>
                      </button>
                    </div>
                  )}
                </div>

                <h3 className="font-bold text-base text-slate-900">{selectedTicket.subject}</h3>

                <div className="flex flex-wrap items-center gap-4 text-[11px] text-slate-500">
                  <div>Initiator: <strong className="text-slate-800">{selectedTicket.userName}</strong> ({selectedTicket.userRole.replace('ROLE_', '')})</div>
                  {selectedTicket.studentName && (
                    <div>Student: <strong className="text-slate-800">{selectedTicket.studentName}</strong> ({selectedTicket.admissionNumber})</div>
                  )}
                  <div>Dept: <strong className="text-slate-800">{selectedTicket.assignedDepartment}</strong></div>
                </div>

                {selectedTicket.resolutionNotes && (
                  <div className="mt-1 p-2.5 bg-emerald-50 border border-emerald-200 rounded-xl text-xs text-emerald-950 font-medium">
                    <strong>Official Resolution:</strong> {selectedTicket.resolutionNotes}
                  </div>
                )}
              </div>

              {/* Message Timeline */}
              <div className="flex-1 overflow-y-auto p-5 space-y-4 bg-slate-50/40">
                {selectedTicket.messages.map((m) => (
                  <div
                    key={m.id}
                    className={`p-4 rounded-2xl shadow-sm text-xs leading-relaxed ${
                      m.internalNote
                        ? 'bg-amber-50 border-2 border-amber-300 text-amber-950'
                        : m.senderRole.includes('ADMIN') || m.senderRole.includes('TEACHER') || m.senderRole.includes('PRINCIPAL')
                        ? 'bg-white border border-brand-200 text-slate-800 ml-4'
                        : 'bg-white border border-slate-200 text-slate-800 mr-4'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-2">
                      <div className="flex items-center gap-2">
                        <strong className="text-slate-900">{m.senderName}</strong>
                        <span className="text-[10px] font-bold text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
                          {m.senderRole.replace('ROLE_', '')}
                        </span>
                        {m.internalNote && (
                          <span className="flex items-center gap-1 text-[10px] font-black uppercase text-amber-800 bg-amber-200 px-2 py-0.5 rounded">
                            <Lock className="w-3 h-3 text-amber-800" />
                            <span>Staff Internal Note (Hidden from Parent)</span>
                          </span>
                        )}
                      </div>
                      <span className="text-[10px] text-slate-400">
                        {new Date(m.createdAt).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' })}
                      </span>
                    </div>

                    <p className="whitespace-pre-line text-slate-700">{m.message}</p>
                  </div>
                ))}
              </div>

              {/* Reply Box */}
              {selectedTicket.status !== 'CLOSED' && (
                <form onSubmit={handleSendReply} className="p-4 border-t border-slate-200 bg-white space-y-2">
                  {isStaff && (
                    <div className="flex items-center justify-between text-xs">
                      <label className="flex items-center gap-2 font-bold cursor-pointer text-slate-700">
                        <input
                          type="checkbox"
                          checked={isInternalNote}
                          onChange={(e) => setIsInternalNote(e.target.checked)}
                          className="rounded text-amber-600 focus:ring-amber-500"
                        />
                        <span className={isInternalNote ? 'text-amber-800 font-black' : ''}>
                          Internal Staff Discussion Note (Invisible to Parent/Student)
                        </span>
                      </label>
                    </div>
                  )}

                  <div className="flex gap-2">
                    <textarea
                      rows={2}
                      placeholder={isInternalNote ? "Write private staff note..." : "Write message reply..."}
                      value={replyMessage}
                      onChange={(e) => setReplyMessage(e.target.value)}
                      className="flex-1 text-xs border border-slate-300 rounded-xl p-2.5 focus:ring-2 focus:ring-brand-500 focus:outline-none"
                    />
                    <button
                      type="submit"
                      disabled={replying || !replyMessage.trim()}
                      className="px-4 py-2 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-colors shadow-sm disabled:opacity-50 self-end flex items-center gap-1.5"
                    >
                      <Send className="w-3.5 h-3.5" />
                      <span>{replying ? 'Sending...' : 'Send'}</span>
                    </button>
                  </div>
                </form>
              )}
            </>
          ) : (
            <div className="h-full flex flex-col items-center justify-center p-8 text-center text-slate-400">
              <MessageSquare className="w-12 h-12 text-slate-300 mb-2" />
              <p className="font-bold text-slate-700 text-sm">Select a Ticket to View Details</p>
              <p className="text-xs text-slate-400 max-w-sm mt-1">
                Select any inquiry from the left queue to view correspondence, reply, or review resolution notes.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* CREATE TICKET MODAL */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-lg max-h-[90vh] overflow-y-auto">
            <div className="p-5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
              <div className="flex items-center gap-2">
                <HelpCircle className="w-5 h-5 text-brand-600" />
                <h3 className="font-bold text-sm text-slate-900">Open Support or Complaint Ticket</h3>
              </div>
              <button onClick={() => setShowCreateModal(false)} className="text-slate-400 hover:text-slate-700">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateTicket} className="p-5 space-y-4 text-xs">
              {students.length > 0 && (
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Regarding Student (Optional)</label>
                  <select
                    value={createForm.studentId}
                    onChange={(e) => setCreateForm({ ...createForm, studentId: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  >
                    <option value="">General Inquiry / Not Student Specific</option>
                    {students.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.fullName || `${s.firstName} ${s.lastName}`} ({s.admissionNumber})
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Inquiry Category *</label>
                  <select
                    value={createForm.category}
                    onChange={(e) => setCreateForm({ ...createForm, category: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  >
                    <option value="ACADEMIC">Academic & Curriculum</option>
                    <option value="TEACHER">Faculty & Teacher Feedback</option>
                    <option value="FEE">Fee Vouchers & Accounts</option>
                    <option value="TRANSPORT">School Transport</option>
                    <option value="ADMINISTRATIVE">Administrative Documents</option>
                    <option value="TECHNICAL">Portal / Tech Issue</option>
                    <option value="SUGGESTION">Improvement Suggestion</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 mb-1">Priority *</label>
                  <select
                    value={createForm.priority}
                    onChange={(e) => setCreateForm({ ...createForm, priority: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  >
                    <option value="LOW">Low - General Inquiry</option>
                    <option value="MEDIUM">Medium - Normal Request</option>
                    <option value="HIGH">High - Needs Prompt Attention</option>
                    <option value="URGENT">Urgent - Critical Escalation</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Subject *</label>
                <input
                  type="text"
                  placeholder="e.g. Inconsistency in term fee voucher discount"
                  value={createForm.subject}
                  onChange={(e) => setCreateForm({ ...createForm, subject: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Description & Details *</label>
                <textarea
                  rows={4}
                  placeholder="Provide complete context, relevant dates, or questions..."
                  value={createForm.description}
                  onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                />
              </div>

              <div className="pt-3 border-t border-slate-200 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 border border-slate-300 rounded-xl text-slate-700 font-bold hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl font-bold shadow-md disabled:opacity-50"
                >
                  {submitting ? 'Submitting...' : 'Submit Support Ticket'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RESOLVE TICKET MODAL */}
      {showResolveModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-md p-6">
            <h3 className="font-bold text-sm text-slate-900 mb-2">Resolve Support Ticket</h3>
            <p className="text-xs text-slate-500 mb-4">
              Enter official resolution notes explaining how this inquiry or complaint was handled for audit and parent closure.
            </p>
            <textarea
              rows={3}
              placeholder="e.g. Corrected fee voucher and sent revised Askari Bank challan..."
              value={resolutionNotes}
              onChange={(e) => setResolutionNotes(e.target.value)}
              className="w-full text-xs border border-slate-300 rounded-xl p-3 focus:ring-2 focus:ring-brand-500 mb-4"
              required
            />
            <div className="flex items-center justify-end gap-2 text-xs">
              <button
                onClick={() => setShowResolveModal(false)}
                className="px-4 py-2 border border-slate-300 rounded-xl font-bold text-slate-700 hover:bg-slate-50"
              >
                Cancel
              </button>
              <button
                onClick={() => handleUpdateStatus('RESOLVED')}
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl shadow-md"
              >
                Confirm Resolution
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
