import React, { useState, useEffect, useRef } from 'react';
import { 
  Bot, 
  X, 
  Send, 
  ShieldAlert, 
  ShieldCheck, 
  Clock, 
  CreditCard, 
  GraduationCap, 
  ChevronRight,
  RefreshCw
} from 'lucide-react';
import { apiClient } from '../../api/client';
import { useNavigate } from 'react-router-dom';

interface AssistantQueryResponse {
  answer: string;
  category: string;
  confidence: number;
  sourceDocument: string;
  suggestedFollowUps?: string[];
  suggestedActions?: { label: string; path: string }[];
  automatedDisclaimer: boolean;
  disclaimer: string;
}

interface Message {
  id: string;
  sender: 'user' | 'assistant';
  text: string;
  timestamp: string;
  source?: string;
  suggestedActions?: { label: string; path: string }[];
  isGuardrail?: boolean;
}

interface FaqTopic {
  category: string;
  title: string;
  sampleQuestions: string[];
}

export const SchoolAssistantWidget: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [inputQuery, setInputQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome',
      sender: 'assistant',
      text: 'Assalam-o-Alaikum! Welcome to Bluebells Schooling System Virtual Assistant. How may I assist you with school timings, fee payment vouchers, admissions, or portal navigation today?',
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      source: 'Bluebells Knowledge Base'
    }
  ]);
  const [faqTopics, setFaqTopics] = useState<FaqTopic[]>([]);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (isOpen && faqTopics.length === 0) {
      apiClient.get<{ success: boolean; data: FaqTopic[] }>('/assistant/faq-topics')
        .then(res => {
          if (res.data?.data) {
            setFaqTopics(res.data.data);
          }
        })
        .catch(err => console.warn('Could not load FAQ categories:', err));
    }
  }, [isOpen]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const handleSend = async (queryText?: string) => {
    const textToSend = queryText || inputQuery;
    if (!textToSend.trim() || loading) return;

    const userMsg: Message = {
      id: Date.now().toString(),
      sender: 'user',
      text: textToSend,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMsg]);
    if (!queryText) setInputQuery('');
    setLoading(true);

    try {
      const res = await apiClient.post<{ success: boolean; data: AssistantQueryResponse }>('/assistant/query', {
        question: textToSend
      });

      const data = res.data?.data;
      if (data) {
        const assistantMsg: Message = {
          id: (Date.now() + 1).toString(),
          sender: 'assistant',
          text: data.answer,
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          source: data.sourceDocument,
          suggestedActions: data.suggestedActions,
          isGuardrail: data.category === 'SECURITY_GUARDRAIL'
        };
        setMessages(prev => [...prev, assistantMsg]);
      }
    } catch (err: any) {
      setMessages(prev => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          sender: 'assistant',
          text: 'We are currently unable to connect to the school assistant service. Please check your network connection or contact the campus administration at Latifabad, Hyderabad (+92 316 9681932).',
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 print:hidden">
      {!isOpen ? (
        <button
          onClick={() => setIsOpen(true)}
          className="flex items-center gap-3 bg-[#123B70] hover:bg-[#0c284d] text-white px-4 py-3.5 rounded-full shadow-2xl transition-all transform hover:scale-105 border-2 border-amber-400 group focus:outline-none focus:ring-4 focus:ring-brand-300"
          aria-label="Open Bluebells Virtual Assistant"
        >
          <div className="relative">
            <Bot className="w-6 h-6 text-amber-300 group-hover:rotate-12 transition-transform" />
            <span className="absolute -top-1 -right-1 w-2.5 h-2.5 bg-emerald-400 rounded-full border-2 border-[#123B70] animate-pulse"></span>
          </div>
          <span className="font-bold text-sm tracking-wide">Ask Bluebells</span>
        </button>
      ) : (
        <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-[95vw] sm:w-[420px] h-[580px] flex flex-col overflow-hidden animate-in fade-in slide-in-from-bottom-6 duration-200">
          {/* Header */}
          <div className="bg-gradient-to-r from-[#123B70] to-[#1c5396] text-white p-4 flex items-center justify-between border-b border-brand-800">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-full bg-white/10 flex items-center justify-center border border-amber-400/40">
                <Bot className="w-5 h-5 text-amber-300" />
              </div>
              <div>
                <h3 className="font-bold text-sm tracking-wide text-white">Bluebells Assistant</h3>
                <div className="flex items-center gap-1.5 text-[11px] text-emerald-300 font-medium">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
                  <span>Grounded in Official School Policy</span>
                </div>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="p-1.5 rounded-lg bg-white/10 hover:bg-white/20 text-white transition-colors"
              aria-label="Close Assistant"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Quick FAQ Chips */}
          <div className="bg-slate-50 border-b border-slate-200 px-3 py-2 flex items-center gap-2 overflow-x-auto text-xs scrollbar-none">
            <button
              onClick={() => handleSend("What are the official school timings?")}
              className="flex-shrink-0 flex items-center gap-1.5 bg-white border border-slate-200 px-2.5 py-1 rounded-full text-slate-700 hover:border-brand-500 hover:text-brand-800 font-medium transition-colors shadow-sm"
            >
              <Clock className="w-3 h-3 text-brand-600" />
              <span>Timings</span>
            </button>
            <button
              onClick={() => handleSend("How can parents pay school fees?")}
              className="flex-shrink-0 flex items-center gap-1.5 bg-white border border-slate-200 px-2.5 py-1 rounded-full text-slate-700 hover:border-brand-500 hover:text-brand-800 font-medium transition-colors shadow-sm"
            >
              <CreditCard className="w-3 h-3 text-emerald-600" />
              <span>Fee Payment</span>
            </button>
            <button
              onClick={() => handleSend("Who is the Principal and Vice Principal?")}
              className="flex-shrink-0 flex items-center gap-1.5 bg-white border border-slate-200 px-2.5 py-1 rounded-full text-slate-700 hover:border-brand-500 hover:text-brand-800 font-medium transition-colors shadow-sm"
            >
              <GraduationCap className="w-3 h-3 text-purple-600" />
              <span>Leadership</span>
            </button>
          </div>

          {/* Conversation Thread */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50/50">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.sender === 'user' ? 'items-end' : 'items-start'}`}
              >
                <div
                  className={`max-w-[85%] rounded-2xl px-4 py-2.5 text-xs sm:text-sm leading-relaxed shadow-sm ${
                    msg.sender === 'user'
                      ? 'bg-[#123B70] text-white rounded-br-sm'
                      : msg.isGuardrail
                      ? 'bg-amber-50 text-amber-950 border border-amber-300 rounded-bl-sm'
                      : 'bg-white text-slate-800 border border-slate-200 rounded-bl-sm'
                  }`}
                >
                  {msg.isGuardrail && (
                    <div className="flex items-center gap-1.5 text-amber-800 font-bold mb-1.5 text-xs">
                      <ShieldAlert className="w-4 h-4 text-amber-600" />
                      <span>Security & Integrity Guardrail</span>
                    </div>
                  )}
                  <div className="whitespace-pre-line">{msg.text}</div>

                  {msg.source && (
                    <div className="mt-2 pt-1.5 border-t border-slate-200/60 flex items-center justify-between text-[10px] text-slate-500">
                      <span className="font-semibold text-slate-600">Source: {msg.source}</span>
                      <span>{msg.timestamp}</span>
                    </div>
                  )}

                  {msg.suggestedActions && msg.suggestedActions.length > 0 && (
                    <div className="mt-2.5 pt-2 border-t border-slate-200 space-y-1.5">
                      <p className="text-[10px] font-bold uppercase tracking-wider text-slate-500">Suggested Action:</p>
                      {msg.suggestedActions.map((action, idx) => (
                        <button
                          key={idx}
                          onClick={() => {
                            setIsOpen(false);
                            navigate(action.path);
                          }}
                          className="w-full flex items-center justify-between bg-brand-50 hover:bg-brand-100 text-brand-900 border border-brand-200 px-2.5 py-1.5 rounded-lg text-xs font-semibold transition-colors"
                        >
                          <span>{action.label}</span>
                          <ChevronRight className="w-3.5 h-3.5 text-brand-700" />
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {loading && (
              <div className="flex items-center gap-2 text-xs text-slate-500 bg-white border border-slate-200 rounded-xl px-3 py-2 w-max">
                <RefreshCw className="w-3.5 h-3.5 animate-spin text-brand-600" />
                <span>Consulting verified school documentation...</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Disclaimer Banner */}
          <div className="px-3 py-1.5 bg-amber-50/80 border-t border-amber-200 text-[10px] text-amber-900 flex items-center gap-1.5">
            <ShieldCheck className="w-3 h-3 text-amber-700 flex-shrink-0" />
            <span className="truncate">Automated guidance grounded in school policy. Contact Principal for formal decisions.</span>
          </div>

          {/* Input Area */}
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            className="p-3 bg-white border-t border-slate-200 flex items-center gap-2"
          >
            <input
              type="text"
              value={inputQuery}
              onChange={(e) => setInputQuery(e.target.value)}
              placeholder="Ask about timings, fee vouchers, portal..."
              className="flex-1 bg-slate-100 border border-slate-300 rounded-xl px-3.5 py-2 text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:bg-white"
              disabled={loading}
            />
            <button
              type="submit"
              disabled={!inputQuery.trim() || loading}
              className="p-2.5 bg-[#123B70] hover:bg-[#0c284d] text-white rounded-xl disabled:opacity-40 transition-colors shadow-sm"
              aria-label="Send message"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>
      )}
    </div>
  );
};
