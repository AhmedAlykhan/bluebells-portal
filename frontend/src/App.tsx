import { Routes, Route } from 'react-router-dom';
import { AppLayout } from './components/layout/AppLayout';
import { useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { ForbiddenPage } from './components/auth/ForbiddenPage';

// Role-specific Dashboards
import { StudentDashboard } from './pages/dashboards/StudentDashboard';
import { ParentDashboard } from './pages/dashboards/ParentDashboard';
import { TeacherDashboard } from './pages/dashboards/TeacherDashboard';
import { AccountantDashboard } from './pages/dashboards/AccountantDashboard';
import { AdminDashboard } from './pages/dashboards/AdminDashboard';

// Module Pages
import { 
  AcademicsPage, 
  AdmissionsPage, 
  StudentsPage, 
  TimetablePage, 
  AttendancePage, 
  LmsPage, 
  ExaminationsPage, 
  FeesPage, 
  ReportsPage, 
  SettingsPage 
} from './pages/ModulePages';

/**
 * Dynamically resolves and renders the specific dashboard for the active role.
 * Ensures students see only their personal student portal, parents see their children's portal,
 * teachers see their classroom workspace, and accountants see their finance ledger.
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
  return <AdminDashboard />;
}

export function App() {
  return (
    <Routes>
      <Route path="/" element={<AppLayout />}>
        {/* Dynamic Role-Based Landing Dashboard */}
        <Route index element={<RoleDashboardRouter />} />

        {/* Academics: Staff and Teachers only */}
        <Route 
          path="academics" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER']}>
              <AcademicsPage />
            </ProtectedRoute>
          } 
        />

        {/* Admissions: School Administration and Principals only */}
        <Route 
          path="admissions" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL']}>
              <AdmissionsPage />
            </ProtectedRoute>
          } 
        />

        {/* Student Directory: Administration and Accounts only */}
        <Route 
          path="students" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT']}>
              <StudentsPage />
            </ProtectedRoute>
          } 
        />

        {/* Timetable: All active roles (views automatically scoped) */}
        <Route 
          path="timetable" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <TimetablePage />
            </ProtectedRoute>
          } 
        />

        {/* Attendance: All active roles (views automatically scoped) */}
        <Route 
          path="attendance" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <AttendancePage />
            </ProtectedRoute>
          } 
        />

        {/* LMS & Coursework: Academic staff, teachers, and enrolled students */}
        <Route 
          path="lms" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT']}>
              <LmsPage />
            </ProtectedRoute>
          } 
        />

        {/* Examinations: Academic staff, teachers, students, and parents */}
        <Route 
          path="examinations" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT']}>
              <ExaminationsPage />
            </ProtectedRoute>
          } 
        />

        {/* Fees & Finance: Administration, Accountants, Parents, and Students (strictly forbidden for Teachers) */}
        <Route 
          path="fees" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT', 'ROLE_PARENT', 'ROLE_STUDENT']}>
              <FeesPage />
            </ProtectedRoute>
          } 
        />

        {/* Reports & Analytics: Senior administration and accountants only */}
        <Route 
          path="reports" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACCOUNTANT']}>
              <ReportsPage />
            </ProtectedRoute>
          } 
        />

        {/* School Settings: Super Admins and School Admins only */}
        <Route 
          path="settings" 
          element={
            <ProtectedRoute allowedRoles={['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN']}>
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
