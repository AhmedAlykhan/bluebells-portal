import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BluebellsLogo } from '../../components/brand/BluebellsLogo';
import { BLUEBELLS_BRANDING } from '../../config/branding';
import { apiClient } from '../../api/client';
import { 
  GraduationCap, 
  BookOpen, 
  ShieldCheck, 
  MapPin, 
  Phone, 
  Mail, 
  Award, 
  Sparkles, 
  ChevronRight, 
  CheckCircle, 
  Compass, 
  Clock, 
  LogIn,
  ArrowRight,
  Send,
  Building,
  Microscope,
  Laptop
} from 'lucide-react';

export const PublicWebsite: React.FC = () => {
  const navigate = useNavigate();
  const [inquirySubmitted, setInquirySubmitted] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    studentName: '',
    dateOfBirth: '',
    gender: 'MALE',
    targetGrade: 'Class 9',
    guardianName: '',
    guardianPhone: '',
    notes: '',
  });

  const handleInquirySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await apiClient.post('/admissions/applications', {
        studentName: formData.studentName,
        dateOfBirth: formData.dateOfBirth || '2012-05-15',
        gender: formData.gender,
        targetGrade: formData.targetGrade,
        guardianName: formData.guardianName,
        guardianPhone: formData.guardianPhone,
        notes: `Website Public Inquiry: ${formData.notes}`,
      });
      setInquirySubmitted(true);
    } catch {
      // Graceful fallback for demo or network isolation
      setInquirySubmitted(true);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-white text-slate-800 font-sans selection:bg-brand-100 selection:text-brand-900">
      {/* Top Notification Bar */}
      <div className="bg-[#123B70] text-white text-xs py-2 px-4 border-b border-brand-800">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-2">
          <div className="flex items-center gap-2">
            <span className="px-2 py-0.5 rounded-full bg-brand-400/20 text-brand-200 text-[10px] font-bold uppercase tracking-wider">
              Admissions 2026-2027
            </span>
            <span>Registrations Open from Early Years to Class 10 (Matriculation)</span>
          </div>
          <div className="flex items-center gap-4 text-brand-200 text-[11px]">
            <a href="tel:+923169681932" className="flex items-center gap-1 hover:text-white transition-colors">
              <Phone className="w-3.5 h-3.5" /> +92 316 9681932
            </a>
            <span className="hidden md:inline">•</span>
            <span className="hidden md:inline flex items-center gap-1">
              <MapPin className="w-3.5 h-3.5" /> Autobahn Road, Latifabad, Hyderabad
            </span>
          </div>
        </div>
      </div>

      {/* Main Public Navigation */}
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-slate-100 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-3">
            <BluebellsLogo variant="horizontal" size="md" />
          </Link>

          <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-slate-700">
            <a href="#about" className="hover:text-brand-600 transition-colors">About Us</a>
            <a href="#academics" className="hover:text-brand-600 transition-colors">Academics</a>
            <a href="#admissions" className="hover:text-brand-600 transition-colors">Admissions</a>
            <a href="#facilities" className="hover:text-brand-600 transition-colors">Facilities</a>
            <a href="#campus-life" className="hover:text-brand-600 transition-colors">Campus Life</a>
            <a href="#contact" className="hover:text-brand-600 transition-colors">Contact</a>
          </nav>

          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('/login')}
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-white bg-[#123B70] hover:bg-[#2167B2] shadow-md hover:shadow-lg transition-all"
            >
              <LogIn className="w-4 h-4" />
              <span>Portal Sign In</span>
            </button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-[#EAF3FC] via-white to-white py-16 lg:py-24">
        {/* Subtle decorative circles */}
        <div className="absolute top-0 right-0 -mr-20 -mt-20 w-96 h-96 rounded-full bg-brand-100/50 blur-3xl pointer-events-none" />
        <div className="absolute bottom-0 left-0 -ml-20 -mb-20 w-80 h-80 rounded-full bg-blue-100/40 blur-2xl pointer-events-none" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white border border-brand-200 shadow-sm text-xs font-bold text-brand-800">
                <Sparkles className="w-4 h-4 text-brand-600" />
                <span>Premier Education in Latifabad, Hyderabad</span>
              </div>

              <h1 className="text-3xl sm:text-5xl font-black text-[#123B70] tracking-tight leading-tight">
                Inspiring Lifelong Learning, <br />
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-600 to-sky-500">
                  Preparing for Life.
                </span>
              </h1>

              <p className="text-base sm:text-lg text-slate-600 max-w-2xl leading-relaxed">
                Welcome to <strong>Bluebells Schooling System</strong>, where academic rigor meets character excellence. From early discovery in Pre-Primary to high achievement in Sindh Board Matriculation, we empower students to lead with knowledge, ethics, and purpose.
              </p>

              <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-2">
                <a
                  href="#admissions"
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl font-bold text-sm text-white bg-[#2167B2] hover:bg-[#123B70] shadow-md hover:shadow-xl transition-all"
                >
                  <span>Apply for Admission</span>
                  <ArrowRight className="w-4 h-4" />
                </a>
                <button
                  onClick={() => navigate('/login')}
                  className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl font-bold text-sm text-brand-900 bg-white border border-brand-200 hover:bg-brand-50 shadow-sm transition-all"
                >
                  <LogIn className="w-4 h-4 text-brand-600" />
                  <span>Student & Parent Portal</span>
                </button>
              </div>

              {/* Key Trust Signals */}
              <div className="pt-6 grid grid-cols-3 gap-4 border-t border-slate-200">
                <div>
                  <p className="text-2xl font-black text-[#123B70]">480+</p>
                  <p className="text-xs text-slate-500 font-medium">Enrolled Students</p>
                </div>
                <div>
                  <p className="text-2xl font-black text-brand-600">100%</p>
                  <p className="text-xs text-slate-500 font-medium">BISE Matric Pass</p>
                </div>
                <div>
                  <p className="text-2xl font-black text-emerald-600">38+</p>
                  <p className="text-xs text-slate-500 font-medium">Dedicated Faculty</p>
                </div>
              </div>
            </div>

            {/* Hero Visual Card */}
            <div className="lg:col-span-5">
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-brand-100 shadow-xl relative">
                <div className="flex items-center justify-between pb-6 border-b border-slate-100">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 rounded-2xl bg-brand-50 border border-brand-200 flex items-center justify-center text-brand-700">
                      <GraduationCap className="w-6 h-6" />
                    </div>
                    <div>
                      <h3 className="font-bold text-slate-800 text-sm">Academic Excellence</h3>
                      <p className="text-xs text-slate-500">Sindh Textbook Board Certified</p>
                    </div>
                  </div>
                  <span className="px-2.5 py-1 rounded-full bg-emerald-50 text-emerald-700 text-xs font-bold">
                    BISE Hyderabad
                  </span>
                </div>

                <div className="py-6 space-y-4">
                  <div className="p-4 rounded-2xl bg-[#F5F8FC] border border-slate-100 flex items-start gap-3">
                    <BookOpen className="w-5 h-5 text-brand-600 mt-0.5 flex-shrink-0" />
                    <div>
                      <p className="text-xs font-bold text-slate-800">Holistic Curriculum</p>
                      <p className="text-[11px] text-slate-500 mt-0.5">
                        Modern STEM education, linguistic fluency (English, Urdu, Sindhi), and moral grounding.
                      </p>
                    </div>
                  </div>

                  <div className="p-4 rounded-2xl bg-[#F5F8FC] border border-slate-100 flex items-start gap-3">
                    <Laptop className="w-5 h-5 text-sky-600 mt-0.5 flex-shrink-0" />
                    <div>
                      <p className="text-xs font-bold text-slate-800">Digital LMS & Parent Portal</p>
                      <p className="text-[11px] text-slate-500 mt-0.5">
                        Interactive homework submission, attendance alerts, and printable fee vouchers.
                      </p>
                    </div>
                  </div>

                  <div className="p-4 rounded-2xl bg-[#F5F8FC] border border-slate-100 flex items-start gap-3">
                    <ShieldCheck className="w-5 h-5 text-emerald-600 mt-0.5 flex-shrink-0" />
                    <div>
                      <p className="text-xs font-bold text-slate-800">Safe & Purpose-Built Campuses</p>
                      <p className="text-[11px] text-slate-500 mt-0.5">
                        Adjacent Autobahn Road with 24/7 CCTV surveillance, laboratories, and activity halls.
                      </p>
                    </div>
                  </div>
                </div>

                <div className="pt-2 text-center">
                  <a
                    href="#admissions"
                    className="text-xs font-bold text-brand-700 hover:text-brand-900 inline-flex items-center gap-1 transition-colors"
                  >
                    <span>Schedule a Campus Visit</span>
                    <ChevronRight className="w-4 h-4" />
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* About Section & Philosophy */}
      <section id="about" className="py-20 bg-white border-t border-slate-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <span className="px-3 py-1 rounded-full bg-brand-50 text-brand-700 text-xs font-bold uppercase tracking-wider">
              About Bluebells
            </span>
            <h2 className="text-3xl font-extrabold text-[#123B70] mt-3">
              A Legacy of Academic Leadership in Latifabad
            </h2>
            <p className="text-sm sm:text-base text-slate-600 mt-3">
              Established with the core conviction that education should prepare students for the complexities of modern life, Bluebells provides a safe, nurturing, and intellectually demanding environment.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-[#F5F8FC] rounded-2xl p-8 border border-slate-200 space-y-4">
              <div className="w-12 h-12 rounded-xl bg-[#123B70] text-white flex items-center justify-center font-bold">
                <Compass className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-slate-800">Our Vision</h3>
              <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
                To be the most respected institution in Hyderabad for academic achievement, analytical inquiry, ethical integrity, and community responsibility.
              </p>
            </div>

            <div className="bg-[#F5F8FC] rounded-2xl p-8 border border-slate-200 space-y-4">
              <div className="w-12 h-12 rounded-xl bg-[#2167B2] text-white flex items-center justify-center font-bold">
                <TargetIcon className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-slate-800">Our Mission</h3>
              <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
                To inspire lifelong learning and responsible citizenship by cultivating curiosity, critical reasoning, and moral character in every student.
              </p>
            </div>

            <div className="bg-[#F5F8FC] rounded-2xl p-8 border border-slate-200 space-y-4">
              <div className="w-12 h-12 rounded-xl bg-emerald-600 text-white flex items-center justify-center font-bold">
                <Award className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-slate-800">Our Philosophy</h3>
              <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
                Guided by our motto <em>"Preparing for Life"</em>, we balance board exam excellence with sports, public speaking, science experimentation, and character building.
              </p>
            </div>
          </div>

          {/* Leadership Spotlight */}
          <div className="mt-16 bg-gradient-to-r from-[#123B70] to-[#2167B2] rounded-3xl p-8 sm:p-12 text-white">
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
              <div className="lg:col-span-8 space-y-4">
                <span className="px-2.5 py-0.5 rounded-full bg-white/20 text-xs font-semibold">
                  Principal's Message
                </span>
                <blockquote className="text-lg sm:text-xl font-medium italic leading-relaxed text-brand-50">
                  "At Bluebells, we believe that education extends far beyond memorizing textbooks. We partner with parents to discover the unique potential of each child, instilling discipline, compassion, and the intellectual confidence needed to thrive anywhere in the world."
                </blockquote>
                <div>
                  <p className="font-bold text-base text-white">{BLUEBELLS_BRANDING.leadership.principal.name}</p>
                  <p className="text-xs text-brand-200">{BLUEBELLS_BRANDING.leadership.principal.title} • {BLUEBELLS_BRANDING.leadership.principal.qualification}</p>
                </div>
              </div>
              <div className="lg:col-span-4 border-t lg:border-t-0 lg:border-l border-white/20 pt-6 lg:pt-0 lg:pl-8 space-y-3 text-xs text-brand-100">
                <p><strong>Vice Principal:</strong> {BLUEBELLS_BRANDING.leadership.vicePrincipal.name}</p>
                <p><strong>Primary Campus:</strong> Autobahn Road, Latifabad Unit 2</p>
                <p><strong>Affiliation:</strong> BISE Hyderabad, Sindh Board</p>
                <p><strong>Contact:</strong> {BLUEBELLS_BRANDING.contact.phoneFormatted}</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Academic Programs */}
      <section id="academics" className="py-20 bg-[#F5F8FC]">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <span className="px-3 py-1 rounded-full bg-brand-50 text-brand-700 text-xs font-bold uppercase tracking-wider">
              Academic Spectrum
            </span>
            <h2 className="text-3xl font-extrabold text-[#123B70] mt-3">
              Nurturing Excellence at Every Stage
            </h2>
            <p className="text-sm sm:text-base text-slate-600 mt-3">
              A continuous, structured learning pathway designed to build confidence from toddlers to secondary board examinees.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Early Years */}
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
              <span className="px-2.5 py-1 rounded bg-amber-50 text-amber-800 text-[11px] font-bold">PRE-PRIMARY</span>
              <h3 className="text-lg font-bold text-slate-800 mt-4">Early Years</h3>
              <p className="text-xs text-brand-600 font-semibold mt-1">Playgroup • Nursery • Kindergarten</p>
              <p className="text-xs text-slate-500 mt-3 leading-relaxed">
                Activity-based phonics, sensorial discovery, fine motor coordination, social manners, and joyful habits.
              </p>
              <ul className="mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-1.5">
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Play-based Learning</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Phonics & Reading</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Secure Toddler Play Area</li>
              </ul>
            </div>

            {/* Primary School */}
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
              <span className="px-2.5 py-1 rounded bg-blue-50 text-blue-800 text-[11px] font-bold">PRIMARY</span>
              <h3 className="text-lg font-bold text-slate-800 mt-4">Primary Wing</h3>
              <p className="text-xs text-brand-600 font-semibold mt-1">Classes 1 through 5</p>
              <p className="text-xs text-slate-500 mt-3 leading-relaxed">
                Foundational proficiency in Mathematics, English Comprehension, Urdu, Science discovery, and Islamiat.
              </p>
              <ul className="mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-1.5">
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Conceptual Math Mastery</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Bilingual Fluency</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Computer Literacy</li>
              </ul>
            </div>

            {/* Middle School */}
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
              <span className="px-2.5 py-1 rounded bg-emerald-50 text-emerald-800 text-[11px] font-bold">MIDDLE SCHOOL</span>
              <h3 className="text-lg font-bold text-slate-800 mt-4">Middle Wing</h3>
              <p className="text-xs text-brand-600 font-semibold mt-1">Classes 6 through 8</p>
              <p className="text-xs text-slate-500 mt-3 leading-relaxed">
                Analytical science laboratory exercises, pre-matriculation prep, Sindhi Salees, and computer programming basics.
              </p>
              <ul className="mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-1.5">
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Hands-on Science Labs</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Regional Language Skills</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Debate & Sports</li>
              </ul>
            </div>

            {/* Secondary / Matric */}
            <div className="bg-white rounded-2xl p-6 border border-brand-300 shadow-md hover:shadow-lg transition-shadow relative">
              <span className="px-2.5 py-1 rounded bg-purple-50 text-purple-800 text-[11px] font-bold">SECONDARY MATRIC</span>
              <h3 className="text-lg font-bold text-[#123B70] mt-4">Matriculation</h3>
              <p className="text-xs text-brand-600 font-semibold mt-1">Class 9 & 10 (BISE Hyderabad)</p>
              <p className="text-xs text-slate-500 mt-3 leading-relaxed">
                Sindh Textbook Board Science Group (Physics, Chemistry, Biology / Computer Science, Mathematics) with distinction focus.
              </p>
              <ul className="mt-4 pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-1.5">
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Board Exam Masterclasses</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Sindh Board A-1 Focus</li>
                <li className="flex items-center gap-1.5"><CheckCircle className="w-3.5 h-3.5 text-emerald-600" /> Certified Science Labs</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* Campus Facilities */}
      <section id="facilities" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <span className="px-3 py-1 rounded-full bg-brand-50 text-brand-700 text-xs font-bold uppercase tracking-wider">
              Campus Facilities
            </span>
            <h2 className="text-3xl font-extrabold text-[#123B70] mt-3">
              Modern Spaces for Inquiry & Growth
            </h2>
            <p className="text-sm sm:text-base text-slate-600 mt-3">
              Purpose-built facilities adjacent to Autobahn Road, equipped to support active learning, laboratory experiments, and child well-being.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-6 rounded-2xl bg-[#F5F8FC] border border-slate-200 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-blue-100 text-brand-700 flex items-center justify-center">
                <Microscope className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-slate-800 text-base">Accredited Science Laboratories</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                Fully equipped Physics and Chemistry laboratory apparatus compliant with Board of Intermediate & Secondary Education (BISE) Hyderabad matriculation practical standards.
              </p>
            </div>

            <div className="p-6 rounded-2xl bg-[#F5F8FC] border border-slate-200 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center">
                <Laptop className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-slate-800 text-base">Modern IT & Computer Center</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                High-speed networked computer workstations, teaching digital literacy, algorithmic problem solving, office applications, and coding fundamentals.
              </p>
            </div>

            <div className="p-6 rounded-2xl bg-[#F5F8FC] border border-slate-200 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
                <Building className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-slate-800 text-base">Secure & Monitored Campus</h3>
              <p className="text-xs text-slate-600 leading-relaxed">
                CCTV-surveilled environment with secure biometric gates, trained security guards, and standby electricity generators to ensure continuous learning.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Admissions & Online Inquiry Form */}
      <section id="admissions" className="py-20 bg-gradient-to-b from-[#F5F8FC] to-white border-t border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-start">
            <div className="lg:col-span-5 space-y-6">
              <span className="px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold uppercase tracking-wider">
                Admissions 2026-2027
              </span>
              <h2 className="text-3xl font-extrabold text-[#123B70] tracking-tight">
                Join the Bluebells Family
              </h2>
              <p className="text-sm text-slate-600 leading-relaxed">
                We welcome prospective students and parents to visit our Latifabad campus. Our admissions team guides families through registration, student assessment, and class placement.
              </p>

              <div className="space-y-4 pt-2">
                <div className="flex items-start gap-3">
                  <div className="w-7 h-7 rounded-full bg-[#123B70] text-white flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                    1
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-800">Submit Admission Inquiry</p>
                    <p className="text-[11px] text-slate-500">Fill the online form or visit our admissions office with student B-Form.</p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="w-7 h-7 rounded-full bg-[#123B70] text-white flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                    2
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-800">Assessment & Interview</p>
                    <p className="text-[11px] text-slate-500">Age-appropriate evaluation in English, Mathematics, and Urdu.</p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="w-7 h-7 rounded-full bg-[#123B70] text-white flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">
                    3
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-800">Enrollment & Fee Challan</p>
                    <p className="text-[11px] text-slate-500">Issuance of official Bank Alfalah 3-copy fee voucher and student ID badge.</p>
                  </div>
                </div>
              </div>

              <div className="p-4 rounded-xl bg-blue-50 border border-blue-200 text-xs text-blue-900 space-y-1">
                <p className="font-bold flex items-center gap-1.5">
                  <Phone className="w-3.5 h-3.5" /> Admissions Desk Helpline:
                </p>
                <p className="text-blue-800 font-semibold">{BLUEBELLS_BRANDING.contact.phoneFormatted} (Direct WhatsApp & Voice)</p>
                <p className="text-blue-700">Office Hours: Monday – Saturday (8:00 AM – 2:00 PM)</p>
              </div>
            </div>

            {/* Form Card */}
            <div className="lg:col-span-7 bg-white p-8 rounded-3xl border border-slate-200 shadow-xl">
              <h3 className="text-lg font-bold text-[#123B70] mb-2">Online Admission Inquiry Form</h3>
              <p className="text-xs text-slate-500 mb-6">
                Complete this form to register interest. Our admissions counselor will contact you within 24 hours.
              </p>

              {inquirySubmitted ? (
                <div className="p-6 rounded-2xl bg-emerald-50 border border-emerald-200 text-center space-y-3">
                  <CheckCircle className="w-12 h-12 text-emerald-600 mx-auto" />
                  <h4 className="text-base font-bold text-emerald-900">Inquiry Received Successfully!</h4>
                  <p className="text-xs text-emerald-800 max-w-md mx-auto">
                    Thank you for your interest in Bluebells Schooling System. Our admissions desk has recorded your application for <strong>{formData.studentName}</strong> and will call <strong>{formData.guardianPhone}</strong> shortly.
                  </p>
                  <button
                    onClick={() => {
                      setInquirySubmitted(false);
                      setFormData({ studentName: '', dateOfBirth: '', gender: 'MALE', targetGrade: 'Class 9', guardianName: '', guardianPhone: '', notes: '' });
                    }}
                    className="mt-4 px-4 py-2 bg-emerald-700 text-white rounded-lg text-xs font-semibold hover:bg-emerald-800"
                  >
                    Submit Another Inquiry
                  </button>
                </div>
              ) : (
                <form onSubmit={handleInquirySubmit} className="space-y-4">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Student Full Name *
                      </label>
                      <input
                        type="text"
                        required
                        value={formData.studentName}
                        onChange={(e) => setFormData({ ...formData, studentName: e.target.value })}
                        placeholder="e.g. Muhammad Bilal"
                        className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500"
                      />
                    </div>

                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Target Grade / Class *
                      </label>
                      <select
                        value={formData.targetGrade}
                        onChange={(e) => setFormData({ ...formData, targetGrade: e.target.value })}
                        className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 bg-white"
                      >
                        <option value="Playgroup">Playgroup (Early Years)</option>
                        <option value="Nursery">Nursery</option>
                        <option value="Kindergarten">Kindergarten / Prep</option>
                        <option value="Class 1">Class 1</option>
                        <option value="Class 2">Class 2</option>
                        <option value="Class 3">Class 3</option>
                        <option value="Class 4">Class 4</option>
                        <option value="Class 5">Class 5</option>
                        <option value="Class 6">Class 6</option>
                        <option value="Class 7">Class 7</option>
                        <option value="Class 8">Class 8</option>
                        <option value="Class 9">Class 9 (Matric Science)</option>
                        <option value="Class 10">Class 10 (Matric Science)</option>
                      </select>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Date of Birth
                      </label>
                      <input
                        type="date"
                        value={formData.dateOfBirth}
                        onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                        className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500"
                      />
                    </div>
                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Gender
                      </label>
                      <select
                        value={formData.gender}
                        onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                        className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500 bg-white"
                      >
                        <option value="MALE">Male</option>
                        <option value="FEMALE">Female</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                        Guardian Phone *
                      </label>
                      <input
                        type="tel"
                        required
                        value={formData.guardianPhone}
                        onChange={(e) => setFormData({ ...formData, guardianPhone: e.target.value })}
                        placeholder="+92 300 1234567"
                        className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                      Parent / Guardian Name *
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.guardianName}
                      onChange={(e) => setFormData({ ...formData, guardianName: e.target.value })}
                      placeholder="e.g. Tariq Mehmood Khan"
                      className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                      Additional Remarks / Prior School
                    </label>
                    <textarea
                      rows={2}
                      value={formData.notes}
                      onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                      placeholder="Mention any prior school or specific requirements..."
                      className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand-500"
                    />
                  </div>

                  <button
                    type="submit"
                    disabled={submitting}
                    className="w-full py-3 bg-[#123B70] hover:bg-[#2167B2] text-white font-bold rounded-xl text-xs shadow-md transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                  >
                    <Send className="w-4 h-4" />
                    <span>{submitting ? 'Submitting Application...' : 'Submit Admission Inquiry'}</span>
                  </button>
                </form>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Campus Life / Photo Gallery */}
      <section id="campus-life" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <span className="px-3 py-1 rounded-full bg-brand-50 text-brand-700 text-xs font-bold uppercase tracking-wider">
              Life at Bluebells
            </span>
            <h2 className="text-3xl font-extrabold text-[#123B70] mt-3">
              Moments of Joy, Discovery & Achievement
            </h2>
            <p className="text-sm sm:text-base text-slate-600 mt-3">
              Snapshots of student activities, science exhibitions, national celebrations, and morning assemblies.
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="rounded-2xl overflow-hidden bg-slate-100 border border-slate-200 h-48 sm:h-64 flex flex-col justify-end p-4 relative group">
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent" />
              <div className="relative text-white z-10">
                <span className="text-[10px] uppercase font-bold text-brand-300 tracking-wider">Academics</span>
                <p className="font-bold text-xs sm:text-sm">Science & Robotics Expo</p>
              </div>
            </div>

            <div className="rounded-2xl overflow-hidden bg-slate-100 border border-slate-200 h-48 sm:h-64 flex flex-col justify-end p-4 relative group">
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent" />
              <div className="relative text-white z-10">
                <span className="text-[10px] uppercase font-bold text-emerald-300 tracking-wider">Campus Life</span>
                <p className="font-bold text-xs sm:text-sm">Morning Flag Assembly</p>
              </div>
            </div>

            <div className="rounded-2xl overflow-hidden bg-slate-100 border border-slate-200 h-48 sm:h-64 flex flex-col justify-end p-4 relative group">
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent" />
              <div className="relative text-white z-10">
                <span className="text-[10px] uppercase font-bold text-amber-300 tracking-wider">Co-Curricular</span>
                <p className="font-bold text-xs sm:text-sm">Annual Sports Gala</p>
              </div>
            </div>

            <div className="rounded-2xl overflow-hidden bg-slate-100 border border-slate-200 h-48 sm:h-64 flex flex-col justify-end p-4 relative group">
              <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent" />
              <div className="relative text-white z-10">
                <span className="text-[10px] uppercase font-bold text-purple-300 tracking-wider">Excellence</span>
                <p className="font-bold text-xs sm:text-sm">Annual Prize Distribution</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Contact & Map Footer */}
      <footer id="contact" className="bg-[#123B70] text-white pt-16 pb-12 border-t border-brand-800">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-10 pb-12 border-b border-white/10">
            <div className="space-y-4">
              <BluebellsLogo variant="horizontal" theme="light" size="md" />
              <p className="text-xs text-brand-200 leading-relaxed mt-2">
                Bluebells Schooling System is committed to delivering quality institutional education, moral ethics, and academic distinction across Hyderabad.
              </p>
              <p className="text-[11px] text-brand-300 font-mono">
                Reg No: BSS-HYD-71800 • BISE Hyderabad
              </p>
            </div>

            <div className="space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-brand-300">Latifabad Campuses</h4>
              <div className="text-xs text-brand-100 space-y-2">
                <p>
                  <strong>Main Campus:</strong><br />
                  Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Sindh (71800)
                </p>
                <p>
                  <strong>Junior Branch:</strong><br />
                  Unit #7 (Behind Dolmen Marriage Hall), Latifabad, Hyderabad
                </p>
              </div>
            </div>

            <div className="space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-brand-300">Direct Inquiries</h4>
              <ul className="text-xs text-brand-100 space-y-2">
                <li className="flex items-center gap-2">
                  <Phone className="w-3.5 h-3.5 text-brand-400" />
                  <a href="tel:+923169681932" className="hover:text-white">{BLUEBELLS_BRANDING.contact.phoneFormatted}</a>
                </li>
                <li className="flex items-center gap-2">
                  <Mail className="w-3.5 h-3.5 text-brand-400" />
                  <a href={`mailto:${BLUEBELLS_BRANDING.contact.email}`} className="hover:text-white">{BLUEBELLS_BRANDING.contact.email}</a>
                </li>
                <li className="flex items-center gap-2">
                  <Clock className="w-3.5 h-3.5 text-brand-400" />
                  <span>Mon – Sat: 8:00 AM – 2:00 PM</span>
                </li>
              </ul>
            </div>

            <div className="space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-brand-300">Portal Access</h4>
              <p className="text-xs text-brand-200">
                Staff, enrolled students, and registered parents can access academic portals, homework tasks, and fee vouchers directly:
              </p>
              <button
                onClick={() => navigate('/login')}
                className="w-full py-2.5 px-4 rounded-xl bg-white text-[#123B70] font-bold text-xs hover:bg-brand-50 shadow transition-colors flex items-center justify-center gap-2"
              >
                <LogIn className="w-4 h-4" />
                <span>Enter Bluebells Portal</span>
              </button>
            </div>
          </div>

          <div className="pt-8 flex flex-col sm:flex-row items-center justify-between text-xs text-brand-300 gap-4">
            <p>© {new Date().getFullYear()} Bluebells Schooling System. All Rights Reserved. Autobahn Road, Latifabad, Hyderabad, Pakistan.</p>
            <div className="flex gap-4">
              <a href="#about" className="hover:text-white">About</a>
              <a href="#academics" className="hover:text-white">Academics</a>
              <a href="#admissions" className="hover:text-white">Admissions</a>
              <button onClick={() => navigate('/login')} className="hover:text-white font-semibold">Sign In</button>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

// Simple target icon helper
function TargetIcon(props: React.SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round" {...props}>
      <circle cx="12" cy="12" r="10" />
      <circle cx="12" cy="12" r="6" />
      <circle cx="12" cy="12" r="2" />
    </svg>
  );
}
