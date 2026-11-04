import { useState, useEffect } from 'react';
import {
  X,
  Download,
  ZoomIn,
  ZoomOut,
  RotateCw,
  Maximize2,
  Minimize2,
  FileText,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';
import { LearningResource } from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';

interface PdfViewerModalProps {
  resource: LearningResource | null;
  isOpen: boolean;
  onClose: () => void;
}

export function PdfViewerModal({ resource, isOpen, onClose }: PdfViewerModalProps) {
  const [zoom, setZoom] = useState<number>(100);
  const [rotation, setRotation] = useState<number>(0);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);
  const totalPages = 5;

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
      setZoom(100);
      setRotation(0);
      setCurrentPage(1);
    }
  }, [isOpen, resource]);

  if (!isOpen || !resource) return null;

  const handleZoomIn = () => setZoom((prev) => Math.min(prev + 20, 200));
  const handleZoomOut = () => setZoom((prev) => Math.max(prev - 20, 60));
  const handleRotate = () => setRotation((prev) => (prev + 90) % 360);
  const handlePrevPage = () => setCurrentPage((prev) => Math.max(prev - 1, 1));
  const handleNextPage = () => setCurrentPage((prev) => Math.min(prev + 1, totalPages));
  const toggleFullscreen = () => setIsFullscreen((prev) => !prev);

  const downloadUrl = resource.filePath 
    ? lmsApi.getFileStreamUrl(resource.filePath, true)
    : (resource.externalUrl || '#');

  const previewUrl = resource.filePath 
    ? lmsApi.getFileStreamUrl(resource.filePath, false)
    : (resource.externalUrl || null);

  return (
    <div
      className={`fixed inset-0 z-50 flex items-center justify-center ${
        isFullscreen ? 'p-0' : 'p-3 sm:p-6'
      } bg-slate-900/80 backdrop-blur-sm transition-all`}
      onClick={onClose}
    >
      <div
        className={`relative w-full ${
          isFullscreen ? 'h-full max-w-none rounded-none' : 'max-w-5xl max-h-[94vh] rounded-2xl'
        } bg-slate-800 shadow-2xl overflow-hidden flex flex-col border border-slate-700`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header Toolbar */}
        <div className="flex items-center justify-between px-5 py-3.5 bg-slate-900 border-b border-slate-700 text-white flex-wrap gap-2">
          <div className="flex items-center gap-3 min-w-0">
            <span className="p-2 rounded-lg bg-purple-900/60 text-purple-300">
              <FileText className="w-5 h-5" />
            </span>
            <div className="truncate">
              <h3 className="text-sm sm:text-base font-bold text-white truncate">{resource.title}</h3>
              <p className="text-xs text-slate-400 truncate">
                {resource.originalFilename || 'Document.pdf'} •{' '}
                {resource.fileSizeBytes ? `${(resource.fileSizeBytes / 1024 / 1024).toFixed(2)} MB` : 'PDF Document'}
              </p>
            </div>
          </div>

          {/* Viewer Controls */}
          <div className="flex items-center gap-1.5 sm:gap-2 flex-wrap">
            {/* Page navigation */}
            <div className="flex items-center bg-slate-800 rounded-lg border border-slate-700 px-2 py-1 text-xs text-slate-300">
              <button
                onClick={handlePrevPage}
                disabled={currentPage <= 1}
                className="p-1 hover:text-white disabled:opacity-40 transition-opacity"
                title="Previous Page"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <span className="px-2 font-mono">
                {currentPage} / {totalPages}
              </span>
              <button
                onClick={handleNextPage}
                disabled={currentPage >= totalPages}
                className="p-1 hover:text-white disabled:opacity-40 transition-opacity"
                title="Next Page"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>

            {/* Zoom Controls */}
            <div className="flex items-center bg-slate-800 rounded-lg border border-slate-700 px-1 py-1 text-xs">
              <button
                onClick={handleZoomOut}
                className="p-1.5 hover:text-white text-slate-300 hover:bg-slate-700 rounded transition-colors"
                title="Zoom Out"
              >
                <ZoomOut className="w-4 h-4" />
              </button>
              <span className="px-2 font-mono text-slate-300">{zoom}%</span>
              <button
                onClick={handleZoomIn}
                className="p-1.5 hover:text-white text-slate-300 hover:bg-slate-700 rounded transition-colors"
                title="Zoom In"
              >
                <ZoomIn className="w-4 h-4" />
              </button>
            </div>

            {/* Rotate */}
            <button
              onClick={handleRotate}
              className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-lg border border-slate-700 transition-colors"
              title="Rotate 90 degrees"
            >
              <RotateCw className="w-4 h-4" />
            </button>

            {/* Fullscreen */}
            <button
              onClick={toggleFullscreen}
              className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-lg border border-slate-700 transition-colors hidden sm:flex"
              title={isFullscreen ? 'Exit Fullscreen' : 'Fullscreen'}
            >
              {isFullscreen ? <Minimize2 className="w-4 h-4" /> : <Maximize2 className="w-4 h-4" />}
            </button>

            {/* Download */}
            <a
              href={downloadUrl}
              download={resource.originalFilename || 'document.pdf'}
              className="px-3 py-1.5 bg-purple-600 hover:bg-purple-700 text-white text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors shadow-sm"
              title="Download Document"
            >
              <Download className="w-3.5 h-3.5" />
              Download
            </a>

            {/* Close */}
            <button
              onClick={onClose}
              className="p-2 bg-slate-800 hover:bg-red-600/80 text-slate-300 hover:text-white rounded-lg border border-slate-700 transition-colors ml-1"
              title="Close (Esc)"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Document Render Canvas */}
        <div className="flex-1 overflow-auto bg-slate-900 p-6 flex items-center justify-center">
          <div
            style={{
              transform: `scale(${zoom / 100}) rotate(${rotation}deg)`,
              transformOrigin: 'top center',
              transition: 'transform 0.15s ease-out',
            }}
            className="w-full max-w-3xl bg-white rounded-xl shadow-2xl p-8 sm:p-12 text-slate-800 border border-slate-200 select-text min-h-[700px]"
          >
            {/* Document Header */}
            <div className="border-b-2 border-purple-800 pb-4 mb-6 flex items-start justify-between">
              <div>
                <p className="text-xs font-bold text-purple-700 tracking-wider uppercase">
                  Bluebells Schooling System • Academic Session 2026-2027
                </p>
                <h1 className="text-xl sm:text-2xl font-black text-slate-900 mt-1">{resource.title}</h1>
                <p className="text-xs text-slate-500 mt-1">
                  Matriculation Wing • Latifabad Unit 2, Hyderabad • Instructor: {resource.uploadedByName || 'Faculty'}
                </p>
              </div>
              <span className="px-3 py-1 rounded bg-purple-100 text-purple-800 text-xs font-bold uppercase">
                {resource.resourceType}
              </span>
            </div>

            {/* Document Content or Embedded PDF */}
            {previewUrl && resource.filePath ? (
              <div className="w-full">
                <iframe
                  src={previewUrl}
                  title={resource.title}
                  className="w-full h-[650px] border-0 rounded-lg shadow-inner"
                />
              </div>
            ) : (
            <div className="space-y-6 text-sm text-slate-700 leading-relaxed font-serif">
              {currentPage === 1 && (
                <>
                  <div className="bg-purple-50/60 p-4 rounded-lg border border-purple-200 text-xs">
                    <p className="font-bold text-purple-900">STUDENT LEARNING OBJECTIVES (SLOs):</p>
                    <ul className="list-disc pl-5 mt-1 space-y-1 text-purple-800">
                      <li>Understand mathematical rigor and foundations according to Sindh Board syllabus.</li>
                      <li>Distinguish between sets of numbers: Natural (N), Whole (W), Integers (Z), Rational (Q), and Irrational (Q').</li>
                      <li>Master the properties of the imaginary unit <span className="font-mono font-bold">i = √(-1)</span> and complex conjugate operations.</li>
                    </ul>
                  </div>

                  <div>
                    <h2 className="text-base font-bold text-slate-900 mb-2">Section 1.1: Complex Conjugates and Modulus</h2>
                    <p>
                      Let <span className="font-mono font-bold">z = a + bi</span> where <span className="font-mono">a, b ∈ ℝ</span>. 
                      The number <span className="font-mono font-bold">a</span> is termed the <em>Real part</em>, denoted as <span className="font-mono">Re(z)</span>, 
                      and <span className="font-mono font-bold">b</span> is the <em>Imaginary part</em>, denoted as <span className="font-mono">Im(z)</span>.
                    </p>
                    <p className="mt-3">
                      The conjugate of <span className="font-mono">z</span> is defined as <span className="font-mono font-bold">z̄ = a - bi</span>.
                      Graphically, on the Argand plane, taking the conjugate corresponds to reflection across the real horizontal axis.
                    </p>
                  </div>

                  <div className="p-4 bg-slate-50 border border-slate-200 rounded-lg">
                    <p className="font-bold text-slate-800 text-xs uppercase tracking-wide">Fundamental Conjugate Theorem:</p>
                    <div className="font-mono text-center text-base font-bold text-purple-900 my-2">
                      z · z̄ = (a + bi)(a - bi) = a² - (bi)² = a² + b² = |z|²
                    </div>
                    <p className="text-xs text-slate-500 text-center">
                      The product of any complex number and its conjugate is strictly a non-negative real number.
                    </p>
                  </div>
                </>
              )}

              {currentPage === 2 && (
                <>
                  <h2 className="text-base font-bold text-slate-900 mb-2">Section 1.2: Worked Step-by-Step Proofs</h2>
                  <p>
                    <strong>Example 1:</strong> Simplify the fraction into standard form <span className="font-mono">a + bi</span>:
                  </p>
                  <div className="font-mono text-center text-sm font-bold bg-slate-50 p-3 rounded border border-slate-200 text-slate-800">
                    z = (3 + 4i) / (1 - 2i)
                  </div>
                  <p className="text-xs font-semibold text-slate-600 mt-2">Solution Procedure:</p>
                  <ol className="list-decimal pl-5 text-xs space-y-1.5 text-slate-700">
                    <li>Identify the conjugate of the denominator: <span className="font-mono">1 + 2i</span>.</li>
                    <li>Multiply both numerator and denominator by <span className="font-mono">(1 + 2i)</span>.</li>
                    <li>Numerator: <span className="font-mono">(3 + 4i)(1 + 2i) = 3 + 6i + 4i + 8i² = 3 + 10i - 8 = -5 + 10i</span>.</li>
                    <li>Denominator: <span className="font-mono">(1 - 2i)(1 + 2i) = 1² + 2² = 1 + 4 = 5</span>.</li>
                    <li>Final Result: <span className="font-mono font-bold text-purple-900">(-5 + 10i) / 5 = -1 + 2i</span>.</li>
                  </ol>
                </>
              )}

              {currentPage >= 3 && (
                <div className="py-12 text-center text-slate-500 space-y-3">
                  <p className="text-base font-bold text-slate-700">Class 9 Matriculation Document Continuation</p>
                  <p className="text-xs">
                    Page {currentPage} of {totalPages} contains extended reference exercise tables, logarithmic indices tables, and homework assignment guidelines.
                  </p>
                  <a
                    href={downloadUrl}
                    download={resource.originalFilename || 'document.pdf'}
                    className="inline-flex items-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-lg text-xs font-semibold shadow-sm transition-colors"
                  >
                    <Download className="w-4 h-4" />
                    Download Full Complete Original File
                  </a>
                </div>
              )}
            </div>
            )}

            {/* Document Footer */}
            <div className="border-t border-slate-200 pt-4 mt-8 flex items-center justify-between text-xs text-slate-400">
              <span>Bluebells Matriculation Curriculum</span>
              <span>Page {currentPage} of {totalPages}</span>
              <span>Confidential & Academic Copy</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
