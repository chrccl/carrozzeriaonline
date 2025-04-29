package it.chrccl.carrozzeriaonline.controllers;

import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.services.RepairCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class ReportController {

    private final IOComponent ioComponent;

    private final RepairCenterService repairCenterService;

    @Autowired
    public ReportController(IOComponent ioComponent, RepairCenterService repairCenterService) {
        this.ioComponent = ioComponent;
        this.repairCenterService = repairCenterService;
    }

    @GetMapping("/addToReportNoleggioSquillace/{ragioneSocialeCarrozzeria}")
    public ResponseEntity<Void> addToReportNoleggioSquillace(@PathVariable String ragioneSocialeCarrozzeria) {
        RepairCenter rc = repairCenterService
                .findRepairCentersByCompanyNameIsLikeIgnoreCase(ragioneSocialeCarrozzeria);
        ioComponent.addToReportNoleggioSquillace(rc);
        URI instagram = URI.create(Constants.SQUILLACE_INSTAGRAM_PAGE);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(instagram)
                .build();
    }
}
