package com.smart.elderly.controller;

import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Room;
import com.smart.elderly.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/list")
    public Result<PageResult<Room>> list(@RequestParam Map<String, Object> params) {
        return roomService.pageList(params);
    }

    @GetMapping("/all")
    public Result<?> getAll() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    public Result<?> add(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return roomService.deleteRoom(id);
    }
}