const API_URL = 'http://localhost:8080/api/admin';

const ESTADO_LABEL = {
  BORRADOR: 'Borrador', PENDIENTE: 'Pendiente', PENDIENTE_DESACTIVACION: 'Pend. desactivación',
  APROBADO: 'Aprobado', RECHAZADO: 'Rechazado', INACTIVO: 'Inactivo', ACTIVO: 'Activo'
};
const MODALIDAD_LABEL = { ESCOLARIZADA: 'Escolarizada', MIXTA: 'Mixta', EN_LINEA: 'En línea' };

// Devuelve { ok, status, data }. 400 => data = { campo: mensaje }, otros => data = { mensaje }
async function request(path, options = {}) {
  try {
    const res = await fetch(API_URL + path, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
      body: options.body ? JSON.stringify(options.body) : undefined
    });
    let data = null;
    try { data = await res.json(); } catch (_) {}
    return { ok: res.ok, status: res.status, data };
  } catch (e) {
    return { ok: false, status: 0, data: { mensaje: 'No se pudo conectar con el servidor' } };
  }
}

const Api = {
  divisiones: {
    listar: () => request('/divisiones'),
    obtener: id => request(`/divisiones/${id}`),
    crear: body => request('/divisiones', { method: 'POST', body }),
    editar: (id, body) => request(`/divisiones/${id}`, { method: 'PUT', body }),
    enviarAprobacion: id => request(`/divisiones/${id}/enviar-aprobacion`, { method: 'PATCH' }),
    solicitarDesactivacion: id => request(`/divisiones/${id}/solicitar-desactivacion`, { method: 'PATCH' }),
    rechazar: (id, motivo) => request(`/divisiones/${id}/rechazar`, { method: 'PATCH', body: { motivo } })
  },
  carreras: {
    listar: () => request('/carreras'),
    obtener: id => request(`/carreras/${id}`),
    crear: body => request('/carreras', { method: 'POST', body }),
    editar: (id, body) => request(`/carreras/${id}`, { method: 'PUT', body }),
    enviarAprobacion: id => request(`/carreras/${id}/enviar-aprobacion`, { method: 'PATCH' }),
    solicitarDesactivacion: id => request(`/carreras/${id}/solicitar-desactivacion`, { method: 'PATCH' }),
    rechazar: (id, motivo) => request(`/carreras/${id}/rechazar`, { method: 'PATCH', body: { motivo } })
  },
  // Rol Secretario: bandeja de solicitudes, aprobar y rechazar. tipo = 'DIVISION' | 'CARRERA'
  secretario: {
    listarSolicitudes: () => request('/secretario/solicitudes'),
    aprobar: (tipo, id) => request(`/secretario/${rutaTipo(tipo)}/${id}/aprobar`, { method: 'PATCH' }),
    rechazar: (tipo, id, motivoRechazo) => request(`/secretario/${rutaTipo(tipo)}/${id}/rechazar`, { method: 'PATCH', body: { motivoRechazo } })
  },
  oferta: {
    listar: (nombre, divisionId) => {
      const p = new URLSearchParams();
      if (nombre) p.set('nombre', nombre);
      if (divisionId) p.set('divisionId', divisionId);
      return request('/oferta' + (p.toString() ? '?' + p : ''));
    }
  }
};

function rutaTipo(tipo) {
  return String(tipo).toUpperCase() === 'DIVISION' ? 'divisiones' : 'carreras';
}

function toast(texto, tipo = 'ok') {
  let box = document.getElementById('toast');
  if (!box) { box = document.createElement('div'); box.id = 'toast'; document.body.appendChild(box); }
  const el = document.createElement('div');
  el.className = tipo; el.textContent = texto; box.appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

function badge(valor) {
  return `<span class="badge b-${valor}">${ESTADO_LABEL[valor] || valor}</span>`;
}

function esc(s) {
  return String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function confirmar(texto) {
  return new Promise(resolve => {
    const bg = document.createElement('div');
    bg.className = 'modal-bg';
    bg.innerHTML = `<div class="modal"><p>${esc(texto)}</p><div class="form-actions">
      <button class="btn secondary" data-r="0">Cancelar</button><button class="btn danger" data-r="1">Confirmar</button></div></div>`;
    bg.addEventListener('click', e => {
      const r = e.target.dataset && e.target.dataset.r;
      if (r !== undefined) { bg.remove(); resolve(r === '1'); }
    });
    document.body.appendChild(bg);
  });
}

// HU12: pide el motivo (obligatorio) y rechaza la solicitud. tipo = 'DIVISION' | 'CARRERA'
// Devuelve true si se rechazó, false si se canceló o hubo error.
function rechazarSolicitud(tipo, id, nombre) {
  return new Promise(resolve => {
    const bg = document.createElement('div');
    bg.className = 'modal-bg';
    bg.innerHTML = `<form class="modal" novalidate>
      <p>Rechazar la solicitud de <b>${esc(nombre)}</b></p>
      <div class="field"><label for="motivo">Motivo del rechazo</label>
        <textarea id="motivo" name="motivo" maxlength="500"></textarea>
        <div class="error" data-for="motivo"></div></div>
      <div class="form-actions">
        <button type="button" class="btn secondary" data-cancelar>Cancelar</button>
        <button type="submit" class="btn danger">Rechazar</button></div></form>`;
    const form = bg.querySelector('form');
    const cerrar = ok => { bg.remove(); resolve(ok); };

    form.querySelector('[data-cancelar]').addEventListener('click', () => cerrar(false));
    form.addEventListener('submit', async e => {
      e.preventDefault(); limpiarErrores(form);
      const motivo = form.motivo.value.trim();
      if (!motivo) return pintarErrores(form, { motivo: 'El motivo de rechazo es obligatorio' });

      const api = tipo === 'DIVISION' ? Api.divisiones : Api.carreras;
      const r = await api.rechazar(id, motivo);
      if (r.ok) { toast(r.data.mensaje); cerrar(true); }
      else if (r.status === 400 && r.data) pintarErrores(form, r.data);
      else { toast(r.data?.mensaje || 'Ocurrió un error', 'err'); cerrar(false); }
    });

    document.body.appendChild(bg);
    form.motivo.focus();
  });
}

// Muestra errores 400 junto a cada input (inputs con name="campo" y <div class="error" data-for="campo">)
function limpiarErrores(form) {
  form.querySelectorAll('.error').forEach(e => e.textContent = '');
  form.querySelectorAll('.invalid').forEach(e => e.classList.remove('invalid'));
}
function pintarErrores(form, errores) {
  Object.entries(errores).forEach(([campo, msg]) => {
    const nombre = campo === 'division' ? 'divisionId' : campo;
    const div = form.querySelector(`.error[data-for="${nombre}"]`);
    if (div) div.textContent = msg;
    const input = form.elements[nombre];
    if (input) input.classList.add('invalid');
  });
}
// Maneja una respuesta fallida de forma uniforme
function manejarError(form, r) {
  if (r.status === 400 && r.data) pintarErrores(form, r.data);
  else toast((r.data && r.data.mensaje) || 'Ocurrió un error', 'err');
}

function idDeUrl() {
  const id = new URLSearchParams(location.search).get('id');
  return id ? Number(id) : null;
}

function renderHeader(activo) {
  document.body.insertAdjacentHTML('afterbegin', `
    <header class="top"><h1>Oferta Académica</h1><nav>
      <a href="index.html">Oferta</a>
      <a href="division-form.html">+ División</a>
      <a href="carrera-form.html">+ Carrera</a>
      <a href="panel-secretario.html">Panel Secretario</a>
    </nav></header>`);
}