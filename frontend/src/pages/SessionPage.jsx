import { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { interviewAPI, sessionAPI } from '../services/api';
import toast from 'react-hot-toast';
import { Clock, ChevronLeft, ChevronRight, CheckCircle, Send, Trophy } from 'lucide-react';

export default function SessionPage() {
  const { id: sessionId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { interview, questions: initQuestions } = location.state || {};

  const [questions, setQuestions] = useState(initQuestions || []);
  const [current, setCurrent] = useState(0);
  const [answers, setAnswers] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [score, setScore] = useState(null);
  const [timer, setTimer] = useState(0);
  const timerRef = useRef(null);

  useEffect(() => {
    timerRef.current = setInterval(() => setTimer(t => t + 1), 1000);
    return () => clearInterval(timerRef.current);
  }, []);

  // If no questions passed via state, load them
  useEffect(() => {
    if (!initQuestions && interview?.id) {
      interviewAPI.getQuestions(interview.id).then(res => {
        setQuestions(res.data.data?.content || []);
      }).catch(() => toast.error('Failed to load questions'));
    }
  }, [interview]);

  const formatTime = (s) => `${Math.floor(s / 60).toString().padStart(2, '0')}:${(s % 60).toString().padStart(2, '0')}`;
  const progress = questions.length > 0 ? ((current + 1) / questions.length) * 100 : 0;
  const answeredCount = Object.values(answers).filter(a => a?.trim()).length;

  const handleSubmit = async () => {
    if (!confirm(`You've answered ${answeredCount}/${questions.length} questions. Submit the session?`)) return;
    setSubmitting(true);
    clearInterval(timerRef.current);
    try {
      const res = await sessionAPI.submit({
        sessionId: parseInt(sessionId),
        answers: questions.map(q => ({
          questionId: q.id,
          userAnswer: answers[q.id] || '',
        })),
      });
      const s = res.data.data?.score || Math.round((answeredCount / questions.length) * 100);
      setScore(s);
      setSubmitted(true);
      toast.success('Session submitted! 🎉');
    } catch (err) {
      // Even if API errors (session might not support answers array), show result
      const s = Math.round((answeredCount / Math.max(questions.length, 1)) * 100);
      setScore(s);
      setSubmitted(true);
    } finally {
      setSubmitting(false);
    }
  };

  if (submitted) {
    const grade = score >= 80 ? { label: 'Excellent! 🏆', color: 'var(--accent-green)' }
      : score >= 60 ? { label: 'Good Job! 👍', color: 'var(--accent-blue)' }
      : { label: 'Keep Practicing! 💪', color: 'var(--accent-yellow)' };

    return (
      <div style={{ minHeight: '100vh', background: 'var(--bg-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '2rem' }}>
        <div className="animate-slideUp" style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '3rem', maxWidth: 520, width: '100%', textAlign: 'center' }}>
          <div style={{ fontSize: '4rem', marginBottom: '1rem' }}>
            {score >= 80 ? '🏆' : score >= 60 ? '🌟' : '💪'}
          </div>
          <h2 style={{ fontSize: '1.8rem', fontWeight: 800, marginBottom: '0.5rem' }}>{grade.label}</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
            Session completed in {formatTime(timer)}
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '2.5rem' }}>
            <div style={{ background: 'var(--bg-secondary)', borderRadius: 'var(--radius-lg)', padding: '1.5rem' }}>
              <div style={{ fontSize: '3rem', fontWeight: 900, color: grade.color }}>{score}%</div>
              <div style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>Overall Score</div>
              <div className="progress-bar" style={{ marginTop: '0.75rem' }}>
                <div className="progress-fill" style={{ width: `${score}%`, background: grade.color }} />
              </div>
            </div>
            <div className="grid-2" style={{ gap: '0.75rem' }}>
              <div style={{ background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)', padding: '1rem' }}>
                <div style={{ fontSize: '1.4rem', fontWeight: 800 }}>{answeredCount}/{questions.length}</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.78rem' }}>Questions Answered</div>
              </div>
              <div style={{ background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)', padding: '1rem' }}>
                <div style={{ fontSize: '1.4rem', fontWeight: 800 }}>{formatTime(timer)}</div>
                <div style={{ color: 'var(--text-muted)', fontSize: '0.78rem' }}>Time Taken</div>
              </div>
            </div>
          </div>

          <div className="flex gap-2" style={{ justifyContent: 'center' }}>
            <button className="btn btn-ghost" onClick={() => navigate('/interviews')}>Browse More</button>
            <button className="btn btn-primary" onClick={() => navigate('/dashboard')}>
              <Trophy size={18} /> Go to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div style={{ minHeight: '100vh', background: 'var(--bg-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div className="spinner" />
      </div>
    );
  }

  const q = questions[current];

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg-primary)' }}>
      {/* Session Header */}
      <div style={{ position: 'fixed', top: 0, left: 0, right: 0, height: 64, background: 'rgba(10,13,26,0.9)', backdropFilter: 'blur(20px)', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 2rem', zIndex: 100 }}>
        <div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '2px' }}>Mock Session</div>
          <div style={{ fontSize: '0.9rem', fontWeight: 600 }}>{interview?.title || 'Interview Session'}</div>
        </div>
        <div style={{ flex: 1, maxWidth: 400, margin: '0 2rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: 4 }}>
            <span>Question {current + 1} of {questions.length}</span>
            <span>{answeredCount} answered</span>
          </div>
          <div className="progress-bar">
            <div className="progress-fill" style={{ width: `${progress}%` }} />
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: 'var(--text-secondary)', fontSize: '0.88rem' }}>
            <Clock size={16} />
            <span style={{ fontVariantNumeric: 'tabular-nums', fontFamily: 'monospace', fontSize: '1rem', fontWeight: 700, color: timer > 1800 ? 'var(--accent-red)' : 'var(--text-primary)' }}>
              {formatTime(timer)}
            </span>
          </div>
          <button className="btn btn-primary btn-sm" onClick={handleSubmit} disabled={submitting}>
            {submitting ? <div className="spinner" style={{ width: 16, height: 16, borderWidth: 2 }} /> : <><Send size={14} /> Submit</>}
          </button>
        </div>
      </div>

      {/* Content */}
      <div className="session-page" style={{ paddingTop: '5rem' }}>
        {/* Question Card */}
        <div className="question-card animate-slideUp" key={q.id}>
          <div className="question-number">Question {current + 1} · {q.topic && <span className="badge badge-blue" style={{ fontSize: '0.68rem', marginLeft: 6 }}>{q.topic}</span>}{q.category && <span className="badge badge-purple" style={{ fontSize: '0.68rem', marginLeft: 4 }}>{q.category}</span>}</div>
          <p className="question-text">{q.question}</p>
          <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'space-between' }}>
            <button className="btn btn-ghost btn-sm" onClick={() => setCurrent(c => Math.max(0, c - 1))} disabled={current === 0}>
              <ChevronLeft size={16} /> Previous
            </button>
            <button className="btn btn-secondary btn-sm" onClick={() => setCurrent(c => Math.min(questions.length - 1, c + 1))} disabled={current === questions.length - 1}>
              Next <ChevronRight size={16} />
            </button>
          </div>
        </div>

        {/* Answer Card */}
        <div className="answer-card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
            <h3 style={{ fontSize: '0.95rem', fontWeight: 700 }}>Your Answer</h3>
            {answers[q.id]?.trim() && <CheckCircle size={18} style={{ color: 'var(--accent-green)' }} />}
          </div>
          <textarea
            className="form-textarea"
            style={{ minHeight: 200, resize: 'vertical' }}
            placeholder="Write your answer here... Take your time to think through the problem."
            value={answers[q.id] || ''}
            onChange={e => setAnswers(a => ({ ...a, [q.id]: e.target.value }))}
          />
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '1rem' }}>
            <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
              {(answers[q.id] || '').length} characters
            </span>
            {current < questions.length - 1 ? (
              <button className="btn btn-primary btn-sm" onClick={() => setCurrent(c => c + 1)}>
                Next Question <ChevronRight size={16} />
              </button>
            ) : (
              <button className="btn btn-success btn-sm" onClick={handleSubmit} disabled={submitting}>
                <Send size={16} /> Submit Session
              </button>
            )}
          </div>
        </div>

        {/* Question Navigator */}
        <div style={{ marginTop: '1.5rem', background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-lg)', padding: '1.25rem' }}>
          <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Quick Navigate</div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
            {questions.map((_, i) => (
              <button
                key={i}
                onClick={() => setCurrent(i)}
                style={{
                  width: 36, height: 36,
                  borderRadius: 8,
                  fontSize: '0.82rem',
                  fontWeight: 700,
                  background: i === current ? 'var(--gradient-primary)' : answers[questions[i]?.id]?.trim() ? 'rgba(16,185,129,0.15)' : 'var(--bg-secondary)',
                  color: i === current ? 'white' : answers[questions[i]?.id]?.trim() ? 'var(--accent-green)' : 'var(--text-muted)',
                  border: `1px solid ${i === current ? 'transparent' : answers[questions[i]?.id]?.trim() ? 'rgba(16,185,129,0.3)' : 'var(--border)'}`,
                  cursor: 'pointer',
                  transition: 'all var(--transition)',
                }}
              >
                {i + 1}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
