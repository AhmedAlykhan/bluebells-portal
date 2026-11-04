import React from 'react';

export interface BluebellsLogoProps {
  variant?: 'full' | 'horizontal' | 'compact' | 'crest-only';
  theme?: 'dark' | 'light' | 'mono';
  size?: 'sm' | 'md' | 'lg' | 'xl';
  className?: string;
  showMotto?: boolean;
}

export const BluebellsLogo: React.FC<BluebellsLogoProps> = ({
  variant = 'horizontal',
  theme = 'dark',
  size = 'md',
  className = '',
  showMotto = true,
}) => {
  // Height and scale mappings
  const sizeMap = {
    sm: { icon: 32, title: 'text-sm', motto: 'text-[9px]', height: 'h-8' },
    md: { icon: 42, title: 'text-base', motto: 'text-[10px]', height: 'h-10' },
    lg: { icon: 54, title: 'text-xl', motto: 'text-xs', height: 'h-14' },
    xl: { icon: 72, title: 'text-2xl', motto: 'text-sm', height: 'h-18' },
  };

  const { icon, title, motto } = sizeMap[size];

  // Theme color definitions
  const isLight = theme === 'light'; // Light text for dark backgrounds (navy sidebar/footer)
  const isMono = theme === 'mono';   // Black & White for print receipts

  const titleColor = isLight ? 'text-white' : (isMono ? 'text-black' : 'text-[#123B70]');
  const subtitleColor = isLight ? 'text-brand-200' : (isMono ? 'text-gray-700' : 'text-[#2167B2]');
  const mottoColor = isLight ? 'text-brand-300' : (isMono ? 'text-gray-600' : 'text-[#64748B]');

  // SVG Emblem: Bluebell flower blossom + academic shield
  const Emblem = (
    <svg
      width={icon}
      height={icon}
      viewBox="0 0 100 100"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="flex-shrink-0 transition-transform duration-200 hover:scale-105"
      aria-label="Bluebells Schooling System Crest"
    >
      <defs>
        <linearGradient id="bluebellGrad" x1="20" y1="20" x2="80" y2="80" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor={isMono ? "#333333" : "#30BDFF"} />
          <stop offset="60%" stopColor={isMono ? "#000000" : "#2167B2"} />
          <stop offset="100%" stopColor={isMono ? "#000000" : "#123B70"} />
        </linearGradient>
        <linearGradient id="goldRibbon" x1="10" y1="80" x2="90" y2="80" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor={isMono ? "#666666" : "#E5A93C"} />
          <stop offset="50%" stopColor={isMono ? "#999999" : "#F7D170"} />
          <stop offset="100%" stopColor={isMono ? "#666666" : "#D49320"} />
        </linearGradient>
        <filter id="subtleGlow" x="-10%" y="-10%" width="120%" height="120%">
          <feDropShadow dx="0" dy="2" stdDeviation="2" floodColor="#123B70" floodOpacity="0.15" />
        </filter>
      </defs>

      {/* Outer Shield Container */}
      <path
        d="M50 4C74 4 88 12 88 28C88 64 64 88 50 96C36 88 12 64 12 28C12 12 26 4 50 4Z"
        fill={isLight ? "#0C2A52" : (isMono ? "#FFFFFF" : "#FFFFFF")}
        stroke={isMono ? "#000000" : (isLight ? "#368CE5" : "#123B70")}
        strokeWidth="3.5"
        strokeLinejoin="round"
        filter="url(#subtleGlow)"
      />

      {/* Inner Decorative Contour */}
      <path
        d="M50 9C70 9 82 16 82 30C82 61 62 82 50 89C38 82 18 61 18 30C18 16 30 9 50 9Z"
        fill="none"
        stroke={isMono ? "#cccccc" : (isLight ? "#1A5392" : "#EAF3FC")}
        strokeWidth="1.5"
      />

      {/* Bluebell Central Floral Blossom */}
      {/* Central Petal */}
      <path
        d="M50 20C44 32 40 44 50 56C60 44 56 32 50 20Z"
        fill="url(#bluebellGrad)"
      />
      {/* Left Petal Flare */}
      <path
        d="M48 24C38 32 32 44 38 52C44 50 46 42 48 34C49 30 48.5 26 48 24Z"
        fill={isMono ? "#555555" : "#30BDFF"}
      />
      {/* Right Petal Flare */}
      <path
        d="M52 24C62 32 68 44 62 52C56 50 54 42 52 34C51 30 51.5 26 52 24Z"
        fill={isMono ? "#111111" : "#123B70"}
      />

      {/* Stem & Foundation Leaves */}
      <path
        d="M50 56V68"
        stroke={isMono ? "#000000" : "#16845B"}
        strokeWidth="3"
        strokeLinecap="round"
      />
      <path
        d="M50 63C42 61 36 65 34 70C40 70 46 66 50 63Z"
        fill={isMono ? "#666666" : "#16845B"}
      />
      <path
        d="M50 63C58 61 64 65 66 70C60 70 54 66 50 63Z"
        fill={isMono ? "#444444" : "#126B49"}
      />

      {/* Golden Academic Stars */}
      <circle cx="28" cy="22" r="2" fill={isMono ? "#000000" : "#E5A93C"} />
      <circle cx="50" cy="14" r="2.5" fill={isMono ? "#000000" : "#E5A93C"} />
      <circle cx="72" cy="22" r="2" fill={isMono ? "#000000" : "#E5A93C"} />

      {/* Bottom Ribbon / Banner */}
      <path
        d="M20 78L26 73H74L80 78L75 83H25L20 78Z"
        fill="url(#goldRibbon)"
        stroke={isMono ? "#000000" : "#B37A12"}
        strokeWidth="1"
      />
      <text
        x="50"
        y="80"
        fontSize="5"
        fontWeight="bold"
        fontFamily="sans-serif"
        textAnchor="middle"
        fill={isMono ? "#000000" : "#3B2602"}
        letterSpacing="0.8"
      >
        PREPARING FOR LIFE
      </text>
    </svg>
  );

  if (variant === 'crest-only') {
    return <div className={`inline-flex items-center justify-center ${className}`}>{Emblem}</div>;
  }

  return (
    <div className={`inline-flex items-center gap-3 ${className}`}>
      {Emblem}

      <div className="flex flex-col text-left">
        <div className="flex items-baseline gap-1.5 leading-none">
          <span className={`font-black tracking-tight ${title} ${titleColor} uppercase font-sans`}>
            Bluebells
          </span>
          <span className={`font-bold tracking-normal ${subtitleColor} text-xs uppercase`}>
            Schooling System
          </span>
        </div>

        {showMotto && (
          <div className="flex items-center gap-1.5 mt-1">
            <span className={`text-[9px] font-semibold uppercase tracking-widest ${mottoColor}`}>
              Hyderabad • Latifabad
            </span>
            <span className="w-1 h-1 rounded-full bg-brand-400 opacity-70"></span>
            <span className={`italic font-medium ${motto} ${mottoColor}`}>
              Preparing for Life
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
