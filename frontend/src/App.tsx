import { Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { ForbiddenPage } from './components/auth/ForbiddenPage';
import { PublicWebsite } from './pages/public/PublicWebsite';
import { LoginPage } from './pages/LoginPage';

// Role-specific Dashboards
import { StudentDashboard } from './pages/dashboards/StudentDashboard';
import { ParentDashboard } from './pages/dashboards/ParentDashboard';
import { TeacherDashboard } from './pages/dashboards/TeacherDashboard';
import { AccountantDashboard } from './pages/dashboards/AccountantDashboard';
import { AdminDashboard } from './pages/dashboards/AdminDashboard';
import { PrincipalDashboard } from './pages/dashboards/PrincipalDashboard';
import { VicePrincipalDashboard } from './pages/dashboards/VicePrincipalDashboard';

// Module Pages
import { 
  AcademicsPage, 
  AdmissionsPage, 
  StudentsPage, 
  StaffPage,
  TimetablePage, 
  AttendancePage, 
  LmsPage, 
  ExaminationsPage, 
  FeesPage, 
  ReportsPage, 
  SettingsPage 
} from './pages/ModulePages';
import { BulkDataImportPage } from './pages/admin/BulkDataImportPage';

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
  );
}

export default App;
