import { useState, useEffect } from 'react';
import {
  X,
  CheckCircle,
  AlertTriangle,
  RotateCw,
  ZoomIn,
  ZoomOut,
  ChevronLeft,
  ChevronRight,
  MessageSquare,
  Award,
  RefreshCw,
  Calendar,
  User as UserIcon,
  Eye
} from 'lucide-react';
import { AssignmentSubmission } from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';

interface TeacherGradingModalProps {
  submission: AssignmentSubmission | null;
  isOpen: boolean;
  onClose: () => void;
  onGraded: (updated: AssignmentSubmission) => void;
}

export function TeacherGradingModal({
  submission,
  isOpen,
  onClose,
  onGraded,
}: TeacherGradingModalProps) {
  const [marks, setMarks] = useState<string>('');
  const [feedback, setFeedback] = useState<string>('');
  const [currentFileIndex, setCurrentFileIndex] = useState<number>(0);
  const [zoom, setZoom] = useState<number>(100);
  const [rotation, setRotation] = useState<number>(0);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
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
    if (isOpen && submission) {
      setMarks(submission.marksObtained !== undefined && submission.marksObtained !== null ? submission.marksObtained.toString() : '');
      setFeedback(submission.teacherFeedback || '');
      setCurrentFileIndex(0);
      setZoom(100);
      setRotation(0);
      setErrorMessage(null);
    }
  }, [isOpen, submission]);

  if (!isOpen || !submission) return null;

  const maxMarks = submission.maxMarks || 100;
  const passingMarks = submission.passingMarks || 40;

  const files = submission.submissionFiles && submission.submissionFiles.length > 0
    ? submission.submissionFiles
    : submission.fileUrl
    ? [{ id: 1, submissionId: submission.id, filePath: submission.fileUrl, originalFilename: 'Homework_File.jpg', fileSizeBytes: 500000, fileType: 'IMAGE' as const, uploadOrder: 1, createdAt: submission.submittedAt }]
    : [];

  const currentFile = files[currentFileIndex];

  const handleZoomIn = () => setZoom((prev) => Math.min(prev + 25, 250));
  const handleZoomOut = () => setZoom((prev) => Math.max(prev - 25, 50));
  const handleRotate = () => setRotation((prev) => (prev + 90) % 360);

  const numericMarks = parseFloat(marks);
  const isInvalidMarks = isNaN(numericMarks) || numericMarks < 0 || numericMarks > maxMarks;
  const isPassing = !isNaN(numericMarks) && numericMarks >= passingMarks;

  const handleAction = async (status: 'GRADED' | 'RETURNED_FOR_CORRECTION') => {
    if (status === 'GRADED') {
      if (marks.trim() === '' || isNaN(numericMarks)) {
        setErrorMessage('Please enter valid marks obtained before publishing grade.');
        return;
      }
      if (numericMarks < 0 || numericMarks > maxMarks) {
        setErrorMessage(`Marks obtained must be between 0.00 and maximum ${maxMarks.toFixed(2)}.`);
        return;
      }
    } else {
      if (!feedback.trim()) {
        setErrorMessage('Please provide written feedback specifying what needs correction.');
        return;
      }
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      const updated = await lmsApi.gradeSubmission(submission.id, {
        marksObtained: isNaN(numericMarks) ? 0 : numericMarks,
        teacherFeedback: feedback.trim(),
        status,
      });
      onGraded(updated);
      onClose();
    } catch (err: unknown) {
      const errorMsg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Error saving grading decision. Please try again.';
      setErrorMessage(errorMsg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const quickFeedbackChips = [
    'Distinction-level work! Excellent conceptual clarity.',
    'Neat step-by-step proofs. Formulas accurately applied.',
    'Calculation on question 3 needs correction. Check arithmetic.',
    'Handwriting is blurry. Please resubmit clear photos of Page 2.',
    'Good attempt, but review conjugate verification steps.'
  ];

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-slate-900/80 backdrop-blur-sm transition-opacity"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-5xl bg-white rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[95vh] border border-slate-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-slate-50 flex-wrap gap-2">
          <div className="flex items-center gap-3 min-w-0">
            <span className="p-2 rounded-lg bg-purple-100 text-purple-700">
              <Award className="w-5 h-5" />
            </span>
            <div className="truncate">
              <h3 className="text-base font-bold text-slate-800 truncate">
                Review & Grade Student Homework
              </h3>
              <p className="text-xs text-slate-500 truncate">
                {submission.assignmentTitle} • Student: <strong className="text-slate-700">{submission.studentName}</strong>
              </p>
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

        {/* Content Layout (Split: Photo Viewer on Left, Grading Controls on Right) */}
        <div className="flex-1 overflow-y-auto grid grid-cols-1 lg:grid-cols-12 min-h-0">
          {/* Left Column: Multi-Photo Student Submission Viewer */}
          <div className="lg:col-span-7 bg-slate-900 flex flex-col border-b lg:border-b-0 lg:border-r border-slate-800 min-h-[350px] lg:min-h-[500px]">
            {/* Viewer Top Toolbar */}
            <div className="flex items-center justify-between px-4 py-2.5 bg-slate-950 border-b border-slate-800 text-xs text-slate-300">
              <div className="flex items-center gap-2">
                <span className="font-bold text-white">
                  Page {files.length > 0 ? currentFileIndex + 1 : 0} of {files.length}
                </span>
                {submission.late && (
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-900/60 text-amber-300 border border-amber-700">
                    LATE SUBMISSION
                  </span>
                )}
              </div>

              {/* Viewer Controls */}
              <div className="flex items-center gap-1.5">
                <button
                  onClick={handleZoomOut}
                  className="p-1.5 hover:bg-slate-800 text-slate-300 hover:text-white rounded"
                  title="Zoom Out"
                >
                  <ZoomOut className="w-4 h-4" />
                </button>
                <span className="font-mono text-[11px] px-1 text-slate-400">{zoom}%</span>
                <button
                  onClick={handleZoomIn}
                  className="p-1.5 hover:bg-slate-800 text-slate-300 hover:text-white rounded"
                  title="Zoom In"
                >
                  <ZoomIn className="w-4 h-4" />
                </button>
                <button
                  onClick={handleRotate}
                  className="p-1.5 hover:bg-slate-800 text-slate-300 hover:text-white rounded"
                  title="Rotate 90°"
                >
                  <RotateCw className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Photo Canvas Area */}
            <div className="flex-1 overflow-auto p-4 flex items-center justify-center relative">
              {currentFile ? (
                <img
                  src={currentFile.filePath}
                  alt={`Page ${currentFileIndex + 1}`}
                  style={{
                    transform: `scale(${zoom / 100}) rotate(${rotation}deg)`,
                    transition: 'transform 0.15s ease-out',
                  }}
                  className="max-h-[480px] max-w-full rounded shadow-xl object-contain"
                />
              ) : (
                <div className="text-center text-slate-500 text-xs py-12">
                  <Eye className="w-8 h-8 mx-auto mb-2 opacity-50" />
                  <p>No image or document file was uploaded for this submission.</p>
                </div>
              )}
            </div>

            {/* Viewer Carousel Bottom Strip */}
            {files.length > 1 && (
              <div className="flex items-center justify-between px-4 py-2 bg-slate-950 border-t border-slate-800">
                <button
                  onClick={() => setCurrentFileIndex((prev) => Math.max(prev - 1, 0))}
                  disabled={currentFileIndex === 0}
                  className="p-1 text-slate-400 hover:text-white disabled:opacity-30 transition-opacity"
                >
                  <ChevronLeft className="w-5 h-5" />
                </button>

                <div className="flex items-center gap-2 overflow-x-auto py-1">
                  {files.map((file, idx) => (
                    <button
                      key={idx}
                      onClick={() => {
                        setCurrentFileIndex(idx);
                        setZoom(100);
                        setRotation(0);
                      }}
                      className={`relative w-10 h-12 rounded border overflow-hidden transition-all ${
                        idx === currentFileIndex
                          ? 'border-purple-400 ring-2 ring-purple-500/50 scale-105'
                          : 'border-slate-700 opacity-60 hover:opacity-100'
                      }`}
                    >
                      <img src={file.filePath} alt={`Thumbnail ${idx + 1}`} className="w-full h-full object-cover" />
                      <span className="absolute bottom-0 inset-x-0 bg-slate-900/80 text-[9px] text-white font-mono text-center">
                        P{idx + 1}
                      </span>
                    </button>
                  ))}
                </div>

                <button
                  onClick={() => setCurrentFileIndex((prev) => Math.min(prev + 1, files.length - 1))}
                  disabled={currentFileIndex === files.length - 1}
                  className="p-1 text-slate-400 hover:text-white disabled:opacity-30 transition-opacity"
                >
                  <ChevronRight className="w-5 h-5" />
                </button>
              </div>
            )}
          </div>

          {/* Right Column: Grading Form & Feedback */}
          <div className="lg:col-span-5 p-6 flex flex-col justify-between space-y-5 bg-white">
            <div className="space-y-5">
              {/* Submission Metadata */}
              <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 text-xs space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 flex items-center gap-1">
                    <UserIcon className="w-3.5 h-3.5" />
                    Student:
                  </span>
                  <strong className="text-slate-800">{submission.studentName}</strong>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5" />
                    Submitted At:
                  </span>
                  <span className="font-mono text-slate-700">
                    {new Date(submission.submittedAt).toLocaleDateString()} at{' '}
                    {new Date(submission.submittedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
                {submission.submissionText && (
                  <div className="pt-1.5 border-t border-slate-200">
                    <p className="text-[11px] font-semibold text-slate-500">Student Remarks:</p>
                    <p className="text-slate-700 mt-0.5 italic">"{submission.submissionText}"</p>
                  </div>
                )}
              </div>

              {/* Marks Entry */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  Marks Obtained (out of {maxMarks.toFixed(2)})
                </label>
                <div className="flex items-center gap-3">
                  <div className="relative flex-1">
                    <input
                      type="number"
                      step="0.5"
                      min="0"
                      max={maxMarks}
                      value={marks}
                      onChange={(e) => setMarks(e.target.value)}
                      placeholder="e.g. 48.00"
                      className={`w-full px-3.5 py-2.5 rounded-xl border text-sm font-bold text-slate-800 focus:outline-none focus:ring-2 transition-all ${
                        marks && isInvalidMarks
                          ? 'border-red-500 ring-red-500/20'
                          : marks && isPassing
                          ? 'border-emerald-500 ring-emerald-500/20 text-emerald-900 bg-emerald-50/30'
                          : 'border-slate-300 focus:border-purple-600 focus:ring-purple-500/20'
                      }`}
                    />
                    <span className="absolute right-3.5 top-2.5 text-xs font-bold text-slate-400">
                      / {maxMarks.toFixed(2)}
                    </span>
                  </div>

                  {marks && !isNaN(numericMarks) && (
                    <span
                      className={`px-3 py-2 rounded-xl text-xs font-bold ${
                        isPassing ? 'bg-emerald-100 text-emerald-800' : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {isPassing ? 'PASSED' : 'BELOW PASSING'}
                    </span>
                  )}
                </div>
                <p className="text-[11px] text-slate-500 mt-1">
                  Passing threshold is <strong>{passingMarks.toFixed(2)} marks</strong>.
                </p>
              </div>

              {/* Teacher Feedback Textarea */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                  <MessageSquare className="w-3.5 h-3.5 text-purple-600" />
                  Written Feedback & Suggestions
                </label>
                <textarea
                  value={feedback}
                  onChange={(e) => setFeedback(e.target.value)}
                  rows={4}
                  placeholder="Enter comments on student's homework presentation, methodology, and errors..."
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400 resize-none"
                />

                {/* Quick Feedback Chips */}
                <div className="mt-2 flex flex-wrap gap-1.5">
                  {quickFeedbackChips.map((chip, idx) => (
                    <button
                      key={idx}
                      type="button"
                      onClick={() => setFeedback((prev) => (prev ? `${prev} ${chip}` : chip))}
                      className="px-2 py-1 rounded-md text-[10px] font-medium bg-slate-100 hover:bg-purple-100 hover:text-purple-800 text-slate-600 transition-colors text-left"
                    >
                      + {chip.slice(0, 32)}...
                    </button>
                  ))}
                </div>
              </div>

              {/* Error Alert */}
              {errorMessage && (
                <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700 flex items-center gap-2">
                  <AlertTriangle className="w-4 h-4 flex-shrink-0" />
                  <span>{errorMessage}</span>
                </div>
              )}
            </div>

            {/* Decision Action Buttons */}
            <div className="pt-4 border-t border-slate-200 space-y-2">
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => handleAction('RETURNED_FOR_CORRECTION')}
                  disabled={isSubmitting}
                  className="px-4 py-2.5 rounded-xl text-xs font-bold bg-amber-50 hover:bg-amber-100 text-amber-800 border border-amber-300 transition-all flex items-center justify-center gap-1.5 disabled:opacity-50"
                  title="Return to student so they can correct homework and re-upload"
                >
                  <RefreshCw className="w-3.5 h-3.5" />
                  Return for Correction
                </button>

                <button
                  type="button"
                  onClick={() => handleAction('GRADED')}
                  disabled={isSubmitting}
                  className="px-4 py-2.5 rounded-xl text-xs font-bold bg-emerald-600 hover:bg-emerald-700 text-white shadow-md shadow-emerald-600/20 transition-all flex items-center justify-center gap-1.5 disabled:opacity-50"
                  title="Finalize marks and publish grade to student portal"
                >
                  <CheckCircle className="w-3.5 h-3.5" />
                  Publish Grade
                </button>
              </div>

              <button
                type="button"
                onClick={onClose}
                disabled={isSubmitting}
                className="w-full py-2 text-xs font-semibold text-slate-500 hover:text-slate-800 transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
