package kelly.gerritz.mtastatus.service;

public enum StatusEnum {
    Delayed("delayed"),
    NotDelayed("not delayed");

    private String status;

    StatusEnum(String statusVal) {
        this.status = statusVal;
    }

    public String getStatus() {
        return status;
    }
}
