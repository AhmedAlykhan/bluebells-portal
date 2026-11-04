import { useState, useEffect } from 'react';
import {
  BookOpen,
  Video,
  FileText,
  CheckCircle,
  Clock,
  Award,
  ChevronRight,
  ChevronDown,
  Play,
  Download,
  Plus,
  ArrowLeft,
  Calendar,
  Eye,
  Folder,
  Layers,
  Sparkles,
  Camera
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import {
  Course,
  CourseModule,
  Lesson,
  LearningResource,
  YouTubeVideo,
  Assignment,
  AssignmentSubmission
} from '../../types/lms';
import { lmsApi } from '../../api/lmsApi';
import {
  INITIAL_COURSES,
  INITIAL_MODULES,
  INITIAL_LESSONS,
  INITIAL_RESOURCES,
  INITIAL_VIDEOS,
  INITIAL_ASSIGNMENTS,
  INITIAL_SUBMISSIONS_ROSTER
} from '../../data/lmsData';

import { YouTubePlayerModal } from '../../components/lms/YouTubePlayerModal';
import { PdfViewerModal } from '../../components/lms/PdfViewerModal';
import { SubmitAssignmentModal } from '../../components/lms/SubmitAssignmentModal';
import { TeacherGradingModal } from '../../components/lms/TeacherGradingModal';
import { CreateAssignmentModal } from '../../components/lms/CreateAssignmentModal';
import { AddResourceModal } from '../../components/lms/AddResourceModal';
import { CreateModuleModal } from '../../components/lms/CreateModuleModal';

export function LmsPage() {
  const { user } = useAuth();
  const isTeacher = user?.roles?.some((r) => ['ROLE_TEACHER', 'ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL'].includes(r));
  const isStudent = user?.roles?.includes('ROLE_STUDENT');
  const isParent = user?.roles?.includes('ROLE_PARENT');

  // State
  const [courses, setCourses] = useState<Course[]>(INITIAL_COURSES);
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);
  const [modules, setModules] = useState<CourseModule[]>(INITIAL_MODULES);
  const [lessons, setLessons] = useState<Lesson[]>(INITIAL_LESSONS);
  const [resources, setResources] = useState<LearningResource[]>(INITIAL_RESOURCES);
  const [videos, setVideos] = useState<YouTubeVideo[]>(INITIAL_VIDEOS);
  const [assignments, setAssignments] = useState<Assignment[]>(INITIAL_ASSIGNMENTS);
  const [submissionsRoster, setSubmissionsRoster] = useState<AssignmentSubmission[]>(INITIAL_SUBMISSIONS_ROSTER);

  const [activeTab, setActiveTab] = useState<'CURRICULUM' | 'MATERIALS' | 'ASSIGNMENTS' | 'GRADING'>('CURRICULUM');
  const [selectedSubjectFilter, setSelectedSubjectFilter] = useState<string>('ALL');
  const [expandedModules, setExpandedModules] = useState<Record<number, boolean>>({ 1: true, 2: true, 4: true, 6: true });

  // Modal States
  const [activeVideo, setActiveVideo] = useState<YouTubeVideo | null>(null);
  const [activePdfResource, setActivePdfResource] = useState<LearningResource | null>(null);
  const [activeAssignmentToSubmit, setActiveAssignmentToSubmit] = useState<Assignment | null>(null);
  const [activeSubmissionToGrade, setActiveSubmissionToGrade] = useState<AssignmentSubmission | null>(null);
  const [showCreateAssignmentModal, setShowCreateAssignmentModal] = useState<boolean>(false);
  const [showAddResourceModal, setShowAddResourceModal] = useState<boolean>(false);
  const [showCreateModuleModal, setShowCreateModuleModal] = useState<boolean>(false);

  // Fetch live courses on mount
  useEffect(() => {
    async function loadData() {
      try {
        const liveCourses = await lmsApi.getAllCourses();
        if (liveCourses && liveCourses.length > 0) {
          setCourses(liveCourses);
        }
      } catch (err) {
        // Fallback to rich baseline data
        setCourses(INITIAL_COURSES);
      }
    }
    loadData();
  }, []);

  // When a course is selected, load its modules, lessons, resources, and assignments
  useEffect(() => {
    if (!selectedCourse) return;

    async function loadCourseContent() {
      try {
        const [liveMods, liveLessons, liveRes, liveVideos, liveAssignments] = await Promise.allSettled([
          lmsApi.getModules(selectedCourse!.id),
          lmsApi.getLessons(selectedCourse!.id, isStudent ? 1 : undefined),
          lmsApi.getResources(selectedCourse!.id),
          lmsApi.getVideos(selectedCourse!.id),
          lmsApi.getAssignments(selectedCourse!.id, isStudent ? 1 : undefined),
        ]);

        if (liveMods.status === 'fulfilled' && liveMods.value && liveMods.value.length > 0) {
          setModules(liveMods.value);
        } else {
          setModules(INITIAL_MODULES.filter((m) => m.courseId === selectedCourse!.id));
        }

        if (liveLessons.status === 'fulfilled' && liveLessons.value && liveLessons.value.length > 0) {
          setLessons(liveLessons.value);
        } else {
          setLessons(INITIAL_LESSONS.filter((l) => l.courseId === selectedCourse!.id));
        }

        if (liveRes.status === 'fulfilled' && liveRes.value && liveRes.value.length > 0) {
          setResources(liveRes.value);
        } else {
          setResources(INITIAL_RESOURCES.filter((r) => r.courseId === selectedCourse!.id));
        }

        if (liveVideos.status === 'fulfilled' && liveVideos.value && liveVideos.value.length > 0) {
          setVideos(liveVideos.value);
        } else {
          setVideos(INITIAL_VIDEOS.filter((v) => v.courseId === selectedCourse!.id));
        }

        if (liveAssignments.status === 'fulfilled' && liveAssignments.value && liveAssignments.value.length > 0) {
          setAssignments(liveAssignments.value);
        } else {
          setAssignments(INITIAL_ASSIGNMENTS.filter((a) => a.courseId === selectedCourse!.id));
        }
      } catch (e) {
        // Fallback filter
        setModules(INITIAL_MODULES.filter((m) => m.courseId === selectedCourse!.id));
        setLessons(INITIAL_LESSONS.filter((l) => l.courseId === selectedCourse!.id));
        setResources(INITIAL_RESOURCES.filter((r) => r.courseId === selectedCourse!.id));
        setVideos(INITIAL_VIDEOS.filter((v) => v.courseId === selectedCourse!.id));
        setAssignments(INITIAL_ASSIGNMENTS.filter((a) => a.courseId === selectedCourse!.id));
      }
    }

    loadCourseContent();
  }, [selectedCourse, isStudent]);

  const toggleModule = (modId: number) => {
    setExpandedModules((prev) => ({ ...prev, [modId]: !prev[modId] }));
  };

  const handleLessonCompletionToggle = async (lesson: Lesson) => {
    const newCompleted = !lesson.completed;
    // Optimistic UI update
    setLessons((prev) =>
      prev.map((l) => (l.id === lesson.id ? { ...l, completed: newCompleted } : l))
    );

    try {
      await lmsApi.markLessonProgress(lesson.id, 1, newCompleted);
    } catch (e) {
      // Local state is preserved
    }

    // Update course completion percentage
    if (selectedCourse) {
      const courseLessons = lessons.map((l) => (l.id === lesson.id ? { ...l, completed: newCompleted } : l));
      const completedCount = courseLessons.filter((l) => l.completed).length;
      const pct = courseLessons.length > 0 ? (completedCount / courseLessons.length) * 100 : 0;

      setSelectedCourse((prev) => (prev ? { ...prev, completionPercentage: Math.round(pct) } : null));
      setCourses((prev) =>
        prev.map((c) => (c.id === selectedCourse.id ? { ...c, completionPercentage: Math.round(pct) } : c))
      );
    }
  };

  const handleSubmissionSuccess = (submission: AssignmentSubmission) => {
    setAssignments((prev) =>
      prev.map((a) =>
        a.id === submission.assignmentId
          ? { ...a, studentSubmission: submission, submissionCount: a.submissionCount + 1 }
          : a
      )
    );
    setSubmissionsRoster((prev) => {
      const existingIdx = prev.findIndex((s) => s.id === submission.id);
      if (existingIdx >= 0) {
        const next = [...prev];
        next[existingIdx] = submission;
        return next;
      }
      return [submission, ...prev];
    });
  };

  const handleGradedSuccess = (updated: AssignmentSubmission) => {
    setSubmissionsRoster((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
    setAssignments((prev) =>
      prev.map((a) =>
        a.id === updated.assignmentId
          ? {
              ...a,
              studentSubmission:
                a.studentSubmission?.id === updated.id ? updated : a.studentSubmission,
              gradedCount: a.gradedCount + 1,
            }
          : a
      )
    );
  };

  const filteredCourses = courses.filter((c) => {
    if (selectedSubjectFilter === 'ALL') return true;
    return c.subjectCode.startsWith(selectedSubjectFilter);
  });

  return (
    <div className="space-y-6">
      {/* =========================================================================
          VIEW 1: COURSE CATALOG / MY COURSES (When no course selected)
         ========================================================================= */}
      {!selectedCourse ? (
        <div className="space-y-6">
          {/* Top Banner */}
          <div className="bg-gradient-to-r from-purple-900 via-indigo-900 to-slate-900 rounded-2xl p-6 sm:p-8 text-white shadow-xl relative overflow-hidden">
            <div className="absolute right-0 top-0 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl pointer-events-none" />
            <div className="relative z-10 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
              <div className="space-y-2 max-w-2xl">
                <span className="px-3 py-1 rounded-full text-xs font-bold bg-purple-400/20 text-purple-200 border border-purple-400/30 uppercase tracking-wider inline-flex items-center gap-1.5">
                  <Sparkles className="w-3.5 h-3.5 text-purple-300" />
                  Bluebells Learning Management System
                </span>
                <h1 className="text-2xl sm:text-3xl font-black tracking-tight">
                  {isStudent
                    ? `My Matriculation Coursework & Learning Modules`
                    : isParent
                    ? `Ward's Courseware & Academic Learning Portal`
                    : isTeacher
                    ? `Courseware, Curricula & Student Grading Hub`
                    : `Academic Courses & Digital Learning Portal`}
                </h1>
                <p className="text-sm text-purple-200/80 leading-relaxed">
                  Interactive video lectures, PDF notes, laboratory worksheets, and multi-page handwritten homework submission for Class 9 Matriculation.
                </p>
              </div>

              {/* Quick Persona Stats */}
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 w-full md:w-auto flex-shrink-0">
                <div className="bg-white/10 backdrop-blur-md rounded-xl p-3 border border-white/15 text-center">
                  <p className="text-2xl font-black">{courses.length}</p>
                  <p className="text-[11px] text-purple-200/80 font-medium">Published Courses</p>
                </div>
                <div className="bg-white/10 backdrop-blur-md rounded-xl p-3 border border-white/15 text-center">
                  <p className="text-2xl font-black">
                    {assignments.reduce((sum, a) => sum + (a.submissionCount || 0), 0)}
                  </p>
                  <p className="text-[11px] text-purple-200/80 font-medium">Submissions Received</p>
                </div>
                <div className="bg-white/10 backdrop-blur-md rounded-xl p-3 border border-white/15 text-center col-span-2 sm:col-span-1">
                  <p className="text-2xl font-black text-emerald-300">
                    {isStudent ? '50%' : '100%'}
                  </p>
                  <p className="text-[11px] text-purple-200/80 font-medium">
                    {isStudent ? 'Syllabus Progress' : 'System Active'}
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Subject Filter Tabs */}
          <div className="flex items-center gap-2 overflow-x-auto pb-1">
            <button
              onClick={() => setSelectedSubjectFilter('ALL')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-sm flex items-center gap-1.5 ${
                selectedSubjectFilter === 'ALL'
                  ? 'bg-purple-700 text-white'
                  : 'bg-white hover:bg-slate-100 text-slate-700 border border-slate-200'
              }`}
            >
              All Subjects ({courses.length})
            </button>
            <button
              onClick={() => setSelectedSubjectFilter('MATH')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-sm ${
                selectedSubjectFilter === 'MATH'
                  ? 'bg-purple-700 text-white'
                  : 'bg-white hover:bg-slate-100 text-slate-700 border border-slate-200'
              }`}
            >
              Mathematics
            </button>
            <button
              onClick={() => setSelectedSubjectFilter('PHY')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-sm ${
                selectedSubjectFilter === 'PHY'
                  ? 'bg-purple-700 text-white'
                  : 'bg-white hover:bg-slate-100 text-slate-700 border border-slate-200'
              }`}
            >
              Physics
            </button>
            <button
              onClick={() => setSelectedSubjectFilter('CS')}
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-sm ${
                selectedSubjectFilter === 'CS'
                  ? 'bg-purple-700 text-white'
                  : 'bg-white hover:bg-slate-100 text-slate-700 border border-slate-200'
              }`}
            >
              Computer Science
            </button>
          </div>

          {/* Course Cards Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredCourses.map((course) => (
              <div
                key={course.id}
                className="bg-white rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-all flex flex-col justify-between overflow-hidden group"
              >
                <div>
                  {/* Card Top Banner */}
                  <div className="p-5 pb-3">
                    <div className="flex items-center justify-between gap-2">
                      <span className="px-2.5 py-0.5 rounded-md text-xs font-bold bg-purple-50 text-purple-700 border border-purple-200">
                        {course.subjectCode}
                      </span>
                      <span className="px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        {course.status}
                      </span>
                    </div>

                    <h3 className="text-lg font-bold text-slate-900 mt-3 group-hover:text-purple-700 transition-colors">
                      {course.title}
                    </h3>
                    <p className="text-xs text-slate-500 mt-1">
                      Instructor: <strong className="text-slate-700">{course.teacherName}</strong> • {course.sectionName}
                    </p>

                    <p className="text-xs text-slate-600 mt-3 line-clamp-2 leading-relaxed">
                      {course.description}
                    </p>
                  </div>

                  {/* Progress Bar (Student Persona) */}
                  <div className="px-5 py-3 bg-slate-50/70 border-t border-b border-slate-100 space-y-1.5">
                    <div className="flex items-center justify-between text-xs">
                      <span className="font-semibold text-slate-700">Course Progress</span>
                      <span className="font-mono font-bold text-purple-700">
                        {course.completionPercentage || 0}%
                      </span>
                    </div>
                    <div className="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
                      <div
                        className="bg-purple-600 h-2 rounded-full transition-all duration-500"
                        style={{ width: `${course.completionPercentage || 0}%` }}
                      />
                    </div>
                  </div>

                  {/* Course Content Metrics */}
                  <div className="p-5 pt-3 grid grid-cols-4 gap-2 text-center text-xs">
                    <div className="p-2 rounded-lg bg-slate-50 border border-slate-100">
                      <p className="font-bold text-slate-800">{course.moduleCount}</p>
                      <p className="text-[10px] text-slate-400">Units</p>
                    </div>
                    <div className="p-2 rounded-lg bg-slate-50 border border-slate-100">
                      <p className="font-bold text-slate-800">{course.lessonCount}</p>
                      <p className="text-[10px] text-slate-400">Lessons</p>
                    </div>
                    <div className="p-2 rounded-lg bg-slate-50 border border-slate-100">
                      <p className="font-bold text-slate-800">{course.videoCount}</p>
                      <p className="text-[10px] text-slate-400">Videos</p>
                    </div>
                    <div className="p-2 rounded-lg bg-slate-50 border border-slate-100">
                      <p className="font-bold text-slate-800">{course.assignmentCount}</p>
                      <p className="text-[10px] text-slate-400">Tasks</p>
                    </div>
                  </div>
                </div>

                {/* Card Action */}
                <div className="p-5 pt-0">
                  <button
                    onClick={() => {
                      setSelectedCourse(course);
                      setActiveTab('CURRICULUM');
                    }}
                    className="w-full py-2.5 rounded-xl text-xs font-bold bg-purple-600 hover:bg-purple-700 text-white shadow-sm transition-all flex items-center justify-center gap-1.5"
                  >
                    <span>Enter Courseware</span>
                    <ChevronRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        /* =========================================================================
            VIEW 2: COURSE DEEP-DIVE (Curriculum, Resources, Homework, Grading)
           ========================================================================= */
        <div className="space-y-6">
          {/* Sticky Course Header */}
          <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div className="space-y-1 min-w-0">
              <button
                onClick={() => setSelectedCourse(null)}
                className="text-xs font-bold text-purple-700 hover:text-purple-900 flex items-center gap-1 mb-2 transition-colors"
              >
                <ArrowLeft className="w-3.5 h-3.5" />
                Back to All Courses
              </button>

              <div className="flex items-center gap-2 flex-wrap">
                <span className="px-2.5 py-0.5 rounded text-xs font-bold bg-purple-100 text-purple-800">
                  {selectedCourse.subjectCode}
                </span>
                <span className="text-xs text-slate-400">•</span>
                <span className="text-xs font-semibold text-slate-600">
                  {selectedCourse.className} - {selectedCourse.sectionName}
                </span>
                <span className="text-xs text-slate-400">•</span>
                <span className="text-xs text-slate-500">
                  Faculty: <strong className="text-slate-700">{selectedCourse.teacherName}</strong>
                </span>
              </div>

              <h2 className="text-xl sm:text-2xl font-black text-slate-900 truncate">
                {selectedCourse.title}
              </h2>
            </div>

            {/* Course Actions for Teachers/Admins */}
            <div className="flex items-center gap-2 flex-wrap w-full md:w-auto">
              {isTeacher && (
                <>
                  <button
                    onClick={() => setShowCreateModuleModal(true)}
                    className="px-3.5 py-2 rounded-xl text-xs font-bold bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 flex items-center gap-1.5 transition-colors"
                  >
                    <Folder className="w-4 h-4 text-slate-500" />
                    + Unit / Module
                  </button>

                  <button
                    onClick={() => setShowAddResourceModal(true)}
                    className="px-3.5 py-2 rounded-xl text-xs font-bold bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 flex items-center gap-1.5 transition-colors"
                  >
                    <Video className="w-4 h-4 text-red-600" />
                    + Video / Material
                  </button>

                  <button
                    onClick={() => setShowCreateAssignmentModal(true)}
                    className="px-4 py-2 rounded-xl text-xs font-bold bg-purple-600 hover:bg-purple-700 text-white shadow-sm flex items-center gap-1.5 transition-colors"
                  >
                    <Plus className="w-4 h-4" />
                    Publish Assignment
                  </button>
                </>
              )}
            </div>
          </div>

          {/* Tab Navigation */}
          <div className="flex items-center border-b border-slate-200 bg-white rounded-t-2xl px-4 pt-2 gap-2 overflow-x-auto">
            <button
              onClick={() => setActiveTab('CURRICULUM')}
              className={`py-3 px-4 text-xs sm:text-sm font-bold border-b-2 transition-all flex items-center gap-2 whitespace-nowrap ${
                activeTab === 'CURRICULUM'
                  ? 'border-purple-600 text-purple-700'
                  : 'border-transparent text-slate-500 hover:text-slate-800'
              }`}
            >
              <Layers className="w-4 h-4" />
              Curriculum & Lessons ({lessons.length})
            </button>

            <button
              onClick={() => setActiveTab('MATERIALS')}
              className={`py-3 px-4 text-xs sm:text-sm font-bold border-b-2 transition-all flex items-center gap-2 whitespace-nowrap ${
                activeTab === 'MATERIALS'
                  ? 'border-purple-600 text-purple-700'
                  : 'border-transparent text-slate-500 hover:text-slate-800'
              }`}
            >
              <FileText className="w-4 h-4" />
              Learning Notes & Manuals ({resources.length})
            </button>

            <button
              onClick={() => setActiveTab('ASSIGNMENTS')}
              className={`py-3 px-4 text-xs sm:text-sm font-bold border-b-2 transition-all flex items-center gap-2 whitespace-nowrap ${
                activeTab === 'ASSIGNMENTS'
                  ? 'border-purple-600 text-purple-700'
                  : 'border-transparent text-slate-500 hover:text-slate-800'
              }`}
            >
              <Award className="w-4 h-4" />
              Assignments & Tasks ({assignments.length})
            </button>

            {isTeacher && (
              <button
                onClick={() => setActiveTab('GRADING')}
                className={`py-3 px-4 text-xs sm:text-sm font-bold border-b-2 transition-all flex items-center gap-2 whitespace-nowrap ${
                  activeTab === 'GRADING'
                    ? 'border-purple-600 text-purple-700'
                    : 'border-transparent text-slate-500 hover:text-slate-800'
                }`}
              >
                <CheckCircle className="w-4 h-4 text-emerald-600" />
                Student Submissions & Grading ({submissionsRoster.length})
              </button>
            )}
          </div>

          {/* =====================================================================
              TAB 1: CURRICULUM & LESSONS (Accordion of Units + Videos + Notes)
             ===================================================================== */}
          {activeTab === 'CURRICULUM' && (
            <div className="space-y-4">
              {modules.length === 0 ? (
                <div className="p-12 text-center bg-white rounded-2xl border border-slate-200">
                  <BookOpen className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                  <p className="text-base font-bold text-slate-800">No Curriculum Modules Added Yet</p>
                  <p className="text-xs text-slate-500 mt-1">
                    Click "+ Unit / Module" to add chapters and organize lessons.
                  </p>
                </div>
              ) : (
                modules.map((module) => {
                  const isExpanded = !!expandedModules[module.id];
                  const moduleLessons = lessons.filter((l) => l.moduleId === module.id);
                  const moduleVideos = videos.filter((v) => v.moduleId === module.id);
                  const moduleResources = resources.filter((r) => r.moduleId === module.id);

                  return (
                    <div
                      key={module.id}
                      className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden"
                    >
                      {/* Module Header Bar */}
                      <button
                        onClick={() => toggleModule(module.id)}
                        className="w-full px-6 py-4 flex items-center justify-between text-left hover:bg-slate-50 transition-colors border-b border-slate-100"
                      >
                        <div className="flex items-center gap-3 min-w-0">
                          <span className="p-2 rounded-xl bg-purple-100 text-purple-700">
                            <Folder className="w-5 h-5" />
                          </span>
                          <div className="truncate">
                            <h4 className="text-base font-bold text-slate-900 truncate">{module.title}</h4>
                            <p className="text-xs text-slate-500 mt-0.5 truncate">
                              {module.description || 'Curriculum Chapter'} • {moduleLessons.length} Lessons •{' '}
                              {moduleVideos.length} Videos
                            </p>
                          </div>
                        </div>

                        <div className="flex items-center gap-2">
                          <span className="text-xs text-slate-400 font-medium hidden sm:inline">
                            {isExpanded ? 'Collapse' : 'Expand'}
                          </span>
                          {isExpanded ? (
                            <ChevronDown className="w-5 h-5 text-slate-400" />
                          ) : (
                            <ChevronRight className="w-5 h-5 text-slate-400" />
                          )}
                        </div>
                      </button>

                      {/* Module Content Body */}
                      {isExpanded && (
                        <div className="p-5 space-y-3 bg-slate-50/40">
                          {moduleLessons.length === 0 && moduleVideos.length === 0 && moduleResources.length === 0 ? (
                            <p className="text-xs text-slate-400 italic py-3 text-center">
                              No lessons or materials added to this module yet.
                            </p>
                          ) : (
                            <>
                              {/* Lessons List */}
                              {moduleLessons.map((lesson) => (
                                <div
                                  key={lesson.id}
                                  className="p-4 rounded-xl bg-white border border-slate-200 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 hover:border-purple-300 transition-all"
                                >
                                  <div className="flex items-start gap-3 min-w-0">
                                    {/* Completion Checkmark */}
                                    <button
                                      type="button"
                                      onClick={() => handleLessonCompletionToggle(lesson)}
                                      className={`mt-0.5 p-1 rounded-full transition-colors ${
                                        lesson.completed
                                          ? 'text-emerald-600 hover:text-emerald-700 bg-emerald-50'
                                          : 'text-slate-300 hover:text-slate-400'
                                      }`}
                                      title={lesson.completed ? 'Mark incomplete' : 'Mark as completed'}
                                    >
                                      <CheckCircle className="w-5 h-5 fill-current" />
                                    </button>

                                    <div className="min-w-0">
                                      <div className="flex items-center gap-2 flex-wrap">
                                        <h5
                                          className={`text-sm font-bold truncate ${
                                            lesson.completed ? 'text-slate-500 line-through' : 'text-slate-900'
                                          }`}
                                        >
                                          {lesson.title}
                                        </h5>
                                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-600 uppercase">
                                          {lesson.lessonType}
                                        </span>
                                        {lesson.durationMinutes > 0 && (
                                          <span className="text-[11px] text-slate-400 flex items-center gap-1 font-mono">
                                            <Clock className="w-3 h-3" />
                                            {lesson.durationMinutes} min
                                          </span>
                                        )}
                                      </div>
                                      <p className="text-xs text-slate-500 mt-1 line-clamp-1">
                                        {lesson.content}
                                      </p>
                                    </div>
                                  </div>

                                  {/* Actions for this lesson */}
                                  <div className="flex items-center gap-2 flex-shrink-0 self-end sm:self-center">
                                    {/* If lesson has an attached video */}
                                    {videos.find((v) => v.moduleId === module.id) && (
                                      <button
                                        onClick={() =>
                                          setActiveVideo(videos.find((v) => v.moduleId === module.id)!)
                                        }
                                        className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 flex items-center gap-1.5 transition-colors"
                                      >
                                        <Play className="w-3.5 h-3.5 fill-red-600" />
                                        Watch Video
                                      </button>
                                    )}

                                    {/* If lesson has attached notes */}
                                    {resources.find((r) => r.moduleId === module.id) && (
                                      <button
                                        onClick={() =>
                                          setActivePdfResource(resources.find((r) => r.moduleId === module.id)!)
                                        }
                                        className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-purple-50 hover:bg-purple-100 text-purple-700 border border-purple-200 flex items-center gap-1.5 transition-colors"
                                      >
                                        <FileText className="w-3.5 h-3.5" />
                                        Read Handout
                                      </button>
                                    )}
                                  </div>
                                </div>
                              ))}
                            </>
                          )}
                        </div>
                      )}
                    </div>
                  );
                })
              )}
            </div>
          )}

          {/* =====================================================================
              TAB 2: LEARNING MATERIALS & NOTES (PDFs, Slide Decks, Lab Manuals)
             ===================================================================== */}
          {activeTab === 'MATERIALS' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between text-xs text-slate-500 px-1">
                <span>Curriculum materials available for online reading & offline revision</span>
                <span>{resources.length} documents uploaded</span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {resources.map((resource) => (
                  <div
                    key={resource.id}
                    className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm flex flex-col justify-between hover:border-purple-300 transition-all group"
                  >
                    <div>
                      <div className="flex items-center justify-between">
                        <span className="p-2.5 rounded-xl bg-purple-50 text-purple-700 group-hover:scale-110 transition-transform">
                          <FileText className="w-6 h-6" />
                        </span>
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-600 uppercase">
                          {resource.resourceType}
                        </span>
                      </div>

                      <h4 className="text-base font-bold text-slate-900 mt-3 group-hover:text-purple-700 transition-colors">
                        {resource.title}
                      </h4>
                      <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                        {resource.description || 'Matriculation Reference Material'}
                      </p>
                    </div>

                    <div className="mt-4 pt-3 border-t border-slate-100 space-y-2">
                      <div className="flex items-center justify-between text-[11px] text-slate-400">
                        <span>{resource.originalFilename || 'document.pdf'}</span>
                        <span>
                          {resource.fileSizeBytes
                            ? `${(resource.fileSizeBytes / 1024 / 1024).toFixed(1)} MB`
                            : 'PDF'}
                        </span>
                      </div>

                      <div className="grid grid-cols-2 gap-2 pt-1">
                        <button
                          onClick={() => setActivePdfResource(resource)}
                          className="w-full py-2 rounded-xl text-xs font-bold bg-purple-50 hover:bg-purple-100 text-purple-800 border border-purple-200 flex items-center justify-center gap-1.5 transition-colors"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          Preview PDF
                        </button>
                        <a
                          href={resource.filePath ? lmsApi.getFileStreamUrl(resource.filePath, true) : '#'}
                          download={resource.originalFilename || 'Document.pdf'}
                          className="w-full py-2 rounded-xl text-xs font-bold bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 flex items-center justify-center gap-1.5 transition-colors"
                        >
                          <Download className="w-3.5 h-3.5" />
                          Download
                        </a>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* YouTube Video Lectures Section */}
              {videos.length > 0 && (
                <div className="pt-6 space-y-4">
                  <div className="flex items-center justify-between text-xs text-slate-500 px-1 border-t border-slate-200 pt-6">
                    <span className="font-bold text-slate-800 text-sm flex items-center gap-2">
                      <Video className="w-4 h-4 text-red-600" />
                      Recorded Video Lectures & Demonstrations
                    </span>
                    <span>{videos.length} videos available</span>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    {videos.map((vid) => (
                      <div
                        key={vid.id}
                        className="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm flex flex-col justify-between hover:border-red-300 transition-all group"
                      >
                        <div>
                          <div className="flex items-center justify-between">
                            <span className="p-2.5 rounded-xl bg-red-50 text-red-600 group-hover:scale-110 transition-transform">
                              <Video className="w-6 h-6" />
                            </span>
                            <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-red-100 text-red-700 uppercase">
                              YouTube 1080p
                            </span>
                          </div>

                          <h4 className="text-base font-bold text-slate-900 mt-3 group-hover:text-red-600 transition-colors">
                            {vid.title}
                          </h4>
                          <p className="text-xs text-slate-500 mt-1 line-clamp-2">
                            {vid.description || 'Sindh Board Recorded Classroom Lecture'}
                          </p>
                        </div>

                        <div className="mt-4 pt-3 border-t border-slate-100 space-y-2">
                          <div className="flex items-center justify-between text-[11px] text-slate-400">
                            <span>Duration: ~{vid.durationSeconds ? Math.round(vid.durationSeconds / 60) : 30} mins</span>
                            <span className="text-red-600 font-semibold">Ready to Stream</span>
                          </div>

                          <button
                            onClick={() => setActiveVideo(vid)}
                            className="w-full py-2 rounded-xl text-xs font-bold bg-red-600 hover:bg-red-700 text-white flex items-center justify-center gap-1.5 transition-colors shadow-sm"
                          >
                            <Play className="w-3.5 h-3.5 fill-white" />
                            Watch Video Lecture
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          {/* =====================================================================
              TAB 3: ASSIGNMENTS & HOMEWORK (Submissions, Deadlines, Multi-Photos)
             ===================================================================== */}
          {activeTab === 'ASSIGNMENTS' && (
            <div className="space-y-4">
              {assignments.map((assignment) => {
                const isPassed = new Date(assignment.dueDate).getTime() < Date.now();
                const submission = assignment.studentSubmission;

                return (
                  <div
                    key={assignment.id}
                    className="p-6 rounded-2xl bg-white border border-slate-200 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-6 hover:border-purple-300 transition-all"
                  >
                    <div className="space-y-2 max-w-2xl min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-purple-100 text-purple-800">
                          {assignment.moduleTitle || 'Homework Task'}
                        </span>
                        <span className="text-xs text-slate-400">•</span>
                        <span className={`text-xs font-semibold flex items-center gap-1 font-mono ${isPassed && !submission ? 'text-amber-600' : 'text-slate-600'}`}>
                          <Calendar className="w-3.5 h-3.5 text-slate-400" />
                          Due: {new Date(assignment.dueDate).toLocaleDateString()}
                          {isPassed && !submission && (
                            <span className="ml-1 text-[10px] font-bold text-amber-700 bg-amber-100 px-1.5 py-0.5 rounded">
                              PAST DUE
                            </span>
                          )}
                        </span>
                      </div>

                      <h3 className="text-lg font-bold text-slate-900">{assignment.title}</h3>
                      <p className="text-xs text-slate-600 leading-relaxed">{assignment.instructions}</p>

                      <div className="flex items-center gap-4 text-xs text-slate-500 pt-1 flex-wrap">
                        <span>• Max Marks: <strong className="text-slate-800">{assignment.maxMarks.toFixed(2)}</strong></span>
                        <span>• Passing: <strong className="text-slate-800">{assignment.passingMarks.toFixed(2)}</strong></span>
                        <span>• Formats: <strong className="text-purple-700">{assignment.allowedFormats}</strong></span>
                      </div>

                      {/* If Student Has Already Submitted */}
                      {submission && (
                        <div className="mt-3 p-3.5 rounded-xl bg-purple-50/60 border border-purple-200 text-xs space-y-1">
                          <div className="flex items-center justify-between flex-wrap gap-2">
                            <span className="font-bold text-purple-900 flex items-center gap-1.5">
                              <CheckCircle className="w-4 h-4 text-emerald-600" />
                              Status: {submission.status}
                            </span>
                            {submission.marksObtained !== undefined && submission.marksObtained !== null && (
                              <span className="font-bold text-emerald-700 bg-emerald-100 px-2.5 py-0.5 rounded-full">
                                Grade: {submission.marksObtained.toFixed(2)} / {assignment.maxMarks.toFixed(2)} (
                                {submission.passed ? 'PASSED' : 'BELOW PASSING'})
                              </span>
                            )}
                          </div>
                          {submission.teacherFeedback && (
                            <p className="text-slate-700 mt-1 italic pl-5">
                              Teacher Feedback: "{submission.teacherFeedback}"
                            </p>
                          )}
                        </div>
                      )}
                    </div>

                    {/* Action Buttons */}
                    <div className="flex flex-col sm:flex-row md:flex-col gap-2 w-full md:w-auto flex-shrink-0">
                      {isStudent && (
                        <button
                          onClick={() => setActiveAssignmentToSubmit(assignment)}
                          className="px-5 py-2.5 rounded-xl text-xs font-bold bg-purple-600 hover:bg-purple-700 text-white shadow-md shadow-purple-600/20 flex items-center justify-center gap-1.5 transition-all"
                        >
                          <Camera className="w-4 h-4" />
                          {submission ? 'Resubmit Homework' : 'Submit Homework (Photos/PDF)'}
                        </button>
                      )}

                      {isTeacher && (
                        <button
                          onClick={() => setActiveTab('GRADING')}
                          className="px-5 py-2.5 rounded-xl text-xs font-bold bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 flex items-center justify-center gap-1.5 transition-all"
                        >
                          <Award className="w-4 h-4 text-purple-600" />
                          Grade Submissions ({assignment.submissionCount || 0})
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {/* =====================================================================
              TAB 4: STUDENT SUBMISSIONS & GRADING (Teacher / Admin View)
             ===================================================================== */}
          {activeTab === 'GRADING' && isTeacher && (
            <div className="space-y-4">
              <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm">
                <div className="px-6 py-4 border-b border-slate-200 bg-slate-50 flex items-center justify-between flex-wrap gap-2">
                  <div>
                    <h3 className="text-base font-bold text-slate-900">Student Homework Submissions</h3>
                    <p className="text-xs text-slate-500">
                      Review student handwritten homework photos, assign marks, and provide feedback
                    </p>
                  </div>
                  <span className="px-3 py-1 rounded-full text-xs font-bold bg-purple-100 text-purple-800">
                    {submissionsRoster.length} Submissions Total
                  </span>
                </div>

                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs">
                    <thead className="bg-slate-100/70 text-slate-500 uppercase tracking-wider font-semibold border-b border-slate-200">
                      <tr>
                        <th className="px-6 py-3.5">Student</th>
                        <th className="px-6 py-3.5">Assignment</th>
                        <th className="px-6 py-3.5">Submitted On</th>
                        <th className="px-6 py-3.5">Status</th>
                        <th className="px-6 py-3.5">Marks</th>
                        <th className="px-6 py-3.5 text-right">Action</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {submissionsRoster.map((sub) => (
                        <tr key={sub.id} className="hover:bg-slate-50/80 transition-colors">
                          <td className="px-6 py-4 font-bold text-slate-900">{sub.studentName}</td>
                          <td className="px-6 py-4 text-slate-600">{sub.assignmentTitle}</td>
                          <td className="px-6 py-4 font-mono text-slate-500">
                            {new Date(sub.submittedAt).toLocaleDateString()}
                          </td>
                          <td className="px-6 py-4">
                            <span
                              className={`px-2.5 py-0.5 rounded-full text-[11px] font-bold ${
                                sub.status === 'GRADED'
                                  ? 'bg-emerald-100 text-emerald-800'
                                  : sub.status === 'RETURNED_FOR_CORRECTION'
                                  ? 'bg-amber-100 text-amber-800'
                                  : 'bg-purple-100 text-purple-800'
                              }`}
                            >
                              {sub.status}
                            </span>
                          </td>
                          <td className="px-6 py-4 font-mono font-bold text-slate-800">
                            {sub.marksObtained !== undefined && sub.marksObtained !== null
                              ? `${sub.marksObtained.toFixed(2)} / ${sub.maxMarks.toFixed(2)}`
                              : 'Pending'}
                          </td>
                          <td className="px-6 py-4 text-right">
                            <button
                              onClick={() => setActiveSubmissionToGrade(sub)}
                              className="px-3.5 py-1.5 rounded-lg text-xs font-bold bg-purple-600 hover:bg-purple-700 text-white shadow-sm transition-colors"
                            >
                              Review & Grade
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* =========================================================================
          MODALS INTEGRATION
         ========================================================================= */}
      {activeVideo && (
        <YouTubePlayerModal
          video={activeVideo}
          isOpen={!!activeVideo}
          onClose={() => setActiveVideo(null)}
          isCompleted={false}
          onMarkCompleted={() => {
            setActiveVideo(null);
          }}
        />
      )}

      {activePdfResource && (
        <PdfViewerModal
          resource={activePdfResource}
          isOpen={!!activePdfResource}
          onClose={() => setActivePdfResource(null)}
        />
      )}

      {activeAssignmentToSubmit && (
        <SubmitAssignmentModal
          assignment={activeAssignmentToSubmit}
          studentId={1}
          isOpen={!!activeAssignmentToSubmit}
          onClose={() => setActiveAssignmentToSubmit(null)}
          onSuccess={handleSubmissionSuccess}
        />
      )}

      {activeSubmissionToGrade && (
        <TeacherGradingModal
          submission={activeSubmissionToGrade}
          isOpen={!!activeSubmissionToGrade}
          onClose={() => setActiveSubmissionToGrade(null)}
          onGraded={handleGradedSuccess}
        />
      )}

      {showCreateAssignmentModal && selectedCourse && (
        <CreateAssignmentModal
          courseId={selectedCourse.id}
          modules={modules}
          isOpen={showCreateAssignmentModal}
          onClose={() => setShowCreateAssignmentModal(false)}
          onCreated={(newAssignment) => {
            setAssignments((prev) => [newAssignment, ...prev]);
            setSelectedCourse((prev) =>
              prev ? { ...prev, assignmentCount: prev.assignmentCount + 1 } : null
            );
          }}
        />
      )}

      {showAddResourceModal && selectedCourse && (
        <AddResourceModal
          courseId={selectedCourse.id}
          modules={modules}
          isOpen={showAddResourceModal}
          onClose={() => setShowAddResourceModal(false)}
          onVideoCreated={(newVideo) => {
            setVideos((prev) => [...prev, newVideo]);
            setSelectedCourse((prev) => (prev ? { ...prev, videoCount: prev.videoCount + 1 } : null));
          }}
          onResourceCreated={(newResource) => {
            setResources((prev) => [...prev, newResource]);
            setSelectedCourse((prev) =>
              prev ? { ...prev, resourceCount: prev.resourceCount + 1 } : null
            );
          }}
        />
      )}

      {showCreateModuleModal && selectedCourse && (
        <CreateModuleModal
          courseId={selectedCourse.id}
          existingCount={modules.length}
          isOpen={showCreateModuleModal}
          onClose={() => setShowCreateModuleModal(false)}
          onCreated={(newMod) => {
            setModules((prev) => [...prev, newMod]);
            setSelectedCourse((prev) => (prev ? { ...prev, moduleCount: prev.moduleCount + 1 } : null));
          }}
        />
      )}
    </div>
  );
}
