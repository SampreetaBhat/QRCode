package Payment.QRCodePayment.Controller;


import Payment.QRCodePayment.Model.QrCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/api/V1/enq")
public class QrCodeController {

    @GetMapping("/form")
    public String showForm() {
        return "inquiry";
    }

    @PostMapping("/inquiry")
    public String handleFormSubmission(@RequestParam Map<String, String> requestParams, Model model) {
        String name = requestParams.get("name");
        String country = requestParams.get("country");
        String state = requestParams.get("state");
        String city = requestParams.get("city");
        String zip = requestParams.get("zip");
        long amount = Long.parseLong(requestParams.get("amount"));
        String address = country + ", " + state + ", " + city + ", " + zip;
        QrCode qrCode = new QrCode();
        qrCode.setName(name);
        qrCode.setAddress(address);
        qrCode.setAmount(amount);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {

            writer.write("Name: " + requestParams.get("name") + "\n");
            writer.write("Address: " + address + "\n");
            writer.write("Amount: " + requestParams.get("amount") + "\n");
            writer.write("---------------------------\n");

            BufferedImage bufferedImage=generateQRCodeImage(qrCode);
            File output=new File("src/main/resources/static/qrcode.jpg");
            if(output.exists()&&output.isFile())
            {
                boolean deleted = output.delete();
                System.out.println("Previous qrcode.jpg deleted");
                ImageIO.write(bufferedImage, "jpg", output);
            }
            else
            {
                ImageIO.write(bufferedImage, "jpg", output);
            }

            model.addAttribute("message", "your info saved and qrcode generated successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error saving your detail: " + e.getMessage());
        } catch (WriterException e) {
            model.addAttribute("message", "failed to generate qrcode: " + e.getMessage());
        }


        return "inquiry";
    }


    public static BufferedImage generateQRCodeImage(QrCode qrcode) throws WriterException {


        StringBuilder str=new StringBuilder();
        if((qrcode.getAmount()!=0))
        {
            str.append("upi://pay?pa=").append(qrcode.getAddress()).append("&pn=").append(qrcode.getName()).append("&am=").append(qrcode.getAmount()).append("&cu=INR");
        }
        else
        {
            str.append("upi://pay?pa=").append(qrcode.getAddress()).append("&pn=").append(qrcode.getName()).append("&cu=INR");
        }
        QRCodeWriter codeWriter=new QRCodeWriter();
        BitMatrix bitMatrix=codeWriter.encode(str.toString(), BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    @PostMapping("/details")
        @ResponseBody
        public String saveInfo(@RequestBody Map<String, String> requestBody) {
            String name = requestBody.get("name");
            String country = requestBody.get("country");
            String state = requestBody.get("state");
            String city = requestBody.get("city");
            String zip = requestBody.get("zip");
            long amount = Long.parseLong(requestBody.get("amount"));

            String address = country + ", " + state + ", " + city + ", " + zip;

            QrCode qrCode = new QrCode();
            qrCode.setName(name);
            qrCode.setAddress(address);
            qrCode.setAmount(amount);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
                writer.write("Name: " + qrCode.getName() + "\n");
                writer.write("Address: " + qrCode.getAddress() + "\n");
                writer.write("Amount: " + qrCode.getAmount() + "\n");
                writer.write("---------------------------\n");
            } catch (IOException e) {
                System.out.println("Error:"+ e.getMessage());
                return "Error saving your details.";
            }

            return "your details saved successfully!";
        }
    }


