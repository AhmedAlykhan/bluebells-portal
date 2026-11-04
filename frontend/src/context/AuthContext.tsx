import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, ApiResponse } from '../types';
import { apiClient } from '../api/client';

export interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (usernameOrEmail: string, password: string) => Promise<void>;
  logout: () => void;
  switchPersona: (username: string) => Promise<void>;
  isLoading: boolean;
  error: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    // If user explicitly logged out or no user stored, return null
    if (localStorage.getItem('bluebells_logged_out') === 'true') {
      return null;
    }
    const saved = localStorage.getItem('bluebells_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        return null;
      }
    }
    return null;
  });

  const [token, setToken] = useState<string | null>(() => {
    if (localStorage.getItem('bluebells_logged_out') === 'true') {
      return null;
    }
    return localStorage.getItem('bluebells_token');
  });

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // If token exists, verify current user profile
    if (token) {
      apiClient.get<ApiResponse<User>>('/auth/me')
        .then((res) => {
          if (res.data?.data) {
            setUser(res.data.data);
            localStorage.setItem('bluebells_user', JSON.stringify(res.data.data));
          }
        })
        .catch(() => {
          // Keep current user state
        });
    }
  }, [token]);

  const login = async (usernameOrEmail: string, password: string): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await apiClient.post<ApiResponse<{ accessToken: string; user: User }>>('/auth/login', {
        usernameOrEmail,
        password
      });

      if (res.data?.data) {
        const { accessToken, user: loggedInUser } = res.data.data;
        setToken(accessToken);
        setUser(loggedInUser);
        localStorage.removeItem('bluebells_logged_out');
        localStorage.setItem('bluebells_token', accessToken);
        localStorage.setItem('bluebells_user', JSON.stringify(loggedInUser));
      }
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { detail?: string } } })?.response?.data?.detail 
        || 'Authentication failed. Please check username and password.';
      setError(message);
      throw new Error(message);
    } finally {
      setIsLoading(false);
    }
  };

  const switchPersona = async (username: string): Promise<void> => {
    return login(username, 'Admin@12345');
  };

  const logout = () => {
    localStorage.setItem('bluebells_logged_out', 'true');
    localStorage.removeItem('bluebells_token');
    localStorage.removeItem('bluebells_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, switchPersona, isLoading, error }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
