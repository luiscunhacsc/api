import React from 'react';
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import './App.css';
import ServicoTable from './components/ServicoTable';

function App() {
  return (
    <div className="min-h-screen flex flex-column bg-blue-50">
      <header className="surface-0 shadow-2 p-3 flex align-items-center justify-content-between">
        <span className="text-2xl font-bold text-primary">Hotel Management</span>
        <span className="text-sm">Powered by PrimeReact</span>
      </header>
      <main className="flex-1 p-4 flex justify-content-center">
        <div style={{width: '100%', maxWidth: 900}}>
          <ServicoTable />
        </div>
      </main>
      <footer className="surface-100 text-center py-3 text-sm text-600">
        &copy; {new Date().getFullYear()} Hotel Management UI
      </footer>
    </div>
  );
}

export default App;
