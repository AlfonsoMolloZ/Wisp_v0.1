package com.wisp.app.servicios;

import java.util.List;

import com.wisp.app.entity.RedWan;

public interface RedWanService {

    //listado de todas las redes WAN
    List<RedWan> listarTodas() ;

    //buscar red wan por id
    RedWan buscarPorId(Long id);

    //guardar red wan
    RedWan guardar(RedWan red);

    //eliminar red wan por id
    void eliminar(Long id);


}
