package kelly.gerritz.mtastatus.service;

public class StatusTime {
    private StatusEnum statusEnum;
    private Long delayedDuration;

    public StatusTime(StatusEnum statusEnum, Long delayedDuration) {
        this.statusEnum = statusEnum;
        this.delayedDuration = delayedDuration;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }

    public Long getDelayedDuration() {
        return delayedDuration;
    }
}
