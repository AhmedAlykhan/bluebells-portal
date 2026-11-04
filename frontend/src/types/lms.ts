export interface CourseModule {
  id: number;
  courseId: number;
  courseTitle?: string;
  title: string;
  description?: string;
  displayOrder: number;
  lessonCount: number;
  resourceCount: number;
  videoCount: number;
  assignmentCount: number;
}

export interface LearningResource {
  id: number;
  courseId: number;
  moduleId?: number;
  moduleTitle?: string;
  lessonId?: number;
  lessonTitle?: string;
  title: string;
  description?: string;
  resourceType: 'PDF' | 'DOCX' | 'PPTX' | 'EXCEL' | 'IMAGE' | 'LINK' | 'OTHER';
  filePath?: string;
  originalFilename?: string;
  fileSizeBytes?: number;
  contentType?: string;
  externalUrl?: string;
  downloadable: boolean;
  uploadedByName?: string;
  createdAt: string;
}

export interface YouTubeVideo {
  id: number;
  courseId: number;
  moduleId?: number;
  moduleTitle?: string;
  lessonId?: number;
  lessonTitle?: string;
  title: string;
  description?: string;
  youtubeUrl: string;
  videoId: string;
  durationSeconds: number;
  displayOrder: number;
  createdAt: string;
}

export interface SubmissionFile {
  id: number;
  submissionId: number;
  filePath: string;
  originalFilename: string;
  fileSizeBytes: number;
  fileType: 'IMAGE' | 'PDF' | 'DOCX' | 'OTHER';
  uploadOrder: number;
  createdAt: string;
}

export interface AssignmentSubmission {
  id: number;
  assignmentId: number;
  assignmentTitle: string;
  maxMarks: number;
  passingMarks?: number;
  allowedFormats?: string;
  studentId: number;
  studentName: string;
  submissionText?: string;
  fileUrl?: string;
  submissionFiles?: SubmissionFile[];
  submittedAt: string;
  late: boolean;
  status: 'SUBMITTED' | 'RESUBMITTED' | 'RETURNED_FOR_CORRECTION' | 'GRADED';
  marksObtained?: number;
  passed?: boolean;
  teacherFeedback?: string;
  gradedAt?: string;
  gradedByName?: string;
}

export interface Assignment {
  id: number;
  courseId: number;
  courseTitle: string;
  moduleId?: number;
  moduleTitle?: string;
  title: string;
  instructions: string;
  maxMarks: number;
  passingMarks: number;
  dueDate: string;
  allowLateSubmission: boolean;
  allowedFormats: string;
  attachmentUrl?: string;
  attachmentName?: string;
  attachmentSizeBytes?: number;
  status: string;
  submissionCount: number;
  gradedCount: number;
  studentSubmission?: AssignmentSubmission;
}

export interface Lesson {
  id: number;
  courseId: number;
  moduleId?: number;
  moduleTitle?: string;
  title: string;
  content?: string;
  sequenceOrder: number;
  lessonType: 'LECTURE' | 'DOCUMENT' | 'VIDEO' | 'LAB' | 'ASSIGNMENT';
  durationMinutes: number;
  completed?: boolean;
  publishedAt?: string;
  resources?: LearningResource[];
  videos?: YouTubeVideo[];
}

export interface Course {
  id: number;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  sectionId: number;
  sectionName: string;
  className: string;
  teacherId: number;
  teacherName: string;
  title: string;
  description?: string;
  status: string;
  moduleCount: number;
  lessonCount: number;
  assignmentCount: number;
  resourceCount: number;
  videoCount: number;
  completionPercentage?: number;
  modules?: CourseModule[];
}
