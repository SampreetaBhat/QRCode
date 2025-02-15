package Payment.QRCodePayment.Controller;


import Payment.QRCodePayment.Model.QrCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
            String address = requestParams.get("country") + ", " + requestParams.get("state") + ", " +
                    requestParams.get("city") + ", " + requestParams.get("zip");

            writer.write("Name: " + requestParams.get("name") + "\n");
            writer.write("Address: " + address + "\n");
            writer.write("Amount: " + requestParams.get("amount") + "\n");
            writer.write("---------------------------\n");

            model.addAttribute("message", "your details saved successfully!");
        } catch (IOException e) {
            model.addAttribute("message", "Error saving your details: " + e.getMessage());
        }

        return "inquiry";
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


