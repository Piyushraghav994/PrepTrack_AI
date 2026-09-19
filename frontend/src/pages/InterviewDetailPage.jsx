import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AppLayout from '../components/AppLayout';
import { interviewAPI, sessionAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { ArrowLeft, Play, Plus, Trash2, ChevronDown, ChevronUp, BookOpen, Edit3 } from 'lucide-react';

export default function InterviewDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [interview, setInterview] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [starting, setStarting] = useState(false);
  const [expanded, setExpanded] = useState(null);
  const [showAddQ, setShowAddQ] = useState(false);
  const [qForm, setQForm] = useState({ question: '', answer: '', topic: '', category: '', interviewId: parseInt(id) });
  const [addingQ, setAddingQ] = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        const [ivRes, qRes] = await Promise.all([
          interviewAPI.getById(id),
          interviewAPI.getQuestions(id),
        ]);
        setInterview(ivRes.data.data);
        setQuestions(qRes.data.data?.content || []);
      } catch { toast.error('Failed to load interview'); }
      finally { setLoading(false); }
    };
    load();
  }, [id]);

  const startSession = async () => {
    if (questions.length === 0) { toast.error('No questions available for this interview'); return; }
    setStarting(true);
    try {
      const res = await sessionAPI.start(parseInt(id));
      const sessionId = res.data.data?.id;
      navigate(`/sessions/${sessionId}`, { state: { interview, questions } });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to start session');
    } finally { setStarting(false); }
  };

  const addQuestion = async (e) => {
    e.preventDefault();
    setAddingQ(true);
    try {
      const res = await interviewAPI.addQuestion({ ...qForm, interviewId: parseInt(id) });
      setQuestions(prev => [...prev, res.data.data]);
      setQForm({ question: '', answer: '', topic: '', category: '', interviewId: parseInt(id) });
      setShowAddQ(false);
      toast.success('Question added!');
    } catch { toast.error('Failed to add question'); }
    finally { setAddingQ(false); }
  };

  const deleteQuestion = async (qId) => {
    if (!confirm('Delete this question?')) return;
    try {
      await interviewAPI.deleteQuestion(qId);
      setQuestions(prev => prev.filter(q => q.id !== qId));
      toast.success('Question deleted');
    } catch { toast.error('Failed to delete'); }
  };

  if (loading) return <AppLayout title="Interview"><div className="loading-screen"><div className="spinner" /></div></AppLayout>;
  if (!interview) return <AppLayout title="Interview"><div className="page-container"><div className="empty-state">Interview not found</div></div></AppLayout>;

  const diffClass = interview.difficulty === 'EASY' ? 'badge-easy' : interview.difficulty === 'MEDIUM' ? 'badge-medium' : 'badge-hard';

  return (
    <AppLayout title={interview.title}>
      <div className="page-container animate-fadeInUp">
        <button className="btn btn-ghost btn-sm mb-2" onClick={() => navigate('/interviews')}>
          <ArrowLeft size={16} /> Back to Interviews
        </button>

        {/* Header */}
        <div style={{
          background: 'linear-gradient(135deg, rgba(99,120,255,0.1) 0%, rgba(168,85,247,0.08) 100%)',
          border: '1px solid var(--border)',
          borderRadius: 'var(--radius-xl)',
          padding: '2.5rem',
          marginBottom: '2rem',
          position: 'relative',
          overflow: 'hidden',
        }}>
          <div style={{ position: 'absolute', top: -60, right: -60, width: 200, height: 200, borderRadius: '50%', background: 'rgba(99,120,255,0.06)' }} />
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1.5rem', flexWrap: 'wrap' }}>
            <div style={{ width: 64, height: 64, borderRadius: 16, background: `hsl(${(interview.company?.charCodeAt(0) || 200) * 5}, 60%, 45%)`, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 800, color: 'white', flexShrink: 0 }}>
              {interview.company?.[0]?.toUpperCase()}
            </div>
            <div style={{ flex: 1 }}>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '8px' }}>{interview.title}</h1>
              <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap', marginBottom: '12px' }}>
                <span className={`badge ${diffClass}`}>{interview.difficulty}</span>
                <span className="badge badge-blue">{interview.company}</span>
                <span className="badge badge-purple">{interview.role}</span>
                <span className="badge badge-cyan">{questions.length} Questions</span>
              </div>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: 1.6 }}>{interview.description}</p>
            </div>
            <button className="btn btn-primary btn-lg" onClick={startSession} disabled={starting}>
              {starting ? <div className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> : <><Play size={20} /> Start Mock Session</>}
            </button>
          </div>
        </div>

        {/* Questions */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
          <h2 style={{ fontSize: '1.1rem', fontWeight: 700 }}>
            Questions ({questions.length})
          </h2>
          {isAdmin && (
            <button className="btn btn-secondary btn-sm" onClick={() => setShowAddQ(true)}>
              <Plus size={16} /> Add Question
            </button>
          )}
        </div>

        {questions.length === 0 ? (
          <div className="empty-state">
            <BookOpen size={40} style={{ opacity: 0.3 }} />
            <div className="empty-state-title">No questions yet</div>
            {isAdmin && <button className="btn btn-primary btn-sm" onClick={() => setShowAddQ(true)}><Plus size={14} /> Add First Question</button>}
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {questions.map((q, i) => (
              <div key={q.id} style={{
                background: 'var(--bg-card)',
                border: '1px solid var(--border)',
                borderRadius: 'var(--radius-lg)',
                overflow: 'hidden',
                transition: 'all var(--transition)',
              }}>
                <div
                  style={{ padding: '1.25rem 1.5rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '1rem' }}
                  onClick={() => setExpanded(expanded === q.id ? null : q.id)}
                >
                  <div style={{ width: 28, height: 28, borderRadius: 8, background: 'rgba(99,120,255,0.12)', color: 'var(--accent-blue)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.75rem', fontWeight: 700, flexShrink: 0 }}>
                    {i + 1}
                  </div>
                  <div style={{ flex: 1 }}>
                    <p style={{ fontSize: '0.92rem', fontWeight: 600, color: 'var(--text-primary)' }}>{q.question}</p>
                    <div style={{ display: 'flex', gap: '8px', marginTop: '4px' }}>
                      {q.topic && <span className="badge badge-blue" style={{ fontSize: '0.68rem' }}>{q.topic}</span>}
                      {q.category && <span className="badge badge-purple" style={{ fontSize: '0.68rem' }}>{q.category}</span>}
                    </div>
                  </div>
                  {isAdmin && (
                    <button className="btn btn-danger btn-icon" onClick={e => { e.stopPropagation(); deleteQuestion(q.id); }} style={{ padding: 6 }}>
                      <Trash2 size={14} />
                    </button>
                  )}
                  {expanded === q.id ? <ChevronUp size={18} style={{ color: 'var(--text-muted)', flexShrink: 0 }} /> : <ChevronDown size={18} style={{ color: 'var(--text-muted)', flexShrink: 0 }} />}
                </div>
                {expanded === q.id && (
                  <div style={{ padding: '0 1.5rem 1.5rem', borderTop: '1px solid var(--border)' }}>
                    <div style={{ marginTop: '1rem', background: 'rgba(16,185,129,0.06)', border: '1px solid rgba(16,185,129,0.15)', borderRadius: 'var(--radius-md)', padding: '1rem' }}>
                      <div style={{ fontSize: '0.72rem', fontWeight: 700, color: 'var(--accent-green)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '6px' }}>Model Answer</div>
                      <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', lineHeight: 1.7 }}>{q.answer}</p>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Add Question Modal */}
      {showAddQ && (
        <div className="modal-overlay" onClick={() => setShowAddQ(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <span className="modal-title">Add Question</span>
              <button className="modal-close" onClick={() => setShowAddQ(false)}>✕</button>
            </div>
            <form onSubmit={addQuestion} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Question *</label>
                <textarea className="form-textarea" placeholder="Enter the interview question..." required value={qForm.question} onChange={e => setQForm(f => ({ ...f, question: e.target.value }))} style={{ minHeight: 80 }} />
              </div>
              <div className="form-group">
                <label className="form-label">Model Answer *</label>
                <textarea className="form-textarea" placeholder="Enter the ideal answer..." required value={qForm.answer} onChange={e => setQForm(f => ({ ...f, answer: e.target.value }))} />
              </div>
              <div className="grid-2" style={{ gap: '0.75rem' }}>
                <div className="form-group">
                  <label className="form-label">Topic</label>
                  <input className="form-input" placeholder="e.g. Java, React" value={qForm.topic} onChange={e => setQForm(f => ({ ...f, topic: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <input className="form-input" placeholder="e.g. Core, Systems" value={qForm.category} onChange={e => setQForm(f => ({ ...f, category: e.target.value }))} />
                </div>
              </div>
              <div className="flex gap-2">
                <button type="button" className="btn btn-ghost" onClick={() => setShowAddQ(false)} style={{ flex: 1, justifyContent: 'center' }}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={addingQ} style={{ flex: 2, justifyContent: 'center' }}>
                  {addingQ ? <div className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }} /> : 'Add Question'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
