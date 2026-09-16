package com.wisp.app.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.wisp.app.entity.Pagos;
import com.wisp.app.repository.AveriaRepository;
import com.wisp.app.repository.InstalacionRepository;
import com.wisp.app.repository.PagosRepository;
import com.wisp.app.servicios.ClienteService;
import com.wisp.app.servicios.PagosService;

@Controller
public class ViewController {

        private final ClienteService clienteService;
        private final AveriaRepository averiaRepo;
        private final PagosRepository pagosRepo;
        private final PagosService pagosService;
        private final InstalacionRepository instalacionRepo;

        public ViewController(
                        ClienteService clienteService,
                        AveriaRepository averiaRepo,
                        PagosRepository pagosRepo,
                        PagosService pagosService,
                        InstalacionRepository instalacionRepo) {

                this.clienteService = clienteService;
                this.averiaRepo = averiaRepo;
                this.pagosRepo = pagosRepo;
                this.pagosService = pagosService;
                this.instalacionRepo = instalacionRepo;
        }

        @GetMapping({ "/", "/dashboard" })
        public String dashboard(Model model) {
                // actualizamos los pagos vencidos
                pagosService.actualizarPagosVencidos();

                // CLIENTES
                model.addAttribute(
                                "totalClientes",
                                clienteService.totalClientes());

                model.addAttribute(
                                "clientesNuevos",
                                clienteService.buscarPorEstado("Nuevo").size());

                model.addAttribute(
                                "clientesPendientes",
                                clienteService.buscarPorEstado("Pendiente").size());

                model.addAttribute(
                                "clientesSuspendidos",
                                clienteService.buscarPorEstado("Suspendido").size());

                model.addAttribute(
                                "clientesActivos",
                                clienteService.buscarPorEstado("Activo").size());

                model.addAttribute(
                                "clientesDeudores",
                                clienteService.buscarPorEstado("Suspendido").size());

                // AVERÍAS
                model.addAttribute(
                                "totalAverias",
                                averiaRepo.count());

                model.addAttribute(
                                "averiasAtendidas",
                                averiaRepo.findByEstado("Atendido").size());

                model.addAttribute(
                                "averiasPendientes",
                                averiaRepo.findByEstado("Sin atender").size());

                // PAGOS
                List<Pagos> pagos = pagosRepo.findAll();

                double totalPagos = pagos.stream()
                                .filter(p -> p != null)
                                .mapToDouble(p -> p.getMonto())
                                .sum();

                int pagosRecibidos = pagosRepo
                                .findByEstado("Pagado")
                                .size();

                int pagosPendientes = pagosRepo
                                .findByEstado("Pendiente")
                                .size();

                int pagosVencidos = pagosRepo
                                .findByEstado("Vencido")
                                .size();

                model.addAttribute(
                                "pagosVencidos",
                                pagosVencidos);

                model.addAttribute(
                                "totalPagos",
                                totalPagos);

                model.addAttribute(
                                "pagosRecibidos",
                                pagosRecibidos);

                model.addAttribute(
                                "pagosPendientes",
                                pagosPendientes);

                // instalaciones
                model.addAttribute(
                                "totalInstalaciones",
                                instalacionRepo.count());

                model.addAttribute(
                                "instalacionesPendientes",
                                instalacionRepo.findByEstado("Pendiente").size());

                model.addAttribute(
                                "instalacionesAtendidas",
                                instalacionRepo.findByEstado("Atendido").size());

                model.addAttribute(
                                "instalacionesAnuladas",
                                instalacionRepo.findByEstado("Anulado").size());

                // alertas
                model.addAttribute(
                                "alertaClientesSuspendidos",
                                clienteService.buscarPorEstado("Suspendido").size());

                model.addAttribute(
                                "alertaPagosVencidos",
                                pagosVencidos);

                model.addAttribute(
                                "alertaInstalacionesPendientes",
                                instalacionRepo.findByEstado("Pendiente").size());

                model.addAttribute(
                                "alertaAveriasPendientes",
                                averiaRepo.findByEstado("Sin atender").size());

                // actividades recientes
                model.addAttribute(
                                "ultimasInstalaciones",
                                instalacionRepo.findTop5ByOrderByFechaAtencionDesc());

                model.addAttribute(
                                "ultimosPagos",
                                pagosRepo.findTop5ByOrderByFechaPagoDesc());

                model.addAttribute(
                                "ultimasAverias",
                                averiaRepo.findTop5ByOrderByFechaAtencionDesc());

                return "dashboard";
        }
}