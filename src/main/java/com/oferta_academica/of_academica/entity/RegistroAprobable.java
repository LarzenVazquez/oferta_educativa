package com.oferta_academica.of_academica.entity;

import com.oferta_academica.of_academica.exception.ReglaNegocioException;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

// Datos y flujo de aprobación comunes a División y Carrera
@MappedSuperclass
public abstract class RegistroAprobable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estatus estatus = Estatus.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAprobacion estadoAprobacion = EstadoAprobacion.BORRADOR;

    @Column(nullable = false)
    private boolean publicado = false;

    @Column(length = 500)
    private String motivoRechazo;

    protected RegistroAprobable() {
    }

    protected RegistroAprobable(String nombre, String descripcion, Estatus estatus) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estatus = estatus;
    }

    // Texto para los mensajes, por ejemplo "La división"
    protected abstract String etiqueta();

    // Mientras el Secretario revisa, no se puede modificar
    public boolean tieneSolicitudPendiente() {
        return estadoAprobacion == EstadoAprobacion.PENDIENTE
                || estadoAprobacion == EstadoAprobacion.PENDIENTE_DESACTIVACION;
    }

    public boolean estaInactivo() {
        return estadoAprobacion == EstadoAprobacion.INACTIVO;
    }

    // Página web (HU11, HU12): se muestra si está publicado y aprobado. Mientras se revisa una
    // desactivación sigue visible, y si esa desactivación se rechaza también sigue visible.
    public boolean esVisibleEnWeb() {
        return publicado && (estadoAprobacion == EstadoAprobacion.APROBADO
                || estadoAprobacion == EstadoAprobacion.PENDIENTE_DESACTIVACION
                || estadoAprobacion == EstadoAprobacion.RECHAZADO);
    }

    // HU02 / HU05: al editar, el registro vuelve a BORRADOR
    protected void actualizarDatosBase(String nombre, String descripcion, Estatus estatus) {
        if (tieneSolicitudPendiente()) {
            throw new ReglaNegocioException(etiqueta() + " tiene una solicitud en revisión");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estatus = estatus;
        this.estadoAprobacion = EstadoAprobacion.BORRADOR;
        this.motivoRechazo = null;
    }

    // HU08
    public void enviarAAprobacion() {
        if (tieneSolicitudPendiente()) {
            throw new ReglaNegocioException(etiqueta() + " ya tiene una solicitud en revisión");
        }
        if (estadoAprobacion == EstadoAprobacion.APROBADO) {
            throw new ReglaNegocioException(etiqueta() + " ya está aprobada");
        }
        if (estaInactivo()) {
            throw new ReglaNegocioException(etiqueta() + " está inactiva y no se puede enviar a aprobación");
        }
        this.estadoAprobacion = EstadoAprobacion.PENDIENTE;
        this.motivoRechazo = null;
    }

    // HU03 / HU06
    public void solicitarDesactivacion() {
        if (estaInactivo()) {
            throw new ReglaNegocioException(etiqueta() + " ya está inactiva");
        }
        if (tieneSolicitudPendiente()) {
            throw new ReglaNegocioException(etiqueta() + " tiene una solicitud en revisión");
        }
        this.estadoAprobacion = EstadoAprobacion.PENDIENTE_DESACTIVACION;
    }

    // HU11: publicación -> APROBADO (aparece en la web); desactivación -> INACTIVO (deja de mostrarse)
    public void aprobar() {
        switch (estadoAprobacion) {
            case PENDIENTE -> {
                this.estadoAprobacion = EstadoAprobacion.APROBADO;
                this.publicado = true;
                this.motivoRechazo = null;
            }
            case PENDIENTE_DESACTIVACION -> {
                this.estadoAprobacion = EstadoAprobacion.INACTIVO;
                this.estatus = Estatus.INACTIVO;
                this.publicado = false;
                this.motivoRechazo = null;
            }
            default -> throw new ReglaNegocioException(etiqueta() + " no tiene una solicitud pendiente de revisión");
        }
    }

    // HU12: queda RECHAZADO con su motivo. Si era de publicación no aparece en la web;
    // si era de desactivación conserva "publicado" (sigue visible).
    public void rechazar(String motivo) {
        if (!tieneSolicitudPendiente()) {
            throw new ReglaNegocioException(etiqueta() + " no tiene una solicitud pendiente");
        }
        if (estadoAprobacion == EstadoAprobacion.PENDIENTE) {
            this.publicado = false;
        }
        this.estadoAprobacion = EstadoAprobacion.RECHAZADO;
        this.motivoRechazo = motivo;
    }

    // HU13: despublicar un registro ya publicado; regresa a BORRADOR y deja de verse en la web
    public void despublicar() {
        this.estadoAprobacion = EstadoAprobacion.BORRADOR;
        this.publicado = false;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public EstadoAprobacion getEstadoAprobacion() {
        return estadoAprobacion;
    }

    public boolean isPublicado() {
        return publicado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}
