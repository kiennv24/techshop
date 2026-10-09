package dashboard.admin.controller;

import dashboard.admin.entity.Order;
import dashboard.admin.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return "redirect:/admin/orders"; 
        }
        model.addAttribute("order", order);
        return "admin/order-detail"; 
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderRepository.findById(id).orElse(new Order()));
        return "admin/order-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Order order) {
        // LƯU Ý: Vì form chỉnh sửa chỉ gửi lên trạng thái, địa chỉ, sđt... 
        // Nên để tránh việc Hibernate ghi đè làm mất thông tin ngày tạo (createdAt) hoặc thông tin khách hàng (user),
        // Chúng ta nên lấy thực thể cũ trong DB ra để cập nhật các trường thay đổi thay vì save đè trực tiếp.
        if (order.getId() != null) {
            Order existingOrder = orderRepository.findById(order.getId()).orElse(null);
            if (existingOrder != null) {
                existingOrder.setStatus(order.getStatus());
                existingOrder.setShippingAddress(order.getShippingAddress());
                existingOrder.setPhoneNumber(order.getPhoneNumber());
                orderRepository.save(existingOrder);
                return "redirect:/admin/orders";
            }
        }
        
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }

    // ĐÃ THÊM: Hàm xử lý thay đổi trạng thái nhanh trực tiếp từ ô select-option của trang order-detail.html
    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return "redirect:/admin/orders/" + id; // Cập nhật xong tự động quay lại chính trang chi tiết đơn hàng đó
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/orders";
    }
}