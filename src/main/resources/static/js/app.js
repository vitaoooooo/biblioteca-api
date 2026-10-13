const $ = (selector) => document.querySelector(selector);
const escapeHtml = (value) => String(value ?? "").replace(/[&<>'"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;" })[c]);
let session = {}, editingBookId = null, editingClientId = null;
const books = new Map(), clients = new Map();

function notice(text, type = "sucesso") {
  const element = $("#mensagem-global"); element.textContent = text; element.className = `mensagem ${type}`; element.hidden = false;
  setTimeout(() => { element.hidden = true; }, 5000);
}
async function errorMessage(response, fallback) {
  try { const error = await response.json(); return Object.values(error.fieldErrors || {}).join(" ") || error.message || error.detail || fallback; } catch (_) { return fallback; }
}
function isAdmin() { return session?.user?.role === "ADMIN"; }
function headers(extra = {}) { return {...extra }; }
function basicToken(email, password) {
  const bytes = new TextEncoder().encode(`${email}:${password}`);
  let binary = "";
  bytes.forEach((byte) => { binary += String.fromCharCode(byte); });
  return btoa(binary);
}
function loginError(message) {
  const error = $("#login-erro");
  error.textContent = message;
  error.hidden = false;
}
function registrationError(message) {
  const error = $("#cadastro-erro");
  error.textContent = message;
  error.hidden = false;
}
function showAuthForm(form) {
  const registering = form === "register";
  $("#form-login").hidden = registering;
  $("#form-cadastro").hidden = !registering;
  $("#painel-login-texto").hidden = registering;
  $("#painel-cadastro-texto").hidden = !registering;
  $("#login-erro").hidden = true;
  $("#cadastro-erro").hidden = true;
  if (registering) $("#cadastro-nome").focus(); else $("#login-email").focus();
}
function setLoginLoading(loading) {
  const button = $("#botao-entrar");
  button.disabled = loading;
  button.textContent = loading ? "Entrando..." : "Entrar";
  button.setAttribute("aria-busy", String(loading));
}
async function api(url, options = {}) {
  let response;
  try {
    response = await fetch(url, { ...options, headers: headers({ Accept: "application/json", ...options.headers }) });
  } catch (_) {
    throw new Error("Não foi possível conectar ao servidor. Inicie a aplicação e acesse http://localhost:8080.");
  }
  if (response.status === 401) { logout("Sua sessão expirou ou as credenciais são inválidas."); throw new Error("Não autenticado"); }
  return response;
}
function logout(message = "") {
  sessionStorage.removeItem("library.session"); session = null; $("#aplicacao").hidden = true; $("#login-panel").hidden = false; $("#form-login").reset();
  if (message) { $("#login-erro").textContent = message; $("#login-erro").hidden = false; }
}
async function startSession() {
  session = {};

  const response = await api("/api/account/me");

  if (!response.ok) {
    throw new Error(
      await errorMessage(response, "Não foi possível abrir a sessão.")
    );
  }

  session.user = await response.json();
  sessionStorage.setItem("library.session", JSON.stringify(session));

  $("#usuario-logado").textContent =
    `${session.user.name} (${session.user.role})`;

  $("#menu-usuarios").hidden = !isAdmin();
  $("#login-erro").hidden = true;
  $("#login-panel").hidden = true;
  $("#aplicacao").hidden = false;

  await loadBooks();
}

$("#form-login").addEventListener("submit", async (event) => {
  event.preventDefault();

  const email = $("#login-email").value.trim();
  const password = $("#login-password").value;

  $("#login-erro").hidden = true;
  setLoginLoading(true);

  try {
    const resp = await fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "same-origin",
      body: JSON.stringify({ email, password })
    });

    if (!resp.ok) {
      throw new Error(
        resp.status === 401 || resp.status === 403
          ? "E-mail ou senha inválidos."
          : `Erro na autenticação: ${resp.status}`
      );
    }

    await startSession();

  } catch (error) {
    logout(error.message);
    loginError(error.message || "Não foi possível entrar.");
  } finally {
    setLoginLoading(false);
  }
});

$("#mostrar-cadastro").addEventListener("click", () => showAuthForm("register"));
$("#mostrar-login").addEventListener("click", () => showAuthForm("login"));
$("#form-cadastro").addEventListener("submit", async (event) => {
  event.preventDefault();
  const name = $("#cadastro-nome").value.trim();
  const email = $("#cadastro-email").value.trim();
  const password = $("#cadastro-password").value;
  const button = $("#botao-cadastrar");
  $("#cadastro-erro").hidden = true;
  button.disabled = true;
  button.textContent = "Criando conta...";
  try {
    const response = await fetch("/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify({ name, email, password })
    });
    if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível criar a conta."));
    await startSession(basicToken(email, password));
  } catch (error) {
    if (error instanceof TypeError) registrationError("Não foi possível conectar ao servidor. Inicie a aplicação e acesse http://localhost:8080.");
    else registrationError(error.message || "Não foi possível criar a conta.");
  } finally {
    button.disabled = false;
    button.textContent = "Criar conta";
  }
});
$("#botao-sair").addEventListener("click", () => logout());

document.querySelectorAll("[data-section]").forEach((button) => button.addEventListener("click", async () => {
  const target = button.dataset.section;
  document.querySelectorAll("main section").forEach((section) => { section.hidden = section.id !== target; });
  if (target === "clientes") await loadClients();
  if (target === "emprestimos") await loadLoans();
  if (target === "usuarios" && isAdmin()) await loadUsers();
}));

async function loadBooks() {
  try {
    const response = await api("/api/books"); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível carregar os livros."));
    const items = await response.json(); books.clear(); $("#lista-livros").innerHTML = items.map((book) => {
      books.set(book.id, book); const actions = isAdmin() ? `<button type="button" onclick="editBook(${book.id})">Editar</button><button type="button" onclick="disableBook(${book.id})">Inativar</button>` : "-";
      return `<tr><td>${book.id}</td><td>${escapeHtml(book.name)}</td><td>${escapeHtml(book.author)}</td><td>R$ ${Number(book.price).toFixed(2)}</td><td>${book.quantity} / ${book.totalQuantity}</td><td>${actions}</td></tr>`;
    }).join("");
  } catch (error) { if (session) notice(error.message, "erro"); }
}
$("#form-livro").addEventListener("submit", async (event) => {
  event.preventDefault();
  const payload = { isbn: $("#livro-isbn").value.replaceAll("-", "") || null, name: $("#livro-nome").value.trim(), author: $("#livro-autor").value.trim(), price: $("#livro-preco").value, quantity: Number($("#livro-quantidade").value) };
  const editing = editingBookId !== null;
  try { const response = await api(editing ? `/api/books/${editingBookId}` : "/api/books", { method: editing ? "PUT" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível salvar o livro.")); resetBook(); await loadBooks(); notice(editing ? "Livro atualizado." : "Livro cadastrado."); } catch (error) { if (session) notice(error.message, "erro"); }
});
window.editBook = (id) => { const book = books.get(id); if (!book || !isAdmin()) return; editingBookId = id; $("#livro-isbn").value = book.isbn || ""; $("#livro-nome").value = book.name; $("#livro-autor").value = book.author; $("#livro-preco").value = book.price; $("#livro-quantidade").value = book.totalQuantity; $("#form-livro button[type='submit']").textContent = "Salvar alterações"; $("#cancelar-edicao-livro").hidden = false; };
window.disableBook = async (id) => { if (!isAdmin() || !confirm("Deseja inativar este livro?")) return; try { const response = await api(`/api/books/${id}`, { method: "DELETE" }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível inativar o livro.")); await loadBooks(); notice("Livro inativado."); } catch (error) { notice(error.message, "erro"); } };
function resetBook() { editingBookId = null; $("#form-livro").reset(); $("#form-livro button[type='submit']").textContent = "Cadastrar livro"; $("#cancelar-edicao-livro").hidden = true; }
$("#cancelar-edicao-livro").addEventListener("click", resetBook);

async function loadClients() {
  try { const response = await api("/api/clients"); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível carregar os clientes.")); const items = await response.json(); clients.clear(); $("#lista-clientes").innerHTML = items.map((client) => { clients.set(client.id, client); const actions = isAdmin() ? `<button type="button" onclick="editClient(${client.id})">Editar</button><button type="button" onclick="disableClient(${client.id})">Inativar</button>` : "-"; return `<tr><td>${client.id}</td><td>${escapeHtml(client.name)}</td><td>${escapeHtml(client.email)}</td><td>${escapeHtml(client.phoneNumber)}</td><td>${actions}</td></tr>`; }).join(""); } catch (error) { if (session) notice(error.message, "erro"); }
}
$("#form-cliente").addEventListener("submit", async (event) => { event.preventDefault(); const payload = { name: $("#cliente-nome").value.trim(), email: $("#cliente-email").value.trim(), phoneNumber: $("#cliente-telefone").value.trim() }; const editing = editingClientId !== null; try { const response = await api(editing ? `/api/clients/${editingClientId}` : "/api/clients", { method: editing ? "PUT" : "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível salvar o cliente.")); resetClient(); await loadClients(); notice(editing ? "Cliente atualizado." : "Cliente cadastrado."); } catch (error) { if (session) notice(error.message, "erro"); } });
window.editClient = (id) => { const client = clients.get(id); if (!client || !isAdmin()) return; editingClientId = id; $("#cliente-nome").value = client.name; $("#cliente-email").value = client.email; $("#cliente-telefone").value = client.phoneNumber; $("#form-cliente button[type='submit']").textContent = "Salvar alterações"; $("#cancelar-edicao-cliente").hidden = false; };
window.disableClient = async (id) => { if (!isAdmin() || !confirm("Deseja inativar este cliente?")) return; try { const response = await api(`/api/clients/${id}`, { method: "DELETE" }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível inativar o cliente.")); await loadClients(); notice("Cliente inativado."); } catch (error) { notice(error.message, "erro"); } };
function resetClient() { editingClientId = null; $("#form-cliente").reset(); $("#form-cliente button[type='submit']").textContent = "Cadastrar cliente"; $("#cancelar-edicao-cliente").hidden = true; }
$("#cancelar-edicao-cliente").addEventListener("click", resetClient);

async function loadLoans() {
  try { const response = await api("/api/loans"); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível carregar os empréstimos.")); const loans = await response.json(); $("#lista-emprestimos").innerHTML = loans.map((loan) => `<tr><td>${escapeHtml(loan.client.name)}</td><td>${escapeHtml(loan.book.name)}</td><td>${loan.loanDate}</td><td>${loan.dueDate}</td><td>${loan.late ? "Em atraso" : "Em aberto"}</td><td><button type="button" onclick="returnLoan(${loan.id})">Devolver</button></td></tr>`).join(""); } catch (error) { if (session) notice(error.message, "erro"); }
}
$("#form-emprestimo").addEventListener("submit", async (event) => { event.preventDefault(); try { const response = await api("/api/loans", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ clientId: Number($("#emprestimo-cliente-id").value), bookId: Number($("#emprestimo-livro-id").value) }) }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível registrar o empréstimo.")); $("#form-emprestimo").reset(); await Promise.all([loadLoans(), loadBooks()]); notice("Empréstimo registrado."); } catch (error) { if (session) notice(error.message, "erro"); } });
window.returnLoan = async (id) => { try { const response = await api(`/api/loans/${id}/return`, { method: "PATCH" }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível registrar a devolução.")); await Promise.all([loadLoans(), loadBooks()]); notice("Devolução registrada."); } catch (error) { if (session) notice(error.message, "erro"); } };
$("#botao-emprestimos-devolvidos").addEventListener("click", async () => { try { const response = await api("/api/loans/returned"); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível carregar o histórico.")); const loans = await response.json(); $("#lista-emprestimos-devolvidos").innerHTML = loans.map((loan) => `<tr><td>${escapeHtml(loan.clientName)}</td><td>${escapeHtml(loan.bookName)}</td><td>${escapeHtml(loan.bookAuthor)}</td><td>${loan.loanDate}</td><td>${loan.returnedDate}</td></tr>`).join(""); $("#historico-emprestimos-devolvidos").hidden = false; } catch (error) { if (session) notice(error.message, "erro"); } });

async function loadUsers() {
  if (!isAdmin()) return;
  try { const response = await api("/api/users"); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível carregar os usuários.")); const users = await response.json(); $("#lista-usuarios").innerHTML = users.map((user) => `<tr><td>${escapeHtml(user.name)}</td><td>${escapeHtml(user.email)}</td><td>${user.role}</td><td>${user.active ? "Ativo" : "Inativo"}</td><td>${user.active ? `<button type="button" onclick="disableUser(${user.id})">Inativar</button>` : "-"}</td></tr>`).join(""); } catch (error) { notice(error.message, "erro"); }
}
$("#form-usuario").addEventListener("submit", async (event) => { event.preventDefault(); try { const payload = { name: $("#usuario-nome").value.trim(), email: $("#usuario-email").value.trim(), password: $("#usuario-senha").value, role: $("#usuario-papel").value }; const response = await api("/api/users", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível cadastrar o usuário.")); $("#form-usuario").reset(); await loadUsers(); notice("Usuário cadastrado."); } catch (error) { if (session) notice(error.message, "erro"); } });
window.disableUser = async (id) => { if (!confirm("Deseja inativar este usuário?")) return; try { const response = await api(`/api/users/${id}`, { method: "DELETE" }); if (!response.ok) throw new Error(await errorMessage(response, "Não foi possível inativar o usuário.")); await loadUsers(); notice("Usuário inativado."); } catch (error) { notice(error.message, "erro"); } };

(async () => { const saved = sessionStorage.getItem("library.session"); if (saved) try { await startSession(JSON.parse(saved).token); } catch (_) { logout(); } })();
