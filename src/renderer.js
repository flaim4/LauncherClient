const closeBtn = document.getElementById('closeBtn');

if (closeBtn) {
  closeBtn.addEventListener('click', (e) => {
    e.preventDefault();
    if (window.electronAPI) {
      window.electronAPI.closeWindow(); // Вызываем метод из preload.js
    } else {
      console.error('Electron API не доступен!');
    }
  });
}