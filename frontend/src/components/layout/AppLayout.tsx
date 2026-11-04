import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { Footer } from './Footer';
import { LoginPage } from '../../pages/LoginPage';
import { SchoolProfile } from '../../types';
import { apiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

export const AppLayout: React.FC = () => {
  const [profile, setProfile] = useState<SchoolProfile>();
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false);
  const { user, logout, switchPersona } = useAuth();

  useEffect(() => {
    apiClient.get<{ success: boolean; data: SchoolProfile }>('/school/profile')
      .then((res) => {
        if (res.data?.data) {
          setProfile(res.data.data);
        }
      })
      .catch((err) => {
        console.warn('Using default school profile metadata:', err.message);
      });
  }, []);

  if (!user) {
    return <LoginPage schoolProfile={profile} />;
  }

  // Authoritative role resolution: student > parent > teacher > accountant > principal > admin
  const roles = user.roles || [];
  let activeRole = 'ROLE_SCHOOL_ADMIN';
  if (roles.includes('ROLE_STUDENT')) activeRole = 'ROLE_STUDENT';
  else if (roles.includes('ROLE_PARENT')) activeRole = 'ROLE_PARENT';
  else if (roles.includes('ROLE_TEACHER')) activeRole = 'ROLE_TEACHER';
  else if (roles.includes('ROLE_ACCOUNTANT')) activeRole = 'ROLE_ACCOUNTANT';
  else if (roles.includes('ROLE_PRINCIPAL')) activeRole = 'ROLE_PRINCIPAL';
  else if (roles.includes('ROLE_SUPER_ADMIN')) activeRole = 'ROLE_SUPER_ADMIN';

  return (
    <div className="min-h-screen flex flex-col bg-[#F5F8FC] print:bg-white print:min-h-0 selection:bg-brand-100 selection:text-brand-900">
      <div className="print:hidden">
        <Header 
          schoolProfile={profile} 
          activeRole={activeRole} 
          currentUser={user}
          onLogout={logout}
          onSwitchPersona={switchPersona}
          onToggleMobileSidebar={() => setIsMobileSidebarOpen(!isMobileSidebarOpen)}
        />
      </div>

      <div className="flex flex-1 print:block">
        <div className="print:hidden">
          <Sidebar 
            activeRole={activeRole} 
            isOpenMobile={isMobileSidebarOpen}
            onCloseMobile={() => setIsMobileSidebarOpen(false)}
          />
        </div>

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto print:p-0 print:m-0 print:max-w-none print:w-full">
          <Outlet context={{ schoolProfile: profile, user }} />
        </main>
      </div>

      <div className="print:hidden">
        <Footer schoolProfile={profile} />
      </div>
    </div>
  );
};
