import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { AnalyticsPage } from './pages/modules/AnalyticsPage';
import { StudentDevelopmentPage } from './pages/modules/StudentDevelopmentPage';
import { StudentIdCardsPage } from './pages/modules/StudentIdCardsPage';
import { CertificatesPage } from './pages/modules/CertificatesPage';
import { SupportCentrePage } from './pages/modules/SupportCentrePage';
import { IntegrationsPage } from './pages/modules/IntegrationsPage';

function renderWithProviders(ui: React.ReactElement, user: any) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  localStorage.setItem('bluebells_user', JSON.stringify(user));
  localStorage.setItem('bluebells_token', 'mock_jwt_token');

  return render(
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          {ui}
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

describe('Bluebells Advanced Modules UI Suite', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  const superAdminUser = {
    id: 1,
    username: 'admin',
    email: 'admin@bluebells.edu.pk',
    fullName: 'Principal Administrator',
    roles: ['ROLE_SUPER_ADMIN', 'ROLE_PRINCIPAL'],
  };

  const accountantUser = {
    id: 2,
    username: 'accountant',
    email: 'finance@bluebells.edu.pk',
    fullName: 'School Accountant',
    roles: ['ROLE_ACCOUNTANT'],
  };

  const parentUser = {
    id: 5,
    username: 'parent.tariq',
    email: 'tariq.khan@gmail.com',
    fullName: 'Tariq Khan',
    roles: ['ROLE_PARENT'],
  };

  describe('Management Analytics UI & Accountant Isolation', () => {
    it('renders all 7 tabs for Super Admin / Leadership', () => {
      renderWithProviders(<AnalyticsPage />, superAdminUser);

      expect(screen.getByText('Management Analytics')).toBeDefined();
      expect(screen.getByText('Overview Executive Summary')).toBeDefined();
      expect(screen.getByText('Student Demographics')).toBeDefined();
      expect(screen.getByText('Attendance & Absenteeism')).toBeDefined();
      expect(screen.getByText('Academic Performance')).toBeDefined();
      expect(screen.getByText('Financial & Fee Ledger')).toBeDefined();
      expect(screen.getByText('LMS & Coursework Engagement')).toBeDefined();
      expect(screen.getByText('Faculty & Workload')).toBeDefined();
    });

    it('enforces least-privilege tab isolation for Accountant: only displays Financial tab', () => {
      renderWithProviders(<AnalyticsPage />, accountantUser);

      // Financial tab MUST be present
      expect(screen.getByText('Financial & Fee Ledger')).toBeDefined();

      // Non-financial tabs MUST NOT be rendered for Accountant
      expect(screen.queryByText('Overview Executive Summary')).toBeNull();
      expect(screen.queryByText('Student Demographics')).toBeNull();
      expect(screen.queryByText('Attendance & Absenteeism')).toBeNull();
      expect(screen.queryByText('Academic Performance')).toBeNull();
      expect(screen.queryByText('LMS & Coursework Engagement')).toBeNull();
      expect(screen.queryByText('Faculty & Workload')).toBeNull();
    });
  });

  describe('Student Development & Achievement Module UI', () => {
    it('renders development hub with verified achievements and skills tracking', () => {
      renderWithProviders(<StudentDevelopmentPage />, superAdminUser);

      expect(screen.getByText(/Student Development Centre/i)).toBeDefined();
      expect(screen.getByText(/Beyond Examination Results/i)).toBeDefined();
      expect(screen.getByText(/Record Achievement \/ Milestone/i)).toBeDefined();
    });
  });

  describe('Student Digital ID Cards Module UI', () => {
    it('renders student identity card generation and management hub', () => {
      renderWithProviders(<StudentIdCardsPage />, superAdminUser);

      expect(screen.getByText(/Digital & Printable Student ID Cards/i)).toBeDefined();
      expect(screen.getByText(/Identity & Credential Management/i)).toBeDefined();
    });
  });

  describe('Official School Certificates Module UI', () => {
    it('renders certificate generation and validation hub', () => {
      renderWithProviders(<CertificatesPage />, superAdminUser);

      expect(screen.getByText(/Digital Certificates & Honors/i)).toBeDefined();
      expect(screen.getByText(/Digital Credentialing & Honors/i)).toBeDefined();
      expect(screen.getByText(/Issue New Certificate/i)).toBeDefined();
    });
  });

  describe('Support & Help Desk Centre UI', () => {
    it('renders support centre with ticket creation and privacy indicators', () => {
      renderWithProviders(<SupportCentrePage />, parentUser);

      expect(screen.getByText(/Support & Complaint Centre/i)).toBeDefined();
      expect(screen.getByText(/Parent, Student & Faculty Helpdesk/i)).toBeDefined();
      expect(screen.getByText(/Open Support Ticket/i)).toBeDefined();
    });
  });

  describe('Integration & API Console UI', () => {
    it('renders system integrations management console for Administrator', () => {
      renderWithProviders(<IntegrationsPage />, superAdminUser);

      expect(screen.getByText(/System Integrations Console/i)).toBeDefined();
      expect(screen.getByText(/Extensibility & External Connectors/i)).toBeDefined();
      expect(screen.getByText(/Plug & Play Credential Architecture/i)).toBeDefined();
    });
  });
});
