import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { Mail, Lock, Eye, EyeOff, Brain, ArrowRight, CheckCircle } from 'lucide-react';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.email) e.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Invalid email';
    if (!form.password) e.password = 'Password is required';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const e2 = validate();
    if (Object.keys(e2).length) { setErrors(e2); return; }
    setLoading(true);
    try {
      await login(form);
      toast.success('Welcome back! 🎉');
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message || 'Invalid email or password';
      toast.error(msg);
      setErrors({ general: msg });
    } finally {
      setLoading(false);
    }
  };

  const fillDemo = () => setForm({ email: 'admin@preptrack.com', password: 'AdminPassword123!' });

  return (
    <div className="auth-page">
      <div className="auth-left">
        <div className="auth-orb auth-orb-1" />
        <div className="auth-orb auth-orb-2" />
        <div className="auth-brand">
          <div className="auth-brand-logo">🧠</div>
          <div className="auth-brand-title">PrepTrack AI</div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1rem', marginBottom: '0.5rem' }}>
            Your AI-powered interview preparation platform
          </p>
          <div className="auth-feature-list">
            {[
              { icon: '🎯', title: 'Mock Interviews', text: 'Practice with real company questions' },
              { icon: '📄', title: 'ATS Resume Scanner', text: 'Get your resume ATS-score instantly' },
              { icon: '📈', title: 'Progress Tracking', text: 'Monitor your improvement over time' },
              { icon: '🔔', title: 'Smart Notifications', text: 'Stay updated on your journey' },
            ].map((f) => (
              <div key={f.title} className="auth-feature">
                <div className="auth-feature-icon" style={{ background: 'rgba(99,120,255,0.1)', fontSize: '1.2rem' }}>
                  {f.icon}
                </div>
                <div>
                  <div className="auth-feature-title">{f.title}</div>
                  <div className="auth-feature-text">{f.text}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-form-container animate-slideUp">
          <div className="auth-form-header">
            <h1 className="auth-form-title">Welcome back 👋</h1>
            <p className="auth-form-subtitle">Sign in to continue your prep journey</p>
          </div>

          <button className="btn btn-ghost w-full mb-2" onClick={fillDemo} style={{ justifyContent: 'center' }}>
            <CheckCircle size={16} /> Use Demo Admin Credentials
          </button>

          <div className="divider-text"><span>or sign in manually</span></div>
          <div style={{ height: '1rem' }} />

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <div className="input-wrapper">
                <Mail className="input-icon" size={16} />
                <input
                  className="form-input"
                  type="email"
                  placeholder="you@example.com"
                  value={form.email}
                  onChange={e => setForm(f => ({ ...f, email: e.target.value }))}
                />
              </div>
              {errors.email && <span className="form-error">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div className="input-wrapper">
                <Lock className="input-icon" size={16} />
                <input
                  className="form-input"
                  type={showPw ? 'text' : 'password'}
                  placeholder="Enter your password"
                  value={form.password}
                  onChange={e => setForm(f => ({ ...f, password: e.target.value }))}
                />
                <span className="input-eye" onClick={() => setShowPw(v => !v)}>
                  {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                </span>
              </div>
              {errors.password && <span className="form-error">{errors.password}</span>}
            </div>

            <div className="flex justify-between items-center">
              <span />
              <Link to="/forgot-password" className="auth-link" style={{ fontSize: '0.82rem' }}>
                Forgot password?
              </Link>
            </div>

            {errors.general && (
              <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)', borderRadius: 8, padding: '10px 14px', fontSize: '0.85rem', color: 'var(--accent-red)' }}>
                {errors.general}
              </div>
            )}

            <button className="btn btn-primary btn-lg w-full" type="submit" disabled={loading} style={{ justifyContent: 'center' }}>
              {loading ? <div className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> : <>Sign In <ArrowRight size={18} /></>}
            </button>
          </form>

          <p className="auth-footer-text">
            Don't have an account?{' '}
            <Link to="/register" className="auth-link">Create one free</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
