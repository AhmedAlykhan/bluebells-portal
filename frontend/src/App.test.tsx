import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import App from './App';

function renderApp() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

describe('Bluebells School Management Portal RBAC & Navigation Suite', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  const adminUser = {
    id: 1,
    username: 'admin',
    email: 'admin@bluebells.edu.pk',
    fullName: 'Bluebells Platform Administrator',
    phoneNumber: '+92 316 9681932',
    status: 'ACTIVE',
    roles: ['ROLE_SUPER_ADMIN', 'ROLE_SCHOOL_ADMIN'],
  };

  it('renders login page when unauthenticated visitor visits the portal', () => {
    renderApp();

    expect(screen.getByText(/Sign In to Portal/i)).toBeDefined();
    expect(screen.getByText(/Or Quick Switch Demo Role/i)).toBeDefined();
  });

  it('renders the administrator dashboard with Bluebells School branding and administrative modules', () => {
    localStorage.setItem('bluebells_user', JSON.stringify(adminUser));
    renderApp();

    // Institutional Branding
    expect(screen.getAllByText(/Bluebells Schooling System/i).length).toBeGreaterThan(0);
    expect(screen.getByText(/Academic Session 2026-2027/i)).toBeDefined();
    expect(screen.getByText(/Active Students/i)).toBeDefined();
    expect(screen.getByText(/Hyderabad, Sindh, Pakistan/i)).toBeDefined();

    // Administrative Sidebar Navigation
    expect(screen.getByText('Campus Dashboard')).toBeDefined();
    expect(screen.getByText('Academics')).toBeDefined();
    expect(screen.getByText('Admissions')).toBeDefined();
    expect(screen.getByText('Student Directory')).toBeDefined();
    expect(screen.getByText('Timetable')).toBeDefined();
    expect(screen.getByText('Attendance')).toBeDefined();
    expect(screen.getByText('LMS & Courses')).toBeDefined();
    expect(screen.getByText('Examinations')).toBeDefined();
    expect(screen.getByText('Fees & Finance')).toBeDefined();
    expect(screen.getByText('Reports & Analytics')).toBeDefined();
    expect(screen.getByText('School Settings')).toBeDefined();
  });

  it('renders school profile footer with accurate Hyderabad location', () => {
    localStorage.setItem('bluebells_user', JSON.stringify(adminUser));
    renderApp();

    expect(
      screen.getByText(/Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad/i)
    ).toBeDefined();
  });

  it('enforces student RBAC isolation: student sees personalized portal and cannot access admin modules', () => {
    // Mock authenticated student Ali Khan
    const studentUser = {
      id: 9,
      username: 'student.ali',
      email: 'student.ali@bluebells.edu.pk',
      fullName: 'Ali Khan',
      phoneNumber: '+92 300 1234567',
      status: 'ACTIVE',
      roles: ['ROLE_STUDENT'],
    };
    localStorage.setItem('bluebells_user', JSON.stringify(studentUser));

    renderApp();

    // Student Welcome & Dashboard
    expect(screen.getByText(/Welcome back, Ali Khan/i)).toBeDefined();
    expect(screen.getByText('Student Portal')).toBeDefined();
    expect(screen.getByText('My Attendance Rate')).toBeDefined();
    expect(screen.getByText('October Fee Status')).toBeDefined();
    expect(screen.getByText('View Sindh Board Report Card')).toBeDefined();

    // Student-Scoped Navigation Links
    expect(screen.getByText('Student Dashboard')).toBeDefined();
    expect(screen.getByText('My Timetable')).toBeDefined();
    expect(screen.getByText('My Attendance')).toBeDefined();
    expect(screen.getByText('My Courses & LMS')).toBeDefined();
    expect(screen.getByText('My Report Cards')).toBeDefined();
    expect(screen.getAllByText('My Fee Invoices').length).toBeGreaterThan(0);

    // STRICT RBAC ISOLATION: Admin and Staff links MUST NOT be rendered for students
    expect(screen.queryByText('Campus Dashboard')).toBeNull();
    expect(screen.queryByText('Academics')).toBeNull();
    expect(screen.queryByText('Admissions')).toBeNull();
    expect(screen.queryByText('Student Directory')).toBeNull();
    expect(screen.queryByText('Reports & Analytics')).toBeNull();
    expect(screen.queryByText('School Settings')).toBeNull();
    expect(screen.queryByText('Fees & Finance')).toBeNull();
  });

  it('enforces teacher RBAC isolation: teacher cannot access admissions, fees, or school settings', () => {
    // Mock authenticated teacher Sir Ahmed
    const teacherUser = {
      id: 4,
      username: 'teacher.ahmed',
      email: 'teacher.ahmed@bluebells.edu.pk',
      fullName: 'Ahmed Khan',
      phoneNumber: '+92 301 9876543',
      status: 'ACTIVE',
      roles: ['ROLE_TEACHER'],
    };
    localStorage.setItem('bluebells_user', JSON.stringify(teacherUser));

    renderApp();

    // Teacher navigation items present
    expect(screen.getByText('Teacher Workspace')).toBeDefined();
    expect(screen.getByText('Academics')).toBeDefined();
    expect(screen.getByText('Timetable')).toBeDefined();
    expect(screen.getByText('Attendance')).toBeDefined();
    expect(screen.getByText('LMS & Courses')).toBeDefined();
    expect(screen.getByText('Examinations')).toBeDefined();

    // Restricted modules MUST NOT be rendered for teachers
    expect(screen.queryByText('Admissions')).toBeNull();
    expect(screen.queryByText('Student Directory')).toBeNull();
    expect(screen.queryByText('Fees & Finance')).toBeNull();
    expect(screen.queryByText('My Fee Invoices')).toBeNull();
    expect(screen.queryByText('School Settings')).toBeNull();
  });
});
