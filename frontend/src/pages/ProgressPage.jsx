import { useEffect, useState } from 'react';
import AppLayout from '../components/AppLayout';
import { progressAPI } from '../services/api';
import { Flame, Target, BookOpen, Award, TrendingUp } from 'lucide-react';

export default function ProgressPage() {
  const [progress, setProgress] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    progressAPI.getMyProgress()
      .then(r => setProgress(r.data.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const stats = progress ? [
    { icon: <BookOpen size={24} color="#6378ff" />, label: 'Interviews Completed', value: progress.completedInterviews, max: 50, color: '#6378ff' },
    { icon: <Target size={24} color="#a855f7" />, label: 'Questions Solved', value: progress.completedQuestions, max: 200, color: '#a855f7' },
    { icon: <Flame size={24} color="#f97316" />, label: 'Current Streak (days)', value: progress.currentStreak, max: 30, color: '#f97316' },
    { icon: <Award size={24} color="#f59e0b" />, label: 'Total Score', value: progress.totalScore, max: 1000, color: '#f59e0b' },
  ] : [];

  return (
    <AppLayout title="Progress">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">My Progress</h1>
            <p className="page-subtitle">Track your improvement journey</p>
          </div>
        </div>

        {loading ? (
          <div className="grid-2">
            {[1,2,3,4].map(i => <div key={i} className="skeleton" style={{ height: 160, borderRadius: 'var(--radius-lg)' }} />)}
          </div>
        ) : !progress ? (
          <div className="empty-state">
            <TrendingUp size={48} style={{ opacity: 0.3 }} />
            <div className="empty-state-title">No progress data yet</div>
            <div className="empty-state-desc">Complete a mock interview to start tracking your progress!</div>
          </div>
        ) : (
          <>
            <div className="grid-2" style={{ marginBottom: '2rem' }}>
              {stats.map(s => (
                <div key={s.label} style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '2rem', transition: 'all var(--transition)' }} className="card">
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                    <div style={{ width: 50, height: 50, borderRadius: 12, background: `${s.color}18`, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      {s.icon}
                    </div>
                    <div style={{ fontSize: '2.2rem', fontWeight: 900, color: s.color }}>{s.value}</div>
                  </div>
                  <div style={{ fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.75rem', fontSize: '0.9rem' }}>{s.label}</div>
                  <div className="progress-bar">
                    <div className="progress-fill" style={{ width: `${Math.min(100, (s.value / s.max) * 100)}%`, background: s.color }} />
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '6px', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    <span>Current</span>
                    <span>Goal: {s.max}</span>
                  </div>
                </div>
              ))}
            </div>

            <div style={{ background: 'linear-gradient(135deg, rgba(99,120,255,0.1) 0%, rgba(168,85,247,0.08) 100%)', border: '1px solid rgba(99,120,255,0.2)', borderRadius: 'var(--radius-xl)', padding: '2rem' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem' }}>🏆 Achievement Summary</h3>
              <div className="grid-3" style={{ gap: '1rem' }}>
                {[
                  { emoji: '🎯', title: 'Consistent Learner', desc: progress.currentStreak >= 7 ? 'Unlocked! 7+ day streak' : `${progress.currentStreak}/7 days to unlock`, locked: progress.currentStreak < 7 },
                  { emoji: '📚', title: 'Interview Master', desc: progress.completedInterviews >= 10 ? 'Unlocked! 10+ interviews' : `${progress.completedInterviews}/10 interviews`, locked: progress.completedInterviews < 10 },
                  { emoji: '⭐', title: 'High Scorer', desc: progress.totalScore >= 500 ? 'Unlocked! 500+ points' : `${progress.totalScore}/500 points`, locked: progress.totalScore < 500 },
                ].map(a => (
                  <div key={a.title} style={{ background: a.locked ? 'var(--bg-secondary)' : 'rgba(99,120,255,0.08)', border: `1px solid ${a.locked ? 'var(--border)' : 'rgba(99,120,255,0.25)'}`, borderRadius: 'var(--radius-lg)', padding: '1.25rem', textAlign: 'center', opacity: a.locked ? 0.6 : 1, transition: 'all var(--transition)' }}>
                    <div style={{ fontSize: '2rem', marginBottom: '8px', filter: a.locked ? 'grayscale(1)' : 'none' }}>{a.emoji}</div>
                    <div style={{ fontWeight: 700, fontSize: '0.88rem', marginBottom: '4px' }}>{a.title}</div>
                    <div style={{ fontSize: '0.75rem', color: a.locked ? 'var(--text-muted)' : 'var(--accent-blue)' }}>{a.desc}</div>
                  </div>
                ))}
              </div>
            </div>
          </>
        )}
      </div>
    </AppLayout>
  );
}
