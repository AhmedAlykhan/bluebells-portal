export interface SubjectResult {
  code: string;
  name: string;
  totalMarks: number;
  obtainedMarks: number;
  grade: string;
  remarks: string;
}

export interface ReportCardData {
  id?: string;
  studentName: string;
  rollNumber: string;
  admissionNumber: string;
  className: string;
  sectionName: string;
  session: string;
  examTerm: string;
  bFormNumber: string;
  attendancePercentage: number;
  totalWorkingDays: number;
  daysPresent: number;
  subjects: SubjectResult[];
  overallTotal: number;
  overallObtained: number;
  overallPercentage: number;
  overallGrade: string;
  overallRemarks: string;
  positionInSection?: string;
  issuedDate: string;
}

export interface ClassOption {
  id: string;
  name: string;
  sections: string[];
  students: {
    key: string;
    name: string;
    roll: string;
    section: string;
  }[];
}

export const STUDENT_REPORT_CARDS: Record<string, ReportCardData> = {
  'ali-khan': {
    id: 'ali-khan',
    studentName: 'Ali Khan',
    rollNumber: '09-A-12',
    admissionNumber: 'BSS-2024-0104',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-1234567-3',
    attendancePercentage: 96.2,
    totalWorkingDays: 52,
    daysPresent: 50,
    subjects: [
      { code: 'MATH-09', name: 'Mathematics (Sindh Textbook Board)', totalMarks: 100, obtainedMarks: 94, grade: 'A-1', remarks: 'Outstanding analytical ability' },
      { code: 'PHY-09', name: 'Physics (Theory & Practical)', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Excellent lab demonstration' },
      { code: 'CHEM-09', name: 'Chemistry', totalMarks: 100, obtainedMarks: 85, grade: 'A', remarks: 'Strong theoretical grasp' },
      { code: 'ENG-09', name: 'English Compulsory', totalMarks: 100, obtainedMarks: 89, grade: 'A-1', remarks: 'Fluent comprehension and essay writing' },
      { code: 'URD-09', name: 'Urdu Salees', totalMarks: 100, obtainedMarks: 82, grade: 'A', remarks: 'Good classical vocabulary' },
      { code: 'SND-09', name: 'Sindhi Compulsory (Normal)', totalMarks: 75, obtainedMarks: 68, grade: 'A-1', remarks: 'Remarkable grammar & handwriting' },
      { code: 'ISL-09', name: 'Islamiat / Pakistan Studies', totalMarks: 75, obtainedMarks: 70, grade: 'A-1', remarks: 'Comprehensive understanding' },
      { code: 'CS-09', name: 'Computer Science (Python & DB)', totalMarks: 100, obtainedMarks: 96, grade: 'A-1', remarks: 'Exemplary programming coursework' },
    ],
    overallTotal: 750,
    overallObtained: 672,
    overallPercentage: 89.6,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Promoted with Honours. Exceptional discipline and academic aptitude throughout the term.',
    positionInSection: '1st in Section',
    issuedDate: '28 October 2026'
  },
  'shahid-afridi': {
    id: 'shahid-afridi',
    studentName: 'Shahid Afridi',
    rollNumber: '09-A-02',
    admissionNumber: 'BSS-2026-0003',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-7654321-1',
    attendancePercentage: 92.3,
    totalWorkingDays: 52,
    daysPresent: 48,
    subjects: [
      { code: 'MATH-09', name: 'Mathematics (Sindh Textbook Board)', totalMarks: 100, obtainedMarks: 85, grade: 'A-1', remarks: 'Very good problem-solving' },
      { code: 'PHY-09', name: 'Physics (Theory & Practical)', totalMarks: 100, obtainedMarks: 82, grade: 'A-1', remarks: 'Good lab work and viva voce' },
      { code: 'CHEM-09', name: 'Chemistry', totalMarks: 100, obtainedMarks: 78, grade: 'A', remarks: 'Solid conceptual understanding' },
      { code: 'ENG-09', name: 'English Compulsory', totalMarks: 100, obtainedMarks: 84, grade: 'A-1', remarks: 'Good reading and presentation skills' },
      { code: 'URD-09', name: 'Urdu Salees', totalMarks: 100, obtainedMarks: 76, grade: 'A', remarks: 'Consistent classroom participation' },
      { code: 'SND-09', name: 'Sindhi Compulsory (Normal)', totalMarks: 75, obtainedMarks: 65, grade: 'A-1', remarks: 'Neat work and grammatical clarity' },
      { code: 'ISL-09', name: 'Islamiat / Pakistan Studies', totalMarks: 75, obtainedMarks: 68, grade: 'A-1', remarks: 'Comprehensive grasp' },
      { code: 'CS-09', name: 'Computer Science (Python & DB)', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Excellent project presentation' },
    ],
    overallTotal: 750,
    overallObtained: 626,
    overallPercentage: 83.5,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Very good conceptual clarity and analytical grasp. Active sports and academic contributor.',
    positionInSection: '2nd in Section',
    issuedDate: '28 October 2026'
  },
  'ayesha-siddiqui': {
    id: 'ayesha-siddiqui',
    studentName: 'Ayesha Siddiqui',
    rollNumber: '09-A-03',
    admissionNumber: 'BSS-2026-0004',
    className: 'Class 9 (Matriculation Part I)',
    sectionName: 'Section A',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-9988776-2',
    attendancePercentage: 94.2,
    totalWorkingDays: 52,
    daysPresent: 49,
    subjects: [
      { code: 'MATH-09', name: 'Mathematics (Sindh Textbook Board)', totalMarks: 100, obtainedMarks: 80, grade: 'A-1', remarks: 'Methodical step-by-step solutions' },
      { code: 'PHY-09', name: 'Physics (Theory & Practical)', totalMarks: 100, obtainedMarks: 78, grade: 'A', remarks: 'Good understanding of core mechanics' },
      { code: 'CHEM-09', name: 'Chemistry', totalMarks: 100, obtainedMarks: 76, grade: 'A', remarks: 'Very attentive in practical sessions' },
      { code: 'ENG-09', name: 'English Compulsory', totalMarks: 100, obtainedMarks: 92, grade: 'A-1', remarks: 'Outstanding essay and grammar marks' },
      { code: 'URD-09', name: 'Urdu Salees', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Excellent prose comprehension' },
      { code: 'SND-09', name: 'Sindhi Compulsory (Normal)', totalMarks: 75, obtainedMarks: 62, grade: 'A-1', remarks: 'Good linguistic comprehension' },
      { code: 'ISL-09', name: 'Islamiat / Pakistan Studies', totalMarks: 75, obtainedMarks: 65, grade: 'A-1', remarks: 'Well-articulated answers' },
      { code: 'BIO-09', name: 'Biology (Botany & Zoology)', totalMarks: 100, obtainedMarks: 80, grade: 'A-1', remarks: 'Detailed biological diagrams' },
    ],
    overallTotal: 750,
    overallObtained: 601,
    overallPercentage: 80.1,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Disciplined and diligent student. High aptitude in linguistic and biological sciences.',
    positionInSection: '3rd in Section',
    issuedDate: '28 October 2026'
  },
  'sara-khan': {
    id: 'sara-khan',
    studentName: 'Sara Khan',
    rollNumber: '08-R-05',
    admissionNumber: 'BSS-2024-0210',
    className: 'Class 8 (Middle School Secondary Prep)',
    sectionName: 'Section Rose',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-1234567-4',
    attendancePercentage: 98.1,
    totalWorkingDays: 52,
    daysPresent: 51,
    subjects: [
      { code: 'MATH-08', name: 'General Mathematics', totalMarks: 100, obtainedMarks: 95, grade: 'A-1', remarks: 'Perfect calculations and arithmetic' },
      { code: 'SCI-08', name: 'General Science & Environment', totalMarks: 100, obtainedMarks: 92, grade: 'A-1', remarks: 'Inquisitive scientific approach' },
      { code: 'ENG-08', name: 'English Language & Literature', totalMarks: 100, obtainedMarks: 90, grade: 'A-1', remarks: 'Creative story writing and diction' },
      { code: 'URD-08', name: 'Urdu Compulsory', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Fine penmanship and poetry grasp' },
      { code: 'SND-08', name: 'Sindhi Salees', totalMarks: 100, obtainedMarks: 92, grade: 'A-1', remarks: 'Flawless handwriting and oral reading' },
      { code: 'SST-08', name: 'Social Studies & Geography', totalMarks: 100, obtainedMarks: 95, grade: 'A-1', remarks: 'Exemplary cartography and historical recall' },
      { code: 'CS-08', name: 'Computer Fundamentals', totalMarks: 100, obtainedMarks: 90, grade: 'A-1', remarks: 'High proficiency in software tools' },
    ],
    overallTotal: 700,
    overallObtained: 642,
    overallPercentage: 91.7,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Academic brilliance with outstanding conduct and leadership in Middle School.',
    positionInSection: '1st in Section',
    issuedDate: '28 October 2026'
  },
  'bilal-hassan': {
    id: 'bilal-hassan',
    studentName: 'Bilal Hassan',
    rollNumber: '10-A-01',
    admissionNumber: 'BSS-2026-0005',
    className: 'Class 10 (Matriculation Part II)',
    sectionName: 'Section A',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-5544332-1',
    attendancePercentage: 90.4,
    totalWorkingDays: 52,
    daysPresent: 47,
    subjects: [
      { code: 'MATH-10', name: 'Mathematics Part II (Sindh Board)', totalMarks: 100, obtainedMarks: 92, grade: 'A-1', remarks: 'Exceptional geometry and trigonometry' },
      { code: 'PHY-10', name: 'Physics Part II (Circuits & Optics)', totalMarks: 100, obtainedMarks: 88, grade: 'A-1', remarks: 'Thorough experiment documentation' },
      { code: 'CHEM-10', name: 'Chemistry Part II (Organic & Inorganic)', totalMarks: 100, obtainedMarks: 84, grade: 'A-1', remarks: 'Accurate titration experiments' },
      { code: 'ENG-10', name: 'English Literature & Composition', totalMarks: 100, obtainedMarks: 86, grade: 'A-1', remarks: 'Strong analytical prose' },
      { code: 'PST-10', name: 'Pakistan Studies (Sindh Board)', totalMarks: 50, obtainedMarks: 45, grade: 'A-1', remarks: 'Mastery of constitutional history' },
      { code: 'SND-10', name: 'Sindhi Salees Part II', totalMarks: 75, obtainedMarks: 72, grade: 'A-1', remarks: 'Distinction level language mastery' },
      { code: 'CS-10', name: 'Computer Science II (Database SQL)', totalMarks: 100, obtainedMarks: 96, grade: 'A-1', remarks: 'Flawless database design project' },
    ],
    overallTotal: 700,
    overallObtained: 623,
    overallPercentage: 89.0,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Well prepared for Board of Secondary Education Hyderabad Matriculation finals.',
    positionInSection: '1st in Section',
    issuedDate: '28 October 2026'
  },
  'zainab-bibi': {
    id: 'zainab-bibi',
    studentName: 'Zainab Bibi',
    rollNumber: '05-J-08',
    admissionNumber: 'BSS-2026-0006',
    className: 'Class 5 (Primary School Certificate)',
    sectionName: 'Section Jasmine',
    session: '2026-2027',
    examTerm: 'Midterm Examination 2026',
    bFormNumber: '41303-3322110-5',
    attendancePercentage: 96.0,
    totalWorkingDays: 52,
    daysPresent: 50,
    subjects: [
      { code: 'MATH-05', name: 'Primary Mathematics & Fractions', totalMarks: 100, obtainedMarks: 96, grade: 'A-1', remarks: 'Enthusiastic and rapid mental math' },
      { code: 'SCI-05', name: 'General Science & Plants', totalMarks: 100, obtainedMarks: 94, grade: 'A-1', remarks: 'Outstanding nature study' },
      { code: 'ENG-05', name: 'English Reading & Phonics', totalMarks: 100, obtainedMarks: 92, grade: 'A-1', remarks: 'Superb storytelling and pronunciation' },
      { code: 'URD-05', name: 'Urdu Reading & Nazm', totalMarks: 100, obtainedMarks: 90, grade: 'A-1', remarks: 'Expressive recitation' },
      { code: 'SST-05', name: 'Social Studies & Community', totalMarks: 100, obtainedMarks: 96, grade: 'A-1', remarks: 'Creative project binder' },
    ],
    overallTotal: 500,
    overallObtained: 468,
    overallPercentage: 93.6,
    overallGrade: 'A-1 Distinction',
    overallRemarks: 'Top Position in Primary Wing. Dedicated, respectful, and joyful learner.',
    positionInSection: '1st in Section',
    issuedDate: '28 October 2026'
  }
};

export const CLASSES_LIST: ClassOption[] = [
  {
    id: 'CLS-9',
    name: 'Class 9 (Matriculation Part I)',
    sections: ['Section A', 'Section B'],
    students: [
      { key: 'ali-khan', name: 'Ali Khan', roll: '09-A-12', section: 'Section A' },
      { key: 'shahid-afridi', name: 'Shahid Afridi', roll: '09-A-02', section: 'Section A' },
      { key: 'ayesha-siddiqui', name: 'Ayesha Siddiqui', roll: '09-A-03', section: 'Section A' },
    ]
  },
  {
    id: 'CLS-10',
    name: 'Class 10 (Matriculation Part II)',
    sections: ['Section A'],
    students: [
      { key: 'bilal-hassan', name: 'Bilal Hassan', roll: '10-A-01', section: 'Section A' },
    ]
  },
  {
    id: 'CLS-8',
    name: 'Class 8 (Middle School)',
    sections: ['Section Rose'],
    students: [
      { key: 'sara-khan', name: 'Sara Khan', roll: '08-R-05', section: 'Section Rose' },
    ]
  },
  {
    id: 'CLS-5',
    name: 'Class 5 (Primary School)',
    sections: ['Section Jasmine'],
    students: [
      { key: 'zainab-bibi', name: 'Zainab Bibi', roll: '05-J-08', section: 'Section Jasmine' },
    ]
  }
];

export function getReportCardByStudentKey(key: string): ReportCardData {
  return STUDENT_REPORT_CARDS[key] || STUDENT_REPORT_CARDS['ali-khan'];
}

export function getReportCardByName(name: string): ReportCardData {
  const normalized = name.toLowerCase().trim();
  for (const card of Object.values(STUDENT_REPORT_CARDS)) {
    if (card.studentName.toLowerCase().includes(normalized) || normalized.includes(card.studentName.toLowerCase())) {
      return card;
    }
  }
  return STUDENT_REPORT_CARDS['ali-khan'];
}
