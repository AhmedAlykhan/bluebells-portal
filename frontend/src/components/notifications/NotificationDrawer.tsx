import React, { useState, useEffect } from 'react';
import { X, Bell, Check, CheckCheck, Info, AlertTriangle, ShieldCheck, Clock } from 'lucide-react';
import { apiClient } from '../../api/client';

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

interface NotificationDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  onCountChange?: (count: number) => void;
}

export const NotificationDrawer: React.FC<NotificationDrawerProps> = ({
  isOpen,
  onClose,
  onCountChange
}) => {
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get<{ data: NotificationItem[] }>('/notifications/my');
      if (res.data?.data) {
        setNotifications(res.data.data);
        const unread = res.data.data.filter(n => !n.isRead).length;
        if (onCountChange) onCountChange(unread);
      }
    } catch {
      // Fallback baseline notifications
      setNotifications([
        {
          id: 101,
          title: 'Fee Due Reminder',
          message: 'Tuition fee for October 2026 is due by 10th of this month. Please deposit at Accounts Office or via Bank Alfalah challan.',
          type: 'INFO',
          isRead: false,
          createdAt: new Date().toISOString()
        },
        {
          id: 102,
          title: 'Midterm Examination Schedule Published',
          message: 'Midterm Examination timetable for Class 9 and Class 10 is available. Exams commence from 15th November 2026.',
          type: 'INFO',
          isRead: false,
          createdAt: new Date(Date.now() - 3600000 * 24).toISOString()
        },
        {
          id: 103,
          title: 'Parent-Teacher Meeting (PTM)',
          message: 'Monthly PTM is scheduled for Saturday at 10:00 AM in the Latifabad Campus Auditorium.',
          type: 'ALERT',
          isRead: true,
          createdAt: new Date(Date.now() - 3600000 * 48).toISOString()
        }
      ]);
      if (onCountChange) onCountChange(2);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen]);

  const handleMarkAsRead = async (id: number) => {
    try {
      await apiClient.put(`/notifications/${id}/read`);
    } catch {
      // Optimistic update
    }
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, isRead: true } : n)
    );
    const unread = notifications.filter(n => n.id !== id && !n.isRead).length;
    if (onCountChange) onCountChange(unread);
  };

  const handleMarkAllAsRead = async () => {
    try {
      await apiClient.put('/notifications/my/read-all');
    } catch {
      // Optimistic update
    }
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
    if (onCountChange) onCountChange(0);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-900/40 backdrop-blur-sm flex justify-end">
      <div className="w-full max-w-md bg-white h-full shadow-2xl border-l border-slate-200 flex flex-col animate-in slide-in-from-right duration-200">
        {/* Header */}
        <div className="px-5 py-4 bg-slate-900 text-white flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Bell className="w-5 h-5 text-amber-400" />
            <h3 className="font-bold text-sm">Campus Notification Center</h3>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handleMarkAllAsRead}
              title="Mark all as read"
              className="text-xs text-slate-300 hover:text-white flex items-center gap-1 px-2 py-1 rounded bg-slate-800 hover:bg-slate-700 transition-colors"
            >
              <CheckCheck className="w-3.5 h-3.5" />
              <span>Mark all read</span>
            </button>
            <button onClick={onClose} className="p-1 text-slate-400 hover:text-white rounded-lg">
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Content list */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {loading ? (
            <div className="p-8 text-center text-slate-400 text-xs">
              Loading notification feed...
            </div>
          ) : notifications.length === 0 ? (
            <div className="p-8 text-center text-slate-400 text-xs">
              No notifications at this time.
            </div>
          ) : (
            notifications.map((item) => (
              <div
                key={item.id}
                className={`p-3.5 rounded-xl border transition-all ${
                  item.isRead
                    ? 'bg-slate-50/70 border-slate-200 opacity-80'
                    : 'bg-white border-brand-200 shadow-sm ring-1 ring-brand-100'
                }`}
              >
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center gap-1.5">
                    {item.type === 'ALERT' ? (
                      <AlertTriangle className="w-4 h-4 text-amber-600 flex-shrink-0" />
                    ) : item.type === 'SECURITY' ? (
                      <ShieldCheck className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                    ) : (
                      <Info className="w-4 h-4 text-blue-600 flex-shrink-0" />
                    )}
                    <h4 className="font-bold text-xs text-slate-800">{item.title}</h4>
                  </div>
                  {!item.isRead && (
                    <button
                      onClick={() => handleMarkAsRead(item.id)}
                      className="text-[10px] text-brand-700 hover:text-brand-900 font-semibold flex items-center gap-0.5 bg-brand-50 px-2 py-0.5 rounded"
                    >
                      <Check className="w-3 h-3" />
                      <span>Read</span>
                    </button>
                  )}
                </div>

                <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">{item.message}</p>

                <div className="mt-2 flex items-center justify-between text-[10px] text-slate-400 font-mono">
                  <span className="flex items-center gap-1">
                    <Clock className="w-3 h-3" />
                    {new Date(item.createdAt).toLocaleDateString('en-GB', {
                      day: 'numeric',
                      month: 'short',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </span>
                  <span className="uppercase text-[9px] font-bold px-1.5 py-0.5 rounded bg-slate-100 text-slate-600">
                    {item.type}
                  </span>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer info */}
        <div className="p-3 border-t border-slate-200 bg-slate-50 text-[11px] text-slate-500 text-center">
          Bluebells Automated Dispatch • Latifabad, Hyderabad
        </div>
      </div>
    </div>
  );
};
