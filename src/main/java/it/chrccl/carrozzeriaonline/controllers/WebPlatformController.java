package it.chrccl.carrozzeriaonline.controllers;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.BulkGateComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.WebTask;
import it.chrccl.carrozzeriaonline.model.dao.*;
import it.chrccl.carrozzeriaonline.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class WebPlatformController {

    private final BulkGateComponent bulkGateComponent;

    private final OtpCheckService otpCheckService;

    private final TaskService taskService;

    private final BRCPerTaskService brcPerTaskService;

    private final TwilioComponent twilioComponent;

    private final AttachmentService attachmentService;

    private final RepairCenterService repairCenterService;

    @Autowired
    public WebPlatformController(BulkGateComponent bulkGateComponent, OtpCheckService otpCheckService,
                                 TaskService taskService, BRCPerTaskService brcPerTaskService, TwilioComponent twilioComponent,
                                 AttachmentService attachmentService, RepairCenterService repairCenterService) {
        this.bulkGateComponent = bulkGateComponent;
        this.otpCheckService = otpCheckService;
        this.taskService = taskService;
        this.brcPerTaskService = brcPerTaskService;
        this.twilioComponent = twilioComponent;
        this.attachmentService = attachmentService;
        this.repairCenterService = repairCenterService;
    }

    @PostMapping("/handleWebPlatformIncomingTask")
    public ResponseEntity<String> handleWebPlatformIncomingTask(@RequestBody WebTask webTask){
        Task task = webTask.getTask();
        List<Attachment> attachments = webTask.getAttachments();
        String userPhone = Constants.TWILIO_PREFIX + task.getUser().getMobilePhone();
        Optional<Task> optionalTask = taskService.findOngoingTaskByPhoneNumber(userPhone);
        if(optionalTask.isPresent()) return ResponseEntity.internalServerError()
                .body("Impossibile creare un nuovo incarico, ne hai già uno in corso sulla piattaforma Whatsapp.");

        task.getUser().setMobilePhone(userPhone);
        OtpCheck usedOTP = otpCheckService.findByOtpId(webTask.getOtpId());
        usedOTP.setTask(taskService.save(task));
        otpCheckService.saveOtpCheck(usedOTP);

        attachmentService.saveAll(attachments);
        RepairCenter rc = repairCenterService.findRepairCentersByCompanyNameIsLikeIgnoreCase(webTask.getCompanyName());
        if (rc != null) {
            brcPerTaskService.save(new BRCPerTask(new BRCPerTaskId(task, rc), task.getCreatedAt(), false));
            twilioComponent.sendWebMessage(new PhoneNumber(userPhone));
            return ResponseEntity.ok("Message processed successfully.");
        }else{
            return ResponseEntity.internalServerError().body("No Repair Center associated to the request");
        }
    }


    @GetMapping("/sendOtp/{fromNumber}")
    public ResponseEntity<String> sendOtp(@PathVariable("fromNumber") String fromNumber){
        String otpID = bulkGateComponent.sendOtp(Constants.ITALY_SMS_COUNTRY_CODE + fromNumber);
        if(otpID != null){
            otpCheckService.saveOtpCheck(new OtpCheck(LocalDateTime.now(), otpID, null, false));
            return ResponseEntity.ok(otpID);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error sending OTP");
        }
    }

    @GetMapping("/resendOtp/{otpID}")
    public ResponseEntity<String> resendOtp(@PathVariable("otpID") String otpID){
        String res = bulkGateComponent.resendOtp(otpID);
        if(res != null && res.equals(otpID)){
            otpCheckService.saveOtpCheck(new OtpCheck(LocalDateTime.now(), otpID, null, false));
            return ResponseEntity.ok(otpID);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error sending OTP");
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam("otpID") String otpID, @RequestParam("Code") String code){
        Boolean isValid = bulkGateComponent.verifyOtp(otpID, code);
        return ResponseEntity.ok(isValid);
    }

}
