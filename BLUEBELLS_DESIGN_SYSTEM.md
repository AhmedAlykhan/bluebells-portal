# Bluebells Schooling System – Design System Specification

**Institution:** Bluebells Schooling System (Hyderabad, Sindh, Pakistan)  
**Primary Aesthetic:** Contemporary Institutional White & Blue  
**Brand Motto:** *"Preparing for Life"*  
**Version:** 2.0.0

---

## 1. Brand Identity & Principles

The Bluebells Design System reflects academic dignity, operational clarity, and welcoming warmth. Blue and white are the dominant colors, representing wisdom, discipline, trust, and cleanliness.

### Design Principles:
1. **Clarity Over Novelty:** Prioritize legibility, logical hierarchy, and rapid comprehension for teachers marking attendance, parents reviewing fees, and students tracking homework.
2. **Pakistani Localization First:** Native support for PKR currency formatting, Asia/Karachi (UTC+5) timezone, BISE Hyderabad matriculation standards, and Bank Alfalah / 1Bill challan conventions.
3. **Data Authenticity:** No simulated stats or decorative non-functional buttons. Every metric, chart, and table maps to actual database models.
4. **Print Fidelity:** Official documents (Report Cards, Fee Vouchers, Transcripts) must render identically on screen and on physical A4/A3 paper with clean page breaks.

---

## 2. Color Palette & Token Scale

### Primary Brand Palette
```css
--color-navy: #123B70;       /* Deep Navy - Headers, Brand Accents, Hero Surfaces */
--color-blue: #2167B2;       /* Primary Blue - Primary Buttons, Active States, Key Actions */
--color-sky: #368CE5;        /* Bright Accent Blue - Badges, Focus Rings, Highlights */
--color-cyan: #30BDFF;       /* Cyan Flower Accent - Logo Blossom, Interactive Hovers */
--color-soft-blue: #EAF3FC;  /* Soft Blue - Subtle Card Backgrounds, Active Nav Items */
--color-page-bg: #F5F8FC;    /* Page Background - Soft, glare-reducing canvas */
--color-card-bg: #FFFFFF;    /* Pure White - Elevated Surfaces and Cards */
```

### Semantic Feedback Palette
```css
--color-success: #16845B;    /* Emerald Green - Present Attendance, Paid Fees, High Marks */
--color-success-bg: #E8F8F0;
--color-warning: #D99A28;    /* Warm Amber - Due Soon, Partial Payments, Late Status */
--color-warning-bg: #FEF8E8;
--color-danger: #D94B55;     /* Crimson Red - Absent, Overdue Invoices, Failing Marks */
--color-danger-bg: #FEECEE;
--color-info: #2563EB;       /* Informational Alerts & Announcements */
--color-info-bg: #EFF6FF;
```

### Neutral & Typography Palette
```css
--color-text-primary: #172B45;   /* Deep Slate Navy - Headings, Body Text */
--color-text-secondary: #64748B; /* Slate Gray - Subtitles, Meta Information */
--color-text-muted: #94A3B8;     /* Light Muted Gray - Placeholders, Timestamps */
--color-border: #E2EAF3;         /* Subtle Ice Border - Card outlines, dividers */
--color-border-focus: #368CE5;   /* Active outline for focused controls */
```

---

## 3. Typography Scale

Font Family: `'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif`

| Token | Size | Line Height | Weight | Usage |
|---|---|---|---|---|
| `text-display` | 36px (2.25rem) | 44px | Bold (700) | Public Hero Title, Report Card Main Header |
| `text-h1` | 28px (1.75rem) | 36px | Bold (700) | Dashboard Section Title, Document Headers |
| `text-h2` | 22px (1.375rem)| 28px | SemiBold (600) | Page Headers, Module Section Banners |
| `text-h3` | 18px (1.125rem)| 24px | SemiBold (600) | Card Titles, Modal Headers |
| `text-h4` | 15px (0.9375rem)| 20px | SemiBold (600) | Subsection Titles, Table Headers |
| `text-body` | 14px (0.875rem)| 20px | Regular (400) | Standard Body Copy, Table Cells, Form Labels |
| `text-small`| 12px (0.75rem) | 16px | Medium (500) | Meta Text, Badges, Timestamps, Captions |
| `text-micro`| 10px (0.625rem)| 14px | SemiBold (600) | Fine Print, Currency Indicators, Status Pills |

---

## 4. Elevation, Radii & Shadows

### Border Radius Tokens
- `rounded-sm`: 4px — Status pills, fine checkboxes, mini badges
- `rounded-md`: 6px — Table buttons, filter chips, dropdown items
- `rounded-lg`: 10px — Standard buttons, input controls, small cards
- `rounded-xl`: 14px — Standard cards, dialog headers, drawer containers
- `rounded-2xl`: 20px — Modal dialogs, dashboard hero banners, public feature cards

### Shadows
- `shadow-subtle`: `0 1px 3px rgba(18, 59, 112, 0.05), 0 1px 2px rgba(18, 59, 112, 0.03)`
- `shadow-card`: `0 4px 12px -2px rgba(18, 59, 112, 0.06), 0 2px 6px -1px rgba(18, 59, 112, 0.03)`
- `shadow-elevated`: `0 12px 28px -4px rgba(18, 59, 112, 0.12), 0 4px 10px -2px rgba(18, 59, 112, 0.04)`
- `shadow-modal`: `0 24px 48px -12px rgba(18, 59, 112, 0.25)`

---

## 5. UI Component Library Specifications

### 5.1 Buttons
- **Primary Button:** Background `--color-blue` (`#2167B2`), hover `#123B70`, text pure white, font-weight 600, rounded-lg, subtle shadow.
- **Secondary Button:** Background pure white, border 1px `--color-border`, text `--color-text-primary`, hover background `--color-soft-blue`.
- **Accent Button:** Background `--color-sky` (`#368CE5`), hover `#2167B2`, text white.
- **Success Action:** Background `--color-success` (`#16845B`), hover `#0F5C3F`, text white.
- **Danger Action:** Background `--color-danger` (`#D94B55`), hover `#B32B34`, text white.
- **Ghost Button:** Transparent background, text `--color-blue`, hover background `--color-soft-blue`.

### 5.2 Badges & Status Pills
- **Active / Present / Paid:** Green badge (`bg-emerald-50 text-emerald-800 border border-emerald-200`)
- **Warning / Late / Partial:** Amber badge (`bg-amber-50 text-amber-800 border border-amber-200`)
- **Danger / Absent / Unpaid:** Red badge (`bg-red-50 text-red-800 border border-red-200`)
- **Session / Class / Info:** Blue badge (`bg-blue-50 text-blue-800 border border-blue-200`)
- **Secondary / Matric:** Purple badge (`bg-purple-50 text-purple-800 border border-purple-200`)

### 5.3 Interactive Tables
- Header: Light ice blue surface (`#F8FAFC`), uppercase text, font size 11px, tracking-wider, text slate-500.
- Rows: Clean white background with hover state (`hover:bg-blue-50/40`), subtle border divider (`#E2EAF3`).
- Empty State: Centered illustration/icon with informative title, description, and primary call-to-action button.
- Pagination: Page indicator, records per page dropdown, and fast previous/next navigation controls.

### 5.4 Document Design System
All printable institutional documents follow standardized layout rules:
- **School Header:** Official Bluebells crest on the left, full official institution name, Latifabad campus address, contact info, and affiliation with Board of Intermediate & Secondary Education (BISE) Hyderabad.
- **Official Border:** Double border with corner accents.
- **Watermark:** Faint Bluebells blossom seal in center background (opacity 0.04).
- **Authorized Signatures:** 4 signature blocks: Class Teacher, Vice Principal, Examination Controller, Academic Principal.
- **Print Optimization:** Formatted strictly for A4 portrait (single-page), A4 landscape (3-copy fee challan), or A3 format (detailed multi-subject transcript).
