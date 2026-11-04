/**
 * Centralized Brand Configuration for Bluebells Schooling System
 * Latifabad, Hyderabad, Sindh, Pakistan
 *
 * This configuration serves as the single source of truth for institution names,
 * official logos, contact details, campus locations, leadership, and colors.
 */

export interface SchoolBranding {
  name: string;
  legalName: string;
  tagline: string;
  motto: string;
  establishedYear: number;
  affiliation: string;
  curriculum: string;
  campusLocations: {
    primary: {
      name: string;
      address: string;
      city: string;
      postalCode: string;
      province: string;
      country: string;
      notes: string;
    };
    secondary?: {
      name: string;
      address: string;
      city: string;
      province: string;
      notes: string;
    };
  };
  contact: {
    phone: string;
    phoneFormatted: string;
    whatsapp: string;
    email: string;
    website: string;
    facebookUrl: string;
  };
  leadership: {
    principal: {
      name: string;
      title: string;
      qualification: string;
    };
    vicePrincipal: {
      name: string;
      title: string;
      qualification: string;
    };
    examinationController: {
      name: string;
      title: string;
    };
  };
  banking: {
    primaryBank: string;
    accountTitle: string;
    accountNumber: string;
    branchCode: string;
    branchName: string;
    iban: string;
    secondaryBank: string;
    oneBillPrefix: string;
  };
  colors: {
    navy: string;
    blue: string;
    sky: string;
    cyan: string;
    softBlue: string;
    pageBg: string;
    cardBg: string;
    darkText: string;
    mutedText: string;
    border: string;
    success: string;
    warning: string;
    danger: string;
  };
}

export const BLUEBELLS_BRANDING: SchoolBranding = {
  name: 'Bluebells Schooling System',
  legalName: 'Bluebells Schooling System (Registered Sindh Education & Literacy Dept)',
  tagline: 'Preparing for Life',
  motto: 'Inspiring Lifelong Learning and Character Excellence',
  establishedYear: 2018,
  affiliation: 'Board of Intermediate & Secondary Education (BISE) Hyderabad',
  curriculum: 'Sindh Textbook Board & Early Years Holistic Framework',
  campusLocations: {
    primary: {
      name: 'Main Latifabad Campus',
      address: 'Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad',
      city: 'Hyderabad',
      postalCode: '71800',
      province: 'Sindh',
      country: 'Pakistan',
      notes: 'Administrative Secretariat, Science Laboratories & Secondary School Wing',
    },
    secondary: {
      name: 'Junior & Admissions Campus',
      address: 'Unit #7 (Behind Dolmen Marriage Hall), Latifabad',
      city: 'Hyderabad',
      province: 'Sindh',
      notes: 'Early Years Playgroup & Primary School Wing',
    },
  },
  contact: {
    phone: '+92 316 9681932',
    phoneFormatted: '+92 316 9681932',
    whatsapp: '+923169681932',
    email: 'Bluebells.schoolingsystem@gmail.com',
    website: 'https://bluebellsschooling.org',
    facebookUrl: 'https://www.facebook.com/BluebellsSchoolingSystem',
  },
  leadership: {
    principal: {
      name: 'Huma Arif',
      title: 'Academic Principal',
      qualification: 'M.Ed, Institutional Leadership',
    },
    vicePrincipal: {
      name: 'Hana Khan',
      title: 'Vice Principal & Head of Academics',
      qualification: 'M.A., Educational Administration',
    },
    examinationController: {
      name: 'Prof. S. R. Abbasi',
      title: 'Controller of Examinations',
    },
  },
  banking: {
    primaryBank: 'Bank Alfalah Limited',
    accountTitle: 'Bluebells Schooling System',
    accountNumber: '5512-0010048291',
    branchCode: '0482',
    branchName: 'Autobahn Road Branch, Latifabad, Hyderabad',
    iban: 'PK42ALFH04820055120010048291',
    secondaryBank: 'Meezan Bank Limited (Latifabad Unit 7 Branch)',
    oneBillPrefix: '100482',
  },
  colors: {
    navy: '#123B70',
    blue: '#2167B2',
    sky: '#368CE5',
    cyan: '#30BDFF',
    softBlue: '#EAF3FC',
    pageBg: '#F5F8FC',
    cardBg: '#FFFFFF',
    darkText: '#172B45',
    mutedText: '#64748B',
    border: '#E2EAF3',
    success: '#16845B',
    warning: '#D99A28',
    danger: '#D94B55',
  },
};

export const BRAND_INFO = {
  fullName: BLUEBELLS_BRANDING.name,
  legalName: BLUEBELLS_BRANDING.legalName,
  tagline: BLUEBELLS_BRANDING.tagline,
  motto: BLUEBELLS_BRANDING.tagline,
  establishedYear: BLUEBELLS_BRANDING.establishedYear,
  affiliation: BLUEBELLS_BRANDING.affiliation,
  address: {
    primary: `${BLUEBELLS_BRANDING.campusLocations.primary.address}, ${BLUEBELLS_BRANDING.campusLocations.primary.city}, ${BLUEBELLS_BRANDING.campusLocations.primary.province}, Pakistan (${BLUEBELLS_BRANDING.campusLocations.primary.postalCode})`,
    secondary: `${BLUEBELLS_BRANDING.campusLocations.secondary?.address}, ${BLUEBELLS_BRANDING.campusLocations.secondary?.city}`,
  },
  phone: {
    primary: BLUEBELLS_BRANDING.contact.phone,
    formatted: BLUEBELLS_BRANDING.contact.phoneFormatted,
  },
  email: BLUEBELLS_BRANDING.contact.email,
  website: BLUEBELLS_BRANDING.contact.website,
  facebookUrl: BLUEBELLS_BRANDING.contact.facebookUrl,
  leadership: {
    principal: BLUEBELLS_BRANDING.leadership.principal.name,
    principalTitle: BLUEBELLS_BRANDING.leadership.principal.title,
    vicePrincipal: BLUEBELLS_BRANDING.leadership.vicePrincipal.name,
    vicePrincipalTitle: BLUEBELLS_BRANDING.leadership.vicePrincipal.title,
  },
  banking: {
    primaryBank: BLUEBELLS_BRANDING.banking.primaryBank,
    branch: BLUEBELLS_BRANDING.banking.branchName,
    accountTitle: BLUEBELLS_BRANDING.banking.accountTitle,
    accountNumber: BLUEBELLS_BRANDING.banking.accountNumber,
    iban: BLUEBELLS_BRANDING.banking.iban,
    secondaryBank: BLUEBELLS_BRANDING.banking.secondaryBank,
    oneBillPrefix: BLUEBELLS_BRANDING.banking.oneBillPrefix,
  },
  colors: BLUEBELLS_BRANDING.colors,
};
