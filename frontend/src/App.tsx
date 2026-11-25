import { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { ForbiddenPage } from './components/auth/ForbiddenPage';
import { PublicWebsite } from './pages/public/PublicWebsite';
import { LoginPage } from './pages/LoginPage';

// Role-specific Dashboards (Code-split with dynamic imports)
const StudentDashboard = lazy(() => import('./pages/dashboards/StudentDashboard').then(m => ({ default: m.StudentDashboard })));
const ParentDashboard = lazy(() => import('./pages/dashboards/ParentDashboard').then(m => ({ default: m.ParentDashboard })));
const TeacherDashboard = lazy(() => import('./pages/dashboards/TeacherDashboard').then(m => ({ default: m.TeacherDashboard })));
const AccountantDashboard = lazy(() => import('./pages/dashboards/AccountantDashboard').then(m => ({ default: m.AccountantDashboard })));
const AdminDashboard = lazy(() => import('./pages/dashboards/AdminDashboard').then(m => ({ default: m.AdminDashboard })));
const PrincipalDashboard = lazy(() => import('./pages/dashboards/PrincipalDashboard').then(m => ({ default: m.PrincipalDashboard })));
const VicePrincipalDashboard = lazy(() => import('./pages/dashboards/VicePrincipalDashboard').then(m => ({ default: m.VicePrincipalDashboard })));

// Module Pages (Code-split with dynamic imports)
const AcademicsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.AcademicsPage })));
const AdmissionsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.AdmissionsPage })));
const StudentsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.StudentsPage })));
const StaffPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.StaffPage })));
const TimetablePage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.TimetablePage })));
const AttendancePage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.AttendancePage })));
const LmsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.LmsPage })));
const ExaminationsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.ExaminationsPage })));
const FeesPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.FeesPage })));
const ReportsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.ReportsPage })));
const AnalyticsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.AnalyticsPage })));
const StudentDevelopmentPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.StudentDevelopmentPage })));
const StudentIdCardsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.StudentIdCardsPage })));
const CertificatesPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.CertificatesPage })));
const SupportCentrePage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.SupportCentrePage })));
const IntegrationsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.IntegrationsPage })));
const SettingsPage = lazy(() => import('./pages/ModulePages').then(m => ({ default: m.SettingsPage })));
const BulkDataImportPage = lazy(() => import('./pages/admin/BulkDataImportPage').then(m => ({ default: m.BulkDataImportPage })));

function PageFallback() {
  return (
    <div className="flex items-center justify-center min-h-[300px] py-16">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-3 border-[#2167B2] border-t-transparent rounded-full animate-spin" />
        <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Loading...</span>
      </div>
    </div>
  );
}

/**
 * Dynamically resolves and renders the authoritative dashboard for the active role.
 * Ensures students see only their personal student portal, parents see their children's portal,
 * teachers see their classroom workspace, accountants see their finance ledger,
 * and leadership (Principal / Vice Principal) see institutional management consoles.
 */
function RoleDashboardRouter() {
  const { user } = useAuth();
  const roles = user?.roles || [];

  if (roles.includes('ROLE_STUDENT')) {
    return <StudentDashboard />;
  }
  if (roles.includes('ROLE_PARENT')) {
    return <ParentDashboard />;
  }
  if (roles.includes('ROLE_TEACHER')) {
    return <TeacherDashboard />;
  }
  if (roles.includes('ROLE_ACCOUNTANT')) {
    return <AccountantDashboard />;
  }
  if (roles.includes('ROLE_PRINCIPAL')) {
    return <PrincipalDashboard />;
  }
  if (roles.includes('ROLE_VICE_PRINCIPAL')) {
    return <VicePrincipalDashboard />;
  }
  return <AdminDashboard />;
}

export function App() {
  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        {/* Public Landing & Institutional Showcase */}
      <Route path="/welcome" element={<PublicWebsite />} />

      {/* Authentication Portal */}
      <Route path="/login" element={<LoginPage />} />

      {/* Portal Layout (AppLayout renders LoginPage when user is unauthenticated) */}
      <Route path="/" element={<AppLayout />}>
        {/* Dynamic Role-Based Landing Dashboard */}
        <Route index element={<RoleDashboardRouter />} />

        {/* Academics: Staff, Leadership, and Teachers */}
        <Route 
          path="academics" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER']}>
              <AcademicsPage />
            </ProtectedRoute>
          } 
        />

        {/* Admissions: Administration and Leadership */}
        <Route 
          path="admissions" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL']}>
              <AdmissionsPage />
            </ProtectedRoute>
          } 
        />

        {/* Student Directory: Administration, Leadership, and Accounts */}
        <Route 
          path="students" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT']}>
              <StudentsPage />
            </ProtectedRoute>
          } 
        />

        {/* Staff & Faculty Directory */}
        <Route 
          path="staff" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL']}>
              <StaffPage />
            </ProtectedRoute>
          } 
        />

        {/* Timetable: All active roles */}
        <Route 
          path="timetable" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <TimetablePage />
            </ProtectedRoute>
          } 
        />

        {/* Attendance: All active roles */}
        <Route 
          path="attendance" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <AttendancePage />
            </ProtectedRoute>
          } 
        />

        {/* LMS & Coursework: Academic staff, teachers, students, and parents */}
        <Route 
          path="lms" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <LmsPage />
            </ProtectedRoute>
          } 
        />

        {/* Examinations: Academic staff, teachers, students, and parents */}
        <Route 
          path="examinations" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <ExaminationsPage />
            </ProtectedRoute>
          } 
        />

        {/* Fees & Finance: Administration, Leadership, Accountants, Parents, and Students */}
        <Route 
          path="fees" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT', 'ROLE_PARENT', 'ROLE_STUDENT']}>
              <FeesPage />
            </ProtectedRoute>
          } 
        />

        {/* Management Analytics */}
        <Route 
          path="analytics" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT']}>
              <AnalyticsPage />
            </ProtectedRoute>
          } 
        />

        {/* Student Development Centre: All active roles */}
        <Route 
          path="development" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <StudentDevelopmentPage />
            </ProtectedRoute>
          } 
        />

        {/* Digital Student ID Cards: All active roles */}
        <Route 
          path="id-cards" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <StudentIdCardsPage />
            </ProtectedRoute>
          } 
        />

        {/* Digital Certificates & Honors: All active roles */}
        <Route 
          path="certificates" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <CertificatesPage />
            </ProtectedRoute>
          } 
        />

        {/* Support & Complaint Centre: All active roles */}
        <Route 
          path="support" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT', 'ROLE_ACCOUNTANT']}>
              <SupportCentrePage />
            </ProtectedRoute>
          } 
        />

        {/* System Integrations Console: Leadership */}
        <Route 
          path="integrations" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL']}>
              <IntegrationsPage />
            </ProtectedRoute>
          } 
        />

        {/* Reports & Analytics: Administration, Leadership, and Accountants */}
        <Route 
          path="reports" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACCOUNTANT']}>
              <ReportsPage />
            </ProtectedRoute>
          } 
        />

        {/* Bulk Data Import: Super Admin, Principal, Vice Principal */}
        <Route 
          path="admin/data-import" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL']}>
              <BulkDataImportPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="admin/bulk-import" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL']}>
              <BulkDataImportPage />
            </ProtectedRoute>
          } 
        />

        {/* School Settings: Super Admins, School Admins, and Principals */}
        <Route 
          path="settings" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL']}>
              <SettingsPage />
            </ProtectedRoute>
          } 
        />

        {/* Fallback unauthorized page */}
        <Route path="*" element={<ForbiddenPage />} />
      </Route>
    </Routes>
    </Suspense>
  );
}

export default App;
