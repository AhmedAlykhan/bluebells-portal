import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/client';
import { 
  UploadCloud, 
  FileSpreadsheet, 
  CheckCircle2, 
  AlertTriangle, 
  Download, 
  RefreshCw, 
  Search, 
  ShieldCheck, 
  UserCheck, 
  Users, 
  Check, 
  ArrowRight,
  Database
} from 'lucide-react';
import { Link } from 'react-router-dom';

interface ImportStatistics {
  totalRecordsImported: number;
  successfulRecords: number;
  failedRecords: number;
  duplicateRecords: number;
  pendingJobs: number;
  totalCompletedJobs: number;
  lastImportDate: string | null;
  lastImportUser: string | null;
}

interface ImportRow {
  id: number;
  rowNumber: number;
  sheetName: string;
  entityIdentifier: string;
  status: 'VALID' | 'INVALID' | 'DUPLICATE' | 'WARNING' | 'IMPORTED' | 'SKIPPED' | 'FAILED';
  errorMessage: string | null;
  rowData: Record<string, string>;
  errors?: {
    fieldName: string;
    fieldValue: string;
    errorMessage: string;
    severity: string;
  }[];
}

interface ImportJobPreview {
  jobId: string;
  importType: string;
  status: string;
  originalFilename: string;
  fileFormat: string;
  fileSizeBytes: number;
  totalRows: number;
  validRows: number;
  invalidRows: number;
  duplicateRows: number;
  warningRows: number;
  importedRows: number;
  skippedRows: number;
  importMode: string;
  rows: ImportRow[];
  allErrors: {
    id: number;
    rowNumber: number;
    fieldName: string;
    fieldValue: string;
    errorCode: string;
    errorMessage: string;
    severity: string;
  }[];
  page: number;
  size: number;
  totalFiltered: number;
}

interface ImportJobSummary {
  jobId: string;
  importType: string;
  originalFilename: string;
  fileFormat: string;
  fileSizeBytes: number;
  status: string;
  importMode: string;
  totalRows: number;
  validRows: number;
  invalidRows: number;
  duplicateRows: number;
  warningRows: number;
  importedRows: number;
  skippedRows: number;
  uploadedBy: string;
  confirmedBy: string | null;
  summaryNotes: string | null;
  createdAt: string;
  completedAt: string | null;
}

interface UserPermission {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  roles: string[];
  importPermissions: string[];
}

const ALL_IMPORT_PERMISSIONS = [
  { code: 'DATA_IMPORT_VIEW', label: 'View Dashboard & Metrics' },
  { code: 'DATA_IMPORT_DOWNLOAD_TEMPLATE', label: 'Download Templates' },
  { code: 'DATA_IMPORT_UPLOAD', label: 'Upload Data Files' },
  { code: 'DATA_IMPORT_VALIDATE', label: 'Run Validation Engine' },
  { code: 'DATA_IMPORT_CONFIRM', label: 'Execute & Commit Imports' },
  { code: 'DATA_IMPORT_VIEW_HISTORY', label: 'View History & Logs' },
  { code: 'DATA_IMPORT_DOWNLOAD_REPORT', label: 'Download Audit Reports' },
  { code: 'DATA_IMPORT_CANCEL', label: 'Cancel Staged Jobs' },
];

export const BulkDataImportPage: React.FC = () => {
  const { user } = useAuth();
  const roles = user?.roles || [];
  const isSuperAdmin = roles.includes('ROLE_SUPER_ADMIN');

  // Navigation tab: 'wizard' | 'history' | 'permissions'
  const [activeTab, setActiveTab] = useState<'wizard' | 'history' | 'permissions'>('wizard');

  // Top KPI Metrics
  const [stats, setStats] = useState<ImportStatistics>({
    totalRecordsImported: 0,
    successfulRecords: 0,
    failedRecords: 0,
    duplicateRecords: 0,
    pendingJobs: 0,
    totalCompletedJobs: 0,
    lastImportDate: null,
    lastImportUser: null,
  });

  // Wizard state: 1 to 6
  const [wizardStep, setWizardStep] = useState<number>(1);
  const [importType, setImportType] = useState<'STUDENT' | 'STAFF'>('STUDENT');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState<boolean>(false);
  const [isValidating, setIsValidating] = useState<boolean>(false);
  const [isExecuting, setIsExecuting] = useState<boolean>(false);
  const [currentJob, setCurrentJob] = useState<ImportJobPreview | null>(null);
  const [executionResult, setExecutionResult] = useState<ImportJobSummary | null>(null);

  // Preview filtering & search
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Mode selection & notes
  const [importMode, setImportMode] = useState<'STRICT' | 'VALID_ONLY'>('STRICT');
  const [importNotes, setImportNotes] = useState<string>('');
  const [showConfirmModal, setShowConfirmModal] = useState<boolean>(false);
  const [errorAlert, setErrorAlert] = useState<string | null>(null);
  const [successAlert, setSuccessAlert] = useState<string | null>(null);

  // History state
  const [historyJobs, setHistoryJobs] = useState<ImportJobSummary[]>([]);
  const [isLoadingHistory, setIsLoadingHistory] = useState<boolean>(false);

  // Permissions state
  const [eligibleUsers, setEligibleUsers] = useState<UserPermission[]>([]);
  const [isLoadingPermissions, setIsLoadingPermissions] = useState<boolean>(false);

  const fileInputRef = useRef<HTMLInputElement>(null);

  // Load KPI Stats
  const fetchStatistics = async () => {
    try {
      const res = await apiClient.get('/admin/data-import/statistics');
      if (res.data?.data) {
        setStats(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load import statistics', err);
    }
  };

  // Load History
  const fetchHistory = async () => {
    setIsLoadingHistory(true);
    try {
      const res = await apiClient.get('/admin/data-import/history?page=0&size=20');
      if (res.data?.data?.content) {
        setHistoryJobs(res.data.data.content);
      }
    } catch (err) {
      console.error('Failed to load import history', err);
    } finally {
      setIsLoadingHistory(false);
    }
  };

  // Load Permissions (Super Admin only)
  const fetchPermissions = async () => {
    if (!isSuperAdmin) return;
    setIsLoadingPermissions(true);
    try {
      const res = await apiClient.get('/admin/data-import/permissions');
      if (res.data?.data) {
        setEligibleUsers(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load eligible users for permissions', err);
    } finally {
      setIsLoadingPermissions(false);
    }
  };

  useEffect(() => {
    fetchStatistics();
  }, []);

  useEffect(() => {
    if (activeTab === 'history') {
      fetchHistory();
    } else if (activeTab === 'permissions') {
      fetchPermissions();
    }
  }, [activeTab]);

  // Handle template download
  const handleDownloadTemplate = (format: 'xlsx' | 'csv') => {
    const type = importType.toLowerCase();
    const url = `/api/v1/admin/data-import/templates/${type}.${format}`;
    window.open(url, '_blank');
  };

  // Handle File Selection
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      const name = file.name.toLowerCase();
      if (!name.endsWith('.xlsx') && !name.endsWith('.csv')) {
        setErrorAlert('Only .xlsx and .csv files are supported.');
        return;
      }
      if (file.size > 20 * 1024 * 1024) {
        setErrorAlert('File size exceeds the 20MB maximum allowable limit.');
        return;
      }
      setSelectedFile(file);
      setErrorAlert(null);
    }
  };

  // Upload and Stage File
  const handleUploadFile = async () => {
    if (!selectedFile) {
      setErrorAlert('Please select a file to upload.');
      return;
    }

    setIsUploading(true);
    setErrorAlert(null);
    try {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('importType', importType);

      const res = await apiClient.post('/admin/data-import/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });

      const jobId = res.data?.data?.jobId;
      if (jobId) {
        setSuccessAlert('File staged successfully. Running server validation...');
        setWizardStep(4);
        await runValidation(jobId);
      }
    } catch (err: any) {
      setErrorAlert(err.response?.data?.message || err.message || 'File upload failed.');
    } finally {
      setIsUploading(false);
    }
  };

  // Run Server Validation
  const runValidation = async (jobId: string) => {
    setIsValidating(true);
    setErrorAlert(null);
    try {
      const res = await apiClient.post(`/admin/data-import/${jobId}/validate`);
      if (res.data?.data) {
        setCurrentJob(res.data.data);
        setSuccessAlert('Validation completed. Inspect the preview table below.');
      }
    } catch (err: any) {
      setErrorAlert(err.response?.data?.message || err.message || 'Validation failed.');
    } finally {
      setIsValidating(false);
    }
  };

  // Filter preview records
  const fetchFilteredPreview = async (filter: string, search: string) => {
    if (!currentJob) return;
    try {
      const params = new URLSearchParams({
        page: '0',
        size: '50',
        statusFilter: filter,
        search: search
      });
      const res = await apiClient.get(`/admin/data-import/${currentJob.jobId}/preview?${params.toString()}`);
      if (res.data?.data) {
        setCurrentJob(res.data.data);
      }
    } catch (err) {
      console.error('Failed to filter preview', err);
    }
  };

  const handleFilterChange = (filter: string) => {
    setStatusFilter(filter);
    fetchFilteredPreview(filter, searchQuery);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setSearchQuery(val);
    fetchFilteredPreview(statusFilter, val);
  };

  // Cancel Staged Job
  const handleCancelJob = async () => {
    if (!currentJob) return;
    try {
      await apiClient.post(`/admin/data-import/${currentJob.jobId}/cancel`);
      setSuccessAlert('Import job has been cancelled.');
      setCurrentJob(null);
      setSelectedFile(null);
      setWizardStep(1);
      fetchStatistics();
    } catch (err: any) {
      setErrorAlert(err.response?.data?.message || err.message || 'Failed to cancel job.');
    }
  };

  // Execute and Confirm Import
  const handleConfirmImport = async () => {
    if (!currentJob) return;
    setIsExecuting(true);
    setErrorAlert(null);
    setShowConfirmModal(false);

    try {
      const res = await apiClient.post(`/admin/data-import/${currentJob.jobId}/confirm`, {
        importMode: importMode,
        notes: importNotes || `Bulk import of ${importType} records`
      });

      if (res.data?.data) {
        setExecutionResult(res.data.data);
        setWizardStep(6);
        setSuccessAlert('Data records imported successfully into live database!');
        fetchStatistics();
      }
    } catch (err: any) {
      setErrorAlert(err.response?.data?.message || err.message || 'Import execution failed.');
    } finally {
      setIsExecuting(false);
    }
  };

  // Download Execution Report
  const handleDownloadReport = (jobId: string) => {
    window.open(`/api/v1/admin/data-import/${jobId}/report`, '_blank');
  };

  // Toggle user permission (Super Admin)
  const handleTogglePermission = async (userPerm: UserPermission, permCode: string) => {
    const hasPerm = userPerm.importPermissions.includes(permCode);
    try {
      if (hasPerm) {
        // Revoke
        await apiClient.post('/admin/data-import/permissions/revoke', {
          userId: userPerm.userId,
          permissionCodes: [permCode]
        });
      } else {
        // Grant
        await apiClient.post('/admin/data-import/permissions/grant', {
          userId: userPerm.userId,
          permissionCodes: [permCode]
        });
      }
      fetchPermissions();
      setSuccessAlert(`Permission ${hasPerm ? 'revoked from' : 'granted to'} ${userPerm.fullName}`);
    } catch (err: any) {
      setErrorAlert(err.response?.data?.message || err.message || 'Permission update failed.');
    }
  };

  // Restart wizard
  const handleStartNewImport = () => {
    setCurrentJob(null);
    setExecutionResult(null);
    setSelectedFile(null);
    setWizardStep(1);
    setStatusFilter('ALL');
    setSearchQuery('');
    setErrorAlert(null);
    setSuccessAlert(null);
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-blue-900 to-indigo-900 text-white p-6 rounded-3xl shadow-xl">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-3 py-1 bg-white/10 text-white rounded-full text-xs font-bold uppercase tracking-wider backdrop-blur-md">
              Institutional Governance
            </span>
            <span className="px-3 py-1 bg-emerald-500/20 text-emerald-300 border border-emerald-400/30 rounded-full text-xs font-semibold flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5" /> High-Assurance RBAC
            </span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight">Bulk Data Import & Migration</h1>
          <p className="text-sm text-blue-100/90 max-w-3xl">
            Upload verified student admissions, guardian records, session enrollments, and faculty profiles via standardized Excel & CSV templates with server-side constraint validation.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => { fetchStatistics(); if (activeTab === 'history') fetchHistory(); }}
            className="flex items-center gap-2 px-4 py-2.5 bg-white/10 hover:bg-white/20 text-white text-xs font-bold rounded-xl transition border border-white/20 shadow-sm"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Refresh Telemetry
          </button>
        </div>
      </div>

      {/* Top Telemetry KPI Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Imported</p>
          <div className="flex items-baseline gap-2 mt-1">
            <p className="text-2xl font-black text-slate-800">{stats.totalRecordsImported.toLocaleString()}</p>
            <span className="text-[10px] text-emerald-600 font-bold bg-emerald-50 px-1.5 py-0.5 rounded">Committed</span>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">All-time DB records</p>
        </div>

        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Success Rate</p>
          <div className="flex items-baseline gap-2 mt-1">
            <p className="text-2xl font-black text-emerald-600">
              {stats.totalRecordsImported + stats.failedRecords > 0 
                ? Math.round((stats.totalRecordsImported / (stats.totalRecordsImported + stats.failedRecords)) * 100) 
                : 100}%
            </p>
            <span className="text-[10px] text-emerald-600 font-bold bg-emerald-50 px-1.5 py-0.5 rounded">Quality</span>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">Zero-corruption intake</p>
        </div>

        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Failed / Invalid</p>
          <div className="flex items-baseline gap-2 mt-1">
            <p className="text-2xl font-black text-rose-600">{stats.failedRecords.toLocaleString()}</p>
            <span className="text-[10px] text-rose-600 font-bold bg-rose-50 px-1.5 py-0.5 rounded">Filtered</span>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">Schema rule violations</p>
        </div>

        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Duplicates Caught</p>
          <div className="flex items-baseline gap-2 mt-1">
            <p className="text-2xl font-black text-amber-600">{stats.duplicateRecords.toLocaleString()}</p>
            <span className="text-[10px] text-amber-600 font-bold bg-amber-50 px-1.5 py-0.5 rounded">Prevented</span>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">File & DB collisions</p>
        </div>

        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Pending Batches</p>
          <div className="flex items-baseline gap-2 mt-1">
            <p className="text-2xl font-black text-blue-600">{stats.pendingJobs}</p>
            <span className="text-[10px] text-blue-600 font-bold bg-blue-50 px-1.5 py-0.5 rounded">In Staging</span>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">Awaiting confirmation</p>
        </div>

        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Last Import</p>
          <div className="mt-1">
            <p className="text-xs font-bold text-slate-800 truncate">{stats.lastImportUser || 'None'}</p>
            <p className="text-[10px] text-slate-500 truncate">
              {stats.lastImportDate ? new Date(stats.lastImportDate).toLocaleDateString() : 'No imports yet'}
            </p>
          </div>
          <p className="text-[10px] text-slate-400 mt-2">Latest executed batch</p>
        </div>
      </div>

      {/* Alerts */}
      {errorAlert && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl flex items-start justify-between text-rose-800 text-xs font-medium animate-fadeIn">
          <div className="flex items-center gap-2">
            <AlertTriangle className="w-4 h-4 text-rose-600 shrink-0" />
            <span>{errorAlert}</span>
          </div>
          <button onClick={() => setErrorAlert(null)} className="text-rose-500 hover:text-rose-700">✕</button>
        </div>
      )}
      {successAlert && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-2xl flex items-start justify-between text-emerald-800 text-xs font-medium animate-fadeIn">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
            <span>{successAlert}</span>
          </div>
          <button onClick={() => setSuccessAlert(null)} className="text-emerald-500 hover:text-emerald-700">✕</button>
        </div>
      )}

      {/* Main Tabs Navigation */}
      <div className="flex items-center gap-2 border-b border-slate-200 pb-2">
        <button
          onClick={() => setActiveTab('wizard')}
          className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold transition ${
            activeTab === 'wizard'
              ? 'bg-blue-900 text-white shadow-md'
              : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
          }`}
        >
          <UploadCloud className="w-4 h-4" /> Import Wizard
        </button>

        <button
          onClick={() => setActiveTab('history')}
          className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold transition ${
            activeTab === 'history'
              ? 'bg-blue-900 text-white shadow-md'
              : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
          }`}
        >
          <Database className="w-4 h-4" /> Import History & Audit Trail
        </button>

        {isSuperAdmin && (
          <button
            onClick={() => setActiveTab('permissions')}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold transition ${
              activeTab === 'permissions'
                ? 'bg-blue-900 text-white shadow-md'
                : 'bg-white text-slate-600 hover:bg-slate-100 border border-slate-200'
            }`}
          >
            <ShieldCheck className="w-4 h-4" /> Leadership Access Control
          </button>
        )}
      </div>

      {/* TAB 1: IMPORT WIZARD */}
      {activeTab === 'wizard' && (
        <div className="space-y-6">
          {/* Progress Steps Header */}
          <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between max-w-4xl mx-auto overflow-x-auto py-1">
              {[
                { num: 1, title: 'Select Type' },
                { num: 2, title: 'Download Template' },
                { num: 3, title: 'Upload & Stage' },
                { num: 4, title: 'Validation & Preview' },
                { num: 5, title: 'Execution Mode' },
                { num: 6, title: 'Result Receipt' },
              ].map((step) => {
                const isPassed = wizardStep > step.num;
                const isCurrent = wizardStep === step.num;
                return (
                  <div key={step.num} className="flex items-center gap-2 shrink-0 px-2">
                    <div
                      className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-extrabold transition ${
                        isPassed
                          ? 'bg-emerald-600 text-white'
                          : isCurrent
                          ? 'bg-blue-900 text-white ring-4 ring-blue-100'
                          : 'bg-slate-100 text-slate-400'
                      }`}
                    >
                      {isPassed ? <Check className="w-4 h-4" /> : step.num}
                    </div>
                    <span
                      className={`text-xs font-bold ${
                        isCurrent ? 'text-blue-900' : isPassed ? 'text-slate-700' : 'text-slate-400'
                      }`}
                    >
                      {step.title}
                    </span>
                    {step.num < 6 && <div className="w-6 h-0.5 bg-slate-200 mx-1" />}
                  </div>
                );
              })}
            </div>
          </div>

          {/* STEP 1: SELECT IMPORT TYPE */}
          {wizardStep === 1 && (
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
              <div>
                <h2 className="text-lg font-bold text-slate-900">Step 1: Choose Import Target Module</h2>
                <p className="text-xs text-slate-500 mt-1">Select the classification of school records you intend to bulk import.</p>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                <div
                  onClick={() => setImportType('STUDENT')}
                  className={`p-6 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                    importType === 'STUDENT'
                      ? 'border-blue-900 bg-blue-50/50 shadow-md ring-2 ring-blue-900/10'
                      : 'border-slate-200 hover:border-slate-300 bg-white'
                  }`}
                >
                  <div className="space-y-3">
                    <div className="w-12 h-12 rounded-xl bg-blue-100 text-blue-900 flex items-center justify-center">
                      <Users className="w-6 h-6" />
                    </div>
                    <h3 className="text-base font-extrabold text-slate-900">Student & Guardian Records</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Bulk ingest student admissions, national identification (B-Form), residential addresses, parent/guardian profiles, and academic session classroom enrollments with roll numbers.
                    </p>
                    <div className="flex flex-wrap gap-1.5 pt-2">
                      <span className="text-[10px] font-bold bg-blue-100 text-blue-800 px-2 py-0.5 rounded-full">Students Table</span>
                      <span className="text-[10px] font-bold bg-indigo-100 text-indigo-800 px-2 py-0.5 rounded-full">Guardians Table</span>
                      <span className="text-[10px] font-bold bg-cyan-100 text-cyan-800 px-2 py-0.5 rounded-full">Enrollments Table</span>
                    </div>
                  </div>
                  <div className="pt-6 flex items-center justify-between text-xs font-bold text-blue-900">
                    <span>Selected Category</span>
                    {importType === 'STUDENT' && <CheckCircle2 className="w-5 h-5 text-blue-900" />}
                  </div>
                </div>

                <div
                  onClick={() => setImportType('STAFF')}
                  className={`p-6 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                    importType === 'STAFF'
                      ? 'border-blue-900 bg-blue-50/50 shadow-md ring-2 ring-blue-900/10'
                      : 'border-slate-200 hover:border-slate-300 bg-white'
                  }`}
                >
                  <div className="space-y-3">
                    <div className="w-12 h-12 rounded-xl bg-indigo-100 text-indigo-900 flex items-center justify-center">
                      <UserCheck className="w-6 h-6" />
                    </div>
                    <h3 className="text-base font-extrabold text-slate-900">Staff & Faculty Directory</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Bulk ingest teaching faculty, administrative personnel, official school emails, staff qualification credentials, salary specifications, and teacher subject-classroom assignments.
                    </p>
                    <div className="flex flex-wrap gap-1.5 pt-2">
                      <span className="text-[10px] font-bold bg-indigo-100 text-indigo-800 px-2 py-0.5 rounded-full">Staff Table</span>
                      <span className="text-[10px] font-bold bg-purple-100 text-purple-800 px-2 py-0.5 rounded-full">Users Table</span>
                      <span className="text-[10px] font-bold bg-violet-100 text-violet-800 px-2 py-0.5 rounded-full">Teacher Assignments</span>
                    </div>
                  </div>
                  <div className="pt-6 flex items-center justify-between text-xs font-bold text-indigo-900">
                    <span>Selected Category</span>
                    {importType === 'STAFF' && <CheckCircle2 className="w-5 h-5 text-indigo-900" />}
                  </div>
                </div>
              </div>

              <div className="flex justify-end pt-4">
                <button
                  onClick={() => setWizardStep(2)}
                  className="flex items-center gap-2 px-6 py-3 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl shadow-md transition"
                >
                  Proceed to Template Download <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}

          {/* STEP 2: DOWNLOAD TEMPLATES & INSTRUCTIONS */}
          {wizardStep === 2 && (
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-100">
                <div>
                  <h2 className="text-lg font-bold text-slate-900">
                    Step 2: Download Standardized {importType === 'STUDENT' ? 'Student' : 'Staff'} Import Templates
                  </h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Use our official pre-formatted Excel or CSV template with built-in validation dropdowns and reference sheets.
                  </p>
                </div>
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => handleDownloadTemplate('xlsx')}
                    className="flex items-center gap-2 px-4 py-2.5 bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-bold rounded-xl shadow transition"
                  >
                    <Download className="w-4 h-4" /> Download Official Excel (.xlsx)
                  </button>
                  <button
                    onClick={() => handleDownloadTemplate('csv')}
                    className="flex items-center gap-2 px-4 py-2.5 bg-slate-800 hover:bg-slate-900 text-white text-xs font-bold rounded-xl shadow transition"
                  >
                    <Download className="w-4 h-4" /> Download CSV (.csv)
                  </button>
                </div>
              </div>

              {/* Template Specs Grid */}
              <div className="grid md:grid-cols-3 gap-4">
                <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 space-y-2">
                  <div className="flex items-center gap-2 text-blue-900 font-bold text-xs">
                    <FileSpreadsheet className="w-4 h-4" /> Formatted Excel (.xlsx)
                  </div>
                  <ul className="text-[11px] text-slate-600 space-y-1 list-disc pl-4">
                    <li>Navy blue stylized headers (#1E3A8A)</li>
                    <li>Frozen top header pane</li>
                    <li>Dropdown menus for Gender, Blood Group, Relation, Category</li>
                    <li>Instructions & Reference_Values sheets</li>
                  </ul>
                </div>

                <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 space-y-2">
                  <div className="flex items-center gap-2 text-indigo-900 font-bold text-xs">
                    <CheckCircle2 className="w-4 h-4" /> Mandatory Constraints
                  </div>
                  <ul className="text-[11px] text-slate-600 space-y-1 list-disc pl-4">
                    <li>Dates must strictly follow <code className="text-rose-600 font-bold">YYYY-MM-DD</code></li>
                    <li>CNIC format: <code className="text-rose-600 font-bold">42101-1234567-1</code></li>
                    <li>Session, Class & Section must match active DB entries</li>
                    <li>Unique Admission # / Employee ID</li>
                  </ul>
                </div>

                <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 space-y-2">
                  <div className="flex items-center gap-2 text-rose-900 font-bold text-xs">
                    <ShieldCheck className="w-4 h-4" /> Security Safeguards
                  </div>
                  <ul className="text-[11px] text-slate-600 space-y-1 list-disc pl-4">
                    <li>CSV & Excel Formula Injection protected</li>
                    <li>Values starting with =, +, -, @ are sanitized</li>
                    <li>Maximum file size: 20 Megabytes</li>
                    <li>Duplicate detection across file and DB</li>
                  </ul>
                </div>
              </div>

              <div className="flex items-center justify-between pt-4">
                <button
                  onClick={() => setWizardStep(1)}
                  className="px-5 py-2.5 text-slate-600 hover:text-slate-800 text-xs font-bold transition"
                >
                  ← Back to Category
                </button>
                <button
                  onClick={() => setWizardStep(3)}
                  className="flex items-center gap-2 px-6 py-3 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl shadow-md transition"
                >
                  Proceed to Upload & Stage <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}

          {/* STEP 3: UPLOAD & STAGE FILE */}
          {wizardStep === 3 && (
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
              <div>
                <h2 className="text-lg font-bold text-slate-900">
                  Step 3: Upload {importType === 'STUDENT' ? 'Student' : 'Staff'} Data File
                </h2>
                <p className="text-xs text-slate-500 mt-1">
                  Upload your completed template file. The system will securely stage and inspect the file structure before validation.
                </p>
              </div>

              {/* Drag and drop dropzone */}
              <div
                onClick={() => fileInputRef.current?.click()}
                className="border-2 border-dashed border-slate-300 hover:border-blue-900/60 bg-slate-50 hover:bg-blue-50/20 rounded-3xl p-10 text-center cursor-pointer transition space-y-4"
              >
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileChange}
                  accept=".xlsx,.csv"
                  className="hidden"
                />

                <div className="w-16 h-16 bg-blue-100 text-blue-900 rounded-2xl flex items-center justify-center mx-auto shadow-sm">
                  <UploadCloud className="w-8 h-8" />
                </div>

                <div className="space-y-1">
                  <p className="text-sm font-bold text-slate-800">
                    {selectedFile ? selectedFile.name : 'Click to browse or drag and drop your completed file'}
                  </p>
                  <p className="text-xs text-slate-400">
                    Supports Microsoft Excel (.xlsx) and Comma-Separated Values (.csv) up to 20MB
                  </p>
                </div>

                {selectedFile && (
                  <div className="inline-flex items-center gap-3 px-4 py-2 bg-white rounded-xl border border-slate-200 shadow-sm text-xs font-bold text-slate-700">
                    <FileSpreadsheet className="w-4 h-4 text-emerald-600" />
                    <span>{selectedFile.name}</span>
                    <span className="text-[10px] font-semibold text-slate-400">({(selectedFile.size / 1024).toFixed(1)} KB)</span>
                    <button
                      onClick={(e) => { e.stopPropagation(); setSelectedFile(null); }}
                      className="ml-2 text-rose-500 hover:text-rose-700"
                    >
                      ✕
                    </button>
                  </div>
                )}
              </div>

              <div className="flex items-center justify-between pt-4">
                <button
                  onClick={() => setWizardStep(2)}
                  className="px-5 py-2.5 text-slate-600 hover:text-slate-800 text-xs font-bold transition"
                >
                  ← Back to Templates
                </button>
                <button
                  disabled={!selectedFile || isUploading}
                  onClick={handleUploadFile}
                  className="flex items-center gap-2 px-6 py-3 bg-blue-900 hover:bg-blue-800 disabled:bg-slate-300 text-white text-xs font-bold rounded-xl shadow-md transition"
                >
                  {isUploading ? (
                    <>
                      <RefreshCw className="w-4 h-4 animate-spin" /> Staging File...
                    </>
                  ) : (
                    <>
                      Upload & Stage File <ArrowRight className="w-4 h-4" />
                    </>
                  )}
                </button>
              </div>
            </div>
          )}

          {/* STEP 4: REAL-TIME VALIDATION & PREVIEW TABLE */}
          {wizardStep === 4 && (
            <div className="bg-white p-6 md:p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
              {/* Header with Job metadata & action buttons */}
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-100">
                <div>
                  <div className="flex items-center gap-2">
                    <h2 className="text-lg font-bold text-slate-900">Step 4: Validation Engine & Preview</h2>
                    <span className="px-2.5 py-0.5 bg-blue-100 text-blue-900 rounded-full text-[10px] font-black uppercase">
                      Job: {currentJob?.jobId}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 mt-1">
                    File: <span className="font-semibold text-slate-700">{currentJob?.originalFilename}</span> • Format: {currentJob?.fileFormat} • Size: {currentJob ? (currentJob.fileSizeBytes / 1024).toFixed(1) : 0} KB
                  </p>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => currentJob && runValidation(currentJob.jobId)}
                    disabled={isValidating}
                    className="flex items-center gap-1.5 px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition"
                  >
                    <RefreshCw className={`w-3.5 h-3.5 ${isValidating ? 'animate-spin' : ''}`} /> Re-Validate
                  </button>
                  <button
                    onClick={() => currentJob && handleDownloadReport(currentJob.jobId)}
                    className="flex items-center gap-1.5 px-3.5 py-2 bg-slate-800 hover:bg-slate-900 text-white text-xs font-bold rounded-xl shadow transition"
                  >
                    <Download className="w-3.5 h-3.5" /> Export Errors CSV
                  </button>
                  <button
                    onClick={handleCancelJob}
                    className="flex items-center gap-1.5 px-3.5 py-2 bg-rose-50 hover:bg-rose-100 text-rose-700 text-xs font-bold rounded-xl transition"
                  >
                    Cancel Staged Job
                  </button>
                </div>
              </div>

              {/* Validation Summary Bar */}
              {currentJob && (
                <div className="grid grid-cols-2 md:grid-cols-5 gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-200">
                  <div className="text-center p-2">
                    <p className="text-[10px] font-bold text-slate-400 uppercase">Total Rows</p>
                    <p className="text-xl font-black text-slate-800">{currentJob.totalRows}</p>
                  </div>
                  <div className="text-center p-2 bg-emerald-50/80 rounded-xl border border-emerald-200/50">
                    <p className="text-[10px] font-bold text-emerald-700 uppercase">Valid Rows</p>
                    <p className="text-xl font-black text-emerald-700">{currentJob.validRows}</p>
                  </div>
                  <div className="text-center p-2 bg-amber-50/80 rounded-xl border border-amber-200/50">
                    <p className="text-[10px] font-bold text-amber-700 uppercase">Warnings</p>
                    <p className="text-xl font-black text-amber-700">{currentJob.warningRows}</p>
                  </div>
                  <div className="text-center p-2 bg-rose-50/80 rounded-xl border border-rose-200/50">
                    <p className="text-[10px] font-bold text-rose-700 uppercase">Invalid Rows</p>
                    <p className="text-xl font-black text-rose-700">{currentJob.invalidRows}</p>
                  </div>
                  <div className="text-center p-2 bg-purple-50/80 rounded-xl border border-purple-200/50">
                    <p className="text-[10px] font-bold text-purple-700 uppercase">Duplicate Rows</p>
                    <p className="text-xl font-black text-purple-700">{currentJob.duplicateRows}</p>
                  </div>
                </div>
              )}

              {/* Filter Pills & Search bar */}
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
                <div className="flex flex-wrap items-center gap-1.5">
                  <span className="text-xs font-bold text-slate-500 mr-1">Filter Rows:</span>
                  {[
                    { key: 'ALL', label: 'All Records' },
                    { key: 'VALID', label: 'Valid Only' },
                    { key: 'WARNING', label: 'Warnings' },
                    { key: 'INVALID', label: 'Errors Only' },
                    { key: 'DUPLICATE', label: 'Duplicates' },
                  ].map((f) => (
                    <button
                      key={f.key}
                      onClick={() => handleFilterChange(f.key)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-bold transition ${
                        statusFilter === f.key
                          ? 'bg-blue-900 text-white shadow'
                          : 'bg-slate-100 hover:bg-slate-200 text-slate-600'
                      }`}
                    >
                      {f.label}
                    </button>
                  ))}
                </div>

                <div className="relative w-full md:w-64">
                  <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
                  <input
                    type="text"
                    placeholder="Search preview records..."
                    value={searchQuery}
                    onChange={handleSearchChange}
                    className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-blue-900 focus:outline-none"
                  />
                </div>
              </div>

              {/* Preview Table */}
              <div className="overflow-x-auto border border-slate-200 rounded-2xl">
                <table className="w-full text-left text-xs text-slate-600">
                  <thead className="bg-slate-50 text-[11px] font-bold text-slate-500 uppercase border-b border-slate-200">
                    <tr>
                      <th className="py-3 px-4">Row</th>
                      <th className="py-3 px-4">Identifier</th>
                      <th className="py-3 px-4">Status</th>
                      <th className="py-3 px-4">Key Preview Details</th>
                      <th className="py-3 px-4">Validation Diagnosis</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {currentJob?.rows && currentJob.rows.length > 0 ? (
                      currentJob.rows.map((row) => {
                        const statusColors: Record<string, string> = {
                          VALID: 'bg-emerald-100 text-emerald-800 border-emerald-300',
                          WARNING: 'bg-amber-100 text-amber-800 border-amber-300',
                          INVALID: 'bg-rose-100 text-rose-800 border-rose-300',
                          DUPLICATE: 'bg-purple-100 text-purple-800 border-purple-300',
                        };

                        return (
                          <tr key={row.id} className="hover:bg-slate-50/80 transition">
                            <td className="py-3 px-4 font-bold text-slate-900">#{row.rowNumber}</td>
                            <td className="py-3 px-4 font-mono font-bold text-blue-900">
                              {row.entityIdentifier}
                            </td>
                            <td className="py-3 px-4">
                              <span className={`px-2 py-0.5 rounded-full text-[10px] font-extrabold border ${statusColors[row.status] || 'bg-slate-100 text-slate-700'}`}>
                                {row.status}
                              </span>
                            </td>
                            <td className="py-3 px-4">
                              <div className="text-xs">
                                {importType === 'STUDENT' ? (
                                  <>
                                    <span className="font-bold text-slate-800">
                                      {row.rowData.first_name} {row.rowData.last_name}
                                    </span>
                                    <span className="text-slate-400"> • {row.rowData.class_name} ({row.rowData.section_name})</span>
                                    <div className="text-[11px] text-slate-500">
                                      Parent: {row.rowData.guardian_name} ({row.rowData.guardian_phone})
                                    </div>
                                  </>
                                ) : (
                                  <>
                                    <span className="font-bold text-slate-800">{row.rowData.full_name}</span>
                                    <span className="text-slate-400"> • {row.rowData.designation} ({row.rowData.department})</span>
                                    <div className="text-[11px] text-slate-500">
                                      {row.rowData.official_email} • {row.rowData.category}
                                    </div>
                                  </>
                                )}
                              </div>
                            </td>
                            <td className="py-3 px-4 max-w-xs">
                              {row.errorMessage ? (
                                <div className="text-[11px] text-rose-700 bg-rose-50 p-2 rounded-lg border border-rose-200">
                                  {row.errorMessage}
                                </div>
                              ) : row.status === 'WARNING' ? (
                                <div className="text-[11px] text-amber-700 bg-amber-50 p-2 rounded-lg border border-amber-200">
                                  Optional field formatting notice
                                </div>
                              ) : (
                                <span className="text-[11px] text-emerald-600 font-semibold flex items-center gap-1">
                                  <Check className="w-3.5 h-3.5" /> Passed all schema constraints
                                </span>
                              )}
                            </td>
                          </tr>
                        );
                      })
                    ) : (
                      <tr>
                        <td colSpan={5} className="py-8 text-center text-slate-400">
                          No records match the selected filter criteria.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {/* Navigation to Execution Mode */}
              <div className="flex items-center justify-between pt-4">
                <button
                  onClick={() => setWizardStep(3)}
                  className="px-5 py-2.5 text-slate-600 hover:text-slate-800 text-xs font-bold transition"
                >
                  ← Upload Different File
                </button>

                <button
                  onClick={() => setWizardStep(5)}
                  className="flex items-center gap-2 px-6 py-3 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl shadow-md transition"
                >
                  Proceed to Execution Settings <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}

          {/* STEP 5: EXECUTION MODE SELECTION & CONFIRMATION */}
          {wizardStep === 5 && (
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
              <div>
                <h2 className="text-lg font-bold text-slate-900">Step 5: Select Import Execution Mode</h2>
                <p className="text-xs text-slate-500 mt-1">
                  Choose how the system should handle rows with errors and duplicates when committing to the database.
                </p>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                {/* Mode A: STRICT */}
                <div
                  onClick={() => setImportMode('STRICT')}
                  className={`p-6 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                    importMode === 'STRICT'
                      ? 'border-blue-900 bg-blue-50/50 shadow-md ring-2 ring-blue-900/10'
                      : 'border-slate-200 hover:border-slate-300 bg-white'
                  }`}
                >
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="px-2.5 py-0.5 bg-blue-100 text-blue-900 rounded-full text-[10px] font-black uppercase">
                        Recommended
                      </span>
                      {importMode === 'STRICT' && <CheckCircle2 className="w-5 h-5 text-blue-900" />}
                    </div>
                    <h3 className="text-base font-extrabold text-slate-900">Strict Mode (All or Nothing)</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Atomically commits data only if 100% of rows are valid with zero errors and zero duplicates. If a single row fails validation, the entire batch is aborted and the database remains completely untouched.
                    </p>
                    {currentJob && (currentJob.invalidRows > 0 || currentJob.duplicateRows > 0) && (
                      <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl text-rose-800 text-xs font-medium">
                        ⚠️ Notice: This batch currently has {currentJob.invalidRows} invalid and {currentJob.duplicateRows} duplicate rows. Strict mode will reject this import until corrected.
                      </div>
                    )}
                  </div>
                </div>

                {/* Mode B: VALID_ONLY */}
                <div
                  onClick={() => setImportMode('VALID_ONLY')}
                  className={`p-6 rounded-2xl border-2 cursor-pointer transition flex flex-col justify-between ${
                    importMode === 'VALID_ONLY'
                      ? 'border-indigo-900 bg-indigo-50/50 shadow-md ring-2 ring-indigo-900/10'
                      : 'border-slate-200 hover:border-slate-300 bg-white'
                  }`}
                >
                  <div className="space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="px-2.5 py-0.5 bg-indigo-100 text-indigo-900 rounded-full text-[10px] font-black uppercase">
                        Partial Intake
                      </span>
                      {importMode === 'VALID_ONLY' && <CheckCircle2 className="w-5 h-5 text-indigo-900" />}
                    </div>
                    <h3 className="text-base font-extrabold text-slate-900">Import Valid Only (Skip Errors)</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Imports all {currentJob?.validRows || 0} valid rows into the database immediately. Invalid ({currentJob?.invalidRows || 0}) and duplicate ({currentJob?.duplicateRows || 0}) rows will be skipped and logged in a downloadable execution receipt.
                    </p>
                  </div>
                </div>
              </div>

              {/* Execution Notes */}
              <div className="space-y-1.5">
                <label className="text-xs font-bold text-slate-700">Audit & Archival Notes (Optional)</label>
                <textarea
                  rows={3}
                  value={importNotes}
                  onChange={(e) => setImportNotes(e.target.value)}
                  placeholder="e.g. Session 2026-2027 Class 9 intake authorized by Academic Principal Huma Arif."
                  className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-blue-900 focus:outline-none"
                />
              </div>

              <div className="flex items-center justify-between pt-4 border-t border-slate-100">
                <button
                  onClick={() => setWizardStep(4)}
                  className="px-5 py-2.5 text-slate-600 hover:text-slate-800 text-xs font-bold transition"
                >
                  ← Back to Preview
                </button>

                <button
                  onClick={() => setShowConfirmModal(true)}
                  className="flex items-center gap-2 px-8 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow-lg transition"
                >
                  Commit Import to Database <CheckCircle2 className="w-4 h-4" />
                </button>
              </div>

              {/* Confirmation Modal */}
              {showConfirmModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4 animate-fadeIn">
                  <div className="bg-white rounded-3xl p-6 md:p-8 max-w-lg w-full space-y-6 shadow-2xl border border-slate-100">
                    <div className="flex items-start gap-4">
                      <div className="w-12 h-12 bg-emerald-100 text-emerald-700 rounded-2xl flex items-center justify-center shrink-0">
                        <CheckCircle2 className="w-6 h-6" />
                      </div>
                      <div>
                        <h3 className="text-lg font-bold text-slate-900">Authorize Bulk Data Persistence?</h3>
                        <p className="text-xs text-slate-500 mt-1">
                          You are about to commit records into the live Bluebells database using <strong>{importMode}</strong> mode.
                        </p>
                      </div>
                    </div>

                    <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 text-xs space-y-2">
                      <div className="flex justify-between">
                        <span className="text-slate-500">Target Type:</span>
                        <span className="font-bold text-slate-800">{importType} Directory</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-slate-500">Execution Mode:</span>
                        <span className="font-bold text-blue-900">{importMode}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-slate-500">Valid Records:</span>
                        <span className="font-bold text-emerald-700">{currentJob?.validRows}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-slate-500">Skipped Records:</span>
                        <span className="font-bold text-rose-700">
                          {importMode === 'VALID_ONLY' ? (currentJob?.invalidRows || 0) + (currentJob?.duplicateRows || 0) : 0}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center justify-end gap-3 pt-2">
                      <button
                        onClick={() => setShowConfirmModal(false)}
                        disabled={isExecuting}
                        className="px-4 py-2.5 text-slate-600 hover:text-slate-800 text-xs font-bold transition"
                      >
                        Cancel
                      </button>
                      <button
                        onClick={handleConfirmImport}
                        disabled={isExecuting}
                        className="flex items-center gap-2 px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-xl shadow transition"
                      >
                        {isExecuting ? <RefreshCw className="w-4 h-4 animate-spin" /> : 'Confirm & Execute'}
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* STEP 6: RESULT RECEIPT & QUICK ACTIONS */}
          {wizardStep === 6 && executionResult && (
            <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm space-y-8 animate-fadeIn">
              <div className="text-center max-w-xl mx-auto space-y-3">
                <div className="w-16 h-16 bg-emerald-100 text-emerald-700 rounded-3xl flex items-center justify-center mx-auto shadow-md">
                  <CheckCircle2 className="w-9 h-9" />
                </div>
                <h2 className="text-2xl font-black text-slate-900">Import Batch Executed Successfully!</h2>
                <p className="text-xs text-slate-500">
                  Data records have been committed to the live database with complete referential integrity.
                </p>
              </div>

              {/* Receipt Summary Grid */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-3xl mx-auto">
                <div className="p-4 bg-emerald-50/70 border border-emerald-200 rounded-2xl text-center">
                  <p className="text-[10px] font-bold text-emerald-600 uppercase">Records Imported</p>
                  <p className="text-3xl font-black text-emerald-700 mt-1">{executionResult.importedRows}</p>
                </div>

                <div className="p-4 bg-amber-50/70 border border-amber-200 rounded-2xl text-center">
                  <p className="text-[10px] font-bold text-amber-600 uppercase">Records Skipped</p>
                  <p className="text-3xl font-black text-amber-700 mt-1">{executionResult.skippedRows}</p>
                </div>

                <div className="p-4 bg-blue-50/70 border border-blue-200 rounded-2xl text-center">
                  <p className="text-[10px] font-bold text-blue-600 uppercase">Total Processed</p>
                  <p className="text-3xl font-black text-blue-900 mt-1">{executionResult.totalRows}</p>
                </div>

                <div className="p-4 bg-slate-50 border border-slate-200 rounded-2xl text-center">
                  <p className="text-[10px] font-bold text-slate-400 uppercase">Execution Mode</p>
                  <p className="text-lg font-black text-slate-800 mt-2">{executionResult.importMode}</p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-4 border-t border-slate-100">
                <button
                  onClick={() => handleDownloadReport(executionResult.jobId)}
                  className="flex items-center gap-2 px-5 py-3 bg-slate-900 hover:bg-black text-white text-xs font-bold rounded-xl shadow transition"
                >
                  <Download className="w-4 h-4" /> Download Execution Summary Report
                </button>

                {importType === 'STUDENT' ? (
                  <Link
                    to="/students"
                    className="flex items-center gap-2 px-5 py-3 bg-blue-900 hover:bg-blue-800 text-white text-xs font-bold rounded-xl shadow transition"
                  >
                    <Users className="w-4 h-4" /> Go to Student Directory
                  </Link>
                ) : (
                  <Link
                    to="/staff"
                    className="flex items-center gap-2 px-5 py-3 bg-indigo-900 hover:bg-indigo-800 text-white text-xs font-bold rounded-xl shadow transition"
                  >
                    <UserCheck className="w-4 h-4" /> Go to Staff Directory
                  </Link>
                )}

                <button
                  onClick={handleStartNewImport}
                  className="flex items-center gap-2 px-5 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition"
                >
                  <RefreshCw className="w-4 h-4" /> Start New Batch Import
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: IMPORT HISTORY & AUDIT LOGS */}
      {activeTab === 'history' && (
        <div className="bg-white p-6 md:p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-100">
            <div>
              <h2 className="text-lg font-bold text-slate-900">Import Job History & Regulatory Audit Trail</h2>
              <p className="text-xs text-slate-500 mt-1">Complete historical ledger of uploaded batches, row validations, and persistence audits.</p>
            </div>
            <button
              onClick={fetchHistory}
              disabled={isLoadingHistory}
              className="flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isLoadingHistory ? 'animate-spin' : ''}`} /> Refresh Log
            </button>
          </div>

          <div className="overflow-x-auto border border-slate-200 rounded-2xl">
            <table className="w-full text-left text-xs text-slate-600">
              <thead className="bg-slate-50 text-[11px] font-bold text-slate-500 uppercase border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4">Job ID</th>
                  <th className="py-3 px-4">Type</th>
                  <th className="py-3 px-4">Filename</th>
                  <th className="py-3 px-4">Uploaded By</th>
                  <th className="py-3 px-4">Execution Breakdown</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {historyJobs.length > 0 ? (
                  historyJobs.map((job) => {
                    const statusBadgeColors: Record<string, string> = {
                      COMPLETED: 'bg-emerald-100 text-emerald-800',
                      COMPLETED_WITH_ERRORS: 'bg-amber-100 text-amber-800',
                      VALIDATED: 'bg-blue-100 text-blue-800',
                      FAILED: 'bg-rose-100 text-rose-800',
                      CANCELLED: 'bg-slate-100 text-slate-600',
                    };

                    return (
                      <tr key={job.jobId} className="hover:bg-slate-50/80 transition">
                        <td className="py-3 px-4 font-mono font-bold text-blue-900">{job.jobId}</td>
                        <td className="py-3 px-4">
                          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-700 uppercase">
                            {job.importType}
                          </span>
                        </td>
                        <td className="py-3 px-4 font-medium text-slate-800">{job.originalFilename}</td>
                        <td className="py-3 px-4">{job.uploadedBy}</td>
                        <td className="py-3 px-4">
                          <span className="text-emerald-700 font-bold">{job.importedRows} imported</span>
                          {job.skippedRows > 0 && <span className="text-rose-600 ml-1.5">• {job.skippedRows} skipped</span>}
                          <span className="text-slate-400 ml-1.5 font-normal">(Total {job.totalRows})</span>
                        </td>
                        <td className="py-3 px-4">
                          <span className={`px-2 py-0.5 rounded-full text-[10px] font-extrabold ${statusBadgeColors[job.status] || 'bg-slate-100 text-slate-700'}`}>
                            {job.status}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-slate-400 text-[11px]">
                          {new Date(job.createdAt).toLocaleString()}
                        </td>
                        <td className="py-3 px-4 text-right">
                          <button
                            onClick={() => handleDownloadReport(job.jobId)}
                            className="inline-flex items-center gap-1 px-3 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition"
                          >
                            <Download className="w-3.5 h-3.5" /> CSV Report
                          </button>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan={8} className="py-8 text-center text-slate-400">
                      No import job records found in history.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 3: LEADERSHIP ACCESS CONTROL (Super Admin Only) */}
      {activeTab === 'permissions' && isSuperAdmin && (
        <div className="bg-white p-6 md:p-8 rounded-3xl border border-slate-200 shadow-sm space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-100">
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-lg font-bold text-slate-900">Leadership Access & Bulk Import Permissions</h2>
                <span className="px-2.5 py-0.5 bg-purple-100 text-purple-900 rounded-full text-[10px] font-black uppercase">
                  Super Admin Exclusive
                </span>
              </div>
              <p className="text-xs text-slate-500 mt-1">
                Grant or revoke bulk data import capabilities for Academic Principal (Huma Arif), Vice Principal (Hana Khan), and operational staff.
              </p>
            </div>
            <button
              onClick={fetchPermissions}
              disabled={isLoadingPermissions}
              className="flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${isLoadingPermissions ? 'animate-spin' : ''}`} /> Refresh Permissions
            </button>
          </div>

          <div className="space-y-4">
            {eligibleUsers.map((u) => {
              const isUserSuperAdmin = u.roles.includes('ROLE_SUPER_ADMIN');

              return (
                <div key={u.userId} className="p-5 bg-slate-50 rounded-2xl border border-slate-200 space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-200/60 pb-3">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-extrabold text-sm text-slate-900">{u.fullName}</span>
                        <span className="text-xs text-slate-500">(@{u.username})</span>
                        {isUserSuperAdmin && (
                          <span className="px-2 py-0.5 bg-blue-100 text-blue-900 text-[10px] font-black rounded-full uppercase">
                            Super Admin
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-slate-500 mt-0.5">{u.email} • Roles: {u.roles.join(', ')}</p>
                    </div>

                    <div>
                      {isUserSuperAdmin ? (
                        <span className="text-xs font-bold text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full border border-emerald-200">
                          Unrestricted Master Access
                        </span>
                      ) : (
                        <span className="text-xs font-bold text-blue-900 bg-blue-50 px-3 py-1 rounded-full border border-blue-200">
                          Delegated School Leader
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Permission Toggles */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                    {ALL_IMPORT_PERMISSIONS.map((perm) => {
                      const hasPerm = isUserSuperAdmin || u.importPermissions.includes(perm.code);

                      return (
                        <button
                          key={perm.code}
                          disabled={isUserSuperAdmin}
                          onClick={() => handleTogglePermission(u, perm.code)}
                          className={`p-2.5 rounded-xl text-left border transition flex items-center justify-between text-xs ${
                            hasPerm
                              ? 'bg-emerald-50 border-emerald-300 text-emerald-900 font-bold'
                              : 'bg-white border-slate-200 text-slate-500 hover:border-slate-300'
                          } ${isUserSuperAdmin ? 'cursor-default opacity-85' : 'cursor-pointer'}`}
                        >
                          <span className="text-[11px] leading-tight">{perm.label}</span>
                          {hasPerm ? (
                            <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 ml-1.5" />
                          ) : (
                            <div className="w-4 h-4 rounded-full border border-slate-300 shrink-0 ml-1.5" />
                          )}
                        </button>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

export default BulkDataImportPage;
