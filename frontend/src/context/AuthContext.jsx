import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authAPI, userAPI } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadProfile = useCallback(async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) { setLoading(false); return; }
    try {
      const res = await userAPI.getProfile();
      setUser(res.data.data);
    } catch {
      localStorage.clear();
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadProfile(); }, [loadProfile]);

  const login = async (credentials) => {
    const res = await authAPI.login(credentials);
    const data = res.data.data;
    const token = data.token || data.accessToken;
    const refreshToken = data.refreshToken;
    if (token) localStorage.setItem('accessToken', token);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
    setUser(data);
    return res.data;
  };

  const register = async (formData) => {
    const res = await authAPI.register(formData);
    const data = res.data.data;
    const token = data.token || data.accessToken;
    const refreshToken = data.refreshToken;
    if (token) localStorage.setItem('accessToken', token);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
    setUser(data);
    return res.data;
  };

  const logout = async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    try { await authAPI.logout(refreshToken); } catch {}
    localStorage.clear();
    setUser(null);
  };

  const refreshUser = () => loadProfile();

  const isAdmin = user?.role === 'ROLE_ADMIN' || user?.role?.name === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, refreshUser, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};
