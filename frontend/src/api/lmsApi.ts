import { apiClient } from './client';
import { ApiResponse } from '../types';
import {
  Course,
  CourseModule,
  Lesson,
  LearningResource,
  YouTubeVideo,
  Assignment,
  AssignmentSubmission,
} from '../types/lms';

export const lmsApi = {
  // Courses
  getAllCourses: async (): Promise<Course[]> => {
    const res = await apiClient.get<ApiResponse<Course[]>>('/lms/courses');
    return res.data.data;
  },

  getCourseById: async (id: number): Promise<Course> => {
    const res = await apiClient.get<ApiResponse<Course>>(`/lms/courses/${id}`);
    return res.data.data;
  },

  getCoursesForSection: async (sectionId: number): Promise<Course[]> => {
    const res = await apiClient.get<ApiResponse<Course[]>>(`/lms/courses/section/${sectionId}`);
    return res.data.data;
  },

  getCoursesForStudent: async (studentId: number): Promise<Course[]> => {
    const res = await apiClient.get<ApiResponse<Course[]>>(`/lms/courses/student/${studentId}`);
    return res.data.data;
  },

  getCoursesForTeacher: async (teacherId: number): Promise<Course[]> => {
    const res = await apiClient.get<ApiResponse<Course[]>>(`/lms/courses/teacher/${teacherId}`);
    return res.data.data;
  },

  createCourse: async (data: {
    subjectId: number;
    sectionId: number;
    teacherId: number;
    title: string;
    description?: string;
  }): Promise<Course> => {
    const res = await apiClient.post<ApiResponse<Course>>('/lms/courses', data);
    return res.data.data;
  },

  // Modules
  getModules: async (courseId: number): Promise<CourseModule[]> => {
    const res = await apiClient.get<ApiResponse<CourseModule[]>>(`/lms/courses/${courseId}/modules`);
    return res.data.data;
  },

  createModule: async (courseId: number, data: {
    title: string;
    description?: string;
    displayOrder?: number;
  }): Promise<CourseModule> => {
    const res = await apiClient.post<ApiResponse<CourseModule>>(`/lms/courses/${courseId}/modules`, data);
    return res.data.data;
  },

  // Lessons
  getLessons: async (courseId: number, studentId?: number): Promise<Lesson[]> => {
    const params = studentId ? { studentId } : undefined;
    const res = await apiClient.get<ApiResponse<Lesson[]>>(`/lms/courses/${courseId}/lessons`, { params });
    return res.data.data;
  },

  createLesson: async (courseId: number, data: {
    moduleId?: number;
    title: string;
    content?: string;
    sequenceOrder?: number;
    lessonType?: string;
    durationMinutes?: number;
  }): Promise<Lesson> => {
    const res = await apiClient.post<ApiResponse<Lesson>>(`/lms/courses/${courseId}/lessons`, data);
    return res.data.data;
  },

  markLessonProgress: async (lessonId: number, studentId: number, completed: boolean): Promise<void> => {
    await apiClient.post(`/lms/lessons/${lessonId}/progress`, { studentId, completed });
  },

  getStudentProgress: async (courseId: number, studentId: number) => {
    const res = await apiClient.get<ApiResponse<{
      totalLessons: number;
      completedLessons: number;
      completionPercentage: number;
    }>>(`/lms/courses/${courseId}/progress/student/${studentId}`);
    return res.data.data;
  },

  // Learning Resources
  getResources: async (courseId: number): Promise<LearningResource[]> => {
    const res = await apiClient.get<ApiResponse<LearningResource[]>>(`/lms/courses/${courseId}/resources`);
    return res.data.data;
  },

  uploadResource: async (courseId: number, formData: FormData): Promise<LearningResource> => {
    const res = await apiClient.post<ApiResponse<LearningResource>>(
      `/lms/courses/${courseId}/resources/upload`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    return res.data.data;
  },

  createResource: async (courseId: number, data: Partial<LearningResource>): Promise<LearningResource> => {
    const res = await apiClient.post<ApiResponse<LearningResource>>(`/lms/courses/${courseId}/resources`, data);
    return res.data.data;
  },

  // YouTube Videos
  getVideos: async (courseId: number): Promise<YouTubeVideo[]> => {
    const res = await apiClient.get<ApiResponse<YouTubeVideo[]>>(`/lms/courses/${courseId}/videos`);
    return res.data.data;
  },

  addVideo: async (courseId: number, data: {
    moduleId?: number;
    lessonId?: number;
    title: string;
    description?: string;
    youtubeUrl: string;
    durationSeconds?: number;
    displayOrder?: number;
  }): Promise<YouTubeVideo> => {
    const res = await apiClient.post<ApiResponse<YouTubeVideo>>(`/lms/courses/${courseId}/videos`, data);
    return res.data.data;
  },

  // Assignments
  getAssignments: async (courseId: number, studentId?: number): Promise<Assignment[]> => {
    const params = studentId ? { studentId } : undefined;
    const res = await apiClient.get<ApiResponse<Assignment[]>>(`/lms/courses/${courseId}/assignments`, { params });
    return res.data.data;
  },

  getAssignmentById: async (id: number): Promise<Assignment> => {
    const res = await apiClient.get<ApiResponse<Assignment>>(`/lms/assignments/${id}`);
    return res.data.data;
  },

  createAssignment: async (courseId: number, data: {
    moduleId?: number;
    title: string;
    instructions: string;
    maxMarks: number;
    passingMarks?: number;
    dueDate: string;
    allowLateSubmission?: boolean;
    allowedFormats?: string;
  }): Promise<Assignment> => {
    const res = await apiClient.post<ApiResponse<Assignment>>(`/lms/courses/${courseId}/assignments`, data);
    return res.data.data;
  },

  // Submissions & Multi-Photo Uploads
  submitAssignmentJson: async (assignmentId: number, data: {
    studentId: number;
    submissionText?: string;
    fileUrl?: string;
    fileUrls?: string[];
    fileNames?: string[];
    fileSizes?: number[];
    fileTypes?: string[];
  }): Promise<AssignmentSubmission> => {
    const res = await apiClient.post<ApiResponse<AssignmentSubmission>>(
      `/lms/assignments/${assignmentId}/submit`,
      data
    );
    return res.data.data;
  },

  submitAssignmentFiles: async (assignmentId: number, formData: FormData): Promise<AssignmentSubmission> => {
    const res = await apiClient.post<ApiResponse<AssignmentSubmission>>(
      `/lms/assignments/${assignmentId}/submit-files`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    return res.data.data;
  },

  getSubmissionsForAssignment: async (assignmentId: number): Promise<AssignmentSubmission[]> => {
    const res = await apiClient.get<ApiResponse<AssignmentSubmission[]>>(
      `/lms/assignments/${assignmentId}/submissions`
    );
    return res.data.data;
  },

  getStudentSubmission: async (assignmentId: number, studentId: number): Promise<AssignmentSubmission> => {
    const res = await apiClient.get<ApiResponse<AssignmentSubmission>>(
      `/lms/assignments/${assignmentId}/submissions/student/${studentId}`
    );
    return res.data.data;
  },

  gradeSubmission: async (submissionId: number, data: {
    marksObtained: number;
    teacherFeedback?: string;
    status?: 'GRADED' | 'RETURNED_FOR_CORRECTION';
  }): Promise<AssignmentSubmission> => {
    const res = await apiClient.post<ApiResponse<AssignmentSubmission>>(
      `/lms/submissions/${submissionId}/grade`,
      data
    );
    return res.data.data;
  },

  // File preview / stream URL helper
  getFileStreamUrl: (filePath: string, download = false): string => {
    const baseUrl = import.meta.env.VITE_API_URL 
      ? `${import.meta.env.VITE_API_URL.replace(/\/+$/, '')}/api/v1` 
      : '/api/v1';
    return `${baseUrl}/lms/files/stream?path=${encodeURIComponent(filePath)}&download=${download}`;
  }
};
