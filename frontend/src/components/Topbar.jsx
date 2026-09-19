import { Bell, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Topbar({ title, notifCount = 0 }) {
  const navigate = useNavigate();
  return (
    <header className="topbar">
      <span className="topbar-title">{title}</span>
      <div className="topbar-actions">
        <button className="topbar-btn" onClick={() => navigate('/notifications')}>
          <Bell size={18} />
          {notifCount > 0 && <span className="dot" />}
        </button>
      </div>
    </header>
  );
}
