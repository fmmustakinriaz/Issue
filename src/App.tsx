import { useState } from 'react';
import { AuthProvider, useAuth } from '@/lib/auth';
import Auth from '@/components/Auth';
import Layout from '@/components/Layout';
import Dashboard from '@/components/Dashboard';
import IssueList from '@/components/IssueList';
import SubmitIssue from '@/components/SubmitIssue';
import IssueDetail from '@/components/IssueDetail';
import AdminUsers from '@/components/AdminUsers';
import AdminCategories from '@/components/AdminCategories';
import { Loader2 } from 'lucide-react';

function AppContent() {
  const { session, profile, loading } = useAuth();
  const [page, setPage] = useState('dashboard');

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <Loader2 className="w-8 h-8 text-blue-500 animate-spin" />
      </div>
    );
  }

  if (!session || !profile) {
    return <Auth />;
  }

  const navigate = (p: string) => setPage(p);

  let content: React.ReactNode;
  if (page.startsWith('issue:')) {
    content = <IssueDetail issueId={page.slice(6)} onNavigate={navigate} />;
  } else {
    switch (page) {
      case 'dashboard':
        content = <Dashboard onNavigate={navigate} />;
        break;
      case 'issues':
        content = <IssueList onNavigate={navigate} />;
        break;
      case 'submit':
        content = <SubmitIssue onNavigate={navigate} />;
        break;
      case 'users':
        content = profile.role === 'admin' ? <AdminUsers /> : <Dashboard onNavigate={navigate} />;
        break;
      case 'categories':
        content = profile.role === 'admin' ? <AdminCategories /> : <Dashboard onNavigate={navigate} />;
        break;
      default:
        content = <Dashboard onNavigate={navigate} />;
    }
  }

  return (
    <Layout currentPage={page} onNavigate={navigate}>
      {content}
    </Layout>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
