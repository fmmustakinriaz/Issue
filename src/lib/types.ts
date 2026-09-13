export type Role = 'student' | 'staff' | 'admin';

export type Priority = 'low' | 'medium' | 'high';

export type IssueStatus = 'open' | 'in_progress' | 'resolved' | 'closed';

export interface Profile {
  id: string;
  full_name: string;
  role: Role;
  department: string | null;
  created_at: string;
}

export interface Category {
  id: string;
  name: string;
  description: string | null;
  created_at: string;
}

export interface Issue {
  id: string;
  title: string;
  description: string;
  category_id: string | null;
  priority: Priority;
  status: IssueStatus;
  created_by: string;
  assigned_to: string | null;
  created_at: string;
  updated_at: string;
  category?: Category | null;
  created_by_profile?: Profile | null;
  assigned_to_profile?: Profile | null;
}

export interface Comment {
  id: string;
  issue_id: string;
  user_id: string;
  body: string;
  created_at: string;
  user?: Profile | null;
}

export const PRIORITY_LABELS: Record<Priority, string> = {
  low: 'Low',
  medium: 'Medium',
  high: 'High',
};

export const STATUS_LABELS: Record<IssueStatus, string> = {
  open: 'Open',
  in_progress: 'In Progress',
  resolved: 'Resolved',
  closed: 'Closed',
};

export const PRIORITY_COLORS: Record<Priority, string> = {
  low: 'bg-blue-100 text-blue-700 border-blue-200',
  medium: 'bg-amber-100 text-amber-700 border-amber-200',
  high: 'bg-red-100 text-red-700 border-red-200',
};

export const STATUS_COLORS: Record<IssueStatus, string> = {
  open: 'bg-blue-100 text-blue-700 border-blue-200',
  in_progress: 'bg-amber-100 text-amber-700 border-amber-200',
  resolved: 'bg-emerald-100 text-emerald-700 border-emerald-200',
  closed: 'bg-gray-100 text-gray-600 border-gray-200',
};
