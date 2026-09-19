import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { userAPI, authAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { User, Mail, Building, GraduationCap, Phone, Link2, GitBranch, Lock, Save, Edit3, Calendar } from 'lucide-react';

export default function ProfilePage() {
  const { user, refreshUser } = useAuth();
  const [form, setForm] = useState(null);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [pwForm, setPwForm] = useState({ oldPassword: '', newPassword: '', confirmPassword: '' });
  const [changingPw, setChangingPw] = useState(false);
  const [showPwSection, setShowPwSection] = useState(false);

  useEffect(() => {
    userAPI.getProfile().then(r => {
      const d = r.data.data;
      setForm({
        fullName: d.fullName || '',
        college: d.college || '',
        branch: d.branch || '',
        passoutYear: d.passoutYear || '',
        phoneNumber: d.phoneNumber || '',
        linkedinUrl: d.linkedinUrl || '',
        githubUrl: d.githubUrl || '',
      });
    }).catch(() => toast.error('Failed to load profile'));
  }, []);

  const set = (f) => (e) => setForm(v => ({ ...v, [f]: e.target.value }));

  const saveProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await userAPI.updateProfile({ ...form, passoutYear: parseInt(form.passoutYear) });
      await refreshUser();
      toast.success('Profile updated! ✅');
      setEditing(false);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Update failed');
    } finally { setSaving(false); }
  };

  const changePassword = async (e) => {
    e.preventDefault();
    if (pwForm.newPassword !== pwForm.confirmPassword) { toast.error('Passwords do not match'); return; }
    setChangingPw(true);
    try {
      await authAPI.changePassword({ oldPassword: pwForm.oldPassword, newPassword: pwForm.newPassword });
      toast.success('Password changed successfully!');
      setPwForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
      setShowPwSection(false);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to change password');
    } finally { setChangingPw(false); }
  };

  const initials = user?.fullName?.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase() || 'U';

  if (!form) return <AppLayout title="Profile"><div className="loading-screen"><div className="spinner" /></div></AppLayout>;

  return (
    <AppLayout title="Profile">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">My Profile</h1>
            <p className="page-subtitle">Manage your personal information and security</p>
          </div>
          {!editing ? (
            <button className="btn btn-secondary" onClick={() => setEditing(true)}><Edit3 size={16} /> Edit Profile</button>
          ) : (
            <button className="btn btn-ghost" onClick={() => setEditing(false)}>Cancel</button>
          )}
        </div>

        {/* Profile Header */}
        <div className="profile-header-card" style={{ marginBottom: '1.5rem' }}>
          <div className="profile-avatar-lg">{initials}</div>
          <div style={{ flex: 1 }}>
            <h2 style={{ fontSize: '1.4rem', fontWeight: 800 }}>{user?.fullName}</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', margin: '4px 0 8px' }}>{user?.email}</p>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <span className="badge badge-blue">{user?.role?.name === 'ROLE_ADMIN' ? 'Administrator' : 'Student'}</span>
              {user?.emailVerified && <span className="badge badge-success">✓ Email Verified</span>}
              {user?.college && <span className="badge badge-purple">{user.college}</span>}
            </div>
          </div>
        </div>

        {/* Profile Form */}
        <form onSubmit={saveProfile}>
          <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '2rem', marginBottom: '1.5rem' }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: 8 }}>
              <User size={18} style={{ color: 'var(--accent-blue)' }} /> Personal Information
            </h3>
            <div className="grid-2" style={{ gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <div className="input-wrapper">
                  <User className="input-icon" size={16} />
                  <input className="form-input" value={form.fullName} onChange={set('fullName')} disabled={!editing} placeholder="Full name" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Email Address</label>
                <div className="input-wrapper">
                  <Mail className="input-icon" size={16} />
                  <input className="form-input" value={user?.email || ''} disabled placeholder="Email" />
                </div>
                <span className="form-hint">Email cannot be changed</span>
              </div>
              <div className="form-group">
                <label className="form-label">College / University</label>
                <div className="input-wrapper">
                  <Building className="input-icon" size={16} />
                  <input className="form-input" value={form.college} onChange={set('college')} disabled={!editing} placeholder="Your college" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Branch / Department</label>
                <div className="input-wrapper">
                  <GraduationCap className="input-icon" size={16} />
                  <input className="form-input" value={form.branch} onChange={set('branch')} disabled={!editing} placeholder="e.g. Computer Science" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Passout Year</label>
                <div className="input-wrapper">
                  <Calendar className="input-icon" size={16} />
                  <input className="form-input" type="number" value={form.passoutYear} onChange={set('passoutYear')} disabled={!editing} min="2000" max="2035" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <div className="input-wrapper">
                  <Phone className="input-icon" size={16} />
                  <input className="form-input" value={form.phoneNumber} onChange={set('phoneNumber')} disabled={!editing} placeholder="+91 98765 43210" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">LinkedIn URL</label>
                <div className="input-wrapper">
                  <Link2 className="input-icon" size={16} />
                  <input className="form-input" value={form.linkedinUrl} onChange={set('linkedinUrl')} disabled={!editing} placeholder="https://linkedin.com/in/you" />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">GitHub URL</label>
                <div className="input-wrapper">
                  <GitBranch className="input-icon" size={16} />
                  <input className="form-input" value={form.githubUrl} onChange={set('githubUrl')} disabled={!editing} placeholder="https://github.com/you" />
                </div>
              </div>
            </div>
            {editing && (
              <div style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'flex-end' }}>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? <div className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> : <><Save size={16} /> Save Changes</>}
                </button>
              </div>
            )}
          </div>
        </form>

        {/* Change Password */}
        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem 2rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: showPwSection ? '1px solid var(--border)' : 'none' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Lock size={18} style={{ color: 'var(--accent-blue)' }} />
              <span style={{ fontWeight: 700, fontSize: '1rem' }}>Change Password</span>
            </div>
            <button className="btn btn-ghost btn-sm" onClick={() => setShowPwSection(v => !v)}>
              {showPwSection ? 'Cancel' : 'Change'}
            </button>
          </div>
          {showPwSection && (
            <form onSubmit={changePassword} style={{ padding: '1.5rem 2rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {['oldPassword', 'newPassword', 'confirmPassword'].map((field, i) => (
                <div key={field} className="form-group">
                  <label className="form-label">{['Current Password', 'New Password', 'Confirm New Password'][i]}</label>
                  <input className="form-input" type="password" value={pwForm[field]} onChange={e => setPwForm(f => ({ ...f, [field]: e.target.value }))} placeholder={['Enter current password', 'Enter new password', 'Confirm new password'][i]} />
                </div>
              ))}
              <button type="submit" className="btn btn-primary" disabled={changingPw} style={{ alignSelf: 'flex-start' }}>
                {changingPw ? <div className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> : 'Update Password'}
              </button>
            </form>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
