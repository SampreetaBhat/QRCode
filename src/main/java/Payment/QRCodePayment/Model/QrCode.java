package Payment.QRCodePayment.Model;


import lombok.Data;

@Data
public class QrCode {
    private String name;
    private String address;
    private long amount;
}
