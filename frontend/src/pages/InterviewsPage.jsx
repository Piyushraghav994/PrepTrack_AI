import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { interviewAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Filter, ArrowRight, BookOpen } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const DIFFICULTIES = ['ALL', 'EASY', 'MEDIUM', 'HARD'];

export default function InterviewsPage() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [interviews, setInterviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [difficulty, setDifficulty] = useState('ALL');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showCreate, setShowCreate] = useState(false);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({ title: '', company: '', role: '', difficulty: 'MEDIUM', description: '' });

  const load = async (p = 0) => {
    setLoading(true);
    try {
      const params = { page: p, size: 12 };
      if (difficulty !== 'ALL') params.difficulty = difficulty;
      if (search.trim()) params.role = search.trim();
      const res = await interviewAPI.getAll(params);
      setInterviews(res.data.data.content || []);
      setTotalPages(res.data.data.totalPages || 0);
      setPage(p);
    } catch { toast.error('Failed to load interviews'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(0); }, [difficulty]);

  const diffClass = (d) => d === 'EASY' ? 'badge-easy' : d === 'MEDIUM' ? 'badge-medium' : 'badge-hard';

  const handleCreate = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await interviewAPI.create(form);
      toast.success('Interview created!');
      setShowCreate(false);
      setForm({ title: '', company: '', role: '', difficulty: 'MEDIUM', description: '' });
      load(0);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create');
    } finally { setCreating(false); }
  };

  return (
    <AppLayout title="Interviews">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">Mock Interviews</h1>
            <p className="page-subtitle">Practice with real company interview questions</p>
          </div>
          {isAdmin && (
            <button className="btn btn-primary" onClick={() => setShowCreate(true)}>
              <Plus size={18} /> Create Interview
            </button>
          )}
        </div>

        {/* Filter Bar */}
        <div className="filter-bar">
          <div className="search-input">
            <div className="input-wrapper">
              <Search className="input-icon" size={16} />
              <input className="form-input" placeholder="Search by role..." value={search}
                onChange={e => setSearch(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && load(0)} />
            </div>
          </div>
          {DIFFICULTIES.map(d => (
            <button key={d} className={`filter-chip ${difficulty === d ? 'active' : ''}`} onClick={() => setDifficulty(d)}>
              {d === 'ALL' ? 'All Levels' : d.charAt(0) + d.slice(1).toLowerCase()}
            </button>
          ))}
          <button className="btn btn-ghost btn-sm" onClick={() => load(0)}><Search size={14} /> Search</button>
        </div>

        {/* Grid */}
        {loading ? (
          <div className="grid-3">
            {[...Array(6)].map((_, i) => <div key={i} className="skeleton" style={{ height: 220, borderRadius: 'var(--radius-lg)' }} />)}
          </div>
        ) : interviews.length === 0 ? (
          <div className="empty-state">
            <BookOpen size={48} style={{ opacity: 0.3 }} />
            <div className="empty-state-title">No interviews found</div>
            <div className="empty-state-desc">Try changing your filters or search term</div>
          </div>
        ) : (
          <>
            <div className="grid-3">
              {interviews.map(iv => (
                <div key={iv.id} className="interview-card" onClick={() => navigate(`/interviews/${iv.id}`)}>
                  <div className="interview-company">
                    <div className="company-logo" style={{ background: `hsl(${(iv.company?.charCodeAt(0) || 200) * 5}, 60%, 45%)` }}>
                      {iv.company?.[0]?.toUpperCase() || '?'}
                    </div>
                    <div>
                      <div className="interview-title">{iv.title}</div>
                      <div className="interview-role">{iv.company} · {iv.role}</div>
                    </div>
                  </div>
                  <p className="interview-desc">{iv.description || 'No description provided.'}</p>
                  <div className="interview-meta">
                    <span className={`badge ${diffClass(iv.difficulty)}`}>{iv.difficulty}</span>
                    <span className="badge badge-blue">{iv.role}</span>
                  </div>
                  <button className="btn btn-primary btn-sm" style={{ marginTop: 'auto' }}>
                    Start Practice <ArrowRight size={14} />
                  </button>
                </div>
              ))}
            </div>

            {totalPages > 1 && (
              <div className="flex justify-center gap-1 mt-3">
                {[...Array(totalPages)].map((_, i) => (
                  <button key={i} className={`btn btn-sm ${page === i ? 'btn-primary' : 'btn-ghost'}`} onClick={() => load(i)}>{i + 1}</button>
                ))}
              </div>
            )}
          </>
        )}
      </div>

      {/* Create Modal */}
      {showCreate && (
        <div className="modal-overlay" onClick={() => setShowCreate(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-title">Create New Interview</span>
              <button className="modal-close" onClick={() => setShowCreate(false)}>✕</button>
            </div>
            <form onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Title *</label>
                <input className="form-input" placeholder="e.g. Java Backend Engineer Mock Interview" required value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} />
              </div>
              <div className="grid-2" style={{ gap: '0.75rem' }}>
                <div className="form-group">
                  <label className="form-label">Company *</label>
                  <input className="form-input" placeholder="Google" required value={form.company} onChange={e => setForm(f => ({ ...f, company: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Role *</label>
                  <input className="form-input" placeholder="Software Engineer" required value={form.role} onChange={e => setForm(f => ({ ...f, role: e.target.value }))} />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Difficulty</label>
                <select className="form-select" value={form.difficulty} onChange={e => setForm(f => ({ ...f, difficulty: e.target.value }))}>
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="form-textarea" placeholder="Describe what this interview covers..." value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} />
              </div>
              <div className="flex gap-2">
                <button type="button" className="btn btn-ghost" onClick={() => setShowCreate(false)} style={{ flex: 1, justifyContent: 'center' }}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={creating} style={{ flex: 2, justifyContent: 'center' }}>
                  {creating ? <div className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> : 'Create Interview'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
