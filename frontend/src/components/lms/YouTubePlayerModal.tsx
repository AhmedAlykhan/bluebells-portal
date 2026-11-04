import { useEffect } from 'react';
import { X, CheckCircle, ExternalLink, Clock, Play } from 'lucide-react';
import { YouTubeVideo } from '../../types/lms';

interface YouTubePlayerModalProps {
  video: YouTubeVideo;
  isOpen: boolean;
  onClose: () => void;
  isCompleted?: boolean;
  onMarkCompleted?: () => void;
}

export function YouTubePlayerModal({
  video,
  isOpen,
  onClose,
  isCompleted = false,
  onMarkCompleted,
}: YouTubePlayerModalProps) {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen || !video) return null;

  const formatDuration = (seconds: number) => {
    if (!seconds) return '10m 00s';
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}m ${s < 10 ? '0' : ''}${s}s`;
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-6 bg-slate-900/80 backdrop-blur-sm transition-opacity"
      onClick={onClose}
    >
      <div
        className="relative w-full max-w-4xl bg-white rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[92vh] border border-slate-200"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200 bg-slate-50">
          <div className="flex items-center gap-3 min-w-0">
            <span className="p-2 rounded-lg bg-red-100 text-red-600">
              <Play className="w-5 h-5 fill-red-600" />
            </span>
            <div className="truncate">
              <h3 className="text-base font-bold text-slate-800 truncate">{video.title}</h3>
              <p className="text-xs text-slate-500 flex items-center gap-2 mt-0.5">
                <span className="font-medium text-slate-600">{video.moduleTitle || 'Course Curriculum'}</span>
                <span>•</span>
                <span className="flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  {formatDuration(video.durationSeconds)}
                </span>
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

        {/* Video Player (16:9 Aspect Ratio) */}
        <div className="relative w-full bg-black aspect-video flex-shrink-0">
          <iframe
            src={`https://www.youtube.com/embed/${video.videoId}?autoplay=1&rel=0&modestbranding=1`}
            title={video.title}
            className="absolute inset-0 w-full h-full border-0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          />
        </div>

        {/* Video Details & Actions */}
        <div className="p-6 overflow-y-auto space-y-4 bg-white flex-1">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-100 text-red-700">
                  YouTube Educational Lecture
                </span>
                {isCompleted && (
                  <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700 flex items-center gap-1">
                    <CheckCircle className="w-3.5 h-3.5" />
                    Completed & Watched
                  </span>
                )}
              </div>
              <h4 className="text-lg font-bold text-slate-800 mt-1">{video.title}</h4>
            </div>

            <div className="flex items-center gap-2 flex-wrap">
              {onMarkCompleted && (
                <button
                  type="button"
                  onClick={onMarkCompleted}
                  className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 shadow-sm ${
                    isCompleted
                      ? 'bg-slate-100 text-slate-600 hover:bg-slate-200 border border-slate-300'
                      : 'bg-emerald-600 hover:bg-emerald-700 text-white'
                  }`}
                >
                  <CheckCircle className="w-4 h-4" />
                  {isCompleted ? 'Mark as Incomplete' : 'Mark Lesson as Watched'}
                </button>
              )}

              <a
                href={video.youtubeUrl}
                target="_blank"
                rel="noreferrer"
                className="px-3 py-2 rounded-lg text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 border border-slate-200 flex items-center gap-1.5 transition-colors"
              >
                <ExternalLink className="w-3.5 h-3.5" />
                Open on YouTube
              </a>
            </div>
          </div>

          {video.description && (
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 text-xs sm:text-sm text-slate-600 leading-relaxed">
              <p className="font-semibold text-slate-800 mb-1">Lecture Overview & Study Objectives:</p>
              {video.description}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
