package com.empmanage.service;

import com.empmanage.entity.Salary;
import com.empmanage.entity.SalaryDetail;
import com.empmanage.repository.SalaryDetailRepository;
import com.empmanage.repository.SalaryRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SalaryRepository salaryRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    public void initializeTwilio() {
        if (accountSid != null && !accountSid.trim().isEmpty() && 
            authToken != null && !authToken.trim().isEmpty()) {
            Twilio.init(accountSid.trim(), authToken.trim());
        }
    }

    @Transactional
    public void sendSalarySms(Long salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("Salary not found with id: " + salaryId));

        String employeeMobile = salary.getEmployee().getMobile();
        if (employeeMobile == null || employeeMobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee mobile number is not available");
        }

        String smsContent = formatSalarySms(salary);

        try {
            initializeTwilio();
            
            // Format recipient phone number (assuming Indian format, add +91 prefix)
            String formattedMobile = formatPhoneNumber(employeeMobile);
            
            // Format and validate Twilio phone number (From number)
            String formattedTwilioNumber = formatTwilioPhoneNumber(twilioPhoneNumber);
            
            if (formattedTwilioNumber == null || formattedTwilioNumber.isEmpty()) {
                throw new IllegalStateException("Twilio phone number not configured");
            }
            
            // Validate phone numbers are in E.164 format
            if (!formattedMobile.startsWith("+")) {
                throw new IllegalArgumentException("Recipient phone number must be in E.164 format (e.g., +91XXXXXXXXXX)");
            }
            
            if (!formattedTwilioNumber.startsWith("+")) {
                throw new IllegalStateException("Twilio phone number must be in E.164 format (e.g., +91XXXXXXXXXX)");
            }
            
            // Send SMS via Twilio
            Message message = Message.creator(
                    new PhoneNumber(formattedMobile),
                    new PhoneNumber(formattedTwilioNumber),
                    smsContent
            ).create();
            
            // Verify message was created successfully
            if (message.getSid() == null) {
                throw new RuntimeException("Failed to create SMS message");
            }

            // Update salary with SMS status
            salary.setSmsSent(true);
            salary.setSmsSentAt(java.time.LocalDateTime.now());
            salaryRepository.save(salary);
            
        } catch (com.twilio.exception.ApiException e) {
            // Handle Twilio-specific errors
            String errorMessage = "Twilio API Error: " + e.getMessage();
            if (e.getCode() == 21659) {
                errorMessage = "Country mismatch: The Twilio phone number country must match the recipient phone number country. " +
                              "Please ensure your Twilio number (" + formatTwilioPhoneNumber(twilioPhoneNumber) + ") " +
                              "is from the same country as the recipient, or use a Twilio number that supports international messaging.";
            }
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage(), e);
        }
    }

    private String formatSalarySms(Salary salary) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Salary Slip - ").append(getMonthName(salary.getMonth())).append(" ").append(salary.getYear()).append("\n");
        sb.append("Emp: ").append(salary.getEmployee().getName()).append("\n");
        sb.append("Base: Rs ").append(salary.getBaseSalary()).append("\n");
        
        // Get salary details sorted by date
        List<SalaryDetail> details = salaryDetailRepository.findBySalaryId(salary.getId())
                .stream()
                .sorted(Comparator.comparing(SalaryDetail::getDate))
                .collect(Collectors.toList());

        // Group by type and date
        if (!details.isEmpty()) {
            sb.append("\nDate-wise Details:\n");
            
            for (SalaryDetail detail : details) {
                String dateStr = detail.getDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                String typeStr = detail.getType().name();
                String amountStr = detail.getAmount().compareTo(BigDecimal.ZERO) >= 0 
                        ? "+Rs " + detail.getAmount() 
                        : "-Rs " + detail.getAmount().abs();
                
                sb.append(dateStr).append(": ").append(typeStr).append(" ").append(amountStr);
                if (detail.getDescription() != null && !detail.getDescription().isEmpty()) {
                    sb.append(" (").append(detail.getDescription()).append(")");
                }
                sb.append("\n");
            }
        }

        sb.append("\nOvertime: +Rs ").append(salary.getTotalOvertime()).append("\n");
        sb.append("Advances: -Rs ").append(salary.getTotalAdvances()).append("\n");
        sb.append("PF: -Rs ").append(salary.getPfDeduction()).append("\n");
        sb.append("Leaves: -Rs ").append(salary.getTotalLeaves()).append("\n");
        sb.append("Final: Rs ").append(salary.getFinalSalary()).append("\n");
        
        return sb.toString();
    }

    private String formatPhoneNumber(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty");
        }
        
        // Remove any whitespace
        String cleaned = mobile.trim();
        
        // If already in E.164 format (starts with +), return as is
        if (cleaned.startsWith("+")) {
            // Validate it has digits after +
            String digits = cleaned.substring(1).replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                return "+" + digits;
            }
        }
        
        // Remove any non-digit characters
        String digits = cleaned.replaceAll("[^0-9]", "");
        
        // If it's 10 digits, assume Indian number and add +91
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        
        // If it's 12 digits and starts with 91, add +
        if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }
        
        // If it already has country code (more than 10 digits), add +
        if (digits.length() > 10) {
            return "+" + digits;
        }
        
        // Default: assume Indian number
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        
        throw new IllegalArgumentException("Invalid phone number format: " + mobile);
    }
    
    private String formatTwilioPhoneNumber(String twilioNumber) {
        if (twilioNumber == null || twilioNumber.trim().isEmpty()) {
            return null;
        }
        
        // Remove any whitespace
        String cleaned = twilioNumber.trim();
        
        // If already in E.164 format (starts with +), return as is
        if (cleaned.startsWith("+")) {
            // Validate it has digits after +
            String digits = cleaned.substring(1).replaceAll("[^0-9]", "");
            if (digits.length() >= 10) {
                return "+" + digits;
            }
        }
        
        // Remove any non-digit characters
        String digits = cleaned.replaceAll("[^0-9]", "");
        
        // If it's 10 digits, assume Indian number and add +91
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        
        // If it's 12 digits and starts with 91, add +
        if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }
        
        // If it already has country code (more than 10 digits), add +
        if (digits.length() > 10) {
            return "+" + digits;
        }
        
        // Return as is if it already has +
        return cleaned.startsWith("+") ? cleaned : "+" + digits;
    }

    private String getMonthName(Integer month) {
        String[] months = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        }
        return "Month " + month;
    }
}

