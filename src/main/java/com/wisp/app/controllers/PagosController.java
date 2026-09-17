package com.wisp.app.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wisp.app.entity.Pagos;
import com.wisp.app.repository.PagosRepository;
import com.wisp.app.servicios.PagosService;

@Controller
@RequestMapping("/pagos")
public class PagosController {

    private final PagosService pagosService;
    private final PagosRepository pagosRepository;

    public PagosController(PagosService pagosService, PagosRepository pagosRepository) {
        this.pagosService = pagosService;
        this.pagosRepository = pagosRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Integer anio,
                         @RequestParam(required = false) Integer mes,
                         Model model) {



            if(anio == null){
                anio = LocalDate.now().getYear();
            }

            if(mes == null){
                mes = LocalDate.now().getMonthValue();
            }


            List<Pagos> pagos = pagosRepository
                    .findByAnioAndMes(anio, mes);


            List<Integer> anios = new ArrayList<>();

                for(int i = anio - 3; i <= anio + 3; i++){
                    anios.add(i);
                }


                model.addAttribute("pagos", pagos);
                model.addAttribute("anios", anios);
                model.addAttribute("anioSeleccionado", anio);
                model.addAttribute("mesSeleccionado", mes);


                return "pagos";
    }

    @GetMapping("/generar")
    public String generarPagos() {

        pagosService.generarPagosMensuales();
        System.out.println("ENTRANDO A GENERAR PAGOS EN EL CONTRLADOR ");

        return "redirect:/pagos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Pagos pago = pagosService.buscarPorId(id);
        model.addAttribute("pago", pago);

        return "pagos-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Pagos pago) {

        pagosService.guardar(pago);

        return "redirect:/pagos";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {

        model.addAttribute(
                "pago",
                pagosService.buscarPorId(id));

        return "pagos-info";
    }
}