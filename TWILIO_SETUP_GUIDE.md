# 📱 Twilio SMS Configuration Guide

This guide will help you configure Twilio for SMS messaging in the Employee Salary Management System.

---

## 📋 Prerequisites

1. **Twilio Account**: You need a Twilio account (free trial available)
   - Sign up at: https://www.twilio.com/try-twilio
   - Free trial includes $15.50 credit for testing

2. **Twilio Phone Number**: A Twilio phone number to send SMS from
   - Available in Twilio Console after account setup

---

## 🔑 Step 1: Get Your Twilio Credentials

### 1.1 Log in to Twilio Console
- Go to: https://console.twilio.com/
- Sign in with your Twilio account

### 1.2 Find Your Account SID and Auth Token
1. Navigate to the **Dashboard** (home page)
2. You'll see:
   - **Account SID**: Starts with `AC...` (e.g., `ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`)
   - **Auth Token**: Click "Show" to reveal (e.g., `your_auth_token_here`)
3. Copy both values - you'll need them in the next step

### 1.3 Get Your Twilio Phone Number
1. Go to **Phone Numbers** → **Manage** → **Active Numbers**
2. You'll see your Twilio phone number (format: `+1234567890`)
3. If you don't have one:
   - Click **Get a number**
   - Choose a number with SMS capability
   - Click **Get Started** (free trial numbers available)

---

## ⚙️ Step 2: Configure Environment Variables

The application reads Twilio credentials from environment variables. You have **3 options**:

### **Option 1: Set Environment Variables (Recommended for Production)**

#### Windows (PowerShell):
```powershell
# Set environment variables (current session only)
$env:TWILIO_ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:TWILIO_AUTH_TOKEN = "your_auth_token_here"
$env:TWILIO_PHONE_NUMBER = "+1234567890"

# To make them permanent (system-wide):
[System.Environment]::SetEnvironmentVariable("TWILIO_ACCOUNT_SID", "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", "User")
[System.Environment]::SetEnvironmentVariable("TWILIO_AUTH_TOKEN", "your_auth_token_here", "User")
[System.Environment]::SetEnvironmentVariable("TWILIO_PHONE_NUMBER", "+1234567890", "User")
```

#### Windows (Command Prompt):
```cmd
setx TWILIO_ACCOUNT_SID "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
setx TWILIO_AUTH_TOKEN "your_auth_token_here"
setx TWILIO_PHONE_NUMBER "+1234567890"
```

#### Linux/Mac:
```bash
export TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
export TWILIO_AUTH_TOKEN="your_auth_token_here"
export TWILIO_PHONE_NUMBER="+1234567890"

# To make them permanent, add to ~/.bashrc or ~/.zshrc:
echo 'export TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"' >> ~/.bashrc
echo 'export TWILIO_AUTH_TOKEN="your_auth_token_here"' >> ~/.bashrc
echo 'export TWILIO_PHONE_NUMBER="+1234567890"' >> ~/.bashrc
```

### **Option 2: Create `.env` File (Development)**

Create a `.env` file in the project root:

```env
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=your_auth_token_here
TWILIO_PHONE_NUMBER=+1234567890
```

**Note**: You may need to install `dotenv` support or use a tool like `dotenv-cli` to load these variables.

### **Option 3: Direct Configuration in `application.properties` (Not Recommended)**

⚠️ **Security Warning**: Only use this for development/testing. Never commit credentials to version control!

Edit `src/main/resources/application.properties`:

```properties
# Twilio Configuration
twilio.account.sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
twilio.auth.token=your_auth_token_here
twilio.phone.number=+1234567890
```

**Important**: Add `application.properties` to `.gitignore` if you use this method!

---

## 🧪 Step 3: Test Your Configuration

### 3.1 Restart the Application

After setting environment variables, restart your Spring Boot application:

```powershell
# Stop the current application (Ctrl+C)
# Then restart:
.\gradlew.bat bootRun
```

### 3.2 Verify Configuration

1. **Check Application Logs**: When the app starts, look for any Twilio initialization errors
2. **Test SMS Sending**:
   - Log in to the application: http://localhost:3000
   - Navigate to **Salaries** page
   - Generate or view a salary
   - Click **Send SMS** button
   - Check if SMS is sent successfully

### 3.3 Check Twilio Console

1. Go to **Twilio Console** → **Monitor** → **Logs** → **Messaging**
2. You should see SMS delivery attempts and status

---

## 📝 Configuration Details

### Current Configuration Files

The application reads Twilio settings from:

**`src/main/resources/application.properties`**:
```properties
twilio.account.sid=${TWILIO_ACCOUNT_SID:}
twilio.auth.token=${TWILIO_AUTH_TOKEN:}
twilio.phone.number=${TWILIO_PHONE_NUMBER:}
```

The `${VARIABLE_NAME:}` syntax means:
- Read from environment variable `VARIABLE_NAME`
- If not found, use empty string (default)

### How It Works

1. **SmsService** (`src/main/java/com/empmanage/service/SmsService.java`):
   - Reads credentials using `@Value` annotations
   - Initializes Twilio SDK on startup
   - Formats salary slip as SMS text
   - Sends SMS via Twilio API

2. **TwilioConfig** (`src/main/java/com/empmanage/config/TwilioConfig.java`):
   - Initializes Twilio SDK when application starts
   - Only initializes if credentials are provided

3. **Phone Number Formatting**:
   - The service automatically formats Indian phone numbers (10 digits → +91XXXXXXXXXX)
   - For other countries, ensure phone numbers include country code

---

## 🔒 Security Best Practices

1. **Never Commit Credentials**:
   - ✅ Use environment variables
   - ✅ Use `.env` files (add to `.gitignore`)
   - ❌ Don't hardcode in `application.properties`
   - ❌ Don't commit credentials to Git

2. **Use Different Credentials for Dev/Prod**:
   - Development: Use Twilio trial account
   - Production: Use verified Twilio account with production phone number

3. **Rotate Credentials Regularly**:
   - Change Auth Token periodically
   - Revoke old tokens in Twilio Console

4. **Monitor Usage**:
   - Check Twilio Console for unexpected usage
   - Set up billing alerts

---

## 🐛 Troubleshooting

### Issue: "Twilio phone number not configured"
**Solution**: Make sure `TWILIO_PHONE_NUMBER` environment variable is set and includes `+` prefix (e.g., `+1234567890`)

### Issue: "Failed to send SMS: Authentication failed"
**Solution**: 
- Verify Account SID and Auth Token are correct
- Check for extra spaces in environment variables
- Ensure Auth Token is not expired (regenerate in Twilio Console if needed)

### Issue: "SMS not being sent"
**Solution**:
1. Check Twilio Console → Logs → Messaging for error details
2. Verify phone number format (must include country code)
3. Check if Twilio account has sufficient balance/credit
4. Verify phone number is verified (for trial accounts)

### Issue: "Invalid phone number format"
**Solution**: 
- Ensure employee mobile numbers in database are valid
- Phone numbers should be 10 digits (for India) or include country code
- The service auto-formats Indian numbers to `+91XXXXXXXXXX`

### Issue: Environment variables not being read
**Solution**:
1. Restart the application after setting environment variables
2. Verify variable names match exactly: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_PHONE_NUMBER`
3. Check if variables are set in the correct scope (User vs System)

---

## 📊 SMS Content Format

The application sends salary slips in this format:

```
Salary Slip - Jan 2026
Emp: John Doe
Base: Rs 50000

Date-wise Details:
01/01: ADVANCE -Rs 5000 (Advance taken)
15/01: LEAVE -Rs 2000 (Sick leave)

Overtime: +Rs 3000
Advances: -Rs 5000
PF: -Rs 2500
Leaves: -Rs 2000
Final: Rs 43500
```

---

## 💰 Twilio Pricing

- **Free Trial**: $15.50 credit included
- **SMS Pricing**: Varies by country
  - India: ~₹0.50 per SMS
  - USA: ~$0.0075 per SMS
- **Phone Number**: Free with trial, then ~$1/month
- Check current pricing: https://www.twilio.com/pricing

---

## ✅ Verification Checklist

- [ ] Twilio account created
- [ ] Account SID copied
- [ ] Auth Token copied
- [ ] Twilio phone number obtained
- [ ] Environment variables set
- [ ] Application restarted
- [ ] SMS test sent successfully
- [ ] SMS received on employee phone
- [ ] Twilio Console shows successful delivery

---

## 📞 Support

- **Twilio Documentation**: https://www.twilio.com/docs
- **Twilio Support**: https://support.twilio.com/
- **Application Issues**: Check application logs and Twilio Console logs

---

## 🎯 Quick Start (TL;DR)

1. Sign up at https://www.twilio.com/try-twilio
2. Get Account SID, Auth Token, and Phone Number from Twilio Console
3. Set environment variables:
   ```powershell
   $env:TWILIO_ACCOUNT_SID = "ACxxxxx"
   $env:TWILIO_AUTH_TOKEN = "your_token"
   $env:TWILIO_PHONE_NUMBER = "+1234567890"
   ```
4. Restart the application
5. Test by sending an SMS from the Salaries page

---

**Configuration Status**: ✅ Ready to configure  
**Last Updated**: January 2026





