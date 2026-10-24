// ===== Cliente de la API + utilidades compartidas =====
// Si sirves los HTML desde Spring (http://localhost:8080) puedes usar '/api/admin'.
// Si los abres con otro servidor (Live Server, etc.) deja la URL completa (CORS ya está habilitado).
const API_URL = 'http://localhost:8080/api/admin';

const ESTADO_LABEL = {
  BORRADOR: 'Borrador', PENDIENTE: 'Pendiente', PENDIENTE_DESACTIVACION: 'Pend. desactivación',
  APROBADO: 'Aprobado', RECHAZADO: 'Rechazado', INACTIVO: 'Inactivo', ACTIVO: 'Activo'
};

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
      solicitarDesactivacion: id => request(`/divisiones/${id}/solicitar-desactivacion`, { method: 'PATCH' })
  }
};

// ---------- UI helpers ----------
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
    </nav></header>`);
}
