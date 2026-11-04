import axios, { AxiosError } from 'axios';
import { ApiError } from '../types';

const apiBase = import.meta.env.VITE_API_URL 
  ? `${import.meta.env.VITE_API_URL.replace(/\/+$/, '')}/api/v1` 
  : '/api/v1';

export const apiClient = axios.create({
  baseURL: apiBase,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('bluebells_token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('bluebells_token');
      localStorage.removeItem('bluebells_user');
      localStorage.setItem('bluebells_logged_out', 'true');
    }
    return Promise.reject(error);
  }
);

export default apiClient;
