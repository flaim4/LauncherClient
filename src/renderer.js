const closeBtn = document.getElementById('closeBtn');
const hideBtn = document.getElementById('hideBtn');

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