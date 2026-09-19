import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { userAPI, roleAPI, permissionAPI } from '../services/api';
import toast from 'react-hot-toast';
import { Users, Shield, Key, Trash2, Plus, Search, ToggleLeft, ToggleRight } from 'lucide-react';

function UsersTab() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const res = await userAPI.getAllUsers(p, 10);
      setUsers(res.data.data?.content || []);
      setTotalPages(res.data.data?.totalPages || 0);
      setPage(p);
    } catch { toast.error('Failed to load users'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const toggleStatus = async (u) => {
    try {
      await userAPI.updateUserStatus(u.id, { enabled: !u.enabled });
      setUsers(prev => prev.map(x => x.id === u.id ? { ...x, enabled: !u.enabled } : x));
      toast.success(`User ${!u.enabled ? 'enabled' : 'disabled'}`);
    } catch { toast.error('Failed to update status'); }
  };

  const deleteUser = async (id) => {
    if (!confirm('Delete this user? This cannot be undone.')) return;
    try {
      await userAPI.deleteUser(id);
      setUsers(prev => prev.filter(u => u.id !== id));
      toast.success('User deleted');
    } catch { toast.error('Failed to delete user'); }
  };

  const filtered = users.filter(u => !search || u.fullName?.toLowerCase().includes(search.toLowerCase()) || u.email?.toLowerCase().includes(search.toLowerCase()));

  return (
    <div>
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <div className="search-input" style={{ flex: 1 }}>
          <div className="input-wrapper">
            <Search className="input-icon" size={16} />
            <input className="form-input" placeholder="Search users..." value={search} onChange={e => setSearch(e.target.value)} />
          </div>
        </div>
        <button className="btn btn-ghost btn-sm" onClick={() => load(page)}>Refresh</button>
      </div>

      <div className="table-wrapper">
        <table className="table">
          <thead>
            <tr>
              <th>User</th>
              <th>Role</th>
              <th>College</th>
              <th>Passout</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>Loading...</td></tr>
            ) : filtered.length === 0 ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>No users found</td></tr>
            ) : filtered.map(u => (
              <tr key={u.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <div style={{ width: 34, height: 34, borderRadius: '50%', background: 'var(--gradient-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.75rem', fontWeight: 700, color: 'white', flexShrink: 0 }}>
                      {u.fullName?.[0]?.toUpperCase() || 'U'}
                    </div>
                    <div>
                      <div className="text-primary-col">{u.fullName}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{u.email}</div>
                    </div>
                  </div>
                </td>
                <td><span className={`badge ${u.role?.name === 'ROLE_ADMIN' ? 'badge-purple' : 'badge-blue'}`}>{u.role?.name === 'ROLE_ADMIN' ? 'Admin' : 'Student'}</span></td>
                <td>{u.college || '—'}</td>
                <td>{u.passoutYear || '—'}</td>
                <td>
                  <span className={`badge ${u.enabled ? 'badge-success' : 'badge-error'}`}>
                    {u.enabled ? 'Active' : 'Disabled'}
                  </span>
                </td>
                <td>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    <button className="btn btn-ghost btn-icon btn-sm" title={u.enabled ? 'Disable' : 'Enable'} onClick={() => toggleStatus(u)}>
                      {u.enabled ? <ToggleRight size={16} style={{ color: 'var(--accent-green)' }} /> : <ToggleLeft size={16} style={{ color: 'var(--text-muted)' }} />}
                    </button>
                    <button className="btn btn-danger btn-icon btn-sm" title="Delete" onClick={() => deleteUser(u.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="flex justify-center gap-1 mt-2">
          {[...Array(totalPages)].map((_, i) => (
            <button key={i} className={`btn btn-sm ${page === i ? 'btn-primary' : 'btn-ghost'}`} onClick={() => load(i)}>{i + 1}</button>
          ))}
        </div>
      )}
    </div>
  );
}

function RolesTab() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [name, setName] = useState('');

  const load = async () => {
    try {
      const res = await roleAPI.getAll();
      setRoles(res.data.data?.content || (Array.isArray(res.data.data) ? res.data.data : []));
    } catch { toast.error('Failed to load roles'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const create = async (e) => {
    e.preventDefault();
    try {
      await roleAPI.create({ name });
      toast.success('Role created!');
      setName('');
      setShowCreate(false);
      load();
    } catch (err) { toast.error(err.response?.data?.message || 'Failed'); }
  };

  const del = async (id) => {
    if (!confirm('Delete this role?')) return;
    try { await roleAPI.delete(id); setRoles(prev => prev.filter(r => r.id !== id)); toast.success('Role deleted'); }
    catch { toast.error('Failed to delete role'); }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
        <button className="btn btn-secondary btn-sm" onClick={() => setShowCreate(true)}><Plus size={16} /> New Role</button>
      </div>
      {showCreate && (
        <form onSubmit={create} style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem', background: 'var(--bg-secondary)', padding: '1rem', borderRadius: 'var(--radius-md)' }}>
          <input className="form-input" placeholder="e.g. ROLE_MODERATOR" value={name} onChange={e => setName(e.target.value)} required style={{ flex: 1 }} />
          <button className="btn btn-primary btn-sm" type="submit">Create</button>
          <button className="btn btn-ghost btn-sm" type="button" onClick={() => setShowCreate(false)}>Cancel</button>
        </form>
      )}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        {loading ? <div className="spinner" style={{ alignSelf: 'center', margin: '2rem' }} /> : roles.map(r => (
          <div key={r.id} style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', padding: '1rem 1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <span style={{ fontWeight: 700, fontSize: '0.9rem' }}>{r.name}</span>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: 2 }}>
                {r.permissions?.length || 0} permissions
              </div>
            </div>
            <button className="btn btn-danger btn-sm btn-icon" onClick={() => del(r.id)}><Trash2 size={14} /></button>
          </div>
        ))}
      </div>
    </div>
  );
}

function PermissionsTab() {
  const [perms, setPerms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [name, setName] = useState('');

  const load = async () => {
    try {
      const res = await permissionAPI.getAll();
      setPerms(res.data.data?.content || (Array.isArray(res.data.data) ? res.data.data : []));
    } catch { toast.error('Failed to load permissions'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const create = async (e) => {
    e.preventDefault();
    try {
      await permissionAPI.create({ name });
      toast.success('Permission created!');
      setName('');
      setShowCreate(false);
      load();
    } catch (err) { toast.error(err.response?.data?.message || 'Failed'); }
  };

  const del = async (id) => {
    if (!confirm('Delete this permission?')) return;
    try { await permissionAPI.delete(id); setPerms(prev => prev.filter(p => p.id !== id)); toast.success('Permission deleted'); }
    catch { toast.error('Failed to delete'); }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
        <button className="btn btn-secondary btn-sm" onClick={() => setShowCreate(true)}><Plus size={16} /> New Permission</button>
      </div>
      {showCreate && (
        <form onSubmit={create} style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.5rem', background: 'var(--bg-secondary)', padding: '1rem', borderRadius: 'var(--radius-md)' }}>
          <input className="form-input" placeholder="e.g. REPORT_VIEW" value={name} onChange={e => setName(e.target.value)} required style={{ flex: 1 }} />
          <button className="btn btn-primary btn-sm" type="submit">Create</button>
          <button className="btn btn-ghost btn-sm" type="button" onClick={() => setShowCreate(false)}>Cancel</button>
        </form>
      )}
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem' }}>
        {loading ? <div className="spinner" style={{ margin: '2rem auto' }} /> : perms.map(p => (
          <div key={p.id} style={{ display: 'flex', alignItems: 'center', gap: 8, background: 'rgba(99,120,255,0.08)', border: '1px solid rgba(99,120,255,0.2)', borderRadius: 20, padding: '6px 14px' }}>
            <span style={{ fontSize: '0.82rem', fontWeight: 600, color: 'var(--accent-blue)' }}>{p.name}</span>
            <button onClick={() => del(p.id)} style={{ color: 'var(--text-muted)', cursor: 'pointer', display: 'flex', background: 'none', border: 'none' }}>
              <Trash2 size={12} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function AdminPage() {
  const [tab, setTab] = useState('users');
  const tabs = [
    { id: 'users', label: 'Users', icon: <Users size={16} /> },
    { id: 'roles', label: 'Roles', icon: <Shield size={16} /> },
    { id: 'permissions', label: 'Permissions', icon: <Key size={16} /> },
  ];

  return (
    <AppLayout title="Admin Panel">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">Admin Panel</h1>
            <p className="page-subtitle">Manage users, roles, and permissions</p>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '6px', marginBottom: '2rem', background: 'var(--bg-card)', padding: '6px', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border)', width: 'fit-content' }}>
          {tabs.map(t => (
            <button key={t.id} onClick={() => setTab(t.id)} className={`btn btn-sm ${tab === t.id ? 'btn-primary' : 'btn-ghost'}`} style={{ gap: 8 }}>
              {t.icon} {t.label}
            </button>
          ))}
        </div>

        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '2rem' }}>
          {tab === 'users' && <UsersTab />}
          {tab === 'roles' && <RolesTab />}
          {tab === 'permissions' && <PermissionsTab />}
        </div>
      </div>
    </AppLayout>
  );
}
