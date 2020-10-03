package kelly.gerritz.mtastatus.api;

import kelly.gerritz.mtastatus.service.MtaStatusService;
import kelly.gerritz.mtastatus.service.StatusEnum;
import kelly.gerritz.mtastatus.service.StatusTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MtaStatusApi {

    private final MtaStatusService mtaStatusService;

    @Autowired
    public MtaStatusApi(MtaStatusService mtaStatusService) {
        this.mtaStatusService = mtaStatusService;
    }

    @GetMapping(path = "/status/{line}")
    public ResponseEntity<String> status(@PathVariable("line") String line) {
        StatusEnum statusEnum = mtaStatusService.getStatuses().get(line.toUpperCase()).getStatusEnum();
        ResponseEntity<String> responseEntity;
        if (statusEnum == null) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            responseEntity = ResponseEntity.ok(String.format("Line %s is %s", line, statusEnum.getStatus()));
        }

        return responseEntity;
    }

    @GetMapping(path = "/uptime/{line}")
    public ResponseEntity<String> uptime(@PathVariable("line") String line) {
        StatusTime statusTime = mtaStatusService.getStatuses().get(line.toUpperCase());
        ResponseEntity<String> responseEntity;
        if (statusTime == null) {
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            Long uptime = mtaStatusService.getUptime();
            Double fraction = 1 - ((double) statusTime.getDelayedDuration() / uptime);
            responseEntity = ResponseEntity.ok(String.format("Line %s has been up %f time", line, fraction));
        }

        return responseEntity;
    }
}
