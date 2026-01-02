package DTOS.Request;

import lombok.Data;

@Data
public class CustomerDataRequest {
    private String name;
    private String surname;
    private String phone;
}
