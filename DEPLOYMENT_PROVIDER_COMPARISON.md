# BLUEBELLS SCHOOLING SYSTEM
## Module H — Production Deployment Provider Comparison Matrix

**Document Reference:** `BSS-DEP-001`  
**Institution:** Bluebells Schooling System, Hyderabad, Sindh, Pakistan  
**Author:** Ahmed Ali Khan <medlykhan@gmail.com>  
**Target Environment:** Production & Public Staging  
**Status:** Approved for Architectural Decision  

---

### 1. Executive Summary

The Bluebells Schooling System is an institutional-grade School ERP and Learning Management System comprising a Java 17 Spring Boot 3 REST API, a React 18 / Vite single-page application (SPA), and a relational MySQL database. 

This document evaluates genuinely free and low-cost cloud hosting providers to determine the optimal deployment topology. Our primary evaluation metric balances **zero financial cost** against **student data privacy, database integrity, persistent storage for student records, and operational reliability**.

---

### 2. Comprehensive Provider Evaluation Matrix

#### 2.1 Frontend Hosting Tier (React 18 SPA)

| Feature / Metric | Vercel (Hobby Free) | Cloudflare Pages (Free) | Netlify (Starter Free) | Render Static Site (Free) |
| :--- | :--- | :--- | :--- | :--- |
| **Bandwidth / Month** | 100 GB | **Unlimited** | 100 GB | 100 GB |
| **Build Minutes** | 6,000 min/mo | **Unlimited builds** | 300 min/mo | 500 min/mo |
| **Custom Domains & SSL** | Free auto Let's Encrypt | Free Universal SSL | Free auto Let's Encrypt | Free auto Let's Encrypt |
| **Global Edge CDN** | Anycast CDN (Fast) | Cloudflare Global Edge (Fastest in Pakistan) | Global CDN | Cloudflare-backed |
| **SPA Fallback Routing** | `vercel.json` rewrites | `_redirects` rule | `_redirects` rule | Rewrite rule in dashboard |
| **Credit Card Required?** | **No** | **No** | **No** | **No** |
| **Suitability Verdict** | **Highly Recommended** (Primary) | **Highly Recommended** (Co-Primary) | Suitable | Backup |

*Analysis:*  
- **Vercel** provides out-of-the-box integration with GitHub, preview deployments for pull requests, and sub-second asset delivery in South Asia.
- **Cloudflare Pages** offers an unbeatable unlimited bandwidth model and has native edge points of presence (PoP) in Karachi and Lahore, providing lowest latency for users in Hyderabad, Sindh.

---

#### 2.2 Backend Application Hosting Tier (Spring Boot 3 / JVM 17)

| Feature / Metric | Render (Free Web Service) | Koyeb (Free Hobby) | Oracle Cloud Always Free (Ampere A1) | Railway / Fly.io (Trial) |
| :--- | :--- | :--- | :--- | :--- |
| **RAM Allocation** | 512 MB | 512 MB | **Up to 24 GB (4 OCPUs)** | 512 MB (Usage credits expire) |
| **CPU Allocation** | 0.1 CPU (shared) | 0.1 CPU (shared) | **4 ARM v8.2 OCPUs** | Shared |
| **Inactivity Sleep / Spin-Down** | **Yes** (Sleeps after 15 min idle) | **Yes** (Free nano sleeps) | **No** (Runs 24/7/365 perpetually) | No (Stops when credits exhaust) |
| **Cold Start Penalty** | **45 to 60 seconds** | **30 to 45 seconds** | **0 seconds** (Always running) | N/A |
| **Disk Persistence** | **Ephemeral only** (Wiped on restart) | **Ephemeral only** | **200 GB Block Storage** (Persistent) | Ephemeral / Volume requires paid |
| **Dynamic Port Binding** | `$PORT` environment var | `$PORT` environment var | Fixed port (e.g. 8080 / Docker) | `$PORT` |
| **Credit Card Required?** | **No** | **No** | **Yes** ($1 authorization hold) | Yes / Credit limited |
| **Suitability Verdict** | **Suitable for Staging / Demo Only** | **Suitable for Staging / Demo Only** | **Production Grade (Recommended)** | Not recommended (Hidden costs) |

*Analysis:*  
- **Render & Koyeb Free Tiers:** While completely free without a credit card, the 512 MB RAM ceiling requires strict JVM tuning (`-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC`). More critically, **ephemeral disk storage wipes all uploaded student photos, assignment files, and generated PDF fee receipts on every sleep cycle or deployment**. Furthermore, a 50-second cold start causes unacceptable delays for parents paying school fees or teachers taking morning roll call.
- **Oracle Cloud Always Free:** Offers unmatched compute (up to 4 ARM Ampere cores and 24 GB RAM) and 200 GB persistent block storage. It is the only genuinely free tier capable of hosting the Bluebells backend, database, and persistent file storage 24/7 without cold starts.

---

#### 2.3 Managed Relational Database Tier (MySQL 8 Compatible)

| Feature / Metric | TiDB Cloud Serverless (Free) | Aiven for MySQL (Free Trial) | PlanetScale (Legacy Free) | Neon (PostgreSQL Free) |
| :--- | :--- | :--- | :--- | :--- |
| **Database Engine** | **MySQL 8.0 Protocol Compatible** | MySQL 8.0 | MySQL (Vitess) | PostgreSQL 16 |
| **Free Storage** | **5 GB free perpetually** | 30 days trial only | Free tier discontinued | 0.5 GB (Requires code rewrite) |
| **Compute Quota** | 50M Request Units (RU)/mo | Limited trial | Paid only ($39/mo) | Free compute hours limit |
| **Foreign Key Enforcement** | **Yes** (TiDB 6.6+ & Serverless) | Yes | Foreign keys restricted | Yes |
| **High Availability** | Multi-AZ Distributed consensus | Single node | Multi-AZ | Multi-AZ |
| **Credit Card Required?** | **No** | **No** (Trial only) | Yes | No |
| **Suitability Verdict** | **Best Free Cloud MySQL Option** | Unsuitable (Expires in 30 days) | Unsuitable (Paid only) | Incompatible (Postgres requires rewrite) |

*Analysis:*  
- **TiDB Cloud Serverless:** The premier no-credit-card MySQL 8-compatible cloud database. It supports 5 GB of persistent data, TLS connection strings, and full Flyway migration compatibility (`V1` to `V7`).
- *Important TiDB Caveats:*
  1. Distributed auto-increment: IDs are monotonic per node but non-sequential across transactions. The Bluebells JPA layer cleanly handles this as entities use surrogate primary keys.
  2. Foreign keys are enforced in modern TiDB Serverless (v7.1+), but sessions must ensure `foreign_key_checks=ON`.

---

#### 2.4 Object & Document Storage Tier

| Feature / Metric | Cloudflare R2 (Free Tier) | Backblaze B2 (Free Tier) | AWS S3 (Free Tier) | Local Persistent Disk (OCI) |
| :--- | :--- | :--- | :--- | :--- |
| **Storage Capacity** | **10 GB free forever** | 10 GB free forever | 5 GB for 12 months only | **200 GB included (OCI)** |
| **Egress (Download) Fees** | **$0.00 / GB (Zero egress fee)** | 3x stored data / day free | High egress charges | $0.00 within 10 TB/mo |
| **API Compatibility** | Amazon S3 API Compatible | S3 Compatible | S3 Native | POSIX File System |
| **Credit Card Required?** | **No** (Cloudflare account) | **No** | Yes | Yes (OCI account) |
| **Suitability Verdict** | **Best for Distributed Architecture** | Alternative | Unsuitable (Expires) | **Best for Unified VPS Architecture** |

*Analysis:*  
- If deploying on distributed PaaS (Render backend + Vercel frontend), **Cloudflare R2** is the only production-safe storage tier for student portraits and fee receipts because Render wipes local disks.
- If deploying on Oracle Cloud Always Free, the native 200 GB persistent block storage mount (`/var/bluebells/storage`) is optimal.

---

### 3. Architecture Topologies & Recommendations

```
+---------------------------------------------------------------------------------------------------+
| TOPOLOGY OPTION A: Distributed Zero-Cost Micro-Tier (No Credit Card Required)                     |
|                                                                                                   |
|  [Parents / Teachers / Admins]                                                                    |
|             |                                                                                     |
|             v                                                                                     |
|   +-----------------------+      API Calls      +--------------------------+                      |
|   | Vercel / Cloudflare   | ------------------> | Render Web Service       |                      |
|   | Frontend (React/Vite) | <------------------ | (Spring Boot 3, 512MB)   |                      |
|   +-----------------------+   CORS: Authorized  +--------------------------+                      |
|                                                            |               |                      |
|                                                JDBC/TLS    |               | S3 REST API          |
|                                                            v               v                      |
|                                                 +-------------+     +-------------------+         |
|                                                 | TiDB Cloud  |     | Cloudflare R2     |         |
|                                                 | Serverless  |     | Object Storage    |         |
|                                                 | (MySQL 8)   |     | (Student Files)   |         |
|                                                 +-------------+     +-------------------+         |
|                                                                                                   |
|  * Verdict: READY FOR STAGING / DEMONSTRATION ONLY.                                              |
|    Drawbacks: Render 50s cold-start on sleep; 512MB RAM pressure; requires R2 storage integration. |
+---------------------------------------------------------------------------------------------------+
```

```
+---------------------------------------------------------------------------------------------------+
| TOPOLOGY OPTION B: Oracle Cloud Always Free Unified Server (Recommended for Production)            |
|                                                                                                   |
|  [Parents / Teachers / Admins]                                                                    |
|             |                                                                                     |
|             v                                                                                     |
|   +-------------------------------------------------------------------------------------------+   |
|   | Oracle Cloud Infrastructure (OCI) - Ampere A1 Compute (4 OCPUs, 24 GB RAM, Free Forever)   |   |
|   |                                                                                           |   |
|   |   +-----------------------------------------------------------------------------------+   |   |
|   |   | Nginx Reverse Proxy (SSL Termination, HTTP/2, Let's Encrypt, Security Headers)     |   |   |
|   |   +-----------------------------------------------------------------------------------+   |   |
|   |             |                                                  |                          |   |
|   |             v                                                  v                          |   |
|   |   +-----------------------+                          +--------------------+               |   |
|   |   | Frontend Static Asset |                          | Spring Boot 3 JRE  |               |   |
|   |   | Container (Port 80)   |                          | Container (Port    |               |   |
|   |   +-----------------------+                          | 8080, 2 GB Heap)   |               |   |
|   |                                                                |                          |   |
|   |                                                                v Internal Docker Network  |   |
|   |                                                      +--------------------+               |   |
|   |                                                      | MySQL 8.0 Server   |               |   |
|   |                                                      | Container (1 GB)   |               |   |
|   |                                                      +--------------------+               |   |
|   |                                                                |                          |   |
|   |                                                                v Mounts                   |   |
|   |                                                      +--------------------+               |   |
|   |                                                      | 200 GB Persistent  |               |   |
|   |                                                      | Block Volume       |               |   |
|   |                                                      +--------------------+               |   |
|   +-------------------------------------------------------------------------------------------+   |
|                                                                                                   |
|  * Verdict: PRODUCTION READY. Zero cold starts, 24/7 uptime, 200 GB persistent storage,           |
|    dedicated private network, no student data routed through third-party multi-tenant PaaS disks.  |
+---------------------------------------------------------------------------------------------------+
```

---

### 4. Final Platform Decision

1. **Staging & Review Deployment:**  
   Option A (Vercel Frontend + Render Backend + TiDB Cloud Serverless) is suitable for non-sensitive feature demonstration, stakeholder preview, and automated PR checks.
2. **Production School Deployment:**  
   Option B (Oracle Cloud Always Free or Dedicated Institutional VPS) is the sole architecture that fulfills all Bluebells security, student privacy, zero-cold-start, and persistent document storage requirements.
