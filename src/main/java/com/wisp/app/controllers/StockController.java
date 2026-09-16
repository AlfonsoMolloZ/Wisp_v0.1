package com.wisp.app.controllers;

import com.wisp.app.dto.StockDto;
import com.wisp.app.entity.Stock;
import com.wisp.app.repository.StockRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockRepository stockRepo;

    public StockController(StockRepository stockRepo) {
        this.stockRepo = stockRepo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("stocks", stockRepo.findAll());
        return "stock";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("stock", new StockDto());
        return "stock-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute StockDto dto) {
        Stock s = toEntity(dto);
        stockRepo.save(s);
        return "redirect:/stock";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Stock s = stockRepo.findById(id).orElseThrow();
        model.addAttribute("stock", toDto(s));
        return "stock-form";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {

        Stock stock = stockRepo.findById(id).orElseThrow();

        model.addAttribute("stock", stock);

        return "stock-info";
    }

    private Stock toEntity(StockDto dto) {
        if (dto == null)
            return null;
        Stock s = new Stock();
        s.setId(dto.getId());
        s.setEquipo(dto.getEquipo());
        s.setMarca(dto.getMarca());
        s.setModelo(dto.getModelo());
        s.setCantidad(dto.getCantidad());
        s.setEstado(dto.getEstado());
        s.setUltimoIngreso(dto.getUltimoIngreso());
        return s;
    }

    private StockDto toDto(Stock s) {
        StockDto dto = new StockDto();
        dto.setId(s.getId());
        dto.setEquipo(s.getEquipo());
        dto.setMarca(s.getMarca());
        dto.setModelo(s.getModelo());
        dto.setCantidad(s.getCantidad());
        dto.setEstado(s.getEstado());
        dto.setUltimoIngreso(s.getUltimoIngreso());
        return dto;
    }
}
