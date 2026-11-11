import React, { useState, useEffect } from 'react';
import { 
  Trophy, 
  Award, 
  Sparkles, 
  Plus, 
  Calendar, 
  ExternalLink, 
  HeartHandshake, 
  Compass, 
  Medal, 
  Users, 
  X,
  FileCheck,
  RefreshCw
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

interface StudentDevelopmentRecord {
  id: number;
  studentId: number;
  studentName: string;
  admissionNumber: string;
  category: string;
  title: string;
  description: string;
  eventName?: string;
  achievementDate: string;
  level?: string;
  positionOrAward?: string;
  teacherAppreciationNote?: string;
  skillsAcquired?: string;
  evidenceFileUrl?: string;
  recordedByName: string;
  recordedByRole: string;
}

export const StudentDevelopmentPage: React.FC = () => {
  const { user } = useAuth();
  const roles = user?.roles || [];
  const isStaff = roles.some(r => ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER'].includes(r));

  const [records, setRecords] = useState<StudentDevelopmentRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedStudentId, setSelectedStudentId] = useState<number | null>(null);
  const [studentsList, setStudentsList] = useState<any[]>([]);
  const [showAddModal, setShowAddModal] = useState(false);
  const [previewEvidenceUrl, setPreviewEvidenceUrl] = useState<string | null>(null);

  // New record form state
  const [formData, setFormData] = useState({
    studentId: '',
    category: 'SPORTS',
    title: '',
    description: '',
    eventName: '',
    achievementDate: new Date().toISOString().split('T')[0],
    level: 'INTER_SCHOOL',
    positionOrAward: '',
    teacherAppreciationNote: '',
    skillsAcquired: ''
  });
  const [submitting, setSubmitting] = useState(false);

  // Load students for selector if staff
  useEffect(() => {
    if (isStaff) {
      apiClient.get('/students')
        .then(res => {
          if (res.data?.data) {
            setStudentsList(res.data.data);
            if (res.data.data.length > 0) {
              setSelectedStudentId(res.data.data[0].id);
            }
          }
        })
        .catch(err => console.error('Error fetching students:', err));
    } else {
      // For parent or student, query linked child or self
      apiClient.get('/auth/me')
        .then(res => {
          const u = res.data?.data;
          if (u?.studentId) {
            setSelectedStudentId(u.studentId);
          } else if (u?.linkedStudents && u.linkedStudents.length > 0) {
            setStudentsList(u.linkedStudents);
            setSelectedStudentId(u.linkedStudents[0].id);
          }
        })
        .catch(err => console.error(err));
    }
  }, [isStaff]);

  const fetchRecords = () => {
    if (!selectedStudentId) return;
    setLoading(true);
    apiClient.get(`/development/students/${selectedStudentId}`)
      .then(res => {
        if (res.data?.data) {
          setRecords(res.data.data);
        }
      })
      .catch(err => {
        console.error('Error fetching development records:', err);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (selectedStudentId) {
      fetchRecords();
    }
  }, [selectedStudentId]);

  const handleCreateRecord = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.title || !formData.description) return;

    setSubmitting(true);
    try {
      await apiClient.post('/development/records', {
        ...formData,
        studentId: Number(formData.studentId || selectedStudentId)
      });
      setShowAddModal(false);
      setFormData({
        studentId: '',
        category: 'SPORTS',
        title: '',
        description: '',
        eventName: '',
        achievementDate: new Date().toISOString().split('T')[0],
        level: 'INTER_SCHOOL',
        positionOrAward: '',
        teacherAppreciationNote: '',
        skillsAcquired: ''
      });
      fetchRecords();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to record student development milestone.');
    } finally {
      setSubmitting(false);
    }
  };

  const categories = [
    { id: 'ALL', label: 'All Records', icon: Sparkles },
    { id: 'ACADEMIC_ACHIEVEMENT', label: 'Academic Distinction', icon: Award },
    { id: 'SPORTS', label: 'Sports & Athletics', icon: Trophy },
    { id: 'COMPETITION', label: 'Competitions & Olympiads', icon: Medal },
    { id: 'CO_CURRICULAR', label: 'Co-Curricular Arts & Debates', icon: Compass },
    { id: 'PROJECT', label: 'Science & Tech Projects', icon: FileCheck },
    { id: 'POSITIVE_BEHAVIOUR', label: 'Positive Behaviour & Civics', icon: HeartHandshake },
    { id: 'TEACHER_APPRECIATION', label: 'Teacher Appreciation', icon: Users },
  ];

  const filteredRecords = selectedCategory === 'ALL' 
    ? records 
    : records.filter(r => r.category === selectedCategory);

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-amber-700 uppercase tracking-widest mb-1">
            <Trophy className="w-4 h-4 text-amber-500" />
            <span>Beyond Examination Results</span>
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">Student Development Centre</h1>
          <p className="text-sm text-slate-500 mt-0.5">
            Holistic student growth tracking co-curricular achievements, sports championships, character commendations, and skills milestones.
          </p>
        </div>

        {isStaff && (
          <button
            onClick={() => setShowAddModal(true)}
            className="flex items-center gap-2 px-4 py-2.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl text-xs font-bold transition-all shadow-md self-start md:self-auto"
          >
            <Plus className="w-4 h-4" />
            <span>Record Achievement / Milestone</span>
          </button>
        )}
      </div>

      {/* Student Selector Toolbar */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-wrap items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <span className="text-xs font-bold text-slate-600">Active Student:</span>
          {studentsList.length > 0 ? (
            <select
              value={selectedStudentId || ''}
              onChange={(e) => setSelectedStudentId(Number(e.target.value))}
              className="text-xs font-bold bg-slate-50 border border-slate-300 rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-brand-500 text-slate-800"
            >
              {studentsList.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.fullName || `${s.firstName} ${s.lastName}`} ({s.admissionNumber})
                </option>
              ))}
            </select>
          ) : (
            <span className="text-xs font-bold text-brand-900 bg-brand-50 px-3 py-1 rounded-md">
              Student ID #{selectedStudentId || 'Active Profile'}
            </span>
          )}
        </div>

        <button
          onClick={fetchRecords}
          className="flex items-center gap-1.5 text-xs font-semibold text-slate-600 hover:text-brand-800 bg-slate-100 hover:bg-slate-200 px-3 py-1.5 rounded-lg transition-colors"
        >
          <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          <span>Refresh Records</span>
        </button>
      </div>

      {/* Category Pills */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
        {categories.map((c) => {
          const Icon = c.icon;
          const active = selectedCategory === c.id;
          return (
            <button
              key={c.id}
              onClick={() => setSelectedCategory(c.id)}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all ${
                active 
                  ? 'bg-amber-500 text-white shadow-sm' 
                  : 'bg-white border border-slate-200 text-slate-600 hover:border-amber-400 hover:text-amber-800'
              }`}
            >
              <Icon className="w-3.5 h-3.5" />
              <span>{c.label}</span>
            </button>
          );
        })}
      </div>

      {/* Achievement Cards Grid */}
      {loading ? (
        <div className="py-20 flex flex-col items-center justify-center bg-white rounded-2xl border border-slate-200">
          <RefreshCw className="w-7 h-7 animate-spin text-amber-500 mb-3" />
          <p className="text-sm font-semibold text-slate-600">Retrieving student development portfolio...</p>
        </div>
      ) : filteredRecords.length === 0 ? (
        <div className="py-16 text-center bg-white rounded-2xl border border-slate-200 p-8">
          <Trophy className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="font-bold text-slate-700 text-base">No Milestones Recorded in this Category</h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto mt-1">
            Bluebells Schooling System records authentic co-curricular distinctions, sports awards, and character commendations verified by teachers and school leadership.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredRecords.map((r) => (
            <div 
              key={r.id} 
              className="bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between overflow-hidden"
            >
              <div className="p-5">
                <div className="flex items-center justify-between gap-2 mb-3">
                  <span className="text-[10px] font-black uppercase tracking-wider px-2.5 py-1 bg-amber-50 text-amber-900 border border-amber-200 rounded-full">
                    {r.category.replace(/_/g, ' ')}
                  </span>
                  <div className="flex items-center gap-1 text-[11px] text-slate-400 font-medium">
                    <Calendar className="w-3.5 h-3.5" />
                    <span>{r.achievementDate}</span>
                  </div>
                </div>

                <h3 className="font-bold text-base text-slate-900 mb-1.5 leading-snug">{r.title}</h3>
                <p className="text-xs text-slate-600 leading-relaxed line-clamp-3 mb-3">{r.description}</p>

                {/* Level and Position Badges */}
                <div className="flex flex-wrap gap-1.5 mb-3">
                  {r.level && (
                    <span className="text-[11px] font-semibold bg-slate-100 text-slate-700 px-2 py-0.5 rounded-md">
                      Level: {r.level.replace(/_/g, ' ')}
                    </span>
                  )}
                  {r.positionOrAward && (
                    <span className="text-[11px] font-bold bg-amber-100 text-amber-900 px-2 py-0.5 rounded-md flex items-center gap-1">
                      <Medal className="w-3 h-3 text-amber-600" />
                      {r.positionOrAward}
                    </span>
                  )}
                </div>

                {/* Skills Acquired Tags */}
                {r.skillsAcquired && (
                  <div className="mb-3">
                    <p className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mb-1">Demonstrated Skills:</p>
                    <div className="flex flex-wrap gap-1">
                      {r.skillsAcquired.split(',').map((skill, sIdx) => (
                        <span key={sIdx} className="text-[10px] font-medium bg-brand-50 text-brand-900 px-2 py-0.5 rounded-md border border-brand-100">
                          {skill.trim()}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Teacher Appreciation Note */}
                {r.teacherAppreciationNote && (
                  <div className="bg-slate-50 border-l-2 border-[#123B70] p-2.5 rounded-r-lg text-xs italic text-slate-700 mb-3">
                    "{r.teacherAppreciationNote}"
                  </div>
                )}
              </div>

              {/* Card Footer */}
              <div className="bg-slate-50/70 border-t border-slate-100 px-5 py-3 flex items-center justify-between text-[11px] text-slate-500">
                <span>Verified by: <strong className="text-slate-700">{r.recordedByName}</strong></span>
                {r.evidenceFileUrl && (
                  <button 
                    onClick={() => setPreviewEvidenceUrl(r.evidenceFileUrl || null)}
                    className="flex items-center gap-1 text-brand-700 hover:text-brand-900 font-bold hover:underline"
                  >
                    <span>View Evidence</span>
                    <ExternalLink className="w-3 h-3" />
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Record Achievement Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-lg max-h-[90vh] overflow-y-auto">
            <div className="p-5 border-b border-slate-200 flex items-center justify-between bg-slate-50">
              <div className="flex items-center gap-2">
                <Trophy className="w-5 h-5 text-amber-500" />
                <h3 className="font-bold text-sm text-slate-900">Record Student Achievement</h3>
              </div>
              <button 
                onClick={() => setShowAddModal(false)}
                className="p-1 rounded-lg hover:bg-slate-200 text-slate-500"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateRecord} className="p-5 space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Student *</label>
                <select
                  value={formData.studentId || selectedStudentId || ''}
                  onChange={(e) => setFormData({ ...formData, studentId: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                >
                  <option value="">Select Student...</option>
                  {studentsList.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.fullName || `${s.firstName} ${s.lastName}`} ({s.admissionNumber})
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Category *</label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  >
                    <option value="SPORTS">Sports & Athletics</option>
                    <option value="ACADEMIC_ACHIEVEMENT">Academic Distinction</option>
                    <option value="COMPETITION">Competition & Olympiad</option>
                    <option value="CO_CURRICULAR">Co-Curricular Arts & Debates</option>
                    <option value="PROJECT">Science & Tech Project</option>
                    <option value="POSITIVE_BEHAVIOUR">Positive Behaviour & Civics</option>
                    <option value="TEACHER_APPRECIATION">Teacher Appreciation</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-slate-700 mb-1">Date *</label>
                  <input
                    type="date"
                    value={formData.achievementDate}
                    onChange={(e) => setFormData({ ...formData, achievementDate: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Title / Milestone Award *</label>
                <input
                  type="text"
                  placeholder="e.g. Hyderabad Inter-School Cricket Championship - Best Bowler"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Competition / Event Name</label>
                  <input
                    type="text"
                    placeholder="e.g. Annual District Sports Gala"
                    value={formData.eventName}
                    onChange={(e) => setFormData({ ...formData, eventName: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 mb-1">Position / Medal</label>
                  <input
                    type="text"
                    placeholder="e.g. 1st Place / Gold Medal"
                    value={formData.positionOrAward}
                    onChange={(e) => setFormData({ ...formData, positionOrAward: e.target.value })}
                    className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Description & Impact *</label>
                <textarea
                  rows={3}
                  placeholder="Describe student's performance, team contribution, or milestone achievement..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                  required
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Skills Demonstrated (comma-separated)</label>
                <input
                  type="text"
                  placeholder="e.g. Team Leadership, Athletic Resilience, Strategic Thinking"
                  value={formData.skillsAcquired}
                  onChange={(e) => setFormData({ ...formData, skillsAcquired: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Teacher Appreciation Note</label>
                <input
                  type="text"
                  placeholder="Personal commendation from Class Teacher / Coach..."
                  value={formData.teacherAppreciationNote}
                  onChange={(e) => setFormData({ ...formData, teacherAppreciationNote: e.target.value })}
                  className="w-full border border-slate-300 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-brand-500"
                />
              </div>

              <div className="pt-3 border-t border-slate-200 flex items-center justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 border border-slate-300 rounded-xl text-slate-700 font-bold hover:bg-slate-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="px-5 py-2 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl font-bold transition-all shadow-md disabled:opacity-50"
                >
                  {submitting ? 'Recording...' : 'Save Milestone'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Evidence Preview Modal */}
      {previewEvidenceUrl && (
        <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full p-4">
            <div className="flex items-center justify-between pb-3 border-b border-slate-200 mb-3">
              <h4 className="font-bold text-sm text-slate-900">Certificate / Evidence Preview</h4>
              <button 
                onClick={() => setPreviewEvidenceUrl(null)}
                className="p-1 rounded-lg hover:bg-slate-100 text-slate-500"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="rounded-xl overflow-hidden bg-slate-100 flex items-center justify-center min-h-[300px]">
              <img 
                src={previewEvidenceUrl} 
                alt="Evidence" 
                className="max-h-[500px] w-auto object-contain"
                onError={(e) => {
                  (e.target as any).src = 'https://placehold.co/600x400?text=Certificate+Evidence+File';
                }}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
