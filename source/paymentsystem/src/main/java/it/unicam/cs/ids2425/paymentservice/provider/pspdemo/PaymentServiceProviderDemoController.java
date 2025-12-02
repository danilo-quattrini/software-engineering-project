package it.unicam.cs.ids2425.paymentservice.provider.pspdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unicam.cs.ids2425.location.LocationDTO;
import it.unicam.cs.ids2425.payment.Payment;
import it.unicam.cs.ids2425.payment.PaymentDTO;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.payment.referable.ReferableRepository;
import it.unicam.cs.ids2425.payment.service.PaymentService;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.UserDTO;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.Map;

@Controller
@RequestMapping("/psp/pspdemo")
public class PaymentServiceProviderDemoController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final ReferableRepository referableRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentServiceProviderDemoController(PaymentService paymentService,
                                                UserRepository userRepository,
                                                ReferableRepository referableRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.referableRepository = referableRepository;
    }

    @GetMapping
    public String showPaymentPage(@RequestParam("dto") String dtoJson, Model model) throws JsonProcessingException {
        PaymentDTO dto = buildPaymentDTO(dtoJson);
        DecimalFormat priceFormat = new DecimalFormat("#0.00");
        model.addAttribute("formattedAmount", priceFormat.format(dto.getReference().getAmount()));
        model.addAttribute("paymentDto", dto);
        model.addAttribute("dtoJson", dtoJson);
        return "providers/pspdemo/index";
    }

    @PostMapping
    public String processPayment(@RequestParam("dto") String dtoJson, Model model) throws JsonProcessingException {
        PaymentDTO dto = buildPaymentDTO(dtoJson);
        User payerEntity = userRepository.findByEmail(dto.getPayer().getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = dto.fromDTO();

        payment.setPayer(payerEntity);

        paymentService.save(payment);

        Referable reference = dto.getReference();

        if (reference.getClass().getSimpleName().equals("Cart")) {
            model.addAttribute("redirectUrl", "/products");
        } else if (reference.getClass().getSimpleName().equals("Event")) {
            model.addAttribute("redirectUrl", "/events");
        } else {
            model.addAttribute("redirectUrl", "/psp/order/confirmed");
        }
        return "providers/pspdemo/success";
    }

    private PaymentDTO buildPaymentDTO(String dtoJson) throws JsonProcessingException {
        Map<String, Object> map = mapper.readValue(dtoJson, Map.class);

        // Reference
        Object refIdObj = map.get("referenceId");
        if (refIdObj == null || refIdObj.toString().trim().isEmpty()) {
            throw new RuntimeException("Reference ID is missing");
        }
        Long referenceId = Long.parseLong(refIdObj.toString());
        Referable reference = referableRepository.findById(referenceId)
                .orElseThrow(() -> new RuntimeException("Reference not found"));

        Object payerEmailObj = map.get("payerEmail");
        if (payerEmailObj == null || payerEmailObj.toString().trim().isEmpty()) {
            throw new RuntimeException("Payer email is missing");
        }
        String payerEmail = payerEmailObj.toString();
        UserDTO payer = userRepository.findByEmail(payerEmail)
                .map(u -> new UserDTO().toDTO(u))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Location
        LocationDTO locationDTO = null;
        Object locObj = map.get("location");
        if (locObj instanceof Map<?, ?> locMap) {
            Double lat = null;
            Double lng = null;
            String address = null;

            Object latObj = locMap.get("lat");
            if (latObj != null && !latObj.toString().trim().isEmpty()) {
                try { lat = Double.parseDouble(latObj.toString()); } catch (NumberFormatException ignored) {}
            }

            Object lngObj = locMap.get("lng");
            if (lngObj != null && !lngObj.toString().trim().isEmpty()) {
                try { lng = Double.parseDouble(lngObj.toString()); } catch (NumberFormatException ignored) {}
            }

            Object addrObj = locMap.get("address");
            if (addrObj != null && !addrObj.toString().trim().isEmpty() && !"undefined".equals(addrObj.toString().trim())) {
                address = addrObj.toString().trim();
            }

            if (lat != null && lng != null) {
                locationDTO = new LocationDTO();
                locationDTO.setLat(lat);
                locationDTO.setLng(lng);
                locationDTO.setAddress(address);
            }
        }

        // Build DTO
        PaymentDTO dto = new PaymentDTO();
        dto.setReference(reference);
        dto.setPayer(payer);
        dto.setLocation(locationDTO);

        return dto;
    }
}
