package com.xingmao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingmao.entity.Delivery;
import com.xingmao.entity.DeliveryPerson;
import com.xingmao.service.DeliveryPersonService;
import com.xingmao.service.DeliveryService;
import com.xingmao.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @GetMapping("/list")
    public Result<?> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long deliveryPersonId) {
        Page<Delivery> page = deliveryService.getPage(pageNum, pageSize, status, deliveryPersonId);
        return Result.success(page);
    }

    @GetMapping("/persons")
    public Result<?> getDeliveryPersons() {
        List<DeliveryPerson> persons = deliveryPersonService.list();
        return Result.success(persons);
    }

    @GetMapping("/persons/available")
    public Result<?> getAvailablePersons() {
        List<DeliveryPerson> persons = deliveryPersonService.getAvailablePersons();
        return Result.success(persons);
    }

    @PostMapping("/assign")
    public Result<?> assignDelivery(@RequestBody Map<String, Object> assignReq) {
        Long orderId = Long.valueOf(assignReq.get("orderId").toString());
        String deliveryName = (String) assignReq.get("deliveryName");
        String deliveryPhone = (String) assignReq.get("deliveryPhone");
        Long deliveryPersonId = assignReq.get("deliveryPersonId") != null
                ? Long.valueOf(assignReq.get("deliveryPersonId").toString())
                : null;
        deliveryService.assignDelivery(orderId, deliveryName, deliveryPhone, deliveryPersonId);
        return Result.success("配送员已分配");
    }

    @PostMapping("/auto-assign/{orderId}")
    public Result<?> autoAssignDelivery(@PathVariable Long orderId) {
        deliveryService.autoAssignDelivery(orderId);
        return Result.success("自动分配成功");
    }

    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Object> statusReq) {
        Long id = Long.valueOf(statusReq.get("id").toString());
        Integer status = Integer.valueOf(statusReq.get("status").toString());
        deliveryService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    // ==================== 配送员管理 ====================

    @PostMapping("/person")
    public Result<?> addDeliveryPerson(@RequestBody DeliveryPerson person) {
        deliveryPersonService.save(person);
        return Result.success("配送员添加成功");
    }

    @PutMapping("/person")
    public Result<?> updateDeliveryPerson(@RequestBody DeliveryPerson person) {
        deliveryPersonService.updateById(person);
        return Result.success("配送员更新成功");
    }

    @DeleteMapping("/person/{id}")
    public Result<?> deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.removeById(id);
        return Result.success("配送员删除成功");
    }

    @GetMapping("/person/{id}")
    public Result<?> getDeliveryPerson(@PathVariable Long id) {
        DeliveryPerson person = deliveryPersonService.getById(id);
        return Result.success(person);
    }
}