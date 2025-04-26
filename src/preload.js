const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  closeWindow: () => ipcRenderer.send('close-window'),
  hideWindow: () => ipcRenderer.send('hide-window'),
});