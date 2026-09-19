import axios from 'axios';

const BASE_URL = '';

const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Handle 401 - try refresh token once
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    // Don't intercept auth endpoints like login/register
    if (original?.url?.includes('/api/auth/')) {
      return Promise.reject(error);
    }
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');
      if (refreshToken) {
        try {
          const { data } = await axios.post(`${BASE_URL}/api/auth/refresh-token`, { refreshToken });
          const newAccess = data?.data?.accessToken || data?.data?.token;
          const newRefresh = data?.data?.refreshToken;
          if (newAccess) {
            localStorage.setItem('accessToken', newAccess);
            if (newRefresh) localStorage.setItem('refreshToken', newRefresh);
            original.headers.Authorization = `Bearer ${newAccess}`;
            return api(original);
          }
        } catch {
          localStorage.clear();
          window.location.href = '/login';
        }
      } else {
        localStorage.clear();
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// ── Auth ─────────────────────────────────────────────────────────────────────
export const authAPI = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
  logout: (refreshToken) =>
    api.post('/api/auth/logout', { refreshToken }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    }),
  forgotPassword: (email) => api.post('/api/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/api/auth/reset-password', data),
  changePassword: (data) => api.post('/api/auth/change-password', data),
  verifyEmail: (token) => api.post('/api/auth/verify-email', { token }),
  refreshToken: (refreshToken) => api.post('/api/auth/refresh-token', { refreshToken }),
};

// ── User ──────────────────────────────────────────────────────────────────────
export const userAPI = {
  getProfile: () => api.get('/api/users/profile'),
  updateProfile: (data) => api.put('/api/users/profile', data),
  getAllUsers: (page = 0, size = 10) => api.get(`/api/users?page=${page}&size=${size}`),
  getUserById: (id) => api.get(`/api/users/${id}`),
  updateUserStatus: (id, data) => api.patch(`/api/users/${id}/status`, data),
  deleteUser: (id) => api.delete(`/api/users/${id}`),
};

// ── Interviews ────────────────────────────────────────────────────────────────
export const interviewAPI = {
  getAll: (params = {}) => {
    const query = new URLSearchParams({ page: 0, size: 12, ...params }).toString();
    return api.get(`/api/interviews?${query}`);
  },
  getById: (id) => api.get(`/api/interviews/${id}`),
  create: (data) => api.post('/api/interviews', data),
  update: (id, data) => api.put(`/api/interviews/${id}`, data),
  delete: (id) => api.delete(`/api/interviews/${id}`),
  getQuestions: (interviewId, page = 0, size = 10) =>
    api.get(`/api/interviews/${interviewId}/questions?page=${page}&size=${size}`),
  addQuestion: (data) => api.post('/api/interviews/questions', data),
  updateQuestion: (questionId, data) => api.put(`/api/interviews/questions/${questionId}`, data),
  deleteQuestion: (questionId) => api.delete(`/api/interviews/questions/${questionId}`),
};

// ── Interview Sessions ────────────────────────────────────────────────────────
export const sessionAPI = {
  start: (interviewId) =>
    api.post('/api/interview-sessions/start', { interviewId }, { params: { interviewId } }),
  submit: (data) => {
    const sessionId = data.sessionId || data.id;
    const score = data.score != null ? data.score : 80;
    return api.post(`/api/interview-sessions/${sessionId}/submit`, { score, ...data });
  },
  getMySession: (sessionId) => api.get(`/api/interview-sessions/${sessionId}`),
  getMySessions: (page = 0, size = 10) => api.get(`/api/interview-sessions/user?page=${page}&size=${size}`),
};

// ── Resume ────────────────────────────────────────────────────────────────────
export const resumeAPI = {
  upload: (fileUrl) => api.post('/api/resumes/upload', { fileUrl }),
  uploadFile: (formData) =>
    api.post('/api/resumes/upload-file', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),
  getMyResumes: (page = 0, size = 10) => api.get(`/api/resumes/user?page=${page}&size=${size}`),
  getAnalysis: (resumeId) => api.get(`/api/resumes/${resumeId}/analysis`),
};

// ── Notifications ─────────────────────────────────────────────────────────────
export const notificationAPI = {
  getAll: (page = 0, size = 10) => api.get(`/api/notifications?page=${page}&size=${size}`),
  getUnread: () => api.get('/api/notifications/unread'),
  markAsRead: (id) => api.put(`/api/notifications/${id}/read`),
  markAllAsRead: () => api.put('/api/notifications/read-all'),
};

// ── Progress ──────────────────────────────────────────────────────────────────
export const progressAPI = {
  getMyProgress: () => api.get('/api/user-progress'),
};

// ── Roles & Permissions ───────────────────────────────────────────────────────
export const roleAPI = {
  getAll: () => api.get('/api/roles'),
  create: (data) => api.post('/api/roles', data),
  update: (id, data) => api.put(`/api/roles/${id}`, data),
  delete: (id) => api.delete(`/api/roles/${id}`),
};

export const permissionAPI = {
  getAll: () => api.get('/api/permissions'),
  create: (data) => api.post('/api/permissions', data),
  delete: (id) => api.delete(`/api/permissions/${id}`),
};

export default api;
