import Sidebar from './Sidebar';
import Topbar from './Topbar';

export default function AppLayout({ children, title = 'PrepTrack AI', notifCount = 0 }) {
  return (
    <div className="app-layout">
      <Sidebar notifCount={notifCount} />
      <div className="main-content">
        <Topbar title={title} notifCount={notifCount} />
        {children}
      </div>
    </div>
  );
}
