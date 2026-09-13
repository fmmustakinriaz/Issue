import { useEffect, useState, useCallback } from 'react';
import { supabase } from '@/lib/supabase';
import { useAuth } from '@/lib/auth';
import type { Issue, IssueStatus, Category } from '@/lib/types';
import { StatusBadge, PriorityBadge } from './Badges';
import { SearchIcon, TicketIcon, Loader2 } from 'lucide-react';

interface Props {
  onNavigate: (page: string) => void;
}

export default function IssueList({ onNavigate }: Props) {
  const { profile } = useAuth();
  const [issues, setIssues] = useState<Issue[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<IssueStatus | 'all'>('all');
  const [categoryFilter, setCategoryFilter] = useState<string>('all');

  const loadIssues = useCallback(async () => {
    let query = supabase.from('issues').select('*, category:categories(*), created_by_profile:profiles!issues_created_by_fkey(full_name,role), assigned_to_profile:profiles!issues_assigned_to_fkey(full_name,role)');

    if (statusFilter !== 'all') query = query.eq('status', statusFilter);
    if (categoryFilter !== 'all') query = query.eq('category_id', categoryFilter);
    if (search) query = query.ilike('title', `%${search}%`);

    const { data } = await query.order('created_at', { ascending: false });
    setIssues((data as unknown as Issue[]) || []);
    setLoading(false);
  }, [statusFilter, categoryFilter, search]);

  useEffect(() => {
    supabase.from('categories').select('*').order('name').then(({ data }) => {
      setCategories((data as Category[]) || []);
    });
  }, []);

  useEffect(() => {
    loadIssues();
    const channel = supabase
      .channel('issues-realtime')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'issues' }, () => {
        loadIssues();
      })
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [loadIssues]);

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Issues</h1>
          <p className="text-slate-500 mt-1">
            {profile?.role === 'student' ? 'All department issues' : profile?.role === 'staff' ? 'All department issues' : 'All department issues'}
          </p>
        </div>
        {profile?.role === 'student' && (
          <button
            onClick={() => onNavigate('submit')}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors"
          >
            New Issue
          </button>
        )}
      </div>

      <div className="flex flex-wrap gap-3">
        <div className="relative flex-1 min-w-[200px]">
          <SearchIcon className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search issues..."
            className="w-full pl-9 pr-4 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700"
          />
        </div>
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as IssueStatus | 'all')}
          className="px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
        >
          <option value="all">All Statuses</option>
          <option value="open">Open</option>
          <option value="in_progress">In Progress</option>
          <option value="resolved">Resolved</option>
          <option value="closed">Closed</option>
        </select>
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="px-3 py-2 rounded-lg border border-slate-200 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none text-sm text-slate-700 bg-white"
        >
          <option value="all">All Categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-16">
          <Loader2 className="w-6 h-6 text-blue-500 animate-spin" />
        </div>
      ) : issues.length === 0 ? (
        <div className="bg-white rounded-xl border border-slate-200 py-16 text-center">
          <TicketIcon className="w-12 h-12 mx-auto mb-3 text-slate-300" />
          <p className="text-slate-400">No issues found</p>
        </div>
      ) : (
        <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 bg-slate-50/50">
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Title</th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Category</th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Priority</th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Status</th>
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Submitted By</th>
                  {profile?.role === 'admin' && (
                    <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Assigned To</th>
                  )}
                  <th className="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {issues.map((issue) => (
                  <tr
                    key={issue.id}
                    onClick={() => onNavigate(`issue:${issue.id}`)}
                    className="cursor-pointer hover:bg-slate-50 transition-colors"
                  >
                    <td className="px-5 py-3.5">
                      <p className="font-medium text-slate-800">{issue.title}</p>
                      <p className="text-xs text-slate-400 mt-0.5 truncate max-w-xs">{issue.description}</p>
                    </td>
                    <td className="px-5 py-3.5 text-sm text-slate-600">{issue.category?.name || '—'}</td>
                    <td className="px-5 py-3.5"><PriorityBadge priority={issue.priority} /></td>
                    <td className="px-5 py-3.5"><StatusBadge status={issue.status} /></td>
                    <td className="px-5 py-3.5 text-sm text-slate-600">{issue.created_by_profile?.full_name || '—'}</td>
                    {profile?.role === 'admin' && (
                      <td className="px-5 py-3.5 text-sm text-slate-600">{issue.assigned_to_profile?.full_name || 'Unassigned'}</td>
                    )}
                    <td className="px-5 py-3.5 text-sm text-slate-400">{new Date(issue.created_at).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
