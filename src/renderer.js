const closeBtn = document.getElementById('closeBtn');
const hideBtn = document.getElementById('hideBtn');

let page = "main"

if (closeBtn) {
  closeBtn.addEventListener('click', (e) => {
    e.preventDefault();
    if (window.electronAPI) {
      window.electronAPI.closeWindow();
    } else {
      console.error('Electron API не доступен!');
    }
  });
}

if (hideBtn) {
  hideBtn.addEventListener('click', (e) => {
    e.preventDefault();
    if (window.electronAPI) {
      window.electronAPI.hideWindow();
    } else {
      console.error('Electron API не доступен!');
    }
  });
}

document.addEventListener('DOMContentLoaded', function () {
  openPage("main")
});

function openPage(name) {
  page = name
  if (page == "main") {
    let main = document.querySelector(".main");
    main.style.display = 'block';
    let login = document.querySelector(".login");
    login.style.display = 'none';
  }
  if (page == "login") {
    let login = document.querySelector(".login");
    login.style.display = 'block';
    let main = document.querySelector(".main");
    main.style.display = 'none';
  }
}

document.querySelector(".panel").addEventListener("click", (event) => {
  const button = event.target.closest(".button");
  if (!button) return;

  document.querySelectorAll(".button").forEach(btn => {
    btn.classList.remove("active");
    btn.classList.add("no-active");
  });

  button.classList.remove("no-active");
  button.classList.add("active");
});

document.querySelectorAll('.card').forEach(card => {
  const info = card.querySelector('.info');
  const btn = card.querySelector('.playbut');
  
  card.addEventListener('mouseenter', () => {
    info.style.display = 'none';
    btn.style.display = 'block';
  });
  
  card.addEventListener('mouseleave', () => {
    info.style.display = 'block';
    btn.style.display = 'none';
  });
});
