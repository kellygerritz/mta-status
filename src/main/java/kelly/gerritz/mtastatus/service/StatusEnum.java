package kelly.gerritz.mtastatus.service;

public enum StatusEnum {
    Delayed("Delayed"),
    NotDelayed("not Delayed");

    private String status;

    StatusEnum(String statusVal) {
        this.status = statusVal;
    }

    public String getStatus() {
        return status;
    }
}
