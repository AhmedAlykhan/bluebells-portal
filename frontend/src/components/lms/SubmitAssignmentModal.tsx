import React, { useState, useEffect, useRef } from 'react';
import {
  X,
  Upload,
  FileText,
  Trash2,
  CheckCircle,
  AlertCircle,
  Clock,
  Eye,
  Camera
} from 'lucide-react';
import { Assignment, AssignmentSubmission } from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';

interface SubmitAssignmentModalProps {
  assignment: Assignment;
  studentId: number;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (submission: AssignmentSubmission) => void;
}

interface UploadedFilePreview {
  file?: File;
  name: string;
  size: number;
  previewUrl: string;
  type: string;
}

export function SubmitAssignmentModal({
  assignment,
  studentId,
  isOpen,
  onClose,
  onSuccess,
}: SubmitAssignmentModalProps) {
  const [submissionText, setSubmissionText] = useState('');
  const [files, setFiles] = useState<UploadedFilePreview[]>([]);
  const [previewEnlarged, setPreviewEnlarged] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const existingSubmission = assignment.studentSubmission;
  const isReturned = existingSubmission?.status === 'RETURNED_FOR_CORRECTION';

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        if (previewEnlarged) {
          setPreviewEnlarged(null);
        } else {
          onClose();
        }
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose, previewEnlarged]);

  useEffect(() => {
    if (isOpen) {
      if (existingSubmission) {
        setSubmissionText(existingSubmission.submissionText || '');
        if (existingSubmission.submissionFiles && existingSubmission.submissionFiles.length > 0) {
          setFiles(
            existingSubmission.submissionFiles.map((sf) => ({
              name: sf.originalFilename,
              size: sf.fileSizeBytes,
              previewUrl: sf.filePath,
              type: sf.fileType,
            }))
          );
        }
      } else {
        setSubmissionText('');
        setFiles([]);
      }
      setErrorMessage(null);
    }
  }, [isOpen, existingSubmission]);

  if (!isOpen || !assignment) return null;

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = e.target.files;
    if (!selectedFiles) return;

    const newFiles: UploadedFilePreview[] = Array.from(selectedFiles).map((file) => ({
      file,
      name: file.name,
      size: file.size,
      previewUrl: URL.createObjectURL(file),
      type: file.type.startsWith('image/') ? 'IMAGE' : 'PDF',
    }));

    setFiles((prev) => [...prev, ...newFiles]);
    setErrorMessage(null);
  };

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (files.length === 0 && !submissionText.trim()) {
      setErrorMessage('Please upload photos of your homework notebook or enter your solution text.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      // If we have actual File objects, submit via multipart form-data
      const hasActualFiles = files.some((f) => f.file instanceof File);
      let submission: AssignmentSubmission;

      if (hasActualFiles) {
        const formData = new FormData();
        formData.append('studentId', studentId.toString());
        formData.append('submissionText', submissionText);
        files.forEach((f) => {
          if (f.file) {
            formData.append('files', f.file);
          }
        });
        submission = await lmsApi.submitAssignmentFiles(assignment.id, formData);
      } else {
        // Fallback to JSON payload
        submission = await lmsApi.submitAssignmentJson(assignment.id, {
          studentId,
          submissionText,
          fileUrl: files[0]?.previewUrl,
          fileUrls: files.map((f) => f.previewUrl),
          fileNames: files.map((f) => f.name),
          fileSizes: files.map((f) => f.size),
          fileTypes: files.map((f) => f.type),
        });
      }

      onSuccess(submission);
      onClose();
    } catch (err: unknown) {
      const errorMsg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Error submitting homework. Please ensure file types are supported.';
      setErrorMessage(errorMsg);
    } finally {
      setIsSubmitting(false);
    }
  };

  const isDueDatePassed = new Date(assignment.dueDate).getTime() < Date.now();

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-900/80 backdrop-blur-sm transition-opacity"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-2xl bg-white rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[92vh] border border-slate-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-slate-50">
          <div className="flex items-center gap-3 min-w-0">
            <span className="p-2 rounded-lg bg-purple-100 text-purple-700">
              <Camera className="w-5 h-5" />
            </span>
            <div className="truncate">
              <h3 className="text-base font-bold text-slate-800 truncate">
                {isReturned ? 'Resubmit Homework for Correction' : 'Submit Homework / Assignment'}
              </h3>
              <p className="text-xs text-slate-500 truncate">{assignment.title}</p>
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
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-5 flex-1">
          {/* Assignment Requirements Alert */}
          <div className="p-4 rounded-xl bg-purple-50/60 border border-purple-200 text-xs text-slate-700 space-y-2">
            <div className="flex items-center justify-between font-semibold text-purple-900">
              <span>Assignment Requirements:</span>
              <span className="flex items-center gap-1 font-mono">
                <Clock className="w-3.5 h-3.5" />
                Due: {new Date(assignment.dueDate).toLocaleDateString()} at{' '}
                {new Date(assignment.dueDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </span>
            </div>
            <p className="text-slate-600 leading-relaxed">{assignment.instructions}</p>
            <div className="flex items-center gap-3 pt-1 text-slate-500 text-[11px] flex-wrap">
              <span>• Max Marks: <strong className="text-slate-700">{assignment.maxMarks.toFixed(2)}</strong></span>
              <span>• Passing Marks: <strong className="text-slate-700">{assignment.passingMarks.toFixed(2)}</strong></span>
              <span>• Formats: <strong className="text-purple-700">{assignment.allowedFormats}</strong></span>
              {isDueDatePassed && (
                <span className="text-amber-700 font-medium">
                  {assignment.allowLateSubmission ? '(Late submission accepted)' : '(Deadline expired)'}
                </span>
              )}
            </div>
          </div>

          {/* Teacher Feedback Banner if Returned for Correction */}
          {isReturned && existingSubmission?.teacherFeedback && (
            <div className="p-4 rounded-xl bg-amber-50 border border-amber-200 text-xs space-y-1">
              <div className="flex items-center gap-1.5 font-bold text-amber-800">
                <AlertCircle className="w-4 h-4 text-amber-600" />
                Teacher's Correction Notes:
              </div>
              <p className="text-amber-900 font-medium leading-relaxed pl-5">
                "{existingSubmission.teacherFeedback}"
              </p>
            </div>
          )}

          {/* Multi-Photo Homework Upload Dropzone */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Upload Handwritten Notebook Pages or Photos
            </label>
            <div
              onClick={() => fileInputRef.current?.click()}
              className="border-2 border-dashed border-purple-300 hover:border-purple-500 bg-purple-50/30 hover:bg-purple-50/60 rounded-xl p-6 text-center cursor-pointer transition-all group"
            >
              <input
                ref={fileInputRef}
                type="file"
                multiple
                accept="image/*,.pdf,.doc,.docx"
                onChange={handleFileChange}
                className="hidden"
              />
              <div className="w-12 h-12 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform">
                <Upload className="w-6 h-6" />
              </div>
              <p className="text-sm font-bold text-slate-800">Click to upload photos of your notebook pages</p>
              <p className="text-xs text-slate-500 mt-1">
                You can upload multiple pages (Page 1, Page 2, Page 3...). Supports JPG, PNG, WebP, and PDF files.
              </p>
            </div>
          </div>

          {/* Uploaded File Previews */}
          {files.length > 0 && (
            <div className="space-y-2">
              <p className="text-xs font-bold text-slate-700">Uploaded Pages ({files.length}):</p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                {files.map((file, idx) => (
                  <div
                    key={idx}
                    className="relative group border border-slate-200 rounded-xl overflow-hidden bg-slate-50 p-2 text-center shadow-sm"
                  >
                    <div className="relative aspect-[3/4] bg-slate-200 rounded-lg overflow-hidden mb-2 flex items-center justify-center">
                      {file.type === 'IMAGE' ? (
                        <img
                          src={file.previewUrl}
                          alt={`Page ${idx + 1}`}
                          className="w-full h-full object-cover group-hover:scale-105 transition-transform"
                        />
                      ) : (
                        <FileText className="w-10 h-10 text-slate-500" />
                      )}
                      <div className="absolute inset-0 bg-slate-900/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                        <button
                          type="button"
                          onClick={() => setPreviewEnlarged(file.previewUrl)}
                          className="p-1.5 bg-white text-slate-800 rounded-lg hover:bg-slate-100 shadow"
                          title="Enlarge preview"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button
                          type="button"
                          onClick={() => removeFile(idx)}
                          className="p-1.5 bg-red-600 text-white rounded-lg hover:bg-red-700 shadow"
                          title="Remove page"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                    <span className="inline-block px-2 py-0.5 rounded-full text-[10px] font-bold bg-purple-100 text-purple-800 mb-1">
                      Page {idx + 1}
                    </span>
                    <p className="text-[11px] font-medium text-slate-700 truncate px-1">{file.name}</p>
                    <p className="text-[10px] text-slate-400">{(file.size / 1024).toFixed(0)} KB</p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Student Comments */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Submission Remarks / Working Notes for Teacher (Optional)
            </label>
            <textarea
              value={submissionText}
              onChange={(e) => setSubmissionText(e.target.value)}
              rows={3}
              placeholder="Write any questions, rough work explanations, or notes for Sir Ahmed..."
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400 resize-none"
            />
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
              {isSubmitting ? 'Submitting Homework...' : isReturned ? 'Resubmit Homework' : 'Submit Homework Now'}
            </button>
          </div>
        </form>

        {/* Enlarged Photo Lightbox Modal */}
        {previewEnlarged && (
          <div
            className="fixed inset-0 z-60 bg-black/90 flex items-center justify-center p-4"
            onClick={() => setPreviewEnlarged(null)}
          >
            <div className="relative max-w-4xl max-h-[90vh]" onClick={(e) => e.stopPropagation()}>
              <button
                onClick={() => setPreviewEnlarged(null)}
                className="absolute -top-10 right-0 p-2 text-white hover:text-slate-300"
              >
                <X className="w-6 h-6" />
              </button>
              <img
                src={previewEnlarged}
                alt="Enlarged Homework Page"
                className="max-h-[85vh] w-auto rounded-lg shadow-2xl object-contain"
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
