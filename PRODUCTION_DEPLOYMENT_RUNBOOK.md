# BLUEBELLS SCHOOLING SYSTEM
## Module H — Production Deployment Runbook for System Administrators

**Document Reference:** `BSS-DEP-010`  
**Institution:** Bluebells Schooling System, Hyderabad, Sindh, Pakistan  
**Author:** Ahmed Ali Khan <medlykhan@gmail.com>  
**Status:** Official Operational Standard  

---

### 1. Pre-Deployment Preparation & Checklist

Before initiating any production deployment, the System Administrator must ensure:
- [ ] Source code branch `main` is clean, tested, and passing all CI checks.
- [ ] Institutional domain name is registered (e.g., `bluebells.edu.pk` or `bluebellsschool.pk`).
- [ ] DNS nameserver access is available to create `A` and `CNAME` records.
- [ ] Strong secrets have been generated using CSPRNG tools (see [`PRODUCTION_ENVIRONMENT_CONFIGURATION.md`](file:///Users/ahmkhan/Desktop/portal/PRODUCTION_ENVIRONMENT_CONFIGURATION.md)).

---

### 2. Path A: Distributed Cloud Stack Deployment (Vercel + Render + TiDB)

This zero-cost multi-cloud topology decouples the frontend, backend, and database layers without upfront hosting fees.

#### Step 1: Provision TiDB Cloud Serverless (Database)
1. Navigate to [TiDB Cloud](https://tidbcloud.com/) and register using institutional email.
2. Click **Create Cluster** -> Select **Serverless** (Free forever, 5 GB storage).
3. Select Cloud Provider **AWS** or **GCP** in region **Singapore (ap-southeast-1)** or **Mumbai (ap-south-1)** for lowest network latency to Pakistan.
4. Set Cluster Name: `bluebells-prod-db`.
5. Once created, click **Connect** -> Choose **General Connection String (MySQL)**.
6. Note the credentials:
   - Host: `gateway01.ap-southeast-1.prod.aws.tidbcloud.com` (example)
   - Port: `4000`
   - User: `[prefix].root`
   - Password: `[generated_password]`
7. Open MySQL client and verify connection with SSL enabled:
   ```bash
   mysql --host=<tidb-host> --port=4000 -u '<tidb-user>' -p'<tidb-password>' --ssl-mode=VERIFY_IDENTITY
   ```
8. Initialize the database schema:
   ```sql
   CREATE DATABASE bluebells_school_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   SET @@global.foreign_key_checks = ON;
   ```

#### Step 2: Deploy Backend to Render (Web Service)
1. Log in to [Render](https://render.com/).
2. Click **New +** -> **Web Service**.
3. Connect your GitHub repository (`Ahmedalikhan77/portal`).
4. Configure service settings:
   - **Name:** `bluebells-backend-api`
   - **Region:** Singapore or Frankfurt
   - **Branch:** `main`
   - **Root Directory:** *Leave blank*
   - **Runtime:** `Docker`
   - **Dockerfile Path:** `backend/Dockerfile`
   - **Instance Type:** `Free` (512 MB RAM, 0.1 CPU)
5. Add Environment Variables under **Advanced**:
   ```ini
   SPRING_PROFILES_ACTIVE=prod
   DB_HOST=<tidb-host>
   DB_PORT=4000
   DB_NAME=bluebells_school_db
   DB_USER=<tidb-user>
   DB_PASSWORD=<tidb-password>
   JWT_SECRET=<your_generated_64_char_secret>
   INITIAL_ADMIN_USERNAME=admin
   INITIAL_ADMIN_PASSWORD=<your_initial_admin_password>
   INITIAL_ADMIN_EMAIL=admin@bluebells.edu.pk
   CORS_ALLOWED_ORIGINS=https://portal.bluebells.edu.pk,https://bluebells-portal.vercel.app
   JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC
   ```
6. Set **Health Check Path:** `/actuator/health`.
7. Click **Create Web Service**. Wait for the Docker build to complete and verify the service reports **Live**.
8. Note your public backend URL: `https://bluebells-backend-api.onrender.com`.

#### Step 3: Deploy Frontend to Vercel (Edge CDN)
1. Log in to [Vercel](https://vercel.com/).
2. Click **Add New...** -> **Project**.
3. Import the GitHub repository (`Ahmedalikhan77/portal`).
4. Configure the project:
   - **Framework Preset:** `Vite`
   - **Root Directory:** Click **Edit** and select `frontend`.
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
5. Under **Environment Variables**, add:
   - `VITE_API_URL` = `https://bluebells-backend-api.onrender.com`
6. Click **Deploy**. Vercel will build and publish your SPA to a global edge network.

---

### 3. Path B: Oracle Cloud Always Free Unified Server Deployment

Recommended for production school operations requiring zero cold starts, 24/7 continuous uptime, and persistent local storage.

#### Step 1: Provision Oracle Cloud Compute Instance
1. In the OCI Console, navigate to **Compute** -> **Instances** -> **Create Instance**.
2. **Name:** `bluebells-production-server`.
3. **Image:** Ubuntu 22.04 LTS (Canonical Ubuntu).
4. **Shape:** Click **Change Shape** -> Select **Ampere (ARM)** -> `VM.Standard.A1.Flex`.
   - Number of OCPUs: `2` (Always Free)
   - Amount of Memory: `12 GB` (Always Free)
5. **Networking:** Select existing VCN or Create New VCN with Internet Gateway.
6. **SSH Keys:** Download your private key and upload your public SSH key.
7. Click **Create**.

#### Step 2: Configure Firewall & DNS
1. Under **Networking** -> **Virtual Cloud Networks** -> Click your VCN -> **Default Security List**:
   - Ingress Rule 1: TCP Port `80` (HTTP) from `0.0.0.0/0`.
   - Ingress Rule 2: TCP Port `443` (HTTPS) from `0.0.0.0/0`.
2. Under **Networking** -> **IP Management** -> **Reserved Public IPs**:
   - Allocate a Reserved Public IP and attach it to your compute instance VNIC.
3. In your Domain DNS Registrar, create DNS records:
   - `A` Record: `portal.bluebells.edu.pk` -> Point to Reserved Public IP.
   - `A` Record: `api.bluebells.edu.pk` -> Point to Reserved Public IP.

#### Step 3: Server Setup & Application Launch
SSH into the instance:
```bash
ssh -i bluebells_oci.key ubuntu@<RESERVED_PUBLIC_IP>

# 1. Update system and configure host firewall
sudo apt update && sudo apt upgrade -y
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT 6 -m state --state NEW -p tcp --dport 443 -j ACCEPT
sudo apt install -y iptables-persistent
sudo netfilter-persistent save

# 2. Install Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker

# 3. Clone Repository
sudo mkdir -p /opt/bluebells
sudo chown -R $USER:$USER /opt/bluebells
git clone https://github.com/Ahmedalikhan77/portal.git /opt/bluebells
cd /opt/bluebells

# 4. Populate Production Environment File
cp .env.example .env
nano .env # Enter DB passwords, JWT secret, and initial admin credentials

# 5. Launch Production Stack
docker compose -f docker-compose.prod.yml up -d --build

# 6. Verify Container Health
docker compose -f docker-compose.prod.yml ps
```

#### Step 4: Configure Let's Encrypt SSL
Install Certbot on the host to terminate SSL for Nginx:
```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot certonly --standalone -d portal.bluebells.edu.pk -d api.bluebells.edu.pk \
  --non-interactive --agree-tos -m admin@bluebells.edu.pk
```

---

### 4. Post-Deployment Verification & First Login

Once services are running:

1. **Verify Actuator Health Check:**
   ```bash
   curl -i https://<backend-url>/actuator/health
   # Expected: HTTP/1.1 200 OK
   # Body: {"status":"UP"}
   ```

2. **Verify CORS Headers:**
   ```bash
   curl -i -X OPTIONS https://<backend-url>/api/v1/auth/login \
     -H "Origin: https://portal.bluebells.edu.pk" \
     -H "Access-Control-Request-Method: POST"
   # Expected: HTTP/1.1 200 OK
   # Header: Access-Control-Allow-Origin: https://portal.bluebells.edu.pk
   ```

3. **First-Time Administrator Login:**
   - Open browser to `https://portal.bluebells.edu.pk/login`.
   - Enter initial administrator credentials (`admin` / `$INITIAL_ADMIN_PASSWORD`).
   - Navigate immediately to **Settings / Security** and verify two-factor authentication or update credentials if desired.
   - Navigate to **School Profile** and verify Hyderabad, Sindh, Pakistan regional settings (Currency: `PKR`, Timezone: `Asia/Karachi`).
