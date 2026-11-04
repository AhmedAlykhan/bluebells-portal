import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from '../../context/AuthContext';
import { LmsPage } from './LmsPage';

function renderLmsPage(userRole: string = 'ROLE_TEACHER', userName: string = 'Ahmed Khan') {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  const mockUser = {
    id: 1,
    username: 'test.user',
    email: 'test.user@bluebells.edu.pk',
    fullName: userName,
    phoneNumber: '+92 300 0000000',
    status: 'ACTIVE',
    roles: [userRole],
  };
  localStorage.setItem('bluebells_user', JSON.stringify(mockUser));

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          <LmsPage />
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

describe('Bluebells LMS (Learning Management System) Suite', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('renders course catalog with Class 9 courses and subject filters', () => {
    renderLmsPage('ROLE_TEACHER');

    // Header Branding & Title
    expect(screen.getByText(/Bluebells Learning Management System/i)).toBeDefined();
    expect(screen.getByText(/Courseware, Curricula & Student Grading Hub/i)).toBeDefined();

    // Default Seeded Courses
    expect(screen.getByText(/Matriculation Mathematics Class 9/i)).toBeDefined();
    expect(screen.getByText(/Secondary Physics Class 9/i)).toBeDefined();
    expect(screen.getByText(/Computer Science Class 9/i)).toBeDefined();

    // Subject Filters
    expect(screen.getByText(/All Subjects/i)).toBeDefined();
    expect(screen.getByText(/^Mathematics/i)).toBeDefined();
    expect(screen.getByText(/^Physics/i)).toBeDefined();
    expect(screen.getAllByText(/^Computer Science/i).length).toBeGreaterThan(0);
  });

  it('enters course detail view and displays modules, lessons, and assignments', () => {
    renderLmsPage('ROLE_TEACHER');

    // Click "Enter Course" on Mathematics Class 9
    const enterButtons = screen.getAllByText(/Enter Course/i);
    expect(enterButtons.length).toBeGreaterThan(0);
    fireEvent.click(enterButtons[0]);

    // Course Header in Detail View
    expect(screen.getAllByText(/Matriculation Mathematics Class 9/i).length).toBeGreaterThan(0);
    expect(screen.getByText(/Back to All Courses/i)).toBeDefined();

    // Tabs
    expect(screen.getByText(/Curriculum & Lessons/i)).toBeDefined();
    expect(screen.getByText(/Learning Notes & Manuals/i)).toBeDefined();
    expect(screen.getByText(/Assignments & Tasks/i)).toBeDefined();
    expect(screen.getByText(/Student Submissions & Grading/i)).toBeDefined();

    // Module Chapters
    expect(screen.getByText(/Unit 1: Real and Complex Numbers/i)).toBeDefined();
    expect(screen.getByText(/Unit 2: Logarithms & Laws of Indices/i)).toBeDefined();
  });

  it('allows switching to Materials tab and displays PDF documents & YouTube lectures', () => {
    renderLmsPage('ROLE_TEACHER');

    // Enter Course
    const enterButtons = screen.getAllByText(/Enter Course/i);
    fireEvent.click(enterButtons[0]);

    // Switch to Materials Tab
    const materialsTab = screen.getByText(/Learning Notes & Manuals/i);
    fireEvent.click(materialsTab);

    // Verify materials exist
    expect(screen.getAllByText(/Class 9 Mathematics Unit 1 Comprehensive Study Notes/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/Understanding Complex Numbers: Real & Imaginary Parts/i).length).toBeGreaterThan(0);
  });

  it('allows switching to Assignments tab and displays due dates and passing marks', () => {
    renderLmsPage('ROLE_TEACHER');

    // Enter Course
    const enterButtons = screen.getAllByText(/Enter Course/i);
    fireEvent.click(enterButtons[0]);

    // Switch to Assignments Tab
    const assignmentsTab = screen.getByText(/Assignments & Tasks/i);
    fireEvent.click(assignmentsTab);

    // Verify Assignment Details
    expect(screen.getByText(/Exercise 1.2 Mathematical Proofs & Complex Conjugates/i)).toBeDefined();
    expect(screen.getAllByText(/Max Marks:/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/Passing:/i).length).toBeGreaterThan(0);
  });

  it('shows student personalized view with student progress and homework submission buttons', () => {
    renderLmsPage('ROLE_STUDENT', 'Ali Khan');

    // Student Header
    expect(screen.getByText(/My Matriculation Coursework & Learning Modules/i)).toBeDefined();

    // Enter Course
    const enterButtons = screen.getAllByText(/Enter Course/i);
    fireEvent.click(enterButtons[0]);

    // Switch to Assignments Tab
    const assignmentsTab = screen.getByText(/Assignments & Tasks/i);
    fireEvent.click(assignmentsTab);

    // Should display student submission status
    expect(screen.getAllByText(/Status: GRADED/i).length).toBeGreaterThan(0);
  });
});
