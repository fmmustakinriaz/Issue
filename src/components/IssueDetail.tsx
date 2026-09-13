import { useEffect, useState, useCallback } from 'react';
import { supabase } from '@/lib/supabase';
import { useAuth } from '@/lib/auth';
import type { Issue, Comment, IssueStatus, Profile, Category } from '@/lib/types';
import { StatusBadge, PriorityBadge } from './Badges';
import { STATUS_LABELS } from '@/lib/types';
import {
  ArrowLeft, Loader2, AlertCircle, SendIcon, UserIcon,
  CalendarIcon, TagIcon, UserCircleIcon,
} from 'lucide-react';

export default function IssueDetail({ issueId, onNavigate }: { issueId: string; onNavigate: (page: string) => void }) {
  const { profile } = useAuth();
  const [issue, setIssue] = useState<Issue | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [staffList, setStaffList] = useState<Profile[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const loadIssue = useCallback(async () => {
    const { data } = await supabase
      .from('issues')
      .select('*, category:categories(*), created_by_profile:profiles!issues_created_by_fkey(full_name,role), assigned_to_profile:profiles!issues_assigned_to_fkey(full_name,role)')
      .eq('id', issueId)
      .maybeSingle();
    setIssue(data as unknown as Issue);
    setLoading(false);
  }, [issueId]);

  const loadComments = useCallback(async () => {
    const { data } = await supabase
      .from('comments')
      .select('*, user:profiles(full_name,role)')
      .eq('issue_id', issueId)
      .order('created_at', { ascending: true });
    setComments((data as unknown as Comment[]) || []);
  }, [issueId]);

  useEffect(() => {
    loadIssue();
    loadComments();
    if (profile?.role === 'admin') {
      supabase.from('profiles').select('*').eq('role', 'staff').order('full_name').then(({ data }) => {
        setStaffList((data as Profile[]) || []);
      });
      supabase.from('categories').select('*').order('name').then(({ data }) => {
        setCategories((data as Category[]) || []);
      });
    }

    const issueChannel = supabase
      .channel(`issue-detail-${issueId}`)
      .on('postgres_changes', { event: '*', schema: 'public', table: 'issues', filter: `id=eq.${issueId}` }, () => {
        loadIssue();
      })
      .on('postgres_changes', { event: '*', schema: 'public', table: 'comments', filter: `issue_id=eq.${issueId}` }, () => {
        loadComments();
      })
      .subscribe();

    return () => {
      supabase.removeChannel(issueChannel);
    };
  }, [profile, loadIssue, loadComments, issueId]);

  const updateIssue = async (updates: Partial<Issue>) => {
    setBusy(true);
    setError('');
    const { error: err } = await supabase.from('issues').update(updates).eq('id', issueId);
    if (err) setError(err.message);
    else await loadIssue();
    setBusy(false);
  };

  const addComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile || !newComment.trim()) return;
    setBusy(true);
    setError('');
    const { error: err } = await supabase.from('comments').insert({
      issue_id: issueId,
      user_id: profile.id,
      body: newComment.trim(),
    });
    if (err) setError(err.message);
    else {
      setNewComment('');
      await loadComments();
    }
    setBusy(false);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 text-blue-500 animate-spin" />
      </div>
    );
  }

  if (!issue) {
    return (
      <div className="text-center py-20">
        <p className="text-slate-400">Issue not found.</p>
        <button onClick={() => onNavigate('issues')} className="text-blue-600 mt-2">Back to issues</button>
      </div>
    );
  }

  const canUpdate = profile?.role === 'admin' || profile?.role === 'staff' || profile?.id === issue.created_by;
  const isAdmin = profile?.role === 'admin';

  return (
    <div className="max-w-3xl mx-auto space-y-5">
      <button
        onClick={() => onNavigate('issues')}
        className="flex items-center gap-2 text-slate-500 hover:text-slate-700 transition-colors text-sm"
      >
        <ArrowLeft className="w-4 h-4" /> Back to Issues
      </button>

      <div className="bg-white rounded-xl border border-slate-200 p-6">
        <div className="flex items-start justify-between gap-4 mb-4">
          <h1 className="text-xl font-bold text-slate-800 flex-1">{issue.title}</h1>
          <div className="flex items-center gap-2 shrink-0">
            <PriorityBadge priority={issue.priority} />
            <StatusBadge status={issue.status} />
          </div>
        </div>

        <div className="flex flex-wrap gap-4 text-sm text-slate-500 mb-5 pb-5 border-b border-slate-100">
          <span className="flex items-center gap-1.5">
            <UserIcon className="w-3.5 h-3.5" />
            {issue.created_by_profile?.full_name || 'Unknown'}
          </span>
          <span className="flex items-center gap-1.5">
            <CalendarIcon className="w-3.5 h-3.5" />
            {new Date(issue.created_at).toLocaleDateString()}
          </span>
          <span className="flex items-center gap-1.5">
            <TagIcon className="w-3.5 h-3.5" />
            {issue.category?.name || 'Uncategorized'}
          </span>
          {issue.assigned_to_profile && (
            <span className="flex items-center gap-1.5">
              <UserCircleIcon className="w-3.5 h-3.5" />
              Assigned: {issue.assigned_to_profile.full_name}
            </span>
          )}
        </div>

        <p className="text-slate-700 whitespace-pre-wrap leading-relaxed">{issue.description}</p>
      </div>

      {canUpdate && (
        <div className="bg-white rounded-xl border border-slate-200 p-5 space-y-4">
          <h3 className="font-semibold text-slate-800 text-sm">Manage Issue</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-500 mb-1.5">Status</label>
              <select
                value={issue.status}
                onChange={(e) => updateIssue({ status: e.target.value as IssueStatus })}
                disabled={busy}
                className="w-full px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
              >
                {(['open', 'in_progress', 'resolved', 'closed'] as IssueStatus[]).map((s) => (
                  <option key={s} value={s}>{STATUS_LABELS[s]}</option>
                ))}
              </select>
            </div>
            {isAdmin && (
              <>
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1.5">Assign To</label>
                  <select
                    value={issue.assigned_to || ''}
                    onChange={(e) => updateIssue({ assigned_to: e.target.value || null })}
                    disabled={busy}
                    className="w-full px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
                  >
                    <option value="">Unassigned</option>
                    {staffList.map((s) => (
                      <option key={s.id} value={s.id}>{s.full_name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1.5">Priority</label>
                  <select
                    value={issue.priority}
                    onChange={(e) => updateIssue({ priority: e.target.value as Issue['priority'] })}
                    disabled={busy}
                    className="w-full px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
                  >
                    <option value="low">Low</option>
                    <option value="medium">Medium</option>
                    <option value="high">High</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-500 mb-1.5">Category</label>
                  <select
                    value={issue.category_id || ''}
                    onChange={(e) => updateIssue({ category_id: e.target.value || null })}
                    disabled={busy}
                    className="w-full px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
                  >
                    <option value="">Uncategorized</option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
              </>
            )}
          </div>
          {error && (
            <div className="flex items-start gap-2 p-3 rounded-lg text-sm bg-red-50 text-red-700">
              <AlertCircle className="w-4 h-4 mt-0.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-200 p-5">
        <h3 className="font-semibold text-slate-800 mb-4">Comments ({comments.length})</h3>
        {comments.length === 0 ? (
          <p className="text-sm text-slate-400 py-4">No comments yet.</p>
        ) : (
          <div className="space-y-4 mb-4">
            {comments.map((c) => (
              <div key={c.id} className="flex gap-3">
                <div className="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center shrink-0">
                  <UserIcon className="w-4 h-4 text-slate-500" />
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-sm font-medium text-slate-700">{c.user?.full_name || 'Unknown'}</span>
                    <span className="text-xs text-slate-400">{new Date(c.created_at).toLocaleDateString()}</span>
                  </div>
                  <p className="text-sm text-slate-600 whitespace-pre-wrap">{c.body}</p>
                </div>
              </div>
            ))}
          </div>
        )}

        <form onSubmit={addComment} className="flex gap-2 mt-4 pt-4 border-t border-slate-100">
          <input
            type="text"
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Add a comment..."
            className="flex-1 px-4 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700"
          />
          <button
            type="submit"
            disabled={busy || !newComment.trim()}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors disabled:opacity-60 flex items-center gap-1.5 text-sm"
          >
            <SendIcon className="w-3.5 h-3.5" /> Post
          </button>
        </form>
      </div>
    </div>
  );
}
