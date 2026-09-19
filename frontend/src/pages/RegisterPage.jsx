import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { Mail, Lock, Eye, EyeOff, User, GraduationCap, ArrowRight, Building } from 'lucide-react';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', confirmPassword: '',
    college: '', branch: '', passoutYear: new Date().getFullYear() + 1,
  });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [step, setStep] = useState(1);

  const set = (field) => (e) => setForm(f => ({ ...f, [field]: e.target.value }));

  const validateStep1 = () => {
    const e = {};
    if (!form.fullName.trim()) e.fullName = 'Full name is required';
    if (!form.email) e.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Invalid email';
    if (!form.password || form.password.length < 6) e.password = 'Password must be at least 6 chars';
    if (form.password !== form.confirmPassword) e.confirmPassword = 'Passwords do not match';
    return e;
  };

  const validateStep2 = () => {
    const e = {};
    if (!form.passoutYear) e.passoutYear = 'Passout year is required';
    return e;
  };

  const nextStep = () => {
    const e = validateStep1();
    if (Object.keys(e).length) { setErrors(e); return; }
    setErrors({});
    setStep(2);
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    const e = validateStep2();
    if (Object.keys(e).length) { setErrors(e); return; }
    setLoading(true);
    try {
      const { confirmPassword, ...payload } = form;
      payload.passoutYear = parseInt(payload.passoutYear);
      await register(payload);
      toast.success('Account created! Welcome to PrepTrack 🎉');
      navigate('/dashboard');
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed';
      toast.error(msg);
      setErrors({ general: msg });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-left">
        <div className="auth-orb auth-orb-1" />
        <div className="auth-orb auth-orb-2" />
        <div className="auth-brand">
          <div className="auth-brand-logo">🧠</div>
          <div className="auth-brand-title">PrepTrack AI</div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1rem' }}>Join thousands of students cracking their dream jobs</p>
          <div style={{ marginTop: '3rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {['Mock interviews from top companies', 'AI-powered resume ATS scoring', 'Real-time progress & streak tracking', 'Instant notifications and feedback'].map(t => (
              <div key={t} className="flex items-center gap-1" style={{ gap: '10px', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                <span style={{ color: 'var(--accent-green)', fontSize: '1rem' }}>✓</span> {t}
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-form-container animate-slideUp">
          <div className="auth-form-header">
            <div style={{ display: 'flex', gap: '8px', marginBottom: '1.5rem' }}>
              {[1, 2].map(s => (
                <div key={s} style={{ flex: 1, height: 4, borderRadius: 2, background: step >= s ? 'var(--accent-blue)' : 'var(--border)', transition: 'background 0.3s' }} />
              ))}
            </div>
            <h1 className="auth-form-title">{step === 1 ? 'Create your account' : 'Your academic info'}</h1>
            <p className="auth-form-subtitle">{step === 1 ? 'Start your prep journey today' : 'Help us personalize your experience'}</p>
          </div>

          <form className="auth-form" onSubmit={step === 1 ? (e) => { e.preventDefault(); nextStep(); } : handleSubmit}>
            {step === 1 ? (
              <>
                <div className="form-group">
                  <label className="form-label">Full Name</label>
                  <div className="input-wrapper">
                    <User className="input-icon" size={16} />
                    <input className="form-input" placeholder="John Doe" value={form.fullName} onChange={set('fullName')} />
                  </div>
                  {errors.fullName && <span className="form-error">{errors.fullName}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <div className="input-wrapper">
                    <Mail className="input-icon" size={16} />
                    <input className="form-input" type="email" placeholder="you@example.com" value={form.email} onChange={set('email')} />
                  </div>
                  {errors.email && <span className="form-error">{errors.email}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Password</label>
                  <div className="input-wrapper">
                    <Lock className="input-icon" size={16} />
                    <input className="form-input" type={showPw ? 'text' : 'password'} placeholder="Min. 6 characters" value={form.password} onChange={set('password')} />
                    <span className="input-eye" onClick={() => setShowPw(v => !v)}>
                      {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                    </span>
                  </div>
                  {errors.password && <span className="form-error">{errors.password}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Confirm Password</label>
                  <div className="input-wrapper">
                    <Lock className="input-icon" size={16} />
                    <input className="form-input" type="password" placeholder="Repeat password" value={form.confirmPassword} onChange={set('confirmPassword')} />
                  </div>
                  {errors.confirmPassword && <span className="form-error">{errors.confirmPassword}</span>}
                </div>
                <button className="btn btn-primary btn-lg w-full" type="submit" style={{ justifyContent: 'center' }}>
                  Continue <ArrowRight size={18} />
                </button>
              </>
            ) : (
              <>
                <div className="form-group">
                  <label className="form-label">College / University</label>
                  <div className="input-wrapper">
                    <Building className="input-icon" size={16} />
                    <input className="form-input" placeholder="e.g. IIT Delhi" value={form.college} onChange={set('college')} />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Branch / Department</label>
                  <div className="input-wrapper">
                    <GraduationCap className="input-icon" size={16} />
                    <input className="form-input" placeholder="e.g. Computer Science" value={form.branch} onChange={set('branch')} />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Passout Year *</label>
                  <select className="form-select" value={form.passoutYear} onChange={set('passoutYear')}>
                    {Array.from({ length: 10 }, (_, i) => 2022 + i).map(y => (
                      <option key={y} value={y}>{y}</option>
                    ))}
                  </select>
                  {errors.passoutYear && <span className="form-error">{errors.passoutYear}</span>}
                </div>
                {errors.general && (
                  <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)', borderRadius: 8, padding: '10px 14px', fontSize: '0.85rem', color: 'var(--accent-red)' }}>
                    {errors.general}
                  </div>
                )}
                <div className="flex gap-2">
                  <button type="button" className="btn btn-ghost" onClick={() => setStep(1)} style={{ flex: 1, justifyContent: 'center' }}>Back</button>
                  <button className="btn btn-primary" type="submit" disabled={loading} style={{ flex: 2, justifyContent: 'center' }}>
                    {loading ? <div className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> : 'Create Account 🚀'}
                  </button>
                </div>
              </>
            )}
          </form>

          <p className="auth-footer-text">
            Already have an account? <Link to="/login" className="auth-link">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
