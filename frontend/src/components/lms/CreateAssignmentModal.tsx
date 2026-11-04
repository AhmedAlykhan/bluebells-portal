import React, { useState, useEffect } from 'react';
import { X, Plus, CheckCircle, AlertCircle } from 'lucide-react';
import { CourseModule, Assignment } from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';

interface CreateAssignmentModalProps {
  courseId: number;
  modules: CourseModule[];
  isOpen: boolean;
  onClose: () => void;
  onCreated: (assignment: Assignment) => void;
}

export function CreateAssignmentModal({
  courseId,
  modules,
  isOpen,
  onClose,
  onCreated,
}: CreateAssignmentModalProps) {
  const [title, setTitle] = useState('');
  const [moduleId, setModuleId] = useState<string>('');
  const [instructions, setInstructions] = useState('');
  const [maxMarks, setMaxMarks] = useState('50.00');
  const [passingMarks, setPassingMarks] = useState('20.00');
  const [dueDate, setDueDate] = useState('');
  const [dueTime, setDueTime] = useState('23:59');
  const [allowLate, setAllowLate] = useState(true);
  const [allowImages, setAllowImages] = useState(true);
  const [allowPdf, setAllowPdf] = useState(true);
  const [allowDocx, setAllowDocx] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  useEffect(() => {
    if (isOpen) {
      setTitle('');
      setModuleId(modules.length > 0 ? modules[0].id.toString() : '');
      setInstructions('');
      setMaxMarks('50.00');
      setPassingMarks('20.00');
      // Default due date to 7 days from now
      const defaultDate = new Date();
      defaultDate.setDate(defaultDate.getDate() + 7);
      setDueDate(defaultDate.toISOString().split('T')[0]);
      setDueTime('23:59');
      setAllowLate(true);
      setAllowImages(true);
      setAllowPdf(true);
      setAllowDocx(false);
      setErrorMessage(null);
    }
  }, [isOpen, modules]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      setErrorMessage('Assignment title is required.');
      return;
    }
    if (!instructions.trim()) {
      setErrorMessage('Detailed assignment instructions are required.');
      return;
    }
    if (!dueDate) {
      setErrorMessage('Due date is required.');
      return;
    }

    const formats: string[] = [];
    if (allowImages) formats.push('IMAGES');
    if (allowPdf) formats.push('PDF');
    if (allowDocx) formats.push('DOCX');
    if (formats.length === 0) formats.push('IMAGES', 'PDF');

    const dueDateTime = new Date(`${dueDate}T${dueTime}:00`).toISOString();

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      const created = await lmsApi.createAssignment(courseId, {
        moduleId: moduleId ? parseInt(moduleId) : undefined,
        title: title.trim(),
        instructions: instructions.trim(),
        maxMarks: parseFloat(maxMarks) || 100,
        passingMarks: parseFloat(passingMarks) || 40,
        dueDate: dueDateTime,
        allowLateSubmission: allowLate,
        allowedFormats: formats.join(','),
      });

      onCreated(created);
      onClose();
    } catch (err: unknown) {
      const errorMsg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Error publishing assignment. Please try again.';
      setErrorMessage(errorMsg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-900/80 backdrop-blur-sm transition-opacity"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-xl bg-white rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[92vh] border border-slate-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-slate-50">
          <div className="flex items-center gap-3 min-w-0">
            <span className="p-2 rounded-lg bg-purple-100 text-purple-700">
              <Plus className="w-5 h-5" />
            </span>
            <div className="truncate">
              <h3 className="text-base font-bold text-slate-800 truncate">Publish Homework / Assignment</h3>
              <p className="text-xs text-slate-500">Distribute tasks with due dates, marks, and allowed formats</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-200/60 rounded-lg transition-colors"
            title="Close (Esc)"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-4 flex-1">
          {/* Title */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Assignment Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Exercise 1.2 Mathematical Proofs & Complex Conjugates"
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400"
            />
          </div>

          {/* Module Selection */}
          {modules.length > 0 && (
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Curriculum Module / Chapter
              </label>
              <select
                value={moduleId}
                onChange={(e) => setModuleId(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all bg-white"
              >
                <option value="">General Course Assignment</option>
                {modules.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Instructions */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Instructions & Questions *
            </label>
            <textarea
              required
              rows={4}
              value={instructions}
              onChange={(e) => setInstructions(e.target.value)}
              placeholder="Specify the exercises, textbook page numbers, proof requirements, or steps students must solve..."
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400 resize-none"
            />
          </div>

          {/* Marks */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Max Marks *
              </label>
              <input
                type="number"
                step="0.5"
                min="1"
                required
                value={maxMarks}
                onChange={(e) => setMaxMarks(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm font-bold text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Passing Marks *
              </label>
              <input
                type="number"
                step="0.5"
                min="0"
                required
                value={passingMarks}
                onChange={(e) => setPassingMarks(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm font-bold text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all"
              />
            </div>
          </div>

          {/* Due Date & Time */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Due Date *
              </label>
              <input
                type="date"
                required
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all bg-white"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Due Time *
              </label>
              <input
                type="time"
                required
                value={dueTime}
                onChange={(e) => setDueTime(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all bg-white"
              />
            </div>
          </div>

          {/* Allowed Submission Formats */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Allowed Student Submission Formats
            </label>
            <div className="flex items-center gap-4 flex-wrap text-xs text-slate-700">
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={allowImages}
                  onChange={(e) => setAllowImages(e.target.checked)}
                  className="w-4 h-4 text-purple-600 rounded focus:ring-purple-500"
                />
                <span>Handwritten Photos (JPG, PNG)</span>
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={allowPdf}
                  onChange={(e) => setAllowPdf(e.target.checked)}
                  className="w-4 h-4 text-purple-600 rounded focus:ring-purple-500"
                />
                <span>PDF Documents</span>
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={allowDocx}
                  onChange={(e) => setAllowDocx(e.target.checked)}
                  className="w-4 h-4 text-purple-600 rounded focus:ring-purple-500"
                />
                <span>Word / Docs (DOCX)</span>
              </label>
            </div>
          </div>

          {/* Late Submissions Toggle */}
          <div className="pt-2">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={allowLate}
                onChange={(e) => setAllowLate(e.target.checked)}
                className="w-4 h-4 text-purple-600 rounded focus:ring-purple-500"
              />
              <span className="text-xs font-medium text-slate-700">
                Allow Late Submissions (Will be marked with a "Late" tag)
              </span>
            </label>
          </div>

          {/* Error Message */}
          {errorMessage && (
            <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          {/* Submit Actions */}
          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-200">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2.5 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2.5 rounded-xl text-xs font-bold bg-purple-600 hover:bg-purple-700 text-white shadow-md shadow-purple-600/20 flex items-center gap-2 disabled:opacity-50 transition-all"
            >
              <CheckCircle className="w-4 h-4" />
              {isSubmitting ? 'Publishing...' : 'Publish Assignment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
