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

document.addEventListener('DOMContentLoaded', function() {
  openPage("main")
});

function openPage(name) {
  page = name
  if (page == "main") {
    let main = document.querySelector(".main");
    main.style.display = 'block';
    let login = document.querySelector(".start-launcher");
    login.style.display = 'none';
  }
  if (page == "login") {
    let login = document.querySelector(".start-launcher");
    login.style.display = 'block';
    let main = document.querySelector(".main");
    main.style.display = 'none';
  }
} 