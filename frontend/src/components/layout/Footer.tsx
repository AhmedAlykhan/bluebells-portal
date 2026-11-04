import React from 'react';
import { SchoolProfile } from '../../types';
import { BLUEBELLS_BRANDING } from '../../config/branding';

interface FooterProps {
  schoolProfile?: SchoolProfile;
}

export const Footer: React.FC<FooterProps> = ({ schoolProfile }) => {
  return (
    <footer className="bg-white border-t border-slate-200/80 py-4 px-4 sm:px-6 lg:px-8 text-xs text-slate-500">
      <div className="flex flex-col sm:flex-row items-center justify-between max-w-7xl mx-auto gap-2">
        <div className="flex items-center gap-2">
          <span className="font-bold text-[#123B70]">
            {schoolProfile?.name || BLUEBELLS_BRANDING.name}
          </span>
          <span className="text-slate-300">•</span>
          <span className="italic text-brand-600 font-medium">"{BLUEBELLS_BRANDING.tagline}"</span>
        </div>
        <p className="text-slate-400 text-center sm:text-right text-[11px]">
          {schoolProfile?.address || BLUEBELLS_BRANDING.campusLocations.primary.address} • Affiliated with BISE Hyderabad
        </p>
      </div>
    </footer>
  );
};
