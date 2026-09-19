import { useState, useCallback } from 'react';
import AppLayout from '../components/AppLayout';
import { resumeAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import { Upload, FileText, Link2, CheckCircle, AlertCircle, TrendingUp, X, Eye } from 'lucide-react';

function ScoreArc({ score }) {
  const r = 54;
  const circ = 2 * Math.PI * r;
  const color = score >= 80 ? '#10b981' : score >= 65 ? '#f59e0b' : '#ef4444';
  const offset = circ - (score / 100) * circ;
  return (
    <div style={{ position: 'relative', width: 140, height: 140, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
      <svg width="140" height="140" style={{ transform: 'rotate(-90deg)', position: 'absolute' }}>
        <circle cx="70" cy="70" r={r} fill="none" stroke="var(--bg-secondary)" strokeWidth="10" />
        <circle cx="70" cy="70" r={r} fill="none" stroke={color} strokeWidth="10" strokeDasharray={circ} strokeDashoffset={offset} strokeLinecap="round" style={{ transition: 'stroke-dashoffset 1s ease' }} />
      </svg>
      <div style={{ textAlign: 'center', zIndex: 1 }}>
        <div style={{ fontSize: '2rem', fontWeight: 900, color }}>{score}</div>
        <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>ATS Score</div>
      </div>
    </div>
  );
}

export default function ResumePage() {
  const { user } = useAuth();
  const [tab, setTab] = useState('url');
  const [url, setUrl] = useState('');
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [analysis, setAnalysis] = useState(null);
  const [resumes, setResumes] = useState([]);
  const [loadingResumes, setLoadingResumes] = useState(false);
  const [drag, setDrag] = useState(false);

  const loadResumes = async () => {
    setLoadingResumes(true);
    try {
      const res = await resumeAPI.getMyResumes();
      const list = res.data.data?.content || [];
      setResumes(list);
    } catch { toast.error('Failed to load resumes'); }
    finally { setLoadingResumes(false); }
  };

  const handleUrlSubmit = async (e) => {
    e.preventDefault();
    if (!url.trim()) { toast.error('Please enter a file URL'); return; }
    setUploading(true);
    setAnalysis(null);
    try {
      const res = await resumeAPI.upload(url);
      const resumeId = res.data.data?.id;
      if (resumeId) {
        const aRes = await resumeAPI.getAnalysis(resumeId);
        setAnalysis(aRes.data.data);
      }
      toast.success('Resume uploaded and analyzed! 🎉');
      setUrl('');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Upload failed');
    } finally { setUploading(false); }
  };

  const handleFileUpload = async () => {
    if (!file) { toast.error('Please select a file'); return; }
    setUploading(true);
    setAnalysis(null);
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await resumeAPI.uploadFile(formData);
      const resumeId = res.data.data?.id;
      if (resumeId) {
        const aRes = await resumeAPI.getAnalysis(resumeId);
        setAnalysis(aRes.data.data);
      }
      toast.success('Resume uploaded and analyzed! 🎉');
      setFile(null);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Upload failed');
    } finally { setUploading(false); }
  };

  const onDrop = useCallback((e) => {
    e.preventDefault();
    setDrag(false);
    const f = e.dataTransfer?.files?.[0];
    if (f && (f.name.endsWith('.pdf') || f.name.endsWith('.docx'))) {
      setFile(f);
      setTab('file');
    } else {
      toast.error('Please upload a PDF or DOCX file');
    }
  }, []);

  const scoreColor = analysis ? (analysis.atsScore >= 80 ? 'var(--accent-green)' : analysis.atsScore >= 65 ? 'var(--accent-yellow)' : 'var(--accent-red)') : '';
  const scoreLabel = analysis ? (analysis.atsScore >= 80 ? 'Excellent' : analysis.atsScore >= 65 ? 'Good' : 'Needs Work') : '';

  return (
    <AppLayout title="Resume Analyzer">
      <div className="page-container animate-fadeInUp">
        <div className="page-header">
          <div>
            <h1 className="page-title">Resume ATS Analyzer</h1>
            <p className="page-subtitle">Upload your resume and get an instant ATS compatibility score</p>
          </div>
        </div>

        <div className="grid-2" style={{ gap: '2rem', alignItems: 'flex-start' }}>
          {/* Upload Panel */}
          <div>
            <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', overflow: 'hidden' }}>
              {/* Tabs */}
              <div style={{ display: 'flex', borderBottom: '1px solid var(--border)' }}>
                {[{ id: 'url', label: '🔗 By URL' }, { id: 'file', label: '📁 Upload File' }].map(t => (
                  <button key={t.id} onClick={() => setTab(t.id)} style={{
                    flex: 1, padding: '14px', fontSize: '0.88rem', fontWeight: 600,
                    background: tab === t.id ? 'rgba(99,120,255,0.08)' : 'transparent',
                    color: tab === t.id ? 'var(--accent-blue)' : 'var(--text-muted)',
                    borderBottom: tab === t.id ? '2px solid var(--accent-blue)' : '2px solid transparent',
                    transition: 'all var(--transition)',
                    cursor: 'pointer',
                    border: 'none',
                    borderBottom: tab === t.id ? '2px solid var(--accent-blue)' : '2px solid transparent',
                  }}>{t.label}</button>
                ))}
              </div>

              <div style={{ padding: '2rem' }}>
                {tab === 'url' ? (
                  <form onSubmit={handleUrlSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div className="form-group">
                      <label className="form-label">Resume URL (Google Drive, Dropbox, etc.)</label>
                      <div className="input-wrapper">
                        <Link2 className="input-icon" size={16} />
                        <input className="form-input" placeholder="https://drive.google.com/..." value={url} onChange={e => setUrl(e.target.value)} />
                      </div>
                      <span className="form-hint">Paste a publicly accessible URL to your resume PDF/DOCX</span>
                    </div>
                    <button type="submit" className="btn btn-primary btn-lg w-full" disabled={uploading} style={{ justifyContent: 'center' }}>
                      {uploading ? <><div className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> Analyzing...</> : <><Upload size={20} /> Analyze Resume</>}
                    </button>
                  </form>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <div
                      onDragOver={e => { e.preventDefault(); setDrag(true); }}
                      onDragLeave={() => setDrag(false)}
                      onDrop={onDrop}
                      onClick={() => document.getElementById('resume-file-inp').click()}
                      style={{
                        border: `2px dashed ${drag ? 'var(--accent-blue)' : 'var(--border)'}`,
                        borderRadius: 'var(--radius-lg)',
                        padding: '3rem 2rem',
                        textAlign: 'center',
                        cursor: 'pointer',
                        background: drag ? 'rgba(99,120,255,0.05)' : 'var(--bg-secondary)',
                        transition: 'all var(--transition)',
                      }}
                    >
                      <input id="resume-file-inp" type="file" accept=".pdf,.docx" style={{ display: 'none' }} onChange={e => setFile(e.target.files?.[0] || null)} />
                      <Upload size={36} style={{ color: drag ? 'var(--accent-blue)' : 'var(--text-muted)', marginBottom: '1rem' }} />
                      {file ? (
                        <div>
                          <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>{file.name}</div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: 4 }}>{(file.size / 1024).toFixed(1)} KB</div>
                          <button className="btn btn-ghost btn-sm" style={{ marginTop: '0.75rem' }} onClick={e => { e.stopPropagation(); setFile(null); }}>
                            <X size={14} /> Remove
                          </button>
                        </div>
                      ) : (
                        <>
                          <div style={{ fontWeight: 600, color: 'var(--text-secondary)', marginBottom: 4 }}>Drag & drop your resume here</div>
                          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>or click to browse · PDF, DOCX supported</div>
                        </>
                      )}
                    </div>
                    <button className="btn btn-primary btn-lg w-full" disabled={uploading || !file} onClick={handleFileUpload} style={{ justifyContent: 'center' }}>
                      {uploading ? <><div className="spinner" style={{ width: 20, height: 20, borderWidth: 2 }} /> Analyzing...</> : <><Upload size={20} /> Analyze Resume</>}
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* Tips */}
            <div style={{ marginTop: '1.5rem', background: 'rgba(99,120,255,0.06)', border: '1px solid rgba(99,120,255,0.15)', borderRadius: 'var(--radius-lg)', padding: '1.25rem' }}>
              <div style={{ fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.75rem', color: 'var(--accent-blue)' }}>💡 Tips for a Higher ATS Score</div>
              {['Use a single-column, clean layout', 'Include quantitative metrics (e.g., "20% improvement")', 'Match keywords from the job description', 'Avoid tables, graphics, and special characters', 'Use standard section headings (Experience, Education, Skills)'].map(t => (
                <div key={t} style={{ display: 'flex', alignItems: 'flex-start', gap: 8, marginBottom: 6, fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
                  <span style={{ color: 'var(--accent-green)', marginTop: 2 }}>✓</span> {t}
                </div>
              ))}
            </div>
          </div>

          {/* Analysis Result */}
          <div>
            {!analysis ? (
              <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '3rem', textAlign: 'center' }}>
                <FileText size={52} style={{ color: 'var(--text-muted)', opacity: 0.4, marginBottom: '1rem' }} />
                <div style={{ fontWeight: 700, color: 'var(--text-secondary)', marginBottom: 8 }}>No Analysis Yet</div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Upload your resume to see your ATS score, detected skills, and improvement suggestions.</p>
              </div>
            ) : (
              <div className="animate-slideUp" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                {/* Score Card */}
                <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '2rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
                    <ScoreArc score={analysis.atsScore} />
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: 4 }}>ATS Compatibility</div>
                      <div style={{ fontSize: '1.6rem', fontWeight: 800, color: scoreColor, marginBottom: 4 }}>{scoreLabel}</div>
                      <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                        {analysis.atsScore >= 80 ? 'Your resume is well-optimized for ATS systems.' : analysis.atsScore >= 65 ? 'Your resume passes most ATS filters with minor improvements needed.' : 'Your resume needs significant ATS optimization.'}
                      </p>
                    </div>
                  </div>
                </div>

                {/* Skills */}
                <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '1.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: '1rem' }}>
                    <CheckCircle size={18} style={{ color: 'var(--accent-green)' }} />
                    <span style={{ fontWeight: 700, fontSize: '0.95rem' }}>Detected Skills</span>
                  </div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                    {(analysis.skills || '').split(',').map(s => s.trim()).filter(Boolean).map(s => (
                      <span key={s} className="badge badge-success">{s}</span>
                    ))}
                  </div>
                </div>

                {/* Missing Keywords */}
                <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '1.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: '1rem' }}>
                    <AlertCircle size={18} style={{ color: 'var(--accent-red)' }} />
                    <span style={{ fontWeight: 700, fontSize: '0.95rem' }}>Missing Keywords</span>
                  </div>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                    {(analysis.missingKeywords || '').split(',').map(s => s.trim()).filter(Boolean).map(s => (
                      <span key={s} className="badge badge-error">{s}</span>
                    ))}
                  </div>
                </div>

                {/* Suggestions */}
                <div style={{ background: 'rgba(99,120,255,0.06)', border: '1px solid rgba(99,120,255,0.15)', borderRadius: 'var(--radius-xl)', padding: '1.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: '1rem' }}>
                    <TrendingUp size={18} style={{ color: 'var(--accent-blue)' }} />
                    <span style={{ fontWeight: 700, fontSize: '0.95rem', color: 'var(--accent-blue)' }}>Improvement Suggestions</span>
                  </div>
                  <p style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', lineHeight: 1.7 }}>{analysis.suggestions}</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </AppLayout>
  );
}
