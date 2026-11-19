package com.wisp.app.config;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wisp.app.entity.Averia;
import com.wisp.app.entity.Cliente;
import com.wisp.app.entity.Instalacion;
import com.wisp.app.entity.Pagos;
import com.wisp.app.entity.Planes;
import com.wisp.app.entity.RedWan;
import com.wisp.app.entity.Stock;
import com.wisp.app.repository.AveriaRepository;
import com.wisp.app.repository.ClienteRepository;
import com.wisp.app.repository.InstalacionRepository;
import com.wisp.app.repository.PagosRepository;
import com.wisp.app.repository.PlanesRepository;
import com.wisp.app.repository.RedWanRepository;
import com.wisp.app.repository.StockRepository;

@Component
@ConditionalOnProperty(name = "WISP_SEED_DEMO", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final PlanesRepository planesRepository;
    private final RedWanRepository redWanRepository;
    private final ClienteRepository clienteRepository;
    private final InstalacionRepository instalacionRepository;
    private final AveriaRepository averiaRepository;
    private final PagosRepository pagosRepository;
    private final StockRepository stockRepository;

    public DemoDataSeeder(
            PlanesRepository planesRepository,
            RedWanRepository redWanRepository,
            ClienteRepository clienteRepository,
            InstalacionRepository instalacionRepository,
            AveriaRepository averiaRepository,
            PagosRepository pagosRepository,
            StockRepository stockRepository) {
        this.planesRepository = planesRepository;
        this.redWanRepository = redWanRepository;
        this.clienteRepository = clienteRepository;
        this.instalacionRepository = instalacionRepository;
        this.averiaRepository = averiaRepository;
        this.pagosRepository = pagosRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            log.info("Datos de prueba ya presentes: se omite el seeder.");
            return;
        }

        LocalDate hoy = LocalDate.now();

        Planes hogar = plan("Hogar 30 Mbps", 59.90, 30);
        Planes hogarPlus = plan("Hogar 60 Mbps", 89.90, 60);
        plan("Empresarial 100 Mbps", 149.90, 100);

        RedWan norte = red("Red Norte", "192.168.10.0", "255.255.255.0", "192.168.10.1");
        RedWan sur = red("Red Sur", "192.168.20.0", "255.255.255.0", "192.168.20.1");

        Cliente luis = cliente("Luis", "Ramirez Torres", "45123789", "987111222",
                "Av. Los Pinos 123", "luis.ramirez@example.com", "Activo", "192.168.10.2", hogar, norte);
        Cliente maria = cliente("Maria", "Fernandez Rojas", "41234567", "987222333",
                "Jr. Las Flores 456", "maria.fernandez@example.com", "Activo", "192.168.10.3", hogarPlus, norte);
        Cliente carlos = cliente("Carlos", "Mendoza Silva", "47891234", "987333444",
                "Calle Lima 789", "carlos.mendoza@example.com", "Nuevo", "192.168.10.4", hogar, norte);
        Cliente ana = cliente("Ana", "Gutierrez Paredes", "45678912", "987444555",
                "Av. Central 321", "ana.gutierrez@example.com", "Pendiente", "192.168.20.2", hogar, sur);
        Cliente pedro = cliente("Pedro", "Salazar Vega", "40112233", "987555666",
                "Jr. Amazonas 654", "pedro.salazar@example.com", "Suspendido", "192.168.20.3", hogarPlus, sur);
        pedro.setFechaSuspension(LocalDateTime.now().minusDays(10));
        clienteRepository.save(pedro);

        instalacion(luis, "Nueva instalacion", "Av. Los Pinos 123", "Atendido",
                LocalDateTime.now().minusDays(40), true, 120.0, "Efectivo");
        instalacion(ana, "Nueva instalacion", "Av. Central 321", "Pendiente",
                LocalDateTime.now().minusDays(2), null, null, null);
        instalacion(carlos, "Reubicacion", "Calle Lima 789", "Anulado",
                LocalDateTime.now().minusDays(15), false, null, null);

        Averia averia1 = averia("AV-001", "Cliente reporta intermitencia en el servicio",
                "Av. Los Pinos 123", "Atendido", luis,
                LocalDateTime.now().minusDays(12), LocalDateTime.now().minusDays(11));
        averia("AV-002", "Cliente sin conexion desde la manana",
                "Av. Central 321", "Sin atender", ana,
                LocalDateTime.now().minusDays(1), null);

        pago(luis, 59.90, hoy.minusMonths(1).withDayOfMonth(5), "Pagado", "Efectivo");
        pago(maria, 89.90, hoy.withDayOfMonth(5), "Pendiente", null);
        pago(pedro, 89.90, hoy.minusMonths(2).withDayOfMonth(5), "Vencido", null);

        stock("Router", "TP-Link", "Archer C6", 12, "Disponible", hoy.minusDays(20));
        stock("Antena", "Ubiquiti", "LiteBeam AC", 8, "Disponible", hoy.minusDays(15));
        stock("Switch", "MikroTik", "CRS326", 3, "Bajo stock", hoy.minusDays(7));

        log.info("Datos de prueba cargados: {} planes, {} redes, {} clientes, {} instalaciones, "
                        + "{} averias, {} pagos, {} items de stock.",
                planesRepository.count(), redWanRepository.count(), clienteRepository.count(),
                instalacionRepository.count(), averiaRepository.count(), pagosRepository.count(),
                stockRepository.count());
    }

    private Planes plan(String nombre, double precio, int velocidad) {
        Planes plan = new Planes();
        plan.setNombre(nombre);
        plan.setPrecio(precio);
        plan.setVelocidad(velocidad);
        return planesRepository.save(plan);
    }

    private RedWan red(String nombre, String direccion, String mascara, String gateway) {
        RedWan red = new RedWan();
        red.setNombre(nombre);
        red.setDireccionRed(direccion);
        red.setMascara(mascara);
        red.setGateway(gateway);
        red.setDns("8.8.8.8");
        red.setEstado("Activa");
        return redWanRepository.save(red);
    }

    private Cliente cliente(String nombres, String apellidos, String dni, String telefono,
            String direccion, String correo, String estado, String ip, Planes plan, RedWan red) {
        Cliente cliente = new Cliente();
        cliente.setNombres(nombres);
        cliente.setApellidos(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setCorreo(correo);
        cliente.setEstado(estado);
        cliente.setIp(ip);
        cliente.setPlan(plan);
        cliente.setRedWan(red);
        return clienteRepository.save(cliente);
    }

    private void instalacion(Cliente cliente, String tipo, String direccion, String estado,
            LocalDateTime fechaRegistro, Boolean cobrar, Double monto, String metodoPago) {
        Instalacion instalacion = new Instalacion();
        instalacion.setPreCliente(cliente);
        instalacion.setTipo(tipo);
        instalacion.setDireccion(direccion);
        instalacion.setEstado(estado);
        instalacion.setFechaRegistro(fechaRegistro);
        if ("Atendido".equals(estado)) {
            instalacion.setFechaAtencion(fechaRegistro.plusDays(1));
        }
        instalacion.setCobrarInstalacion(cobrar);
        instalacion.setMontoInstalacion(monto);
        instalacion.setMetodoPago(metodoPago);
        instalacionRepository.save(instalacion);
    }

    private Averia averia(String codigo, String descripcion, String direccion, String estado,
            Cliente cliente, LocalDateTime fechaRegistro, LocalDateTime fechaAtencion) {
        Averia averia = new Averia();
        averia.setCodigo(codigo);
        averia.setDescripcion(descripcion);
        averia.setDireccion(direccion);
        averia.setEstado(estado);
        averia.setCliente(cliente);
        averia.setFechaRegistro(fechaRegistro.toLocalDate());
        if (fechaAtencion != null) {
            averia.setFechaAtencion(fechaAtencion.toLocalDate());
        }
        return averiaRepository.save(averia);
    }

    private void pago(Cliente cliente, double monto, LocalDate fechaEmision, String estado,
            String metodoPago) {
        Pagos pago = new Pagos();
        pago.setCliente(cliente);
        pago.setMonto(monto);
        pago.setFechaEmision(fechaEmision);
        pago.setFechaVencimiento(fechaEmision.withDayOfMonth(1).plusMonths(1));
        pago.setMes(fechaEmision.getMonthValue());
        pago.setAnio(fechaEmision.getYear());
        pago.setEstado(estado);
        pago.setMetodoPago(metodoPago);
        if ("Pagado".equals(estado)) {
            pago.setFechaPago(fechaEmision.plusDays(3));
        }
        pagosRepository.save(pago);
    }

    private void stock(String equipo, String marca, String modelo, int cantidad, String estado,
            LocalDate ultimoIngreso) {
        Stock item = new Stock();
        item.setEquipo(equipo);
        item.setMarca(marca);
        item.setModelo(modelo);
        item.setCantidad(cantidad);
        item.setEstado(estado);
        item.setUltimoIngreso(ultimoIngreso);
        stockRepository.save(item);
    }
}
