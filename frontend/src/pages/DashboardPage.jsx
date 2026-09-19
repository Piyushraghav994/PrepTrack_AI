import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { useAuth } from '../context/AuthContext';
import { progressAPI, interviewAPI, notificationAPI, resumeAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { BookOpen, FileText, TrendingUp, Zap, ArrowRight, Target, Flame, Award, Clock } from 'lucide-react';

function StatCard({ icon, label, value, color, gradient }) {
  return (
    <div className="stat-card" style={{ '--accent-color': color }}>
      <div className="stat-icon" style={{ background: gradient || `rgba(${color}, 0.12)` }}>
        {icon}
      </div>
      <div className="stat-info">
        <div className="stat-value">{value ?? '—'}</div>
        <div className="stat-label">{label}</div>
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [progress, setProgress] = useState(null);
  const [interviews, setInterviews] = useState([]);
  const [notifCount, setNotifCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [progRes, intRes] = await Promise.all([
          progressAPI.getMyProgress().catch(() => null),
          interviewAPI.getAll({ page: 0, size: 4 }).catch(() => null),
        ]);
        if (progRes?.data?.data) setProgress(progRes.data.data);
        if (intRes?.data?.data?.content) setInterviews(intRes.data.data.content);

        const notifRes = await notificationAPI.getUnread().catch(() => null);
        if (notifRes?.data?.data) setNotifCount(Array.isArray(notifRes.data.data) ? notifRes.data.data.length : 0);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const initials = user?.fullName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() || 'U';
  const greeting = () => {
    const h = new Date().getHours();
    if (h < 12) return 'Good morning';
    if (h < 18) return 'Good afternoon';
    return 'Good evening';
  };

  const diffColor = (d) => d === 'EASY' ? 'var(--accent-green)' : d === 'MEDIUM' ? 'var(--accent-yellow)' : 'var(--accent-red)';
  const diffClass = (d) => d === 'EASY' ? 'badge-easy' : d === 'MEDIUM' ? 'badge-medium' : 'badge-hard';

  return (
    <AppLayout title="Dashboard" notifCount={notifCount}>
      <div className="page-container animate-fadeInUp">
        {/* Welcome Banner */}
        <div style={{
          background: 'linear-gradient(135deg, rgba(99,120,255,0.15) 0%, rgba(168,85,247,0.1) 100%)',
          border: '1px solid rgba(99,120,255,0.2)',
          borderRadius: 'var(--radius-xl)',
          padding: '2rem 2.5rem',
          marginBottom: '2rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '1.5rem',
          position: 'relative',
          overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', top: -40, right: -40, width: 200, height: 200, borderRadius: '50%', background: 'rgba(99,120,255,0.06)', pointerEvents: 'none' }} />
          <div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '4px' }}>
              {greeting()}, <strong style={{ color: 'var(--text-primary)' }}>{user?.fullName?.split(' ')[0] || 'there'}</strong> 👋
            </div>
            <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.5rem' }}>
              Ready to <span style={{ background: 'var(--gradient-primary)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>level up</span> today?
            </h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              {isAdmin ? 'You have admin access. Manage the platform below.' : 'Practice makes perfect. Start a mock interview or analyze your resume.'}
            </p>
          </div>
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <button className="btn btn-primary" onClick={() => navigate('/interviews')}>
              <BookOpen size={18} /> Start Interview
            </button>
            <button className="btn btn-secondary" onClick={() => navigate('/resume')}>
              <FileText size={18} /> Analyze Resume
            </button>
          </div>
        </div>

        {/* Stats */}
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          <StatCard icon={<BookOpen size={22} color="var(--accent-blue)" />} label="Interviews Done" value={progress?.completedInterviews ?? 0} color="var(--accent-blue)" gradient="rgba(99,120,255,0.12)" />
          <StatCard icon={<Target size={22} color="var(--accent-purple)" />} label="Questions Solved" value={progress?.completedQuestions ?? 0} color="var(--accent-purple)" gradient="rgba(168,85,247,0.12)" />
          <StatCard icon={<Flame size={22} color="var(--accent-orange)" />} label="Day Streak 🔥" value={progress?.currentStreak ?? 0} color="var(--accent-orange)" gradient="rgba(249,115,22,0.12)" />
          <StatCard icon={<Award size={22} color="var(--accent-yellow)" />} label="Total Score" value={progress?.totalScore ?? 0} color="var(--accent-yellow)" gradient="rgba(245,158,11,0.12)" />
        </div>

        {/* Recent Interviews */}
        <div style={{ marginBottom: '2rem' }}>
          <div className="page-header" style={{ marginBottom: '1rem' }}>
            <div>
              <h3 className="page-title" style={{ fontSize: '1.2rem' }}>Available Interviews</h3>
              <p className="page-subtitle">Jump into a mock session right now</p>
            </div>
            <button className="btn btn-ghost btn-sm" onClick={() => navigate('/interviews')}>
              View All <ArrowRight size={14} />
            </button>
          </div>

          {loading ? (
            <div className="grid-2">
              {[1, 2, 3, 4].map(i => (
                <div key={i} className="skeleton" style={{ height: 160, borderRadius: 'var(--radius-lg)' }} />
              ))}
            </div>
          ) : interviews.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">📋</div>
              <div className="empty-state-title">No interviews available</div>
              <div className="empty-state-desc">Check back later or ask an admin to add some!</div>
            </div>
          ) : (
            <div className="grid-2">
              {interviews.map(iv => (
                <div key={iv.id} className="interview-card" onClick={() => navigate(`/interviews/${iv.id}`)}>
                  <div className="interview-company">
                    <div className="company-logo">{iv.company?.[0] || '?'}</div>
                    <div>
                      <div className="interview-title">{iv.title}</div>
                      <div className="interview-role">{iv.company} · {iv.role}</div>
                    </div>
                  </div>
                  <p className="interview-desc">{iv.description}</p>
                  <div className="interview-meta">
                    <span className={`badge ${diffClass(iv.difficulty)}`}>{iv.difficulty}</span>
                    <span className="badge badge-blue">{iv.role}</span>
                  </div>
                  <button className="btn btn-primary btn-sm" style={{ alignSelf: 'flex-start', marginTop: '0.25rem' }}>
                    Start Session <ArrowRight size={14} />
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Quick Actions */}
        <div style={{
          background: 'var(--bg-card)',
          border: '1px solid var(--border)',
          borderRadius: 'var(--radius-lg)',
          padding: '1.5rem',
        }}>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem' }}>Quick Actions</h3>
          <div className="grid-4">
            {[
              { icon: '🎯', label: 'Mock Interview', desc: 'Practice with AI', path: '/interviews', color: '#6378ff' },
              { icon: '📄', label: 'Upload Resume', desc: 'Get ATS score', path: '/resume', color: '#a855f7' },
              { icon: '📊', label: 'View Progress', desc: 'Track your growth', path: '/progress', color: '#10b981' },
              { icon: '🔔', label: 'Notifications', desc: 'Check updates', path: '/notifications', color: '#f59e0b' },
            ].map(a => (
              <div key={a.label}
                onClick={() => navigate(a.path)}
                style={{
                  background: `rgba(${a.color === '#6378ff' ? '99,120,255' : a.color === '#a855f7' ? '168,85,247' : a.color === '#10b981' ? '16,185,129' : '245,158,11'}, 0.06)`,
                  border: `1px solid rgba(${a.color === '#6378ff' ? '99,120,255' : a.color === '#a855f7' ? '168,85,247' : a.color === '#10b981' ? '16,185,129' : '245,158,11'}, 0.15)`,
                  borderRadius: 'var(--radius-md)',
                  padding: '1rem',
                  cursor: 'pointer',
                  transition: 'all var(--transition)',
                  textAlign: 'center',
                }}
                className="card"
              >
                <div style={{ fontSize: '1.6rem', marginBottom: '6px' }}>{a.icon}</div>
                <div style={{ fontSize: '0.88rem', fontWeight: 700, color: 'var(--text-primary)' }}>{a.label}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '2px' }}>{a.desc}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </AppLayout>
  );
}
