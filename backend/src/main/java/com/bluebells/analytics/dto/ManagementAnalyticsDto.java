package com.bluebells.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public class ManagementAnalyticsDto {

    private StudentAnalyticsDto studentAnalytics;
    private AttendanceAnalyticsDto attendanceAnalytics;
    private AcademicAnalyticsDto academicAnalytics;
    private FinancialAnalyticsDto financialAnalytics;
    private LmsAnalyticsDto lmsAnalytics;
    private StaffAnalyticsDto staffAnalytics;

    public ManagementAnalyticsDto() {}

    public ManagementAnalyticsDto(
            StudentAnalyticsDto studentAnalytics,
            AttendanceAnalyticsDto attendanceAnalytics,
            AcademicAnalyticsDto academicAnalytics,
            FinancialAnalyticsDto financialAnalytics,
            LmsAnalyticsDto lmsAnalytics,
            StaffAnalyticsDto staffAnalytics) {
        this.studentAnalytics = studentAnalytics;
        this.attendanceAnalytics = attendanceAnalytics;
        this.academicAnalytics = academicAnalytics;
        this.financialAnalytics = financialAnalytics;
        this.lmsAnalytics = lmsAnalytics;
        this.staffAnalytics = staffAnalytics;
    }

    public StudentAnalyticsDto getStudentAnalytics() { return studentAnalytics; }
    public void setStudentAnalytics(StudentAnalyticsDto studentAnalytics) { this.studentAnalytics = studentAnalytics; }

    public AttendanceAnalyticsDto getAttendanceAnalytics() { return attendanceAnalytics; }
    public void setAttendanceAnalytics(AttendanceAnalyticsDto attendanceAnalytics) { this.attendanceAnalytics = attendanceAnalytics; }

    public AcademicAnalyticsDto getAcademicAnalytics() { return academicAnalytics; }
    public void setAcademicAnalytics(AcademicAnalyticsDto academicAnalytics) { this.academicAnalytics = academicAnalytics; }

    public FinancialAnalyticsDto getFinancialAnalytics() { return financialAnalytics; }
    public void setFinancialAnalytics(FinancialAnalyticsDto financialAnalytics) { this.financialAnalytics = financialAnalytics; }

    public LmsAnalyticsDto getLmsAnalytics() { return lmsAnalytics; }
    public void setLmsAnalytics(LmsAnalyticsDto lmsAnalytics) { this.lmsAnalytics = lmsAnalytics; }

    public StaffAnalyticsDto getStaffAnalytics() { return staffAnalytics; }
    public void setStaffAnalytics(StaffAnalyticsDto staffAnalytics) { this.staffAnalytics = staffAnalytics; }

    // --- Sub-DTOs ---

    public static class StudentAnalyticsDto {
        private long totalActiveStudents;
        private long newAdmissionsCount;
        private long withdrawalsCount;
        private List<CategoryCountDto> classWiseStrength;
        private List<SectionDistributionDto> sectionWiseDistribution;
        private List<GenderDistributionDto> genderDistribution;
        private List<SessionComparisonDto> sessionComparison;

        public StudentAnalyticsDto() {}

        public long getTotalActiveStudents() { return totalActiveStudents; }
        public void setTotalActiveStudents(long totalActiveStudents) { this.totalActiveStudents = totalActiveStudents; }

        public long getNewAdmissionsCount() { return newAdmissionsCount; }
        public void setNewAdmissionsCount(long newAdmissionsCount) { this.newAdmissionsCount = newAdmissionsCount; }

        public long getWithdrawalsCount() { return withdrawalsCount; }
        public void setWithdrawalsCount(long withdrawalsCount) { this.withdrawalsCount = withdrawalsCount; }

        public List<CategoryCountDto> getClassWiseStrength() { return classWiseStrength; }
        public void setClassWiseStrength(List<CategoryCountDto> classWiseStrength) { this.classWiseStrength = classWiseStrength; }

        public List<SectionDistributionDto> getSectionWiseDistribution() { return sectionWiseDistribution; }
        public void setSectionWiseDistribution(List<SectionDistributionDto> sectionWiseDistribution) { this.sectionWiseDistribution = sectionWiseDistribution; }

        public List<GenderDistributionDto> getGenderDistribution() { return genderDistribution; }
        public void setGenderDistribution(List<GenderDistributionDto> genderDistribution) { this.genderDistribution = genderDistribution; }

        public List<SessionComparisonDto> getSessionComparison() { return sessionComparison; }
        public void setSessionComparison(List<SessionComparisonDto> sessionComparison) { this.sessionComparison = sessionComparison; }
    }

    public static class AttendanceAnalyticsDto {
        private BigDecimal todayAttendanceRate;
        private long todayPresent;
        private long todayAbsent;
        private long todayLate;
        private long todayExcused;
        private BigDecimal monthlyAttendanceRate;
        private List<AbsenteeismTrendDto> absenteeismTrends;
        private List<ClassAttendanceDto> classWiseAttendance;
        private List<ChronicAbsenteeDto> chronicAbsentees;
        private List<AttendanceDistributionDto> attendanceDistribution;

        public AttendanceAnalyticsDto() {}

        public BigDecimal getTodayAttendanceRate() { return todayAttendanceRate; }
        public void setTodayAttendanceRate(BigDecimal todayAttendanceRate) { this.todayAttendanceRate = todayAttendanceRate; }

        public long getTodayPresent() { return todayPresent; }
        public void setTodayPresent(long todayPresent) { this.todayPresent = todayPresent; }

        public long getTodayAbsent() { return todayAbsent; }
        public void setTodayAbsent(long todayAbsent) { this.todayAbsent = todayAbsent; }

        public long getTodayLate() { return todayLate; }
        public void setTodayLate(long todayLate) { this.todayLate = todayLate; }

        public long getTodayExcused() { return todayExcused; }
        public void setTodayExcused(long todayExcused) { this.todayExcused = todayExcused; }

        public BigDecimal getMonthlyAttendanceRate() { return monthlyAttendanceRate; }
        public void setMonthlyAttendanceRate(BigDecimal monthlyAttendanceRate) { this.monthlyAttendanceRate = monthlyAttendanceRate; }

        public List<AbsenteeismTrendDto> getAbsenteeismTrends() { return absenteeismTrends; }
        public void setAbsenteeismTrends(List<AbsenteeismTrendDto> absenteeismTrends) { this.absenteeismTrends = absenteeismTrends; }

        public List<ClassAttendanceDto> getClassWiseAttendance() { return classWiseAttendance; }
        public void setClassWiseAttendance(List<ClassAttendanceDto> classWiseAttendance) { this.classWiseAttendance = classWiseAttendance; }

        public List<ChronicAbsenteeDto> getChronicAbsentees() { return chronicAbsentees; }
        public void setChronicAbsentees(List<ChronicAbsenteeDto> chronicAbsentees) { this.chronicAbsentees = chronicAbsentees; }

        public List<AttendanceDistributionDto> getAttendanceDistribution() { return attendanceDistribution; }
        public void setAttendanceDistribution(List<AttendanceDistributionDto> attendanceDistribution) { this.attendanceDistribution = attendanceDistribution; }
    }

    public static class AcademicAnalyticsDto {
        private BigDecimal overallAveragePercentage;
        private List<ClassAverageDto> classAverages;
        private List<SubjectPerformanceDto> subjectPerformance;
        private List<GradeDistributionDto> gradeDistribution;
        private List<AcademicProgressDto> academicProgressOverTime;
        private List<StudentAcademicSupportDto> studentsNeedingSupport;

        public AcademicAnalyticsDto() {}

        public BigDecimal getOverallAveragePercentage() { return overallAveragePercentage; }
        public void setOverallAveragePercentage(BigDecimal overallAveragePercentage) { this.overallAveragePercentage = overallAveragePercentage; }

        public List<ClassAverageDto> getClassAverages() { return classAverages; }
        public void setClassAverages(List<ClassAverageDto> classAverages) { this.classAverages = classAverages; }

        public List<SubjectPerformanceDto> getSubjectPerformance() { return subjectPerformance; }
        public void setSubjectPerformance(List<SubjectPerformanceDto> subjectPerformance) { this.subjectPerformance = subjectPerformance; }

        public List<GradeDistributionDto> getGradeDistribution() { return gradeDistribution; }
        public void setGradeDistribution(List<GradeDistributionDto> gradeDistribution) { this.gradeDistribution = gradeDistribution; }

        public List<AcademicProgressDto> getAcademicProgressOverTime() { return academicProgressOverTime; }
        public void setAcademicProgressOverTime(List<AcademicProgressDto> academicProgressOverTime) { this.academicProgressOverTime = academicProgressOverTime; }

        public List<StudentAcademicSupportDto> getStudentsNeedingSupport() { return studentsNeedingSupport; }
        public void setStudentsNeedingSupport(List<StudentAcademicSupportDto> studentsNeedingSupport) { this.studentsNeedingSupport = studentsNeedingSupport; }
    }

    public static class FinancialAnalyticsDto {
        private BigDecimal totalBilled;
        private BigDecimal totalCollected;
        private BigDecimal outstandingBalance;
        private BigDecimal collectionRate;
        private BigDecimal discountsAndScholarships;
        private List<MonthlyRevenueDto> monthlyRevenue;
        private List<ClassCollectionDto> classWiseCollection;

        public FinancialAnalyticsDto() {}

        public BigDecimal getTotalBilled() { return totalBilled; }
        public void setTotalBilled(BigDecimal totalBilled) { this.totalBilled = totalBilled; }

        public BigDecimal getTotalCollected() { return totalCollected; }
        public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }

        public BigDecimal getOutstandingBalance() { return outstandingBalance; }
        public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }

        public BigDecimal getCollectionRate() { return collectionRate; }
        public void setCollectionRate(BigDecimal collectionRate) { this.collectionRate = collectionRate; }

        public BigDecimal getDiscountsAndScholarships() { return discountsAndScholarships; }
        public void setDiscountsAndScholarships(BigDecimal discountsAndScholarships) { this.discountsAndScholarships = discountsAndScholarships; }

        public List<MonthlyRevenueDto> getMonthlyRevenue() { return monthlyRevenue; }
        public void setMonthlyRevenue(List<MonthlyRevenueDto> monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }

        public List<ClassCollectionDto> getClassWiseCollection() { return classWiseCollection; }
        public void setClassWiseCollection(List<ClassCollectionDto> classWiseCollection) { this.classWiseCollection = classWiseCollection; }
    }

    public static class LmsAnalyticsDto {
        private long activeCoursesCount;
        private long totalResourcesCount;
        private long totalAssignments;
        private long totalSubmissions;
        private BigDecimal assignmentCompletionRate;
        private long overdueAssignmentsCount;
        private BigDecimal averageQuizScore;
        private long learningMaterialViews;

        public LmsAnalyticsDto() {}

        public long getActiveCoursesCount() { return activeCoursesCount; }
        public void setActiveCoursesCount(long activeCoursesCount) { this.activeCoursesCount = activeCoursesCount; }

        public long getTotalResourcesCount() { return totalResourcesCount; }
        public void setTotalResourcesCount(long totalResourcesCount) { this.totalResourcesCount = totalResourcesCount; }

        public long getTotalAssignments() { return totalAssignments; }
        public void setTotalAssignments(long totalAssignments) { this.totalAssignments = totalAssignments; }

        public long getTotalSubmissions() { return totalSubmissions; }
        public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }

        public BigDecimal getAssignmentCompletionRate() { return assignmentCompletionRate; }
        public void setAssignmentCompletionRate(BigDecimal assignmentCompletionRate) { this.assignmentCompletionRate = assignmentCompletionRate; }

        public long getOverdueAssignmentsCount() { return overdueAssignmentsCount; }
        public void setOverdueAssignmentsCount(long overdueAssignmentsCount) { this.overdueAssignmentsCount = overdueAssignmentsCount; }

        public BigDecimal getAverageQuizScore() { return averageQuizScore; }
        public void setAverageQuizScore(BigDecimal averageQuizScore) { this.averageQuizScore = averageQuizScore; }

        public long getLearningMaterialViews() { return learningMaterialViews; }
        public void setLearningMaterialViews(long learningMaterialViews) { this.learningMaterialViews = learningMaterialViews; }
    }

    public static class StaffAnalyticsDto {
        private long totalStaffCount;
        private long totalTeachersCount;
        private List<CategoryCountDto> departmentWiseStaff;
        private List<CategoryCountDto> designationWiseStaff;
        private List<TeacherWorkloadDto> teacherWorkload;
        private BigDecimal staffAttendanceRate;

        public StaffAnalyticsDto() {}

        public long getTotalStaffCount() { return totalStaffCount; }
        public void setTotalStaffCount(long totalStaffCount) { this.totalStaffCount = totalStaffCount; }

        public long getTotalTeachersCount() { return totalTeachersCount; }
        public void setTotalTeachersCount(long totalTeachersCount) { this.totalTeachersCount = totalTeachersCount; }

        public List<CategoryCountDto> getDepartmentWiseStaff() { return departmentWiseStaff; }
        public void setDepartmentWiseStaff(List<CategoryCountDto> departmentWiseStaff) { this.departmentWiseStaff = departmentWiseStaff; }

        public List<CategoryCountDto> getDesignationWiseStaff() { return designationWiseStaff; }
        public void setDesignationWiseStaff(List<CategoryCountDto> designationWiseStaff) { this.designationWiseStaff = designationWiseStaff; }

        public List<TeacherWorkloadDto> getTeacherWorkload() { return teacherWorkload; }
        public void setTeacherWorkload(List<TeacherWorkloadDto> teacherWorkload) { this.teacherWorkload = teacherWorkload; }

        public BigDecimal getStaffAttendanceRate() { return staffAttendanceRate; }
        public void setStaffAttendanceRate(BigDecimal staffAttendanceRate) { this.staffAttendanceRate = staffAttendanceRate; }
    }

    // --- Common Helper DTOs ---

    public static class CategoryCountDto {
        private String category;
        private long count;

        public CategoryCountDto() {}
        public CategoryCountDto(String category, long count) {
            this.category = category;
            this.count = count;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }

    public static class SectionDistributionDto {
        private String className;
        private String sectionName;
        private long studentCount;

        public SectionDistributionDto() {}
        public SectionDistributionDto(String className, String sectionName, long studentCount) {
            this.className = className;
            this.sectionName = sectionName;
            this.studentCount = studentCount;
        }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public long getStudentCount() { return studentCount; }
        public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
    }

    public static class GenderDistributionDto {
        private String gender;
        private long count;
        private BigDecimal percentage;

        public GenderDistributionDto() {}
        public GenderDistributionDto(String gender, long count, BigDecimal percentage) {
            this.gender = gender;
            this.count = count;
            this.percentage = percentage;
        }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }

    public static class SessionComparisonDto {
        private String sessionName;
        private long studentCount;

        public SessionComparisonDto() {}
        public SessionComparisonDto(String sessionName, long studentCount) {
            this.sessionName = sessionName;
            this.studentCount = studentCount;
        }

        public String getSessionName() { return sessionName; }
        public void setSessionName(String sessionName) { this.sessionName = sessionName; }
        public long getStudentCount() { return studentCount; }
        public void setStudentCount(long studentCount) { this.studentCount = studentCount; }
    }

    public static class AbsenteeismTrendDto {
        private String dayOfWeek;
        private BigDecimal absenceRate;

        public AbsenteeismTrendDto() {}
        public AbsenteeismTrendDto(String dayOfWeek, BigDecimal absenceRate) {
            this.dayOfWeek = dayOfWeek;
            this.absenceRate = absenceRate;
        }

        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public BigDecimal getAbsenceRate() { return absenceRate; }
        public void setAbsenceRate(BigDecimal absenceRate) { this.absenceRate = absenceRate; }
    }

    public static class ClassAttendanceDto {
        private String className;
        private BigDecimal attendanceRate;

        public ClassAttendanceDto() {}
        public ClassAttendanceDto(String className, BigDecimal attendanceRate) {
            this.className = className;
            this.attendanceRate = attendanceRate;
        }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public BigDecimal getAttendanceRate() { return attendanceRate; }
        public void setAttendanceRate(BigDecimal attendanceRate) { this.attendanceRate = attendanceRate; }
    }

    public static class ChronicAbsenteeDto {
        private Long studentId;
        private String admissionNumber;
        private String studentName;
        private String className;
        private String sectionName;
        private long totalAbsences;
        private BigDecimal attendanceRate;

        public ChronicAbsenteeDto() {}
        public ChronicAbsenteeDto(Long studentId, String admissionNumber, String studentName, String className, String sectionName, long totalAbsences, BigDecimal attendanceRate) {
            this.studentId = studentId;
            this.admissionNumber = admissionNumber;
            this.studentName = studentName;
            this.className = className;
            this.sectionName = sectionName;
            this.totalAbsences = totalAbsences;
            this.attendanceRate = attendanceRate;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getAdmissionNumber() { return admissionNumber; }
        public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public long getTotalAbsences() { return totalAbsences; }
        public void setTotalAbsences(long totalAbsences) { this.totalAbsences = totalAbsences; }
        public BigDecimal getAttendanceRate() { return attendanceRate; }
        public void setAttendanceRate(BigDecimal attendanceRate) { this.attendanceRate = attendanceRate; }
    }

    public static class AttendanceDistributionDto {
        private String band;
        private long count;
        private BigDecimal percentage;

        public AttendanceDistributionDto() {}
        public AttendanceDistributionDto(String band, long count, BigDecimal percentage) {
            this.band = band;
            this.count = count;
            this.percentage = percentage;
        }

        public String getBand() { return band; }
        public void setBand(String band) { this.band = band; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }

    public static class ClassAverageDto {
        private String className;
        private BigDecimal averageMarks;

        public ClassAverageDto() {}
        public ClassAverageDto(String className, BigDecimal averageMarks) {
            this.className = className;
            this.averageMarks = averageMarks;
        }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public BigDecimal getAverageMarks() { return averageMarks; }
        public void setAverageMarks(BigDecimal averageMarks) { this.averageMarks = averageMarks; }
    }

    public static class SubjectPerformanceDto {
        private String subjectName;
        private BigDecimal averageMarks;
        private BigDecimal highestMarks;
        private BigDecimal passRate;

        public SubjectPerformanceDto() {}
        public SubjectPerformanceDto(String subjectName, BigDecimal averageMarks, BigDecimal highestMarks, BigDecimal passRate) {
            this.subjectName = subjectName;
            this.averageMarks = averageMarks;
            this.highestMarks = highestMarks;
            this.passRate = passRate;
        }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public BigDecimal getAverageMarks() { return averageMarks; }
        public void setAverageMarks(BigDecimal averageMarks) { this.averageMarks = averageMarks; }
        public BigDecimal getHighestMarks() { return highestMarks; }
        public void setHighestMarks(BigDecimal highestMarks) { this.highestMarks = highestMarks; }
        public BigDecimal getPassRate() { return passRate; }
        public void setPassRate(BigDecimal passRate) { this.passRate = passRate; }
    }

    public static class GradeDistributionDto {
        private String grade;
        private long count;
        private BigDecimal percentage;

        public GradeDistributionDto() {}
        public GradeDistributionDto(String grade, long count, BigDecimal percentage) {
            this.grade = grade;
            this.count = count;
            this.percentage = percentage;
        }

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }

    public static class AcademicProgressDto {
        private String termOrExamName;
        private BigDecimal averagePercentage;

        public AcademicProgressDto() {}
        public AcademicProgressDto(String termOrExamName, BigDecimal averagePercentage) {
            this.termOrExamName = termOrExamName;
            this.averagePercentage = averagePercentage;
        }

        public String getTermOrExamName() { return termOrExamName; }
        public void setTermOrExamName(String termOrExamName) { this.termOrExamName = termOrExamName; }
        public BigDecimal getAveragePercentage() { return averagePercentage; }
        public void setAveragePercentage(BigDecimal averagePercentage) { this.averagePercentage = averagePercentage; }
    }

    public static class StudentAcademicSupportDto {
        private Long studentId;
        private String admissionNumber;
        private String studentName;
        private String className;
        private BigDecimal averageMarks;
        private int failingSubjectsCount;

        public StudentAcademicSupportDto() {}
        public StudentAcademicSupportDto(Long studentId, String admissionNumber, String studentName, String className, BigDecimal averageMarks, int failingSubjectsCount) {
            this.studentId = studentId;
            this.admissionNumber = admissionNumber;
            this.studentName = studentName;
            this.className = className;
            this.averageMarks = averageMarks;
            this.failingSubjectsCount = failingSubjectsCount;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getAdmissionNumber() { return admissionNumber; }
        public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public BigDecimal getAverageMarks() { return averageMarks; }
        public void setAverageMarks(BigDecimal averageMarks) { this.averageMarks = averageMarks; }
        public int getFailingSubjectsCount() { return failingSubjectsCount; }
        public void setFailingSubjectsCount(int failingSubjectsCount) { this.failingSubjectsCount = failingSubjectsCount; }
    }

    public static class MonthlyRevenueDto {
        private String month;
        private BigDecimal billedAmount;
        private BigDecimal collectedAmount;

        public MonthlyRevenueDto() {}
        public MonthlyRevenueDto(String month, BigDecimal billedAmount, BigDecimal collectedAmount) {
            this.month = month;
            this.billedAmount = billedAmount;
            this.collectedAmount = collectedAmount;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public BigDecimal getBilledAmount() { return billedAmount; }
        public void setBilledAmount(BigDecimal billedAmount) { this.billedAmount = billedAmount; }
        public BigDecimal getCollectedAmount() { return collectedAmount; }
        public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }
    }

    public static class ClassCollectionDto {
        private String className;
        private BigDecimal billedAmount;
        private BigDecimal collectedAmount;
        private BigDecimal outstandingAmount;

        public ClassCollectionDto() {}
        public ClassCollectionDto(String className, BigDecimal billedAmount, BigDecimal collectedAmount, BigDecimal outstandingAmount) {
            this.className = className;
            this.billedAmount = billedAmount;
            this.collectedAmount = collectedAmount;
            this.outstandingAmount = outstandingAmount;
        }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        public BigDecimal getBilledAmount() { return billedAmount; }
        public void setBilledAmount(BigDecimal billedAmount) { this.billedAmount = billedAmount; }
        public BigDecimal getCollectedAmount() { return collectedAmount; }
        public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }
        public BigDecimal getOutstandingAmount() { return outstandingAmount; }
        public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    }

    public static class TeacherWorkloadDto {
        private Long teacherId;
        private String teacherName;
        private String employeeCode;
        private int classesCount;
        private int sectionsCount;
        private int subjectsCount;
        private int totalAssignmentsGiven;

        public TeacherWorkloadDto() {}
        public TeacherWorkloadDto(Long teacherId, String teacherName, String employeeCode, int classesCount, int sectionsCount, int subjectsCount, int totalAssignmentsGiven) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
            this.employeeCode = employeeCode;
            this.classesCount = classesCount;
            this.sectionsCount = sectionsCount;
            this.subjectsCount = subjectsCount;
            this.totalAssignmentsGiven = totalAssignmentsGiven;
        }

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public int getClassesCount() { return classesCount; }
        public void setClassesCount(int classesCount) { this.classesCount = classesCount; }
        public int getSectionsCount() { return sectionsCount; }
        public void setSectionsCount(int sectionsCount) { this.sectionsCount = sectionsCount; }
        public int getSubjectsCount() { return subjectsCount; }
        public void setSubjectsCount(int subjectsCount) { this.subjectsCount = subjectsCount; }
        public int getTotalAssignmentsGiven() { return totalAssignmentsGiven; }
        public void setTotalAssignmentsGiven(int totalAssignmentsGiven) { this.totalAssignmentsGiven = totalAssignmentsGiven; }
    }
}
