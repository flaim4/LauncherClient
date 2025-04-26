const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('node:path');

let mainWindow;

if (require('electron-squirrel-startup')) {
  app.quit();
}

const createWindow = () => {
  mainWindow = new BrowserWindow({
    width: 1100,
    height: 664,
    titleBarStyle: 'hidden',
    frame: false,
    resizable: false,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    },
  });

  mainWindow.loadFile(path.join(__dirname, 'index.html'));
  mainWindow.webContents.openDevTools();
};

app.whenReady().then(() => {
  createWindow();
  
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

ipcMain.on('close-window', () => {
  const win = BrowserWindow.getFocusedWindow();
  if (win) win.close();
});

ipcMain.on('hide-window', () => {
  const win = BrowserWindow.getFocusedWindow();
  if (win) win.minimize();
});
