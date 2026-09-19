import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, BookOpen, FileText, User,
  Bell, Shield, LogOut, Brain, ChevronRight,
  TrendingUp, Settings
} from 'lucide-react';
import { useState } from 'react';

const navItems = [
  { label: 'Dashboard', icon: LayoutDashboard, path: '/dashboard' },
  { label: 'Interviews', icon: BookOpen, path: '/interviews' },
  { label: 'Resume', icon: FileText, path: '/resume' },
  { label: 'Progress', icon: TrendingUp, path: '/progress' },
  { label: 'Notifications', icon: Bell, path: '/notifications' },
  { label: 'Profile', icon: User, path: '/profile' },
];

const adminItems = [
  { label: 'Admin Panel', icon: Shield, path: '/admin' },
];

export default function Sidebar({ notifCount = 0 }) {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const initials = user?.fullName
    ? user.fullName.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()
    : 'U';

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">🧠</div>
        <span className="sidebar-logo-text">PrepTrack AI</span>
      </div>

      <nav className="sidebar-nav">
        <span className="nav-section-label">Main</span>
        {navItems.map(({ label, icon: Icon, path }) => (
          <NavLink key={path} to={path} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Icon />
            {label}
            {label === 'Notifications' && notifCount > 0 && (
              <span className="nav-badge">{notifCount}</span>
            )}
          </NavLink>
        ))}

        {isAdmin && (
          <>
            <span className="nav-section-label">Administration</span>
            {adminItems.map(({ label, icon: Icon, path }) => (
              <NavLink key={path} to={path} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
                <Icon />
                {label}
              </NavLink>
            ))}
          </>
        )}
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-user" onClick={() => navigate('/profile')}>
          <div className="user-avatar">{initials}</div>
          <div className="user-info">
            <div className="user-name truncate">{user?.fullName || 'User'}</div>
            <div className="user-role">{isAdmin ? 'Admin' : 'Student'}</div>
          </div>
          <ChevronRight size={14} style={{ color: 'var(--text-muted)', flexShrink: 0 }} />
        </div>
        <button className="nav-item w-full mt-1" style={{ color: 'var(--accent-red)' }} onClick={handleLogout}>
          <LogOut size={18} />
          Sign Out
        </button>
      </div>
    </aside>
  );
}
