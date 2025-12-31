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
import java.time.LocalDate;
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
        if (accountSid != null && !accountSid.isEmpty() && 
            authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Transactional
    public void sendSalarySms(Long salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new IllegalArgumentException("Salary not found with id: " + salaryId));

        String employeeMobile = salary.getEmployee().getMobile();
        String smsContent = formatSalarySms(salary);

        try {
            initializeTwilio();
            
            // Format phone number (assuming Indian format, add +91 prefix)
            String formattedMobile = formatPhoneNumber(employeeMobile);
            
            if (twilioPhoneNumber != null && !twilioPhoneNumber.isEmpty()) {
                Message message = Message.creator(
                        new PhoneNumber(formattedMobile),
                        new PhoneNumber(twilioPhoneNumber),
                        smsContent
                ).create();

                // Update salary with SMS status
                salary.setSmsSent(true);
                salary.setSmsSentAt(java.time.LocalDateTime.now());
                salaryRepository.save(salary);
            } else {
                throw new IllegalStateException("Twilio phone number not configured");
            }
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
        // Remove any non-digit characters
        String digits = mobile.replaceAll("[^0-9]", "");
        
        // If it's 10 digits, assume Indian number and add +91
        if (digits.length() == 10) {
            return "+91" + digits;
        }
        
        // If it already has country code, just add +
        if (digits.length() > 10 && !digits.startsWith("+")) {
            return "+" + digits;
        }
        
        return digits;
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

