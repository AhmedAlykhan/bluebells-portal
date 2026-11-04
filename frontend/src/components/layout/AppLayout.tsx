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
  const { user, logout, switchPersona } = useAuth();

  useEffect(() => {
    apiClient.get<{ success: boolean; data: SchoolProfile }>('/school/profile')
      .then((res) => {
        if (res.data && res.data.data) {
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

  const activeRole = user.roles?.[0] || 'ROLE_SCHOOL_ADMIN';

  return (
    <div className="min-h-screen flex flex-col bg-slate-100">
      <Header 
        schoolProfile={profile} 
        activeRole={activeRole} 
        currentUser={user}
        onLogout={logout}
        onSwitchPersona={switchPersona}
      />
      <div className="flex flex-1">
        <Sidebar activeRole={activeRole} />
        <main className="flex-1 p-6 lg:p-8 max-w-7xl">
          <Outlet context={{ schoolProfile: profile, user }} />
        </main>
      </div>
      <Footer schoolProfile={profile} />
    </div>
  );
};
