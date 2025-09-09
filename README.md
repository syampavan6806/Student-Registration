# 1. Install and Configure MySQL on AWS EC2

## MySQL Installation on AWS Amazon Linux EC2

```bash
sudo wget https://dev.mysql.com/get/mysql80-community-release-el9-1.noarch.rpm
sudo dnf install -y mysql80-community-release-el9-1.noarch.rpm
sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2023
sudo dnf install -y mysql-community-server
sudo systemctl enable --now mysqld

# Configure MySQL to Allow Remote Connections
# This changes MySQL to listen on all interfaces, not just localhost.
echo -e "[mysqld]\nbind-address = 0.0.0.0" | sudo tee -a /etc/my.cnf
sudo systemctl restart mysqld

#Secure the MySQL Installation
sudo grep 'A temporary password' /var/log/mysqld.log
sudo mysql_secure_installation
```

👉 mysql_secure_installation will:
- Set a new root password
- Remove anonymous users
- Disallow remote root login
- Remove the test database

### Create Database and User

Login to MySQL:

```bash
# Use root password what ever u have set in above step
 mysql -u root -p
```

Inside MySQL:

```sql
CREATE DATABASE studentdb;
CREATE USER 'rushitech'@'%' IDENTIFIED BY 'Test@123';
GRANT ALL PRIVILEGES ON studentdb.* TO 'rushitech'@'%';
FLUSH PRIVILEGES;
```

This creates a dedicated user rushitech with password Test@123, allowed to connect from any host (%)

For production, replace % with your app server’s private IP for better security.

This installs **MySQL 8.0**, configures it for remote access, and creates the `studentdb` database with a dedicated user `rushitech`.

---
# 2. Install Tomcat to Deploy Application 

## Install Tomcat

### 1. Open the file:

```bash
vi installtomcat.sh
```

### 2. Add your bellow commands to your script file:

```bash
#!/bin/bash
yum update -y
yum install -y java-11-amazon-corretto
java -version
cd /opt
yum install wget -y
wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.109/bin/apache-tomcat-9.0.109.tar.gz
tar -xvzf apache-tomcat-9.0.109.tar.gz
mv apache-tomcat-9.0.109 tomcat
rm apache-tomcat-9.0.109.tar.gz
chown -R ec2-user:ec2-user /opt/tomcat
chmod +x /opt/tomcat/bin/startup.sh
chmod +x /opt/tomcat/bin/shutdown.sh
chmod +x /opt/tomcat/bin/catalina.sh

sudo tee /etc/systemd/system/tomcat.service > /dev/null <<EOF
[Unit]
Description=Apache Tomcat 9 Web Application Container
After=network.target

[Service]
Type=forking

User=ec2-user
Group=ec2-user

Environment="JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reexec
systemctl daemon-reload
systemctl start tomcat
systemctl enable tomcat
```

### 3. Execute Script to install Java & Tomcat:

```bash
chmod +x installTomcat.sh
sudo ./installTomcat.sh
```
---
## Configuring Tomcat to Use MySQL Environment Variables

This guide shows how to set **database connection variables** in `/etc/environment` and configure **systemd** so that Tomcat picks them up automatically.

---

### 1. Edit `/etc/environment`

Open the file:

```bash
sudo vi /etc/environment
```

Add your MySQL connection variables:

```ini
DB_HOST=10.0.0.126
DB_PORT=3306
DB_NAME=studentdb
DB_USER=rushitech
DB_PASSWORD=Test@123
```

Make sure the format is `KEY=value` (no `export`, no quotes).

---

### 2. Configure Tomcat systemd service

Open your Tomcat service unit (service name may be `tomcat`, `tomcat9`, or similar):

```bash
sudo vi /etc/systemd/system/tomcat.service
```

Inside the `[Service]` section, add:

```ini
[Service]
EnvironmentFile=-/etc/environment
```

The leading `-` tells systemd to ignore errors if the file is missing.

---

### 3. Reload and restart Tomcat

Apply the systemd changes:

```bash
sudo systemctl daemon-reload
sudo systemctl restart tomcat
```

Check status:

```bash
sudo systemctl status tomcat
```

✅ Done! Now Tomcat will always load your database connection settings from `/etc/environment`.


✅ Final Notes
- Make sure Security Groups for your EC2 instance allow inbound MySQL (3306) from your app host.
- Configuring Tomcat to Use MySQL Environment Variables
---

### Build an application package(war) file and deploy to tomcat from Build Server Or CI/CD Server