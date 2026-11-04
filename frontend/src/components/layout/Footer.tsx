import React from 'react';
import { SchoolProfile } from '../../types';

interface FooterProps {
  schoolProfile?: SchoolProfile;
}

export const Footer: React.FC<FooterProps> = ({ schoolProfile }) => {
  return (
    <footer className="bg-white border-t border-slate-200 py-3 px-6 text-center text-xs text-slate-500">
      <div className="flex flex-col sm:flex-row items-center justify-between max-w-7xl mx-auto">
        <p>
          © 2026 {schoolProfile?.name || 'Bluebells Schooling System'}. All rights reserved.
        </p>
        <p className="mt-1 sm:mt-0 text-slate-400">
          {schoolProfile?.address || 'Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad, Pakistan'}
        </p>
      </div>
    </footer>
  );
};
