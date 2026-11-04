import React, { useState, useEffect, useRef } from 'react';
import {
  X,
  Play,
  FileText,
  Upload,
  Link as LinkIcon,
  CheckCircle,
  AlertCircle,
  Clock,
  Video
} from 'lucide-react';
import { CourseModule, LearningResource, YouTubeVideo } from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';

interface AddResourceModalProps {
  courseId: number;
  modules: CourseModule[];
  isOpen: boolean;
  onClose: () => void;
  onVideoCreated: (video: YouTubeVideo) => void;
  onResourceCreated: (resource: LearningResource) => void;
}

export function AddResourceModal({
  courseId,
  modules,
  isOpen,
  onClose,
  onVideoCreated,
  onResourceCreated,
}: AddResourceModalProps) {
  const [activeTab, setActiveTab] = useState<'VIDEO' | 'DOCUMENT'>('VIDEO');
  const [moduleId, setModuleId] = useState<string>('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  // YouTube Fields
  const [youtubeUrl, setYoutubeUrl] = useState('');
  const [extractedVideoId, setExtractedVideoId] = useState<string | null>(null);
  const [durationMinutes, setDurationMinutes] = useState('15');

  // Document Fields
  const [resourceType, setResourceType] = useState<'PDF' | 'DOCX' | 'PPTX'>('PDF');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

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
      setDescription('');
      setYoutubeUrl('');
      setExtractedVideoId(null);
      setSelectedFile(null);
      setModuleId(modules.length > 0 ? modules[0].id.toString() : '');
      setErrorMessage(null);
    }
  }, [isOpen, modules]);

  // Live YouTube ID Extraction
  useEffect(() => {
    if (!youtubeUrl.trim()) {
      setExtractedVideoId(null);
      return;
    }
    const match = youtubeUrl.match(
      /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/(?:watch\?v=|embed\/|v\/|shorts\/)|youtu\.be\/)([a-zA-Z0-9_-]{11})/
    );
    if (match) {
      setExtractedVideoId(match[1]);
      setErrorMessage(null);
    } else if (youtubeUrl.trim().length === 11 && /^[a-zA-Z0-9_-]{11}$/.test(youtubeUrl.trim())) {
      setExtractedVideoId(youtubeUrl.trim());
      setErrorMessage(null);
    } else {
      setExtractedVideoId(null);
    }
  }, [youtubeUrl]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      setErrorMessage('Title is required.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      if (activeTab === 'VIDEO') {
        if (!extractedVideoId) {
          setErrorMessage('Please provide a valid YouTube URL (e.g., https://www.youtube.com/watch?v=...)');
          setIsSubmitting(false);
          return;
        }

        const video = await lmsApi.addVideo(courseId, {
          moduleId: moduleId ? parseInt(moduleId) : undefined,
          title: title.trim(),
          description: description.trim(),
          youtubeUrl: youtubeUrl.trim(),
          durationSeconds: (parseInt(durationMinutes) || 10) * 60,
          displayOrder: 1,
        });

        onVideoCreated(video);
        onClose();
      } else {
        // Document submission
        let resource: LearningResource;
        if (selectedFile) {
          const formData = new FormData();
          if (moduleId) formData.append('moduleId', moduleId);
          formData.append('title', title.trim());
          if (description.trim()) formData.append('description', description.trim());
          formData.append('file', selectedFile);

          resource = await lmsApi.uploadResource(courseId, formData);
        } else {
          resource = await lmsApi.createResource(courseId, {
            moduleId: moduleId ? parseInt(moduleId) : undefined,
            title: title.trim(),
            description: description.trim(),
            resourceType,
            downloadable: true,
          });
        }

        onResourceCreated(resource);
        onClose();
      }
    } catch (err: unknown) {
      const errorMsg =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
        'Error saving learning material. Please try again.';
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
            <span className="p-2 rounded-lg bg-red-100 text-red-600">
              <Video className="w-5 h-5" />
            </span>
            <div className="truncate">
              <h3 className="text-base font-bold text-slate-800 truncate">Add Learning Content</h3>
              <p className="text-xs text-slate-500">Integrate YouTube video lectures or upload PDF/notes</p>
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

        {/* Tab Selection */}
        <div className="grid grid-cols-2 border-b border-slate-200 bg-slate-100/70 p-1">
          <button
            type="button"
            onClick={() => setActiveTab('VIDEO')}
            className={`py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all ${
              activeTab === 'VIDEO'
                ? 'bg-white text-red-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Play className="w-4 h-4 fill-current" />
            YouTube Video Lecture
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('DOCUMENT')}
            className={`py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all ${
              activeTab === 'DOCUMENT'
                ? 'bg-white text-purple-700 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <FileText className="w-4 h-4" />
            Study Notes / PDF Handout
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-4 flex-1">
          {/* Module Selector */}
          {modules.length > 0 && (
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                Target Curriculum Module / Chapter
              </label>
              <select
                value={moduleId}
                onChange={(e) => setModuleId(e.target.value)}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all bg-white"
              >
                <option value="">General Course Material</option>
                {modules.map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.title}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Title */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Title *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder={
                activeTab === 'VIDEO'
                  ? 'e.g. Understanding Complex Numbers: Real & Imaginary Parts'
                  : 'e.g. Unit 1 Solved Exercise & Formula Summary'
              }
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400"
            />
          </div>

          {/* Tab Specific Inputs */}
          {activeTab === 'VIDEO' ? (
            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  YouTube Video Link / URL *
                </label>
                <div className="relative">
                  <input
                    type="url"
                    required
                    value={youtubeUrl}
                    onChange={(e) => setYoutubeUrl(e.target.value)}
                    placeholder="https://www.youtube.com/watch?v=... or youtu.be/..."
                    className="w-full pl-9 pr-3.5 py-2.5 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-red-500/20 focus:border-red-600 transition-all font-mono"
                  />
                  <LinkIcon className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                </div>
                <p className="text-[11px] text-slate-500 mt-1">
                  Supports standard watch links, youtu.be short links, embed links, and YouTube shorts.
                </p>
              </div>

              {/* YouTube Preview Card */}
              {extractedVideoId && (
                <div className="p-3 rounded-xl bg-slate-900 border border-slate-800 text-white flex items-center gap-3">
                  <div className="relative w-28 aspect-video rounded-lg overflow-hidden bg-black flex-shrink-0">
                    <img
                      src={`https://img.youtube.com/vi/${extractedVideoId}/mqdefault.jpg`}
                      alt="Thumbnail preview"
                      className="w-full h-full object-cover"
                    />
                    <div className="absolute inset-0 bg-black/30 flex items-center justify-center">
                      <Play className="w-6 h-6 text-white fill-white" />
                    </div>
                  </div>
                  <div className="min-w-0 text-xs">
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-red-600 text-white">
                      Valid YouTube ID: {extractedVideoId}
                    </span>
                    <p className="text-slate-300 mt-1 truncate">Ready for student viewing & watch tracking</p>
                  </div>
                </div>
              )}

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5 flex items-center gap-1.5">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  Estimated Lecture Duration (Minutes)
                </label>
                <input
                  type="number"
                  min="1"
                  max="180"
                  value={durationMinutes}
                  onChange={(e) => setDurationMinutes(e.target.value)}
                  className="w-32 px-3 py-2 rounded-xl border border-slate-300 text-xs sm:text-sm font-bold text-slate-800"
                />
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  Document Type
                </label>
                <div className="flex items-center gap-4 text-xs font-medium text-slate-700">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="doctype"
                      value="PDF"
                      checked={resourceType === 'PDF'}
                      onChange={() => setResourceType('PDF')}
                    />
                    <span>PDF Notes / Manual</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="doctype"
                      value="DOCX"
                      checked={resourceType === 'DOCX'}
                      onChange={() => setResourceType('DOCX')}
                    />
                    <span>Word Document (DOCX)</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="doctype"
                      value="PPTX"
                      checked={resourceType === 'PPTX'}
                      onChange={() => setResourceType('PPTX')}
                    />
                    <span>Slide Deck (PPTX)</span>
                  </label>
                </div>
              </div>

              {/* File Dropzone */}
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
                  Upload File
                </label>
                <div
                  onClick={() => fileInputRef.current?.click()}
                  className="border-2 border-dashed border-purple-300 hover:border-purple-500 bg-purple-50/30 hover:bg-purple-50/60 rounded-xl p-6 text-center cursor-pointer transition-all group"
                >
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept=".pdf,.doc,.docx,.ppt,.pptx"
                    onChange={(e) => {
                      if (e.target.files && e.target.files[0]) {
                        setSelectedFile(e.target.files[0]);
                        if (!title) {
                          setTitle(e.target.files[0].name.replace(/\.[^/.]+$/, ''));
                        }
                      }
                    }}
                    className="hidden"
                  />
                  <div className="w-10 h-10 rounded-full bg-purple-100 text-purple-600 flex items-center justify-center mx-auto mb-2 group-hover:scale-110 transition-transform">
                    <Upload className="w-5 h-5" />
                  </div>
                  {selectedFile ? (
                    <div>
                      <p className="text-xs font-bold text-purple-900">{selectedFile.name}</p>
                      <p className="text-[11px] text-slate-500">{(selectedFile.size / 1024 / 1024).toFixed(2)} MB</p>
                    </div>
                  ) : (
                    <div>
                      <p className="text-xs font-bold text-slate-800">Click to browse or drop document</p>
                      <p className="text-[11px] text-slate-500 mt-0.5">Supports PDF, DOCX, and PPTX up to 25MB</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Description */}
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1.5">
              Description & Study Goals (Optional)
            </label>
            <textarea
              rows={2}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Summary of topics covered, reference chapter page numbers, and key concepts..."
              className="w-full px-3.5 py-2 rounded-xl border border-slate-300 text-xs sm:text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-600 transition-all placeholder:text-slate-400 resize-none"
            />
          </div>

          {/* Error Message */}
          {errorMessage && (
            <div className="p-3 rounded-lg bg-red-50 border border-red-200 text-xs text-red-700 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          {/* Actions */}
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
              className={`px-6 py-2.5 rounded-xl text-xs font-bold text-white shadow-md flex items-center gap-2 disabled:opacity-50 transition-all ${
                activeTab === 'VIDEO'
                  ? 'bg-red-600 hover:bg-red-700 shadow-red-600/20'
                  : 'bg-purple-600 hover:bg-purple-700 shadow-purple-600/20'
              }`}
            >
              <CheckCircle className="w-4 h-4" />
              {isSubmitting ? 'Saving...' : activeTab === 'VIDEO' ? 'Integrate Video' : 'Add Material'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
