import { useEffect, useState, useCallback } from 'react';
import { supabase } from '@/lib/supabase';
import { useAuth } from '@/lib/auth';
import type { Issue, IssueStatus } from '@/lib/types';
import { StatusBadge, PriorityBadge } from './Badges';
import {
  TicketIcon, CheckCircle2, ClockIcon, XCircleIcon,
  TrendingUp, AlertTriangle, ArrowRight,
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

interface Stats {
  total: number;
  open: number;
  in_progress: number;
  resolved: number;
  closed: number;
  high_priority: number;
}

const STAT_CARDS: { key: IssueStatus | 'total' | 'high'; label: string; icon: LucideIcon; color: string; bg: string }[] = [
  { key: 'total', label: 'Total Issues', icon: TicketIcon, color: 'text-slate-700', bg: 'bg-slate-100' },
  { key: 'open', label: 'Open', icon: AlertTriangle, color: 'text-blue-700', bg: 'bg-blue-100' },
  { key: 'in_progress', label: 'In Progress', icon: ClockIcon, color: 'text-amber-700', bg: 'bg-amber-100' },
  { key: 'resolved', label: 'Resolved', icon: CheckCircle2, color: 'text-emerald-700', bg: 'bg-emerald-100' },
  { key: 'closed', label: 'Closed', icon: XCircleIcon, color: 'text-gray-600', bg: 'bg-gray-100' },
  { key: 'high', label: 'High Priority', icon: TrendingUp, color: 'text-red-700', bg: 'bg-red-100' },
];

export default function Dashboard({ onNavigate }: { onNavigate: (page: string) => void }) {
  const { profile } = useAuth();
  const [stats, setStats] = useState<Stats>({ total: 0, open: 0, in_progress: 0, resolved: 0, closed: 0, high_priority: 0 });
  const [recent, setRecent] = useState<Issue[]>([]);
  const [loading, setLoading] = useState(true);

  const loadData = useCallback(async () => {
    const { data: recentData } = await supabase
      .from('issues')
      .select('*, category:categories(*), created_by_profile:profiles!issues_created_by_fkey(full_name,role), assigned_to_profile:profiles!issues_assigned_to_fkey(full_name,role)')
      .order('created_at', { ascending: false })
      .limit(5);
    setRecent((recentData as unknown as Issue[]) || []);

    const { data: all } = await supabase.from('issues').select('status, priority');
    const s: Stats = { total: 0, open: 0, in_progress: 0, resolved: 0, closed: 0, high_priority: 0 };
    (all || []).forEach((i: { status: string; priority: string }) => {
      s.total++;
      if (i.status === 'open') s.open++;
      else if (i.status === 'in_progress') s.in_progress++;
      else if (i.status === 'resolved') s.resolved++;
      else if (i.status === 'closed') s.closed++;
      if (i.priority === 'high') s.high_priority++;
    });
    setStats(s);
    setLoading(false);
  }, []);

  useEffect(() => {
    loadData();

    const channel = supabase
      .channel('dashboard-realtime')
      .on('postgres_changes', { event: '*', schema: 'public', table: 'issues' }, () => {
        loadData();
      })
      .subscribe();

    return () => {
      supabase.removeChannel(channel);
    };
  }, [loadData]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <p className="text-slate-500 mt-1">
          Welcome back, {profile?.full_name} — here's an overview of all department issues.
        </p>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {STAT_CARDS.map((card) => {
          const Icon = card.icon;
          const value = card.key === 'total' ? stats.total : card.key === 'high' ? stats.high_priority : stats[card.key as IssueStatus];
          return (
            <div key={card.key} className="bg-white rounded-xl border border-slate-200 p-4 hover:shadow-md transition-shadow">
              <div className={`w-10 h-10 rounded-lg flex items-center justify-center mb-3 ${card.bg}`}>
                <Icon className={`w-5 h-5 ${card.color}`} />
              </div>
              <p className="text-2xl font-bold text-slate-800">{loading ? '—' : value}</p>
              <p className="text-xs text-slate-500 mt-0.5">{card.label}</p>
            </div>
          );
        })}
      </div>

      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <h2 className="font-semibold text-slate-800">Recent Issues</h2>
          <button
            onClick={() => onNavigate('issues')}
            className="text-sm text-blue-600 hover:text-blue-700 flex items-center gap-1 font-medium"
          >
            View all <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
        {recent.length === 0 ? (
          <div className="px-5 py-12 text-center text-slate-400">
            <TicketIcon className="w-10 h-10 mx-auto mb-3 text-slate-300" />
            <p>No issues yet</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-50">
            {recent.map((issue) => (
              <button
                key={issue.id}
                onClick={() => onNavigate(`issue:${issue.id}`)}
                className="w-full flex items-center justify-between px-5 py-3.5 hover:bg-slate-50 transition-colors text-left"
              >
                <div className="min-w-0 flex-1">
                  <p className="font-medium text-slate-800 truncate">{issue.title}</p>
                  <p className="text-sm text-slate-400 mt-0.5">
                    {issue.category?.name || 'Uncategorized'} · {issue.created_by_profile?.full_name || 'Unknown'} · {new Date(issue.created_at).toLocaleDateString()}
                  </p>
                </div>
                <div className="flex items-center gap-2 ml-4 shrink-0">
                  <PriorityBadge priority={issue.priority} />
                  <StatusBadge status={issue.status} />
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      {profile?.role === 'student' && (
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-xl p-6 text-white">
          <h3 className="font-semibold text-lg">Have an issue to report?</h3>
          <p className="text-blue-100 text-sm mt-1 mb-4">Submit a new issue and track its progress.</p>
          <button
            onClick={() => onNavigate('submit')}
            className="bg-white text-blue-700 px-4 py-2 rounded-lg font-medium hover:bg-blue-50 transition-colors"
          >
            Submit New Issue
          </button>
        </div>
      )}
    </div>
  );
}
