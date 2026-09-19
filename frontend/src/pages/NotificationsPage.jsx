import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { notificationAPI } from '../services/api';
import toast from 'react-hot-toast';
import { Bell, CheckCheck, Info, CheckCircle, AlertTriangle, XCircle, Settings } from 'lucide-react';

const typeConfig = {
  SUCCESS: { icon: <CheckCircle size={18} />, color: 'var(--accent-green)', bg: 'rgba(16,185,129,0.1)' },
  ERROR: { icon: <XCircle size={18} />, color: 'var(--accent-red)', bg: 'rgba(239,68,68,0.1)' },
  WARNING: { icon: <AlertTriangle size={18} />, color: 'var(--accent-yellow)', bg: 'rgba(245,158,11,0.1)' },
  INFO: { icon: <Info size={18} />, color: 'var(--accent-blue)', bg: 'rgba(99,120,255,0.1)' },
  SYSTEM: { icon: <Settings size={18} />, color: 'var(--accent-purple)', bg: 'rgba(168,85,247,0.1)' },
};

export default function NotificationsPage() {
  const [notifs, setNotifs] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      const res = await notificationAPI.getAll(0, 20);
      setNotifs(res.data.data?.content || []);
    } catch { toast.error('Failed to load notifications'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const markRead = async (id) => {
    try {
      await notificationAPI.markAsRead(id);
      setNotifs(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
    } catch {}
  };

  const markAll = async () => {
    try {
      await notificationAPI.markAllAsRead();
      setNotifs(prev => prev.map(n => ({ ...n, read: true })));
      toast.success('All marked as read');
    } catch { toast.error('Failed to mark all'); }
  };

  const unreadCount = notifs.filter(n => !n.isRead && !n.read).length;
  const formatDate = (d) => {
    if (!d) return '';
    const date = new Date(d);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  };

  return (
    <AppLayout title="Notifications">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">Notifications</h1>
            <p className="page-subtitle">{unreadCount} unread notification{unreadCount !== 1 ? 's' : ''}</p>
          </div>
          {unreadCount > 0 && (
            <button className="btn btn-secondary btn-sm" onClick={markAll}>
              <CheckCheck size={16} /> Mark All Read
            </button>
          )}
        </div>

        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', overflow: 'hidden' }}>
          {loading ? (
            <div style={{ padding: '2rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {[1,2,3].map(i => <div key={i} className="skeleton" style={{ height: 70, borderRadius: 8 }} />)}
            </div>
          ) : notifs.length === 0 ? (
            <div className="empty-state" style={{ padding: '4rem' }}>
              <Bell size={48} style={{ opacity: 0.3 }} />
              <div className="empty-state-title">No notifications yet</div>
              <div className="empty-state-desc">You'll receive notifications when you complete interviews, upload resumes, or get system updates.</div>
            </div>
          ) : (
            notifs.map(n => {
              const cfg = typeConfig[n.type] || typeConfig.INFO;
              return (
                <div
                  key={n.id}
                  className={`notification-item ${!n.read && !n.isRead ? 'unread' : ''}`}
                  onClick={() => !n.read && !n.isRead && markRead(n.id)}
                  style={{ cursor: !n.read && !n.isRead ? 'pointer' : 'default' }}
                >
                  <div className="notif-icon" style={{ background: cfg.bg, color: cfg.color }}>
                    {cfg.icon}
                  </div>
                  <div className="notif-content">
                    <div className="notif-title">{n.title}</div>
                    <div className="notif-message">{n.message}</div>
                    <div className="notif-time">{formatDate(n.createdAt)}</div>
                  </div>
                  {(!n.read && !n.isRead) && (
                    <div style={{ width: 8, height: 8, borderRadius: '50%', background: 'var(--accent-blue)', flexShrink: 0, marginTop: 6 }} />
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>
    </AppLayout>
  );
}
